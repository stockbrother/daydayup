package daydayup.openstock.sina;

import java.io.File;

public abstract class FilesPreprocessor {

	protected File sourceDir;
	protected File targetDir;

	public FilesPreprocessor(File sourceDir, File targetDir) {
		this.sourceDir = sourceDir;
		this.targetDir = targetDir;
	}
	public abstract void process() ;
}
