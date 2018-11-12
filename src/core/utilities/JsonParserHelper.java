package core.utilities;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JsonParserHelper {

	 private static Object get(Object json, String propertyPath) throws JSONException {
	        String[] properties = propertyPath.split("\\.");

	        for (String property : properties) {
	            String propertyStripped = property;
	            boolean listIndex = false;
	            String[] condition = null;
	            boolean isListProperty = property.contains("[") && property.endsWith("]");
	            if (isListProperty) {
	            	listIndex=true;
	                int indexOfOpeningBrace = property.indexOf('[');
	                propertyStripped = property.substring(0, indexOfOpeningBrace);
	                 condition=property.substring(indexOfOpeningBrace + 1, property.length() - 1).split(",");
	            }

	            json = invoke(json, propertyStripped);
	           if (listIndex) {
	        	   if (condition.length==1){
	        		   json = invoke(json,0) ;
	        	   }
	        	   else{
	        		   json = invoke(json, condition[0],condition[1]);
	        	   }
	        	  
	            }
	        }
	        
	        if (json instanceof Double)
	        {
	        	if ((double)json == Math.floor((double) json) && !Double.isInfinite((double) json))
	        	return ((Double) json).intValue();
	        }
	        return json;
	    }

	    private static Object invoke(Object json, String property) throws JSONException {
	        JSONObject jsonObject;
	        if (property.isEmpty()) {
	            return json;
	        } else if (json instanceof JSONObject) {
	            jsonObject = ((JSONObject) json); 
	        } else {
	            try {
	                jsonObject = new JSONObject(json.toString());
	            } catch (JSONException e) {
                throw new JSONException("Could not convert: " + json.toString() + " to JSON");
	            }
	        }

	        try {
	            return jsonObject.get(property);
	        } catch (JSONException e) {
	            throw new JSONException("No property with name: " + property + " found");
	        }
	    }

	    private static Object invoke(Object json, int listIndex) throws JSONException {
	        JSONArray jsonArray;
	        if (json instanceof JSONArray) {
	            jsonArray = (JSONArray) json;
	        } else {
	            try {
	                jsonArray = new JSONArray(json.toString());
	            } catch (JSONException e) {
	                throw new JSONException("Could not convert: " + json.toString() + " to JSON Array");
	            }
	        }

	        try {
	            return jsonArray.get(listIndex);
	        } catch (JSONException e) {
	            throw new JSONException("Error while finding element at index: " + listIndex);
	        }
	    }
	    
	    private static Object invoke(Object json,String condition, String value) throws JSONException{
	    	JSONArray jsonArray;
	    	if (json instanceof JSONArray) {
	            jsonArray = (JSONArray) json;
	        }
	    	else {
	            try {
	                jsonArray = new JSONArray(json.toString());
	            } catch (JSONException e) {
	                throw new JSONException("Could not convert: " + json.toString() + " to JSON Array");
	            }
	        }
	    	try {
	    		
	    		for (int i = 0; i < jsonArray.length(); i++){
	    			 Object object = jsonArray.get(i);
		             Object actualValue = get(object, condition);
	    			if (actualValue.toString().equals(value)){
	    				return jsonArray.get(i);
	    			}
	    			
	    		}
	            
        } catch (JSONException e) {
	            throw new JSONException("Error while finding element with value: " + value);
	        }
	    	
	    	
	    	
	    	return value;
	    	
	    }

	    public static String getAsString(Object json, String propertyPath) throws JSONException {
	        return JsonConverterUtils.asString(get(json, propertyPath));
	    }

	    public static Integer getAsInteger(Object json, String propertyPath) throws JSONException {
	        return JsonConverterUtils.asInteger(get(json, propertyPath));
	    }

	    public static Boolean getAsBoolean(Object json, String propertyPath) throws JSONException {
	        return JsonConverterUtils.asBoolean(get(json, propertyPath));
	    }

	    public static Double getAsDouble(Object json, String propertyPath) throws JSONException {
	        return JsonConverterUtils.asDouble(get(json, propertyPath));
	    }

	    public static Date getAsDate(Object json, String propertyPath) throws JSONException {
	        return JsonConverterUtils.asDate(get(json, propertyPath));
	    }

}




