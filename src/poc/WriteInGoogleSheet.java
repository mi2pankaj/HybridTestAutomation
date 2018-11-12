package poc;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.datetime.joda.LocalDateParser;
import org.testng.annotations.Test;

import com.google.api.client.util.Data;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import framework.googleAPI.classes.GoogleSheetAPI;
import framework.utilities.GenericMethodsLib;

public class WriteInGoogleSheet {


	public static void writeInGoogleSheet(List<List<Object>> finallist){
		try{
			//			List<List<Object>>finallist=new ArrayList<>();
			//			for(Map.Entry<String, List<Object>>finaldata:sheetrecord.entrySet()){
			//				finallist.add(finaldata.getValue());	
			//			}

			Sheets sheetservice;
			String sheetid=GenericMethodsLib.googleConfigurationProperties.getProperty("failed_testcases_result_sheet").toString();
			sheetservice=new GoogleSheetAPI().getSheetsService();

			System.out.println();
			ValueRange valrange =new ValueRange();

			List<List<Object>> dd = Arrays.asList(Arrays.asList("Desktop_TC_16",1,"job_12"), Arrays.asList("Desktop_TC_17",1,"job_13"));
			valrange.setValues(dd);
			sheetservice.spreadsheets().values().update(sheetid, "A2", valrange).setValueInputOption("RAW").execute();
			System.out.println("Printed");


			valrange.setValues(finallist);
			sheetservice.spreadsheets().values().update(sheetid, "A5", valrange).setValueInputOption("RAW").execute();

		}catch(Exception e){
			e.printStackTrace();

		}

		System.out.println("Done");
	}



	public static void writeAsList(){

		List<List<Object>> finallist = new ArrayList<>();

		List<Object> a = Arrays.asList("a","b","c","d");
		List<Object> b = Arrays.asList("e","f","g","h");

		finallist.add(a);
		finallist.add(b);

		writeInGoogleSheet(finallist);
		//		writeInGoogleSheet(Arrays.asList(a,b));
	}
	public  static void writeinGooleSheet(){
		try{
			Sheets sheetservice;
			long mili=System.currentTimeMillis();
			String now=new java.sql.Date(mili).toString();
			sheetservice=new GoogleSheetAPI().getSheetsService();
			String sheetid="1PeOQHnfhWwJjYO0w7FzKYhuMznxe0iTCQqz-9RHjzD0";
			String sheetRange="A:C";
			ValueRange valrange=new ValueRange();
			System.out.println("written ");
			List<List<Object>> record=new GoogleSheetAPI().getSpreadSheetRecords(sheetid, sheetRange);
			List<Object>header=record.get(0);
			String []fieldnames=new String [header.size()];
			HashMap<Object, Object>sheetdata = new HashMap<>();

			for(int j=0;j<record.size();j++){
				List<Object>value=  record.get(j);
				sheetdata.put(value.get(0), value.get(1));				
			}
			System.out.println("printing map now ---");
			System.out.println(sheetdata.toString());
			if(sheetdata.containsKey("Desktop_TC_16")){
				String count=sheetdata.get("Desktop_TC_16").toString();
				int ncount =Integer.valueOf(count)+1;
				valrange.setValues(Arrays.asList(
						Arrays.asList("Desktop_TC_16",ncount,"job_12")	
						));
				sheetservice.spreadsheets().values().update(sheetid, "A2", valrange).setValueInputOption("RAW").execute();
				System.out.println("Printed");
			}else{
				valrange.setValues(Arrays.asList(
						Arrays.asList("Desktop_TC_19","0","job_12")	
						));
				sheetservice.spreadsheets().values().append(sheetid, "A1", valrange).setValueInputOption("RAW")
				.setInsertDataOption("INSERT_ROWS").setIncludeValuesInResponse(true).execute();				
			}


		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void get_roastersdata(){
		try{
		String sheetId ="1Wnd6-7bCBzmjlTr2ldWck_ekP1A37wH30TcyPhYyKwE";
		String sheetRange="A:C";
		
		List<List<Object>> record=new GoogleSheetAPI().getSpreadSheetRecords(sheetId, sheetRange);
		System.out.println("record of the roster sheet is : "+record);
		for(int i=0;i<record.size();i++){
			List<Object>values=record.get(i);
			int size=values.size();
			if(size>2){
		    String number=values.get(1).toString();
		    System.out.println("phone number:"+number);
		   System.out.println("Roaster  :"+values.get(2)); 
		    
			}
		}
		}catch(Exception e){
			
		}
	}
	public static void main(String args[]){
		WriteInGoogleSheet.get_roastersdata();
	
		
	}
	


}