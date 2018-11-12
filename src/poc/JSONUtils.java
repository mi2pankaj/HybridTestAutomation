package poc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import framework.utilities.FileLib;


public class JSONUtils {
	public static Map<String, Object> map = new HashMap<String, Object>();

	public static void main(String[] args) throws JSONException{
		String x=FileLib.ReadContentOfFile(System.getProperty("user.dir").concat("/src/poc/newAPI")).toString();
		JSONObject jsonObject=new JSONObject(x);
		Map<String, Object> map=jsonToMap(jsonObject);
		for (Map.Entry<String,Object> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			//System.out.println("Key = " + entry.getKey() +", Value = " + entry.getValue());
			
		}
	}

	public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
		Map<String, Object> retMap = new HashMap<String, Object>();

		if(json != null) {
			retMap = toMap(json);
		}
		return retMap;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(JSONObject object) throws JSONException {

		//System.out.println("map instance"+map);

		Iterator<String> keysItr = object.keys();
		while(keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if(value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if(value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}

			map.put(key, value);

		}
		return map;
	}

	public static List<Object> toList(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for(int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if(value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if(value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}
}
