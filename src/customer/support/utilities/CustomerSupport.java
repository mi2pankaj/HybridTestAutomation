package customer.support.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class CustomerSupport {


	public static void main(String[] args){

		//walletLKPlusBulkCredit_BulkDebit("8503088989", "20", "Test_Order_For_Creating_Jenkins_Job", "credit", "lenskartplus");
		addOrRemoveLoyalty("1209802590", "removeLoyalty");
	}


	public static String encodeEmailId (String email_id){
		String encodedEmailId="";
		try{
			Base64.Encoder encoder=Base64.getEncoder();
			byte[] encodeByteArray=encoder.encode(email_id.getBytes());

			for(int i=0; i<encodeByteArray.length; i++){
				char encodedChar =(char) encodeByteArray[i];
				encodedEmailId +=encodedChar;
			}
		}catch(Exception e){
			System.out.println("Unable to encode the customer email provided due to the exception, "+e);
		}
		return encodedEmailId;
	}

	// delete customer account
	public static void deleteCustomerAccount (String encodedEmailId){

		ArrayList<NameValuePair> postParameters;
		HttpClient httpclient= HttpClientBuilder.create().build();
		HttpEntity responseEntity=null;
		String responseResult=null;
		try{

			String requestUrl ="https://www.lenskart.com/me/index/removeAccount";
			HttpPost httpPost =new HttpPost(requestUrl);

			postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("emailtoken", "manualremove"));
			postParameters.add(new BasicNameValuePair("email", encodedEmailId));

			httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
			HttpResponse response = httpclient.execute(httpPost);

			responseEntity=response.getEntity();
			responseResult=EntityUtils.toString(responseEntity,"UTF-8");

			System.out.println();
			System.out.println("*********************************************");
			System.out.println();
			System.out.println("post request result is = "+ responseResult);
			System.out.println();
			System.out.println("*********************************************");
			System.out.println();

		}catch(Exception e){
			System.out.println("error occur while deactivating the customer account, "+e);
			e.printStackTrace();
		}

	}

	// get session token
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
			e.printStackTrace();
		}

		return sessionToken;
	}

	// get wallet session token
	public static String getWalletAuthToken (){

		String walletAuthToken = "";

		try{

			//get session token
			String sessionToken = getSessionToken();
			if(sessionToken.isEmpty()) {
				sessionToken = getSessionToken();
			}

			HttpClient httpclient= HttpClientBuilder.create().build();
			HttpEntity responseEntity=null;
			String responseResult=null;

			String requestUrl ="https://api.lenskart.com/v2/money/wallet/generate-token";
			HttpPost httpPost =new HttpPost(requestUrl);

			//add headers
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("X-Session-Token", sessionToken);
			httpPost.setHeader("X-Api-Client", "jenkins");
			httpPost.setHeader("X-Refresh-Token", "8e8b0816-8f7d-4f08-4c73-022dcd186a91");

			HttpResponse response = httpclient.execute(httpPost);

			responseEntity=response.getEntity();
			responseResult=EntityUtils.toString(responseEntity,"UTF-8");

			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(responseResult);

			walletAuthToken = ((JSONObject)obj.get("result")).get("token").toString();

			//System.out.println("session token is = "+ walletSessionToken);

		}catch(Exception e){
			System.out.println("error occurred while getting session token, "+e);
			e.printStackTrace();
		}

		return walletAuthToken;
	}

	// merge mobile account
	public static void mergeMobileEmailAccount (String emailId, String mobileNumber){

		StringBuffer response = new StringBuffer();

		try{
			//get session token
			String sessionToken = getSessionToken();
			if(sessionToken.isEmpty()) {
				sessionToken = getSessionToken();
			}

			String requestUrl ="https://api.lenskart.com/v2/customers/mergeAccounts?mobileNumber="+mobileNumber+"&email="+emailId;
			URL obj = new URL(requestUrl);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			//optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("X-Session-Token", sessionToken);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Api-Client", "JENKINS");
			con.setRequestProperty("X-Auth-Token", "8e8b0816-4c73-4f08-8f7d-022dcd186a91");

			String inputLine;
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			System.out.println();
			System.out.println("*********************************************");
			System.out.println();
			System.out.println("*********** " +response);
			System.out.println();
			System.out.println("*********************************************");
			System.out.println();

		}catch(Exception e){
			System.out.println("Error Occurred while merging the accounts. "+e);
			e.printStackTrace();
		}
	}

	//wallet bulk credit
	@SuppressWarnings("unchecked")
	public static void walletBulkCredit_BulkDebit (String mobileNumber, String amount, String narration, String flagBulkCredit_Or_BulkDebit){
		try{
			//get session token
			String sessionToken = getSessionToken();
			if(sessionToken.isEmpty()) {
				sessionToken = getSessionToken();
			}

			//get session token
			String walletAuthToken = getWalletAuthToken();
			if(walletAuthToken.isEmpty()) {
				walletAuthToken = getWalletAuthToken();
			}

			boolean flag= false;

			// get bulk debit / bulk credit url
			String requestUrl ="";
			if(flagBulkCredit_Or_BulkDebit.equalsIgnoreCase("credit"))
			{
				flag = true;
				requestUrl ="https://api.lenskart.com/v2/money/wallet/bulk-credit";
			}
			else if(flagBulkCredit_Or_BulkDebit.equalsIgnoreCase("debit"))
			{
				flag = true;
				requestUrl ="https://api.lenskart.com/v2/money/wallet/bulk-debit";
			}

			if(flag)
			{
				HttpPost httpPost =new HttpPost(requestUrl);

				//add headers
				httpPost.setHeader("Content-Type", "application/json");
				httpPost.setHeader("X-Session-Token", sessionToken);
				httpPost.setHeader("X-Api-Client", "jenkins");
				httpPost.setHeader("X-Api-Auth-Token", walletAuthToken);

				//set body			
				JSONArray jsonArray = new JSONArray();
				JSONObject object = new JSONObject();
				object.put("amount", amount);
				object.put("merchantRefId", mobileNumber+"_"+new Date().getTime());
				object.put("mobileNumber", mobileNumber);
				object.put("narration", narration);
				object.put("walletType", "lenskart");
				jsonArray.add(object);

				// add body
				httpPost.setEntity(new StringEntity(jsonArray.toJSONString()));

				HttpClient httpclient= HttpClientBuilder.create().build();
				HttpResponse response = httpclient.execute(httpPost);

				HttpEntity responseEntity=response.getEntity();
				String responseResult=EntityUtils.toString(responseEntity,"UTF-8");

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseResult);
				Object responseMessage =  (Object)((JSONObject)obj.get("result")).get("message");
				Object responseCode =  (Object)((JSONObject)obj.get("result")).get("code");

				System.out.println();
				System.out.println("*********************************************");
				System.out.println();
				System.out.println("Action: "+flagBulkCredit_Or_BulkDebit+ ", Amount: "+amount + ", Mobile Number: " +mobileNumber +", Status: " +responseMessage +  ", Response Code: "+responseCode);
				System.out.println();
				System.out.println("*********************************************");
				System.out.println();
			}
			else
			{
				System.out.println();
				System.out.println("*********************************************");
				System.out.println();
				System.out.println("***** wrong input - "+flagBulkCredit_Or_BulkDebit + " supply valid value either of debit or credit");
				System.out.println();
				System.out.println("*********************************************");
				System.out.println();
			}

		}catch(Exception e){
			System.out.println("error occurred while bulk wallet deposit "+e);
			e.printStackTrace();
		}
	}


	//wallet bulk credit
	@SuppressWarnings("unchecked")
	public static void walletLKPlusBulkCredit_BulkDebit (String mobileNumber, String amount, String narration, String flagBulkCredit_Or_BulkDebit, String wallet_type, String order_id){
		try{
			//get session token
			String sessionToken = getSessionToken();
			if(sessionToken.isEmpty()) {
				sessionToken = getSessionToken();
			}

			//get session token
			String walletAuthToken = getWalletAuthToken();
			if(walletAuthToken.isEmpty()) {
				walletAuthToken = getWalletAuthToken();
			}

			boolean flag= false;

			// get bulk debit / bulk credit url
			String requestUrl ="";
			if(flagBulkCredit_Or_BulkDebit.equalsIgnoreCase("credit"))
			{
				flag = true;
				requestUrl ="https://api.lenskart.com/v2/money/wallet/bulk-credit";
			}
			else if(flagBulkCredit_Or_BulkDebit.equalsIgnoreCase("debit"))
			{
				flag = true;
				requestUrl ="https://api.lenskart.com/v2/money/wallet/bulk-debit";
			}

			if(flag)
			{
				HttpPost httpPost =new HttpPost(requestUrl);

				//add headers
				httpPost.setHeader("Content-Type", "application/json");
				httpPost.setHeader("X-Session-Token", sessionToken);
				httpPost.setHeader("X-Api-Client", "jenkins");
				httpPost.setHeader("X-Api-Auth-Token", walletAuthToken);

				//set body			
				JSONArray jsonArray = new JSONArray();
				JSONObject object = new JSONObject();
				object.put("amount", amount);
				object.put("mobileNumber", mobileNumber);
				object.put("walletType", wallet_type);

				//change fields for lenskartplus 
				if(wallet_type.trim().equalsIgnoreCase("lenskartplus")) {
					narration = "Delayed Order Refund "+order_id;
					object.put("merchantRefId", "Delayed_Order_Refund"+"_"+mobileNumber+"_"+new Date().getTime());
				}else {
					object.put("merchantRefId", mobileNumber+"_"+new Date().getTime());	
				}

				object.put("narration", narration);

				jsonArray.add(object);

				// add body
				httpPost.setEntity(new StringEntity(jsonArray.toJSONString()));

				HttpClient httpclient= HttpClientBuilder.create().build();
				HttpResponse response = httpclient.execute(httpPost);

				HttpEntity responseEntity=response.getEntity();
				String responseResult=EntityUtils.toString(responseEntity,"UTF-8");

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseResult);
				Object responseMessage =  (Object)((JSONObject)obj.get("result")).get("message");
				Object responseCode =  (Object)((JSONObject)obj.get("result")).get("code");

				System.out.println();
				System.out.println("*********************************************");
				System.out.println();
				System.out.println("Action: "+flagBulkCredit_Or_BulkDebit+ ", Amount: "+amount + ", Mobile Number: " +mobileNumber +", Status: " +responseMessage +  ", Response Code: "+responseCode);
				System.out.println();
				System.out.println("*********************************************");
				System.out.println();
			}
			else
			{
				System.out.println();
				System.out.println("*********************************************");
				System.out.println();
				System.out.println("***** wrong input - "+flagBulkCredit_Or_BulkDebit + " supply valid value either of debit or credit");
				System.out.println();
				System.out.println("*********************************************");
				System.out.println();
			}

		}catch(Exception e){
			System.out.println("error occurred while bulk wallet deposit "+e);
			e.printStackTrace();
		}
	}

	/**
	 * Method to add and remove loyalty
	 * @param order_ids
	 * @param addOrRemoveLoyalty
	 */
	@SuppressWarnings("unchecked")
	public static void addOrRemoveLoyalty(String order_ids, String addOrRemoveLoyalty){		
		try{
			/** create a sessionToken */
			String xSessionToken = getSessionToken();

			/** get list of order id on which action is to be performed */
			List<Long> listOforderIds = getOrderIdList(order_ids);

			boolean flag= false;

			String requestUrl ="";
			if(addOrRemoveLoyalty.equalsIgnoreCase("removeLoyalty"))
			{
				flag = true;
				requestUrl ="https://api.lenskart.com/v2/orders/removeloyalty";
			}
			else if(addOrRemoveLoyalty.equalsIgnoreCase("addLoyalty"))
			{
				flag = true;
				requestUrl ="https://api.lenskart.com/v2/orders/addloyalty";
			}

			if(flag){
				HttpPost httpPost =new HttpPost(requestUrl);

				httpPost.setHeader("Content-Type", "application/json");
				httpPost.setHeader("X-Session-Token", xSessionToken);

				JSONObject requestJSON= new JSONObject();

				requestJSON.put("orderIdsList", listOforderIds);

				httpPost.setEntity(new StringEntity(requestJSON.toJSONString()));

				HttpClient httpclient= HttpClientBuilder.create().build();
				HttpResponse response = httpclient.execute(httpPost);

				HttpEntity responseEntity=response.getEntity();
				String responseResult=EntityUtils.toString(responseEntity,"UTF-8");

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseResult);
				Object responseMessage =  obj.get("result");
				Object responseCode =  obj.get("status");

				System.out.println();
				System.out.println("*********************************************");
				System.out.println();
				System.out.println("Action: "+addOrRemoveLoyalty+ ", orderIds: "+listOforderIds.toString() + ", Status: " +responseMessage +  ", Response Code: "+responseCode);
				System.out.println();
				System.out.println("*********************************************");
				System.out.println();
			}
			else{
				System.out.println();
				System.out.println("*********************************************");
				System.out.println();
				System.out.println("***** wrong input - "+addOrRemoveLoyalty + " supply valid value either of addloyalty or removeLoyalty");
				System.out.println();
				System.out.println("*********************************************");
				System.out.println();
			}
		}catch(Exception e){
			System.out.println("error occurred while" +addOrRemoveLoyalty +" for the order_ids "+order_ids+" exception is "+e);
			e.printStackTrace();
		}

	}

	/**
	 * Get list or orderIds
	 * @param order_ids
	 * @return
	 */
	public static List<Long> getOrderIdList(String order_ids){
		List<Long> listOfOrderIds = new ArrayList<>(); ;
		try{
			/**
			 * If we have multiple orderids separated by ,(comma) or whitespace then add all order id in a list of long
			 */
			if(order_ids.contains(",")|| order_ids.contains(" ")){
				String[] stringArray =order_ids.split("\\s|,");
				for(int i=0; i<stringArray.length; i++){
					listOfOrderIds.add(Long.parseLong(stringArray[i].trim()));
				}
			}else{
				/** Adding the input string into the list as no separator is specified */
				listOfOrderIds.add(Long.parseLong(order_ids));
			}
		}catch(Exception e){
			System.out.println("error occurred while getting the listfor the order_ids "+order_ids+" exception is "+e);
			e.printStackTrace();
		}
		return listOfOrderIds;
	}

}
