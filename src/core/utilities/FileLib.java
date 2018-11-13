/**
 * Last Changes Done on Jan 27, 2015 12:39:54 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */
package core.utilities;


import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.CellFinder;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import tests.SuiteClass;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;

import core.utilities.GenericUtils; 


public class FileLib 
{		

	static Logger logger = Logger.getLogger(FileLib.class.getName());

	static PrintStream logFile;	//Declared variable to save Log file to be used in method SaveConsoleLogs
	static String onlinePlayerFolderLocation;


	//This constructor is used to set folder location in server for each type of online player. 
	//further the value set here will be used in method: FileLib.WritingTestURLInExcelSheet to construct Test URL for online ads.
	public FileLib(String onlinePlayerType) 
	{
		onlinePlayerFolderLocation = onlinePlayerType;	
	}


	//********** Save Console Logs In A Given File, In Logs Folder: *********************************************//
	public static void SaveConsoleLogs(String logFileLocation, String logFileName) throws FileNotFoundException 
	{

		//String logFileLocation = TestSuiteClass.AUTOMATION_HOME.concat("/logs").toString();	

		//Check if Log Folder exists, if not then create it.
		File checkLogFileLocation = new File(logFileLocation);

		if(!(checkLogFileLocation.exists()))
		{
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Log folder doesn't exist at " +logFileLocation);
			boolean b = checkLogFileLocation.mkdirs();

			if(b)
			{
				//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Refer logs stored in folder: "+logFileLocation); 
			}
			else
			{
				logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Log folder wasn't created");
			}
		}

		//Create Log file and save all Console Log in it.
		logFile = new PrintStream(new FileOutputStream(logFileLocation.concat("/").concat(logFileName)), true);
		System.setOut(logFile);
		//System.setErr(logFile);

		//Printing Test Start Time.
		logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : ###################################################################");
		logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : ******* Test Suite Execution Started At Time ********: " +GenericUtils.DateTimeStamp("MMddyyyy_hhmmss"));
		logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : ###################################################################");
	}



	//******************** Generic Method To Write String In File ***************************//
	public static boolean WriteFile(String strContent, String testFileLocation, String fileName)
	{	
		boolean flag = false;
		try
		{
			File testDataFolderLocation = new File(testFileLocation);

			if(!(testDataFolderLocation.exists()))
			{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Test Data folder doesn't exist at " +testFileLocation);
				boolean b = testDataFolderLocation.mkdirs();

				if(b)
				{
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Test data folder was created successfully "); 
				}
				else
				{
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Test data folder wasn't created");
				}
			}

			String testFile = testFileLocation+"/"+fileName;
			File file = new File(testFile);

			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(strContent);
			bw.close();
			fw.close();

			flag = true;
		}catch(Exception t)
		{
			flag = false;
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while writing file. ", t);
		}
		return flag;
	}


	//******************** Generic Method To Write / Append String In File ***************************//
	@SuppressWarnings("finally")
	public static boolean WriteFile(String strContent, String testFileLocation, String fileName, boolean append) 
	{	

		boolean flag = false;

		try{
			File testDataFolderLocation = new File(testFileLocation);

			if(!(testDataFolderLocation.exists()))
			{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Directory doesn't exist at " +testFileLocation);
				boolean b = testDataFolderLocation.mkdirs();

				if(b)
				{
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Directory was created successfully "); 
				}
				else
				{
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Directory was not created");
				}
			}

			String testFile = testFileLocation+"/"+fileName;
			File file = new File(testFile);

			FileWriter fw = new FileWriter(file, append);
			fw.write(strContent);
			fw.close();

			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : File : " +testFile+ " is written successfully");

			flag = true;
		}catch(Exception e)
		{
			flag = false;
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occured while writing in file. ", e);
		}
		finally
		{
			return flag;
		}
	}


	//******************** Generic Method To Copy File From One Folder To Another Folder And Rename With Date Stamp  ***************************//
	public static File CopyExcelFile(String sourceFileNameWithLocation, String destinationFileLocationWithOutExtension) 
	{
		File testResultFile = null;

		try{
			File testDataFile = new File(sourceFileNameWithLocation);

			String testResultFileNameWithLocation = destinationFileLocationWithOutExtension.concat(".xls");
			testResultFile = new File(testResultFileNameWithLocation);

			//Copying Test Data File to Test Results Folder
			FileUtils.copyFile(testDataFile, testResultFile);

			if (testResultFile.exists())
			{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Test Result File Created At: " +testResultFile.getPath());
			}
		}
		catch(Exception e)
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occured while while copying excel file: "+sourceFileNameWithLocation, e);
		}

		return testResultFile;	
	}



	//******************** Generic Method To remove rows containing Yes in a particular column  ***************************//
	public static void RemoveRowsInExcelFile(String fileNameWithLocation) throws IOException, BiffException, WriteException 
	{
		try
		{
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : fileNameWithLocation: " +fileNameWithLocation);
			//Getting WorkBook
			Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));
			WritableWorkbook copiedBook = Workbook.createWorkbook(new File(fileNameWithLocation), book);	
			WritableSheet sheet = copiedBook.getSheet(0);	//.getSheet(sheetName);

			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : total rows: " +sheet.getRows());

			for(int row=1;row<sheet.getRows();row++)
			{
				String flag = sheet.getCell(0, row).getContents().toString().toLowerCase().trim();
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : flag: " +flag + " at row: " +row);

				if (flag.equalsIgnoreCase("no"))
				{
					logger.info(flag + " - and - " +row);
					sheet.removeRow(row);
				}
			}
			copiedBook.write();
			copiedBook.close();
			book.close();
		}
		catch(NullPointerException e)
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occured while writing data.", e);
		}
	}



	//*********************** Generic Method To Write MYSQL Data Into Excel Sheet: *********************************************//
	public static void WritingMySQLRecordsInExcelSheet (String fileNameWithLocation, String[][] strRecord) throws IOException, RowsExceededException, WriteException
	{

		String directory = StringLib.splitDirectoryFromFileLocation(fileNameWithLocation);

		//Creating Test Data Folder if it doesn't exist.
		//File testDataFolderLocation = new File(TestSuiteClass.AUTOMATION_HOME.concat("/tc_data/adserve/mobileAdServe/DataToFormURL"));

		File testDataFolderLocation = new File(directory);

		if(!(testDataFolderLocation.exists()))
		{
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Test Data folder doesn't exist at " + directory);
			boolean b = testDataFolderLocation.mkdirs();

			if(b)
			{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Test data folder was created successfully "); 
			}
			else
			{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Test data folder wasn't created");
			}
		}

		//Delete Existing Test Data File Before Creating It 
		File checkFile;
		checkFile = new File(fileNameWithLocation);

		if (checkFile.exists())
		{	
			checkFile.delete();
		}

		//Writing The Data Into Excel After Deleting Existing File
		FileOutputStream outputfile = new FileOutputStream(fileNameWithLocation,true);
		WritableWorkbook book = Workbook.createWorkbook(outputfile);
		WritableSheet sheet = book.createSheet("Test_Data", 0);

		try
		{
			for(int i=0; i<strRecord.length; i++)		// For Every Row
			{
				for(int j=0; j<strRecord[0].length; j++)	// For Every Column
				{
					Label lblRecordData = new Label(j, i, strRecord[i][j]);
					sheet.addCell(lblRecordData);
					//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Printing Elements : "+strRecord[i][j]);
				}
				// logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : ");
			}
		}
		catch (ArrayIndexOutOfBoundsException e) 
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : No Records Returned By The Query. Handled by Method : WritingMySQLRecordsInExcelSheet:", e);
		}
		catch (Exception e) 
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception Handled by Method : WritingMySQLRecordsInExcelSheet:", e);
		}
		finally
		{
			book.write();
			book.close();		
		}
		logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Data Was Successfully Written In Excel Sheet By Method WritingExcelSheet: " + " Rows: " +strRecord.length + ", Total Columns: " +strRecord[0].length);
	}			    


	/**
	 *  This method will fetch data from given file .
	 * @param fileNameWithLocation
	 * @param columns
	 * @return
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 * @throws BiffException
	 */
	public static String[][] FetchDataFromExcelSheet(String fileNameWithLocation, String ... columns)		
	{	
		//Read file name from configuration file and forming the file path using the system environment variable
		int columnNo = columns.length;
		Workbook book = null;
		try
		{
			book = Workbook.getWorkbook(new File(fileNameWithLocation));
		}catch( IOException | BiffException e)
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while reading excel file: "+fileNameWithLocation, e);
		}

		String [][]testDataProvider = null;
		if(book != null)
		{
			Sheet sheet = book.getSheet(0);	//getSheet(sheetName);
			CellFinder cellFind = new CellFinder(sheet);	//Finding the Cell with a particular text and later on get the corresponding Row or Column;

			testDataProvider = new String[sheet.getRows()-1][columnNo];
			// This will work as Data Provider - returning columns as - Ad_Format, Test_URLs, Campaign_ID, Channel_ID

			for(int row=1;row<sheet.getRows();row++)
			{
				//c=0 is fileLocation passed in method FetchDataFromExcelSheet, so counting from c=1
				for (int c = 0; c < columnNo; c++)
				{
					try
					{
						String strAd_Format = sheet.getCell(cellFind.findLabelCell(columns[c]).getColumn(), row).getContents().toString().trim();
						testDataProvider[row-1][c] = strAd_Format;
						//System.out.print(strAd_Format + "   :   ");
					}catch(NullPointerException n)
					{
						testDataProvider[row-1][c] = "";
						logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : There may be a Leading or Trailing SPACE in this cell: " + "row: "+row + "  and coulmn: "+columnNo );
					}
				}
				//logger.info();
			}	   

			book.close();
		}
		return testDataProvider;				
	}


	/**
	 * This method will fetch the data from the specified sheet of the supplied excel file.
	 * @param fileNameWithLocation
	 * @param sheetName
	 * @param columns
	 * @return
	 */
	public static String[][] FetchDataFromExcel_SpecificSheet(String fileNameWithLocation, String sheetName, String ... columns)		
	{	
		//Read file name from configuration file and forming the file path using the system environment variable
		int columnNo = columns.length;
		Workbook book = null;
		try
		{
			book = Workbook.getWorkbook(new File(fileNameWithLocation));
		}catch( IOException | BiffException e)
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while reading excel file: "+fileNameWithLocation, e);
		}

		String [][]testDataProvider = null;
		if(book != null)
		{
			Sheet sheet = book.getSheet(sheetName);
			CellFinder cellFind = new CellFinder(sheet);	//Finding the Cell with a particular text and later on get the corresponding Row or Column;

			testDataProvider = new String[sheet.getRows()-1][columnNo];
			// This will work as Data Provider - returning columns as - Ad_Format, Test_URLs, Campaign_ID, Channel_ID

			for(int row=1;row<sheet.getRows();row++)
			{
				//c=0 is fileLocation passed in method FetchDataFromExcelSheet, so counting from c=1
				for (int c = 0; c < columnNo; c++)
				{
					try
					{
						String strAd_Format = sheet.getCell(cellFind.findLabelCell(columns[c]).getColumn(), row).getContents().toString().trim();
						testDataProvider[row-1][c] = strAd_Format;
						//System.out.print(strAd_Format + "   :   ");
					}catch(NullPointerException n)
					{
						testDataProvider[row-1][c] = "";
						logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : There may be a Leading or Trailing SPACE in this cell: " + "row: "+row + "  and coulmn: "+columnNo );
					}
				}
			}	   

			book.close();
		}
		return testDataProvider;				
	}


	/** This method will get the test cases from portal test case spreadsheet for the supplied runnable test case ids,
	 * considering portal spreadsheet has fixed columnName though location of coulmns may change.
	 * 
	 * @param fileNameWithLocation
	 * @param sheetName
	 * @param runnableTC_ID
	 * @param columns
	 * @return
	 */
	public static String[][] FetchPortalTestCases(String fileNameWithLocation, String sheetName, List<String> runnableTC_ID, String ... columns)		
	{	
		/** Read file name from configuration file and forming the file path using the system environment variable */
		int columnNo = columns.length;
		Workbook book = null;
		try
		{
			book = Workbook.getWorkbook(new File(fileNameWithLocation));
		}catch( IOException | BiffException e)
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while reading excel file: "+fileNameWithLocation, e);
		}

		/** this list will contain the 1D array of test step data */
		List<String []> tcStepDataList = new ArrayList<>();

		if(book != null)
		{
			Sheet sheet = book.getSheet(sheetName);

			/** Finding the Cell with a particular text and later on get the corresponding Row or Column; */
			CellFinder cellFind = new CellFinder(sheet);

			for(int row=1;row<sheet.getRows();row++)
			{
				/** getting the contents of TC_ID column for each row and checking if that is contained in
				 * supplied TC_ID list, if yes then add that whole row in array else not. */
				String tcID = sheet.getCell(cellFind.findLabelCell("TC_ID").getColumn(), row).getContents().trim();

				if(runnableTC_ID.contains(tcID))
				{
					/** setting up array */
					String []testStepData = new String[columnNo];

					for (int c = 0; c < columnNo; c++)
					{
						try
						{
							String strData = sheet.getCell(cellFind.findLabelCell(columns[c]).getColumn(), row).getContents().toString().trim();
							testStepData[c] = strData;

						}catch(NullPointerException n)
						{
							testStepData[c] = "";
							logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : There may be a Leading or Trailing SPACE in this cell: " + "row: "+row + "  and coulmn: "+c );
						}
					}

					/** add teststepdata into list */
					tcStepDataList.add(testStepData);
				}
			}	   

			book.close();
		}

		/** convert List of 1 D array to 2D array -- this is being done to avoid 2D array containing null because of selected data. */
		return StringLib.get2DArrayFrom1DArrayList(tcStepDataList);				
	}



	//******************** Generic Method To Fetch Data as Data Provider from any Test Data Spreadsheet *************************************************//
	public static String[][] FetchDataFromExcelSheetWithColumnNames(String fileNameWithLocation, String ... columns) throws BiffException, IOException 		
	{	
		//Read file name from configuration file and forming the file path using the system environment variable
		int columnNo = columns.length;

		Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));	
		Sheet sheet = book.getSheet(0);	//getSheet(sheetName);

		logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Total Rows: "+sheet.getRows());

		CellFinder cellFind = new CellFinder(sheet);	//Finding the Cell with a particular text and later on get the corresponding Row or Column;

		//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Test Data Contains: Rows: " +sheet.getRows() + " And Columns: " +sheet.getColumns());

		String [][]testDataProvider = new String[sheet.getRows()][columnNo];
		// This will work as Data Provider - returning columns as - Ad_Format, Test_URLs, Campaign_ID, Channel_ID

		for(int row=0;row<sheet.getRows();row++)
		{
			//c=0 is fileLocation passed in method FetchDataFromExcelSheet, so counting from c=1
			for (int c = 0; c < columnNo; c++)
			{
				try
				{
					String strAd_Format = sheet.getCell(cellFind.findLabelCell(columns[c]).getColumn(), row).getContents().toString().trim();
					testDataProvider[row][c] = strAd_Format;

					//System.out.print(strAd_Format + "   :   ");
				}catch(NullPointerException n)
				{
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : There may be a Leading or Trailing SPACE in this cell: " + "row: "+row + "  and coulmn: "+columnNo );
					n.printStackTrace();
				}
			}
			//logger.info();
		}	   

		book.close();
		return testDataProvider;				
	}


	//******************** This Method Will Copy The Sikuli-IDE.app files to /Applications Folder In Mac Only *************************************************//
	public static void SetUpSikuli()  
	{
		try
		{
			if(System.getProperty("os.name").matches("^Windows.*"))
			{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Environment Variable has been already setup for: "+System.getProperty("os.name"));
			}
			else
			{
				//Applications Folder In Mac:
				File sikuliIDE = new File("/Applications/Sikuli-IDE.app");

				String sikuliSourceLocation = SuiteClass.AUTOMATION_HOME.concat("/tpt/Sikuli-IDE.app");
				String sikuliDestination = "/Applications/";

				if(!(sikuliIDE.exists()))
				{
					//String command = "cp -r /Users/user/Desktop/Automation/qascripting/Vdopia_Automation/tpt/Sikuli-IDE.app /Applications/";

					String command = "cp -r " + sikuliSourceLocation + " " + sikuliDestination;
					ExecuteCommands.ExecuteCommand_ReturnsOutput(command);

					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Sikuli has been setup successfully in: "+ System.getProperty("os.name"));
				}
				else
				{
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Sikuli already exists in: "+ System.getProperty("os.name"));
				}
			}
		}
		catch(Exception e)
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occured while setting up sikuli. ", e);
		}
	}



	//******************** This Method Will open Result file at the end    *************************************************//
	public static void OpenResult(String HtmlFilename) 
	{
		try{
			Desktop.getDesktop().open(new File(HtmlFilename));
		}
		catch(Exception e)
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : ", e);
		}
	}



	//******************** This Method Will Create the directory at the given location *************************************************//
	public static boolean CreateDirectory(String directoryLocation)
	{
		File checkLocation = new File(directoryLocation);
		boolean b = false;

		if(!(checkLocation.exists()))
		{
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Log folder doesn't exist at " +logFileLocation);
			b = checkLocation.mkdirs();

			if(b)
			{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Directory is created at: "+checkLocation);
				return b;
			}
			else
			{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Directory wasn't created at: "+checkLocation);
				return b;
			}
		}
		else
		{
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Directory: " +checkLocation + " already exists.");
			return true;
		}
	}



	//******************** This Method Will Return All The Files Matching The Given Extension *************************************************//
	@SuppressWarnings("finally")
	public static String GetFilesWithSpecificExtensionFromDirectory(String directory, final String fileExtension)
	{
		String fileNames = "";
		String[] fileList = null;

		try
		{
			File file = new File(directory);

			fileList = file.list(new FilenameFilter()
			{	
				@Override
				public boolean accept(File dir, String name) 
				{
					return name.endsWith(fileExtension);
				}
			});

			if(fileList.length < 1)
			{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : No File was found having extension: "+fileExtension);
			}
			else if(fileList.length == 1)
			{
				fileNames = fileList[0].toString();
			}
			else
			{
				//Print file list
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Printing List Of Files Found Having Extension: "+fileExtension);

				for(int i=0; i<fileList.length; i++)
				{
					fileNames = fileNames + fileList[i].toString();

					if(i<fileList.length - 1)
					{
						fileNames = fileNames + ",";
					}

					logger.info(fileList[i].toString());
				}
			}
		}
		catch(Exception e)
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception handled by method: GetFileWithSpecificExtensionInDirectory. ", e);
		}
		finally
		{
			return fileNames;
		}

	}



	//*********** This method will be used to find a text in a file. ******************
	@SuppressWarnings({ "finally" })
	public static boolean FindTextInFile(File desiredFile, String desiredText) throws IOException
	{
		boolean flag = false;
		try
		{
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Text: "+desiredText + " is being searched in file: "+desiredFile);
			BufferedReader reader = new BufferedReader(new FileReader(desiredFile));

			String line = "";

			while((line = reader.readLine()) != null)
			{
				if(line.contains(desiredText))
				{
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : desired text: "+ desiredText+ " is found in line: "+line);
					flag = true;
					break;
				}
			}
			reader.close();
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception handled while searching text: "+desiredText+" in file: "+desiredFile.toString(), e);
		}
		finally
		{
			return flag;
		}
	}

	
	/** This method will be used to get the whole content of file into a string
	 * 
	 * @param desiredFile
	 * @return
	 */
	public static StringBuilder ReadContentOfFile(String desiredFile) 
	{
		StringBuilder content = new StringBuilder("");
		String line = "";

		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(desiredFile));

			while((line = reader.readLine()) != null)
			{
				content = content.append(line); 
			}
			reader.close();
		}
		catch(Exception e)
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occured while reading file: "+desiredFile, e);
		}
		return content;
	}



	//*********** This method will be used to wait for a text to appear in file. ******************
	@SuppressWarnings("finally")
	public static boolean WaitForTextToAppearInFile(File desiredFile, String desiredText, int waitSeconds)
	{
		boolean flag = false;
		try
		{
			for(int i=0; i<waitSeconds; i++)
			{
				try
				{
					flag = FindTextInFile(desiredFile, desiredText);
				}
				catch(Exception e)
				{
					Thread.sleep(1000);
				}

				if(flag == true)
				{
					break;
				}
				else
				{
					Thread.sleep(1000);
					continue;
				}
			}
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception handled while waiting for text: "+desiredText + " to appear in file. ", e);
			Thread.sleep(1000);
		}
		finally
		{
			return flag;	
		}

	}



	/*********** This method will be used to copy all (only) files (not sub directory) of source directory to destination dir
	 * 
	 * @param sourceFile
	 * @param destinationDir
	 * @return
	 */
	@SuppressWarnings("finally")
	public static boolean CopyAllFilesToDirectory(String sourceFile, String destinationDir)
	{
		boolean flag = false;
		try
		{
			File sourceDir = new File(sourceFile); 
			File copyDir = new File(destinationDir);

			//Getting all files located in source directory
			String []requiredFiles = sourceDir.list();

			//Copying all sub files 
			for(int i=0; i<requiredFiles.length; i++)
			{

				File tempFile = new File(sourceDir+"/"+requiredFiles[i]);

				if(!tempFile.isDirectory())
				{
					FileUtils.copyFileToDirectory(tempFile, copyDir);
				}
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : File: "+tempFile.toString() + " is copied successfully to directory: "+copyDir.toString());
			}

			flag = true;
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : All files are copied. ");
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception handled by method: CopyAllFilesToDirectory. ", e);
		}
		finally
		{
			return flag;
		}
	}



	//*********** This method will be used to check a specific file in a directory
	@SuppressWarnings("finally")
	public static boolean CheckFileInDirectory(String directory, String checkFileName)
	{
		boolean flag = false;
		try
		{
			File sourceDir = new File(directory); 

			//Getting all files located in source directory
			String []requiredFiles = sourceDir.list();

			//Copying all sub files 
			for(int i=0; i<requiredFiles.length; i++)
			{
				File tempFile = new File(sourceDir+"/"+requiredFiles[i]);

				if(!tempFile.isDirectory())
				{
					//Checking if desired file exists in given directory
					if(requiredFiles[i].equalsIgnoreCase(checkFileName))
					{
						flag = true;
						logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : File: "+checkFileName + " exists in directory: "+directory);
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exceptin handled by method: CheckFileInDirectory. ", e);
		}
		finally
		{
			return flag;
		}
	}



	//*********** This method will replace unique and single line in file containing a particular text in that line ******************
	@SuppressWarnings({ "finally" })
	public static boolean ReplaceSingleLineInFile(String propertyFile, String desiredTextContained, String desiredLine) throws IOException
	{
		String line = "";
		String actualText = "";
		File desiredFile = new File(propertyFile);

		boolean flag = false;
		boolean occurence = true;
		try
		{	
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : File: "+desiredFile+" is being replaced by the expected lines...");

			BufferedReader reader = new BufferedReader(new FileReader(desiredFile));

			while((line = reader.readLine()) != null)
			{
				if(occurence)
				{
					if(line.contains(desiredTextContained))
					{	
						//get the file content appended in a string
						line = desiredLine;

						//This flag will make sure to replace only first occurrence of desired line
						occurence = false;
					}
				}

				actualText =  actualText + line  + "\n";
			}

			reader.close();	

			//Writing file 
			FileWriter writer = new FileWriter(desiredFile);
			writer.write(actualText);
			writer.close();

			flag = true;
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : File: " +desiredFile+ " has been replaced with desired lines successfully. ");
		}
		catch(Exception e)
		{
			flag = false;
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : ********** Exception Handled By Method: ReplaceSingleLineInFile. ", e);
		}
		finally
		{
			return flag;
		}

	}



	/** This method will write the supplied text in given file in overwrite mode. 
	 * 
	 * @param fileNameWithLocation
	 * @param strText
	 * @return
	 */
	@SuppressWarnings("finally")
	public static boolean WriteTextInFile(String fileNameWithLocation, String strText)
	{
		boolean flag =  false;

		try
		{
			logger.debug(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Received file location: "+fileNameWithLocation);
			String dir = StringLib.splitDirectoryFromFileLocation(fileNameWithLocation);
			logger.debug(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Splitted directory location: "+dir);

			//Get the directory from file location 
			File directory = new File(dir);

			//and check if directory exists
			if(!(directory.exists()))
			{
				//If not then create directory
				if(directory.mkdirs())
				{
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Directory: "+directory + " wasn't existed, its created now. ");
				}
			}

			File file = new File(fileNameWithLocation);

			//And then create file if it doesn't exist
			if(!(file.exists()))
			{
				if(file.createNewFile())
				{
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : File: " +fileNameWithLocation + " wasn't existed, its created now. ");
				}
			}


			//Write content in file
			FileWriter writer = new FileWriter(fileNameWithLocation);
			writer.write(strText);
			writer.close();

			flag = true;
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occured while writing text in file. ", e);
		}
		finally
		{
			return flag;
		}
	}


	//*********** This method will create a new file at given location ******************
	@SuppressWarnings("finally")
	public static boolean CreateNewFile(String fileNameWithLocation)
	{
		boolean flag =  false;

		try
		{
			//Get the directory from file location 
			File directory = new File(StringLib.splitDirectoryFromFileLocation(fileNameWithLocation));

			//and check if directory exists
			if(!(directory.exists()))
			{
				//If not then create directory
				if(directory.mkdirs())
				{
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Directory: "+directory + " wasn't existed, its created now. ");
				}
			}

			File file = new File(fileNameWithLocation);

			//And then create file if it doesn't exist
			if(!(file.exists()))
			{
				if(file.createNewFile())
				{
					flag = true;
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : File: " +fileNameWithLocation + " wasn't existed, its created now. ");
				}
			}
			else
			{
				flag = true;
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : File: " +fileNameWithLocation + " already existed. ");
			}
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception handled by method: CreateNewFile. ", e);
		}
		finally
		{
			return flag;
		}
	}



	//*********** This method will return the last modified file at the given directory ******************
	@SuppressWarnings("finally")
	public static String GetLastModifiedFile(String directory, String fileExtension)
	{
		String modifiedFile = "NO_FILE"; 
		try
		{
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Searching the last modified file at location: "+directory);

			File dir = new File(directory);

			FileFilter fileFilter = new WildcardFileFilter("*."+fileExtension);
			File[] files = dir.listFiles(fileFilter);

			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

			modifiedFile = files[0].toString();

			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Found the last modified file: " +modifiedFile);
		}
		catch(Exception e)
		{
			modifiedFile = "NO_FILE";
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception handled by method: GetLastModifiedFile. ", e);
		}
		finally
		{
			return modifiedFile;
		}
	}

	/** Get clipboard text
	 * 
	 * @return
	 */
	public static String getClipBoardText()
	{
		String text= "";

		try {
			text = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (Exception e){
			logger.error(e.getMessage(), e);
		}	
		return text;
	}
}
