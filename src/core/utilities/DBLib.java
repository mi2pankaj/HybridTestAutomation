/**
 * Last Changes Done on Jan 27, 2015 12:44:43 PM
 * Last Changes Done by Pankaj Katiyar

 * Purpose of change: 
 */
package core.utilities;

import java.util.HashMap;

import org.apache.log4j.Logger; 
import com.mysql.jdbc.CommunicationsException;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;

import lenskart.tests.TestSuiteClass;


public class DBLib 
{
	static Logger logger = Logger.getLogger(DBLib.class.getName());

	Connection connection; 

	/** Need to have this
	 * 
	 */
	public DBLib()
	{

	}

	/** Getting db connection 
	 * 
	 * @param connection
	 */
	public DBLib(Connection connection)
	{
		this.connection = connection;
	}


	public static String adlogCount() 
	{
		String ad_format_value = "0";
		try
		{
			String NewSqlQuery = "SELECT sum(COUNT) FROM (SELECT COUNT(*) AS COUNT FROM adplatform.ad_log1 UNION ALL SELECT COUNT(*) AS COUNT FROM adplatform.ad_log0) AS temp;";
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Running Query in DB : " + NewSqlQuery);

			Connection NewCon =  GenericMethodsLib.CreateSQLConnection();
			//Connection NewCon =  MobileTestClass_Methods.CreateServeSQLConnection();
			Statement NewStmt = (Statement) NewCon.createStatement();
			ResultSet NewRs = (ResultSet) NewStmt.executeQuery(NewSqlQuery);

			while(NewRs.next())
			{
				ad_format_value =  NewRs.getString("ad_format").toString();
			}

			NewCon.close();
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while getting adlog count:", e);
		}
		return ad_format_value;

	}


	/** Returns the map of db information, for this, supplied select query should return only one row. 
	 * 
	 * @param NewCon
	 * @return
	 */
	public HashMap<String, String> getDBInformationMap(Connection NewCon, String sqlQuery) 
	{
		HashMap<String, String> hashmap = new HashMap<String, String>();
		
		try
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Running Query - " + sqlQuery);

			Statement NewStmt = (Statement) NewCon.createStatement();
			ResultSet recordSet = (ResultSet) NewStmt.executeQuery(sqlQuery);

			int columns = recordSet.getMetaData().getColumnCount();
			recordSet.beforeFirst();	// Setting the cursor at first line

			while(recordSet.next())
			{
				for(int i=1; i<=columns; i++)
				{
					String key = recordSet.getMetaData().getColumnLabel(i).toString().trim(); 
					String value = recordSet.getString(i).toString().trim();
					hashmap.put(key, value);
				}
			}
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting the information from db: ", e);
		}

		return hashmap;
	}


	/** This method will execute the update / insert query.
	 * 
	 * @return
	 */
	public boolean executeUpdateInsertQuery(Connection connection, String sql)
	{
		boolean flag;
		try
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Running Query in DB : " + sql);
			Statement statement = (Statement) connection.createStatement();
			statement.executeUpdate(sql);

			flag = true;
		}catch(CommunicationsException | MySQLNonTransientConnectionException w)
		{
			flag = false;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : SQL connection was closed while executing query. ");
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while executing query: "+sql, e);
		}
		return flag;
	}



}
