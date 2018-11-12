/**
 * Last Changes Done on 5 Mar, 2015 12:07:46 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */
package core.classes;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lenskart.tests.TestSuiteClass;


public class WriteTestResults {

	/**
	 * @param args
	 */

	Logger logger = Logger.getLogger(WriteTestResults.class.getName());

	public void addResultColumn(File testResultFile, String sheetName, String resultLabel)
	{
		try{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Adding label: "+resultLabel +" column in file: "+testResultFile + " in sheet: "+sheetName);

			Workbook book = Workbook.getWorkbook(testResultFile);
			WritableWorkbook copiedBook = Workbook.createWorkbook(testResultFile, book);
			WritableSheet sheet = copiedBook.getSheet(sheetName);

			Label lblColumnName = new Label(sheet.getColumns(), 0, resultLabel);
			sheet.addCell(lblColumnName);

			copiedBook.write();
			copiedBook.close();
			book.close();
		}catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while adding Test Result column in file: "+testResultFile, e);
		}
	}

	/** write execution control sheet contents
	 * 
	 * @param executionControlSheet
	 * @param updatedTestCasesList
	 */
	public void writeExecutionControlSheetContent(XSSFSheet executionControlSheet, List<TestCaseObject> updatedTestCasesList)
	{
		int i=0;
		for (TestCaseObject testcaseObject : updatedTestCasesList)
		{
			XSSFRow row = executionControlSheet.createRow(i+1);

			row.createCell(0).setCellValue(testcaseObject.getTestCaseId());
			row.createCell(1).setCellValue(testcaseObject.getTestDataID());
			row.createCell(2).setCellValue(testcaseObject.getTestCaseDescription());

			row.createCell(3).setCellValue(testcaseObject.getTestCaseSupportedBrowserType());
			row.createCell(4).setCellValue(testcaseObject.getTestCaseDataDriven());
			row.createCell(5).setCellValue(testcaseObject.getTestCaseResult());
			row.createCell(6).setCellValue(testcaseObject.getTestCaseExecutionTime());
			row.createCell(7).setCellValue(testcaseObject.getOwnerName());
			i++;
		}
	}

	/** Write execution control header labels
	 * 
	 * @param executionControlSheet
	 */
	public void addHeader_ExecutionControlSheet(XSSFSheet executionControlSheet)
	{
		//create header
		XSSFRow rowhead = executionControlSheet.createRow((short)0);

		rowhead.createCell(0).setCellValue("TC_ID");
		rowhead.createCell(1).setCellValue("TD_ID");
		rowhead.createCell(2).setCellValue("Description");

		rowhead.createCell(3).setCellValue("Supported_Browser_Type");
		rowhead.createCell(4).setCellValue("Data_Driven");
		rowhead.createCell(5).setCellValue("Result");
		rowhead.createCell(6).setCellValue("ExecutionTime(Sec)");
		rowhead.createCell(7).setCellValue("Owner");
	}

	/** write test case object results 
	 * 
	 * @param updatedTestCasesList
	 * @return
	 */
	public synchronized boolean writeTestObjectResults_UsingPoI (List<TestCaseObject> updatedTestCasesList, String filePath)  
	{
		boolean flag;
		try
		{
			logger.info("Writing Test Results ******* received test objects size: "+updatedTestCasesList.size());

			FileOutputStream out = new FileOutputStream(new File(filePath));

			XSSFWorkbook workbook = new XSSFWorkbook();

			/** add execution control sheet */
			XSSFSheet executionControlSheet = workbook.createSheet("executionControl");

			/** add test case step sheet */
			XSSFSheet testCaseStepsSheet = workbook.createSheet("testCaseSteps");

			/** Write execution control header labels */
			addHeader_ExecutionControlSheet(executionControlSheet);

			/** Write test step sheet header labels */
			addHeader_TestCaseStepsSheet(testCaseStepsSheet);

			/** write execution control data */
			writeExecutionControlSheetContent(executionControlSheet, updatedTestCasesList);

			/** write test step data */
			writeTestCaseStepsSheetContent(testCaseStepsSheet, updatedTestCasesList);

			workbook.write(out);
			workbook.close();

			flag = true;

		}catch (Exception e) {
			flag = false;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " Exception while writing results ." , e);
		}

		return flag;
	}

	/** Write testCaseSteps header labels
	 * 
	 * @param executionControlSheet
	 */
	public void addHeader_TestCaseStepsSheet(XSSFSheet testCaseStepsSheet)
	{
		//create header
		XSSFRow rowhead = testCaseStepsSheet.createRow((short)0);

		rowhead.createCell(0).setCellValue("TC_ID");
		rowhead.createCell(1).setCellValue("TD_ID");

		rowhead.createCell(2).setCellValue("Step_ID");
		rowhead.createCell(3).setCellValue("Description");

		rowhead.createCell(4).setCellValue("Keyword");
		rowhead.createCell(5).setCellValue("objectName");

		rowhead.createCell(6).setCellValue("inputData");
		rowhead.createCell(7).setCellValue("Test_Results");
		
		rowhead.createCell(8).setCellValue("ExecutionTime(Sec)");
		rowhead.createCell(9).setCellValue("RetryResult(If required)");
	}

	/** write test case step sheet contents
	 * 
	 * @param executionControlSheet
	 * @param updatedTestCasesList
	 */
	public void writeTestCaseStepsSheetContent(XSSFSheet testCaseStepsSheet, List<TestCaseObject> updatedTestCasesList)
	{
		int i=1;

		for (TestCaseObject testcaseObject : updatedTestCasesList)
		{
			/** get test step object list from each test case object */
			List<TestStepObject> testStepObjectList = testcaseObject.gettestStepObjectsList();

			XSSFRow row;

			/** iterate each test step and write results */
			for(TestStepObject testStepObject : testStepObjectList)
			{
				row = testCaseStepsSheet.createRow(i);

				/** write test step objects */
				getTestCaseSteps_RowContent(row, testStepObject.getTestCaseId(), testcaseObject.getTestDataID(), 
						testStepObject.getTestStepId(), testStepObject.getTestStepDescription(), 
						testStepObject.getKeyword(), testStepObject.getObjectName(), testStepObject.getData(), 
						testStepObject.getTestStepResult(), testStepObject.getTestStepExecutionTime(), testStepObject.getTestStepBeforeRetryResult());
				i++;
			}

			/** add a blank row after each test case in test step sheet */
			row = testCaseStepsSheet.createRow(i);
			getTestCaseSteps_RowContent(row, "", "", "", "", "", "", "","","","");
			i++;
		}
	}

	/** write test case step sheet contents
	 * 
	 * @param executionControlSheet
	 * @param updatedTestCasesList
	 */
	public void getTestCaseSteps_RowContent(XSSFRow row, String getTestCaseId, String getTestDataID, 
			String getTestStepId, String getTestStepDescription, String getKeyword, 
			String getObjectName, String getData, String getTestStepResult, String getExecutionTime, String getTestStepRetryResult)
	{
		try
		{
			row.createCell(0).setCellValue(getTestCaseId);
			row.createCell(1).setCellValue(getTestDataID);

			row.createCell(2).setCellValue(getTestStepId);
			row.createCell(3).setCellValue(getTestStepDescription);

			row.createCell(4).setCellValue(getKeyword);
			row.createCell(5).setCellValue(getObjectName);

			row.createCell(6).setCellValue(getData);
			row.createCell(7).setCellValue(getTestStepResult);
			
			row.createCell(8).setCellValue(getExecutionTime);
			row.createCell(9).setCellValue(getTestStepRetryResult);
			
		}catch (Exception e) {
			logger.error("Exception while writing results: "+ getTestCaseId + " - " + getTestStepId + " - " + getKeyword + " - " +getTestStepResult);
		}
	}

}
