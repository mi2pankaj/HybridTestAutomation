package core.classes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

import lenskart.tests.TestSuiteClass;


public class TestDataObject {

	Logger logger = Logger.getLogger(TestDataObject.class.getName());


	@Test
	public static void main() throws IOException 
	{
		TestDataObject dataSheet = new TestDataObject();

		Recordset recordset=dataSheet.readTestDataSheet
				(System.getProperty("user.dir").concat("/tc_data/desktopSite/desktopSite_Test_Data.xls"), 
						"Select * from Test_Data");

		List<HashMap<String, String>> testDataList = new TestDataObject().getTestDatasheet(recordset);

		List<TestCaseObject> updatedTestCasesList = dataSheet.getUpdatedTestCasesObjectList(testDataList, 
				new ReadTestCases().getRunnableTestCaseObjects(
						System.getProperty("user.dir").concat("/tc_cases/desktopSite/desktopSite_Test_Cases_Data_Driven.xls")
						));

		new WriteTestResults().writeTestObjectResults_UsingPoI(updatedTestCasesList,System.getProperty("user.dir")+"/rishi");

	}



	/** read the supplied sheet based on query --> get the record set
	 * 
	 * @param file
	 * @param query
	 * @return
	 */
	public Recordset readTestDataSheet(String file, String query) 
	{
		Recordset record = null;

		try {
			Fillo fillo=new Fillo();
			Connection connection =fillo.getConnection(file);
			record = connection.executeQuery(query);

		}catch (Exception e) {
			logger.error("Exception while loading test data from sheet: "+file, e);
		}
		return record;
	}


	/** Get the list of test data map which is each row of test data sheet --- like first row will be key of map and subsequent row will be values. 
	 * 
	 * @param recordset
	 * @return
	 */
	public  List<HashMap<String, String>> getTestDatasheet(Recordset recordset)
	{
		List<HashMap<String, String>> listOftestDataMaps = new ArrayList<>();

		try
		{
			ArrayList<String> dataColumnNames =recordset.getFieldNames();

			while(recordset.next())
			{				
				HashMap<String, String> rowHashMap = new HashMap<>();

				for(int j=0; j<dataColumnNames.size(); j++)
				{
					String fieldName = dataColumnNames.get(j);
					String value = recordset.getField(fieldName);

					rowHashMap.put(fieldName, value);
				}

				listOftestDataMaps.add(rowHashMap);
			}
		}catch (Exception e) {
			logger.error(" : Exception occurred while getting test data map : ", e);
		}

		return listOftestDataMaps;
	}


	/** Get the test case object which is data driven.
	 * 
	 * @param testCaseObjectList
	 * @param testCaseId
	 * @return
	 */
	public TestCaseObject getDataDrivenTestCaseObject(List<TestCaseObject> testCaseObjectList, String testCaseId)  
	{
		TestCaseObject testCaseObject = new TestCaseObject();

		try
		{
			for(int i=0; i<testCaseObjectList.size();i++) {

				if(testCaseObjectList.get(i).getTestCaseId().equalsIgnoreCase(testCaseId)) {
					testCaseObject = (TestCaseObject) testCaseObjectList.get(i).clone();
					break;
				}
			}
		}catch (Exception e) {
			logger.error(" Exception occurred : ", e);
		}
		return testCaseObject;
	}


	/** Update the data in received test step object from - Test Data Map.
	 * 
	 * @param rowHashMap
	 * @param testcaseobj
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws CloneNotSupportedException 
	 */
	public TestCaseObject updateDataInTestCaseObject(HashMap<String, String> rowHashMap, TestCaseObject testcaseobj){
		try
		{
			List<TestStepObject> teststepobjlist= testcaseobj.gettestStepObjectsList();
			List<TestStepObject> updatedTestStepobjlist = new ArrayList<>();

			for(int i =0; i<teststepobjlist.size(); i++) {

				/** create a clone of object before updating it otherwise this update statement will update the received test step object list in test case object */
				TestStepObject teststepobject =(TestStepObject) teststepobjlist.get(i).clone();
				String testData=teststepobject.getData();

				/** add a regular expression to find the used test data keys in format [abcd] so that all of these can be parsed and replaced in test case data */
				Pattern pattern = Pattern.compile("\\[.*?\\]");
				Matcher matcher = pattern.matcher(testData);

				while(matcher.find()) {
					String testDataKey = matcher.group();

					/** check if the received key from regular expression is available in row hashmap */
					if(rowHashMap.containsKey(testDataKey)) {
						String testValue = (String) rowHashMap.get(testDataKey);
						testData = testData.replace(testDataKey, testValue);

						teststepobject.setData(testData);
					}					
				}				

				/** using this code again - in case test data macros are not used with [] in test cases */
				if(rowHashMap.containsKey(testData)) {
					String testValue = (String) rowHashMap.get(testData);
					teststepobject.setData(testValue);
				}

				/** updating the test step objects in updated object list */
				updatedTestStepobjlist.add(teststepobject);
			}

			/** update test case object with test steps */
			testcaseobj.settestStepObjectsList(updatedTestStepobjlist);

			/** lenskart specific -- in case of Data Driven Test -- TD_ID is not received like in case of VSM there is no test data sheet
			 * actual test data comes from a serialized json file -- to display the test data id in log -- need to set order id as Uniq_Execution_Id */
			if(rowHashMap.get("TD_ID") == null) {
				testcaseobj.setTestDataID(rowHashMap.get("order_Id"));

				/** saving test case id as testCaseId-TestDataID --> this is required to save whole data in final map with unique keys. */
				testcaseobj.setTestCaseId(testcaseobj.getTestCaseId()+"-"+testcaseobj.getTestDataID());
			}else {
				/** set test data id in test case object */
				testcaseobj.setTestDataID(rowHashMap.get("TD_ID"));

				/** update test case id as TestCaseId-TestDataId */
				testcaseobj.setTestCaseId(testcaseobj.getTestCaseId()+"-"+rowHashMap.get("TD_ID"));
			}

		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + e.getMessage(), e);
		}
		return testcaseobj;
	}


	/** get the updated the Test Case Object List --> supply the actual test case object list, this will read the test data sheet and 
	 * create the as number of test case object as number of test data is available -- data driven tests
	 * 
	 * @param testDataMap
	 * @param testCaseObjectList
	 * @return
	 */
	public List<TestCaseObject> getUpdatedTestCasesObjectList(List<HashMap<String, String>> testDataMap, List<TestCaseObject> testCaseObjectList){

		List<TestCaseObject> updatedTestCasesList = new ArrayList<>();

		try{
			/** iterate test data map and create that many data driven test case object and them into final list to be executed later on */
			for(HashMap<String, String> rowHashMap : testDataMap){

				String testCaseId = rowHashMap.get("TC_ID");

				/** check if test case id is data driven or not, clone method is required not the copy because in case of copy same object is being created
				 * whereas we want a new object reference all the time- to achieve this, implemented clonable interface */				
				TestCaseObject testCaseObject = (TestCaseObject) getDataDrivenTestCaseObject(testCaseObjectList, testCaseId).clone();

				/** if not null then update test case object */
				if(testCaseObject != null && testCaseObject.getTestCaseDataDriven() != null 
						&& testCaseObject.getTestCaseDataDriven().equalsIgnoreCase("Yes")){

					testCaseObject = updateDataInTestCaseObject(rowHashMap, testCaseObject);

					/** add the final test case object in updatedTestCases list */
					updatedTestCasesList.add(testCaseObject);
				}
			}

			/** now iterate the received testCaseObjectList and find those test case objects which are have data_driven = no 
			 * and add them in updatedTestCases list */
			for(TestCaseObject testCaseObjects : testCaseObjectList) {

				if(testCaseObjects.getTestCaseDataDriven().equalsIgnoreCase("No")) {
					updatedTestCasesList.add(testCaseObjects);
				}
			}

		}catch (Exception e) {
			logger.error(" Error occurred while getting updated test case object list: ", e);
		}

		return updatedTestCasesList;
	}

	/** get the updated the Test Case Object List --> supply the actual test case object list, this will read the test data sheet and 
	 * create the as number of test case object as number of test data is available -- data driven tests
	 * 
	 * @param testDataMap
	 * @param testCaseObjectList
	 * @return
	 */
	public List<TestCaseObject> getUpdatedTestCasesObjectListWithSuitetype(List<HashMap<String, String>> testDataMap, List<TestCaseObject> testCaseObjectList, String suite_type){

		List<TestCaseObject> updatedTestCasesList = new ArrayList<>();
		String testCaseId= null;

		try{
			/** iterate test data map and create that many data driven test case object and them into final list to be executed later on */
			for(HashMap<String, String> rowHashMap : testDataMap){

				if(suite_type.trim().equalsIgnoreCase("mobile")){
					testCaseId = rowHashMap.get("Mobile_TC_ID");
				}else if(suite_type.trim().equalsIgnoreCase("desktop")){
					testCaseId = rowHashMap.get("Desktop_TC_ID");
				}


				/** check if test case id is data driven or not, clone method is required not the copy because in case of copy same object is being created
				 * whereas we want a new object reference all the time- to achieve this, implemented clonable interface */				
				TestCaseObject testCaseObject = (TestCaseObject) getDataDrivenTestCaseObject(testCaseObjectList, testCaseId).clone();

				/** if not null then update test case object */ 
				if(testCaseObject != null && testCaseObject.getTestCaseDataDriven().equalsIgnoreCase("Yes")){

					testCaseObject = updateDataInTestCaseObject(rowHashMap, testCaseObject);

					/** add the final test case object in updatedTestCases list */
					updatedTestCasesList.add(testCaseObject);
				}
			}

			/** now iterate the received testCaseObjectList and find those test case objects which are have data_driven = no 
			 * and add them in updatedTestCases list */
			for(TestCaseObject testCaseObjects : testCaseObjectList) {

				if(testCaseObjects.getTestCaseDataDriven().equalsIgnoreCase("No")) {
					updatedTestCasesList.add(testCaseObjects);
				}
			}

		}catch (Exception e) {
			logger.error(" Error occurred while getting updated test case object list: ", e);
		}

		return updatedTestCasesList;
	}

}
