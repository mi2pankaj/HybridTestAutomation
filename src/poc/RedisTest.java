package poc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import framework.utilities.httpClientWrap;

public class RedisTest {

	static {
		System.out.println("static ");
	}
	public static void main(String[] args) {

		List<String> a = new ArrayList<>();
		a.add("a");
		a.add("abb");
		a.add("a");
		
		a.remove("a");
		
		System.out.println(a.toString());
		
		String x ="SeeksforGeeks";

		String x1 = x.substring(0, x.indexOf("for"));
		String x2 = x.substring(x.indexOf("for")+3, x.length());
		System.out.println(x1 + " -- " +x2);

		char [] c1 = x1.toCharArray();
		char [] c2 = x2.toCharArray();

		String sx1 = String.valueOf(c1);
		String sx2 = String.valueOf(c2);

		System.out.println(sx2+"for"+sx1);

		test1();
	}

	public static void test1()
	{
		System.out.println("test1");

		new RedisTest().test();
	}	

	public void test()
	{


		String brand="";
		String category="";
		try {
			httpClientWrap httpclient=new httpClientWrap();
			HashMap<Object, Object> redisKey=httpclient.sendGetRequestWithParams("https://www.lenskart.com/juno/services/v1/redis?keys=PDP_CONFIG");
			String valueOfRediskey=(String)redisKey.get("response");
			JSONParser jparse=new JSONParser();
			JSONObject result=(JSONObject)jparse.parse(valueOfRediskey);
			//System.out.println(result);
			JSONObject valueOfPdpConfig=(JSONObject) result.get("result");
			//System.out.println(valueOfPdpConfig);
			String keyOnPdp=(String) valueOfPdpConfig.get("PDP_CONFIG");
			JSONObject valueOfPdpConfigjson=(JSONObject)jparse.parse(keyOnPdp);
			//System.out.println(valueOfPdpConfigjson);

			JSONObject showTAS = (JSONObject)jparse.parse(valueOfPdpConfigjson.toString());

			JSONObject showTASObj=(JSONObject) showTAS.get("showTAS");

			Set<String> keys = showTASObj.keySet();

			for(String k : keys) {
				JSONObject jsonCatList=(JSONObject)showTASObj.get(k);
				if(String.valueOf(jsonCatList).contains("ON")){
					System.out.println(jsonCatList);
					System.out.println("category ==> "+k);

					Set<String> brandset=jsonCatList.keySet();
					for(String s:brandset) {

						Object brandname=jsonCatList.get(s);
						if(String.valueOf(brandname).equalsIgnoreCase("ON")) {
							System.out.println(s);
							category=k;
							brand=s;
							System.out.println("Category is :"+ category + " Brand is : "+ brand);
							break;
						}

					}

					break;
				}
			}
		} catch (ParseException e) {

			e.printStackTrace();
		}
	}
}