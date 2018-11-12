package poc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class GetApi {
	
	
	private void sendGet(String geturl) throws Exception {
		String USER_AGENT = "Mozilla/5.0";
		String XApiClient="desktop";

		String url = geturl;
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		
		con.setRequestMethod("GET");

	
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("X-Api-Client",XApiClient);

		int responseCode = con.getResponseCode();
		System.out.println(	con.getResponseMessage());
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		
	String result=	response.toString();
	JSONObject obj1 = new JSONObject(result);
	System.out.println(obj1.toString());
	JSONObject result1= (JSONObject) obj1.get("result");
	JSONArray array1= (JSONArray) result1.get("frameDetails");
	JSONObject obj2=new JSONObject(array1.get(1).toString());
	String value = obj2.get("value").toString();
	System.out.println("value is >>>>"+ value);
	boolean price=result.contains("Price");
	System.out.println(price);
	System.out.println(result);
	result.replaceAll("{", "\\s");
System.out.println(result);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		GetApi gt = new GetApi();
		  gt.sendGet("https://www.lenskart.com/juno/services/v1/product/120472");

	}

}
