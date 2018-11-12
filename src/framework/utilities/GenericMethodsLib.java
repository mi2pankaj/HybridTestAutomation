/**
 * Last Changes Done on Jan 23, 2015 3:45:57 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: Implemented logger, added support for rtb_win and rtb_bp trackers for hudson requests
 */

package framework.utilities;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import lenskart.tests.DataObject;
import lenskart.tests.OrderDetails;
import lenskart.tests.TestSuiteClass;

import org.apache.commons.configuration.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

import framework.core.classes.SingletonTestObject;
import framework.core.classes.TestCaseObject;


public class GenericMethodsLib 
{

	static Logger logger = Logger.getLogger(GenericMethodsLib.class.getName());

	public static PropertiesConfiguration generalConfigurationProperties;
	public static PropertiesConfiguration googleConfigurationProperties;


	/**
	 * Initializing Configuration File
	 */
	public static void InitializeConfiguration()  
	{	
		try
		{
			generalConfigurationProperties = new PropertiesConfiguration();
			String varAutomationHome = TestSuiteClass.AUTOMATION_HOME;

			/** Now we will add path to conf folder and qaconf.properties is the file which will be needed to fetch the configurations. */
			String config = varAutomationHome.concat("/properties/generalConfiguration.properties");
			generalConfigurationProperties.load(config);
			
			/** get roster mobile number from google sheet and then load it it general config property */
			String phoneNo=lenskart.tests.MobileNoExtract.getRosterMobile();
		
			if(!phoneNo.isEmpty()){
				generalConfigurationProperties.setProperty("guest_mobile_number", phoneNo);
			}
			
			/** loading google properties also */
			logger.info("Final Guest Mobile Number --> "+generalConfigurationProperties.getProperty("guest_mobile_number"));
			
			googleConfigurationProperties = new PropertiesConfiguration();
			googleConfigurationProperties.load(TestSuiteClass.AUTOMATION_HOME+"/properties/googleConfiguration.properties");
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception Error occurred While Reading Config File, Ensure that Config file is at the mentioned path. ", e);
		}
	}


	//********** Establishing JDBC Connection to Mysql database: *********************************************//
	public static Connection CreateSQLConnection()  
	{
		Connection qaConnection = null;
		try
		{
			GenericMethodsLib.InitializeConfiguration();

			String dbClass = "com.mysql.jdbc.Driver";		
			Class.forName(dbClass);

			// Getting Values for dburl,dbUsername and dbPassword from configuration file
			String dburl = generalConfigurationProperties.getProperty("dbURL").toString();
			String dbuserName = generalConfigurationProperties.getProperty("dbUserName").toString();
			String dbpassword = generalConfigurationProperties.getProperty("dbPassword").toString();

			qaConnection = (Connection) DriverManager.getConnection (dburl,dbuserName,dbpassword);
		}
		catch(NullPointerException e)
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : NullPointerException Handled By Method CreateSQLConnection, Plz check Config Values or Initialize Config by calling Method - InitializeConfiguration", e);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while creating sql connection. ", e);
		}
		//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : SQL Connection Was Made Successfully By Method CreateSQLConnection: " +url + " ; " + userName + " ; " +password);
		return qaConnection;
	}


	//********** Executing MySQL Query and Returning Result Set: *********************************************//
	public static ResultSet ExecuteMySQLQueryReturnsResultSet(Connection con, String sqlQuery) throws SQLException 
	{		
		try{
			Statement stmt = (Statement) con.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sqlQuery);
			return rs;
		}catch(MySQLSyntaxErrorException m){
			logger.error(m.getMessage());
			return null;
		}
	}

	//********** Executing MySQL Query and Returning 2 D Array containing the Result Set without Column Name) *********************************************//
	public static String [][] ExecuteMySQLQueryReturnsArray(Connection con, String sqlQuery) 
	{		

		String [][]arrayRecords = null;

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			rs.last();	// Setting the cursor at last
			int rows = rs.getRow();
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : rows in result set: " +rows);

			int columns = rs.getMetaData().getColumnCount();
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :  Column Count: "+columns);

			arrayRecords = new String[rows][columns];

			rs.beforeFirst();	// Setting the cursor at first line	
			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					String strRecord = rs.getString(i).toString();
					arrayRecords[rs.getRow()-1][i-1] = strRecord;
					//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Writing Rows BY METHOD - ExecuteMySQLQueryReturnsArray: " +strRecord);
					//}
				}
				//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : ");
			}			
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : MySQL Data Was Successfully Exported By Method ExecuteMySQLQueryReturnsArray. Rows: " +arrayRecords.length + ", Columns: "+arrayRecords[0].length);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturnsArray:", e);
		}
		catch (NullPointerException e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : NullPointerExpection Handled By: ExecuteMySQLQueryReturnsArray", e);
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Used MySQL query may have returned a NULL column in Result Set, Therefore use IFNULL with that particular column in query.", e);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Expection Handled By: ExecuteMySQLQueryReturnsArray", e);
		}

		return arrayRecords;
	}



	//********** Executing MySQL Query and Returning 1 D Array containing the Result Set without Column Name *********************************************//
	public static String [] ExecuteMySQLQueryReturns1DArray(Connection con, String sqlQuery) 
	{		
		String []arrayRecords = null;

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			if(rs !=null)
			{
				int columns = rs.getMetaData().getColumnCount();
				//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :  Column Count: "+columns);

				arrayRecords = new String[columns];

				rs.beforeFirst();	// Setting the cursor at first line	
				while (rs.next())
				{
					for(int i=1;i<=columns;i++)
					{
						String strRecord = rs.getString(i).toString();
						arrayRecords[i-1] = strRecord;
						//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Writing Rows BY METHOD - ExecuteMySQLQueryReturns1DArray: " +arrayRecords[i-1]);
					}
				}	
			}
			else
			{
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received NULL record set for the supplied query: "+sqlQuery);
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturns1DArray:", e);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception Handled By: ExecuteMySQLQueryReturns1DArray. ", e);
		}

		return arrayRecords;
	}


	/** Executing MySQL Query and Returning 1 D Array containing the Result Set without Column Name 
	 * 
	 * @param con
	 * @param sqlQuery
	 * @return
	 */
	@SuppressWarnings("finally")
	public static List<String> ExecuteMySQLQueryReturnsList(Connection con, String sqlQuery)
	{		
		List<String> recordList = new ArrayList<String>();

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			int columns = rs.getMetaData().getColumnCount();

			rs.beforeFirst();	// Setting the cursor at first line	
			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					String strRecord = rs.getString(i).toString().trim();
					recordList.add(strRecord);
				}
			}		
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturns1DArray:", e);
		}
		catch (NullPointerException e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : NullPointerExpection Handled By: ExecuteMySQLQueryReturns1DArray", e);
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Used MySQL query may have returned a NULL column in Result Set, Therefore use IFNULL with that particular column in query.", e);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Expection Handled By: ExecuteMySQLQueryReturnsList. " ,e);
		}
		finally
		{
			return recordList;
		}
	}


	//********** Executing MySQL Query and Returning 1 D Array containing the Only Column Name Of Result Set *********************************************//
	public static String [] ExecuteMySQLQueryReturnsOnlyColumnNames(Connection con, String sqlQuery) throws SQLException
	{		
		String []arrayRecords = null;

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			int columns = rs.getMetaData().getColumnCount();
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :  Column Count: "+columns);

			arrayRecords = new String[columns];

			rs.beforeFirst();	// Setting the cursor at first line	
			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					String strRecord = rs.getMetaData().getColumnLabel(i).toString();
					arrayRecords[i-1] = strRecord;
					//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Writing Rows BY METHOD - ExecuteMySQLQueryReturnsOnlyColumnNames: " +strRecord);
				}
			}		
			con.close();			
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturnsOnlyColumnNames:", e);
		}
		catch (NullPointerException e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : NullPointerExpection Handled By: ExecuteMySQLQueryReturnsOnlyColumnNames", e);
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Used MySQL query may have returned a NULL column in Result Set, Therefore use IFNULL with that particular column in query.",e);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Expection Handled By: ExecuteMySQLQueryReturnsOnlyColumnNames. ", e);
		}

		return arrayRecords;
	}



	//********** Executing MySQL Query and Returning 2 D Array containing the Result Set with Column Name) *********************************************//
	public static String [][] ExecuteMySQLQueryReturnsArrayWithColumnName(Connection con, String sqlQuery) 
	{		
		String [][]arrayRecords = null;
		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Running this query: "+sqlQuery);

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);
			/*
			//Un-comment this for debugging
			while (rs.next())
			{
				for(int i=1;i<=rs.getMetaData().getColumnCount();i++)
				{
					String strRecord = rs.getString(i).toString();
					System.out.print(" : "+strRecord);
				}
				logger.info();
			}
			 */
			rs.last();	// Setting the cursor at last
			int rows = rs.getRow();
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : rows in result set: " +rows);

			int columns = rs.getMetaData().getColumnCount();
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :  Column Count: "+columns);

			arrayRecords = new String[rows+1][columns];

			rs.beforeFirst();	// Setting the cursor at first line

			while (rs.next())
			{
				int currentRow = rs.getRow();

				for(int i=1;i<=columns;i++)
				{
					if(currentRow == 1)
					{
						String strRecord = rs.getMetaData().getColumnLabel(i).toString();
						arrayRecords[currentRow-1][i-1] = strRecord;
						//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Column Label: " +strRecord);

						String strRecord_1 = rs.getString(i).toString();
						arrayRecords[currentRow][i-1] = strRecord_1;
						//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Record: " +strRecord_1);
					}
					else
					{
						String strRecord = rs.getString(i).toString();
						arrayRecords[currentRow][i-1] = strRecord;
						//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : record in result set: " +strRecord);
					}
				}

			}					
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : MySQL Data Was Successfully Exported By Method ExecuteMySQLQueryReturnsArray. Rows: " +arrayRecords.length + ", Columns: "+arrayRecords[0].length);

		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturnsArray:", e);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception Handled by Method : ExecuteMySQLQueryReturnsArray: ",e);
		}

		/*
		// Only for debugging
		for(int i=0; i<arrayRecords.length; i++)
		{
			for(int j=0; j<arrayRecords[0].length; j++)
			{
				System.out.print(" : " +arrayRecords[i][j]);
			}
			logger.info();
		}
		 */

		return arrayRecords;
	}



	//******************** Get Current Date Time Stamp *************************************************//
	public static String DateTimeStamp(String dateStampFormat)
	{
		try
		{
			//Sample: MMddyyyy_hhmmss
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(dateStampFormat);
			String formattedDate = sdf.format(date);
			return formattedDate;
		}
		catch(Exception n)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Please check the supplied date format. " , n);
			return null;
		}
	}



	//******************** Writing The Date Time Stamp *************************************************//
	public static String DateTimeStampWithMiliSecond()
	{
		try
		{
			String dateStampFormat = "MMddyyyy_hhmmss_ms";
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Date Time Stamp Format will be:" +dateStampFormat);

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(dateStampFormat);
			String formattedDate = sdf.format(date);
			return formattedDate;
		}
		catch(Exception n)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception handled by method: DateTimeStampWithMiliSecond. ", n);
			return null;
		}
	}


	/**
	 * kill emulator 
	 * @param deviceUDID
	 */
	public void killEmulator (String deviceUDID)
	{
		try{
			String command = "";

			/** kill emulator */
			if(System.getProperty("os.name").contains("Window")){
				command = System.getenv("ANDROID_HOME").concat("\\platform-tools\\adb.exe -s " + deviceUDID+ " emu kill");		
				ExecuteCommands.ExecuteCommand_ReturnsOutput(new String[]{"cmd", "/c", command});
			}
			else{
				command = System.getenv("ANDROID_HOME").concat("/platform-tools/adb -s " + deviceUDID+ " emu kill");
			}

			try{Runtime.getRuntime().exec(command);}catch (Exception e) {}
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " killed emulator - "+command);
		}
		catch(Exception e){
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" -- " + e.getMessage(), e);
		}
	}

	/**
	 *  kill a service running on a specific port.
	 * @param port
	 */
	public void killProcess (int port)
	{
		try{

			if(System.getProperty("os.name").contains("Window")){
				String command = "FOR /F \"usebackq tokens=5\" %a in (`netstat -nao ^| findstr /R /C:\""+port+" \"`) do (FOR /F \"usebackq\" %b in (`TASKLIST /FI \"PID eq %a\" ^| findstr /I chromedriver.exe`) do (IF NOT %b==\"\" TASKKILL /F /PID %a))";
				try{Runtime.getRuntime().exec(command);}catch (Exception e) {}
			}
			else{
				try{Runtime.getRuntime().exec("lsof -ti:"+port+" | xargs kill");}catch (Exception e) {}
			}

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " killed process running on port:  "+port);
		}
		catch(Exception e){
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" -- " + e.getMessage(), e);
		}
	}


	/** This method cleans all the process.
	 * 
	 * @param suiteStartTime
	 */
	public static void cleanProcesses()
	{
		/** close mysql db connection */
		try{SingletonTestObject.getSingletonTestObject().getMysqlDbConnection().close();}catch (Exception e) {	}

		logger.info("Shutting down executors ... ");
		SingletonTestObject.getSingletonTestObject().getTestExecutorService().shutdownNow();
		SingletonTestObject.getSingletonTestObject().getMonitoringExecutorService().shutdownNow();

		/** close mongodb connection*/
		logger.info("closing mongo db connection ");
		try {SingletonTestObject.getSingletonTestObject().getMongoDBClientConnnection().close();}catch (Exception e) {}

		List<String> windowsProcesses = Arrays.asList("chromedriver.exe", "chrome.exe", "node.exe", "adb.exe", "qemu-system-i386.exe");
		List<String> macProcesses = Arrays.asList("chromedriver", "chrome", "node", "adb", "qemu-system-i386");

		/** Close all the remaining instance of the browser. */
		if(System.getProperty("os.name").contains("Window")){
			for(String process : windowsProcesses) {
				try{Runtime.getRuntime().exec("taskkill /F /IM "+process);}catch (Exception e) {}
				ExecuteCommands.ExecuteCommand_ReturnsOutput(new String[]{"cmd", "/c", "taskkill /F /IM "+process });
			}
		}else{
			for(String process : macProcesses) {
				ExecuteCommands.ExecuteMacCommand_ReturnsOutput("killall "+process);
			}
		}
	}

	/**
	 * create avd dynamically 
	 * 
	 * @param deviceName
	 */
	public void createAVDDynamically(String deviceName)
	{
		try
		{
			String androidHome = System.getProperty("ANDROID_HOME");
			String command = androidHome+"/tools/bin/avdmanager create avd -n " + deviceName
					+ " -k \"system-images;android-25;google_apis;x86\" -b x86 -c 100M -f --device \"Nexus 5X\" "; 

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " executing command: "+command);
			if(System.getProperty("os.name").contains("Window")) {
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : "+ ExecuteCommands.ExecuteCommand_ReturnsOutput(new String[]{"cmd", "/c", command }));
			}else {
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : "+ ExecuteCommands.ExecuteMacCommand_ReturnsOutput(command));
			}
		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " error occurred while creating AVD "+deviceName, e);
		}
	}


	/** delete an existing avd dynamically
	 * 
	 * @param deviceName
	 */
	public void deleteAVDDynamically(String deviceName)
	{
		try
		{
			String androidHome = System.getProperty("ANDROID_HOME");
			String command = androidHome+"/tools/bin/avdmanager delete avd -n " + deviceName;

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " executing command: "+command);
			if(System.getProperty("os.name").contains("Window")) {
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : "+ ExecuteCommands.ExecuteCommand_ReturnsOutput(new String[]{"cmd", "/c", command }));
			}else {
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : "+ ExecuteCommands.ExecuteMacCommand_ReturnsOutput(command));
			}
		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " error occurred while deleting AVD "+deviceName, e);
		}
	}


	public static String getDateInString(String data){
		String date=null;
		Date currentDate=null;
		String daysToAdd=null;
		try{
			daysToAdd=data.substring(data.indexOf("add")+3, data.indexOf("days")).trim();
			DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
			currentDate= new Date();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(currentDate);
			calendar.add(Calendar.DATE, Integer.parseInt(daysToAdd));
			Date nextdate=calendar.getTime();
			date=dateFormat.format(nextdate);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Get the new date as per the input data :"+date);
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to get the date", e);
		}
		return date;
	}


	/**
	 * Method is used to generate list of hashmap from the json string 
	 * @return
	 */
	public static List<HashMap<String, String>> listOfMapFromJson(String json){

		List<HashMap<String, String>> listOfMap=new ArrayList<>();		

		try{
			/** get json array from de-serialized object */
			JSONArray jsonArray =new JSONArray(json);

			for(int i=0; i<jsonArray.length(); i++){

				HashMap<String, String> hashmap=new HashMap<>();

				/** get each josn obj of json arr and store it in a hashmap */
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Iterator<?> keys = jsonObject.keys();

				while (keys.hasNext()) {
					String key = (String)keys.next();
					String value =jsonObject.getString(key);
					hashmap.put(key, value);
				}

				listOfMap.add(hashmap);
			}
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Succesfully stored dataobject into list Of hashmap:");	
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to get the list of hashmap", e);
		}
		return listOfMap;
	}


	/**
	 * Method is used to generate list of hashmap from the json string 
	 * @return
	 */
	public static List<HashMap<String, String>> listOfMapFromJson(String json, List<TestCaseObject> listTestCaseObjects){

		List<HashMap<String, String>> listOfMap=new ArrayList<>();		

		try{

			/** get json array from de-serialized object */
			JSONArray jsonArray =new JSONArray(json);
			TestCaseObject testCaseObjects=new TestCaseObject();

			for(int j=0; j<listTestCaseObjects.size(); j++){

				testCaseObjects= (TestCaseObject) listTestCaseObjects.get(j).clone();
				String tcKey="TC_ID";
				String tcValue=testCaseObjects.getTestCaseId();

				for(int i=0; i<jsonArray.length(); i++){
					HashMap<String, String> hashmap=new HashMap<>();
					hashmap.put(tcKey, tcValue);

					/** get each josn obj of json arr and store it in a hashmap */
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Iterator<?> keys = jsonObject.keys();

					while (keys.hasNext()) {
						String key = (String)keys.next();
						String value =jsonObject.getString(key);
						hashmap.put(key, value);
					}

					/**  here orders will load  which start from 12 and their vasm_execution flag false and null  - */
					String order_ids=hashmap.get("order_Id");
					if(order_ids.startsWith("12")) {
						if( hashmap.get("vsm_Execution").equalsIgnoreCase("null")
								|| hashmap.get("vsm_Execution").equalsIgnoreCase("false")){

							/** making a unique test case id - to be utilized later on in final test object hash map */
							System.out.println("order_id: "+order_ids);
							listOfMap.add(hashmap);	
						}
					}
				}
			}
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Succesfully stored dataobject into list Of hashmap, final length: "+listOfMap.size());

		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to get the list of hashmap", e);
		}
		return listOfMap;
	}

	/** return the de-serialized object
	 * 
	 * @param objectPath
	 * @return
	 */
	public static OrderDetails deserializeObject(String objectPath) {

		OrderDetails orderDetailObj = null;

		try{
			/** de-serailze object */
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(new File(objectPath)));
			orderDetailObj =(OrderDetails) ois.readObject();
			ois.close();

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Succesfully deserializeObject:");	
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to deserializeObject", e);
		}
		return orderDetailObj;
	}


	/** return the de-serialized object
	 * 
	 * @param objectPath
	 * @return
	 */
	public static List<HashMap<String, String>> dataObjectToListOfMap(List<TestCaseObject> listTestcasesObject) {

		List<HashMap<String, String>> listOfMap=new ArrayList<>();	
		try{
			/** de-serialize object - commenting out the de-serailized code */
			//OrderDetails orderDetailObj=deserializeObject(objectPath);
			//String jsonString=orderDetailObj.getOrderDetail();

			/** now reading json tree using jackson mapper with this -- those keys which are not supplied from test cases 
			 * but are present in DataObject Class mapping - will be passed as null - for example if VSM_Execution is not passed but DataObject class
			 * has this declaration then final json will have "VSM_Execution" : null */
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.readTree(new File(TestSuiteClass.AUTOMATION_HOME+"/dataObject")).toString();

			/** get list of map from json*/
			listOfMap=listOfMapFromJson(jsonString,listTestcasesObject);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Succesfully deserializeObject:");

		}catch (Exception e) {
			// TODO: handle exception
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to deserializeObject", e);
		}
		return listOfMap;
	}


	/**
	 * Method to get merged JSON object from 2 json and
	 */

	public static JSONObject mergeJSONObject(JSONObject json1, JSONObject json2){
		JSONObject mergedJSONObject=null;
		JSONObject jsonObject1=null;
		JSONObject jsonObject2=null;
		try{
			jsonObject1=json1;
			jsonObject2=json2;
			mergedJSONObject=jsonObject2;

			Iterator<?> keys=json1.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();

				try{
					jsonObject2.get(key);
				}catch(JSONException e){

					mergedJSONObject.put(key, jsonObject1.get(key));
					//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : JSON object added is="+mergedJSONObject.get(key) +" for key: "+key);
				}
			}

		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to get merged JSON", e);
		}
		return mergedJSONObject;
	}

	/**
	 * This method will deserialize the dataobject and store the order information in a separate file and this file will be send with the test result
	 */
	public static String storeOrderData(String objectFilePath, String dataFilePath){
		String objectString=null;
		String text="";

		try{
			/**
			 * Deserialize data object
			 */
			OrderDetails orderDetailObj=deserializeDataObject(objectFilePath);
			/**
			 * Getting string from the Order detail object and converting it into the jsonArray
			 */
			objectString=orderDetailObj.getOrderDetail();
			JSONArray jsonArray=new JSONArray(objectString);

			/** Creating the file Writer obj
			 */
			FileWriter fileWriter=new FileWriter(dataFilePath);

			try{
				for(int i =0 ; i<jsonArray.length(); i++){
					JSONObject innerJson=jsonArray.getJSONObject(i);
					Iterator<?> keys=innerJson.keys();

					while(keys.hasNext()){
						String key = (String)keys.next();
						System.out.println("Key is="+ key +"and value is ="+innerJson.get(key));

						if(key.toLowerCase().contains("tc_id") || key.toLowerCase().contains("order_id")){ 
							if(key.toLowerCase().contains("tc_id")){
								fileWriter.write(innerJson.get(key).toString()+"---");
								text+=innerJson.getString(key).toString()+"---";
							}else{
								fileWriter.write(innerJson.get(key).toString());
								text+=innerJson.getString(key).toString();
								fileWriter.write("\n");
								text+="\n";
							}

						}
					}
				}
			}catch(Exception e){
				System.out.println(e.getMessage());
			}finally {
				try{
					if(fileWriter !=null){
						fileWriter.close();
					}
				}catch(IOException ex){
					System.out.println(ex.getMessage());
				}

			}
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to get merged JSON", e);
		}
		return text;
	}


	/**
	 * This method will deserialize the dataobject and store the order information in a separate file and this file will be send with the test result
	 */
	public static String storeOrderData_UsingJacksonMapper(String objectFilePath, String dataFilePath){

		String text="";
		FileWriter fileWriter= null;

		/** de-serialize json object */
		ObjectMapper mapper = new ObjectMapper();

		try{
			fileWriter=new FileWriter(dataFilePath);
			TypeReference<List<DataObject>> typeReference = new TypeReference<List<DataObject>>() {};
			List<DataObject> dataObject = mapper.readValue(new File(objectFilePath), typeReference);

			for(int i =0 ; i<dataObject.size(); i++){

				if(dataObject.get(i).getOrder_Id() != null && !dataObject.get(i).getOrder_Id().equalsIgnoreCase("null") && !dataObject.get(i).getOrder_Id().isEmpty()){

					if(dataObject.get(i).getDesktop_TC_ID() != null) {
						text = text + "" + dataObject.get(i).getDesktop_TC_ID() + " ==> " + dataObject.get(i).getOrder_Id()+"\n";	
					}
					if(dataObject.get(i).getMobile_TC_ID() != null) {
						text = text + "" + dataObject.get(i).getMobile_TC_ID() + " ==> " + dataObject.get(i).getOrder_Id()+"\n";	
					}
				}
			}

			fileWriter.write(text);
			fileWriter.close();

		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : unable to get orders from serialized file. ", e);
		}
		return text;
	}


	/**
	 * This method will deserialize the dataobject and return its object
	 */
	public static OrderDetails deserializeDataObject(String objectFilePath){
		boolean isFileExist=false;
		OrderDetails orderObj=null;
		try{
			File serializedFile=new File(objectFilePath);
			isFileExist=serializedFile.exists();
			if(isFileExist){
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(serializedFile));
				orderObj = (OrderDetails) in.readObject();
				in.close();
			}
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to get merged JSON", e);
		}
		return orderObj;
	}
}


