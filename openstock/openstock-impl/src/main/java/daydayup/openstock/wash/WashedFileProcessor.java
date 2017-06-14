package daydayup.openstock.wash;

import java.io.Reader;

import daydayup.openstock.wash.WashedFileLoader.WashedFileLoadContext;

public interface WashedFileProcessor {

	public void process(Reader reader, WashedFileLoadContext xContext);
}