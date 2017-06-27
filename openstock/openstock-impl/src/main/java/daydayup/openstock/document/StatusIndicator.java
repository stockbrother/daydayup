package daydayup.openstock.document;

public interface StatusIndicator {

	public void start(String string, int i);

	public void end();

	public void setText(String string);

	public void setValue(int i);

}
