package poc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import framework.googleAPI.classes.GoogleSheetAPI;

public class ReadGoogleSheetData {

	private String spreadsheetId = "1_IMkH9AuYk_sHpeHMsAaCF9ajs66z_UOJoGyLk_1-Wg"; 
	private String range = "'Test_Data'!A:O";


	public void readData() throws IOException{
		GoogleSheetAPI sheetAPI=new GoogleSheetAPI();
		List<List<Object>> values = sheetAPI.getSpreadSheetRecords(spreadsheetId, range);

		//int size =values.size();


		List<Object> headers =values.get(0);
		String[] fieldNames = new String[headers.size()];
		for(int j=0; j< headers.size(); j++){
			fieldNames[j]=headers.get(j).toString();
		}
		List<Map<String, String>> listOfTestData= new ArrayList<>();
		for(int i=1; i<values.size(); i++){
			Map<String, String> dataMap=new HashMap<String, String>();
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

		System.out.println(listOfTestData.toString());
	}

	public static void main(String[] args) throws IOException{
		createExcelFile(null);
	}


	public static void createExcelFile(JSONObject jsonObjectRepo) {
		try {

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("OR");

			Iterator<String> it = jsonObjectRepo.keys();

			int rowNum=0;

			while(it.hasNext()) {

				String objectName = it.next();
				String identifierName = jsonObjectRepo.getJSONObject(objectName).getString("identifierName");
				String identifierValue = jsonObjectRepo.getJSONObject(objectName).getString("identifierValue");
				String screenName = jsonObjectRepo.getJSONObject(objectName).getString("screenName");

				Row row = sheet.createRow(rowNum);

				try {
					Cell screenNameCell = row.createCell(0);
					screenNameCell.setCellType(Cell.CELL_TYPE_STRING);
					screenNameCell.setCellValue(screenName);
				}catch (java.lang.IllegalArgumentException e) {
					e.printStackTrace();
					System.out.println("Object: "+objectName + " at row: "+rowNum);
				}

				try {
					Cell objectNameCell = row.createCell(1);
					objectNameCell.setCellType(Cell.CELL_TYPE_STRING);
					objectNameCell.setCellValue(objectName);
				}catch (java.lang.IllegalArgumentException e) {
					System.out.println("Object: "+objectName + " at row: "+rowNum);
				}

				try {
					Cell identifierNameCell = row.createCell(2);
					identifierNameCell.setCellType(Cell.CELL_TYPE_STRING);
					identifierNameCell.setCellValue(identifierName);
				}catch (java.lang.IllegalArgumentException e) {
					System.out.println("Object: "+objectName + " at row: "+rowNum);
				}

				try {
					Cell identifierValueCell = row.createCell(3);
					identifierValueCell.setCellType(Cell.CELL_TYPE_STRING);
					identifierValueCell.setCellValue(identifierValue);
				}catch (java.lang.IllegalArgumentException e) {
					System.out.println("Object: "+objectName + " at row: "+rowNum);
				}
				
				rowNum++;
			}

			workbook.write(new FileOutputStream(new File("/Users/pankaj.katiyar/Desktop/xyz.xlsx")));
			workbook.close();

			System.out.println("Done");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


}
