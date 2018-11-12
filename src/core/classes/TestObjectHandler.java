package core.classes;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate.Builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;

import core.utilities.MongoDBHandler;
import tests.SuiteClass;


public class TestObjectHandler {

	Logger logger = Logger.getLogger(TestObjectHandler.class.getName());

	/** This method returns the free test case object which is not yet picked up by any thread
	 * -- free test case objects are picked up either from mongo or from map depending on configuration  
	 * @return
	 */
	public synchronized TreeMap<String, TestCaseObject> getFreeTestCaseObject(){

		if(SingletonTestObject.getSingletonTestObject().isScale_execution_via_mongo()){

			return getFreeTestCaseObjectViaMongoJack();
		}
		else{

			return getFreeTestCaseObjectViaMap();
		}
	}	


	/** This method returns the free test case object which is not yet picked up by any thread
	 * once this is identified, set getIfTestCaseQueued = true so that it doesn't picked up again and
	 * setTestCaseExecutionStatus = 0 to mark it as in progress. 
	 * 
	 * @param testCaseObjects
	 * @return
	 */
	public synchronized TreeMap<String, TestCaseObject> getFreeTestCaseObjectViaMap(){

		TreeMap<String, TestCaseObject> freeTestCaseObjectMap = null;

		try
		{
			for(Entry<String, TestCaseObject> tcObj : SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().entrySet()){

				TestCaseObject testCaseObject = tcObj.getValue();
				String testCaseKey = tcObj.getKey();

				if(testCaseObject.getIfTestCaseQueued().get()==false){

					/** set flag to true so that it doesn't get picked up again */
					testCaseObject.setIfTestCaseQueued(new AtomicBoolean(true));
					testCaseObject.setTestCaseExecutionProgressStatus(new AtomicInteger(0));

					/** before returning the test object, update the actual map */
					SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().put(testCaseKey, testCaseObject);

					/** return the map containing the key and testcase object like: <1, <TestCaseObject>> 
					 * this will help in updating the progress of test case object based on received key in the
					 * global test case object map */
					freeTestCaseObjectMap = new TreeMap<>();
					freeTestCaseObjectMap.put(testCaseKey, testCaseObject);

					logger.info(" Returning Free Test Case ID Via Map: "+testCaseObject.getTestCaseId());
					return freeTestCaseObjectMap;
				}
				else{
					continue;
				}
			}

		}catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}

		return freeTestCaseObjectMap;
	}


	/** This method returns the free test case object which is not yet picked up by any thread
	 * once this is identified, set getIfTestCaseQueued = true so that it doesn't picked up again and
	 * setTestCaseExecutionStatus = 0 to mark it as in progress. 
	 *  
	 *  -- using mongo rather than map
	 *  
	 * @return
	 */
	public synchronized TreeMap<String, TestCaseObject> getFreeTestCaseObjectViaMongo(){

		TreeMap<String, TestCaseObject> freeTestCaseObjectMap = new TreeMap<>();

		try
		{
			/** get the free test case object */
			FindIterable<Document> document = 
					new MongoDBHandler().getMongoDbDocument(SingletonTestObject.getSingletonTestObject().getMongoDBClientConnnection(), 
							"{\"ifTestCaseQueued\":false}.limit(1)");

			/** process further only if document is received. */
			if(document.first() != null) {

				String testCaseObjectFromMongo = document.first().toJson().toString();

				ObjectMapper mapper = new ObjectMapper();
				TestCaseObject testCaseObject = mapper.readValue(testCaseObjectFromMongo, TestCaseObject.class);			

				logger.info(" Returning Free Test Case ID Via Mongo: "+testCaseObject.getTestCaseId() + " at machine: "+InetAddress.getLocalHost().getHostAddress());

				/** set flag to true so that it doesn't get picked up again */
				testCaseObject.setIfTestCaseQueued(new AtomicBoolean(true));
				testCaseObject.setTestCaseExecutionProgressStatus(new AtomicInteger(0));
				testCaseObject.setExecutorMachineIpAddress(InetAddress.getLocalHost().getHostAddress());

				/** create filter criteria */
				Map<String, Object> filterMap = new HashMap<>();
				filterMap.put("testCaseId", testCaseObject.getTestCaseId());

				/** replace this updated object with existing document in mongo db. */
				new MongoDBHandler().replaceMongoDbDocument(SingletonTestObject.getSingletonTestObject().getMongoDBClientConnnection()
						,filterMap
						,new ObjectMapper().writeValueAsString(testCaseObject));

				/** keep a copy of this map in singletonclass.HashMap also -- why I am putting an int in SingletonTestObject.TestCaseObjectMap() --
				 * this is just to match the structure and way -- we do in case of maps only  */
				//int x = SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().size()+1;
				SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().put(testCaseObject.getTestCaseId(), testCaseObject);

				/** return the updated map */
				freeTestCaseObjectMap.put(testCaseObject.getTestCaseId(), testCaseObject);
			}
		}
		catch(Exception e)
		{
			logger.error("Error while getting data from mongo:  " + e.getMessage(), e);
		}

		return freeTestCaseObjectMap;
	}

	/** This method returns the free test case object which is not yet picked up by any thread
	 * once this is identified, set getIfTestCaseQueued = true so that it doesn't picked up again and
	 * setTestCaseExecutionStatus = 0 to mark it as in progress. 
	 *  
	 *  -- using mongojack rather than map
	 *  
	 * @return
	 */

	public synchronized TreeMap<String, TestCaseObject> getFreeTestCaseObjectViaMongoJackObslete(){

		TreeMap<String, TestCaseObject> freeTestCaseObjectMap = new TreeMap<>();

		try
		{
			/** find a free test case object from mongo and remove it from collection to avoid picking it up by another machine, later on insert 
			 * this object after updating the values. */
			TestCaseObject testCaseObject = SingletonTestObject.getSingletonTestObject().getMongoDBCollection()
					.findAndRemove(new BasicDBObject("ifTestCaseQueued",new AtomicBoolean(false).get()));

			/** process further only if document is received. */
			if(testCaseObject != null) {

				logger.info(" Found and Removed Free Test Case ID From Mongo: "+testCaseObject.getTestCaseId() + " at machine: "+InetAddress.getLocalHost().getHostAddress());

				/** set flag to true so that it doesn't get picked up again */
				testCaseObject.setIfTestCaseQueued(new AtomicBoolean(true));
				testCaseObject.setTestCaseExecutionProgressStatus(new AtomicInteger(0));
				testCaseObject.setExecutorMachineIpAddress(InetAddress.getLocalHost().getHostAddress());

				/** earlier we're replacing an existing document and now new approach is to find a document and remove it from collection
				 * and then update the test case object in memory and then insert it. */
				SingletonTestObject.getSingletonTestObject().getMongoDBCollection().insert(testCaseObject);

				/** keep a copy of this map in singletonclass.HashMap also -- why I am putting an int in SingletonTestObject.TestCaseObjectMap() --
				 * this is just to match the structure and way -- we do in case of maps only  ...  */
				//int x = SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().size()+1;
				SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().put(testCaseObject.getTestCaseId(), testCaseObject);

				/** update the local map also with free test case object */
				freeTestCaseObjectMap.put(testCaseObject.getTestCaseId(), testCaseObject);
			}
		}
		catch(Exception e)
		{
			logger.error("Error while getting data from mongo:  " + e.getMessage(), e);
		}

		return freeTestCaseObjectMap;
	}



	/** This method returns the free test case object which is not yet picked up by any thread
	 * once this is identified, set getIfTestCaseQueued = true so that it doesn't picked up again and
	 * setTestCaseExecutionStatus = 0 to mark it as in progress. 
	 *  
	 *  -- using mongojack rather than map
	 *  
	 * @return
	 */
	public synchronized TreeMap<String, TestCaseObject> getFreeTestCaseObjectViaMongoJack(){

		TreeMap<String, TestCaseObject> freeTestCaseObjectMap = new TreeMap<>();

		try
		{
			/** creating builder instance and setting the fields to update in testcaseObject mongoDB */
			Builder builder = new Builder();
			
			builder.set("ifTestCaseQueued", new AtomicBoolean(true));
			builder.set("testCaseExecutionProgressStatus", new AtomicInteger(0));
			builder.set("executorMachineIpAddress", InetAddress.getLocalHost().getHostAddress());

			/** Query to find the testcaseObject from mongoDB */
			BasicDBObject query = new BasicDBObject("ifTestCaseQueued",new AtomicBoolean(false).get());

			/** finding and modifying the mongoDB object and returning the testcaseobject */
			TestCaseObject testCaseObject= SingletonTestObject.getSingletonTestObject().getMongoDBCollection().
					findAndModify(query, null, null, false, builder, true, false);

			/** process further only if document is received. */
			if(testCaseObject != null) {

				logger.info(" Assigning the free test case with: "+testCaseObject.getTestCaseId() + " at machine: "+InetAddress.getLocalHost().getHostAddress());

				/** keep a copy of this map in singletonclass.HashMap also -- why I am putting an int in SingletonTestObject.TestCaseObjectMap() --
				 * this is just to match the structure and way -- we do in case of maps only  ...  */
				SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().put(testCaseObject.getTestCaseId(), testCaseObject);

				/** update the local map also with free test case object */
				freeTestCaseObjectMap.put(testCaseObject.getTestCaseId(), testCaseObject);
			}
		}
		catch(Exception e)
		{
			logger.error("Error while getting data from mongo:  " + e.getMessage(), e);
		}

		return freeTestCaseObjectMap;
	}

	/** check if all test case objects are executed, check from maps / mongo -- decided by configuration
	 * 
	 * @return
	 */
	public synchronized boolean ifAllTestCaseObjectsAreExecuted(){

		if(SingletonTestObject.getSingletonTestObject().isScale_execution_via_mongo()){

			return ifAllTestCaseObjectsAreExecutedViaMongoJack();
		}
		else{

			return ifAllTestCaseObjectsAreExecutedViaMap();
		}
	}


	/** Find if all tasks are executed by checking  getTestCaseExecutionStatus, which should be 1 -- via map.
	 * 
	 * @return
	 */
	public synchronized boolean ifAllTestCaseObjectsAreExecutedViaMap(){

		boolean flag = true;

		try{
			for(Entry<String, TestCaseObject> tcObj : SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().entrySet()){

				TestCaseObject testCaseObject = tcObj.getValue();

				/** that means there is still some task are going on, if no 0 then all were executed */
				if(testCaseObject.getTestCaseExecutionProgressStatus().get()==(Integer.parseInt("0"))){

					logger.info("test case objects execution in progress via maps .. found object: "+testCaseObject.getTestCaseId());
					return false;
				}
				else{
					continue;
				}
			}
		}catch(Exception e)
		{
			flag = false;
			logger.error("Error while checking completed test case objects via maps .. " + e.getMessage(), e);
		}

		return flag;
	}


	/** find out if all test case object are executed via mongo, 
	 * checking getTestCaseExecutionStatus -- should be 1 for completed test cases.
	 * 
	 * @return
	 */
	public synchronized boolean ifAllTestCaseObjectsAreExecutedViaMongo(){

		boolean flag = true;

		try{
			/** check if there is any testcase object for which testCaseExecutionProgressStatus =0 - that means execution is still in
			 * progress, return false */
			FindIterable<Document> documents = new MongoDBHandler().getMongoDbDocument
					(SingletonTestObject.getSingletonTestObject().getMongoDBClientConnnection()
							, "{\"testCaseExecutionProgressStatus\":0}");

			/** return false if there is any document returned using above query - that means there is still test in progress */
			if(documents.iterator().hasNext()) {

				logger.info("test case objects execution in progress via mongo .. ");
				return false;
			}
		}catch(Exception e)
		{
			flag = false;
			logger.error("Error while checking completed test case objects via mongo .. " + e.getMessage(), e);
		}

		return flag;
	}


	/** find out if all test case object are executed via mongoJack, 
	 * checking getTestCaseExecutionStatus -- should be 1 for completed test cases.
	 * 
	 * @return
	 */
	public synchronized boolean ifAllTestCaseObjectsAreExecutedViaMongoJack(){

		boolean flag = true;

		try{
			/** check if there is any testcase object for which testCaseExecutionProgressStatus =0 - that means execution is still in
			 * progress, return false */
			DBCursor<TestCaseObject> listTestCasesObject = new MongoDBHandler().getMongoDbDocumentMongoJack(
					"testCaseExecutionProgressStatus", new AtomicInteger(0)
					);

			/** return false if there is any document returned using above query - that means there is still test in progress */
			if(listTestCasesObject.iterator().hasNext()) {

				logger.info("test case objects execution in progress via mongo .. ");
				return false;
			}
		}catch(Exception e)
		{
			flag = false;
			logger.error("Error while checking completed test case objects via mongo .. " + e.getMessage(), e);
		}

		return flag;
	}

	/** why this is required ?? actually after a test case object is executed, I need to put the executed object back into same static list
	 * but if I do that --> then list will keep on increasing as every time I add object in a list, it treats that a different object and this
	 * way, I would have twice of the actual size of objects in test case list, therefore better to have a map and in that I can put the updated object
	 * back in the same key. 
	 * 
	 * @param testCaseObjectList
	 * @return
	 */
	public ConcurrentHashMap<String, TestCaseObject> getTestCaseObjectMap(List<TestCaseObject> testCaseObjectList)
	{
		ConcurrentHashMap<String, TestCaseObject> testCaseObjectMap = new ConcurrentHashMap<>();

		for(int i=0; i<testCaseObjectList.size(); i++)
		{
			testCaseObjectMap.put(testCaseObjectList.get(i).getTestCaseId(), testCaseObjectList.get(i));
		}

		return testCaseObjectMap;
	}


	/** add separate method to execute test case and write respective results 
	 * 
	 * @param testCaseObject
	 * @return
	 */
	public synchronized Object executeTask()
	{
		try
		{
			/** get a free test object before proceeding */
			TreeMap<String, TestCaseObject> testCaseObjectMapToBeExecuted = new TestObjectHandler().getFreeTestCaseObject();

			if(testCaseObjectMapToBeExecuted != null && !testCaseObjectMapToBeExecuted.isEmpty()){

				logger.info("Executing Free Test Case: "+testCaseObjectMapToBeExecuted.firstEntry().getValue().getTestCaseId());

				/** while execution, received map will always contain a single entry */
				TestCaseObject testCaseObject = testCaseObjectMapToBeExecuted.firstEntry().getValue();

				/** setting up execution id - as test case id */
				SuiteClass.UNIQ_EXECUTION_ID.set(testCaseObject.getTestCaseId());

				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+": starting test ... "+testCaseObject.getTestCaseId());

				String testCaseKey = testCaseObjectMapToBeExecuted.firstEntry().getKey();

				/** execute test case object */
				testCaseObject = new ReadTestCases().executeTestCaseObject(testCaseObject, SingletonTestObject.getSingletonTestObject().getMysqlDbConnection());

				/** mark test case progress to 1 -- that means - completed and add that back into global test object map or update object in mongodb */
				testCaseObject.setTestCaseExecutionProgressStatus(new AtomicInteger(1));
				SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().put(testCaseKey, testCaseObject);

				/** update mongodb document - if required */				
				replaceMongoDBDocumentWithMongoJackObject(testCaseObject);

				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+": test object completed ... ");
			}
			else {
				logger.info("No_Free_Task_Received");

				return "No_Free_Task_Received";
			}

		}
		catch(Exception e){
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() +" : " +e.getMessage(), e);
		}

		return SuiteClass.UNIQ_EXECUTION_ID.get()+" - Test_Completed";
	}


	/** assign the free task to executors and execute tasks 
	 * 
	 * @param executor
	 * @param connectionServe
	 * @param jsonObjectRepo
	 * @return
	 */
	public synchronized Object assignTasks_ForParallelExecution()
	{
		try
		{
			/** first get a not executed test case */
			//			TreeMap<Integer, TestCaseObjects> testCaseObjectMapToBeExecuted = new TestObjectsHandler().getFreeTestCaseObject();

			//			if(testCaseObjectMapToBeExecuted != null && !testCaseObjectMapToBeExecuted.isEmpty())
			//			{	
			//				logger.info("Executing Free Test Case: "+testCaseObjectMapToBeExecuted.firstEntry().getValue().getTestCaseId());

			CompletableFuture.supplyAsync(() -> new TestObjectHandler().executeTask(), SingletonTestObject.getSingletonTestObject().getTestExecutorService());

			return "Task_In_Progress";
			//			}
			//			else
			//			{
			//				return "No_Free_Task_Received";
			//			}
		}catch (Exception e) {

			logger.error(e.getMessage(), e);
			return "Exception_Occurred";
		}
	}


	/** Get the suite specific test case objects
	 * 
	 * @param testCaseObjectList
	 * @param test_suite_type
	 * @return
	 */
	public List<TestCaseObject> getTestCaseObjects_SuiteSpecific(List<TestCaseObject> testCaseObjectList)
	{
		List<TestCaseObject> updatedTestCaseObjectList = new ArrayList<>();

		/** set by maven command line param */
		String test_suite_type = System.getProperty("test_suite_type");

		/** if not found then set this regression */
		if(test_suite_type == null) {
			test_suite_type = "regression";
		}

		/** run all in case of regression or no value */
		if(test_suite_type.equalsIgnoreCase("regression") || test_suite_type.isEmpty() )
		{
			for(TestCaseObject testCaseObject : testCaseObjectList) {

				/** ignoring LoadTest and CartDelete Cases */
				if(!testCaseObject.getTestCaseType().trim().toLowerCase().contains("excluderegression") && !testCaseObject.getTestCaseType().trim().toLowerCase().contains("module")) {
					updatedTestCaseObjectList.add(testCaseObject);
				}
			}

			System.out.println(" test suite: "+test_suite_type + " - total tests: " +updatedTestCaseObjectList.size());
			return updatedTestCaseObjectList;
		}
		else
		{
			for(TestCaseObject testCaseObject : testCaseObjectList)
			{
				if(testCaseObject.getTestCaseType().trim().toLowerCase().contains(test_suite_type.toLowerCase()))
				{
					updatedTestCaseObjectList.add(testCaseObject);
				}	
			}

			System.out.println(" test suite: "+test_suite_type + " - total tests: " +updatedTestCaseObjectList.size());

			return updatedTestCaseObjectList;
		}

	}


	/** Get the desired test case object from global map, for test data driven test cases - test case id will be from test sheet / google sheet 
	 * but TestSuite.Uniq_Execution_Id will be testdataid.
	 * 
	 * @param testCaseId
	 * @return
	 */
	public TreeMap<String, TestCaseObject> getDesiredTestCaseObject(String testCaseId)
	{
		for(Entry<String, TestCaseObject> entry : SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().entrySet())
		{
			if(entry.getValue().getTestCaseId().equalsIgnoreCase(testCaseId))
			{
				TreeMap<String, TestCaseObject> map = new TreeMap<>();
				map.put(entry.getKey(), entry.getValue());

				return map;
			}
			else
			{
				continue;
			}
		}

		return null;
	}


	/** update this testobject in mongodb if required by configuration
	 * -- this method is useful for those case where mongodb needs to be updated based on configuration, we'll just serialize the 
	 * received test case object and replace the existing one with this one.
	 * 
	 * @param testCaseObject
	 * @return
	 */
	public boolean replaceMongoDBDocumentWithSerailzedTestCaseObjectJson(TestCaseObject testCaseObject)
	{
		try {
			if(SingletonTestObject.getSingletonTestObject().isScale_execution_via_mongo()) {

				String testCaseId = testCaseObject.getTestCaseId();

				/** serialize test object as json and write in mongodb. */
				String testCaseObjectJson = new ObjectMapper().writeValueAsString(testCaseObject);

				/** create search query */
				Map<String, Object> filterMap = new HashMap<>();
				filterMap.put("testCaseId", testCaseId);

				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " document is being updated in mongodb. ");

				/** update existing document */
				new MongoDBHandler().replaceMongoDbDocument(SingletonTestObject.getSingletonTestObject().getMongoDBClientConnnection()
						,filterMap
						,testCaseObjectJson);

				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " document updated in mongodb. ");
			}

			return true;
		}
		catch (Exception e) {
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " - " + e.getMessage(), e);

			return false;
		}
	}


	/** update this testobject in mongodb if required by configuration
	 * -- this method is useful for those case where mongodb needs to be updated based on configuration, we'll just serialize the 
	 * received test case object and replace the existing one with this one.
	 * 
	 * @param testCaseObject
	 * @return
	 */
	public boolean replaceMongoDBDocumentWithMongoJackObject(TestCaseObject testCaseObject)
	{
		try {
			if(SingletonTestObject.getSingletonTestObject().isScale_execution_via_mongo()) {

				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " document is being updated in mongodb. ");

				String fieldName = "testCaseId";

				//				/** update existing document */
				//				String testCaseId = testCaseObject.getTestCaseId();
				//				new MongoDBHandler().replaceMongoDbDocumentMongoJack(fieldName, testCaseId, testCaseObject);

				/** trial approach - first find and remove -- then insert one .. coz having issues with updating doc 
				 * -- doing this won't hurt coz  its a final step in execution .. */
				SingletonTestObject.getSingletonTestObject().getMongoDBCollection().findAndRemove(DBQuery.is(fieldName, testCaseObject.getTestCaseId()));
				SingletonTestObject.getSingletonTestObject().getMongoDBCollection().insert(testCaseObject);				

				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " document updated in mongodb. ");
			}

			return true;
		}
		catch (Exception e) {
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " - " + e.getMessage(), e);

			return false;
		}
	}

}


