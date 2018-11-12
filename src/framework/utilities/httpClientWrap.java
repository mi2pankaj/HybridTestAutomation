/**
 * Last Changes Done on Jan 27, 2015 12:43:12 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */
package framework.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;

import lenskart.tests.TestSuiteClass; 


public class httpClientWrap 
{

	static Logger logger = Logger.getLogger(httpClientWrap.class.getName()); 

	@SuppressWarnings("finally")
	public static String sendGetRequest(String ServerURL) 
	{
		// It may be more appropriate to use FileEntity class in this particular
		// instance but we are using a more generic InputStreamEntity to demonstrate
		// the capability to stream out data from any arbitrary source
		//
		// FileEntity entity = new FileEntity(file, "binary/octet-stream");


		// add request header
		//GetRequest.addHeader("User-Agent", USER_AGENT);

		ServerURL = ServerURL.replace("{", "");
		ServerURL = ServerURL.replace("}", "");
		ServerURL = ServerURL.trim();

		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		try 
		{
			if(ServerURL.isEmpty() || ServerURL.equalsIgnoreCase(""))
			{	
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Provided server url is Either BLANK or EMPTY, Please check it again. "+ServerURL);
			}
			else
			{
				httpclient = HttpClients	.createDefault();

				ServerURL = ServerURL.replace("%%", "").trim();
				HttpGet GetRequest = new HttpGet(ServerURL);
				try{
					response = httpclient.execute(GetRequest);
				}catch(HttpHostConnectException h){
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Couldn't connect to host: "+ServerURL);
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : --------------------------RESPONSE ----------------------------");
				BufferedReader rd = null;
				try{
					logger.info(response.getStatusLine());
					rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));
				}catch(NullPointerException e){
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No RESPONSE RECEIVED.");
				}

				if(rd != null)
				{
					String line = "";
					while ((line = rd.readLine()) != null)
					{
						result.append(line);
					}

					logger.info(result.toString());
				}
			}
		} 
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting response from url: "+ServerURL, e);
		}
		finally 
		{

			try{
				response.close();
				httpclient.close();
			}
			catch(NullPointerException n)
			{ logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : null pointer exception handled. ");}
			catch(Exception e){
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while closing channel. ",e);
			}


			return result.toString();
		}
	}

	@SuppressWarnings("finally")
	public static String sendGetRequestPrintHeaders(String ServerURL) 
	{
		// It may be more appropriate to use FileEntity class in this particular
		// instance but we are using a more generic InputStreamEntity to demonstrate
		// the capability to stream out data from any arbitrary source
		//
		// FileEntity entity = new FileEntity(file, "binary/octet-stream");


		// add request header
		//GetRequest.addHeader("User-Agent", USER_AGENT);

		ServerURL = ServerURL.replace("{", "");
		ServerURL = ServerURL.replace("}", "");
		ServerURL = ServerURL.trim();

		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		try 
		{
			if(ServerURL.isEmpty() || ServerURL.equalsIgnoreCase(""))
			{	
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Provided server url is Either BLANK or EMPTY, Please check it again. "+ServerURL);
			}
			else
			{
				httpclient = HttpClients.createDefault();

				ServerURL = ServerURL.replace("%%", "").trim();
				HttpGet GetRequest = new HttpGet(ServerURL);
				try{
					response = httpclient.execute(GetRequest);
				}catch(HttpHostConnectException h){
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Couldn't connect to host: "+ServerURL);
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : --------------------------RESPONSE ----------------------------");
				BufferedReader rd = null;
				try{
					logger.info(response.getStatusLine());
					rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));
				}catch(NullPointerException e){
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No RESPONSE RECEIVED.");
				}

				if(rd != null)
				{
					String line = "";
					while ((line = rd.readLine()) != null)
					{
						result.append(line);
					}

					logger.info(result.toString());
				}
			}
		} 
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting response from url: "+ServerURL, e);
		}
		finally 
		{

			try{

				for(int i=0; i< response.getAllHeaders().length; i++)
				{
					System.out.println(response.getAllHeaders()[i].getName() + " - "+ response.getAllHeaders()[i].getValue());
				}

				response.close();
				httpclient.close();
			}
			catch(NullPointerException n)
			{ logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : null pointer exception handled. ");}
			catch(Exception e){
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while closing channel. ",e);
			}


			return result.toString();
		}
	}

	//Send Get Request with custom headers
	public static String sendGetRequest(String serverURL, HashMap<String, String> headers)  
	{
		serverURL = serverURL.replace("{", "");
		serverURL = serverURL.replace("}", "");
		serverURL = serverURL.trim();

		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received URL: "+serverURL);

		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		try 
		{
			if(serverURL.isEmpty() || serverURL.equalsIgnoreCase(""))
			{	
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Provided server url is Either BLANK or EMPTY, Please check it again. "+serverURL);
			}
			else
			{
				httpclient = HttpClients.createDefault();
				HttpGet GetRequest = new HttpGet(serverURL);

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Adding custom headers... ");

				//Adding add custom headers in request
				for(Entry<String, String> map : headers.entrySet())
				{
					String name = map.getKey().trim();
					String value = map.getValue().trim();
					logger.error(name +" : "+value);
					GetRequest.addHeader(name, value);
				}

				response = httpclient.execute(GetRequest);

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : --------------------------RESPONSE ----------------------------");
				logger.debug(response.getStatusLine());

				BufferedReader rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));

				String line = "";
				while ((line = rd.readLine()) != null)
				{
					result.append(line);
				}

				logger.info(result.toString());

				response.close();
				httpclient.close();
			}
		} 
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting response from url: "+serverURL, e);
		}
		//		finally 
		//		{
		return result.toString();
		//		}
	}

	/** Send Get Request with custom headers, return status code.
	 * 
	 * @param serverURL
	 * @param headers
	 * @return
	 */
	@SuppressWarnings("finally")
	public static int getStatusCodeOfGetRequest(String serverURL, HashMap<String, String> headers)  
	{
		serverURL = serverURL.replace("{", "");
		serverURL = serverURL.replace("}", "");
		serverURL = serverURL.trim();

		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received URL: "+serverURL);

		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		int statusCode = 0;
		try 
		{
			if(serverURL.isEmpty() || serverURL.equalsIgnoreCase(""))
			{	
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Provided server url is Either BLANK or EMPTY, Please check it again. "+serverURL);
			}
			else
			{
				httpclient = HttpClients.createDefault();
				HttpGet GetRequest = new HttpGet(serverURL);

				//Adding add custom headers in request
				if(headers != null)
				{
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Adding custom headers... ");
					for(Entry<String, String> map : headers.entrySet())
					{
						String name = map.getKey().trim();
						String value = map.getValue().trim();
						GetRequest.addHeader(name, value);
					}
				}

				response = httpclient.execute(GetRequest);

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : --------------------------RESPONSE ----------------------------");
				logger.debug(response.getStatusLine());

				BufferedReader rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));

				String line = "";
				while ((line = rd.readLine()) != null)
				{
					result.append(line);
				}

				logger.info(result.toString());
			}
		} 
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting response from url: "+serverURL, e);
		}
		finally 
		{
			try
			{
				statusCode = response.getStatusLine().getStatusCode();
				response.close();
				httpclient.close();
			}
			catch(Exception e)
			{
				logger.error(e);
			}

			return statusCode;
		}
	}

	/**
	 * send post request 
	 * @param url
	 * @return
	 */
	public static String sendPostRequest(String url)
	{
		String response= "";
		try
		{
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(url);
			
			/** adding an authorization header - */
			httppost.addHeader("Authorization", "Basic "+new String(org.apache.commons.codec.binary.Base64.encodeBase64("pankaj:password@123".getBytes())));
			HttpResponse httpResponse = httpclient.execute(httppost);
			HttpEntity entity = httpResponse.getEntity();
			response = EntityUtils.toString(entity,"UTF-8");

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + "url == "+url + " response == " +response);
		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() +  " error while requesting url: "+url + " ===> " + e.getMessage(), e);
		}

		return response;
	}

	/** send post request with timeout param
	 * 
	 * @param url
	 * @param postData
	 * @return
	 */
	public HashMap<Object, Object> sendPostRequestWithParams(String url, String postData)
	{
		HashMap<Object, Object> responseMap = new HashMap<>();

		try
		{
			int timeout = 60 * 1000; //sec 

			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout)
					.setSocketTimeout(timeout)
					.build();

			HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();

			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new StringEntity(postData));
			HttpResponse httpResponse = null;

			try {
				httpResponse = httpClient.execute(httppost);
			}catch (Exception e) {
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Timeout in "+timeout + " ===> "+ e.getMessage() + " for URL: "+url + " post data: "+postData);

				/** in case server is not up .. putting status 404 */
				responseMap.put("statuscode", 404);
			}

			if(httpResponse != null) {

				BufferedReader rd = null;
				try{
					logger.info(httpResponse.getStatusLine().getStatusCode() + "  ----  " + httpResponse.getStatusLine());
					rd = new BufferedReader( new InputStreamReader(httpResponse.getEntity().getContent()));
				}catch(NullPointerException e){
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No RESPONSE RECEIVED FROM URL: "+url);
				}

				StringBuffer response = new StringBuffer();
				if(rd != null){
					
					String line = "";
					while ((line = rd.readLine()) != null){
						response.append(line);
					}
				}

				responseMap.put("response", response.toString());
				responseMap.put("statuscode", httpResponse.getStatusLine().getStatusCode());
			}
		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception while sending post request - "+e.getMessage(), e);	
		}

		return responseMap;
	} 

	/** send get request with timeout param 
	 * 
	 * @param url
	 * @return
	 */
	public HashMap<Object, Object> sendGetRequestWithParams(String url) {

		HashMap<Object, Object> responseMap = new HashMap<>();
		try
		{
			int timeout = 60 * 1000; //sec 

			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout)
					.setSocketTimeout(timeout)
					.build();
			HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();

			HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(new HttpGet(url));
			}catch (Exception e) {

				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Timeout in "+timeout + " ===> "+ e.getMessage() + " for URL: "+url);

				/** in case server is not up .. putting status 404 */
				responseMap.put("statuscode", 404);
			}

			if(httpResponse != null) {

				BufferedReader rd = null;
				try{
					logger.info(httpResponse.getStatusLine().getStatusCode() + "  ----  " + httpResponse.getStatusLine());

					rd = new BufferedReader( new InputStreamReader(httpResponse.getEntity().getContent()));
				}catch(NullPointerException e){
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No RESPONSE RECEIVED FROM URL: "+url);
				}

				StringBuffer response = new StringBuffer();
				if(rd != null){
					String line = "";
					while ((line = rd.readLine()) != null){
						response.append(line);
					}
				}

				responseMap.put("response", response.toString());
				responseMap.put("statuscode", httpResponse.getStatusLine().getStatusCode());
			}

		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception while sending get request - "+e.getMessage(), e);
		}

		return responseMap;
	}

	/** send GET request and receive json response.
	 * 
	 * @param url
	 * @param header
	 * @return
	 */
	public org.json.simple.JSONObject getRequest(String url, HashMap<String, String> header){

		org.json.simple.JSONObject jsonResultObject = new org.json.simple.JSONObject();

		try{
			HttpClient httpclient=HttpClientBuilder.create().build();
			HttpEntity responseEntity;
			String responseResult;
			HttpGet httpget=new HttpGet(url);

			if(header != null) {				
				for(Entry<String, String> en : header.entrySet()) {
					httpget.addHeader(en.getKey(), en.getValue());
				}
			}

			HttpResponse response=httpclient.execute(httpget);
			responseEntity=response.getEntity();
			responseResult=EntityUtils.toString(responseEntity);

			JSONParser jp = new JSONParser();
			jsonResultObject = (org.json.simple.JSONObject) jp.parse(responseResult);

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " - " + " response received: "+jsonResultObject);

		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " - " +e.getMessage(), e);
		}

		return jsonResultObject; 
	}

}
