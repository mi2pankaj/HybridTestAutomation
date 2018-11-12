package poc;

import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin.Response;

import framework.utilities.httpClientWrap;

public class Produc_Api {
	public static String  getproductqty(String objectName,String data) {
		   String jsonval[]=objectName.split("\\.");
		   String qty="";
		try {
			Integer pid	=Integer.valueOf(data);
			HashMap< String, String> headers=new HashMap<>();
			headers.put("x-api-client", "desktop");
			String url= "https://www.lenskart.com/juno/services/v1/product/"+pid;
			httpClientWrap httpclient=new httpClientWrap();
			String responsebody=httpClientWrap.sendGetRequest(url, headers);
			JSONParser jsonparser=new JSONParser();
			JSONObject jsonobject=(JSONObject) jsonparser.parse(responsebody);
			String result= jsonobject.get("result").toString();
			JSONObject result1=(JSONObject) jsonparser .parse(result);
	        qty= result1.get(jsonval[0]).toString();	
	         
			
		}catch(Exception e) {
			System.out.println("exception occured "+e.getMessage());
			System.out.println("Pass");
		}
      return qty;

	}
	public static void categorytitle(String data) {
		try {
			String categoryValue="";
			String url="https://www.lenskart.com/juno/services/v1/category/"+data+"?page-size=90&page=0";
			HashMap<String, String>header=new HashMap<>();
			header.put("x-api-client", "desktop");
			String responsbody=httpClientWrap.sendGetRequest(url, header);
			JSONParser parser =new JSONParser();
			JSONObject jsonobject=(JSONObject) parser.parse(responsbody);
			JSONObject result1=(JSONObject) jsonobject.get("result");

			if(result1.containsKey("h1")) {
				categoryValue=result1.get("h1").toString();
				System.out.println(categoryValue);

			}else {
				System.out.println("getting category Name");
				categoryValue=result1.get("category_name").toString();
				System.out.println(categoryValue);
			}

		}catch(Exception e) {

		}	
	}
	public  static String  notequals(String data) {
		String result="";
		try {
		String [] comp=data.split(",");
		Integer actual=Integer.valueOf(comp[0]);
		Integer expected=Integer.valueOf(comp[1]);
		if(actual>expected) {
			result="pass";
		}
		}catch(Exception e) {}
		return result;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String aty=getproductqty("breadcrumb","122231");
		System.out.println("value of supplied key is "+aty);
		//		categorytitle("2459");
     
//		m.out.println(notequals("12,90"));
	}


}
