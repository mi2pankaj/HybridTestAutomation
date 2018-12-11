package core.utilities;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import core.classes.SingletonTestObject;
import core.classes.TestCaseObject;
import tests.SuiteClass;

public class MongoDBHandler {

	// forwarding ports
	private static final String LOCAL_HOST = "localhost";
	private static final String REMOTE_HOST = ""; 
	private static final Integer LOCAL_PORT = 8988;
	private static final Integer REMOTE_PORT = 27017;

	// ssh connection info
	private static final String SSH_USER = "NONE";
	private static final String SSH_PASSWORD = "NONE";
	private static final String SSH_HOST = "HOST_IP";
	private static final Integer SSH_PORT = 22;

	private static Session SSH_SESSION;

	Logger logger = Logger.getLogger(MongoDBHandler.class.getName());


	public static void main(String[] args) throws JSchException{
		//		MongoDBHandler obj = new MongoDBHandler();
		//		obj.connectMongo();


		new MongoDBHandler().connectMongoUpdated();
	}

	public void connectMongo() throws JSchException{
		try{
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");

			JSch jsch = new JSch();
			SSH_SESSION = null;
			SSH_SESSION = jsch.getSession(SSH_USER, SSH_HOST, SSH_PORT);
			SSH_SESSION.setPassword(SSH_PASSWORD);
			SSH_SESSION.setConfig(config);
			SSH_SESSION.connect();
			SSH_SESSION.setPortForwardingL(LOCAL_PORT, REMOTE_HOST, REMOTE_PORT);

			MongoClient mongoClient = new MongoClient(LOCAL_HOST, LOCAL_PORT);
			//			mongoClient.setReadPreference(ReadPreference.nearest());

			MongoCursor<String> dbNames = mongoClient.listDatabaseNames().iterator();
			while (dbNames.hasNext()) {
				System.out.println(dbNames.next());
			}	
			mongoClient.close();

		}catch(Exception e){
			System.out.println("exception occurred ="+ e);
		}
		//		finally {
		//			SSH_SESSION.delPortForwardingL(LOCAL_PORT);
		//			SSH_SESSION.disconnect();
		//		}

	}

	public void connectMongoUpdated(){

		try{
			//			java.util.Properties config = new java.util.Properties();
			//			config.put("StrictHostKeyChecking", "no");
			//
			//			JSch jsch = new JSch();
			//			SSH_SESSION = null;
			//			SSH_SESSION = jsch.getSession(SSH_USER, SSH_HOST, SSH_PORT);
			//			SSH_SESSION.setPassword(SSH_PASSWORD);
			//			SSH_SESSION.setConfig(config);
			//			SSH_SESSION.connect();
			//			SSH_SESSION.setPortForwardingL(LOCAL_PORT, REMOTE_HOST, REMOTE_PORT);

			MongoClient mongoClient = new MongoClient("<IP>", 27017);

			MongoCursor<String> dbNames = mongoClient.listDatabaseNames().iterator();
			while (dbNames.hasNext()) {
				System.out.println(dbNames.next());
			}	
			mongoClient.close();

		}catch(Exception e){
			System.out.println("exception occurred ="+ e);
		}

	}

	/** get mongodb client instance 
	 * 
	 * @return
	 */
	public MongoClient getMongoClient_Obsolete(){
		MongoClient mongoClient = null;

		try{
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");

			JSch jsch = new JSch();
			SSH_SESSION = null;
			SSH_SESSION = jsch.getSession(SSH_USER, SSH_HOST, SSH_PORT);
			SSH_SESSION.setPassword(SSH_PASSWORD);
			SSH_SESSION.setConfig(config);
			SSH_SESSION.connect();
			SSH_SESSION.setPortForwardingL(LOCAL_PORT, REMOTE_HOST, REMOTE_PORT);

			mongoClient = new MongoClient(LOCAL_HOST, LOCAL_PORT);
			//mongoClient.setWriteConcern(WriteConcern.NORMAL);

			logger.info("Mongo client instance is acquired. ");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}

		return mongoClient;
	}

	public MongoClient getMongoClient(){
		MongoClient mongoClient = null;

		try{
			CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
					fromProviders(PojoCodecProvider.builder().automatic(true).build()));
			mongoClient = new MongoClient("<IP>", MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());

			logger.info("Mongo client instance is acquired. ");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}

		return mongoClient;
	}

	public void closeMongoConnection() throws JSchException{
		SSH_SESSION.delPortForwardingL(LOCAL_PORT);
		SSH_SESSION.disconnect();	
	}

	/** This method will store the supplied json array of test object in mongo db 
	 */
	public boolean storeSerailzedTestObjectsJson_MongoDb(List<TestCaseObject> serializedTestObjectJson, MongoClient mongoClient) {
		try
		{
			logger.info("started writing serialized object in mongodb .. ");

			JSONArray jsonArray= new JSONArray(serializedTestObjectJson);
			MongoDatabase db = mongoClient.getDatabase("Automation");
			db.getCollection("testcaseobjects").drop();

			/** drop existing collection and create new one */
			db.getCollection("testcaseobjects").drop();
			db.createCollection("testcaseobjects");

			for(int i=0; i<jsonArray.length(); i++){
				JSONObject json = (JSONObject) jsonArray.get(i);
				Document doc = Document.parse(json.toString());
				db.getCollection("testcaseobjects").insertOne(doc);
			}

			logger.info("done - writing serialized object in mongodb .. ");

			return true;
		}catch (Exception e) {
			logger.error(e.getMessage(), e);

			return false;
		}
	}

	/** This method will store the supplied json array of test object in mongo db 
	 */
	public boolean storeTestCaseObjectToMongo_MongoJack(List<TestCaseObject> listTestCaseObject, MongoClient mongoClient) {
		try
		{
			logger.info("started writing serialized object in mongodb .. ");
			SingletonTestObject.getSingletonTestObject().getMongoDBCollection().drop();	
			SingletonTestObject.getSingletonTestObject().getMongoDBCollection().insert(listTestCaseObject);

			logger.info("done - writing serialized object in mongodb .. ");
			return true;

		}catch (Exception e) {
			logger.error(e.getMessage(), e);

			return false;
		}
	}

	/** get the records from mongo db from the supplied query 
	 * 
	 * @param mongoClient
	 * @param query
	 * @return
	 */
	public FindIterable<Document> getMongoDbDocument(MongoClient mongoClient, String query) {

		try
		{
			MongoDatabase db = mongoClient.getDatabase("Automation");

			/**search a document in mongo db */ 
			FindIterable<Document> document;

			if(!query.isEmpty()) {
				document= db.getCollection("testcaseobjects").find(Document.parse(query));
			}else {
				document= db.getCollection("testcaseobjects").find();
			}

			return document;

		}catch (Exception e) {
			logger.error("Error occurred while getting document from mongodb. ", e);

			return null;
		}
	}

	/** get the records from mongo db from the supplied query 
	 * 
	 * @param mongoClient
	 * @param query
	 * @return
	 */
	public DBCursor<TestCaseObject> getMongoDbDocumentMongoJack(String fieldName, AtomicInteger value) {

		try
		{
			/**Get the Query result from the mongo */
			DBCursor<TestCaseObject> listofTestCases= SingletonTestObject
					.getSingletonTestObject()
					.getMongoDBCollection()
					.find(new BasicDBObject(fieldName,value.get()));

			return listofTestCases;

		}catch (Exception e) {
			logger.error("Error occurred while getting document from mongodb. ", e);

			return null;
		}
	}

	/** update values of document in mongodb.
	 * 
	 * @param mongoClient
	 * @param filterMap
	 * @param updatedDocumentMap
	 * @return
	 */
	public boolean updateMongoDBDocument(MongoClient mongoClient, Map<String, Object> filterMap, Map<String, Object> updatedDocumentMap) {

		try
		{
			/** first create the search filter  */ 
			Bson filter = new Document(filterMap);

			/** set the new value */
			Bson updatedDocument = new Document(updatedDocumentMap);

			/** set the updated document */
			Bson updateOperation = new Document("$set", updatedDocument);

			/** update the document now */
			mongoClient.getDatabase("Automation").getCollection("testcaseobjects").updateOne(filter, updateOperation);

			return true;

		}catch (Exception e) {

			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " - error occurred while updating mongodb document. " + e.getMessage(), e);
			return false;
		}
	}

	/** replace an existing mongodb document based on supplied search query.
	 * 
	 * @param mongoClient
	 * @param filterMap
	 * @param jsonDocument
	 * @return
	 */
	public boolean replaceMongoDbDocument(MongoClient mongoClient, Map<String, Object> filterMap, String jsonDocument) {
		try
		{
			/** first create the search filter  */ 
			Bson filter = new Document(filterMap);
			/** update the document now */
			mongoClient.getDatabase("Automation").getCollection("testcaseobjects").replaceOne(filter, Document.parse(jsonDocument));

			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " - replacing mongodb document. " +filterMap.toString());

			return true;
		}catch (Exception e) {

			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " - error occurred while replacing mongodb document. " + e.getMessage(), e);
			return false;
		}
	}

	/** replace an existing mongodb document based on supplied search query using mongoJack.
	 * 
	 * @param mongoClient
	 * @param filterMap
	 * @param jsonDocument
	 * @return
	 */
	public boolean replaceMongoDbDocumentMongoJack(String fieldName, String value, TestCaseObject testCaseObject) {
		try
		{
			/**Updating the document*/
			SingletonTestObject.getSingletonTestObject().getMongoDBCollection().update(DBQuery.is(fieldName, value), testCaseObject);
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " - replacing mongodb document. " + value);

			return true;
		}catch (Exception e) {

			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " - error occurred while replacing mongodb document. " + e.getMessage(), e);
			return false;
		}
	}

}
