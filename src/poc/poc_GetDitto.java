package poc;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.jcraft.jsch.Logger;

public class poc_GetDitto {
	/*creating  session */
	public static String getSessionToken (){

		String sessionToken = "";
		try{
			HttpClient httpclient= HttpClientBuilder.create().build();
			HttpEntity responseEntity=null;
			String responseResult=null;

			String requestUrl ="https://api.lenskart.com/v2/sessions/";
			HttpPost httpPost =new HttpPost(requestUrl);
			HttpResponse response = httpclient.execute(httpPost);

			responseEntity=response.getEntity();
			responseResult=EntityUtils.toString(responseEntity,"UTF-8");

			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(responseResult);

			sessionToken = ((JSONObject)obj.get("result")).get("id").toString();

			//System.out.println("session token is = "+ sessionToken);

		}catch(Exception e){

			System.out.println("error occurred while getting session token, "+e);
		}

		return sessionToken;
	}
	/*   using above session creating User login session token */
	public static String get_userSessionToken(String Email){
		String token="";
		try{
			HttpClient httpclient= HttpClientBuilder.create().build();
			HttpEntity responseEntity =null;
			String responseresult=null;
			String url="https://api.lenskart.com/v2/customers/authenticate";
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("x-session-token",getSessionToken ());
			httppost.addHeader("x-api-client", "desktop");
			httppost.addHeader("Content-Type", "application/json");
			JSONObject jsonobject=new JSONObject();
			jsonobject.put("password", "valyoo123");
			jsonobject.put("username", Email);
			httppost.setEntity(new StringEntity(jsonobject.toString()));
			HttpResponse respons=httpclient.execute(httppost);
			//			System.out.println(respons.getStatusLine());
			//		System.out.println(respons);
			responseEntity= respons.getEntity();
			responseresult=EntityUtils.toString(responseEntity,"UTF-8");
			//			System.out.println("respone:"+responseresult);
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(responseresult);
			JSONObject result=(JSONObject) obj.get("result");
			token=result.get("token").toString();
			//			System.out.println(token);

		}catch(Exception e){
			e.printStackTrace();
			return token;
		}
		return token;	
	}
	/* getting ditto from above user account*/
	public static int get_Ditto(String User){
		int numberOfDittos = 0;
		try{
			String url="https://api.lenskart.com/v2/utility/ditto/customer/";
			HttpClient httpclient=HttpClientBuilder.create().build();
			HttpEntity responseEntity;
			String responseResult;
			HttpGet httpget=new HttpGet(url);
			httpget.addHeader("x-session-token", get_userSessionToken( User));
			httpget.addHeader("x-api-client", "desktop");
			HttpResponse response=httpclient.execute(httpget);
			int statusCode=response.getStatusLine().getStatusCode();
			//			System.out.println(statusCode);
			responseEntity=response.getEntity();
			responseResult=EntityUtils.toString(responseEntity);
			JSONParser parser=new JSONParser();
			JSONObject jsonobject=(JSONObject) parser.parse(responseResult);
			jsonobject=(JSONObject) jsonobject.get("result");
			String dittos= jsonobject.get("dittoId").toString();
			//			System.out.println(dittos);
			JSONArray dittoArrays=(JSONArray) parser.parse(dittos);
			numberOfDittos=dittoArrays.size();
			return numberOfDittos;

		}catch(Exception e){
			e.printStackTrace();
			System.out.println(" exception occured while getting ditto from user account"+e);
		}
		return numberOfDittos;
	}
	/* creating ditto using a hard coded  ditto id for user lenskart.test52@gmail.com   */
	public static int create_Ditto(String UserEmail){
		int statusCode=0;
		try{
			HttpClient httpclient=HttpClientBuilder.create().build();
			HttpEntity responseEntity;
			String url="https://www.lenskart.com/juno/services/v1/ditto";
			HttpPost httppost=new HttpPost(url);
			httppost.addHeader("sessiontoken", get_userSessionToken(UserEmail));
			httppost.addHeader("Content-Type", "application/json");
			JSONObject jsonobject=new JSONObject();
			jsonobject.put("ditto_id", "0994517264c26c7fa0762ae20e3d9256167c299b");
			httppost.setEntity(new StringEntity(jsonobject.toString()));
			HttpResponse response= httpclient.execute(httppost);
			statusCode =response.getStatusLine().getStatusCode();
			//	   System.out.println("status code"+statusCode);
			responseEntity =response.getEntity();
			String responseResult=EntityUtils.toString(responseEntity);
			//	  System.out.println(responseResult);
			return statusCode;
		}catch(Exception e){
			System.out.println("Exception occured while creating ditto"+e);

		}
		return statusCode;
	}


	public static void main(String args[]){

		int ditto=get_Ditto("lenskart.test52@gmail.com");
		System.out.println(ditto);
	}
}
