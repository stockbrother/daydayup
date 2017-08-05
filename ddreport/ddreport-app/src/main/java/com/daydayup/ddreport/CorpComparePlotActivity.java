package com.daydayup.ddreport;

/**
 * Created by wuzhen on 8/1/2017.
 */

import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private XYPlot plot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_xy_plot_example);


        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        // create a couple arrays of y-values t plot:
        final String[] domainLabels = {"2016", "2015", "2014", "2013", "2012"};
        Number[] series1Numbers = {10000000D, 400000D, 200000D, 800000D, 400000D, };
        Number[] series2Numbers = {5000000D, 2000D, 10000D, 5000D, 20000D, };
        List<Number[]> data = getData();
        LOG.info("rows:"+data.size());
        for(Number[] row:data){
            String s = "";
            for(Number n:row){
                s+=n+",";
            }
            LOG.info("row:"+s);
        }

        series1Numbers = data.get(0);
        series2Numbers = data.get(1);

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format =
                new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels);

        LineAndPointFormatter series2Format =
                new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels_2);

        // add an "dash" effect to the series2 line:
        series2Format.getLinePaint().setPathEffect(new DashPathEffect(new float[]{

                // always use DP when specifying pixel sizes, to keep things consistent across devices:
                PixelUtils.dpToPix(20),
                PixelUtils.dpToPix(15)}, 0));

        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(5, CatmullRomInterpolator.Type.Centripetal));

        series2Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(5, CatmullRomInterpolator.Type.Centripetal));

        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);
        plot.addSeries(series2, series2Format);

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
    private List<Number[]> getData() {
        String scope = "and corpId in('000001','601166')";
        DdrContext dc = AndroidDdrContext.getInstance();
        CommandContext scc = new CommandContext(dc);

        List<String> indexAliasL = new ArrayList<>();
        List<DatedIndex> indexNameL = new ArrayList<>();
        indexAliasL.add("Y2016");
        indexAliasL.add("Y2015");
        indexAliasL.add("Y2014");
        indexAliasL.add("Y2013");
        indexAliasL.add("Y2012");
        indexNameL.add(DatedIndex.valueOf("A_资产总计@date0","2016/12/31"));
        indexNameL.add(DatedIndex.valueOf("A_资产总计@date0","2015/12/31"));
        indexNameL.add(DatedIndex.valueOf("A_资产总计@date0","2014/12/31"));
        indexNameL.add(DatedIndex.valueOf("A_资产总计@date0","2013/12/31"));
        indexNameL.add(DatedIndex.valueOf("A_资产总计@date0","2012/12/31"));

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


        return scc.getDataBaseService().execute(new JdbcAccessTemplate.JdbcOperation<List<Number[]>>() {

            @Override
            public List<Number[]> execute(Connection con, JdbcAccessTemplate t) {
                return t.executeQuery(con, sql.toString(), sqlArgL, new ResultSetProcessor<List<Number[]>>() {

                    @Override
                    public List<Number[]> process(ResultSet rs) throws SQLException {
                        List<Number[]> rt = new ArrayList<>();
                        while(rs.next()){
                            Number[] row= new Number[5];
                            for(int i=0;i<row.length;i++){
                                row[i] = rs.getDouble(3+i);
                                if(row[i]==null){
                                    row[i]=0D;
                                }
                            }


                            rt.add(row);
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