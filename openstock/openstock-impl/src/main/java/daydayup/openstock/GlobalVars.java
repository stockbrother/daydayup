package daydayup.openstock;

public class GlobalVars {
	private static GlobalVars ME = new GlobalVars();

	static {

	}

	private CorpNameService corpNameService = new CorpNameService();

	GlobalVars() {

	}

	public static GlobalVars getInstance() {
		return ME;
	}

	public CorpNameService getCorpNameService() {
		return this.corpNameService;
	}
}
