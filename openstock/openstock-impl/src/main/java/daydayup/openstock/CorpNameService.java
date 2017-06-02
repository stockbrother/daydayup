package daydayup.openstock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CorpNameService {
	Map<String, String> nameMap = new HashMap<>();

	public void addCorpName(String code, String name) {
		nameMap.put(code, name);
	}

	public String getName(String code) {
		return this.nameMap.get(code);
	}

	public String[] getSortedCorpCodeArray() {
		String[] rt = this.nameMap.keySet().toArray(new String[] {});
		Arrays.sort(rt);
		return rt;
	}

}
