package daydayup.openstock.netease;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import daydayup.openstock.EnvUtil;

public class NeteaseUtil {

	public static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

	public static File getDataDownloadDir() {
		File root1 = new File(EnvUtil.getDataDir().getAbsolutePath() + File.separator + "163" + File.separator + "raw"
				+ File.separator + "2016_year");
		return root1;
	}

	public static File getDataWashedDir() {
		File root2 = new File(EnvUtil.getDataDir().getAbsolutePath() + File.separator + "163" + File.separator
				+ "washed" + File.separator + "2016_year");
		return root2;
	}
}
