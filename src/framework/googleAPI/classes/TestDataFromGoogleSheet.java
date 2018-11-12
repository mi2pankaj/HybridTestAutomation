package framework.googleAPI.classes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;


public class TestDataFromGoogleSheet {
	
	Logger logger = Logger.getLogger(TestDataFromGoogleSheet.class.getName());
	
	/**
	 * Method to get test data from the google sheet
	 * @param sheetId
	 * @param sheetRange
	 * @return
	 * @throws IOException
	 */
	public List<HashMap<String, String>> getTestDataFromGoogle(String sheetId, String sheetRange) {
		
		/** Declaring the List of map to store all the test data*/
		List<HashMap<String, String>> listOfTestData= new ArrayList<>();
		
		try{
			/** Creating the object for the google sheet API class */
			GoogleSheetAPI sheetAPI=new GoogleSheetAPI();
			
			/** Getting the spreadsheet data in the list*/
			List<List<Object>> values = sheetAPI.getSpreadSheetRecords(sheetId, sheetRange);
			
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
			
		}catch (Exception e) {
			logger.error("Unable to get data from the google sheet");
		}
		return listOfTestData;
	}
	
		
	}


