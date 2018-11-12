/**
 * Last Changes Done on Jan 20, 2015 12:41:21 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */

package lenskart.tests;

import java.net.InetAddress;
import java.util.TreeMap;
import org.apache.log4j.Logger; 
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeSuite;

import framework.utilities.EmailTemplate;
import framework.utilities.EmailTemplateData;
import framework.utilities.FileLib;
import framework.utilities.GenericMethodsLib;
import lenskart.tests.TestSuiteClass;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;

public class TestSuiteClass
{
	public static String executionResult;

	public static String resultFileLocation;
	public static TreeMap<String, Integer> totalTC = new TreeMap<String, Integer>();
	public static String AUTOMATION_HOME;

	/** setting up unique execution id to be used in case of threads */
	public static ThreadLocal<Object> UNIQ_EXECUTION_ID = new ThreadLocal<>();

	public static EmailTemplate email =new EmailTemplate();
	public static EmailTemplateData templateData=new EmailTemplateData();

	public static String currentTestEnv;
	public static String liveIpAddress_Lenskart_Com;

	/** Declaring logger */
	Logger logger = Logger.getLogger(TestSuiteClass.class.getName());


	//@Parameters({"logFileLocation", "logFileName", "ReRun"})
	@BeforeSuite
	public void beforeSuite()
	{
		try
		{
						
			/** setting up automation_home */
			AUTOMATION_HOME=System.getProperty("user.dir");

			System.out.println("AUTOMATION_HOME is: "+AUTOMATION_HOME);

			/** 1. Initialize configuration */
			GenericMethodsLib.InitializeConfiguration();

			/** Loading log4j.properties file for logger and creating logs folder in advance */
			PropertyConfigurator.configure(TestSuiteClass.AUTOMATION_HOME.concat("/properties/log4j.properties"));
			FileLib.CreateDirectory(TestSuiteClass.AUTOMATION_HOME.concat("/logs"));
			
			try{
				currentTestEnv = System.getProperty("test_env").trim();

				/** in case not set by maven */
				if(currentTestEnv == null) {
					currentTestEnv = "prod";
				}
			}catch(NullPointerException e){
				currentTestEnv = "prod";
			}

			/** get the live ip address of lenskart.com */
			liveIpAddress_Lenskart_Com = InetAddress.getByName("api.lenskart.com").getHostAddress();
			
			/**********  Checking the Basic set of API's working fine or not ************/
			try{
				if(System.getProperty("api_test_run").toString().equalsIgnoreCase("yes")){
					APIVerification verifyAPI = new APIVerification();
					String result =""; 
					
					if(currentTestEnv.equalsIgnoreCase("prod")){
						result =verifyAPI.apiVerification("api.lenskart.com");
					}else{
						result =verifyAPI.apiVerification("api-"+currentTestEnv.trim()+".lenskart.com");
					}
					
					if(result.contains("Fail")){
						
						logger.error("API verification failed");
						System.out.println("API verification failed please check the logs for more details");
						Assert.fail("API verification failed please check the logs for more details");
						
					}
				}else{
					logger.info("Skipping API test run as the run_mode is set to false");
				}
			}catch(NullPointerException e){
				logger.info("Skipping API test run as the run_mode is not set");
			}
						
			System.out.println("****** Running Test In Test Environment: "+currentTestEnv + " **********");

		}
		catch(Exception e)
		{
			logger.error("Exception handled during execution of beforeTestSuite. ", e); 
		}
	}

	@AfterSuite
	public void afterSuite()  
	{
		try{
 
			GenericMethodsLib.storeOrderData_UsingJacksonMapper(AUTOMATION_HOME+"/dataObject", AUTOMATION_HOME+"/automationOrders");

			/** create property file to store results summary for jenkins email */
			TestSuiteClass.templateData.testDataInPropertiesFile();
			
			GenericMethodsLib.cleanProcesses();			
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}

}
