package core.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import tests.SuiteClass;

public class EmailTemplateData {

	Logger logger = Logger.getLogger(EmailTemplateData.class.getName());


	public void storeEmailTemplate( Object[][] resultData, String channelType, String executionTime){

		try{
			int pass=getPassOrFailCount(resultData)[0];
			int fail=getPassOrFailCount(resultData)[1];

			if(channelType.equalsIgnoreCase("mobile")){

				SuiteClass.email.setMobileTestPassed(Integer.toString(pass));
				SuiteClass.email.setMobileTestFailed(Integer.toString(fail));
				SuiteClass.email.setMobileTestTotalCases(Integer.toString(pass+fail));
				SuiteClass.email.setMobileExecutionTime(executionTime+"(mins)");
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+ " - mobile suite data is stored");
			}

			else if(channelType.equalsIgnoreCase("desktop")){

				SuiteClass.email.setDesktopTestPassed(Integer.toString(pass));
				SuiteClass.email.setDesktopTestFailed(Integer.toString(fail));
				SuiteClass.email.setDesktopTestTotalCases(Integer.toString(pass+fail));
				SuiteClass.email.setDesktopExecutionTime(executionTime+"(mins)");
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+ " - Desktop suite data is stored");
			}

			else if(channelType.equalsIgnoreCase("vsm")){

				SuiteClass.email.setVsmTestPassed(Integer.toString(pass));
				SuiteClass.email.setVsmTestFailed(Integer.toString(fail));
				SuiteClass.email.setVsmTestTotalCases(Integer.toString(pass+fail));
				SuiteClass.email.setVsmExecutionTime(executionTime+"(mins)");
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+ " - vsm suite data is stored");
			}

			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+ " - email template data is stored in object for channel: "+channelType);
		}catch(Exception e){
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+ " - Unable to store the email template data", e);
		}

	}

	/** Get the Pass/Fail count for the test cases executed
	 * 
	 * @param request
	 * @return
	 */

	public int[] getPassOrFailCount(Object [][] resultData){
		int count[]= new int[2];
		int passCount=0;
		int failCount=0;
		try{
			for(int i=0; i<resultData.length;i++){
				String result=resultData[i][1].toString();
				if(result.equalsIgnoreCase("pass")){
					passCount++;
				}else{
					failCount++;
				}
			}
			count[0]=passCount;
			count[1]=failCount;

		}catch(Exception e){
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+ " -- " +e.getMessage(), e);
		}
		return count;
	}

	/**
	 * This method is calculating the owner wise test case execution result and storing in the email template object as hashmap
	 * @param result
	 * @param channelType
	 */
	public void SetOwnerWiseResultData(Object[][] result, String channelType){
		int ownerFailCount;
		int totalValue; 
		try{
			/**Map to store the total test cases count owner wise */
			Map<String, Integer> ownerWiseTotalMap=new HashMap<String, Integer>();

			/**Map to store the Failed test cases count owner wise */
			Map<String, Integer> ownerFailedCasesMap=new HashMap<String, Integer>();

			/** Storing total test cases per owner */
			for(int i=0; i<result.length; i++){
				if(ownerWiseTotalMap.containsKey(result[i][2].toString())){
					totalValue = ownerWiseTotalMap.get(result[i][2].toString());
					totalValue ++; 
					ownerWiseTotalMap.put(result[i][2].toString(), totalValue);
				}else{
					ownerWiseTotalMap.put(result[i][2].toString(), 1);
				}
				/** Storing total test cases per owner */
				if(result[i][1].toString().equalsIgnoreCase("fail")){
					if(ownerFailedCasesMap.containsKey(result[i][2].toString())){
						ownerFailCount = ownerFailedCasesMap.get(result[i][2].toString());
						ownerFailCount ++; 
						ownerFailedCasesMap.put(result[i][2].toString(), ownerFailCount);
					}else{
						ownerFailedCasesMap.put(result[i][2].toString(), 1);
					}
				}
			}
			Map<String, String> finalMap = new HashMap<String, String>();
			for(Entry<String, Integer> map1 : ownerWiseTotalMap.entrySet()){
				if(ownerFailedCasesMap.containsKey(map1.getKey())){
					String key = map1.getKey();
					String failedCases = ownerFailedCasesMap.get(key).toString();
					String totalCases = ownerWiseTotalMap.get(key).toString();

					finalMap.put(channelType+"_"+key, failedCases+"/"+totalCases);	
				}else{
					String key = map1.getKey();
					String failedCases = "0";
					String totalCases = ownerWiseTotalMap.get(key).toString();
					finalMap.put(channelType+"_"+key, failedCases+"/"+totalCases);
				}
				if(channelType.equalsIgnoreCase("mobile")){
					SuiteClass.email.setMobile_ownerWiseTestResult(finalMap);
				}else if(channelType.equalsIgnoreCase("desktop")){
					SuiteClass.email.setDesktop_ownerWiseTestResult(finalMap);
				}
			}
		}catch(Exception e){
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+ "error occured while creating the owner's properties file" +e.getMessage(), e);
		}
	}

	/**
	 * This method will write owner wise test result in properties file
	 */
	public void ownerTestResultDataInPropertyFile(){
		String filePath = SuiteClass.AUTOMATION_HOME+"/OwnerWiseTestResult.properties";
		try{
			Properties property=new Properties();

			Map<String, String> mobileResultMap=SuiteClass.email.getMobile_ownerWiseTestResult();

			Map<String, String> desktopResultMap=SuiteClass.email.getDesktop_ownerWiseTestResult();

			for(Entry<String, String> map : desktopResultMap.entrySet()){
				String key = map.getKey();
				String value = desktopResultMap.get(key);
				property.setProperty(key, value);
			}

			for(Entry<String, String> map : mobileResultMap.entrySet()){
				String key = map.getKey();
				String value = mobileResultMap.get(key);
				property.setProperty(key, value);
			}

			OutputStream os = new FileOutputStream(new File(filePath));
			property.store(os, "testResult");
			os.close();
			logger.info("Writing Owner_wise summary property file at: "+filePath);

		}catch(Exception e){
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+ "error occured while creating the Owner wise test result properties file" +e.getMessage(), e);
		}
	}

	/**
	 * This method will write test result in properties file
	 */
	public void testDataInPropertiesFile(){
		try{

			String filePath = SuiteClass.AUTOMATION_HOME+"/testResult.properties";

			/** creating the test result properties file*/
			Properties property=new Properties();

			property.setProperty("Total_TestCases_Desktop", SuiteClass.email.getDesktopTestTotalCases());
			property.setProperty("TestCases_Passed_Desktop", SuiteClass.email.getDesktopTestPassed());
			property.setProperty("TestCases_Failed_Desktop", SuiteClass.email.getDesktopTestFailed());
			property.setProperty("Execution_Time_Desktop", SuiteClass.email.getDesktopExecutionTime());

			property.setProperty("Total_TestCases_mobile", SuiteClass.email.getMobileTestTotalCases());
			property.setProperty("TestCases_Passed_mobile", SuiteClass.email.getMobileTestPassed());
			property.setProperty("TestCases_Failed_mobile", SuiteClass.email.getMobileTestFailed());
			property.setProperty("Execution_Time_mobile", SuiteClass.email.getMobileExecutionTime());

			property.setProperty("Total_TestCases_Vsm", SuiteClass.email.getVsmTestTotalCases());
			property.setProperty("TestCases_Passed_Vsm", SuiteClass.email.getVsmTestPassed());
			property.setProperty("TestCases_Failed_Vsm", SuiteClass.email.getVsmTestFailed());
			property.setProperty("Execution_Time_Vsm", SuiteClass.email.getVsmExecutionTime());

			/** setting up owner wise contribution */
			Map<String, String> desktopResultMap=SuiteClass.email.getDesktop_ownerWiseTestResult();

			for(Entry<String, String> map : desktopResultMap.entrySet()){
				String key = map.getKey();
				String value = desktopResultMap.get(key);
				property.setProperty(key, value);
			}

			Map<String, String> mobileResultMap=SuiteClass.email.getMobile_ownerWiseTestResult();

			for(Entry<String, String> map : mobileResultMap.entrySet()){
				String key = map.getKey();
				String value = mobileResultMap.get(key);
				property.setProperty(key, value);
			}

			Map<String, String> vsmResultMap=SuiteClass.email.getVsm_ownerWiseTestResult();

			for(Entry<String, String> map : vsmResultMap.entrySet()){
				String key = map.getKey();
				String value = mobileResultMap.get(key);
				property.setProperty(key, value);
			}

			OutputStream os = new FileOutputStream(new File(filePath));
			property.store(os, "testResult");
			os.close();

			logger.info("Writing test summary property file at: "+filePath);

		}catch(Exception e){
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+ "error occured while creating the test result properties file" +e.getMessage(), e);
		}

	}


}
