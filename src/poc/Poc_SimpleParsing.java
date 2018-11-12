package poc;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import framework.utilities.httpClientWrap;

public class Poc_SimpleParsing {
	public static void main(String []args) {

		try
		{
			httpClientWrap httpclient=new httpClientWrap();
			HashMap<Object, Object> response =	httpclient.sendGetRequestWithParams("https://www.lenskart.com/juno/services/v1/redis?keys=GET_BOGO_DETAILS");
			String result =response.get("response").toString();

			System.out.println(result);
			JSONParser jsonparser= new JSONParser();
			JSONObject jsonobject=(JSONObject) jsonparser.parse(result);
			String firstresult=jsonobject.get("result").toString();
			//			System.out.println(firstresult);
			jsonobject=(JSONObject) jsonparser.parse(firstresult);
			String BogoDetails=jsonobject.get("GET_BOGO_DETAILS").toString();
//			System.out.println(BogoDetails);
			jsonobject=(JSONObject) jsonparser.parse(BogoDetails);
			String vincentchase=jsonobject.get("Vincent Chase").toString();
			JSONArray jsonarray=(JSONArray) jsonparser.parse(vincentchase);
			for(int i=0; i<jsonarray.size();i++){
				JSONObject jsonobjectarray=(JSONObject) jsonarray.get(i);
				System.out.println(jsonobjectarray.get("img"));
				
				System.out.println(jsonobjectarray);
				
				
			}
			
			
			
			
			

		}catch (Exception e) {
			e.printStackTrace();
		}


	}

}
