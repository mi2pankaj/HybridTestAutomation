package lenskart.tests;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;



public class APIVerification {

	Logger logger = Logger.getLogger(APIVerification.class.getName());


	/**
	 * This method is verifiying whether all the API's giving 200 ok response or not
	 * @param apiHostName
	 * @return pass or fail
	 */
	public String apiVerification(String apiHostName){
		String result="";
		try{
			APIVerification obj=new APIVerification();

			String sessionAPIResponse = obj.sessionAPIEVerification(apiHostName);
			String getMenuAPIResponse = obj.getMenuAPIVerification(apiHostName);
			String getCategoryAPIResponse = obj.categoryAPI(apiHostName);
			String getProductAPIResponse = obj.productAPI(apiHostName);
			sessionAPIResponse = obj.sessionAPIEVerification(apiHostName);
			

			if(sessionAPIResponse.contains("200") 
					&& getMenuAPIResponse.contains("200")
					&& getCategoryAPIResponse.contains("200")
					&& getProductAPIResponse.contains("200"))
			{
				result = "Pass: API verification passed";
				logger.info("API verification passed");

			}else{
				result = "Fail: API verification failed";
				logger.info("API verification failed");
			}

		}catch(Exception e){
			logger.error(e.getMessage(), e);
			System.out.println(e.getMessage());
		}
		return result;
	}

	/**
	 * Method to verify session api is working fine or not
	 * @return response_code
	 */
	public String sessionAPIEVerification(String apiHostName){
		String response_code ="";
		String requestUrl = "";
		
		try{
			requestUrl = "https://"+apiHostName+"/v2/sessions/";
			HttpPost request = new HttpPost(requestUrl);
			
			logger.info("Requested url for the session API is ="+ requestUrl);
			System.out.println("Requested url for the session API is ="+ requestUrl);
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse response = httpclient.execute(request);
			
			/** Get the api response for the session api */
			HttpEntity responseEntity = response.getEntity();
			String sessionAPIResponse = EntityUtils.toString(responseEntity, "UTF-8");
			JSONObject responseObject=new JSONObject(sessionAPIResponse);
			
			logger.info("Request header is >>>> " + "Not required" );
			
			logger.info("Response for the session api is >>> "+responseObject+"\n");
			System.out.println("Response for the session api is >>> "+responseObject+"\n");
		
			response_code = response.getStatusLine().toString();
			if(response_code.contains("200")){
				logger.info("Session API is working fine with the response = "+ response_code + " request: " + requestUrl);
				System.out.println("Session API is working fine with the response = "+ response_code + " request: " + requestUrl);
			}else{
				logger.error("Session API is not working fine with the response = "+ response_code + " request: " + requestUrl);
				System.out.println("Session API is not working fine with the response = "+ response_code + " request: " + requestUrl);
			}
		}catch(Exception e){
			logger.error("Exception occured while executing the API = "+requestUrl, e);
			System.out.println("Exception occured while executing the API = "+requestUrl+ " " + e);
		}
		return response_code;
	}

	/**
	 * Method to verify getMenu api is working fine or not
	 * @param apiHostName
	 * @return
	 */
	public String getMenuAPIVerification(String apiHostName){
		String response_code ="";
		String requestUrl = "https://www.lenskart.com/getMenu/navigation";
		
		try{
			if(apiHostName.equalsIgnoreCase("preprod")){
				
				requestUrl = "http://52.77.163.233/getMenu/navigation";
				
			}else if(apiHostName.equalsIgnoreCase("webqe")){
				
				requestUrl = "http://52.77.71.55/getMenu/navigation";
				
				
			}else if(apiHostName.equalsIgnoreCase("scmqe")){
				
				requestUrl = "http://54.255.251.151/getMenu/navigation";
				
			}else if(apiHostName.equalsIgnoreCase("posqe")){
				
				requestUrl = "http://54.255.251.194/getMenu/navigation";
				
			}
			logger.info("Requested url for the getMenu API is ="+ requestUrl);
			System.out.println("Requested url for the getMenu API is ="+ requestUrl);
			
			HttpGet request = new HttpGet(requestUrl);

			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse response = httpclient.execute(request);
			response_code = response.getStatusLine().toString();
			
			/** Get the api response for the getMenu api */
			HttpEntity responseEntity = response.getEntity();
			String getMenuAPIResponse = EntityUtils.toString(responseEntity, "UTF-8");
			JSONArray responseObject =new JSONArray(getMenuAPIResponse);
			
			logger.info("Request header is >>>> " + "Not required" );
			logger.info("Response for the getMenu api is >>> "+responseObject+"\n");
			System.out.println("Response for the getMenu api is >>> "+responseObject+"\n");
			
			if(response_code.contains("200")){
				logger.info("getMenu API is working fine with the response = "+ response_code + " request: " + requestUrl);
				System.out.println("getMenu API is working fine with the response = "+ response_code + " request: " + requestUrl);
			}else{
				logger.error("getMenu API is not working fine with the response = "+ response_code + " request: " + requestUrl);
				System.out.println("getMenu API is not working fine with the response = "+ response_code + " request: " + requestUrl);
			}
		}catch(Exception e){
			logger.error("Exception occured while executing the API = "+requestUrl, e);
			System.out.println("Exception occured while executing the API = "+requestUrl+" " + e);
		}
		return response_code;
	}

	/**
	 * Method to verify Category api is working fine or not
	 * @param apiHostName
	 * @return
	 */
	public String categoryAPI(String apiHostName){
		String response_code ="";
		String requestUrl = "";
		
		try{
			requestUrl = "https://"+apiHostName+"/juno/services/v1/category/3194";
			logger.info("Requested url for the category API is ="+ requestUrl);
			System.out.println("Requested url for the category API is ="+ requestUrl);
			
			HttpGet request = new HttpGet(requestUrl);
			request.setHeader("X-Api-Client", "desktop");

			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse response = httpclient.execute(request);
			
			logger.info("Request header is >>>> " + "'X-Api-Client' : 'desktop'"  );
			System.out.println("Request header is >>>> " + "'X-Api-Client' : 'desktop'" );
			
			/** Get the api response for the category api */
			HttpEntity responseEntity = response.getEntity();
			String categoryAPIResponse = EntityUtils.toString(responseEntity, "UTF-8");
			JSONObject responseObject=new JSONObject(categoryAPIResponse);
			
			logger.info("Response for the category api is >>> "+responseObject+"\n");
			System.out.println("Response for the category api is >>> "+responseObject+"\n");
			
			response_code = response.getStatusLine().toString();
			if(response_code.contains("200")){
				logger.info("Category API is working fine with the response ="+ response_code + " request: " + requestUrl);
				System.out.println("Category API is working fine with the response ="+ response_code + " request: " + requestUrl);
			}else{
				logger.error("Category API is not working fine with the response ="+ response_code + " request: "+requestUrl);
				System.out.println("Category API is not working fine with the response ="+ response_code + " request: "+requestUrl);
			}
		}catch(Exception e){
			logger.error("Exception occured while executing the API ="+requestUrl, e);
			System.out.println("Exception occured while executing the API ="+requestUrl+" "+ e);
		}
		return response_code;
	}

	/**
	 * Method to verify Product API is working fine or not
	 * @param apiHostName
	 * @return
	 */
	public String productAPI(String apiHostName){
		String response_code ="";
		String sessionToken ="";
		
		String url = "";
		try{

			url = "https://"+apiHostName+"/v2/sessions/";
			HttpPost postRequest = new HttpPost(url);

			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse response = httpclient.execute(postRequest);
			String sessionResponse_code = response.getStatusLine().toString();
			logger.info("session API is giving the response ="+ sessionResponse_code);
			System.out.println("session API is giving the response ="+ sessionResponse_code);

			if(sessionResponse_code.contains("200")){

				HttpEntity responseEntity = response.getEntity();
				String sessionResponse = EntityUtils.toString(responseEntity, "UTF-8");
				JSONObject sessionObject=new JSONObject(sessionResponse);
				JSONObject sessionResultObj=sessionObject.getJSONObject("result");
				sessionToken=sessionResultObj.get("id").toString();
			}else{
				logger.info("Session API is not working fine with response code = "+sessionResponse_code);
				
				System.out.println("Session API is not working fine with response code =" + sessionResponse_code );
			}
			/**
			 * Now Running the Product API by using the session token generated from the session api
			 */
			String product_id = "122049";
			String requestUrl = "https://"+apiHostName+"/juno/services/v1/product/"+product_id;
			HttpGet request = new HttpGet(requestUrl);
			request.setHeader("X-Api-Client", "desktop");
			request.setHeader("X-Session-Token", sessionToken);
			
			logger.info("Request header is >>>> " + "'X-Api-Client' : 'desktop'");
			logger.info("Request header is >>>> " + "'X-Session-Token' :" + sessionToken  );

			CloseableHttpClient httpclient1 = HttpClients.createDefault();
			CloseableHttpResponse response1 = httpclient1.execute(request);
			
			/** Get the api response for the product api */
			HttpEntity responseEntity = response1.getEntity();
			String productAPIResponse = EntityUtils.toString(responseEntity, "UTF-8");
			JSONObject responseObject=new JSONObject(productAPIResponse);
			
			logger.info("Response for the product api is >>> "+responseObject+"\n");
			System.out.println("Response for the product api is >>> "+responseObject+"\n");
			
			
			response_code = response1.getStatusLine().toString();
			
			if(responseObject.toString().contains("Product does not exist")){
				logger.info("Product doesn't exists hence setting the status code as 200");
				response_code = "200";
			}
			
			if(response_code.contains("200")){
				logger.info("Product API is working fine with the response ="+ response_code + " request url: "+url);
				System.out.println("Product API is working fine with the response ="+ response_code + " request url: "+url);
			}else{
				logger.error("product API is not working fine with the response ="+ response_code + " request url: "+url + "and product_id = "+product_id);
				System.out.println("product API is not working fine with the response ="+ response_code + " request url: "+url + "and product_id = "+product_id);
			}
		}catch(Exception e){
			logger.error("Exception occured while executing the API ="+ url, e);
			System.out.println("Exception occured while executing the API ="+ url+" "+ e);
		}
		return response_code;
	}


}
