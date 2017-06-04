package daydayup.openstock.netease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvRowMap {

	public Map<String, CsvRow> map = new HashMap<String, CsvRow>();
	public List<String> keyList = new ArrayList<>();

	public CsvRowMap() {

	}

	public void put(String key, CsvRow value) {
		CsvRow old = this.map.put(key, value);
		if (old == null) {
			keyList.add(key);//
		}
	}

	public CsvRow get(String key, boolean force) {
		CsvRow rt = map.get(key);
		if (rt == null && force) {
			throw new RuntimeException("no value for key:" + key);
		}
		return rt;

	}

}
