/**
 * Last Changes Done on Feb 20, 2015 3:32:54 PM
 * Last Changes Done by ${author}
 * Purpose of change: Changed Name of class, adding few methods
 * 
 * Sample javascript to check xpath in Browser Console:
var element = document.evaluate("//a[@id='clients' and @class='dropdown-toggle toggle-active']" ,document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue;
if (element != null) {
  element.click();
};
 *
 *
 * Sample javascript to check the top window of in webpage
function myFunction() { if (window.top != window.self)  { alert('No! Top Window');} 
else { alert('Yes! Top Window'); } }; 
myFunction(); 
 *
 *  Sample Java Script:
 *  
 *  				String javaScript = "document.getElementsByClassName('icon-cog')[5].scrollIntoView();";
				elementHandler.executeJavaScript(driver, javaScript, null);
				elementHandler.executeJavaScript(driver, "arguments[0].click()", webelement);
				javaScript = "arguments[0].scrollIntoView(false);";
 *  
 *  
 */


package core.classes;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.Connection;

import core.utilities.CaptureScreenShotLib;
import core.utilities.CustomException;
import core.utilities.GenericMethodsLib;
import core.utilities.IntegerLib;
import core.utilities.httpClientWrap;
import lenskart.tests.DataObject;
import lenskart.tests.TestSuiteClass;
import net.lightbody.bmp.proxy.ProxyServer;

public class HandlerLib 
{
	Logger logger = Logger.getLogger(HandlerLib.class.getName());


	/** This method will be called by keyword class while performing operations, almost all thrown selenium exception will be handled 
	 * by explicit webdriver wait. this method accepts by locator, and desired exception to be handled.
	 * 
	 *  
	 * @param driver
	 * @param webelement
	 * @param bylocator
	 * @param e
	 * @return
	 */
	public boolean applyExplicitWait(WebDriver driver, By bylocator, Exception e)
	{
		boolean flag = false;
		try
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Explicit Wait for 60 seconds for exception: " + e.getClass().getName() +" is being applied ");

			WebDriverWait wait = new WebDriverWait(driver, 60);

			if(e instanceof ElementNotVisibleException)
			{
				wait.until(ExpectedConditions.visibilityOfElementLocated(bylocator));
			}
			else if(e instanceof WebDriverException)
			{
				wait.until(ExpectedConditions.visibilityOfElementLocated(bylocator));
				wait.until(ExpectedConditions.elementToBeClickable(bylocator));
			}
			else if(e instanceof NoSuchElementException)
			{
				wait.until(ExpectedConditions.presenceOfElementLocated(bylocator));
			}

			flag = true;
		}
		catch(TimeoutException t){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ " : " + t.getMessage());
		}
		catch(Exception ex)
		{
			flag = false;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while performing external wait. ", ex);
		}

		logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" fixed explicit wait finish. ");
		return flag;
	}


	/** This method will be called by keyword class while performing operations, almost all thrown selenium exception will be handled 
	 * by explicit webdriver wait. this method accepts by locator, and desired exception to be handled.
	 * 
	 * @param driver
	 * @param bylocator
	 * @param e
	 * @param timeout
	 * @return
	 */
	public boolean applyExplicitWait(WebDriver driver, By bylocator, Exception e, long timeoutSeconds)
	{
		boolean flag = false;
		try
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Explicit Wait for time - "+timeoutSeconds +" seconds for exception: " + e.getClass().getName() +" is being applied ");

			WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);

			if(e instanceof ElementNotVisibleException)
			{
				wait.until(ExpectedConditions.visibilityOfElementLocated(bylocator));
			}
			else if(e instanceof WebDriverException)
			{
				wait.until(ExpectedConditions.visibilityOfElementLocated(bylocator));
				wait.until(ExpectedConditions.elementToBeClickable(bylocator));
			}
			else if(e instanceof NoSuchElementException)
			{
				wait.until(ExpectedConditions.presenceOfElementLocated(bylocator));
			}

			flag = true;
		}
		catch(WebDriverException w)
		{
			if(w instanceof UnhandledAlertException)
			{
				org.openqa.selenium.Alert alert = driver.switchTo().alert();
				String text = alert.getText();
				alert.accept();

				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unhandled alert found having text: "+text+ ", re-applying explicit wait ...",w);
				applyExplicitWait(driver, bylocator, e, timeoutSeconds);
			}
		}
		catch(Exception ex)
		{
			flag = false;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while performing external wait. ", ex);
		}

		logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : received explicit wait finish. ");

		return flag;
	}

	/** This method will get the difference in supplied dates like date1 - date2
	 * and return the difference in days
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 * @throws ParseException 
	 */
	public long getDateDiff(Date dateFirst, Date dateSecond) throws ParseException
	{
		long duration = 0;

		//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		//		Date dateFirst = format.parse(date1);
		//		Date dateSecond = format.parse(date2);

		duration = TimeUnit.MILLISECONDS.toDays((dateFirst.getTime() - dateSecond.getTime()));

		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : date difference in days: "+duration);

		return duration;
	}


	/** This method will wait until a particular text is displayed in video upload screen, to make sure  video is 
	 * successfully uploaded.
	 * 
	 * @param elementXPathLocator
	 * @param desiredText
	 */
	public String waitForUploadVideoText(WebDriver driver, By byElement, String desiredText) 
	{
		String result = "";
		boolean flag = false;
		WebElement desiredWebElement = null;

		try
		{	
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Desired Text: "+desiredText );

			//Adding explicit wait for webelement having upload text to appear
			WebDriverWait wait = new WebDriverWait(driver, 600);
			wait.until(ExpectedConditions.presenceOfElementLocated(byElement));			

			for(int i=0; i<600; i ++)
			{
				desiredWebElement = driver.findElement(byElement); 

				String dynamicText = desiredWebElement.getText().trim();

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Print Text: " +dynamicText);

				if(dynamicText.toLowerCase().contains(desiredText.toLowerCase().trim()))
				{
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Desired text is found: " +dynamicText);
					flag = true;
					break;
				}

				Thread.sleep(1000);
			}
			if(flag)
			{
				result = "Pass: Video was uploaded successfully. ";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Video is uploaded and notified for Video Ad: ");
			}
			else
			{
				result = "Fail: Video wasn't uploaded. ";
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Video uploaded message was not found. ");
			}	
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred. ", e);
		}
		return result; 
	}

	/** This method will check if there is any alert is present.
	 * 
	 * @param driver
	 * @return
	 */
	public boolean checkIfAlertPresent(WebDriver driver)
	{
		try
		{
			driver.switchTo().alert();
			logger.warn(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Alert is displayed having text: " +driver.switchTo().alert().getText());

			driver.switchTo().alert().accept();
			return true;
		}
		catch(NoAlertPresentException ex)
		{
			return false;
		}
		catch (Exception e) {
			return false; 
		}
	}


	/** This method will execute the javascript to perform the operation.
	 * This method returns the object --> which can store boolean, string and integer.
	 * As it is returning false in case of exception, so at the time of using this method, always use condition like:
	 * if((Boolean) objJsOutput){ } 
	 * 
	 * @param driver
	 * @param javaScript
	 * @param webelement
	 * @return
	 */
	public Object executeJavaScript(WebDriver driver, String javaScript, WebElement webelement)
	{
		/**
		 * Sample code, if javascript has to be executed on a webelement:
		 * js.executeScript("arguments[0].click()", webelement);
		 */

		Object objJsOutput = null;
		try
		{
			JavascriptExecutor js = (JavascriptExecutor) driver;

			if(webelement != null)
			{
				objJsOutput = js.executeScript(javaScript, webelement);
			}
			else
			{
				objJsOutput = js.executeScript(javaScript);
			}
		}
		catch(WebDriverException w)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while executing java script: "+javaScript +" for supplied element: "+webelement);
		}
		catch(Exception ex)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while executing java script: "+javaScript +" for supplied element: "+webelement, ex);
		}
		return objJsOutput;
	}


	/** This method will be used to click on supplied web element using Mouse Action.	 * 
	 * @param driver
	 * @param webelement
	 * @return
	 */
	public boolean performMouseAction(WebDriver driver, WebElement webelement)
	{
		try{
			Actions action = new Actions(driver);
			action.moveToElement(webelement)
			.click()
			.build()
			.perform();

			return true;
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while performing mouse action. ", e);
			return false;
		}
	}


	/**
	 * This method will be used to replace the dynamic parameters from the
	 * supplied objects and data. Any dynamic input will be like 
	 * #time# -- current time stamp
	 * #TC_02_03# -- data used in step TC_02_03
	 * #random# -- random value having range 1-100
	 * #nonzero_random# -- alphanumeric random string excluding zero 
	 * 
	 * @param data
	 * @param testStepID_InputData
	 * @return
	 */
	public String dataParser(String data, String keyword, HashMap<String, String> testStepID_InputData, Connection connection)
	{
		try{

			Pattern pattern = Pattern.compile("#([\\sa-zA-Z0-9_-]+)#");
			Matcher match = pattern.matcher(data);

			while(match.find())
			{	
				String matchString = match.group();

				String key = matchString.replace("#", "").toLowerCase().trim();
				String value = "";

				/** in case time stamp is passed */
				if(key.equalsIgnoreCase("time"))
				{
					value = GenericMethodsLib.DateTimeStamp("MMddyy_hhmmss");
				}
				/** random parameter #random# is passed */
				else if(key.equalsIgnoreCase("random"))
				{
					value = String.valueOf(IntegerLib.GetRandomNumber(5, 1));
				}
				/** random parameter #nonzero_random# is passed */
				else if(key.equalsIgnoreCase("nonzero_random"))
				{
					value = GenericMethodsLib.DateTimeStamp("MMM").concat("_"+GenericMethodsLib.DateTimeStamp("hhmmss").replace("0", "z"));
				}
				/** get date for the days given in the input sheet */
				else if(key.contains("currentdate"))
				{
					data=GenericMethodsLib.getDateInString(data);
				}
				else
				{
					value = testStepID_InputData.get(key);

					if(value == null)
					{
						value = "";
						logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received step id: "+key + " wasn't found in map, reassigning to space. ");
					}
				}
				data = data.replace(matchString, value);
			}

			/** now check if the received data is a sql query, if yes then execute it and then return the output as final data
			 * but don't execute query for verifydbdetails keyword, coz this keyword expects a query */
			if(data.toLowerCase().trim().startsWith("select") && data.contains("from") && !keyword.equalsIgnoreCase("verifydbdetails") )
			{
				String queryResult = GenericMethodsLib.ExecuteMySQLQueryReturns1DArray(connection, data)[0];

				if(queryResult == null){
					data = "";
					logger.error("Received null output of query: "+data + ", re-assigning it as space. ");
				}else{
					data = queryResult;
				}
			}
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while parsing supplied string: ", e);
		}
		return data;
	}

	/** This method will parse the expected data -- to be used in verifydb keyword, with this change, user can 
	 * supply query even for expected data also, this method will return the final expected data.
	 * 
	 * @param data
	 * @param connection
	 * @return
	 */
	public String dataParser(String data, Connection connection)
	{
		String [] records = null;
		try {
			records = GenericMethodsLib.ExecuteMySQLQueryReturns1DArray(connection, data);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		if(records != null)
		{
			return records[0];
		}
		else
		{
			return data;
		}
	}

	/** This method will type values slowly in the supplied element.
	 * 
	 * @param webelement
	 * @param data
	 * @return
	 */
	public boolean typeSlowly(WebElement webelement, String data)
	{
		boolean flag = true;
		try
		{
			for(int i=0;i<data.length();i++)
			{
				String strChannelName = data.substring(i, i+1);
				webelement.sendKeys(strChannelName);
				Thread.sleep(250);
			}
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while typing data: "+data, e);
		}
		return flag;
	}


	/** This method will return the video location to be uploaded based on supplied video file name
	 * 
	 * @param data
	 * @return
	 */
	public String getUploadVideoLocation(String data)
	{
		/**
		 * Get extension of supplied file
		 */
		String extension = data.substring(data.indexOf(".")+1, data.length()).toLowerCase().trim();

		String videoLocation = TestSuiteClass.AUTOMATION_HOME.toString().concat("\\tc_data\\sample_videos");

		switch (extension)
		{
		case "mp4":

			data = videoLocation.concat("\\mp4\\"+data);
			break;

		case "avi":

			data = videoLocation.concat("\\avi\\"+data);
			break;

		case "m4v":

			data = videoLocation.concat("\\m4v\\"+data);
			break;

		case "mov":

			data = videoLocation.concat("\\mov\\"+data);
			break;

		case "mpeg":

			data = videoLocation.concat("\\mpeg\\"+data);
			break;

		case "rv":

			data = videoLocation.concat("\\rv\\"+data);
			break;

		case "rm":

			data = videoLocation.concat("\\rm\\"+data);
			break;

		default:

			data = "";
			break;
		}


		if(!System.getProperty("os.name").matches("^Windows.*"))
		{
			data = data.replace("\\", "/");
		}

		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Video location is being returned = "+data);
		return data;

	}


	/** This method will return the image location to be uploaded based on supplied image file name
	 * 
	 * @param data
	 * @return
	 */
	public String getUploadImageLocation(String data)
	{
		/**
		 * Get extension of supplied file
		 */
		String extension = data.substring(data.indexOf(".")+1, data.length()).toLowerCase().trim();
		String imageLocation = TestSuiteClass.AUTOMATION_HOME.toString().concat("\\tc_data\\sample_images");

		switch (extension)
		{
		case "jpg":

			data = imageLocation.concat("\\jpg\\"+data);
			break;

		case "png":

			data = imageLocation.concat("\\png\\"+data);
			break;

		default:

			data = imageLocation.concat("\\jpg\\"+data);
			break;
		}

		if(!System.getProperty("os.name").matches("^Windows.*"))
		{
			data = data.replace("\\", "/");
		}

		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Image location is being returned = "+data);
		return data;

	}


	/** This method will wait for an alert to be present.
	 * 
	 * @param driver
	 * @return
	 */
	public boolean waitForAlert(WebDriver driver)
	{
		boolean flag;

		try
		{
			WebDriverWait wait = new WebDriverWait(driver, 10);		
			wait.until(ExpectedConditions.alertIsPresent());

			flag = true;
		}
		catch(Exception e)
		{
			flag = false;
		}

		return flag;
	}


	/** Getting data after removing any flag like "must pass"
	 * 
	 * @param data
	 * @return
	 */
	public String parseMustPassFlag(String data)
	{
		List<String> dataList = new ArrayList<String>();

		if(data.contains(";")){
			dataList = new ArrayList<>(Arrays.asList(data.split(";")));
		}
		else if(data.contains(",")){
			dataList = new ArrayList<>(Arrays.asList(data.split(",")));
		}

		/** Check the last item in the dataList */
		if(dataList.get(dataList.size()-1).trim().equalsIgnoreCase("must pass"))
		{
			int mustPassFlag = dataList.size()-1;
			dataList.remove(mustPassFlag);
		}

		return dataList.toString().replace("[", "").replace("]", "").trim();
	}


	/**
	 * This keyword will verify the existence of supplied web element(s), multiple elements 
	 * can be supplied separated by comma(,) or semicolon(;).
	 * 
	 * @param objectName
	 * @param data
	 * @param driver
	 * @param webelement
	 * @param getObject
	 * @param handler
	 * @param applyRules
	 * @param passed_status
	 * @param failed_status
	 * @param locationToSaveSceenShot
	 * @return
	 */
	public String verifyElementPresent(String objectName, String data, WebDriver driver, WebElement webelement, GetObjects getObject, 
			HandlerLib handler, String passed_status, String failed_status, String locationToSaveSceenShot, JSONObject jsonObjectRepo)
	{
		String result = "";
		List<String> notPresentObjectList = new ArrayList<String>();

		try{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Checking presence of supplied element: "+objectName);

			boolean listFlag = false;
			List<String> suppliedObjectList = new ArrayList<String>();

			/** if comma separated objects are supplied then splitting them into a list. */
			if(objectName.contains(","))
			{
				listFlag = true;
				suppliedObjectList = new ArrayList<String>(Arrays.asList(objectName.split(",")));
			}
			else
			{
				listFlag = false;
				boolean iselementDisplayed = false;

				webelement = new GetObjects().getFinalWebElement(driver, objectName, data, jsonObjectRepo);
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Element is: " + webelement);

				if(webelement == null)
				{
					iselementDisplayed = false;

					/** Taking screenshot during exception */
					CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : received webelement as null, Screenshot is captured at: "+locationToSaveSceenShot);

					result = failed_status + "Element is not present. ";
				}
				else
				{
					/** Checking if element is displayed */
					try{
						iselementDisplayed = webelement.isDisplayed();
					}catch(StaleElementReferenceException e){

						webelement = new GetObjects().getFinalWebElement(driver, objectName, data, jsonObjectRepo);
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " Handling StaleElementReferenceException ... retried to find element ... ");

						iselementDisplayed = webelement.isDisplayed();
					}

					if(iselementDisplayed) {
						result = passed_status + "Element is present. ";
					}else {
						/** if element was not null, then only wait for visibility */
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " element is not null, explicit waiting for visiblity..  ");

						By byLocator = new GetObjects().getFinalByLocator(driver, objectName, data, jsonObjectRepo);
						handler.applyExplicitWait(driver, byLocator, new ElementNotVisibleException(""));

						if(driver.findElement(byLocator).isDisplayed()) {
							result = passed_status+ "Element is present. ";
						}else {
							result = failed_status + "Element is not present. ";

							logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " element not found, taking screenshot ... ");
							CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
						}
					}
				}
			}

			/** Iterating list and collecting not present objects into notPresentObjectList list, in case of InvocationTargetException exception
			 * also, adding object into  notPresentObjectList list
			 */
			if(listFlag)
			{
				for(int i=0; i<suppliedObjectList.size(); i++)
				{
					/** Catching InvocationTargetException exception in case webelement is not found on webpage.
					 */
					try{
						objectName = suppliedObjectList.get(i);
						webelement = new GetObjects().getFinalWebElement(driver, objectName, data, jsonObjectRepo);

						if(!webelement.isDisplayed())
						{
							notPresentObjectList.add(suppliedObjectList.get(i));
						}
					}catch(NullPointerException e)
					{		
						notPresentObjectList.add(suppliedObjectList.get(i));
						logger.info(suppliedObjectList.get(i) + " wasn't found on web page. ");

						/** Taking screenshot during exception */
						CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
					}
				}

				/** checking if notPresentObjectList's size, if this is empty then its pass 
				 * else fail it.
				 */
				if(notPresentObjectList.toString().replace("[", "").replace("]", "").trim().isEmpty())
				{
					result = passed_status + "All supplied elements were present. ";
				}
				else
				{
					/** writing not present objects in results. */
					result = failed_status + "Element(s): "+notPresentObjectList +" was(were) not present. ";
				}
			}
		}
		catch(Exception e)
		{
			if(e instanceof CustomException)
			{
				result = e.getMessage();
			}
			else
			{
				result = failed_status + "Couldn't check the presence of element. ";
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while checking the presence of element. ", e);

				/** Taking screenshot during exception */
				CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
			}
		}

		return result;

	}	


	/** new keyword to replace the existing verifyelementpresent -- reduce wait time 
	 * 
	 * @param objectName
	 * @param data
	 * @param driver
	 * @param webelement
	 * @param getObject
	 * @param handler
	 * @param passed_status
	 * @param failed_status
	 * @param locationToSaveSceenShot
	 * @param jsonObjectRepo
	 * @return
	 */
	public String verifyElementPresentNew(String objectName, String data, WebDriver driver, WebElement webelement, GetObjects getObject, 
			HandlerLib handler, String passed_status, String failed_status, String locationToSaveSceenShot, JSONObject jsonObjectRepo)
	{
		String result ="";
		boolean iselementDisplayed = false;
		try{

			webelement = new GetObjects().getFinalWebElement(driver, objectName, data, jsonObjectRepo);

			try{
				iselementDisplayed = webelement.isDisplayed();
			}catch(NullPointerException e){
				iselementDisplayed = false;

				/** Taking screenshot during exception */
				CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : element threw NullPointer - Screenshot is captured at: "+locationToSaveSceenShot);
			}

			if(iselementDisplayed){
				result = passed_status + "Element is present";
			}else{
				result = failed_status + "Element is not present";
			}

		}catch(Exception e){
			result = failed_status + "Couldn't check the presence of element. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while checking the presence of element. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	public String selectCheckbox(WebDriver driver, String objectName, WebElement webelement, String data, String locationToSaveSceenShot, 
			String passed_status, String failed_status, JSONObject jsonObjectRepo)
	{			
		String result; 

		By byLocator = null;

		try{
			/** create dynamic element */
			byLocator = new GetObjects().getFinalByLocator(driver, objectName, data, jsonObjectRepo);
		}catch(CustomException e)
		{
			/** create element normally */
			try {
				byLocator = new GetObjects().getFinalByLocator(driver, objectName, data, jsonObjectRepo);
			} catch (CustomException e1) { logger.error(e.getMessage()); }
		}

		try{
			/** apply explicit wait to handle exceptions */
			new HandlerLib().applyExplicitWait(driver, byLocator, new WebDriverException(""));

			webelement = driver.findElement(byLocator);
			if(!webelement.isSelected()){
				webelement.click();
			}

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Clicked checkbox is: " +webelement);
			result = passed_status+ "Clicked checkbox successfully";
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't click checkbox";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while clicking checkbox : " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}


	/** adding a logic to parse the data - for example if a test step has to be executed based on certain test condition 
		then use expression like: expression ([Connection_Type]=VAST/VPAID/XYZ?DEAL12435) -->
		in this case first the macro [Connection_Type] will be replaced and then its value will be compared with VAST and if matched then 
		use DEAL12345 for that step else skipThisTest.
	 * @param data
	 * @return
	 */
	public String parseTestDataExpression (String data)
	{
		data = data.replace("expression", "").replace("(", "").replace(")", "").trim();

		String defaultValue = "";
		try{defaultValue = data.split("\\?")[1].trim();}
		catch(ArrayIndexOutOfBoundsException a){}

		/** get the whole condition */
		String condition = data.split("\\?")[0].trim();

		/** further split condition by && and then evaluate this */
		String [] conditions = condition.split("&&");

		boolean expressionFlag;
		for(String expression : conditions)
		{
			expressionFlag = getExpressionFlag(expression);

			if(!expressionFlag)
			{
				data = "skipTestStep";
				break;
			}else{
				data = defaultValue;
			}
		}

		return data;
	}


	/** This method parses the test data expression flag and return the true or false
	 * 
	 * @param condition
	 * @return
	 */
	public boolean getExpressionFlag(String condition)
	{
		boolean flag = false;

		String actualValue = condition.split("=")[0].trim();
		String expectedValue = condition.split("=")[1].trim();		

		/** in case multiple values are given as a list of expected values */
		List<String> expectedValuesList = new ArrayList<>();

		String [] expectedValueArray = expectedValue.split("/");

		for(int i=0; i<expectedValueArray.length; i++)
		{
			expectedValuesList.add(expectedValueArray[i].trim().toLowerCase());
		}

		if(expectedValuesList.contains(actualValue.toLowerCase()))
		{
			flag = true;
		}

		return flag;
	}

	/** Get broken links 
	 * 
	 * @param urlList
	 * @return
	 */
	public List<String> getBrokenLinks(List<String> urlList)
	{
		List<String> brokenLinks = new ArrayList<>();

		for (int i = 0; i < urlList.size(); i++) {

			if (!urlList.get(i).contains("javascript:void")){

				if (httpClientWrap.getStatusCodeOfGetRequest(urlList.get(i), null)!=200){
					brokenLinks.add(urlList.get(i));
				}
			}	
		}

		return brokenLinks;
	}


	/** This method will be used to handle conditions for specific web elements.  
	 * 
	 * @param driver
	 * @param objectName
	 * @param webelement
	 * @return
	 */
	public String getCaptcha(WebElement webelement, WebDriver driver, By bylocator)
	{
		/** if there is any exception thrown while clicking link, then reattempt after catching that exception */
		String captchaText = "";
		String sourceValue = "";
		try{
			sourceValue=webelement.getAttribute("src");
			if (sourceValue.contains("numcaptcha1.jpg")) {
				captchaText = "12985";
			} else if (sourceValue.contains("numcaptcha2.jpg")) {
				captchaText = "76045";

			} else if (sourceValue.contains("numcaptcha3.jpg")) {
				captchaText = "84673";

			} else if (sourceValue.contains("numcaptcha4.jpg")) {
				captchaText = "50365";

			} else if (sourceValue.contains("numcaptcha5.jpg")) {
				captchaText = "20746";

			} else if (sourceValue.contains("numcaptcha6.jpg")) {
				captchaText = "50251";

			} else if (sourceValue.contains("numcaptcha7.jpg")) {
				captchaText = "40985";

			} else {
				captchaText = "Not Found";
			}

		}catch(WebDriverException w){
			new HandlerLib().applyExplicitWait(driver, bylocator, new WebDriverException());
			sourceValue=webelement.getAttribute("src");
			if (sourceValue.contains("numcaptcha1.jpg")) {
				captchaText = "12985";
			} else if (sourceValue.contains("numcaptcha2.jpg")) {
				captchaText = "76045";

			} else if (sourceValue.contains("numcaptcha3.jpg")) {
				captchaText = "84673";

			} else if (sourceValue.contains("numcaptcha4.jpg")) {
				captchaText = "50365";

			} else if (sourceValue.contains("numcaptcha5.jpg")) {
				captchaText = "20746";

			} else if (sourceValue.contains("numcaptcha6.jpg")) {
				captchaText = "50251";

			} else if (sourceValue.contains("numcaptcha7.jpg")) {
				captchaText = "40985";

			} else {
				captchaText = "Not Found";
			}

		}

		return captchaText;
	}


	/** expression parser - when received condition is like: SUBSTING contains STRING
	 * This method will take care of parsing and removing any trailing spaces 
	 * 
	 * @param xyz
	 * @return
	 */
	public String parseSubExpression(String xyz)
	{
		String [] xx = xyz.split(" ");
		List<String> list = new ArrayList<>();

		for(int i=0; i< xx.length; i++)
		{
			String abc = xx[i];
			list.add(abc.trim());
		}

		String rishi ="";

		for(int i=0; i<list.size(); i++) {
			if(!list.get(i).trim().isEmpty()) {
				rishi = (rishi + " " + list.get(i)).trim() ;
			}
		}

		return rishi.trim();
	}


	/** parse and evaluate the sub expression 
	 * 
	 * @param xyz
	 * @return
	 */
	public boolean evaluateSubExpression(String xyz)
	{
		System.out.println();
		boolean conditionResult = false;
		try
		{
			String [] xx = xyz.split(" ");

			String data1 = xx[0].trim();
			String condition = xx[1].trim();
			String data2 = xx[2].trim();



			SubExpressions expressionObj = new SubExpressions();

			//Method conditionMethod = SubExpressions.class.getMethod(condition, STRING.class, String.class);

			Method conditionMethod = null;
			for(Method m : SubExpressions.class.getMethods())
			{
				if(m.getName().equalsIgnoreCase(condition))
				{
					conditionMethod = m; 
					break;
				}
			}

			conditionResult = (boolean) conditionMethod.invoke(expressionObj, data1, data2);
		}
		catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ " -- " +e.getMessage(), e);
		}
		return conditionResult;
	}

	/** This method is used to store data in serialized json form to process further using jackson api 
	 * 
	 * @param data
	 * @return
	 */
	public synchronized String storedataobject_UsingJacksonMapper(String data){

		try{
			if(!data.isEmpty()){
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" data json is " + data);

				ObjectMapper mapper = new ObjectMapper();

				/** create a list of object to serializes the data in json array format */
				List<DataObject> dataObjectList = new ArrayList<>();
				TypeReference<List<DataObject>> typeReference = new TypeReference<List<DataObject>>() {};

				/** first read the received json string as a Class Object */
				DataObject dataObjectFromTestCase = mapper.readValue(data, DataObject.class);

				/** then read the existing json string as a Class Object, if there is any serialized json file found,
				 * in case none is found - handle file not found exception */
				try {
					dataObjectList = mapper.readValue(new File(TestSuiteClass.AUTOMATION_HOME.concat("/dataObject")), typeReference);
				}catch (FileNotFoundException e) {}

				/** merge test case json object with the existing json array (if found) to be written as json array later on */
				dataObjectList.add(dataObjectFromTestCase);

				/** now serialized the merged / updated object list as json array */
				mapper.writeValue(new File(TestSuiteClass.AUTOMATION_HOME.concat("/dataObject")), dataObjectList);

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" data is serialized in the dataObject" + data);
				return "Pass: ";
			}
			else{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Data is empty hence nothing is added in json object" + data);
				return "Skip: " + " Empty data is received from test case to serialize. ";
			}

		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occured with writing data to object for "+data, e);
			return "Fail: " + e.getMessage();
		}

	}


	/** This method will return the callable task to be consumed by verify verifyelementpresent_parallel keyword.
	 * 
	 * @param connection
	 * @param jsonObjectRepo
	 * @param proxyServer
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public Callable<Object> getCallableTask_VerifyElementPresent(Connection connection, JSONObject jsonObjectRepo, ProxyServer proxyServer,
			WebDriver driver, String objectName, String data)
	{

		Callable<Object> callable = new Callable<Object>() {

			@Override
			public Object call() throws Exception {

				String result = new KeywordsExtended(connection, jsonObjectRepo, proxyServer).verifyelementpresent(driver, objectName, data);

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : callable task result for element: " +data + " -- "+ result);
				return result;
			}
		};

		return callable;
	}

	/** parse and evaluate the sub expression 
	 * 
	 * @param xyz
	 * @return
	 */
	public String convertTextIntoDate(String xyz)
	{
		String dateString=null;
		try
		{
			SimpleDateFormat sdf2=new SimpleDateFormat("MMM dd yyyy");
			if(xyz.equalsIgnoreCase("today")){
				Date date=new Date();
				dateString =sdf2.format(date);
			}else if(xyz.equalsIgnoreCase("tomorrow")){
				Calendar c = Calendar.getInstance();
				c.setTime(new Date()); // Now use today date.
				c.add(Calendar.DATE, 1);//add one day
				dateString =sdf2.format(c.getTime());
			}else{

				/**
				 * (?:[A-Z][a-z]{1,10}\s\d{1,2},\s\d{4}$)
				 * ?: --> starting regex
				 * [A-Z][a-z]{1,10} --> characters starting with upperCASE followed by lowercase having length of lowercases characters between 1-10
				 * \s --> consider space
				 * \d{1,2} --> consider digits, with length of digits between 1 and 2
				 * , --> consider comma
				 * \s --> consider space
				 * \d{4} --> consider digits with exact 4 length
				 * $ --> end syntax
				 */

				String re1="(?:[A-Z][a-z]{2},\\s[A-Z][a-z]{2}\\s\\d{1,2}$)";	// Day Of Week 1 //sample: Mar, Mon 10 
				String re2="(?:[A-Z][a-z]{2,3}\\s[0-9]{1,2},\\s[0-9]{2,4})"; //sample: Marc 10, 18 or Marc 10, 2018
				String re3 = "(?:[A-Z][a-z]{1,10}\\s\\d{1,2},\\s\\d{4}$)"; // sample: August 9, 2018

				Pattern p1 = Pattern.compile(re1);
				Matcher m1 = p1.matcher(xyz);

				Pattern p2 = Pattern.compile(re2);
				Matcher m2 = p2.matcher(xyz);

				Pattern p3 = Pattern.compile(re3);
				Matcher m3 = p3.matcher(xyz);

				if (m1.find()){
					String str=m1.group();
					str+=" "+Calendar.getInstance().get(Calendar.YEAR);;
					SimpleDateFormat sdf1 = new SimpleDateFormat("EEE, MMM dd yyyy");
					Date date = sdf1.parse(str);
					sdf2=new SimpleDateFormat("MMM dd yyyy");
					dateString=sdf2.format(date);
				}

				else if(m2.find()){
					String str=m2.group();
					SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd, yyyy");
					Date date = sdf1.parse(str);
					sdf2=new SimpleDateFormat("MMM dd yyyy");
					dateString=sdf2.format(date);
				}

				else if(m3.find()) {
					String str=m3.group();
					SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd, yyyy");
					Date date = sdf1.parse(str);
					sdf2=new SimpleDateFormat("MMM dd yyyy");
					dateString=sdf2.format(date);
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ " -- " +e.getMessage(), e);
		}
		return dateString;
	}


	/** get the headers from the received request json object 
	 * 
	 * @param request
	 * @return
	 */
	public String getHeaders(org.json.simple.JSONObject request) {

		String headerString = "";

		org.json.simple.JSONArray headers = (org.json.simple.JSONArray) request.get("headers");

		for(int i=0; i<headers.size(); i++)
		{
			org.json.simple.JSONObject headerObject = (org.json.simple.JSONObject) headers.get(i);
			String name = (String) headerObject.get("name");
			String value = (String) headerObject.get("value");

			headerString = headerString + "\n" + name + " : " + value;
		}

		return headerString;
	}


	/** This code - de-serializes the dataObject using jackson mapper and covert the whole json into Class Objects so that its very easy to do the manipulation.
	 * 
	 * @param data
	 * @return
	 */
	public synchronized String changeVsmExecutionStatus_UsingJacksonMapper(String data){

		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" data json is " + data);

		try{
			if(!data.isEmpty()){
				ObjectMapper mapper = new ObjectMapper();

				/** create a type reference to readt json file */
				TypeReference<List<DataObject>> typereference = new TypeReference<List<DataObject>>() {};
				List<DataObject> dataObjectList =mapper.readValue(new File(TestSuiteClass.AUTOMATION_HOME.concat("/dataObject")), typereference);

				/** iterate this list to manipulate the data */
				for(int i=0; i<dataObjectList.size(); i++) {					
					if(dataObjectList.get(i).getOrder_Id().equalsIgnoreCase(data)) {
						dataObjectList.get(i).setVsm_Execution("true");
					}
				}

				/** serialize the updated json file */
				mapper.writeValue(new File(TestSuiteClass.AUTOMATION_HOME.concat("/dataObject")), dataObjectList);

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" data is added in the dataObject" + data);
				return "Pass: ";
			}
			else{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Data is empty hence nothing is added in json object" + data);
				return "Skip: " + " Empty data is received from test case to update the serialized json file. ";
			}
		}

		catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occured with writing data to object for "+data, e);
			return "Fail: " + e.getMessage();
		}
	}

	/**
	 * This method is used to update a file and replacing an old text with new one.	
	 * @param filePath
	 * @param newString
	 * @param oldString
	 */
	public void modifyFile(String filePath, String newString, String oldString){

		String oldContent ="";
		BufferedReader reader = null;
		FileWriter writer = null;
		try{
			reader = new BufferedReader(new FileReader(new File(filePath)));
			String line = reader.readLine();

			while(line !=null){
				oldContent = oldContent + line+ System.lineSeparator();
				line = reader.readLine();
			}
			String newContent = oldContent.replace(oldString, newString);

			//Rewriting the input text file with newContent

			writer = new FileWriter(new File(filePath));

			writer.write(newContent);


		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ "error occured while modifying the template file" +e.getMessage(), e);
		}
		finally{
			try
			{
				//Closing the resources    
				reader.close();
				writer.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}


	/**
	 * store result in a map with key either supplied from description as #abx# or test_step_id 
	 * usage -- to supply result of one step to another step as data ..
	 * 
	 * @param testStepID_InputData
	 * @param testStepObject
	 * @return
	 */
	public HashMap<String, String> storeTestStepResult(HashMap<String, String> testStepID_InputData,TestStepObject testStepObject){
		testStepID_InputData.put(testStepObject.getTestStepId().toLowerCase().trim(), testStepObject.getTestStepResult());

		/** checking description ,if description contain data in format like #store_data# then we will put in the map like */
		Pattern pattern = Pattern.compile("#([\\sa-zA-Z0-9_-]+)#");
		Matcher match = pattern.matcher(testStepObject.getTestStepDescription());
		if(match.find()) {
			String matchString = match.group();
			String key = matchString.replace("#", "").toLowerCase().trim();
			testStepID_InputData.put(key, testStepObject.getTestStepResult());
		}
		return testStepID_InputData;
	}


}


