package a;

import java.util.HashMap;
import java.util.Map;

public class MyVar {
	private Map<String, Object> vars = new HashMap<String, Object>();

	public Object get(String key) {
		return this.vars.get(key);//
	}

	public Boolean getBoolean(String key) {
		return (Boolean) this.vars.get(key);
	}

	public void setBoolean(String key, boolean value) {
		this.vars.put(key, Boolean.valueOf(value));//
	}
}
