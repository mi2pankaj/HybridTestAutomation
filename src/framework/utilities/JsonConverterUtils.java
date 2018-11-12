package framework.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonConverterUtils {
	private static final String[] DATE_FORMATS = {"mm/dd/yyyy hh:mm a", "MMM dd, yyyy", "dd/MM/yyyy HH:mm",
            "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "dd/MM/yyyy HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSSSSSXXX", "yyyy-MM-dd HH:mm:ss.0", "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss"};

    public static String asString(Object object) {
        return object.toString();
    }

    public static Integer asInteger(Object object) {
        if (object instanceof Integer) {
            return (Integer) object;
        } else if (object instanceof String) {
            return Integer.parseInt(object.toString());
        } else {
            throw new IllegalArgumentException("Unable to convert: " + object.toString() + " to type Integer, as it is of type: " + object.getClass().getSimpleName());
        }
    }

    public static Boolean asBoolean(Object object) {
        if (object instanceof Boolean) {
            return (Boolean) object;
        } else if (object instanceof String) {
            return "TRUE".equals(object.toString().toUpperCase());
        } else {
            throw new IllegalArgumentException("Unable to convert: " + object.toString() + " to type Boolean, as it is of type: " + object.getClass().getSimpleName());
        }
    }

    public static Double asDouble(Object object) {
        if (object instanceof Double) {
            return (Double) object;
        } else if (object instanceof String) {
            return Double.parseDouble(object.toString());
        } else {
            throw new IllegalArgumentException("Unable to convert: " + object.toString() + " to type Double, as it is of type: " + object.getClass().getSimpleName());
        }
    }

    public static Date asDate(Object object) {
        if (object instanceof Date) {
            return (Date) object;
        } else if (object instanceof String) {
            String dateString = object.toString();
            for (String dateFormat : DATE_FORMATS) {
                try {
                    return new SimpleDateFormat(dateFormat).parse(dateString);
                } catch (ParseException ignored) {
                }
            }
        }
        throw new IllegalArgumentException("Non-parseable date format in date string: " + object.toString());
    }

    @SuppressWarnings("unchecked")
    public static List asList(Object object) throws JSONException {
        if (object instanceof List) {
            return (List) object;
        } else if (object instanceof Set) {
            return new ArrayList((Set) object);
        } else if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            List<Object> list = new ArrayList<Object>();
            for (int i = 0; i < jsonArray.length(); i++) {
                Object value = jsonArray.get(i);
                if (value instanceof JSONArray) {
                    value = asList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    value = asMap((JSONObject) value);
                }
                list.add(value);
            }
            return list;
        } else {
            throw new JSONException("Unable to convert: " + object.toString() + " to type List, as it is of type: " + object.getClass().getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map asMap(Object object) throws JSONException {
        if (object instanceof Map) {
            return (Map) object;
        } else if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            Map<String, Object> map = new HashMap<String, Object>();

            Iterator<String> keysItr = jsonObject.keys();
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                Object value = jsonObject.get(key);

                if (value instanceof JSONArray) {
                    value = asList(value);
                } else if (value instanceof JSONObject) {
                    value = asMap(value);
                }
                map.put(key, value);
            }
            return map;
        } else {
            throw new JSONException("Unable to convert: " + object.toString() + " to type Map, as it is of type: " + object.getClass().getSimpleName());
        }
    }
}
