package lenskart.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

import core.classes.SingletonTestObject;
import core.classes.TestCaseObject;
import core.utilities.GenericMethodsLib;
import tpt.classes.googleAPI.GoogleSheetAPI;

public class WriteFailResultInGoogleSheet {

	public static Logger logger = Logger.getLogger(WriteFailResultInGoogleSheet.class.getName());

	/**
	 * checking the updated list and writing fail test case in the Google sheet as date wise  
	 * @param testCaseObjectMap
	 */
	public static void writeFailCaseInGoogleSheet(){	

		try{

			/** getting the suite type from mvn */
			/** writing result for only daily sanity*/
			String test_suite_type = System.getProperty("test_suite_type").trim();

			if (test_suite_type.equalsIgnoreCase("daily_morning_sanity") || test_suite_type.equalsIgnoreCase("regression") 
					|| test_suite_type.equalsIgnoreCase("mandeep")){

				String Result;
				HashMap<String, List<Object>> sheetrecord=getGooglesheetData();

				/** iterate the testcase object map - get this from singleton class */
				for(Entry<String, TestCaseObject> entrySet : SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().entrySet()){

					Result=entrySet.getValue().getTestCaseResult().toLowerCase();

					if(Result.contains("fail")){

						String testcaseid= entrySet.getKey();
						List<Object> row=new ArrayList<>();

						if(sheetrecord.containsKey(testcaseid)){
							row=sheetrecord.get(testcaseid);
							String ncout= row.get(1).toString();
							int count=Integer.valueOf(ncout)+1;
							row.set(1, count);
							Object now=java.time.LocalDate.now();
							row.set(3, now);
						}else{
							row.add(0, testcaseid);
							row.add(1, "1");
							row.add(2,java.time.LocalDate.now());
							row.add(3, java.time.LocalDate.now());
						}
						sheetrecord.put(testcaseid, row);
					}
				}

				/** writing data in google sheet */
				writeInGoogleSheet(sheetrecord);
				logger.info("*****Sucessfully written in the google sheet**");
			}
		}catch(Exception e){
			logger.error("** Exception occured while geeting value from testcaseobject and writing in the google sheet***"+e.getMessage(), e);
		}
	}


	/**
	 * Writing in the google sheet
	 * @param sheetrecord
	 */
	public static void writeInGoogleSheet(HashMap<String, List<Object>>sheetrecord){

		try{

			Sheets sheetservice=new GoogleSheetAPI().getSheetsService();
			String sheetid=GenericMethodsLib.googleConfigurationProperties.getProperty("failed_testcases_result_sheet").toString();
			String sheetrange=GenericMethodsLib.googleConfigurationProperties.getProperty("failed_testcases_result_sheet_Range").toString();
			
			/** below code will delete the sheet data */
			sheetservice.spreadsheets().values().clear(sheetid, sheetrange, new ClearValuesRequest()).execute();

			/** below code will iterate over the final list and will write in the sheet*/
			List<ValueRange> data = new ArrayList<>();
			
			/** writing column name first */
//			data.add(new ValueRange().setRange(String.valueOf("'Failed-Cases-Stats'!"+"A1:D1"))
//			.setValues(Arrays.asList(Arrays.asList("TEST_CASE_ID","COUNT","FAILURES_STARTED","LAST_FAILED"))).setMajorDimension("ROWS"));
			
			int counter = 1;
			for(Map.Entry<String, List<Object>>finaldata : sheetrecord.entrySet()){

				ValueRange valrange =new ValueRange();

				List<Object>values=finaldata.getValue();				
				String TC_id=values.get(0).toString();

				String count;
				try { count=values.get(1).toString();}catch (IndexOutOfBoundsException e) {
					count = "0";
				}

				String  start_date;
				try{start_date=values.get(2).toString();}catch (IndexOutOfBoundsException e) {
					start_date=String.valueOf(java.time.LocalDate.now());
				}

				String lastfail_date;
				try{lastfail_date=values.get(3).toString();}catch (IndexOutOfBoundsException e) {
					lastfail_date=String.valueOf(java.time.LocalDate.now());
				}
				
				valrange.setRange(String.valueOf("'Failed-Cases-Stats'!"+"A"+counter+":D"+counter))
				.setValues(Arrays.asList(Arrays.asList(TC_id,count,start_date,lastfail_date))).setMajorDimension("ROWS");

				data.add(valrange);

				counter++;
			}

			BatchUpdateValuesRequest batchBody = new BatchUpdateValuesRequest()
					.setValueInputOption("USER_ENTERED")
					.setData(data);

			BatchUpdateValuesResponse batchResult = sheetservice.spreadsheets().values()
					.batchUpdate(sheetid, batchBody)
					.execute();

			logger.info("****** Failed Test cases written successfully in the google sheets ******* total cells updated "+ batchResult.getTotalUpdatedCells());
		}catch(Exception e){
			logger.error("Exception occur while writing in the google sheet - "+e.getMessage(),e);
		}
	}


	/**it will get the sheet record and will put in the map*/
	public static HashMap<String, List<Object>> getGooglesheetData(){
		HashMap<String, List<Object>>sheetdata=new HashMap<>();
		try{
			String sheetid=GenericMethodsLib.googleConfigurationProperties.getProperty("failed_testcases_result_sheet").toString();
			String sheetrange=GenericMethodsLib.googleConfigurationProperties.getProperty("failed_testcases_result_sheet_Range").toString();
			List<List<Object>> values=new GoogleSheetAPI().getSpreadSheetRecords(sheetid, sheetrange);

			for(int i=0;i<values.size();i++){
				List <Object>rowdata=values.get(i);
				String  key = rowdata.get(0).toString();
				sheetdata.put(key, rowdata);			
			}
		}catch(Exception e){}	
		return sheetdata; 
	}
}
