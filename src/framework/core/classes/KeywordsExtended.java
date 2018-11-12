package framework.core.classes;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import com.mysql.jdbc.Connection;

import framework.utilities.CaptureScreenShotLib;
import framework.utilities.GenericMethodsLib;
import framework.utilities.JsonParserHelper;
import framework.utilities.httpClientWrap;
import lenskart.tests.ApiDataMapping;
import lenskart.tests.CLProductLenskartPrice;
import lenskart.tests.OrderDetails;
import lenskart.tests.TestSuiteClass;
import net.lightbody.bmp.proxy.ProxyServer;


public class KeywordsExtended extends Keywords{

	Logger logger = Logger.getLogger(KeywordsExtended.class.getName());

	public KeywordsExtended(Connection connection, JSONObject jsonObjectRepo, ProxyServer proxyServer) {
		super(connection, jsonObjectRepo, proxyServer);
	}

	/**
	 * This keyword compare 2 string coming from data. data e.g. string1,string2
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String comparestring(WebDriver driver, String objectName, String data)
	{
		String result = "";

		String[] stringArray=null;
		try{
			stringArray=data.split(",");
			String first=stringArray[0].trim();
			String second=stringArray[1].trim();

			if(first.equalsIgnoreCase(second)){
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Both First and Second strings are same, expected="+first+" and actual ="+second);
				result=passed_status+ " expected="+first+" and actual ="+second;
			}else{
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Both First and Second strings are not same, expected="+first+" and actual ="+second);
				result=failed_status+ " expected= "+first+" and actual= "+second;
			}
		}
		catch(Exception e)
		{
			result = failed_status+ "Unable to compare strings ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while clicking button element: " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}

	/** check if prescription banner is displayed on mobile site after aad to cart on package screen, get the json values 
	 * rather than waiting for it.
	 * 
	 * @return
	 */
	public String getpackagesconfigdetail(WebDriver driver, String objectName, String data) {

		String result; 
		try
		{
			String x = httpClientWrap.sendGetRequest("https://www.lenskart.com/juno/services/v1/redis?keys=PACKAGES_CONFIG&v="+new Date().getTime());

			JSONParser jsonq = new JSONParser();
			org.json.simple.JSONObject xx =  (org.json.simple.JSONObject) jsonq.parse(x);
			org.json.simple.JSONObject xxx = (org.json.simple.JSONObject)xx.get("result");
			org.json.simple.JSONObject xxxx  = (org.json.simple.JSONObject) jsonq.parse(xxx.get("PACKAGES_CONFIG").toString());

			try {
				result = xxxx.get(data.trim()).toString();
			}catch (Exception e) {
				result = failed_status + "Error Occurred while getting value of key: "+data + " -- "+e.getMessage();
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + e.getMessage(), e);
			}

		}catch (Exception e) {

			result = failed_status + "Error_Occurred";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + e.getMessage(), e);
		}

		return result;
	}

	/** delete cart items
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String deletecart(WebDriver driver, String objectName, String data) {

		try {

			WebElement cartIcon = null;

			try {			
				cartIcon=driver.findElement(By.xpath(".//a[@class='icon-remove']"));
			}catch (org.openqa.selenium.NoSuchElementException e) {
				return passed_status + ": Cart is already empty";
			}

			while(cartIcon!=null) {
				cartIcon.click();

				try{cartIcon=driver.findElement(By.xpath(".//a[@class='icon-remove']"));}catch (org.openqa.selenium.NoSuchElementException e) {
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : " +e.getMessage() + " Exiting code ... ");

					break;
				}
			}

			return passed_status + ": Cart is deleted";

		}catch (Exception e) {

			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occurred while deleting cart", e);

			// Taking screenshot during exception 
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
			return failed_status + " Could not remove the product from the cart ";

		}

	}

	/** this will change vsm execution status in recevied serial file - for specific orders -- status change to true. 
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String changevsmexecutionstatus(WebDriver driver, String objectName, String data){
		return new HandlerLib().changeVsmExecutionStatus_UsingJacksonMapper(data);
	}

	public String get_apiresponse(WebDriver driver, String objectName, String data){
		objectName = objectName.replace("{", "");
		objectName = objectName.replace("}", "");
		objectName = objectName.trim();

		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received URL: "+objectName);

		//StringBuffer apiresult = new StringBuffer();
		String result="";
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		String sessionToken1 = null;
		String xSessionToken = null;


		try 
		{
			if(objectName.isEmpty() || objectName.equalsIgnoreCase(""))
			{	
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Provided server url is Either BLANK or EMPTY, Please check it again. "+objectName);
			}
			else
			{			
				//Adding add custom headers in request
				if(data != null && (!data.isEmpty()))
				{
					httpclient = HttpClients.createDefault();
					String method = JsonParserHelper.getAsString(data, "type");

					if (method.equals("Post")){

						HttpPost request = new HttpPost(objectName);
						HttpResponse postSessionResponse=httpclient.execute(request);
						HttpEntity sessionEntity=postSessionResponse.getEntity();

						String sessionResponse=EntityUtils.toString(sessionEntity, "UTF-8");
						JSONObject sessionObject=new JSONObject(sessionResponse);
						JSONObject sessionResultObj=sessionObject.getJSONObject("result");
						sessionToken1=sessionResultObj.get("id").toString();

						if ((JsonParserHelper.getAsString(data,"cartClean").equals("yes"))){
							request = new HttpPost("https://api.lenskart.com/v2/customers/authenticate");
							//postSessionResponse=httpclient.execute(request);
							request.setHeader("X-Api-Client", "desktop");
							request.setHeader("X-Session-Token", sessionToken1);
							request.setHeader("Content-Type", "application/json");
							String stringBody="{\"username\":\""+JsonParserHelper.getAsString(data, "username")+"\",\"password\":\""+JsonParserHelper.getAsString(data, "password")+"\"}";

							StringEntity JsonEntityObj = new StringEntity(stringBody.toString());
							request.setEntity(JsonEntityObj);
							//postLoginRequest.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
							response=httpclient.execute(request);

							HttpEntity loginEntity=response.getEntity();
							String loginRes=EntityUtils.toString(loginEntity,"UTF-8");
							JSONObject loginJSONObj=new JSONObject(loginRes);
							JSONObject loginResultObj=loginJSONObj.getJSONObject("result");

							xSessionToken=loginResultObj.get("token").toString();

							HttpGet getRequest = new HttpGet("https://api.lenskart.com/v2/carts");
							getRequest.setHeader("X-Api-Client", "desktop");
							getRequest.setHeader("X-Session-Token", xSessionToken);
							getRequest.setHeader("Content-Type", "application/json");
							response =httpclient.execute(getRequest);
							HttpDelete deleteRequest = new HttpDelete("https://api.lenskart.com/v2/carts/items");
							deleteRequest.setHeader("X-Api-Client", "desktop");
							deleteRequest.setHeader("X-Session-Token", xSessionToken);
							deleteRequest.setHeader("Content-Type", "application/json");

							response =httpclient.execute(deleteRequest);

							httpclient.close();
						}						
					}
					else if (method.equals("Get")){
						HttpGet request = new HttpGet(objectName);
						response = httpclient.execute(request);

					}
					else if (method.equals("Delete")){
						HttpDelete request = new HttpDelete(objectName);
						request.setHeader("X-Api-Client", "desktop");
						request.setHeader("X-Session-Token", xSessionToken);

						response =httpclient.execute(request);						
						httpclient.close();
					}

				}

			}
		} 
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting response from url: "+objectName, e);
		}
		return result;
	}

	/**
	 * Keyword is used to retrieve object based on grammar from the serialized object.
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String retrievedataobject(WebDriver driver, String objectName, String data){
		String object=null;
		String objectPath =TestSuiteClass.AUTOMATION_HOME+"/dataObject";
		String jsonData;
		JSONArray jsonArrayObj;
		OrderDetails orderdetailObj;
		ObjectInputStream ois;
		int index=0;
		String objectParam=null;
		try{
			objectParam=data.substring(data.indexOf("GET")+3, data.indexOf("ON Index")).trim();
			index=Integer.parseInt(data.substring(data.indexOf("ON Index")+8, data.length()).trim());
			ois=new ObjectInputStream(new FileInputStream(new File(objectPath)));
			orderdetailObj = (OrderDetails) ois.readObject();
			jsonData=orderdetailObj.getOrderDetail();
			jsonArrayObj=new JSONArray(jsonData);
			JSONObject obj = (JSONObject) jsonArrayObj.get(index);
			object=obj.get(objectParam).toString();
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occured with retriving data from the object "+data, e);
		}
		return object;
	}

	/**
	 * This keyword is used to get Price of any element.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String storedata_calculateloyalitydiscount(WebDriver driver, String objectName, String data)
	{
		String value="";
		int intValue=0;
		int discount=0;
		try 
		{
			intValue=Integer.parseInt(data);
			discount=(intValue*10)/100;
			value=String.valueOf(discount);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Discount calculated is: " +value);
			return value;

		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred  "+e+" while getting the text from the element: " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + " : could not retrive the text";
		}
	}

	/**
	 * Get the special price for cl price check
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String storedata_get_cl_producturl(WebDriver driver, String objectName, String data){
		String productUrl ;
		try{
			CLProductLenskartPrice clObj = new ApiDataMapping().serializeCLDataUsingJacksonMapper();

			if(clObj.getProductUrl().isEmpty() || clObj.getProductUrl()==null || clObj.getProductUrl()==""){
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Product Url is null, ");
				return failed_status + " Couldn't get the product url ";
			}else{
				productUrl = clObj.getProductUrl();
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : product Url for the cl special price is = "+productUrl);
				return productUrl;
			}
		}catch(Exception e){

			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occurred getting CL product url", e);
			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + " Couldn't get the CL product url ";
		}
	}

	/** get captcha code to be entered
	 * @author rishi
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String storedata_getcaptchacode(WebDriver driver, String objectName, String data){
		String result=null;
		String captchaText=null;
		try{
			boolean staleExceptionHandleFlag = true;
			int staleExceptionAttempt=0;

			/** Adding a check on staleExceptionHandleFlag exception, in case this occurs then find the element again until the max attempt = 5.
			 */
			while(staleExceptionHandleFlag)
			{
				try
				{					
					/** First check if this a dynamic element, if not then catch customexception and find element conventionally --> 
					 * Now putting condition on data, if data is empty then get element from object repository using objectName 
					 * else find element using objectLabel --> to be used in keyword clickmenu.
					 */
					webelement=getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

					/** Wait until link is visible and clickable, if its not enabled.*/
					By bylocator = getObject.getFinalByLocator(driver, objectName, data, jsonObjectRepo);

					if(!webelement.isEnabled() && webelement != null)
					{
						handler.applyExplicitWait(driver, bylocator, new WebDriverException());
					}

					captchaText = handler.getCaptcha(webelement, driver, bylocator);

					staleExceptionHandleFlag = false;

				}catch(StaleElementReferenceException e){
					staleExceptionAttempt ++;
				}

				if(staleExceptionAttempt ==5){
					break;
				}
			}

			Thread.sleep(2500);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" captcha text is" + captchaText);
			result = captchaText;

		}
		catch(Exception e)
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Unable to capture captcha text" + e);
		}
		return result;	
	}

	/**
	 * This keyword is used to get Price of any element.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String storedata_getprice(WebDriver driver, String objectName, String data)
	{
		String actualValue="";
		try 
		{
			actualValue=storedata_gettext(driver, objectName, data);

			actualValue = actualValue.replace("Rs.", "");

			//Pattern p = Pattern.compile("-?\\d+");

			Pattern p = Pattern.compile("-?[0-9.]+");
			Matcher m = p.matcher(actualValue);
			if(m.find()){
				actualValue=m.group();
			}
			if(actualValue.contains("-")){
				actualValue=actualValue.replace("-", "");
			}

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Text of element is: " +actualValue);
			return actualValue;
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred  "+e+" while getting the price from the element: " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + " : could not retrive the price";
		}
	}

	/**
	 * This keyword is used to get the Objectname of the element.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String storedata_getproductdata(WebDriver driver, String objectName, String data)
	{

		try{
			if(!data.isEmpty()){
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Getting the objectname of element : " + objectName );
				return data;

			}else{
				logger.error(failed_status + "data is blank");
				return "";	
			}
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred  "+e+" while getting the objectname from the element: " +objectName, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + " : could not retrive the objectname";
		}
	}

	/**
	 * Get the product id for cl price check
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String storedata_getproductid(WebDriver driver, String objectName, String data){
		String product_id ;
		try{
			CLProductLenskartPrice clObj = new ApiDataMapping().serializeCLDataUsingJacksonMapper();

			if(clObj.getProduct_id().isEmpty() || clObj.getProduct_id()==null){
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : product_id is null, ");
				return failed_status + " Couldn't get the CL product id ";
			}else{
				product_id = clObj.getProduct_id();
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : product id for the cl special price is = "+product_id);
				return product_id;
			}

		}catch(Exception e){

			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occurred getting CL product id.", e);
			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + " Couldn't get the CL product id ";
		}
	}

	/**
	 * Get the special price for cl price check
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String storedata_getspecialprice(WebDriver driver, String objectName, String data){
		String specialPrice ;
		try{
			CLProductLenskartPrice clObj = new ApiDataMapping().serializeCLDataUsingJacksonMapper();

			if(clObj.getLenskartPrice().isEmpty() || clObj.getLenskartPrice()==null || clObj.getLenskartPrice()==""){
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Product price is null, ");
				return failed_status + " Couldn't get the CL product id ";
			}else{
				specialPrice = clObj.getLenskartPrice();
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : product id for the cl special price is = "+specialPrice);
				return specialPrice;
			}
		}catch(Exception e){

			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occurred getting CL special price.", e);
			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + " Couldn't get the CL special price ";
		}
	}

	/**
	 * This keyword is to match values.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String comparevalue(WebDriver driver, String objectName, String data)
	{
		String result = "";

		String actualValue="";
		float actualFloatValue=0;
		float expectedFloatValue = 0;

		try{

			try{
				/** get the actual text of supplied element */
				actualValue=storedata_getprice(driver, objectName, data);

				/** converting received value in flat */
				actualFloatValue=Float.parseFloat(actualValue);
				expectedFloatValue = Float.parseFloat(data);

				if(actualFloatValue==expectedFloatValue){
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Both expected and actual values are same, actual="+actualFloatValue+" and expected ="+expectedFloatValue);
					result=passed_status+ "Both expected and actual values are same";
				}else{
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Both expected and actual values are not same, actual="+actualFloatValue+" and expected ="+expectedFloatValue);
					result=failed_status+ "Both expected and actual values are not same, actual= "+actualFloatValue+" and expected= "+expectedFloatValue;
				}

			}catch(StaleElementReferenceException w){}

		}
		catch(Exception e)
		{
			result = failed_status+ "Error occurred while comparing values: actual="+actualFloatValue+" and expected ="+expectedFloatValue;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while comparing values. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}

	/**
	 * This keyword is used to get Price of any element.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String calculate_expression(WebDriver driver, String objectName, String data)
	{
		String expression="";
		try 
		{
			expression=data;
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("JavaScript");
			Object objValue=engine.eval(expression);
			int value =Integer.parseInt(objValue.toString());
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Calculate expression is: " +Integer.toString(value));
			return Integer.toString(value);
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred  "+e+" while getting the price from the element: " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + " : could not retrive the price";
		}
	}

	/** this method is used to create session 
	 * @return
	 */
	public String get_sessiontoken (WebDriver driver, String objectName, String data){

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

			JSONObject json= new JSONObject(responseResult);
			JSONObject result=(JSONObject) json.get("result");
			sessionToken =result.get("id").toString();
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " error occurred while getting session token "+e.getMessage(), e);
		}

		return sessionToken;
	}

	/**
	 *  creating session token for the supplied User email. 
	 *  
	 * @param driver
	 * @param objectName
	 * @param Email
	 * @return
	 */
	public String get_usersessiontoken(WebDriver driver, String objectName, String data){

		String token="";
		try{
			HttpClient httpclient= HttpClientBuilder.create().build();
			HttpEntity responseEntity =null;
			String responseresult=null;
			String url="https://api.lenskart.com/v2/customers/authenticate";
			HttpPost httppost = new HttpPost(url);

			httppost.addHeader("x-session-token", get_sessiontoken(driver, objectName, data));

			httppost.addHeader("x-api-client", "desktop");
			httppost.addHeader("Content-Type", "application/json");
			JSONObject jsonobject=new JSONObject();
			jsonobject.put("password", "valyoo123");
			jsonobject.put("username", data);

			httppost.setEntity(new StringEntity(jsonobject.toString()));
			HttpResponse respons=httpclient.execute(httppost);
			responseEntity= respons.getEntity();
			responseresult=EntityUtils.toString(responseEntity,"UTF-8");

			JSONObject json = new JSONObject(responseresult);
			JSONObject result=(JSONObject) json.get("result");
			token=result.get("token").toString();

		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " - " +e.getMessage(), e);
		}

		return token;	
	}

	/** getting number of dittos from supplied user's account
	 * 
	 * @param driver
	 * @param objectName
	 * @param User
	 * @return
	 */
	public String get_numberofditto(WebDriver driver, String objectName, String email){

		int numberOfDittos = 0;
		try{

			String url="https://api.lenskart.com/v2/utility/ditto/customer/";
			HttpClient httpclient=HttpClientBuilder.create().build();
			HttpEntity responseEntity;
			String responseResult;
			HttpGet httpget=new HttpGet(url);
			httpget.addHeader("x-session-token", get_usersessiontoken(driver,objectName,email));
			httpget.addHeader("x-api-client", "desktop");
			HttpResponse response=httpclient.execute(httpget);
			//			int status=response.getStatusLine().getStatusCode();
			responseEntity=response.getEntity();
			responseResult=EntityUtils.toString(responseEntity);
			JSONObject json= new JSONObject(responseResult);
			JSONObject jsonobject= json.getJSONObject("result");
			String dittos= jsonobject.get("dittoId").toString();
			JSONArray jsonArray=new JSONArray(dittos);
			numberOfDittos=jsonArray.length();

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " - " + " number of dittos: "+numberOfDittos);

		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " - " +e.getMessage(), e);
		}

		return String.valueOf(numberOfDittos);
	}

	/** this method will be used to get the status of create ditto api.
	 * 
	 * sample user - user lenskart.test52@gmail.com
	 * @param driver
	 * @param objectName
	 * @param UserEmail
	 * @return
	 */
	public String get_createdittostatus(WebDriver driver, String objectName,String UserEmail){

		int statusCode=0;

		try{

			HttpClient httpclient=HttpClientBuilder.create().build();
			String url="https://www.lenskart.com/juno/services/v1/ditto";

			HttpPost httppost=new HttpPost(url);
			httppost.addHeader("sessiontoken", get_usersessiontoken(driver,objectName,UserEmail));
			httppost.addHeader("Content-Type", "application/json");

			JSONObject jsonobject=new JSONObject();
			jsonobject.put("ditto_id", "0994517264c26c7fa0762ae20e3d9256167c299b");
			httppost.setEntity(new StringEntity(jsonobject.toString()));
			HttpResponse response= httpclient.execute(httppost);
			statusCode =response.getStatusLine().getStatusCode();

		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " - " +e.getMessage(), e);
		}

		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " - returning status: "+statusCode);
		return String.valueOf(statusCode);
	}

	/** getting the status of filter api like 200
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String get_filterapistatus(WebDriver driver, String objectName, String data)
	{
		String status="";

		String har = TestSuiteClass.AUTOMATION_HOME+"/HAR-"+new Date().getTime();
		try{
			if(proxyServer != null){
				proxyServer.getHar().writeTo(new File(har));
				LineIterator it = FileUtils.lineIterator(new File(har));
				StringBuffer str = new StringBuffer();

				while(it.hasNext())
				{
					str.append(it.next());
				}

				JSONParser jsonParser = new JSONParser();
				org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonParser.parse(str.toString());
				org.json.simple.JSONObject log=(org.json.simple.JSONObject) jsonObject.get("log");
				org.json.simple.JSONArray array=(org.json.simple.JSONArray) log.get("entries");

				for(int i=0; i<array.size(); i++){
					org.json.simple.JSONObject finalObject=(org.json.simple.JSONObject) array.get(i);
					org.json.simple.JSONObject request=(org.json.simple.JSONObject) finalObject.get("request");
					org.json.simple.JSONObject response=(org.json.simple.JSONObject) finalObject.get("response");

					if(request.get("url").toString().contains("https://www.lenskart.com/juno/services/v1/filters/")){
						status=response.get("status").toString();
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " response: "+status+ " of filter api : "+request);
						break;
					}
				}
			}

		}catch(Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" exception occured while getting the status of Filter api: "+e.getMessage(), e);
		}
		return status;
	}

	/** this method will get the PDP_CONFIG api - response to check the CTA on specific products .. 
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 */
	public String get_ctafrom_pdpconfig(WebDriver driver, String objectName, String data) {

		String brand="";
		String category="";

		try {
			httpClientWrap httpclient=new httpClientWrap();
			HashMap<Object, Object> redisKey=httpclient.sendGetRequestWithParams("https://www.lenskart.com/juno/services/v1/redis?keys=PDP_CONFIG&v="+new Date().getTime());
			String valueOfRediskey=(String)redisKey.get("response");
			JSONParser jparse=new JSONParser();
			org.json.simple.JSONObject result=(org.json.simple.JSONObject)jparse.parse(valueOfRediskey);
			org.json.simple.JSONObject valueOfPdpConfig=(org.json.simple.JSONObject) result.get("result");
			String keyOnPdp=(String) valueOfPdpConfig.get("PDP_CONFIG");
			org.json.simple.JSONObject valueOfPdpConfigjson=(org.json.simple.JSONObject)jparse.parse(keyOnPdp);
			org.json.simple.JSONObject showTAS = (org.json.simple.JSONObject)jparse.parse(valueOfPdpConfigjson.toString());
			org.json.simple.JSONObject showTASObj=(org.json.simple.JSONObject) showTAS.get("showTAS");
			Set<String> keys = showTASObj.keySet();

			for(String k : keys) {
				org.json.simple.JSONObject jsonCatList=(org.json.simple.JSONObject)showTASObj.get(k);
				if(String.valueOf(jsonCatList).contains("ON")){

					Set<String> brandset=jsonCatList.keySet();
					for(String s:brandset) {

						Object brandname=jsonCatList.get(s);
						if(String.valueOf(brandname).equalsIgnoreCase("ON")) {

							category=k;
							brand=s;
							logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ " cta received from api==> Category is :"+ category + " Brand is : "+ brand);
							break;
						}
					}

					break;
				}
			}
		} catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " - " +e.getMessage(), e);
		}

		return category+","+brand;
	}

	/** getting the category title from category api 
	 * >>if cat api return h1 then title will be h1 value else category name from api
	 * 
	 * 
	 * */
	public  String  get_categorytitle(WebDriver driver, String objectName,String data) {
		String categoryValue="";

		try {

			String url="https://www.lenskart.com/juno/services/v1/category/"+data+"?page-size=90&page=0";
			HashMap<String, String>header=new HashMap<>();
			header.put("x-api-client", "desktop");
			String responsbody=httpClientWrap.sendGetRequest(url, header);
			JSONParser parser =new JSONParser();
			org.json.simple.JSONObject jsonobject=(org.json.simple.JSONObject) parser.parse(responsbody);
			org.json.simple.JSONObject result1=(org.json.simple.JSONObject) jsonobject.get("result");

			if(result1.containsKey("h1")) {
				categoryValue=result1.get("h1").toString();
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"category tiltle will be the value of h1 and value  is: "+categoryValue);
			}else {
				categoryValue=result1.get("category_name").toString();
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"category title is:"+categoryValue);
			}

		}catch(Exception e) {

			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Exception occured while getting category title"+e.getMessage(),e);
		}

		return categoryValue;
	}

	/**

	 * checking the Product quantity ,
	 * we can check the  other thing of product api which present on the first index(i.e quantity ,..) 
	 * by putting the value in the object name
	 * 
	 * **/
	public  String  get_product(WebDriver driver, String objectName,String data) {
		String qty="";

		try {
			Integer pid	=Integer.valueOf(data);
			HashMap< String, String> headers=new HashMap<>();
			headers.put("x-api-client", "desktop");
			String url= "https://www.lenskart.com/juno/services/v1/product/"+pid;
			String responsebody=httpClientWrap.sendGetRequest(url, headers);
			JSONParser jsonparser=new JSONParser();
			org.json.simple.JSONObject jsonobject=(org.json.simple.JSONObject) jsonparser.parse(responsebody);
			String resultValue= jsonobject.get("result").toString();
			org.json.simple.JSONObject result1=(org.json.simple.JSONObject) jsonparser .parse(resultValue);

			qty= result1.get(objectName).toString();
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"value of "+objectName+":"+qty);

		}catch(Exception e) {
			logger.error("Exception occured while getting ther quantity"+e.getMessage(),e);

		}
		return qty;

	}

	/** This method will return all the console logs, later on this can be refined - like just to get errors etc.
	 * 
	 */
	public String getbrowserconsolelogs(WebDriver driver, String objectName, String data)
	{
		String result = "";

		if(driver instanceof FirefoxDriver){

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " - firefox driver doesn't support console logs.");
			result=warning_status +" firefox driver doesn't support console logs.";

			return result;

		}else{

			try
			{
				LogEntries en = driver.manage().logs().get(LogType.BROWSER);

				for(LogEntry e : en)
				{
					/** excluding INFO and FINE log level, adding only SEVERE, WARNING */
					if(
							(!e.getMessage().contains("tracker.unbxdapi.com"))
							&& (!e.getMessage().contains("secure.livechatinc.com"))
							&& (e.getMessage().contains("api.lenskart.com") || e.getMessage().contains("www.lenskart.com")
									|| e.getMessage().contains("home-") || e.getMessage().contains("compare-looks-") || e.getMessage().contains("checkout-")
									|| e.getMessage().contains("my-account-") || e.getMessage().contains("success-") || e.getMessage().contains("summary-")
									|| e.getMessage().contains("retrypayment") || e.getMessage().contains("product-") || e.getMessage().contains("category-")
									|| e.getMessage().contains("omni-"))
							&& !e.getLevel().toString().equalsIgnoreCase("INFO")	 
							&& !e.getLevel().toString().equalsIgnoreCase("FINE")
							)
					{
						result = result + e.getLevel() + " ==> " + e.getMessage() + "\n";
					}
				}
			}catch (Exception e) {
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occured while getting console browser logs. ", e);
			}
			return result;
		}
	}

	/** this keyword will return the network requests
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String getproxylog(WebDriver driver, String objectName, String data)
	{
		String result= "";

		String har = TestSuiteClass.AUTOMATION_HOME+"/HAR-"+TestSuiteClass.UNIQ_EXECUTION_ID.get()+"-"+new Date().getTime();
		try{
			if(proxyServer != null){
				proxyServer.getHar().writeTo(new File(har));
				LineIterator it = FileUtils.lineIterator(new File(har));
				StringBuffer str = new StringBuffer();

				while(it.hasNext())
				{
					str.append(it.next());
				}

				JSONParser jsonParser = new JSONParser();
				org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonParser.parse(str.toString());
				org.json.simple.JSONObject log=(org.json.simple.JSONObject) jsonObject.get("log");
				org.json.simple.JSONArray array=(org.json.simple.JSONArray) log.get("entries");

				for(int i=0; i<array.size(); i++){
					org.json.simple.JSONObject finalObject=(org.json.simple.JSONObject) array.get(i);
					org.json.simple.JSONObject request=(org.json.simple.JSONObject) finalObject.get("request");
					org.json.simple.JSONObject response=(org.json.simple.JSONObject) finalObject.get("response");
					org.json.simple.JSONObject content=(org.json.simple.JSONObject) response.get("content");

					if(		!request.get("url").toString().contains("tracker.unbxdapi.com")
							&& (request.get("url").toString().contains("lenskart.com")
									&& (response.get("status").toString().startsWith("5") || response.get("status").toString().startsWith("4"))))
					{
						String requestedUrl="";
						String status ="";
						String responseText ="";
						String headers ="";
						try{
							requestedUrl=request.get("url").toString();
						}catch(NullPointerException e){

						}

						try{
							status=response.get("status").toString();
						}catch(NullPointerException e){

						}

						try{
							responseText=content.get("text").toString();
						}catch(NullPointerException e){

						}

						try{
							headers=handler.getHeaders(request);
						}catch(NullPointerException e){

						}

						result = result + "Request ==> " + requestedUrl +
								"\nStatus ==> " + status +
								"\nResponse ==> " + responseText +
								"\nHeaders ==> " + headers + "\n";
					}
				}
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Logs are generated for the test case");	

				/** stopping server after getting har file */
				proxyServer.stop();
			}else{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Proxy server wasn't started. ");
			}
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occured while getting the log file", e);
		}

		return result;
	}

	public String get_randomproduct(WebDriver driver, String objectName, String data){

		By bylocator;
		try {
			bylocator = getObject.getFinalByLocator(driver, objectName, data, jsonObjectRepo);
			List<WebElement> totalProduct = driver.findElements(bylocator);
			if(totalProduct.size()!=1){
				Random randomBrand = new Random();
				int randomBrandIndex = randomBrand.nextInt(totalProduct.size()-1);
				WebElement el1= totalProduct.get(randomBrandIndex);
				/*	productId= el1.getAttribute("unbxdparam_sku");
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Product ID is:" +productId);*/
				handler.performMouseAction(driver,el1);

				String javaScript = "arguments[0].click();";
				handler.executeJavaScript(driver, javaScript, el1);
				//clickByJS(driver,el1);

			} 
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" get_randomproduct Fail ");
			return failed_status;

		}

		return passed_status;

	}

	/**
	 * This is a generic method to parse JSON response
	 	result.items[productId,122078].catalogPrices[name,Market Price].value to get market price value for product is 122078
		result.id.value to get value of key id
		Note [] is used when we want to retrieve the value of a particular index from JSON array, example catalogPrices[name,Market Price]

	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String get_responsefieldasstring (WebDriver driver, String objectName, String data) {
		String result="";
		try {
			result =JsonParserHelper.getAsString(data, objectName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Exception occurred while prasing JSON response field", e);
			return failed_status + "Cannot get value for objectName specified" + objectName;
		}


		return  result;		
	}

	public String get_driver_session(WebDriver driver, String objectName, String data){

		String session = driver.manage().getCookieNamed("frontend").getValue();
		logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Session id is :- ");
		return session;
	}

	/**
	 * get the PRESCRIPTION_MODAL value from redis to decide if the prescription info screen will be displayed or not.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public  String  get_prescriptiondetailsvalue(WebDriver driver, String objectName, String data) {
		String result="";
		try {
			String Url="https://www.lenskart.com/juno/services/v1/redis?keys=PRESCRIPTION_MODAL&v="+new Date().getTime();
			String response=httpClientWrap.sendGetRequest(Url);
			JSONParser parser =new JSONParser();
			org.json.simple.JSONObject jsonobject=(org.json.simple.JSONObject) parser.parse(response);
			org.json.simple.JSONObject resultvalue=(org.json.simple.JSONObject) jsonobject.get("result");
			result=resultvalue.get("PRESCRIPTION_MODAL").toString();
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"value of the PRESCRIPTION_MODAL is "+result);

		}catch(Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Exception occured while getting the value of PRESCRIPTION_MODAL"+e.getMessage(),e );

		}
		return result;

	}

	/** This method get the color options array from the product api and the count of the color option array is our color options product visible 
	 * on the UI of product detail page. This method return the count of array of color options 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return 
	 */ 

	public String coloroptions_count(WebDriver driver, String objectName, String data) {
		String coloroptions_count="";
		try {
			String colorOption=get_product(driver, objectName, data);
			JSONParser jparse=new JSONParser();
			org.json.simple.JSONArray coloroptionsArray=(org.json.simple.JSONArray) jparse.parse(colorOption);
			coloroptions_count=String.valueOf(coloroptionsArray.size()) ;
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Count of the color options is "+coloroptions_count);

		} catch (ParseException e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Exception occured while getting the count of coloroptions from the product page api"+e.getMessage(),e );

		}
		return coloroptions_count;

	}

	
	
	/** checking the hec service api  in har file and getting the book now option */

	public String getbooknowoption(WebDriver driver, String objectName, String data)
	{
		String result="";
		String status;

		String har = TestSuiteClass.AUTOMATION_HOME+"/HAR-"+new Date().getTime();
		try{
			if(proxyServer != null){
				proxyServer.getHar().writeTo(new File(har));
				LineIterator it = FileUtils.lineIterator(new File(har));
				StringBuffer str = new StringBuffer();

				while(it.hasNext())
				{
					str.append(it.next());
				}

				JSONParser jsonParser = new JSONParser();
				org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonParser.parse(str.toString());
				org.json.simple.JSONObject log=(org.json.simple.JSONObject) jsonObject.get("log");
				org.json.simple.JSONArray array=(org.json.simple.JSONArray) log.get("entries");


				for(int i=0; i<array.size(); i++){
					org.json.simple.JSONObject finalObject=(org.json.simple.JSONObject) array.get(i);
					org.json.simple.JSONObject request=(org.json.simple.JSONObject) finalObject.get("request");
					org.json.simple.JSONObject response=(org.json.simple.JSONObject) finalObject.get("response");
					org.json.simple.JSONObject content=(org.json.simple.JSONObject) response.get("content");

					if(request.get("url").toString().contains("https://hto.lenskart.com/HTO/api/HTOService?location=")){
						status=response.get("status").toString();
						if(status.equalsIgnoreCase("200")) {
							String responsebody=content.get("text").toString();
							JSONParser jsonparser=new JSONParser();
							org.json.simple.JSONObject jsonobject=(org.json.simple.JSONObject) jsonparser.parse(responsebody);
							if(jsonobject.containsKey("bookNow")) {
								logger.info(responsebody);
							    result="bookNow";
						        logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Book Now Option is available for Hto order ");
							}else
								result="book now not available";
						}
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " response status: "+status+ " of  api : "+request);
						break;
					}
				}
			}

		}catch(Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" exception occured while getting the status of Filter api: "+e.getMessage(), e);
			result="book now not available";
		}
		return result;
	}

	/**
	 * This keyword is used to get the date from any element.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String storedata_getdate(WebDriver driver, String objectName, String data)
	{
	
		try{
			webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);
	
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Getting the text of element : " + webelement );
	
			String actualValue = webelement.getText().trim();
	
			if(actualValue.contains("BY ")){
				actualValue=actualValue.split("BY ")[1];
	
				if(actualValue.contains("#"))
				{
					int charIndex =	actualValue.indexOf("#")+1;
					String finalvalue=actualValue.substring(charIndex);
					actualValue = finalvalue;
				}
			}
			actualValue=new HandlerLib().convertTextIntoDate(actualValue);	
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Text of element is: " +actualValue);
	
			if(actualValue == null) {
				actualValue = "";
			}
	
			if(webelement.getText().trim().equalsIgnoreCase("today") || webelement.getText().trim().equalsIgnoreCase("tomorrow")){
				actualValue =actualValue+ " i.e. "+webelement.getText().trim();
			}
	
			return actualValue;
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred  "+e+" while getting the text from the element: " +webelement, e);
	
			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
	
			return failed_status + " : could not retrive the text";
		}
	}

	/**
	 * This keyword is used to get the text of any element.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String storedata_gettext(WebDriver driver, String objectName, String data)
	{		
		try{
	
			webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);
	
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Getting the text of element : " + webelement );
	
	
			String actualValue = webelement.getText().trim();
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual Text of element is: " +actualValue);
	
			if(actualValue.contains("Order Id") || actualValue.contains("Appt no") || actualValue.contains("Product Id")){
				actualValue=actualValue.split(": ")[1];
	
				if(actualValue.contains("#"))
				{
					int charIndex =	actualValue.indexOf("#")+1;
					String finalvalue=actualValue.substring(charIndex);
					actualValue = finalvalue;
				}
			}
	
			if(actualValue.contains(",")) {
				actualValue = actualValue.replace(",", "");
			}
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Text of element after processing is: " +actualValue);
	
			return actualValue;
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred  "+e+" while getting the text from the element: " +webelement, e);
	
			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
	
			return failed_status + "could not retrieve the text.";
		}
	}

	/** this keyword is used to get the random data which used to register user
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String get_randomdetails(WebDriver driver, String objectName,String data) {
		String result = "";
		try {
			Random rand=new Random();
			int randnum=100+rand.nextInt(899);
			String randvalue=Integer.toString(randnum);
	
			String Emailid="lenskart.test~~@gmail.com";
			String MobileNo="9999999~~";
	
			if(data.equalsIgnoreCase("email")) {
				result=Emailid.replaceAll("~~",randvalue);
			}else if(data.equalsIgnoreCase("mobile")) {
				result=MobileNo.replaceAll("~~",randvalue);	
			}
		}catch(Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting random data : " + e);
		}
	
		return result;
	}

	/** serialize the received data
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String storedataobject(WebDriver driver, String objectName, String data){
		return new HandlerLib().storedataobject_UsingJacksonMapper(data);
	}




}

