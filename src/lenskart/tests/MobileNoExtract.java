package lenskart.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.testng.annotations.Test;

import framework.googleAPI.classes.GoogleSheetAPI;


public class MobileNoExtract {

	@Test
	public void ExtractMobile(){

		try{

			String sheetId ="1tTxMkhi2zdjXSeZzjBPy39Ff73tHi9kF1lzcAXh6kOQ";
			String sheetRange="ROSTER!A:C";

			List<List<Object>> record=new GoogleSheetAPI().getSpreadSheetRecords(sheetId, sheetRange);
			System.out.println("record of the roster sheet is : "+record);

			String number="";

			for(int i=0;i<record.size();i++){

				List<Object>values=record.get(i);
				int size=values.size();

				if(size>2){
					number=values.get(1).toString();

					System.out.println("phone number:"+number);
					System.out.println("Roaster  :"+values.get(2)); 
				}
			}

			// write values in properties file 
			Properties property = new Properties();
			property.setProperty("guest_mobile_no", number);
			property.store(new FileOutputStream(new File(System.getProperty("user.dir")+"/roster.properties")), "comments ");

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * get mobile number 
	 * @return
	 */
	public static String getRosterMobile(){

		String number="";
		try{
			String sheetId ="1tTxMkhi2zdjXSeZzjBPy39Ff73tHi9kF1lzcAXh6kOQ";
			String sheetRange="ROSTER!A:C";

			List<List<Object>> record=new GoogleSheetAPI().getSpreadSheetRecords(sheetId, sheetRange);
			System.out.println("record of the roster sheet is : "+record);

			for(int i=0;i<record.size();i++){

				List<Object>values=record.get(i);
				int size=values.size();

				if(size>2){
					number=values.get(1).toString();

					System.out.println("phone number:"+number);
					System.out.println("Roaster  :"+values.get(2)); 
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return number;
	}
}
