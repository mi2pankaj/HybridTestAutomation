package tpt.classes.googleAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import core.utilities.FileLib;

public class GoogleSheetUtils {

	static Logger logger = Logger.getLogger(FileLib.class.getName());

	/**
	 * Get Values from the google spreadsheet
	 * @param spreadSheetId
	 * @param sheetRange
	 * @return
	 */
	public List<List<Object>> getSheetData(String spreadSheetId, String sheetRange){
		List<List<Object>> records = null;
		try{
			GoogleSheetAPI googleSheet = new GoogleSheetAPI();
			records=googleSheet.getSpreadSheetRecords(spreadSheetId, sheetRange);

		}catch(Exception e){
			logger.error("Unable to read the google sheet "+ e);
		}
		return records;
	}

	/**
	 * Get the sheet headers in the string
	 * @param records
	 * @return
	 */
	public String[] getDataHeaders(List<List<Object>> records){
		String[] fieldNames = null;
		try{
			/** Getting list of headers*/
			List<Object> headers =records.get(0);

			/** Declaring  that size of String array to store all headers*/
			fieldNames = new String[headers.size()];

			/** Storing headers in the String array */
			for(int j=0; j< headers.size(); j++){
				fieldNames[j]=headers.get(j).toString();
			}
		}catch(Exception e){
			logger.error("Unable to get the sheet headers " + e);
		}
		return fieldNames;
	}

	/**
	 * Store sheet data in the list of hashmap.
	 * @param records
	 * @param values
	 * @param headers
	 */
	public List<HashMap<String, String>> storeSheetDataInList(List<List<Object>> records, String[] headers){
		List<HashMap<String, String>> listOfTestData =null;
		try{
			/** Declaring the List of map to store all the test data*/
			listOfTestData= new ArrayList<>();

			/** Storing all the data from excel sheet to the list of map*/
			for(int i=1; i<records.size(); i++){

				/** Map to store row data in map*/
				HashMap<String, String> dataMap=new HashMap<String, String>();

				List<Object> data= records.get(i);
				for(int k=0; k< headers.length; k++){
					String fieldName;
					String value;
					fieldName = headers[k];
					try{
						value= data.get(k).toString();
					}catch(IndexOutOfBoundsException e){
						value = "";
					}	
					dataMap.put(fieldName, value);
				}
				listOfTestData.add(dataMap);
			}
		}catch(Exception e){
			logger.error("Unable to read the google sheet "+ e);
		}
		return listOfTestData;
	}

	public JSONObject getObjectRepoAsJSON(List<HashMap<String, String>> listOfMapObjectRepo){
		JSONObject jsonObj= new JSONObject();
		try{
			int rows_Count=listOfMapObjectRepo.size();

			for(int row=0; row<rows_Count; row++){
				HashMap<String, String> mapOfObject=new HashMap<String, String>();
				mapOfObject=listOfMapObjectRepo.get(row);

				String objectName = mapOfObject.get("objectName");
				if(objectName == null){
					objectName ="";
				}
				JSONObject json=new JSONObject();
				for(String key : mapOfObject.keySet()){

					if(!key.equalsIgnoreCase(objectName)){
						String value = mapOfObject.get(key);
						/** put values in json only if there is no empty values. */
						if(!key.isEmpty())
						{
							json.put(key, value);
						}
					}
				}
				jsonObj.put(objectName, json);
			}

		}catch(Exception e){
			logger.error("Unable to get the json for object repository "+ e);
		}
		return jsonObj;
	}

	/**
	 * Method to get test data from the google sheet
	 * @param sheetId
	 * @param sheetRange
	 * @return
	 * @throws IOException
	 */
	public List<HashMap<String, String>> getDataFromGoogle(String sheetId, String sheetRange) {

		/** Declaring the List of map to store all the test data*/
		List<HashMap<String, String>> listOfTestData= new ArrayList<>();

		try{
			/** Creating the object for the google sheet API class */
			GoogleSheetAPI sheetAPI=new GoogleSheetAPI();

			/** Getting the spreadsheet data in the list*/
			List<List<Object>> values = sheetAPI.getSpreadSheetRecords(sheetId, sheetRange);

			/** create map when received list is not empty */
			if(!values.isEmpty() && values != null) {

				/** Getting list of headers*/
				List<Object> headers =values.get(0);

				/** Declaring  that size of String array to store all headers*/
				String[] fieldNames = new String[headers.size()];

				/** Storing headers in the String array */
				for(int j=0; j< headers.size(); j++){
					fieldNames[j]=headers.get(j).toString();
				}

				/** Storing all the data from excel sheet to the list of map*/
				for(int i=1; i<values.size(); i++){

					/** Map to store row data in map*/
					HashMap<String, String> dataMap=new HashMap<String, String>();

					List<Object> data= values.get(i);
					for(int k=0; k< headers.size(); k++){
						String fieldName;
						String value;
						fieldName = fieldNames[k];

						try{
							value= data.get(k).toString();
						}catch(IndexOutOfBoundsException e){
							value = "";
						}	
						dataMap.put(fieldName, value);
					}
					listOfTestData.add(dataMap);
				}
			}else {
				logger.error("No data received from the google sheet");
			}
		}catch (Exception e) {
			logger.error("Unable to get data from the google sheet");
		}
		return listOfTestData;
	}


}
