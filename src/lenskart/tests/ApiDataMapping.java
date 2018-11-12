package lenskart.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import core.utilities.GenericMethodsLib;
import lenskart.api.productApiMapper.Product_Parent;


public class ApiDataMapping {
	
	Logger logger = Logger.getLogger(ApiDataMapping.class.getName());
	
	String request_url;
	HttpGet getRequest;
	CloseableHttpClient httpclient;
	CloseableHttpResponse response;
	String status_code;
	HttpEntity responseEntity;
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException{
		CLProductLenskartPrice obj=new ApiDataMapping().serializeCLDataUsingJacksonMapper();
		 System.out.println(">>>>>product_id ="+obj.getProduct_id());
		 System.out.println(">>>>>special price is ="+obj.getLenskartPrice());
		 System.out.println(">>>>>product url is ="+obj.getProductUrl());
	}
	
	/**
	 *	This method will get the list of PID eligible for special discount. 
	 * @return
	 */
	public List<String> getCL_DiscountPIds(){
		String clDiscountAPIResponse;
		List<String> pids= new ArrayList<>();
		try{
			/**get the requested url from the configuration file */
			//request_url = "https://api.lenskart.com/juno/services/v1/redis?keys=cl_discount";
			request_url = "https://api.lenskart.com/juno/services/v1/redis?keys="+GenericMethodsLib.generalConfigurationProperties.getProperty("api_Cl_special_price").toString();
			getRequest = new HttpGet(request_url);
			
			/** execute the API request */
			httpclient  = HttpClients.createDefault();
			response = httpclient.execute(getRequest);
			
			responseEntity = response.getEntity();
			clDiscountAPIResponse = EntityUtils.toString(responseEntity, "UTF-8");
			
			status_code = response.getStatusLine().toString();
			if(!status_code.contains("200")){
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :Cl discount API is throwing error with error code ="+ status_code);
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :API response is ="+ clDiscountAPIResponse);
			}else{
				/** converting the response String to jsonObject */
				JSONObject responseJson= new JSONObject(clDiscountAPIResponse);
				
				/** get result as json object */
				JSONObject resultObj = (JSONObject) responseJson.get("result");
				String cl_discount= resultObj.get("cl_discount").toString();
				
				/** get CL discount as jsonObject */
				JSONObject clDiscountObj = new JSONObject(cl_discount);
							
				/** Store all pids from the redis key into a list */
				JSONObject itemsObject = (JSONObject) clDiscountObj.get("items");
				Iterator<?> keys = itemsObject.keys();
				while(keys.hasNext()){
					String key = (String) keys.next();
					pids.add(key);
				}
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : list of all CL pids are saved");
			}	
		}catch(Exception e){
			System.out.println(e.getMessage());
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :Exception occured while getting the API result : "+ e);
		}
		return pids;
	}
	
	/**
	 *Get random pid for contact lens having special price to check
	 * @param pids
	 * @return
	 */
	public String getRandomCLProduct_id(List<String> pids){
		String pid ="";
		try{
			/** getting the random number generated for getting the pids */
			int listSize = pids.size();
			Random random =new Random();
			int index = random.nextInt(listSize);
			/** get random pid */
			pid = pids.get(index);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :random pid is ="+pid);
			
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :Unable to get the product id from the list : "+ e);
		}
		return pid;
	}
	
	/**
	 * This method is to get the special price for the pid supplied in case of contact lenses 
	 * @param product_id
	 * @return
	 */
	public JSONObject getProductSpecialPrice(String product_id){
		String lenskartPrice = "";
		String status_code;
		String productAPIResponse;
		String productUrl ="";
		JSONObject clJsonObject= new JSONObject();
		try{
			
			
			clJsonObject.put("product_id", product_id);
			request_url = "https://api.lenskart.com/juno/services/v1/product/"+product_id;
			
			getRequest = new HttpGet(request_url);
			getRequest.setHeader("X-Api-Client", "desktop");
			
			/** Execute the API */
			httpclient = HttpClients.createDefault();
			response = httpclient.execute(getRequest);
			
			status_code = response.getStatusLine().toString();
			
			responseEntity = response.getEntity();
			productAPIResponse = EntityUtils.toString(responseEntity, "UTF-8");
			try{
				if(new JSONObject(productAPIResponse).get("error").toString().equalsIgnoreCase("Product does not exist")){
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+": Product API is throwing error with error code ="+ status_code);
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+":API response is ="+ productAPIResponse);
				}
			}catch (JSONException e) {
				// TODO: handle exception
			}
			
			if(!status_code.contains("200")){
				
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :Product API is throwing error with error code ="+ status_code);
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :API response is ="+ productAPIResponse);
				
			}else{
				ObjectMapper productAPIMapper = new ObjectMapper();
				Product_Parent productAPI= productAPIMapper.readValue(productAPIResponse, Product_Parent.class);
				
				/**get product url and store in the jsonObject*/
				productUrl = productAPI.getResult().getUrl();
				clJsonObject.put("productUrl", productUrl);
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : product url is ="+productUrl);
				
				List<Map<String, String>> prices = productAPI.getResult().getPrices();
				
				for(int i=0; i<prices.size(); i++){
					String priceType = prices.get(i).get("name");
					if(priceType.equalsIgnoreCase("Lenskart Price")){
						int price = (int) Float.parseFloat(prices.get(i).get("price"));
						lenskartPrice = Integer.valueOf(price).toString();
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :lenskart price is captured "+lenskartPrice);
					}else{
						logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :Unable to get the Lenskart price");
						lenskartPrice = "";
						
					}
				}
				
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :Unable to get the special price for product id  : "+ e);
		}
		try {
			clJsonObject.put("lenskartPrice", lenskartPrice);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :error occured while adding special price in json, "+ e);
		}
		return clJsonObject;
	}

	/**
	 * This method will return json object containing cl pid and its special price from the product API
	 * @return
	 */
	public JSONObject getCLSpecialPriceJson(){
		JSONObject clJsonObj = new JSONObject();
		try{
			ApiDataMapping apiDataMappingObj = new ApiDataMapping();
			/** get all pids for the special prices check */
			List<String> pids= apiDataMappingObj.getCL_DiscountPIds();
			
			/** get the random product id from the list of pids */
			String product_id=apiDataMappingObj.getRandomCLProduct_id(pids);
			
			/** get the special price for the product from the product api */
			clJsonObj = apiDataMappingObj.getProductSpecialPrice(product_id);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :CL Data json is ready");
		}catch(Exception e){
			System.out.println(e.getMessage());
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :Unable to get the special price for product id  : "+ e);
		}
		return clJsonObj;
	}
	
	/**
	 * Get Serialized object for the CLspecial price
	 * @return
	 */
	public CLProductLenskartPrice serializeCLDataUsingJacksonMapper(){
		CLProductLenskartPrice obj = null;
		try{
			JSONObject json= new ApiDataMapping().getCLSpecialPriceJson();
			
			ObjectMapper objectMapper = new ObjectMapper();
			obj = objectMapper.readValue(json.toString(), CLProductLenskartPrice.class);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :CL Data json is serialized");
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :Unable to serialize the clData  : "+ e);
		}
		return obj;
	}
}
