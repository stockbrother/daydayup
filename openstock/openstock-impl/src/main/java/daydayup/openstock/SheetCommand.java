package daydayup.openstock;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.uno.XComponentContext;

import daydayup.openstock.cninfo.CninfoCorpInfo2DbSheetCommand;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.netease.NeteaseUtil;
import daydayup.openstock.sheetcommand.IndexTableSheetCommand;
import daydayup.openstock.sheetcommand.ScopedIndexTableSheetCommand;
import daydayup.openstock.sheetcommand.SqlQuerySheetCommand;
import daydayup.openstock.sheetcommand.SqlUpdateSheetCommand;
import daydayup.openstock.sina.SinaQuotesDownloadAndWashSheetCommand;
import daydayup.openstock.sina.SinaQuotesWashed2DBSheetCommand;
import daydayup.openstock.sse.SseCorpInfo2DbSheetCommand;
import daydayup.openstock.szse.SzseCorpInfo2DbSheetCommand;
import daydayup.openstock.util.DocUtil;
import daydayup.openstock.wash.WashedFileLoader;
import daydayup.openstock.wash.WashedFileLoader.WashedFileLoadContext;

public class SheetCommand extends CommandBase<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(SheetCommand.class);

	public static final String SN_SYS_CMDS = "SYS_CMDS";

	public static final String SN_SYS_SQL_QUERY = "SYS_SQL_QUERY";

	public static final String SN_SYS_SQL_UPDATE = "SYS_SQL_UPDATE";

	public static final String SN_SYS_INDEX_DEFINE = "SYS_INDEX_DEFINE";

	public static final String SN_SYS_SCOPED_INDEX_TABLE = "SYS_SCOPED_INDEX_TABLE";

	public static final String SN_SYS_INDEX_TABLE = "SYS_INDEX_TABLE";

	public static final String SN_SYS_CFG = "SYS_CFG";

	@Override
	public Object doExecute(CommandContext cc) {
		XComponentContext xcc = cc.getComponentContext();
		DataBaseService dbs = cc.getDataBaseService();
		XSpreadsheet xSheet = DocUtil.getSpreadsheetByName(xcc, SN_SYS_CMDS, false);

		if (xSheet == null) {
			// "no sheet with name CMDS";
			return "no sheet with name " + SN_SYS_CMDS;
		}
		String invokeId = null;
		boolean body = false;
		String command = null;
		List<String> argL = new ArrayList<>();
		for (int i = 0; i < 1024 * 1024; i++) {
			String value0I = DocUtil.getText(xSheet, 0, i);

			if ("Invoke".equals(value0I)) {
				invokeId = DocUtil.getText(xSheet, 1, i);
				continue;
			}
			if (invokeId == null) {
				continue;
			}
			if ("ID".equals(value0I)) {
				body = true;
				continue;
			}

			if (!body) {
				continue;
			}
			if (value0I == null || value0I.trim().length() == 0) {
				break;
			}

			if (invokeId.equals(value0I)) {
				// found the command to invoke.
				command = DocUtil.getText(xSheet, 1, i);
				for (int j = 2;; j++) {
					String argJ = DocUtil.getText(xSheet, j, i);
					if (argJ == null || argJ.trim().length() == 0) {
						break;
					}
					argL.add(argJ);
				}

				break;
			}

		}

		if (invokeId == null) {
			LOG.warn("no invokeId found.");
			return "no invokeId found.";
		}

		if (command == null) {
			LOG.warn("no command found for invokeId:{}", invokeId);
			return "no command found for invokeId";
		}
		SheetCommandContext scc = new SheetCommandContext(cc, argL);

		if (command.equals(SN_SYS_SQL_QUERY)) {
			return new SqlQuerySheetCommand().execute(scc);
		} else if (command.equals(SN_SYS_SQL_UPDATE)) {
			return new SqlUpdateSheetCommand().execute(scc);
		} else if (command.equals(SN_SYS_SCOPED_INDEX_TABLE)) {
			return new ScopedIndexTableSheetCommand().execute(scc);
		} else if (command.equals(SN_SYS_INDEX_TABLE)) {
			return new IndexTableSheetCommand().execute(scc);
		} else if (command.equals("NETEASE_WASHED_2_DB")) {
			return this.executeNeteaseWashed2Db(cc, argL);
		} else if (command.equals("RESET_SHEET")) {
			return this.executeResetSheet(cc);
		} else if (command.equals("CNINFO_CORPINFO_2_DB")) {
			return new CninfoCorpInfo2DbSheetCommand().execute(scc);
		} else if (command.equals("SINA_DOWNLOAD_AND_WASH")) {
			return new SinaQuotesDownloadAndWashSheetCommand().execute(scc);
		} else if (command.equals("SINA_WASHED_2_DB")) {
			return new SinaQuotesWashed2DBSheetCommand().execute(scc);
		} else if (command.equals("SSE_CORPINFO_2_DB")) {
			return new SseCorpInfo2DbSheetCommand().execute(scc);
		}else if (command.equals("SZSE_CORPINFO_2_DB")) {
			return new SzseCorpInfo2DbSheetCommand().execute(scc);
		}else {
			return "not supporte:" + command;
		}
	}

	private Object executeResetSheet(CommandContext cc) {
		XSpreadsheetDocument xDoc = DocUtil.getSpreadsheetDocument(cc.getComponentContext());

		String[] names = xDoc.getSheets().getElementNames();
		for (String name : names) {
			if (name.startsWith("SYS_")) {
				continue;
			}
			try {
				xDoc.getSheets().removeByName(name);
			} catch (NoSuchElementException e) {
				throw RtException.toRtException(e);
			} catch (WrappedTargetException e) {
				throw RtException.toRtException(e);
			} //
		}
		return "done";
	}

	private Object executeNeteaseWashed2Db(CommandContext cc, List<String> argL) {
		DataBaseService dbs = cc.getDataBaseService();
		WashedFileLoadContext flc = new WashedFileLoadContext(dbs);
		new WashedFileLoader().load(NeteaseUtil.getDataWashedDir(), flc);
		return "done";
	}

}
