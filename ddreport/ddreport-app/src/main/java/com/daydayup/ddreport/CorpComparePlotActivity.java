package com.daydayup.ddreport;

/**
 * Created by wuzhen on 8/1/2017.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.androidplot.ui.*;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.*;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.CommandContext;
import daydayup.openstock.DdrContext;
import daydayup.openstock.cup.IndexSqlSelectFieldsResolveContext;
import daydayup.openstock.database.Tables;
import daydayup.openstock.sheetcommand.DatedIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.*;

/**
 * A simple XYPlot
 */
public class CorpComparePlotActivity extends AppCompatActivity {
    private static final Logger LOG = LoggerFactory.getLogger(CorpComparePlotActivity.class);

    private static final Integer[] lineColors = new Integer[]{Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.GRAY, Color.CYAN};

    private XYPlot plot;

    private static class SeriesData {
        private String corpId;
        private String corpName;
        private Number[] numbers;

        private SeriesData() {

        }

        public static SeriesData readRow(ResultSet rs) throws SQLException {
            SeriesData rt = new SeriesData();

            rt.corpId = rs.getString(1);
            rt.corpName = rs.getString(2);
            rt.numbers = new Number[5];
            for (int i = 0; i < rt.numbers.length; i++) {
                rt.numbers[i] = rs.getDouble(3 + i);
                if (rt.numbers[i] == null) {
                    rt.numbers[i] = 0D;
                }
            }
            return rt;

        }
    }

    private static class MyFormatter extends LineAndPointFormatter {
        private SeriesData row;

        public MyFormatter(SeriesData corpId, Integer color) {
            super(color, color, 0, null);
            this.row = corpId;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_xy_plot_example);


        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        // create a couple arrays of y-values t plot:
        final String[] domainLabels = {"2016", "2015", "2014", "2013", "2012"};

        List<SeriesData> data = getData();


        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        for (int i = 0; i < data.size(); i++) {
            SeriesData row = data.get(i);

            XYSeries series1 = new SimpleXYSeries(
                    Arrays.asList(row.numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "" + row.corpId + "/" + row.corpName);

            Integer color = lineColors[i % lineColors.length];
            MyFormatter series1Format =
                    new MyFormatter(row, color);

            // just for fun, add some smoothing to the lines:
            // see: http://androidplot.com/smooth-curves-and-androidplot/
            series1Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(5, CatmullRomInterpolator.Type.Centripetal));


            // add a new series' to the xyplot:
            plot.addSeries(series1, series1Format);
        }
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        plot.getLegend().setTableModel(new DynamicTableModel(1, data.size()));
        plot.getLegend().setLegendItemComparator(new Comparator<XYLegendItem>() {
            @Override
            public int compare(XYLegendItem i1, XYLegendItem i2) {
                LOG.info("i1.item:" + i1.item + ",i2.item:" + i2.item);
                SeriesData s1 = ((MyFormatter) i1.item).row;
                SeriesData s2 = ((MyFormatter) i2.item).row;

                return (int) (s2.numbers[s2.numbers.length - 1].doubleValue() - s1.numbers[s1.numbers.length - 1].doubleValue());
            }
        });

        //plot.getLegend().setWidth(PixelUtils.dpToPix(100), SizeMode.FILL);

        // reposition the grid so that it rests above the bottom-left
        // edge of the graph widget:
        plot.getLegend().setSize(new Size(
                PixelUtils.dpToPix(20 * data.size()),
                SizeMode.ABSOLUTE, 0.5f, SizeMode.RELATIVE));
        plot.getLegend().position(
                50,
                HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                150,
                VerticalPositioning.ABSOLUTE_FROM_TOP,
                Anchor.RIGHT_TOP);

        //plot.setRangeBoundaries(0, BoundaryMode.FIXED, 500, BoundaryMode.FIXED);
        //bottom line labels
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
    }

    private List<SeriesData> getData() {
        //String scope = "and corpId in('000001','601166')";
        String scope = "and corpId in (select corpId from " + Tables.TN_GROUP_ITEM + " where 1=1)";

        DdrContext dc = AndroidDdrContext.getInstance();
        CommandContext scc = new CommandContext(dc);

        List<String> indexAliasL = new ArrayList<>();
        List<DatedIndex> indexNameL = new ArrayList<>();
        indexAliasL.add("Y2016");
        indexAliasL.add("Y2015");
        indexAliasL.add("Y2014");
        indexAliasL.add("Y2013");
        indexAliasL.add("Y2012");
        indexNameL.add(DatedIndex.valueOf("A_资产总计@date0", "2016/12/31"));
        indexNameL.add(DatedIndex.valueOf("A_资产总计@date0", "2015/12/31"));
        indexNameL.add(DatedIndex.valueOf("A_资产总计@date0", "2014/12/31"));
        indexNameL.add(DatedIndex.valueOf("A_资产总计@date0", "2013/12/31"));
        indexNameL.add(DatedIndex.valueOf("A_资产总计@date0", "2012/12/31"));

        final StringBuffer sql = new StringBuffer();
        sql.append("select corpId as CORP,corpName as NAME");

        Set<Integer> typeSet = new HashSet<>();
        String corpInfoTableAlias = "ci";
        final List<Object> sqlArgL = new ArrayList<>();

        for (int i = 0; i < indexNameL.size(); i++) {
            DatedIndex indexName = indexNameL.get(i);
            IndexSqlSelectFieldsResolveContext src = new IndexSqlSelectFieldsResolveContext(scc, indexName, sql,
                    sqlArgL);

            src.corpInfoTableAlias = corpInfoTableAlias;
            sql.append(",");
            src.resolveSqlSelectFields();

            sql.append(" as " + indexAliasL.get(i));
            src.getReportTypeSet(typeSet, true);
        }
        // from
        int ts = 0;
        sql.append(" from " + Tables.TN_CORP_INFO + " as " + corpInfoTableAlias);
        /**
         * <code>
         for (Integer type : typeSet) {
         if (ts > 0) {
         sql.append(",");
         }
         sql.append(Tables.getReportTable(type) + " as r" + type);
         ts++;
         </code> }
         */

        // where join on.
        ts = 0;
        sql.append(" where 1=1");

        if (scope != null) {
            sql.append(" ");
            sql.append(scope);
        }

        sql.append(" order by corpId");


        return scc.getDataBaseService().execute(new JdbcAccessTemplate.JdbcOperation<List<SeriesData>>() {

            @Override
            public List<SeriesData> execute(Connection con, JdbcAccessTemplate t) {
                return t.executeQuery(con, sql.toString(), sqlArgL, new ResultSetProcessor<List<SeriesData>>() {

                    @Override
                    public List<SeriesData> process(ResultSet rs) throws SQLException {
                        List<SeriesData> rt = new ArrayList<>();

                        while (rs.next()) {

                            SeriesData t3 = SeriesData.readRow(rs);
                            rt.add(t3);
                        }

                        return rt;
                    }
                });
            }
        }, false);

    }

    public void showView() {


    }
}