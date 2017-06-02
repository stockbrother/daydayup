package daydayup.openstock.netease;

import java.io.File;

import daydayup.openstock.EnvUtil;

public class NeteaseUtil {

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
