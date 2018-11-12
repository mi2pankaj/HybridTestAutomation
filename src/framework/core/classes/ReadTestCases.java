/**
 * Last Changes Done on Jan 16, 2015 12:13:22 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */


package framework.core.classes;


import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger; 
import org.openqa.selenium.WebDriver;

import com.mysql.jdbc.Connection;

import framework.googleAPI.classes.GoogleSheetUtils;
import io.appium.java_client.AppiumDriver;
import jxl.Sheet;
import jxl.Workbook;
import lenskart.tests.LenskartWebTest_Utils;
import lenskart.tests.TestSuiteClass;
import net.lightbody.bmp.proxy.ProxyServer;


@SuppressWarnings("deprecation")
public class ReadTestCases implements Cloneable
{
	public String tcSummaryRunColumn;
	public String tcSummaryLabelColumn;
	public String tcSummaryTCIdColumn;
	public String tcSummarySupportedBrowserTypeColumn;
	public String tcSummaryDescription;
	public String tcSummaryDataDriven;
	public String tcSummaryType;
	public String tcOwnerName;

	public String tcStepTCIdColumn;
	public String tcStepTCStepIDColumn;
	public String tcStepKeywordColumn;
	public String tcStepObjectColumn;
	public String tcStepDataColumn;
	public String tcStepDescriptionColumn;

	String keyword;
	String objectName;
	String data;
	public String tcSummaryResultColumn;
	public String tcStepResultColumn;
	String separator;
	public String testCaseSummarySheet;
	public String testStepSheet;
	String haltedTestStepResult;
	String haltFlag;
	Instant testCaseStartTime;
	Instant testCaseFinishTime;
	Duration testExecutionDuration;

	static Logger logger = Logger.getLogger(ReadTestCases.class.getName());


	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	public ReadTestCases()
	{	
		this.tcSummaryRunColumn = "Run" ;
		this.tcSummaryTCIdColumn = "TC_ID" ;
		this.tcSummaryResultColumn = "Test_Results";
		this.tcSummarySupportedBrowserTypeColumn = "Supported_Browser_Type";
		this.tcOwnerName = "Owner";

		this.tcSummaryLabelColumn = "Label";
		this.tcSummaryDataDriven = "Data_Driven";
		this.tcSummaryDescription = "Description";
		this.tcSummaryType = "Type";

		this.tcStepTCIdColumn = "TC_ID";
		this.tcStepResultColumn = "Test_Results";
		this.tcStepTCStepIDColumn = "Step_ID";
		this.tcStepKeywordColumn = "Keyword";
		this.tcStepObjectColumn = "objectName";
		this.tcStepDescriptionColumn = "Description";

		/** Choosing input data column based on current test environment */
		if(TestSuiteClass.currentTestEnv.equalsIgnoreCase("preprod"))
		{
			this.tcStepDataColumn = "inputData_Preprod";
		}
		else
		{
			this.tcStepDataColumn = "inputData_Production";
		}

		this.separator = "####";

		this.testCaseSummarySheet = "executionControl";
		this.testStepSheet = "testCaseSteps";	

		this.haltedTestStepResult = "Not Executed.";
		this.haltFlag = "must pass";
	}


	/** getter to return testStepSheet name
	 * @return
	 */
	public String gettestStepSheet()
	{
		return testStepSheet;
	}


	/** getter to return testCaseSummarySheet name
	 * @return
	 */
	public String gettestCaseSummarySheet()
	{
		return testCaseSummarySheet;
	}


	public List<TestCaseObject> getRunnableTestCasesObjects(String SpreadSheetId, String testSummarySheetRange, String testStepSheetRange){

		/** create a list of LoadTestCaseObjects Objects - which contains the runnable testcase id and corresponding testStep details in form of objects */
		List<TestCaseObject> getRunnableTestCaseObjectsList = new ArrayList<>();

		try{
			List<String> tc_id = new ArrayList<String>();
			List<List<Object>> records;
			String[] headers;
			List<HashMap<String, String>> listOfSummaryData ;

			List<List<Object>> testStepRecords;
			String[] testStepHeaders;
			List<HashMap<String, String>> listOfTestSteps ;
			GoogleSheetUtils sheetUtils = new GoogleSheetUtils();

			/** Get the test case summary data from google sheet  */
			records = sheetUtils.getSheetData(SpreadSheetId, 
					testSummarySheetRange);

			/** Get test case summary sheet headers*/
			headers=sheetUtils.getDataHeaders(records);

			/** get the list of test cases summary data i.e. list of hashMap */
			listOfSummaryData =sheetUtils.storeSheetDataInList(records, headers);


			/** Get the test Step sheet data from google sheet  */
			testStepRecords = sheetUtils.getSheetData(SpreadSheetId, 
					testStepSheetRange);

			/** Get test case step sheet headers*/
			testStepHeaders=sheetUtils.getDataHeaders(testStepRecords);

			/** get the list of test cases step sheet data i.e. list of hashMap */
			listOfTestSteps =sheetUtils.storeSheetDataInList(testStepRecords, testStepHeaders);
			
			/** Get ModuleData and test steps in a map */
			Map<String, LinkedList<TestStepObject>> moduleMap = getModuleTestStepData(listOfSummaryData, listOfTestSteps);

			/** Iterate the list of data to get the test cases summary objects to run*/
			for(int row =0; row<listOfSummaryData.size(); row++){

				String runMode = listOfSummaryData.get(row).get(tcSummaryRunColumn).trim();
				
				if(runMode.equalsIgnoreCase("yes")){

					TestCaseObject testCaseObject = new TestCaseObject();

					/** create a List of TestCaseObjects and all the step details in that object */
					LinkedList<TestStepObject> testStepObjectsList = new LinkedList<>();
					
					String testSummary_TCID = listOfSummaryData.get(row).get(tcSummaryTCIdColumn).trim();
					tc_id.add(testSummary_TCID);

					/** get runnable test case - supported browser type and description */
					String testCaseSupportedBrowserType = listOfSummaryData.get(row).get(tcSummarySupportedBrowserTypeColumn).trim();
					String testCaseDescription = listOfSummaryData.get(row).get(tcStepDescriptionColumn).trim();						
					String testCaseDataDriven = listOfSummaryData.get(row).get(tcSummaryDataDriven).trim();
					String testCaseType = listOfSummaryData.get(row).get(tcSummaryType).trim();
					String testCaseOwnerName = listOfSummaryData.get(row).get(tcOwnerName).trim();;

					/** Iterate over the test step sheet data*/
					int index =0;
					for(int rowTestStep =0 ; rowTestStep<listOfTestSteps.size(); rowTestStep++){
						
						String testStep_TCID = listOfTestSteps.get(rowTestStep).get(tcStepTCIdColumn).trim();

						if(testSummary_TCID.equalsIgnoreCase(testStep_TCID)){

							/** get the test steps details corresponding to above test case id */
							String data = listOfTestSteps.get(rowTestStep).get(tcStepDataColumn).trim();
							String description = listOfTestSteps.get(rowTestStep).get(tcStepDescriptionColumn).trim();
							
							/** If any step is calling of the modules then get modules supplied in data from the module map and fit it in the test case object 
							 * if you need to call any module then mention this text in description - include module and supply comma separated module names in test data */
							if(description.toLowerCase().trim().contains("include_module")){
					
								String[] modulesArray = data.split(",");	
								
								/** Add modules steps in the existing teststep linked list */
								for(int i=0; i<modulesArray.length; i++){
									if(moduleMap.containsKey(modulesArray[i])){
										
										/** Get the module and the index i */
										LinkedList<TestStepObject> moduleStepsList = moduleMap.get(modulesArray[i]);
										
										/** Creating a new list of test step object for updating the module test case id and test step id test cases wise */
										LinkedList<TestStepObject> updateModuleStepList = new LinkedList<>();
								
										/** Update the testStep id and test case id for each module steps */
										for(TestStepObject moduleStep : moduleStepsList){	
											/** Copying the value of module's teststepobject to a new object then updating the object
											 * and storing it in update module list */ 
											TestStepObject stepObject= (TestStepObject) moduleStep.clone();
											stepObject.setTestCaseId(testStep_TCID);
											stepObject.setTestStepId(testStep_TCID+"_"+moduleStep.getTestStepId());
											updateModuleStepList.add(stepObject);
										}
										testStepObjectsList.addAll(index, updateModuleStepList);
										index+=moduleStepsList.size();
									}
								}
								
							}else{
								String testStepID = listOfTestSteps.get(rowTestStep).get(tcStepTCStepIDColumn).trim();
								String keyword = listOfTestSteps.get(rowTestStep).get(tcStepKeywordColumn).trim();
								String objectName = listOfTestSteps.get(rowTestStep).get(tcStepObjectColumn).trim();
								String testStepDescription = listOfTestSteps.get(rowTestStep).get(tcStepDescriptionColumn).trim();

								/** create object -- and set all the details for this object */
								TestStepObject tcStepObject = new TestStepObject();
								tcStepObject.setData(data);
								tcStepObject.setKeyword(keyword);
								tcStepObject.setObjectName(objectName);
								tcStepObject.setTestCaseId(testStep_TCID);
								tcStepObject.setTestStepId(testStepID);
								tcStepObject.setTestStepDescription(testStepDescription);

								/** add object in list */
								testStepObjectsList.add(tcStepObject);
							}
							index++;
						}
					}

					/** loading all the test case objects in object of new LoadTestCaseObjects() */
					testCaseObject.settestStepObjectsList(testStepObjectsList);
					testCaseObject.setIfTestCaseQueued(new AtomicBoolean(false));
					testCaseObject.setTestCaseExecutionProgressStatus(new AtomicInteger(0));

					testCaseObject.setTestCaseId(testSummary_TCID);
					testCaseObject.setTestCaseSupportedBrowserType(testCaseSupportedBrowserType);
					testCaseObject.setTestCaseDescription(testCaseDescription);
					testCaseObject.setTestCaseDataDriven(testCaseDataDriven);
					testCaseObject.setTestCaseType(testCaseType);
					testCaseObject.setOwnerName(testCaseOwnerName);

					testCaseObject.setDateTime(new SimpleDateFormat("dd-MM-yy:HH:mm:ss").format(new Date().getTime()).toString());

					/** add test case object in a list finally */
					getRunnableTestCaseObjectsList.add(testCaseObject);
				}
			}

		}catch(Exception e){
			logger.error(" Exception occurred while reading Test Case Summary Sheet :", e);
		}

		return getRunnableTestCaseObjectsList;
	}


	/** what this does - this will first read the execution control sheet and then find those test cases which are set to yes and get the test step details 
	 * from teststep sheet and put those details in objects and finally return the map -- like -- <TestCaseID, <TestStepDetailsObject>>
	 * 
	 * @param fileNameWithLocation
	 * @return
	 */
	public List<TestCaseObject> getRunnableTestCaseObjects(String fileNameWithLocation)		
	{	
		logger.info("Test Case Summary File is : "+fileNameWithLocation);
		List<String> tc_id = new ArrayList<String>();
		Sheet testCaseSummarySheetObj = null;
		Sheet testStepSheetObj = null;
		Workbook book = null;
		boolean flag = true;

		/** create a list of LoadTestCaseObjects Objects - which contains the runnable testcase id and corresponding testStep details in form of objects */
		List<TestCaseObject> getRunnableTestCaseObjectsList = new ArrayList<>();

		try{
			book = Workbook.getWorkbook(new File(fileNameWithLocation));
			testCaseSummarySheetObj = book.getSheet(testCaseSummarySheet);
			testStepSheetObj = book.getSheet(testStepSheet);
		}catch(Exception e){
			flag = false;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exiting -- Please check the file location, Error occurred while loading file: "+fileNameWithLocation, e);
		}

		try
		{
			/** if no exception, then proceed */
			if(flag){

				logger.info("****** loading objects ***** ");

				/** get the column numbers from test step sheet - to retrieve the details later on */
				int tc_id_column = testStepSheetObj.findCell(tcStepTCIdColumn, 0, 0,testStepSheetObj.getColumns(), 0 , false).getColumn();
				int tc_step_id_column = testStepSheetObj.findCell(tcStepTCStepIDColumn, 0, 0, testStepSheetObj.getColumns(),0, false).getColumn();
				int keyword_column = testStepSheetObj.findCell(tcStepKeywordColumn, 0, 0,testStepSheetObj.getColumns(), 0 , false).getColumn();
				int object_column = testStepSheetObj.findCell(tcStepObjectColumn, 0, 0, testStepSheetObj.getColumns(),0, false).getColumn();
				int data_column = testStepSheetObj.findCell(tcStepDataColumn, 0, 0, testStepSheetObj.getColumns(),0, false).getColumn();
				int description_column = testStepSheetObj.findCell(tcStepDescriptionColumn, 0, 0, testStepSheetObj.getColumns(),0, false).getColumn();

				/** Finding "Run" and "TC_ID" column in the Test Case Summary -- which is executionControl sheet */
				int run_column = testCaseSummarySheetObj.findCell(tcSummaryRunColumn, 0, 0, testCaseSummarySheetObj.getColumns(), 0, false).getColumn();
				int id_column = testCaseSummarySheetObj.findCell(tcSummaryTCIdColumn, 0, 0, testCaseSummarySheetObj.getColumns(), 0, false).getColumn();
				int testType_column = testCaseSummarySheetObj.findCell(tcSummaryType, 0, 0, testCaseSummarySheetObj.getColumns(), 0, false).getColumn();

				/** get supported browser type - like chrome, mobile etc. */
				int supportedBrowserType_column = testCaseSummarySheetObj.findCell(tcSummarySupportedBrowserTypeColumn, 0, 0, testCaseSummarySheetObj.getColumns(),0, false).getColumn();
				int testcase_datadriven_column = testCaseSummarySheetObj.findCell(tcSummaryDataDriven, 0, 0, testCaseSummarySheetObj.getColumns(),0, false).getColumn();

				/** get test case description */
				int testCaseDescription_column = testCaseSummarySheetObj.findCell(tcSummaryDescription, 0, 0, testCaseSummarySheetObj.getColumns(),0, false).getColumn();

				/** get test case owner name */
				int testCaseOwnerName_column = testCaseSummarySheetObj.findCell(tcOwnerName,0,0, testCaseSummarySheetObj.getColumns(),0, false).getColumn();

				/** find those test steps from test cases steps sheet, for those test cases which are set to RUN=Yes in executionControl sheet */
				for(int row=1;row<testCaseSummarySheetObj.getRows();row++)
				{
					String runMode = testCaseSummarySheetObj.getCell(run_column, row).getContents().trim();

					if (runMode.equalsIgnoreCase("yes")) 
					{
						/** create an object of LoadTestCaseObjects and set all the details of testStep Objects in this object */
						TestCaseObject testCaseObject = new TestCaseObject();

						/** create a List of TestCaseObjects and all the step details in that object */
						List<TestStepObject> testStepObjectsList = new ArrayList<>();

						/** get runnable test case id */
						String testSummary_TCID = testCaseSummarySheetObj.getCell(id_column, row).getContents().trim();
						tc_id.add(testSummary_TCID);

						/** get runnable test case - supported browser type and description */
						String testCaseSupportedBrowserType = testCaseSummarySheetObj.getCell(supportedBrowserType_column, row).getContents().trim();
						String testCaseDescription = testCaseSummarySheetObj.getCell(testCaseDescription_column, row).getContents().trim();						
						String testCaseDataDriven = testCaseSummarySheetObj.getCell(testcase_datadriven_column, row).getContents().trim();
						String testCaseType = testCaseSummarySheetObj.getCell(testType_column, row).getContents().trim();
						String testCaseOwnerName = testCaseSummarySheetObj.getCell(testCaseOwnerName_column, row).getContents().trim();


						/** iterate the test step sheet -- to find those test steps for which there is a runnable test case id */
						for(int row_testStep =1; row_testStep<testStepSheetObj.getRows(); row_testStep++)
						{
							String testStep_TCID = testStepSheetObj.getCell(tc_id_column , row_testStep).getContents().trim();
							if(testSummary_TCID.equalsIgnoreCase(testStep_TCID))
							{
								/** get the test steps details corresponding to above test case id */
								String testStepID = testStepSheetObj.getCell(tc_step_id_column, row_testStep).getContents().trim();
								String keyword = testStepSheetObj.getCell(keyword_column, row_testStep).getContents().trim();
								String objectName = testStepSheetObj.getCell(object_column, row_testStep).getContents().trim();
								String data = testStepSheetObj.getCell(data_column, row_testStep).getContents().trim();
								String testStepDescription = testStepSheetObj.getCell(description_column, row_testStep).getContents().trim();

								/** create object -- and set all the details for this object */
								TestStepObject tcStepObject = new TestStepObject();
								tcStepObject.setData(data);
								tcStepObject.setKeyword(keyword);
								tcStepObject.setObjectName(objectName);
								tcStepObject.setTestCaseId(testStep_TCID);
								tcStepObject.setTestStepId(testStepID);
								tcStepObject.setTestStepDescription(testStepDescription);

								/** add object in list */
								testStepObjectsList.add(tcStepObject);
							}
						}

						/** loading all the test case objects in object of new LoadTestCaseObjects() */
						testCaseObject.settestStepObjectsList(testStepObjectsList);
						testCaseObject.setIfTestCaseQueued(new AtomicBoolean(false));
						testCaseObject.setTestCaseExecutionProgressStatus(new AtomicInteger(0));

						testCaseObject.setTestCaseId(testSummary_TCID);
						testCaseObject.setTestCaseSupportedBrowserType(testCaseSupportedBrowserType);
						testCaseObject.setTestCaseDescription(testCaseDescription);
						testCaseObject.setTestCaseDataDriven(testCaseDataDriven);
						testCaseObject.setTestCaseType(testCaseType);
						testCaseObject.setOwnerName(testCaseOwnerName);
						testCaseObject.setDateTime(new SimpleDateFormat("dd-MM-yy:HH:mm:ss").format(new Date().getTime()).toString());

						/** add test case object in a list finally */
						getRunnableTestCaseObjectsList.add(testCaseObject);
					}
				}
				book.close();
			}
		}
		catch(Exception e)
		{
			logger.error(" Exception occurred while reading Test Case Summary File :" +fileNameWithLocation, e);
		}

		return getRunnableTestCaseObjectsList; 				
	}


	/** This method is the innovative one, this gonna execute the received test case object and get it loaded with test results 
	 * and later on, can do anything with this single method.  
	 *  
	 * @param testCaseObject
	 * @param connection
	 * @param jsonObjectRepo
	 * @return
	 */
	public TestCaseObject executeTestCaseObject(TestCaseObject testCaseObject, Connection connection)
	{
		String result;

		/** for pass and fail */
		boolean resultFlag;

		try{			
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : "+"executeTestCaseObject Method -- iterating test case object ... ");

			/** This hashmap contains the test step id and corresponding test step data, will not reset for each test case id.
			 * this will be used when user wants to give same (dynamic) data in multiple test steps for one test case.
			 */
			HashMap<String, String> testStepID_InputData = new HashMap<String, String>();

			/** this test step object list will contain the updated test test stp objects with result, and finally this will be
			 * put into test case object */
			List<TestStepObject> finalTestStepsObjectList = new ArrayList<>();

			/** Getting the test case start time */
			testCaseStartTime =Instant.now();

			/** Launching a driver here -- for every test case, it will be launched in every test case id
			 * --> Making this change to make it work --> End to End Test - Threads Framework */
			ProxyServer proxyServer=new WebdriverSetup().startProxyServer(testCaseObject.getTestCaseSupportedBrowserType());

			/** re-initialize driver if found null -- hack -- to be verified by Rishi the Great */
			WebDriver driver=WebdriverSetup.invokeBrowser(testCaseObject.getTestCaseSupportedBrowserType(), proxyServer);

			/** This resultFlag is set to false if any of test step of a test case is failed. 
			 * This flag sets the result either PASS or FAIL or SKIP for the test case id in executionControl sheet.
			 */
			resultFlag = true;

			/** This haltExecution is set to false by default, in case any result is fail and corresponding input data has 
			 * must pass flag, then subsequent steps, for that test case id, will not be executed and default result will set = "Not Executed"  
			 */
			boolean haltExecution = false;

			/** Setting up new feature --> on_error_resume_next, if this flag is found no in the first step of Test Case, then in case of any test step 
			 * failure, subsequent steps won't be executed, similar to must pass flag, but difference is --> must pass flag can be used only with 
			 * verify keywords like verifyText, verifyTitle etc. not with other keywords like typeValue or clickButton etc. 
			 */
			boolean on_error_resume_next = false;

			/** get the list of test step objects - which need to be executed, iterating this...  */
			List<TestStepObject> testSteps = testCaseObject.gettestStepObjectsList();

			for(int row=0; row<testSteps.size(); row++){

				result = "";

				/** Getting the test step start time*/
				Instant testStepStartTime =Instant.now();

				/** get test step object and getting all data from this object */
				TestStepObject testStepObject = testSteps.get(row);
				String testStepID = testStepObject.getTestStepId();
				String keyword = testStepObject.getKeyword();
				String objectName = testStepObject.getObjectName();
				String data = testStepObject.getData();

				/** if found in the first step as no or blank --> that means upon first failure, 
				 * subsequent steps won't be executed. -- set on_error_resume_next flag to true */
				if(row == 0 && keyword.equalsIgnoreCase("on_error_resume_next") && (data.equalsIgnoreCase("no") || data.isEmpty())){
					on_error_resume_next = true;
				}

				/** If the supplied data has #time# then replace #time# with the time stamp. */
				/** First of all store each step step id and corresponding data in a hashmap for each test step id. */
				/** 1. If user supplies the input data like #TC_01_03# in test step id TC_01_06 then this means the input data for 
				 * step 06 is the same as data given in step 03, in this case hashmap stores value like (TC_01_06,#TC_01_03#),
				 * Now first of all get the input data from testStepID_InputData hashmap for id(key) = TC_01_03 after removing # from it.
				 * If hashmap has this value then update hashmap as (TC_01_06,Value) and pass this value for further processing, else data = ""
				 */
				data = new HandlerLib().dataParser(data, keyword, testStepID_InputData, connection);
				objectName = new HandlerLib().dataParser(objectName, keyword, testStepID_InputData, connection);

				/** Update test steps data column with processed data*/
				testStepObject.setData(data);
				testStepObject.setObjectName(objectName);

				/** Check if execution needs to be halted, if yes then set Result = "Not Executed."
				 * for keyword = closebrowser, getproxylog execution will not be halted. 
				 */
				if(!haltExecution || keyword.equalsIgnoreCase("closebrowser") || keyword.equalsIgnoreCase("getproxylog") || keyword.equalsIgnoreCase("getbrowserconsolelogs")){

					/** precondition check - hack to handle notification in emulator -- to be reviewed later on */
					new LenskartWebTest_Utils().handlePreConditions(testCaseObject, driver);

					/** Performing action based on received keyword, object and input data */
					PerformAction action = new PerformAction();
					result = action.performAction(driver, keyword, objectName, data, connection, 
							SingletonTestObject.getSingletonTestObject().getJsonObjectRepository(), proxyServer, testStepObject);

					/** Creating retry controller and evaluating retry condition and performing action against it. 
					 * commenting this code for appium driver for now. on 1/8/2018 with Rishi */
					if(!(driver instanceof AppiumDriver<?>)) {
						result=new RetryController().checkAndPerformRetry(driver, connection, SingletonTestObject.getSingletonTestObject().getJsonObjectRepository(), 
								proxyServer, action, testSteps, row, result, testStepObject);
					}
					/*********************************************************************************************/

					/** if result is received as null then consider it as space */
					result = (result == null) ? "Skip: No Result Received." : result;  
				}
				else{
					result = haltedTestStepResult;
					logger.debug("halting steps now, test step: "+testStepID);
				}

				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : "+"Keyword: "+keyword + ", Object Name: "+objectName + ", Input Data: "+data + ", Test Step: "+testStepID+ ", Result: "+result);

				/** Setting up haltExecution flag to true if on_error_resume_next is found to be true and any fail result. 
				 * If on_error_resume_next is found in first row as no and if there is any Fail result found then set haltExecution = true.
				 */
				if(on_error_resume_next && 
						(
								result.toLowerCase().startsWith("fail:") 
								|| result.toLowerCase().contains("fail:")

								/** set halt execution to true when subsequent steps need to be halted based on result from evaluate_expression keyword, this is important 
								 * to handle the dynamic scenario where a screen somes up suddenly and after that user can't proceed or need not to proceed then subsequent steps will
								 * be skipped. */
								|| result.toLowerCase().startsWith("skipfollowing")
								)
						){
					haltExecution = true;
				}

				/** Setting result of each test case id using flag resultFlag, test case id result will be Fail 
				 * if any of the test step is failed or if any test step result = Not Executed
				 */
				// --->  Need to handle this case where skip test result need to be shown ::::  result.equalsIgnoreCase(haltedTestStepResult)
				if(resultFlag){
					if(result.toLowerCase().trim().matches("^fail.*")
							|| result.toLowerCase().trim().startsWith("fail:") 
							|| result.toLowerCase().contains("fail:")){
						resultFlag = false;
						logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : "+"Found Test Step: "+testStepID +" = Failed:");
					}
				}

				/** Calculating the test step execution time*/
				Instant testStepEndTime = Instant.now();
				Duration timeElasped = Duration.between(testStepStartTime, testStepEndTime);
				long timeTaken = timeElasped.toMillis();
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : "+"Found Test Step: "+testStepID +" Time Taken in seconds = "+Long.toString(timeTaken/1000));

				/** Putting time taken of each test step in respective test step object */
				testStepObject.setTestStepExecutionTime(Long.toString(timeTaken/1000));

				/** Putting results of each test step in respective test step object */
				testStepObject.setTestStepResult(result);

				/** store result in global map - to supply result of one step to another step as data .. */
				testStepID_InputData = new HandlerLib().storeTestStepResult(testStepID_InputData, testStepObject);
				
				/** add updated test step object into a final object list */
				finalTestStepsObjectList.add(testStepObject);
			}

			/** finally add the updated test steps object list in the received test case object and return this */
			testCaseObject.settestStepObjectsList(finalTestStepsObjectList);

			/** set the test case result based on resultFlag -- if its true then Pass else Fail */
			if(resultFlag){
				testCaseObject.setTestCaseResult("PASS");
			}
			else{
				testCaseObject.setTestCaseResult("FAIL");
			}

			/** Calculating the test case execution time*/
			testCaseFinishTime =Instant.now();
			testExecutionDuration =Duration.between(testCaseStartTime, testCaseFinishTime);
			long duration = testExecutionDuration.toMillis()/1000;
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " Total Seconds Taken = "+Long.toString(duration) + " And Test Case Result: "+resultFlag);

			/** Set test case execution time*/
			testCaseObject.setTestCaseExecutionTime(Long.toString(duration));
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : "+" Exception occurred: ", e);
		}
		return testCaseObject;
	}
	
	/**
	 * Iterate over the execution control data sheet and if it contains modules then add each modules and its steps in map
	 * 
	 * @param executionControlList
	 * @param testStepsList
	 * @return
	 */
	public Map<String, LinkedList<TestStepObject>> getModuleTestStepData(List<HashMap<String, String>> executionControlList, List<HashMap<String, String>> testStepsList){
		Map<String, LinkedList<TestStepObject>> moduleMap = new HashMap<>();
		
		try{
			for(int index=0; index<executionControlList.size(); index++){
				
				LinkedList<TestStepObject> moduleStepList = new LinkedList<>();
				
				/** Store the type and test_case_id of test case in variable type and module_id*/
				String type= executionControlList.get(index).get(tcSummaryType).trim();
				
				if(type.equalsIgnoreCase("module")){
					String module_id = executionControlList.get(index).get(tcSummaryTCIdColumn).trim();
					
					for(int stepsIndex=0; stepsIndex<testStepsList.size(); stepsIndex++){
						
						String step_id = testStepsList.get(stepsIndex).get(tcStepTCIdColumn).trim();
						
						if(step_id.equalsIgnoreCase(module_id)){
							/** get the test steps details corresponding to above test case id */
							String testStepID = testStepsList.get(stepsIndex).get(tcStepTCStepIDColumn).trim();
							String keyword = testStepsList.get(stepsIndex).get(tcStepKeywordColumn).trim();
							String objectName = testStepsList.get(stepsIndex).get(tcStepObjectColumn).trim();
							String data = testStepsList.get(stepsIndex).get(tcStepDataColumn).trim();
							String testStepDescription = testStepsList.get(stepsIndex).get(tcStepDescriptionColumn).trim();

							/** create object -- and set all the details for this object */
							TestStepObject tcStepObject = new TestStepObject();
							tcStepObject.setData(data);
							tcStepObject.setKeyword(keyword);
							tcStepObject.setObjectName(objectName);
							tcStepObject.setTestCaseId(module_id);
							tcStepObject.setTestStepId(testStepID);
							//							tcStepObject.setTestStepIdRowNumber(rowTestStep);
							tcStepObject.setTestStepDescription(testStepDescription);
							
							moduleStepList.add(tcStepObject);
						}
						
					}
					
					moduleMap.put(module_id, moduleStepList);
					System.out.println();
				}
			}
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : "+" Exception occurred while getting module data: ", e);
		}
		return moduleMap;
	}

}
