package daydayup.openstock.netease;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.uno.XComponentContext;

public abstract class FileProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(FileProcessor.class);

	public void process(File file, WashedFileLoadContext xContext) {
		LOG.info("processor:" + this.getClass().getName() + " going to process file:" + file.getAbsolutePath());

		InputStream is;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		Charset cs = Charset.forName("UTF-8");
		Reader reader = new InputStreamReader(is, cs);
		this.process(reader, xContext);//
	}

	protected abstract void process(Reader reader, WashedFileLoadContext xContext);

}
