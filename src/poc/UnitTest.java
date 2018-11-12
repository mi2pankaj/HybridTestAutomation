package poc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import framework.core.classes.TestCaseObject;
import framework.utilities.httpClientWrap;

import java.util.Set;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import lenskart.tests.DataObject;

public class UnitTest {


	@org.testng.annotations.Test
	public void test() throws JsonParseException, JsonMappingException, IOException 
	{		
		//		GenericMethodsLib.storeOrderData_UsingJacksonMapper(TestSuiteClass.AUTOMATION_HOME+"/dataObject", TestSuiteClass.AUTOMATION_HOME+"/automationOrders");

		//		new LenskartWebTest_Utils().serializeMergedDataObjectJson(TestSuiteClass.AUTOMATION_HOME+"/..");
		//		
		ObjectMapper mapper = new ObjectMapper();
		DataObject dataObject = mapper.readValue(new File("/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/dataObject"), DataObject.class);
		System.out.println(dataObject.getEmail_id());

		List<DataObject> dataObjectList = new ArrayList<>();
		dataObjectList.add(dataObject);

		mapper.writeValue(new File("/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/updated"), dataObjectList);

		TypeReference<List<DataObject>> typeReference = new TypeReference<List<DataObject>>() {};
		List<DataObject> dataObjectListFromTestCase = mapper.readValue(new File("/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/dataObject"), typeReference);

		System.out.println(dataObjectListFromTestCase.get(0).getEmail_id());

		//JsonNode json = mapper.readTree(new File(TestSuiteClass.AUTOMATION_HOME+"/dataObject111"));
		//		System.out.println(json.toString());
	}

	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, ParseException {
		// TODO Auto-generated method stub		

		String zx = "3500";
		float ii = Float.parseFloat(zx);
		
//		try {
//			ii = Integer.parseInt(zx);
//		}catch (NumberFormatException e) {
//			
//		}
		
		System.out.println(ii);
		
		//httpClientWrap.sendPostRequest("http://pankaj:aff8393a5b46b614368cdee1b23fe5d5@192.168.10.34:8080/job/Lenskart_Grid_Node_Server/buildWithParameters?token=kVfSb7gSm2eDaaFq&git_branch=master&test_suite_type=Test&host_entry=&test_env=prod&channel_type=mobile");

		System.out.println(new String(org.apache.commons.codec.binary.Base64.encodeBase64("pankaj:password@123".getBytes())));

		System.setProperty("test", "testing");
		System.out.println(System.getProperty("test"));

		String respone = httpClientWrap.sendGetRequest("https://api-preprod.lenskart.com/juno/services/v1/redis?keys=GUEST_MSG,ENABLE_OPTIMIZELY,ENABLE_LOYALTY,UPDATE_TOAST_CONFIG,OFFLINE_ORDER_NUMBER,NEW_CAPTURE_NUMBER,NEW_CAPTURE_NUMBER_IMAGE_URL,NEW_CAPTURE_NUMBER_EXPIRY,HOME_PAGE_ID,LISTING_PAGE_SIZE,NEW_CAPTURE_NUMBER_CATEGORY,NEW_CAPTURE_NUMBER_CATEGORY_SCROLLHEIGHT,FILTER_IMAGES_CDN_PATH,FILTER_ROW_POSITION,ENABLE_INLINE_FILTERS,NEW_CAPTURE_NUMBER_TEXT,NEW_CAPTURE_NUMBER_CLOSE,NEW_CAPTURE_NUMBER_CLOSE_EXPIRY,DISABLE_NEW_CAPTURE_NUMBER_CATEGORY_IOS,LOYALTY_LOGIN_TEXT,timerForWalletResendOTP,LOGIN_EMAIL_INPUT,LOGIN_EMAIL_INPUT_LABEL,NEW_AUTH_CONFIG,TAT_INFO,DISABLE_BUY_ON_APP,DISABLE_BUY_NOW,BUY_ON_APP_INFO,DISABLE_OFFER_TEXT,DO_ANDROID_CHECK,DISABLE_BUY_CALL,cl_discount,lenskartGoldBanner,PDP_CONFIG,MISSCALL_CONFIG,LOYALTY_ALLOW_BRAND,LOYALTY_ALLOW_CLSFN,DISABLE_POWER_MISS_INFO,cod_info_enable,cod_info_message,LOYALTY_PID,HIDE_RECENT_PRODUCTS,RECENT_VIEWED_TITLE,RECENT_VIEWED_COUNT,RECENT_VIEWED_PER_SLIDE,HIDE_RECOMMENDED_PRODUCTS,RECOMMENDED_PRODUCTS_TITLE,RECOMMENDED_PRODUCTS_COUNT,RECOMMENDED_PRODUCTS_PER_SLIDE,GST,GST_TEXT,VC_FULL_RIM_MOBILE,VC_HALF_RIM_MOBILE,VC_RIMLESS_MOBILE,SL_EXCEEDED_MESSAGE,PACKAGES_CONFIG,ENABLE_FRESHWORKS,SHOW_BLUECUT_GIF,BLUECUT_GIF_PATH,LKCashEnabled,SHOW_CMS_HOMEPAGE,BOGO_BANNER_DETAILS,SHOW_HIDE_LENSKART_GOLDBANNER,SHOW_LKCASH_CONTAINER,LK_CONFIG_MESSAGE,SUBSCRIPTION_CONFIG,OOS_CONFIG,BUY_ON_CALL_CONFIG&v=2560395");
		System.out.println(respone);
		JSONParser jp = new JSONParser();

		JSONObject json = (JSONObject) jp.parse(respone); 
		JSONObject result = (JSONObject)json.get("result");

		System.out.println();
		System.out.println("----> "+result.get("PDP_CONFIG"));;

		JSONObject pdp = (JSONObject) jp.parse(String.valueOf(result.get("PDP_CONFIG")));

		System.out.println();
		System.out.println("tah --> "+pdp.get("showTAH"));


		String x = new SimpleDateFormat("dd-MM-yy:HH:mm:ss").format(new Date().getTime());
		//System.out.println(x);

		//sample expression1 - when #TC_10_01#	       contains 				newsite then dothis with
		//sample expression2 - when verifyelement then gettext with 
		//		String xyz = "#TC_10_01#	       contains 				newsite     ";
		//		xyz = "    newsite	       contains 				newsite     ";
		//		xyz(xyz);


		//		String ip =  InetAddress.getByName("api.lenskart.com").getHostAddress();
		//		System.out.println(ip);
		//
		//		HashMap<String, String> map = new HashMap<>();
		//		map.put("q", "v");
		//
		//		String k = map.keySet().iterator().next();
		//		String v = map.get(k);
		//
		//		System.out.println(k + "  --  " +v);
		//
		//		Properties p = new Properties();
		//		p.setProperty("kk", "vv");
		//		p.setProperty("kk1", "vv2");
		//
		//		OutputStream os = new FileOutputStream(new File("//Users/pankaj.katiyar/Desktop/abc.pro"));
		//		p.store(os, "rishi");


	}

	public static void xyz(String xyz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		//String superset;
		//String subset;
		//String condition;
		//String superset=new String();
		//String xyz = "     #TC_10_01#	       contains 				newsite     ";
		String [] xx = xyz.split(" ");

		TreeMap<String, Integer> map = new TreeMap<>();
		Set<String> set = new HashSet<>();

		List<String> list = new ArrayList<>();

		for(int i=0; i< xx.length; i++)
		{
			String abc = xx[i];
			map.put(abc.trim(), i);

			set.add(abc.trim());
			list.add(abc.trim());
		}

		System.out.println();
		String rishi ="";

		for(Entry<String, Integer> en:  map.entrySet())
		{
			rishi = rishi + " " + en.getKey();
		}

		System.out.println("Stringt iss =" +rishi.trim());

		System.out.println();

		String rishi1 ="";
		Iterator<String> x = set.iterator();
		while(x.hasNext())
		{
			rishi1 = rishi1 + " " + x.next();
		}

		System.out.println("rishi1: " +rishi1);


		System.out.println();
		String rishi2 ="";

		for(int i=0; i<list.size(); i++) {
			if(!list.get(i).trim().isEmpty()) {
				rishi2 = (rishi2 + " " + list.get(i)).trim() ;
			}
		}
		System.out.println("rishi2-"+rishi2);

		//		String[] stringArr=rishi.trim().split(" ");
		//		superset=stringArr[0];
		//		condition=stringArr[1];
		//		subset=stringArr[2];
		//
		//		Method method=superset.getClass().getMethod(condition, String.class);
		//
		//		/** check condition */
		//		Object obj=method.invoke(stringArr, subset);
		//		System.out.println(obj.toString());
	}


	public static void test(int u)
	{
		int x = 3;

		int y = x + u; 

		if(y > 1)
		{
			System.out.println("calling ");
			test(y);
		}
	}		

}
