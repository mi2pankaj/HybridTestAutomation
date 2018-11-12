/**
 * Last Changes Done on Jan 20, 2015 12:41:21 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */

package tests;

import java.net.InetAddress;
import java.util.TreeMap;
import org.apache.log4j.Logger; 
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeSuite;

import core.utilities.EmailTemplate;
import core.utilities.EmailTemplateData;
import core.utilities.FileLib;
import core.utilities.GenericMethodsLib;
import tests.SuiteClass;

import org.testng.annotations.AfterSuite;

public class SuiteClass
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
	Logger logger = Logger.getLogger(SuiteClass.class.getName());


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
			PropertyConfigurator.configure(SuiteClass.AUTOMATION_HOME.concat("/properties/log4j.properties"));
			FileLib.CreateDirectory(SuiteClass.AUTOMATION_HOME.concat("/logs"));
			
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
			/** create property file to store results summary for jenkins email */
			SuiteClass.templateData.testDataInPropertiesFile();
			
			GenericMethodsLib.cleanProcesses();			
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}

}
