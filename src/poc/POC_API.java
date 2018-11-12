package poc;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import framework.utilities.FileLib;

public class POC_API {

	public static void main(String[] args) throws JSONException, ClientProtocolException, IOException{
		
		String x=FileLib.ReadContentOfFile(System.getProperty("user.dir").concat("/src/poc/api.json")).toString();
		String responseResult=null;
		JSONObject jsonObject=new JSONObject(x);
		CloseableHttpResponse response = null;
		HttpEntity responseEntity=null;
		CloseableHttpClient httpclient =HttpClients.createDefault();
		String method=jsonObject.get("method").toString();
		String requestUrl= jsonObject.get("request").toString();
		//String inputType=jsonObject.get("inputType").toString();
		String parameters= jsonObject.get("input").toString();
		String headers =jsonObject.get("headers").toString();
		
		if(method.equalsIgnoreCase("post")){
			HttpPost request=new HttpPost(requestUrl);
			if(!headers.equals("")){
				JSONObject header=new JSONObject(headers);
				String[] headerArray=header.getNames(header);
				for(int i=0; i<headerArray.length; i++){
					request.setHeader(headerArray[i], header.get(headerArray[i]).toString());
				}
			}
				if(!parameters.equals("")){
					StringEntity jsonEntityObj = new StringEntity(parameters);
					jsonEntityObj.setContentType("application/json");
					request.setEntity(jsonEntityObj);
					}
			response=httpclient.execute(request);
			responseEntity=response.getEntity();
			responseResult=EntityUtils.toString(responseEntity,"UTF-8");
			System.out.println("post request result is = "+ responseResult);
		}
		else if (method.equalsIgnoreCase("get")){
			HttpGet request = new HttpGet(requestUrl);
			if(!headers.equals("")){
				JSONObject header=new JSONObject(headers);
				String[] headerArray=header.getNames(header);
				for(int i=0; i<headerArray.length; i++){
					request.setHeader(headerArray[i], header.get(headerArray[i]).toString());
				}
			}
			response = httpclient.execute(request);
			responseEntity=response.getEntity();
			responseResult=EntityUtils.toString(responseEntity,"UTF-8");
			System.out.println("get request result is = "+ responseResult);
		}else if(method.equalsIgnoreCase("delete")){
			HttpDelete request = new HttpDelete(requestUrl);
			if(!headers.equals("")){
				JSONObject header=new JSONObject(headers);
				String[] headerArray=header.getNames(header);
				for(int i=0; i<headerArray.length; i++){
					request.setHeader(headerArray[i], header.get(headerArray[i]).toString());
				}
			}
			response =httpclient.execute(request);
			responseEntity=response.getEntity();
			responseResult=EntityUtils.toString(responseEntity,"UTF-8");
			System.out.println("delete request result is = "+ responseResult);
		}
		
		//new JSONObject()
		
	}
	
}
