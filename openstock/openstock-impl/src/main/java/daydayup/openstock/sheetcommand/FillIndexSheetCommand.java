package daydayup.openstock.sheetcommand;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.IndexSqlQuery;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.document.Spreadsheet;

public class FillIndexSheetCommand extends BaseSheetCommand<Object> {

	@Override
	protected Object doExecute(SheetCommandContext scc) {

		Spreadsheet sheet = scc.getSheet();
		String corpId = null;
		int headerRow = -1;
		List<String> indexL = new ArrayList<>();
		List<String> dateL = new ArrayList<>();

		IndexSqlQuery isq = new IndexSqlQuery();
		int corpIdRow = -1;
		for (int i = 0; i < 100; i++) {

			String key = sheet.getText(0, i);

			if (key == null || key.trim().length() == 0) {
				break;
			}

			if (headerRow == -1) {

				if (key.equals("CorpId")) {
					corpId = sheet.getText(1, i);
					corpIdRow = i;
				}

				if (key.equals("Index/Date")) {
					headerRow = i;
					for (int col = 1; col < 100; col++) {
						String date = sheet.getText(col, i);
						if (date == null || date.trim().length() == 0) {
							break;
						}
						dateL.add(date);
					}
				}
			} else {// row is now must be index name
				String name = sheet.getText(0, i);
				indexL.add(name);
			}

		}

		isq.scope("and corpId='" + corpId + "'");

		for (int i = 0; i < indexL.size(); i++) {
			String name = indexL.get(i);
			for (int j = 0; j < dateL.size(); j++) {
				String date = dateL.get(j);
				isq.addIndex(DatedIndex.valueOf(name, date), "C" + i + "_" + j);
			}
		}
		int corpIdRowF = corpIdRow;
		int headerRowF = headerRow;
		return isq.execute(scc, new ResultSetProcessor<Object>() {

			@Override
			public Object process(ResultSet rs) throws SQLException {

				return FillIndexSheetCommand.this.process(isq.getDataOffset(), rs, indexL, dateL, headerRowF, sheet,
						scc, corpIdRowF);
			}
		});

	}

	private Object process(int resultSetOffset, ResultSet rs, List<String> indexL, List<String> dateL, int headerRow,
			Spreadsheet sheet, SheetCommandContext scc, int corpIdRow) throws SQLException {
		if (rs.next()) {
			Object corpName = rs.getObject("corpName");
			sheet.setValue(2, corpIdRow, corpName);

			for (int i = 0; i < indexL.size(); i++) {
				int row = i + headerRow + 1;
				for (int j = 0; j < dateL.size(); j++) {
					int col = j + 1;
					int colIdx = i * dateL.size() + (j + 1) + resultSetOffset;
					Object value = rs.getObject(colIdx);
					sheet.setValue(col, row, value);
				}
			}

		}
		return "done";
	}

}
