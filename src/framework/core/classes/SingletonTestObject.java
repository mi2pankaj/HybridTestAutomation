package framework.core.classes;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.json.JSONObject;
import org.mongojack.JacksonDBCollection;

import com.mongodb.MongoClient;
import com.mysql.jdbc.Connection;

import framework.core.grid.configuration.Node;

public class SingletonTestObject {

	/** The object of this singleton class must be set and filled by the test classes like LenskartWebTest etc. 
	 */
	private static SingletonTestObject singletonTestObject;
	private ConcurrentHashMap<String, TestCaseObject> testCaseObjectMap;
	private List<TestCaseObject> testCaseObjectList;
	private Connection mysqlDbConnection;
	private MongoClient mongoDBClientConnnection;
	private boolean scale_execution_via_mongo; 
	private boolean ifNodeMachine;
	private boolean ifHubMachine;
	private Node nodeConfiguration;
	private ExecutorService testExecutorService;
	private ExecutorService monitoringExecutorService;
	private JacksonDBCollection<TestCaseObject, String> mongoDBCollection;
	private JSONObject jsonObjectRepository;

	/** lenskart specific */
	private String channel_type; 

	static {
		singletonTestObject = new SingletonTestObject();
	}

	/** making this constructor private to make this class singleton */
	private SingletonTestObject() {
		System.out.println("Private Constructor Set ... ");
	}

	/** return the singleton object */
	public static SingletonTestObject getSingletonTestObject() {
		return singletonTestObject;
	}

	public List<TestCaseObject> getTestCaseObjectList() {
		return testCaseObjectList;
	}

	public void setTestCaseObjectList(List<TestCaseObject> testCaseObjectList) {
		this.testCaseObjectList = testCaseObjectList;
	}

	public ConcurrentHashMap<String, TestCaseObject> getTestCaseObjectMap() {
		return testCaseObjectMap;
	}

	public void setTestCaseObjectMap(ConcurrentHashMap<String, TestCaseObject> testCaseObjectMap) {
		this.testCaseObjectMap = testCaseObjectMap;		
	}

	public Connection getMysqlDbConnection() {
		return mysqlDbConnection;
	}

	public void setMysqlDbConnection(Connection mysqlDbConnection) {
		this.mysqlDbConnection = mysqlDbConnection;
	}

	public MongoClient getMongoDBClientConnnection() {
		return mongoDBClientConnnection;
	}

	public void setMongoDBClientConnnection(MongoClient mongoDBClientConnnection) {
		this.mongoDBClientConnnection = mongoDBClientConnnection;
	}

	public boolean isScale_execution_via_mongo() {
		return scale_execution_via_mongo;
	}

	public void setScale_execution_via_mongo(boolean scale_execution_via_mongo) {
		this.scale_execution_via_mongo = scale_execution_via_mongo;
	}

//	public ExecutorService getExecutorService() {
//		return executorService;
//	}
//
//	public void setExecutorService(ExecutorService executorService) {
//		this.executorService = executorService;
//	}

	public boolean isNodeMachine() {
		return ifNodeMachine;
	}

	public void setIfNodeMachine(boolean ifNodeMachine) {
		this.ifNodeMachine = ifNodeMachine;
	}

	public boolean isHubMachine() {
		return ifHubMachine;
	}

	public void setIfHubMachine(boolean ifHubMachine) {
		this.ifHubMachine = ifHubMachine;
	}

	public Node getNodeConfiguration() {
		return nodeConfiguration;
	}

	public void setNodeConfiguration(Node nodeConfiguration) {
		this.nodeConfiguration = nodeConfiguration;
	}

	public String getChannel_type() {
		return channel_type;
	}

	public void setChannel_type(String channel_type) {
		this.channel_type = channel_type;
	}

	public JacksonDBCollection<TestCaseObject, String> getMongoDBCollection() {
		return mongoDBCollection;
	}

	public void setMongoDBCollection(JacksonDBCollection<TestCaseObject, String> mongoDBCollection) {
		this.mongoDBCollection = mongoDBCollection;
	}

	public ExecutorService getMonitoringExecutorService() {
		return monitoringExecutorService;
	}

	public void setMonitoringExecutorService(ExecutorService monitoringExecutorService) {
		this.monitoringExecutorService = monitoringExecutorService;
	}

	public ExecutorService getTestExecutorService() {
		return testExecutorService;
	}

	public void setTestExecutorService(ExecutorService testExecutorService) {
		this.testExecutorService = testExecutorService;
	}

	public JSONObject getJsonObjectRepository() {
		return jsonObjectRepository;
	}

	public void setJsonObjectRepository(JSONObject jsonObjectRepository) {
		this.jsonObjectRepository = jsonObjectRepository;
	}

}
