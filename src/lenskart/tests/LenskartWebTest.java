/**
 * Last Changes Done on Jan 16, 2015 12:04:40 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */

package lenskart.tests;

import java.awt.Robot;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONObject;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.testng.annotations.Test;


import com.mongodb.DB;
import com.mongodb.client.FindIterable;

import core.classes.InitializeSingletonTestObject;
import core.classes.ReadTestCases;
import core.classes.SingletonTestObject;
import core.classes.TestCaseObject;
import core.classes.TestObjectHandler;
import core.classes.WriteTestResults;
import core.utilities.CaptureScreenShotLib;
import core.utilities.FileLib;
import core.utilities.GenericMethodsLib;
import core.utilities.KeyBoardActionsUsingRobotLib;
import core.utilities.MongoDBHandler;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;


public class LenskartWebTest {

	Logger logger = Logger.getLogger(LenskartWebTest.class.getName());

	String channel_type;
	String is_parallel_execution;	

	/** store result in two D array to get testNg results for jenkins */
	static Object [][] resultData;
	Map<String,String> templateMap = new HashMap<String,String>(); 

	/** store start, endtime and execution time for the test run */
	Instant startTime;
	Instant endTime;
	Duration executionDuration;
	String executionTime;	

	/** setting up configuration before test */
	@SuppressWarnings("unused")
	@Parameters({"channel_type", "is_parallel_execution"})
	@BeforeClass
	public void beforeClass(String channel_type, String is_parallel_execution) 
	{
		try
		{		
			logger.info("***************** Test Started: " +channel_type + " ************************ ");

			GenericMethodsLib.InitializeConfiguration();

			startTime=Instant.now();

			/** this is for jenkins */
			System.out.println("***************** Test Started: " +channel_type + " ************************ ");

			/** lenskart specific initialization - if this is received empty then get it from system property --> 
			 * this case will happen while using GRID.
			 * also - in case of mongo scale execution ==> channel_type need to set as system propoerty 
			 * so that this can be passed to jenkins and then to node server. */
			if(channel_type.isEmpty()) {
				channel_type = System.getProperty("channel_type").trim();
			}

			System.setProperty("channel_type", channel_type);
			this.channel_type = channel_type;
			this.is_parallel_execution = is_parallel_execution;

			SingletonTestObject.getSingletonTestObject().setChannel_type(channel_type);

			System.out.println("received awt property: "+System.getProperty("java.awt.headless"));

			/** Initializing constructor of KeyBoardActionsUsingRobotLib and CaptureScreenShotLib here, 
			 * so that focus on chrome browser is not disturbed. 
			 */
			Robot rt = new Robot();
			KeyBoardActionsUsingRobotLib keyBoard = new KeyBoardActionsUsingRobotLib(rt);
			CaptureScreenShotLib captureScreenshot = new CaptureScreenShotLib(rt);

			System.out.println("#### robot class initialized #### ");

			System.out.println("robot execute");
			/** collect all dataObject json files located in at AUTOMATION_HOME/.. - this is for jenkins - basically all the other testsuites
			 * will copy the dataObject file at AUTOMATION_HOME/.. of VSM_Test_Suite therefore need collect all and merge in one json 
			 * to make it usable by VSM Execution test */
			new LenskartWebTest_Utils().serializeMergedDataObjectJson(TestSuiteClass.AUTOMATION_HOME+"/..");

			List<TestCaseObject> testCaseObjectList = new ArrayList<>();
			if(GenericMethodsLib.generalConfigurationProperties.getProperty("data_from_google").toString().equalsIgnoreCase("yes")){

				/** load test case objects in list for sequential execution with data driven test object list */
				testCaseObjectList = new LenskartWebTest_Utils().getRunnableTestCaseObjects_ChannelSpecific_FromGoogle(channel_type);

			}else{

				/** load test case objects in list for sequential execution with data driven test object list */
				testCaseObjectList = new LenskartWebTest_Utils().getRunnableTestCaseObjects_ChannelSpecific(channel_type); 
			}

			/** update the testCaseObjectList as per test type received from testng.xml*/
			testCaseObjectList = new TestObjectHandler().getTestCaseObjects_SuiteSpecific(testCaseObjectList);

			/**  update the singleton TestObject map, load test case objects in map for parallel execution */
			ConcurrentHashMap<String, TestCaseObject> testCaseObjectMap = new TestObjectHandler().getTestCaseObjectMap(testCaseObjectList);

			/** getting object repository from google sheet ot local sheet based on configuration */
			JSONObject jsonObjectRepo = new JSONObject();
			if(GenericMethodsLib.generalConfigurationProperties.getProperty("data_from_google").toString().equalsIgnoreCase("yes")){

				/** get object repository as json object from google sheet */
				jsonObjectRepo = new LenskartWebTest_Utils().getObjectRepoAsJson_TestSpecificFromGoogle(channel_type);

			}else{

				/** get object repository as json object from local excel sheet */
				jsonObjectRepo = new LenskartWebTest_Utils().getObjectRepoAsJson_TestSpecific(channel_type);
			}


			/** initialize an array which has only results -- this will be iterated to check pass and fail results and Jenkins will be informed */
			resultData = new Object[testCaseObjectList.size()][3];

			/** initialize singleton class with all params */
			new InitializeSingletonTestObject(testCaseObjectList, testCaseObjectMap, jsonObjectRepo);		
		}
		catch (Exception e)
		{
			logger.error("Error occurred before starting the suite: "+channel_type, e);
		}
	}


	/** running tests */
	@Test(priority=1)
	public void runTests()
	{
		/** Use this code for sequential execution -- */
		if(is_parallel_execution.trim().equalsIgnoreCase("no")) {
			logger.info("--- starting sequential execution --- ");

			ReadTestCases readTestCases = new ReadTestCases();

			/** iterate the test case object list and execute test case and write results - getting testobject list from singleton test object */
			List<TestCaseObject> testCaseObjectList = SingletonTestObject.getSingletonTestObject().getTestCaseObjectList();

			for(int i=0; i<testCaseObjectList.size(); i++){

				TestCaseObject testCaseObject = testCaseObjectList.get(i);
				readTestCases.executeTestCaseObject(testCaseObject, SingletonTestObject.getSingletonTestObject().getMysqlDbConnection());

				/** get the result in a 2d array to use as data provider later on -- to get testng results */
				resultData[i][0] = channel_type + " : " + testCaseObject.getTestCaseId()+" : "+testCaseObject.getTestCaseDescription();
				resultData[i][1] = testCaseObject.getTestCaseResult();
				resultData[i][2] = testCaseObject.getOwnerName();
			}
		}

		/** Use this code for parallel execution -- */
		else {
			logger.info("--- starting parallel execution --- ");

			/** wait until all tasks from test case object map are not completed --> progress status = 1 and picked up status flag = true; */
			while(!new TestObjectHandler().ifAllTestCaseObjectsAreExecuted()) {
				try {
					/** assign tasks for parallel execution */	
					new TestObjectHandler().assignTasks_ForParallelExecution();

					Thread.sleep(2000);
				} catch (InterruptedException e) {}
			}

			/** collect results for Mr. Jenkins */
			collectResultsMongoJack();
		}
	}


	/**
	 *  This method collects the results either from hashmap or mongo depending on confguration in an array - basically this array is iterated again and Asserts are fired 
	 * so that testNg-results.xml is created and Jenkins comes to know pass and failures.  
	 */
	public void collectResults()
	{
		/** use mongo to get results - only in case of hub */
		if(SingletonTestObject.getSingletonTestObject().isScale_execution_via_mongo() && SingletonTestObject.getSingletonTestObject().isHubMachine()) {

			FindIterable<Document> documents = new MongoDBHandler().getMongoDbDocument
					(SingletonTestObject.getSingletonTestObject().getMongoDBClientConnnection(), "");

			int i=0;
			for(Document doc : documents)
			{
				resultData[i][0] = channel_type + " : " + doc.getString("testCaseId")+" : "+doc.getString("testCaseDescription");
				resultData[i][1] = doc.getString("testCaseResult");
				resultData[i][2] = doc.getString("ownerName");

				i++;
			}
		}

		/** use maps */
		else{

			int i=0;
			for(Entry<String, TestCaseObject> entry : SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().entrySet()) {

				TestCaseObject testCaseObject = entry.getValue();
				resultData[i][0] = channel_type + " : " + testCaseObject.getTestCaseId()+" : "+testCaseObject.getTestCaseDescription();
				resultData[i][1] = testCaseObject.getTestCaseResult();
				resultData[i][2] = testCaseObject.getOwnerName();

				i++;
			}
		}
	}


	/**
	 *  This method collects the results either from hashmap or mongo depending on confguration in an array - basically this array is iterated again and Asserts are fired 
	 * so that testNg-results.xml is created and Jenkins comes to know pass and failures.  
	 */
	public void collectResultsMongoJack()
	{
		/** use mongo to get results - only in case of hub */
		if(SingletonTestObject.getSingletonTestObject().isScale_execution_via_mongo() && SingletonTestObject.getSingletonTestObject().isHubMachine()) {
			DB db = SingletonTestObject.getSingletonTestObject().getMongoDBClientConnnection().getDB("Automation");

			/** Mapping from testcasesObject to mongo document */
			JacksonDBCollection<TestCaseObject, String> collection = JacksonDBCollection.wrap(db.getCollection("testcaseobjects"), TestCaseObject.class,
					String.class);

			/**Get the Query result from the mongo */
			DBCursor<TestCaseObject> listofTestCases= collection.find();
			int i=0;
			for(TestCaseObject testCaseObject : listofTestCases)
			{
				resultData[i][0] = channel_type + " : " + testCaseObject.getTestCaseId()+" : "+testCaseObject.getTestCaseDescription();
				resultData[i][1] = testCaseObject.getTestCaseResult();
				resultData[i][2] = testCaseObject.getOwnerName();

				i++;
			}
		}

		/** use maps */
		else{

			int i=0;
			for(Entry<String, TestCaseObject> entry : SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().entrySet()) {

				TestCaseObject testCaseObject = entry.getValue();
				resultData[i][0] = channel_type + " : " + testCaseObject.getTestCaseId()+" : "+testCaseObject.getTestCaseDescription();
				resultData[i][1] = testCaseObject.getTestCaseResult();
				resultData[i][2] = testCaseObject.getOwnerName();

				i++;
			}
		}
	}

	@Test(priority=2, dataProvider="getResults")
	public void testResults(Object testCaseDescription, Object testCaseResult, Object testCaseOwnerName)
	{
		if(testCaseResult.toString().toLowerCase().contains("skip"))
		{
			throw new SkipException(testCaseResult.toString());
		}
		else if(testCaseResult.toString().toLowerCase().contains("fail"))
		{
			Assert.fail(testCaseResult.toString());
		}
		else if(testCaseResult.toString().toLowerCase().contains("pass"))
		{
			Assert.assertTrue(true, testCaseResult.toString());
		}
	}


	@DataProvider
	public Object [][] getResults()
	{
		return resultData;
	}


	/** finishing tests, writing results and saving in db */
	@AfterClass
	public void afterClass()  
	{
		try {
			/** convert the testcaseobject map { either from singleton class or from mongo db - depending on configuration }
			 *  in a list -- as desired by write results method -- only in case of parallel execution */
			new LenskartWebTest_Utils().getUpdatedTestCaseResultsListMongoJack(is_parallel_execution);

			/** create result directory  */
			FileLib.CreateDirectory(TestSuiteClass.AUTOMATION_HOME+"/results/"+channel_type);
			String resultFile=TestSuiteClass.AUTOMATION_HOME+"/results/"+channel_type+"/"+channel_type+"_TestResults.xlsx";

			/** write result file - use mongo to get results - only in case of hub. if not scaling - then also use this menthod */
			if((SingletonTestObject.getSingletonTestObject().isScale_execution_via_mongo() && SingletonTestObject.getSingletonTestObject().isHubMachine())
					|| !SingletonTestObject.getSingletonTestObject().isScale_execution_via_mongo()) {

				logger.info("writing test results at hub ...  ");
				new WriteTestResults().writeTestObjectResults_UsingPoI(SingletonTestObject.getSingletonTestObject().getTestCaseObjectList(), resultFile);	
			}

			/** kill all running process */
			GenericMethodsLib.cleanProcesses();

			endTime =Instant.now();
			executionDuration=Duration.between(startTime, endTime);
			executionTime=Long.toString(executionDuration.toMinutes());

			/** write owner wise data */
			TestSuiteClass.templateData.storeEmailTemplate(resultData, channel_type, executionTime);
			TestSuiteClass.templateData.SetOwnerWiseResultData(resultData, channel_type);

			/** write fail cases in the google sheet*/
			logger.info("**** Started writing fail test cases in google sheet ******");
			WriteFailResultInGoogleSheet.writeFailCaseInGoogleSheet();

			logger.info("***************** Test Ended: " +channel_type + " ************************ ");

			/** this is for jenkins */
			System.out.println("***************** Test Ended: " +channel_type + " ************************ ");

		}catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}

	}


}
