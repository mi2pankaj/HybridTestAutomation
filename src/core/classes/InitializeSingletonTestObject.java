package core.classes;

import java.net.InetAddress;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;

import core.grid.GetGridConfiguration;
import core.grid.Node;
import core.utilities.GenericMethodsLib;
import core.utilities.MongoDBHandler;
import core.utilities.httpClientWrap;
import tests.TestClass_Utils;

/**
 * 
 * @author pankaj.katiyar
 *
 * This class will serve the purpose of filling up singleton object with all necessary details. 
 * Just need to call this constructor to fill it up and use later.
 * 
 */

public class InitializeSingletonTestObject {

	Logger logger = Logger.getLogger(InitializeSingletonTestObject.class.getName());

	/**
	 * set all the initializer for singleton class
	 * 
	 * @param testCaseObjectList
	 * @param testCaseObjectMap
	 *
	 */
	public InitializeSingletonTestObject(List<TestCaseObject> testCaseObjectList, 
			ConcurrentHashMap<String, TestCaseObject> testCaseObjectMap,
			JSONObject jsonObjectRepository) {

		try {
			/** setting up test monitor executor size - this will be used for other purposes like monitoring appium driver or emualtor cration etc. */
			ExecutorService monitorExecutor=Executors.newCachedThreadPool();
			SingletonTestObject.getSingletonTestObject().setMonitoringExecutorService(monitorExecutor);

			/** update the singleton TestObject with test object list */
			SingletonTestObject.getSingletonTestObject().setTestCaseObjectList(testCaseObjectList);

			/** update the singleton TestObject with this test object map -- only map  should be used via singleton object. */
			SingletonTestObject.getSingletonTestObject().setTestCaseObjectMap(testCaseObjectMap);

			/** add json object repository in singleton object  */
			SingletonTestObject.getSingletonTestObject().setJsonObjectRepository(jsonObjectRepository);
			
			/** create avd dynamically asynchronously before starting tests rather than creating during tests - if required */
			new TestClass_Utils().manageAVDDynamically();

			/** get system property to set mongodb for execution */
			String scale_execution_via_mongo = get_scale_execution_via_mongo();

			/** set the grid ready */
			if(scale_execution_via_mongo.equalsIgnoreCase("Yes")) {

				/** setting up mongo instance. */
				SingletonTestObject.getSingletonTestObject().setMongoDBClientConnnection(new MongoDBHandler().getMongoClient());

				/** check if mongo connection is created, if yes then set setScale_execution_via_mongo = true - we'll use this later on in whole classes  */
				if(SingletonTestObject.getSingletonTestObject().getMongoDBClientConnnection() != null) {

					/** set flag for mongo execution to true in singleton class */
					SingletonTestObject.getSingletonTestObject().setScale_execution_via_mongo(true);

					/** set up mongo db name and collection name */
					SingletonTestObject.getSingletonTestObject().setMongoDBCollection(getMongoDbCollection());

					/** set up roles for grid*/
					setGridRoles();

					/** write test object in mongo if its a hub only*/
					storeSerializedTestObjectInMongo();

					/** once all this is done - then make a curl call to all nodes to invoke execution */
					if(SingletonTestObject.getSingletonTestObject().isHubMachine()) {
						
						callNodeUrls();
						logger.info("called node machines... ");
					}

				}else {
					SingletonTestObject.getSingletonTestObject().setScale_execution_via_mongo(false);
					logger.warn("mongo connection was unsuccessful ... ");
				}			
			}else {
				logger.warn("map will be used for scaling the execution ... ");
			}

			/** set executor size for grid - in case of no scaling - this size will be used for running locally for maps */
			setExecutorSize();

			/** set db connection -- customize */
			SingletonTestObject.getSingletonTestObject().setMysqlDbConnection(null);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/** get scale_execution_via_mongo value.
	 * @return
	 */
	private String get_scale_execution_via_mongo() {
		String scale_execution_via_mongo = "";
		try{
			scale_execution_via_mongo = System.getProperty("scale_execution_via_mongo").trim();
			if(scale_execution_via_mongo == null) {
				scale_execution_via_mongo = "No";
			}
		}catch (Exception e) {scale_execution_via_mongo = "No";}

		return scale_execution_via_mongo;
	}

	/** set roles for execution machines in grid.  
	 * 
	 */
	private void setGridRoles()	{
		try {
			List<Node> nodes = new GetGridConfiguration().getGridConfiguration().getNodes();
			String localIpAddress = InetAddress.getLocalHost().getHostAddress();
			logger.info("Local IPAddress : "+localIpAddress);

			/** check if the execution machine is node */
			for(Node node : nodes) {
				if(localIpAddress.equalsIgnoreCase(node.getIpAddress().trim())){					

					/** getting the desired node object and set in singleton class */
					SingletonTestObject.getSingletonTestObject().setNodeConfiguration(node);
					SingletonTestObject.getSingletonTestObject().setIfNodeMachine(true);

					logger.info("node machine set. ");
					break;
				}
			}

			/** check if the execution machine is hub */
			String hub = new GetGridConfiguration().getGridConfiguration().getHub().getIpAddress().trim();
			if(localIpAddress.equalsIgnoreCase(hub)){					
				SingletonTestObject.getSingletonTestObject().setIfHubMachine(true);

				logger.info("hub machine set. ");
			}

		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	/** write test objects in mongo for hub only 
	 */
	private void storeSerializedTestObjectInMongo() {
		try {

			if(SingletonTestObject.getSingletonTestObject().isHubMachine()) {

				/** serialize test objects as json */
				List<TestCaseObject> listOfTestCaseObject = SingletonTestObject.getSingletonTestObject().getTestCaseObjectList();

				/** store test object in mongo */
				new MongoDBHandler().storeTestCaseObjectToMongo_MongoJack(listOfTestCaseObject, 
						SingletonTestObject.getSingletonTestObject().getMongoDBClientConnnection());

				logger.info("mongo instance is ready with test objects ... ");
			}
			else {
				logger.info("not writing test objects in mongo coz its not hub, node ? "+SingletonTestObject.getSingletonTestObject().isNodeMachine());
			}
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/** set the parallel executor for node and hub machine 
	 */
	private void setExecutorSize() {
		try {
			int count =0 ;

			/** set size in case of scale via mongo is set to Yes -- for Grid */
			if(SingletonTestObject.getSingletonTestObject().isScale_execution_via_mongo()) {

				TreeMap<String, Integer> map= new TreeMap<>();

				if(SingletonTestObject.getSingletonTestObject().isHubMachine()) {				
					map = new GetGridConfiguration().getGridConfiguration().getHub().getExecutor_size();					
				}

				if(SingletonTestObject.getSingletonTestObject().isNodeMachine()) {
					map = SingletonTestObject.getSingletonTestObject().getNodeConfiguration().getExecutor_size();
				}

				/** look for channel type else go by default numbers */
				if(map.containsKey(SingletonTestObject.getSingletonTestObject().getChannel_type())) {
					count = map.get(SingletonTestObject.getSingletonTestObject().getChannel_type());
				}else {
					count = map.get("default");
				}

				logger.info("setting up mongo executor capacity: "+count + " for channel: "+SingletonTestObject.getSingletonTestObject().getChannel_type());
			}

			/** set size in case of scale via mongo is set to NO then initialize for local execution */
			else {

				if(SingletonTestObject.getSingletonTestObject().getChannel_type().equalsIgnoreCase("vsm")) {
					count = Integer.parseInt(GenericMethodsLib.generalConfigurationProperties.getProperty("vsm_executorCapacity").toString().trim());

				}else if(SingletonTestObject.getSingletonTestObject().getChannel_type().equalsIgnoreCase("mobile")) {

					try{count = Integer.parseInt(GenericMethodsLib.generalConfigurationProperties.getProperty("mobile_executorCapacity").toString().trim());}
					catch (Exception e) {
						
						/** if key is not there then fall back to  executorCapacity*/
						count = Integer.parseInt(GenericMethodsLib.generalConfigurationProperties.getProperty("executorCapacity").toString().trim());
					}
				}
				else {
					count = Integer.parseInt(GenericMethodsLib.generalConfigurationProperties.getProperty("executorCapacity").toString().trim());
				}

				logger.info("setting up local executor capacity: "+count + " for channel: "+SingletonTestObject.getSingletonTestObject().getChannel_type());
			}

			/** exit code is executor size is not decided */
			if(count< 1) {
				logger.info("********** Executor Size is not defined. ********");
				System.exit(1);
			}

			/** setting up test executor size */
			ExecutorService testExecutor=Executors.newFixedThreadPool(count);
			SingletonTestObject.getSingletonTestObject().setTestExecutorService(testExecutor);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * calling each supplied node urls
	 */
	private void callNodeUrls() {

		for(Node node : new GetGridConfiguration().getGridConfiguration().getNodes()) {
			try {
				logger.info(httpClientWrap.sendPostRequest(getNodeURLWithParameters(node)));
			}catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/** This method will append the parameters required to be set before calling a node server.
	 * 
	 * @param node
	 * @return
	 */
	private String getNodeURLWithParameters(Node node) {

		String remote_url= "";
		try {
			remote_url = node.getRemote_url();
			String [] parametersArray = node.getParameters().split(",");

			for(int i=0; i<parametersArray.length; i++) {
				String name = parametersArray[i];
				
				/** node url may require some parameters, these parameters are supplied via maven property variables from hub machine. */
				String value = System.getProperty(name);

				if(value == null) {
					value = "";
				}
				remote_url = remote_url.concat("&"+name+"="+value);
			}
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.info("Final Remote URL Of Node: "+remote_url);
		return remote_url;
	}


	/** set up mongodb collection object 
	 * 
	 * @return
	 */
	private JacksonDBCollection<TestCaseObject, String> getMongoDbCollection()
	{
		JacksonDBCollection<TestCaseObject, String> collection = null;
		try
		{
			DB db = SingletonTestObject.getSingletonTestObject().getMongoDBClientConnnection().getDB("Automation");
			collection = JacksonDBCollection.wrap(
					db.getCollection("testcaseobjects"), 
					TestCaseObject.class,
					String.class);

			logger.info("Mongo collection object is setup .. ");
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return collection;
	}


}
