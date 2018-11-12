/**
 * Summary: This class is written for keyword definitions
 * Last Changes Done on Feb 2, 2015 3:56:34 PM
 * Purpose of change: Added keywords to be used while executing test cases.
 */


package core.classes;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mysql.jdbc.Connection;

import core.utilities.CaptureScreenShotLib;
import core.utilities.CustomException;
import core.utilities.CustomExceptionStopExecution;
import core.utilities.DBLib;
import core.utilities.GenericMethodsLib;
import core.utilities.IntegerLib;
import core.utilities.KeyBoardActionsUsingRobotLib;
import core.utilities.httpClientWrap;
import io.appium.java_client.AppiumDriver;
import net.lightbody.bmp.proxy.ProxyServer;
import tests.TestSuiteClass;


// TODO: Auto-generated Javadoc
/**
 * @author Pankaj Katiyar
 *
 */

@SuppressWarnings("deprecation")
public class Keywords {

	Logger logger = Logger.getLogger(Keywords.class.getName());

	String passed_status;
	String failed_status;
	String skip_status;
	String skip_following_steps;

	/**
	 * Defining Variables
	 */
	String warning_status;
	WebDriver driver;
	WebElement webelement;
	String noObjectSuppliedMessage;
	String noDataSuppliedMessage;

	GetObjects getObject = new GetObjects();
	HandlerLib handler = new HandlerLib();
	String locationToSaveSceenShot;

	JSONObject jsonObjectRepo;
	static Connection connection;

	ProxyServer proxyServer;

	/**
	 * Constructor initialization.
	 */
	public Keywords(Connection connection, JSONObject jsonObjectRepo, ProxyServer proxyServer)
	{
		this.passed_status = "Pass: ";
		this.failed_status = "Fail: ";
		this.skip_status = "Skip: ";
		this.warning_status = "Warning: ";
		this.skip_following_steps="skipFollowing";
		this.noObjectSuppliedMessage = failed_status + "Please supply the desired object from object repository.";
		this.noDataSuppliedMessage = failed_status + "Please supply the desired test data.";
		this.locationToSaveSceenShot = TestSuiteClass.AUTOMATION_HOME.concat("/screenshots/").concat("ErrorKeywords").concat("/");

		this.jsonObjectRepo = jsonObjectRepo;
		this.proxyServer = proxyServer;
	}


	/**
	 * This keyword launches browser.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String launchbrowser(WebDriver driver, String objectName, String data)
	{
		String result;

		try{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received browser name is: "+data);

			if(data.isEmpty())
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Empty browser is received. ");
				result = failed_status + "Browser type: "+data +" can't be empty, please supply the supported browser: chrome or firefox.";
			}
			else if(data.equalsIgnoreCase("chrome") || data.equalsIgnoreCase("firefox"))
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Setting up browser: "+data);

				driver = WebdriverSetup.WebDriverSetUp(data, null,false);
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Launched browser name is: "+data + " browsername "+ driver );			

				/** Bring browser in focus, normally chrome opens in background. */
				getbrowserinfocus(driver, objectName, data);

				result = passed_status+ "Browser launched successfully";
			}
			else
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Supplied browser: "+data + " is not supported. ");
				result = failed_status + "Supplied browser type: "+data +" is not supported, supported ones are chrome and firefox.";
			}
		}catch(Exception e)
		{
			result = failed_status + "Couldn't launch browser";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while launching browser: "+data, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This method is a customized work around to bring browser on top of all windows and keep in focus.
	 * 
	 * @param driver
	 */
	public String getbrowserinfocus(WebDriver driver, String objectName, String data)
	{
		try
		{
			/** Store window state */
			String currentWindowHandle = driver.getWindowHandle();

			/** run javascript and alert code */
			((JavascriptExecutor)driver).executeScript("alert('Test')"); 

			/** Accept alert */
			try{driver.switchTo().alert().accept();}
			catch(NoAlertPresentException n){}

			/** Switch back to to the window using the handle saved earlier -- this may not be needed.. */
			driver.switchTo().window(currentWindowHandle);
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception: ", e);
		}

		/** no need to fail case based on this failures */
		return passed_status;
	}


	/** This keyword closes the browsers opened by automation code.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String closebrowser  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Closing browser. ");

			/** kill emulator for appium driver */
			if(driver instanceof AppiumDriver<?>) {
				String deviceUDID = SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().get(TestSuiteClass.UNIQ_EXECUTION_ID.get()).getDeviceUDID();
				new GenericMethodsLib().killEmulator(deviceUDID);
			}else {
				/** for normal browser */
				driver.quit();
			}

			result = passed_status+ "Browser closed successfully.";
		}catch(Exception e)
		{
			result = warning_status + "Couldn't close browser. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while closing browser. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/**
	 * This keyword navigates to a URL.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String navigateurl(WebDriver driver, String objectName, String data)
	{
		String result;
		try{

			//data = data + "?timestamp="+new Date().getTime();

			/** putting a random delay to avoid simultaneous load on server */
			int delay = IntegerLib.GetRandomNumber(2000, 1000);
			Thread.sleep(delay);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : random delay of: "+delay);

			if(driver != null)
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received url is: "+data+ " and browser is: " +driver);
				try{driver.get(data);}catch(Exception e)
				{
					Thread.sleep(IntegerLib.GetRandomNumber(1000, 500));
					driver.get(data);
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred, reloading again : "+data);
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Navigated url is: "+data);
				result = passed_status+ "Navigated url successfully.";				
			}
			else
			{
				result = failed_status + "Didn't receive the driver. ";
			}
		}catch(Exception e)
		{
			result = failed_status + "Couldn't navigate to url";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while navigating url : "+data+ " at browser : " +driver, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}

	/**
	 * This keyword navigates back on step
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String navigateback(WebDriver driver, String objectName, String data)
	{
		String result;
		try{

			/** putting a random delay to avoid simultaneous load on server */
			int delay = IntegerLib.GetRandomNumber(5000, 1000);
			Thread.sleep(delay);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : random delay of: "+delay);

			if(driver != null)
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received driver url is: "+driver.getCurrentUrl());
				try{driver.navigate().back();;}catch(Exception e)
				{
					Thread.sleep(IntegerLib.GetRandomNumber(1500, 500));
					driver.navigate().back();
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred, reloading again : "+data);
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Navigated back url is: "+driver.getCurrentUrl());
				result = passed_status+ "Navigated back successfully.";
			}
			else
			{
				result = failed_status + "Couldn't navigate to url. ";
			}
		}catch(Exception e)
		{
			result = failed_status + "Couldn't navigate back";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while navigating back url : "+data+ "at browser : " +driver, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword will move the driver to new browser window.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String movetonewbrowserwindow  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Moving to new browser window" );

			String currentState="";
			try{currentState = driver.getWindowHandle().toString();}catch (Exception e) 
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception handled : "+e.getMessage() );
			}

			/** This code will explicitly wait for max 5 sec to appear multiple window 
			 * for driver to switch on */
			int i=0;
			while(driver.getWindowHandles().size()<2){
				Thread.sleep(3000);
				i++;

				if(i==5){
					break;
				}
			}

			for(String handles : driver.getWindowHandles())
			{
				if(!handles.equalsIgnoreCase(currentState))
				{
					driver.switchTo().window(handles);
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Switched window has title: "+driver.getTitle());
				}
				/** special case handling - where web page is closed and driver has to be moved to new window then 
				 * -- reference Facebook Login on Lenskart */
				else if(currentState==""){
					driver.switchTo().window(handles);
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Switched window has title: "+driver.getTitle());
				}
			}
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Moved to new window: "+driver.getCurrentUrl());
			result = passed_status+ "Moved to new window successfully";
		}catch(Exception e)
		{
			result = failed_status + "Couldn't moved to new window";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while moving to new window ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/**
	 * This keyword is used to upload the creative.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String uploadimage(WebDriver driver, String objectName, String data)
	{
		String result;
		try{

			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received file name with location is: " +data);

			/**
			 * Getting relative image file
			 */
			data = handler.getUploadImageLocation(data);
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Uploading image file: "+data);

			Thread.sleep(1500);

			webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);
			webelement.click();

			//			this is jugaad to click on upload presecription button - need to revise
			//			String value = jsonObjectRepo.getJSONObject(objectName).get("identifierValue").toString();
			//			String  javascript = "var element = document.evaluate(\""+ value +"\" ,document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; element.click();";
			//			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " +" executing js: "+javascript);
			//			((JavascriptExecutor)driver).executeScript(javascript);

			Thread.sleep(2500);

			KeyBoardActionsUsingRobotLib.ChooseFileToUpload(data, driver);

			/**
			 * Wait until image is uploaded successfully. Currently hardcoding this 
			 * later on need to get from OR
			 */
			By byLocator = By.xpath("//div[@class='ui-progressbar ui-widget ui-widget-content ui-corner-all'][@aria-valuenow='100']");
			handler.applyExplicitWait(driver, byLocator, new NoSuchElementException(""));

			result = passed_status+ "Creative uploaded successfully";
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't upload creative";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while uploading file : "+data, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword is used to click link.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */
	public  String clickelement(WebDriver driver, String objectName, String data)
	{
		String result;

		try{
			boolean staleExceptionHandleFlag = true;
			int staleExceptionAttempt=0;

			/** Adding a check on staleExceptionHandleFlag exception, in case this occurs then find the element again until the max attempt = 5.
			 */
			while(staleExceptionHandleFlag)
			{
				try
				{	/** find element, if returned as null then retry */				
					webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

					/** Wait until link is visible and clickable, if its not enabled.*/
					By bylocator = getObject.getFinalByLocator(driver, objectName, data, jsonObjectRepo);					

					if(webelement != null && !webelement.isEnabled()){
						handler.applyExplicitWait(driver, bylocator, new WebDriverException());
					}

					//This is jugaad to be reviewed.
					if(objectName.equalsIgnoreCase("productPage_TAT_check_button")){
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " MANDEEP/DEEP/ABHIRAJ/ASHIT ==> This is a jugad, need to remove this asap ... ");

						scrollpagedown(driver, objectName, data);
						webelement.click();
					}
					else
					{
						/** if there is any exception thrown while clicking link, then reattempt after catching that exception */
						try{
							webelement.click();
						}catch(Exception w){

							/** in case element is not clickable because of not visible -- or some other exception message */
							if((w instanceof org.openqa.selenium.ElementClickInterceptedException) ||
									(w instanceof WebDriverException && 
											w.getMessage().contains("Other element would receive the click")) ){

								logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " not clickable exception is being handled by scolling element into view.");
								String javaScript = "arguments[0].scrollIntoView(true);";
								handler.executeJavaScript(driver, javaScript, webelement);
								webelement.click();
							}else {
								handler.applyExplicitWait(driver, bylocator, new WebDriverException());
								webelement.click();
							}
						}
					}

					staleExceptionHandleFlag = false;

				}catch(StaleElementReferenceException e){
					staleExceptionAttempt ++;
				}

				if(staleExceptionAttempt ==5){
					break;
				}
			}

			Thread.sleep(2500);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Clicking element: " + webelement );
			result = passed_status+ "Clicked link successfully";

		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't click link";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while clicking link: " +webelement, e);

			if(!(e instanceof NullPointerException)) {

				/** Taking screenshot during exception */
				CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
			}
			else
			{
				CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :  Taking Screenshot For Nullpointer Exception. ");
			}
		}
		return result;
	}


	/** This keyword is used to click link.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String evaluate_expression (WebDriver driver, String objectName, String data)
	{
		String result = "";
		String actionCondition=null;
		String keyword="";
		String value="";
		boolean status=false;

		try{

			/** make case insensitive */
			String temp = data.toLowerCase().trim();

			/** parse the received data sample - when ifelementpresent then getText with data */
			actionCondition = temp.substring(temp.indexOf("when")+4, data.indexOf("then")).trim();
			keyword=temp.substring(temp.indexOf("then")+4, temp.indexOf("with")).trim();

			/** data need not be changed here, use the way as it was passed to methods */
			int dataStartingLength = temp.indexOf("with")+4;
			value=data.substring(dataStartingLength, data.length()).trim();

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Condition = "+actionCondition + " keywordAction = "+keyword + " and data: "+value  );

			KeywordsExtended keywordsObj=new KeywordsExtended(SingletonTestObject.getSingletonTestObject().getMysqlDbConnection(), jsonObjectRepo, proxyServer);

			if(actionCondition != null){

				try {
					/** get the (keyword - condition) dynamically */
					Method conditionMethod=keywordsObj.getClass().getMethod(actionCondition, WebDriver.class, String.class, String.class);

					/** check condition */
					Object obj=conditionMethod.invoke(keywordsObj, driver, objectName, value);

					/** check if condition passed */
					if(!obj.toString().toLowerCase().startsWith(failed_status.toLowerCase()) 
							&& 
							!obj.toString().toLowerCase().startsWith(skip_status.toLowerCase())){

						status=true;
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : condition = "+actionCondition+" passed. ");
					}
				}catch (NoSuchMethodException e) {
					/** in case supplied condition is not a keyword rather its a custom expression like "SUBSTRING contains STRING" then parse sub-condition */						
					status = handler.evaluateSubExpression(handler.parseSubExpression(actionCondition));
				}

				/** perform action if condition passed */
				if(status){
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Performing action "+keyword+" on the data");
					Method method = keywordsObj.getClass().getMethod(keyword, WebDriver.class, String.class, String.class);

					Object resultObj = method.invoke(keywordsObj, driver, objectName, value);
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : action "+keyword+" invoked successfully");

					result = (String) resultObj;

				}else{
					result = skip_status + " Supplied Condition Failed. ";
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : action "+keyword+" is not invoked");
				}
				/** in case any such condition is retrieved like when #TC_10_01#	       contains 				newsite then dothis with
				 * then this -- #TC_10_01#	       contains 				newsite will be handled differently
				 */ 

			}else{
				result = skip_status + " No condition was supplied with when keyword. ";
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : action "+keyword+" is not invoked as not condition is specified");
			}

		}
		catch(Exception e)
		{
			result = skip_status + "Exception occurred while evaluating expression.";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Element is not available" +e.getMessage());

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}

	/**
	 * This keyword is to select radio button.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String selectradiobutton  (WebDriver driver, String objectName, String data)
	{
		String result;

		try{
			webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

			/** apply explicit wait */
			WebDriverWait wait = new WebDriverWait(driver, 45);
			wait.until(ExpectedConditions.elementToBeClickable(webelement));

			if(!webelement.isSelected())
			{
				webelement.click();
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Selected radio button option is: " +webelement);
				result = passed_status+ "Selected radio button successfully";
			}
			else
			{
				result = passed_status+ "Desired radio button was already selected. ";
			}
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't select radio button";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while selecting radio button option : ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}


	/** This keyword clicks any check box if its not already checked. this keyword supports multiple check box selection.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String selectcheckbox  (WebDriver driver, String objectName, String data)
	{
		String result = "";

		/** if object is supplied by comma separated then select every check box */
		if(objectName.contains(","))
		{
			List<String> objectList = new ArrayList<>(Arrays.asList(objectName.split(",")));
			for(int i=0; i<objectList.size(); i++)
			{
				result = result + "\n" + handler.selectCheckbox(driver, objectList.get(i).trim(), webelement, data, 
						locationToSaveSceenShot, passed_status, failed_status, jsonObjectRepo);
			}
		}
		else if(data.contains(","))
		{
			List<String> dataList = new ArrayList<>(Arrays.asList(data.split(",")));
			for(int i=0; i<dataList.size(); i++)
			{
				result = result + "\n" + handler.selectCheckbox(driver, objectName, webelement, dataList.get(i).trim(), 
						locationToSaveSceenShot, passed_status, failed_status, jsonObjectRepo);
			}
		}
		else
		{
			result = handler.selectCheckbox(driver, objectName, webelement, data, locationToSaveSceenShot, passed_status, failed_status, jsonObjectRepo);
		}

		return result;
	}


	/** This keyword unselect any selected check box.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String unselectcheckbox  (WebDriver driver, String objectName, String data)
	{
		String result;

		try{
			webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

			if(webelement.isSelected()){
				webelement.click();
			}

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Checkbox is cleared. ");
			result = passed_status+ "Checkbox cleared successfully";
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't clear checkbox. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while clearing checkbox : " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword verifies if supplied check box is selected.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String ischeckboxselected  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{
			webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);
			if(webelement.isSelected())
			{
				return passed_status + "Checkbox is selected. "; 
			}
			else
			{
				return failed_status + "Checkbox is not selected. ";
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't check the supplied checkbox selection. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while checking selection of checkbox : " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

		}
		return result;
	}

	/** This keyword will be used to select the desired value(s) out of pre populated list by searched records based on 
	 *  user input, like Select Placement in Assign Placement to Client screen / select channel in Generate Tag screen etc.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String chooseinlist  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{
			if(!objectName.isEmpty() && !data.isEmpty())
			{
				/** In case of stale element exception, find element again to do operation.
				 *  repeatAction parameter will keep the code in loop and attemptCount parameter will limit the 
				 *  number of attempt to 5 to avoid infinite loop.
				 */
				boolean repeatAction = true;
				int attemptCount = 0;

				while(repeatAction)
				{
					try{
						Thread.sleep(1000);
						webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);
						webelement.click();
						Thread.sleep(1000);

						repeatAction = false;
					}catch(StaleElementReferenceException e){
						repeatAction = true;
						attemptCount++;
					}

					if(attemptCount ==5){
						break;
					}
				}

				result = passed_status+ "Selected value: "+data+" successfully";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Selected value: "+data+" successfully");
			}
			else
			{
				result = noDataSuppliedMessage + "   " + noObjectSuppliedMessage;
			}
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't select "+data +" from list. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while selecting value: "+data +" from list. ");

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/**
	 * This keyword is for type the value in text field/area.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String typevalue  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{

			if(!objectName.isEmpty())
			{
				webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

				/** in case of Fire Fox Driver - if there is exception while sending keys then find the element again and type. */
				try{webelement.sendKeys(data);}
				catch (org.openqa.selenium.ElementNotInteractableException e) {

					/** just to bring browser back in focus */
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("alert('Test')");
					driver.switchTo().alert().accept();

					webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);
					webelement.sendKeys(data);
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Typing the value : " + data + " in the element: " + webelement );
				result = passed_status+ "Value typed successfully";
			}
			else
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No object was provided to type value. " );
				result = failed_status+ "No object was provided to type value. ";
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't type value";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while Typing the value : " + data + " in the element: " + webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}


	/** this keyword will type slowly - one char at a time.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public  String typeslowly  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{

			if(!objectName.isEmpty())
			{
				webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

				/** get bytes from received data */
				char [] chars = data.toCharArray();

				for(int i=0; i<chars.length; i++) {

					Thread.sleep(1500);
					webelement.sendKeys(String.valueOf(chars[i]));
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Typing the value : " + chars[i] + " in the element: " + webelement );
				}

				result = passed_status+ "Value typed successfully";
			}
			else
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No object was provided to type value. " );
				result = failed_status+ "No object was provided to type value. ";
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't type value";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while Typing the value : " + data + " in the element: " + webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}	


	/** This keyword is for verify the text of any supplied object.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 * @throws CustomExceptionStopExecution 
	 */	
	public String verifytext  (WebDriver driver, String objectName, String data) throws CustomExceptionStopExecution
	{
		String result = "";

		try{

			if(objectName.isEmpty())
			{
				result = failed_status + "Supplied object name is empty. ";
			}

			else
			{
				//Getting data after removing any flag like "must flag"
				if(data.contains(";")){
					data = data.split(";")[0].trim();
				}
				else if(data.contains(",")){
					data = data.split(",")[0].trim();
				}

				String actualValue = "";

				/** Handling stale element reference exception, in this exception retry to find the element, max attempt is 5
				 */
				boolean staleElementReferenceException = true;
				int staleElementReferenceExceptionCount = 0;
				By bylocator = null;

				while(staleElementReferenceException)
				{
					try
					{
						/** First the check if the supplied element is a dynamic object which needs data to create element definition, 
						 * if no, then createDynamicWebElement method will throw CustomExceptionsLib exception, and then find the element using 
						 * 
						 */
						bylocator = getObject.getFinalByLocator(driver, objectName, data, jsonObjectRepo);

						/** apply explicit wait of 45 sec before finding the element */
						handler.applyExplicitWait(driver, bylocator, new NoSuchElementException(""), 60);
						webelement = driver.findElement(bylocator);

						for (int i=0; i<5; i++)
						{
							actualValue = driver.findElement(bylocator).getText().trim(); 
							if(actualValue.contains("#")){
								actualValue=actualValue.split("#")[1];
							}
							logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : actual text of element: " + actualValue);

							if(actualValue.equalsIgnoreCase(data))
							{
								break;
							}
							else
							{
								Thread.sleep(1000);
							}
						}

						staleElementReferenceException = false;
					}
					catch(StaleElementReferenceException s)
					{
						staleElementReferenceException = true;
						staleElementReferenceExceptionCount++;
					}

					if(staleElementReferenceExceptionCount == 5)
					{
						break;
					}
				}

				if(actualValue.equalsIgnoreCase(data))
				{
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Matched. ");
					result = passed_status + "Text is as expected.";
				}
				else 
				{
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Doesn't Match. ");
					result = failed_status + "The actual value is: " + actualValue + ", the expected value is: " + data;
				}
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch (TimeoutException e) 
		{
			result = failed_status + "Could not retrieve the text."; 
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Timed out while waiting for text to be present: "+data);
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ " : " + failed_status + "Exception occurred while verifying the text: " + data + " of the element: " +webelement, e);
			result = failed_status + "Could not retrieve the text."; 

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Current url: "+driver.getCurrentUrl());
		return result;
	}



	/**
	 * This keyword will verify the details from mysql database, user has to
	 * supply the db query in objectName and expected comma separated result(s)
	 * in data column, usage example: [objectName = select ABC from campaign
	 * where id = "XYZ"][input = xyz].
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */	
	public String verifydbdetails  (WebDriver driver, String objectName, String data) 
	{
		String result = "";
		try{
			/** 
			 * Do not proceed if there is no query supplied.
			 */
			if(objectName.isEmpty())
			{
				result = failed_status + "No query was supplied. ";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ ": " + result);
			}
			else
			{
				/** parse received data */
				data = handler.dataParser(data, connection);

				/** Parsing the supplied sql query. */
				String sqlQuery = objectName.replace("\"", "'");
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Executing supplied query: "+sqlQuery);

				String [] records = GenericMethodsLib.ExecuteMySQLQueryReturns1DArray(connection, sqlQuery);

				/** proceed to test only if received records is not null */
				if(records != null)
				{
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received Number Of Records: "+records.length);

					boolean dataListFlag = false;
					List<String> dataList = new ArrayList<>();

					/**
					 * This is for a special case If there is only one supplied data but containing ',' into it.
					 */
					if(data.contains("Vpaid") || data.contains("Mraid"))
					{
						/**
						 *  Replace the provided data as per the saved data in database
						 */
						data.replace("Vpaid", "2");
						data.replace("Mraid1", "3");
						data.replace("Mraid2", "5");

						if(records[0].trim().equalsIgnoreCase(data.trim()))
						{
							result = passed_status + "Actual value is same as expected. ";
						}
						else
						{
							result = failed_status + "Expected value= "+data + " whereas actual value saved in db = "+records[0];
						}
					}

					/**
					 * Converting the comma / semi colon separated supplied data into a list 
					 * only when --> data contains must pass flag separated by , or ;
					 */

					else if(data.contains(";"))
					{
						if(data.toLowerCase().contains("must pass"))
						{
							dataListFlag = true;	

						}
						else
						{
							dataListFlag = false;
						}

						/** Recasting the splitted string as list to avoid unsupported operation exception. */
						dataList = new ArrayList<>(Arrays.asList(data.split(";")));
					}
					else if(data.contains(","))
					{
						if(data.toLowerCase().contains("must pass"))
						{
							dataListFlag = true;
						}
						else
						{
							dataListFlag = false;
						}
						/** Recasting the splitted string as list to avoid unsupported operation exception. */
						dataList = new ArrayList<>(Arrays.asList(data.split(",")));
					}
					else
					{
						/** If there is only one supplied data::: 
						 */
						if(records[0].trim().equalsIgnoreCase(data.trim()))
						{
							result = passed_status + "Actual value is same as expected. ";
						}
						else
						{
							result = failed_status + "Expected value= "+data + " whereas actual value saved in db = "+records[0];
						}
					}

					/** If the supplied data is a list then iterating it: */
					if(dataListFlag)
					{
						/** Remove any must pass flag from the supplied user data list, checking the only last item
						 * coz must pass can be used only at the last place. 
						 */
						if(dataList.get(dataList.size()-1).trim().equalsIgnoreCase("must pass"))
						{
							dataList.remove(dataList.size()-1);
						}
					}	
					if(!dataList.isEmpty())
					{
						/** This failFlag will be used to verify if there is any case of data mismatch */
						boolean failFlag = true;

						/** Iterating the supplied data list. */
						for(int i=0; i<dataList.size(); i++)
						{
							String suppliedExpectedValue = dataList.get(i).trim();
							String actualDBValue = records[i];

							/** Compare each supplied data with the retrieved value from database */
							if(!suppliedExpectedValue.equalsIgnoreCase(actualDBValue))
							{
								result = result + "Expected value= "+dataList.get(i) +" whereas actual value saved in db= "+records[i] + "  ";

								/** If there is even a single mismatch failFlag = false, later on to be determined if there was any mismatch. */
								if(failFlag)
								{
									failFlag = false;
								}
							}

							/** Check if the whole list is iterated yet, if yes then check the failFlag, if failFlag is true 
							 * then there was no mismatch else there was a mismatch in data.  
							 */
							if(i==dataList.size()-1)
							{
								if(failFlag)
								{
									result = passed_status + "All values are saved as expected in database.";
								}
								else
								{
									result = failed_status + result;
								}
							}
						}

					}
				}
				else
				{
					result = failed_status + "Received null in database. ";
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No Records Received ... ");
				}
			}
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ " : " + failed_status + "Exception occurred while verifying the database details." , e);
			result = failed_status + "Could not get database details. "; 

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}

	/**
	 * This keyword will execute the supplied insert / update query in mysql database, user has to
	 * supply the db query in objectName , usage example: [objectName = update abc where a = c].
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */	
	public String executedbquery  (WebDriver driver, String objectName, String data) 
	{
		String result = "";
		try{
			/** 
			 * Do not proceed if there is no query supplied.
			 */
			if(objectName.isEmpty())
			{
				result = failed_status + "No query was supplied. ";
			}
			else
			{
				/** Parsing the supplied sql query. */
				String sqlQuery = objectName.replace("\"", "'");
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Executing supplied query: "+sqlQuery);

				boolean flag = new DBLib().executeUpdateInsertQuery(connection, sqlQuery);
				if(flag)
				{
					result = passed_status + "Query was executed.";
				}
				else {
					result = failed_status + "Query was not executed.";
				}
			}
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ " : " + failed_status + "Exception occurred while verifying the database details." , e);
			result = failed_status + "Could not get database details. "; 

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/**
	 * This keyword is little special, it will be used to verify the text of
	 * desired parameter corresponding to the given searched value. For example,
	 * in Marketplace Connections screen, user searches a connection by giving
	 * Connection Identifier = "SearchOnly_DontDelete" and verifies Connection
	 * Name = "searchonly_dontdelete_both_video_rtb22_all" corresponding to
	 * given Connection Identifier, then this keyword verifytextofsearchedrecord
	 * will be used with the below values in input data along with the object Name of Object to be verified
	 * (in this case objectName of Connection Name)
	 * INPUT DATA: SearchOnly_DontDelete, searchonly_dontdelete_both_video_rtb22_all
	 * 
	 * First Parameter is the Value Used To Perform Search, Second is the
	 * expected value of desired parameter which will be matched with the actual
	 * value. This is required to maintain the relation.
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String verifytextofsearchedrecord  (WebDriver driver, String objectName, String data)
	{
		String result = "";
		try
		{
			if(objectName.isEmpty() || data.isEmpty())
			{
				result = failed_status + "Both object name and desired test in data should be supplied to use this keyword. ";
			}
			else
			{
				String searchParam = "";
				String expectedValue = "";

				/** Getting searched parameter and expected value from the supplied data. 
				 * First parameter will always be the data to be used in finding element and second one will be 
				 * the expected data.
				 */

				if(data.contains(";"))
				{
					searchParam = data.split(";")[0].trim();
					expectedValue = data.split(";")[1].trim();
				}
				else if(data.contains(","))
				{
					searchParam = data.split(",")[0].trim();
					expectedValue = data.split(",")[1].trim();
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Search parameter is: " + searchParam);

				/** Create the dynamic element using searchParam, putting sleep to handle sync
				 */
				Thread.sleep(2000);
				webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);
				if(webelement != null)
				{
					/** Wait until the expected text is present in the web element.  
					 */
					try{
						WebDriverWait wait = new WebDriverWait(driver, 2);
						wait.until(ExpectedConditions.textToBePresentInElement(webelement, expectedValue));		
					}catch(Exception e){
					}
					String actualValue = webelement.getText().trim();
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : actual text of element : " + actualValue);

					/** Matching expected and actual values */
					if(actualValue.equals(expectedValue))
					{
						logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Matched. ");
						result = passed_status + "Text is as expected.";
					}
					else 
					{
						logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Doesn't Match. ");
						result = failed_status + "The actual value is: " + actualValue + ", the expected value is: " + expectedValue;
					}
				}
				else
				{
					result = "FAIL: Couldn't find the supplied webelement therefore text couldn't be verified.";
				}
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the text: " + data + " of the element: " +webelement, e);
			result = failed_status + "Could not retrieve the text."; 

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/**
	 * This keyword is for verify the title of the browser.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String verifybrowsertitle  (WebDriver driver, String objectName, String data)
	{
		try{
			//Getting data after removing any flag like "must pass flag"
			if(data.contains(";")){
				data = data.split(";")[0].trim();
			}
			else if(data.contains(",")){
				data = data.split(",")[0].trim();
			}

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Verifying the title of browser : " + driver );
			String actualValue = driver.getTitle().trim();
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Title of browser is: "+actualValue);

			if(actualValue.equalsIgnoreCase(data))
			{
				return passed_status + " Title is as expected.";
			}
			else 
			{
				return failed_status + " the actual title is : " + actualValue + " but the expected title is : " + data;
			}
		}catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the title: " + data + " in the element: " +driver, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + ": could not retrive the browser title."; 
		}
	}


	/**
	 * This keyword is used to get the text of any element.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String gettext(WebDriver driver, String objectName, String data)
	{		
		try{
			webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

			String actualValue = webelement.getText().trim();
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual Text of element is: " +actualValue);
			
			return actualValue;
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred  "+e+" while getting the text from the element: " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + "could not retrieve the text.";
		}
	}


	/** This keyword will get the text of alert, if any.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String getalerttext  (WebDriver driver, String objectName, String data)
	{
		try{
			String actualText = driver.switchTo().alert().getText().toString();

			//Accepting alert
			acceptalert(driver,objectName, data);
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : actual text present in alert: " + actualText );
			return actualText;
		}
		catch(NoAlertPresentException e)
		{
			logger.warn("No Alert found.");
			return "No alert found.";
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the alert text. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + "could not retrieve the alert text."; 
		}
	}


	/** this keyword will return true if the supplied text present in page source - this will be usefull in case appium driver. 
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String ifpagesourcecontainstext (WebDriver driver, String objectName, String data)
	{
		try{
			if(driver.getPageSource().contains(data.trim())){
				return String.valueOf(true);
			}else {
				return String.valueOf(false);
			}
		}
		catch(Exception e)
		{
			logger.error(failed_status + " Exception occurred while verifying the text in page source. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return String.valueOf(false); 
		}
	}



	/**
	 * This keyword is for get the title of browser.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String getbrowsertitle  (WebDriver driver, String objectName, String data)
	{
		try{			
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Title of browser : " + driver );
			String actualValue = driver.getTitle().trim();
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Title of browser is: "+actualValue);
			return actualValue;
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred  "+e+" while getting the browser title from the element: " +driver, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + " : could not retrive the page title"; 
		}
	}

	/**
	 * This method is being used to switch between iframes.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String movetoiframe(WebDriver driver, String objectName, String data)
	{
		try{
			driver.switchTo().defaultContent();
			WebDriverWait wait = new WebDriverWait(driver, 60);
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(data));

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Switched to iframe: "+data);			
			return passed_status;
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while switching frame: ", e);
			return failed_status;
		}

	}


	/** This keyword will verify the existence of supplied web element(s), multiple elements 
	 * can be supplied separated by comma(,) or semicolon(;). 
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String verifyelementpresent  (WebDriver driver, String objectName, String data)
	{
		String result = "";

		try
		{
			/** checking if supplied data is q query, if yes then execute it and get the data, create webelement using it
			 * and call recursively verifyelementpresent */
			if(data.toLowerCase().trim().startsWith("select") && data.toLowerCase().trim().contains("from"))
			{
				String [] arrayData = GenericMethodsLib.ExecuteMySQLQueryReturns1DArray(connection, data);

				/** iterating the received data */
				for(int i=0; i<arrayData.length; i++)
				{
					/** recursive call to keywords.verifyelementpresent method */
					String dataToBeUsed = arrayData[i].trim();
					result = result + "\n" + verifyelementpresent(driver, objectName, dataToBeUsed);
				}
			}
			else
			{
				result = handler.verifyElementPresent(objectName, data, driver, webelement, getObject, 
						handler, passed_status, failed_status, locationToSaveSceenShot, jsonObjectRepo);
			}
		}catch(Exception e)
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


	/** This method will be used when multiple elements (on the same page) need to be checked in parallel sharing same driver instance - to reduce time. 
	 * Going to use executor with callables.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String verifyelementpresent_parallel (WebDriver driver, String objectName, String data)
	{	
		String result = "";

		try {

			/** create executor */
			ExecutorService ex = Executors.newCachedThreadPool();

			/** crate a set to store all the callable tasks */
			HashSet<Callable<Object>> set = new HashSet<>();

			/** iterate list and add callables to be submitted to executors -- for parallelism */
			if(objectName.contains(",")){
				List<String> objects = Arrays.asList(objectName.split(","));

				for(String elementName : objects){
					set.add(handler.getCallableTask_VerifyElementPresent(connection, jsonObjectRepo, proxyServer, driver, elementName, data));
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " callable task added for element:  " +elementName);
				}
			}

			/** submit task for execution in parallel */
			List<Future<Object>> futureResult = ex.invokeAll(set);

			for (Future<Object> actualResult : futureResult)
			{
				if(actualResult.isDone()) {
					result = result + " - " + actualResult.get().toString();
				}
			}

		}
		catch (Exception e) {
			result = failed_status + " Couldn't check the presence of element. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while checking the presence of element. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}	


	/** This keyword will verify the existence of supplied web element, if element not present then result is pass.
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String verifyelementnotpresent  (WebDriver driver, String objectName, String data)
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
			else if(objectName.contains(";"))
			{
				listFlag = true;
				suppliedObjectList = new ArrayList<String>(Arrays.asList(objectName.split(";")));
			}
			else
			{
				listFlag = false;

				boolean iselementDisplayed = false;

				/** if data is supplied with or without comma / semi colon then convert the data into list and get the first string 
				 * as data input to create the dynamic element, multiple data can be supplied like: xyz ; must pass 
				 * then last value needs to be separated out, other than this data can't be supplied.
				 */
				webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

				if(webelement == null){
					iselementDisplayed = false;
				}else
				{
					//Checking if element is displayed
					iselementDisplayed = webelement.isDisplayed();
				}

				if(iselementDisplayed)
				{
					result = failed_status + "Element is present. ";
				}
				else
				{
					result = passed_status + "Element is not present. ";
				}
			}

			/** Iterating list and collecting not present objects into notPresentObjectList list, in case of InvocationTargetException exception
			 * also, adding object into  notPresentObjectList list
			 */
			if(listFlag)
			{
				for(int i=0; i<suppliedObjectList.size(); i++)
				{
					/** Catching InvocationTargetException exception in case webelement is not found on web page.
					 */
					try{
						webelement = getObject.getFinalWebElement(driver, suppliedObjectList.get(i), data, jsonObjectRepo);

						if(!webelement.isDisplayed())
						{
							notPresentObjectList.add(suppliedObjectList.get(i));
						}
					}catch(NullPointerException e)
					{
						notPresentObjectList.add(suppliedObjectList.get(i));
						logger.info(suppliedObjectList.get(i) + " wasn't found on web page. ");
					}
				}

				/** checking if notPresentObjectList's size, if this is equal to supplied one then pass
				 * else fail it.
				 */

				if(notPresentObjectList.size() == suppliedObjectList.size())
				{
					result = passed_status + "All supplied elements were not present. ";
				}
				else
				{
					/**
					 * Removing not present objects from supplied list and display only present elements in results.
					 */
					suppliedObjectList.removeAll(notPresentObjectList);
					result = failed_status + "Element(s): "+suppliedObjectList +" was(were) present. ";
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


	/** This keyword will move the driver focus to alert, if any and accept it.
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String acceptalert  (WebDriver driver, String objectName, String data)
	{
		String result;

		try{

			if(handler.waitForAlert(driver))
			{
				driver.switchTo().alert().accept();
				result = passed_status+ "Alert accepted.";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Accepted alert. ");
			}
			else
			{
				return warning_status + "No alert found.";
			}
		}
		catch(NoAlertPresentException e)
		{
			logger.warn("No Alert found.");
			return warning_status + "No alert found.";
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't move to alert.";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while accepting alert. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword will move the driver focus to alert (if any) and dismiss it.
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String dismissalert  (WebDriver driver, String objectName, String data)
	{
		String result;

		try{

			if(handler.waitForAlert(driver))
			{
				driver.switchTo().alert().dismiss();
				result = passed_status+ "Alert dismissed.";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Alert dismissed. ");
			}
			else
			{
				return warning_status + "No alert found.";
			}
		}
		catch(NoAlertPresentException e)
		{
			logger.warn("No Alert found.");
			return warning_status + "No alert found.";
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't move to alert.";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while dismissing alert. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword is to verify the text of any alert.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String verifyalerttext(WebDriver driver, String objectName, String data)
	{
		try{

			//Getting data after removing any flag like "must flag"
			if(data.contains(";")){
				data = data.split(";")[0].trim();
			}
			else if(data.contains(",")){
				data = data.split(",")[0].trim();
			}

			String actualText = driver.switchTo().alert().getText().toString();

			//Accepting alert
			acceptalert(driver,objectName, data);

			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : actual text present in alert: " + actualText );

			if(actualText.equals(data))
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Matched. ");
				return passed_status;
			}
			else 
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Doesn't Match. ");
				return failed_status + "The actual value is: " + actualText + " but the expected value is: " + data;
			}
		}
		catch(NoAlertPresentException e)
		{
			logger.warn("No Alert found.");
			return failed_status + "No alert found.";
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the alert text. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + "could not retrieve the alert text."; 


		}
	}


	/** This keyword is to verify if any alert is present.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String verifyalertpresent(WebDriver driver, String objectName, String data)
	{
		try
		{
			/** Get alert text, if no alert then move to NoAlertPresentException exception. */

			String alertText = driver.switchTo().alert().getText().toString();

			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : An alert was present having text = "+alertText);
			driver.switchTo().alert().accept();

			return passed_status + "An alert is present having text = "+alertText;
		}
		catch(NoAlertPresentException a)
		{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No Alert Was Found.");
			return failed_status + "No alert was present."; 
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the presence of alert. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + "could not verify the presence of alert.";
		}
	}


	/** This keyword is to verify if alert is not present.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String verifyalertnotpresent(WebDriver driver, String objectName, String data)
	{
		try
		{
			/** Get alert text, if no alert then move to NoAlertPresentException exception. */

			String alertText = driver.switchTo().alert().getText().toString();

			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : An alert was present having text = "+alertText);
			driver.switchTo().alert().accept();

			return failed_status + "An alert is present having text = "+alertText;
		}
		catch(NoAlertPresentException a)
		{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No Alert Was Found.");
			return passed_status + "No alert was present."; 
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the presence of alert. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + "could not verify the presence of alert.";
		}
	}


	/** This keyword is to verify the value of supplied element, it checks the text present in VALUE attribute of
	 * supplied element.
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String verifyelementvalue(WebDriver driver, String objectName, String data)throws CustomExceptionStopExecution
	{
		String result = "";
		try
		{
			if(objectName.isEmpty())
			{
				result = failed_status + "Supplied object name is empty. ";
			}
			else
			{
				//Getting data after removing any flag like "must pass"
				if(data.contains(";")){
					data = data.split(";")[0].trim();
				}
				else if(data.contains(",")){
					data = data.split(",")[0].trim();
				}

				/** First the check if the supplied element is a dynamic object which needs data to create element definition, 
				 * if no, then createDynamicWebElement method will throw CustomExceptionsLib exception, and then find the element using 
				 * method: getWebElementFromRepository
				 */
				webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Verifying the value of element: " + webelement );
				String actualValue = webelement.getAttribute("value").trim();

				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : actual value of element : " + actualValue);

				if(actualValue.equalsIgnoreCase(data))
				{
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Value Matched. ");
					result = passed_status + "Value is as expected.";
				}
				else 
				{
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Value Doesn't Match. ");
					result = failed_status + "The actual value is: " + actualValue + ", the expected value is: " + data;
				}
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the value: " + data + " of the element: " +webelement, e);
			result = failed_status + "Could not retrieve the value."; 

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

		}
		return result;
	}


	/** This keyword will be used to scroll objects.
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String scrollobject(WebDriver driver, String objectName, String data) 
	{
		String result = "";
		//By byLocator = null;
		try
		{	
			webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

			//execute js to bring element into view
			String javaScript = "arguments[0].scrollIntoView(false);";
			handler.executeJavaScript(driver, javaScript, webelement);

			result = passed_status+ "Scrolled the bar successfully";
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Scrolled the bar successfully " );
		}
		catch(Exception e){
			result = failed_status + ": Unable to scroll the bar";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while scrolling the bar ", e);
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}

	/** Waiting for element visibility
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String waitforelementvisibility(WebDriver driver, String objectName, String data) 
	{
		String result = "";

		try{
			By bylocator = getObject.getFinalByLocator(driver, objectName, data, jsonObjectRepo);

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" wait for the element visibility - by locator: "+bylocator.toString());

			/** wait for element using by locator - not creating element - to avoid timeout while finding the element */
			if(handler.applyExplicitWait(driver, bylocator, new ElementNotVisibleException(""), 60)) {

				webelement = driver.findElement(bylocator);

				if(webelement.isDisplayed()){
					result = passed_status+ "Element is visible now.";
				}
			}
			else{
				CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
				result = failed_status + "Element is not visible. ";
			}
		}catch(Exception ex){
			result = failed_status + "Unable to wait for the element visibility.";

			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Unable to wait for the element visibility because of: ", ex);

			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** Select the element on the basis of tag name
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String selectdropdownlisting(WebDriver driver, String objectName, String data) 
	{
		String result = "";
		List<WebElement> elements=null;
		String optionValue=null;
		String[] objectArray=null;
		String tagName="";
		try
		{	
			if(objectName.contains("@@")){
				objectArray=objectName.split("@@");
				objectName=objectArray[0];
				tagName=objectArray[1];
			}

			webelement=getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

			if(tagName !=""){
				elements=webelement.findElements(By.tagName(tagName));
			}else{
				elements=webelement.findElements(By.tagName("option"));
			}

			/** applying wait for 10 sec to make options available in drop down listing */
			int counter=0;
			while(counter < 5) {

				if(elements.size()<2){
					Thread.sleep(2000);
					if(tagName !=""){
						elements=webelement.findElements(By.tagName(tagName));
					}else{
						elements=webelement.findElements(By.tagName("option"));
					}
				}else {
					break;
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " +" retry wait for values - "+counter);
				counter ++;
			}

			for(WebElement element:elements){
				optionValue=element.getText().trim();
				System.out.println(optionValue);
				if(optionValue.equalsIgnoreCase(data)){
					element.click();
					result = passed_status + "Clicked on the element matched";
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Selected the option successfully -- Actual ="+optionValue+" and Expected ="+data);
					break;
				}else{
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Input is not matched with the getText() -- Actual ="+optionValue+" and Expected ="+data);
				}
			}
		}
		catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception in selecting from the dropdown", e);
			result = failed_status + " Error occurred while selecting drop down list. ";
		}
		return result;
	}

	/** This is used to escape the auto fill --- pages like package and deal screen
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String escapeautofill  (WebDriver driver, String objectName, String data)
	{
		String result = "";
		try
		{	Actions action = new Actions(driver);
		action.sendKeys(Keys.ESCAPE).build().perform();
		result = passed_status+"Escaped the object successfully";
		}
		catch(Exception e){
			result = failed_status + e.getMessage();
		}
		return result;	
	}


	/** This keyword will bring the desired element in focus.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String getfocusonelement(WebDriver driver, String objectName, String data)
	{
		String result = "";

		try{
			if(!objectName.isEmpty())
			{
				webelement=getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

				Thread.sleep(1000);
				String javaScript = "arguments[0].scrollIntoView(false);";
				Thread.sleep(1000);

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Executing java script: " +javaScript);
				handler.executeJavaScript(driver, javaScript, webelement);

				result = passed_status+ "script executed successfully";
			}
			else
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No Object received to bring into focus.");
				result = failed_status+ "No Object received to bring into focus. ";
			}
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't bring the element in focus. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while moving foucs on element: " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}


	/** This keyword has no use in this class, it is just a flag which is read by ReadTestCases class even before coming to this class,
	 * this is declared here because, if its not declared here then Test Case will try to find this keyword here and that would throw
	 * an error saying this - on_error_resume_next doesn't exist. 
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String on_error_resume_next(WebDriver driver, String objectName, String data)
	{
		if(data.equalsIgnoreCase("no") || data.equalsIgnoreCase("false"))
		{
			return passed_status+"After encountering first failure, subsequent steps won't be executed.";
		}
		else
		{
			return passed_status +"After encountering first failure, subsequent steps will still be executed.";
		}
	}

	/** this keyword will wait until the supplied element is disappeared.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String waitforelementtodisappear(WebDriver driver, String objectName, String data)
	{
		String result = "";
		try{
			By bylocator = getObject.getFinalByLocator(driver, objectName, data, jsonObjectRepo);
			WebDriverWait wait = new WebDriverWait(driver, 60);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(bylocator));

			result = passed_status +" Success. ";
		}
		catch (TimeoutException e) {
			result = warning_status +" Object wasn't disappeared even after waiting for 60 sec. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Element wasn't invisbile. ");
		}
		catch(Exception e)
		{
			result = failed_status +" error occurred while waiting for disappearance of supplied object. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :Exception occured: ", e);
		}
		return result;

	}


	/** This keyword will bring the desired element in focus.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String reloadpage(WebDriver driver, String objectName, String data)
	{
		String result = "";

		try{
			driver.navigate().refresh();
			result = passed_status +" page was refreshed";
		}
		catch(Exception e)
		{
			result = passed_status +" page was not refreshed";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}


	/** This keyword is created specifically for e2e tests where we need the test data in test cases for further usage and the test data is supplied 
	 * after replacing the desired macros. 
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String donothing(WebDriver driver, String objectName, String data)
	{
		return data;
	}


	/** apply delay in seconds
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String applyrandomdelaysec(WebDriver driver, String objectName, String data)
	{
		int delay = 0;
		try
		{
			delay = IntegerLib.GetRandomNumber(6000, 3000);
			Thread.sleep(delay);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Applied Random Delay: "+delay);
		}catch(Exception e){}
		return "PASS: Applied Random Delay of: "+delay;
	}


	/**verify all links for footer and header
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String verifylinks(WebDriver driver, String objectName, String data) 
	{
		String url, text ="";
		int status=0;

		List<String> brokenlinks = new ArrayList<>();

		try{

			By bylocator = getObject.getFinalByLocator(driver, objectName, data, jsonObjectRepo);

			List<WebElement> alllinks = driver.findElements(bylocator);

			for (int i = 0; i < alllinks.size(); i++) {
				url = alllinks.get(i).getAttribute("href");
				text =  alllinks.get(i).getText();

				if (!url.contains("javascript:void")){
					status = httpClientWrap.getStatusCodeOfGetRequest("{"+url+"}", null);

					if (status!=200){
						brokenlinks.add(text + " - " +url);
					}
				}	

			}
		}
		catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ "-" +e.getMessage(), e);
		}
		if (brokenlinks.size()==0){
			return passed_status+" No Broken Link on.";
		}
		else{
			return failed_status + "Links not working for "+brokenlinks.toString();
		}
	}


	/** Get all the links from the supplied bylocator 
	 * 
	 * @param driver
	 * @param bylocator
	 * @return
	 */
	public String getlinksofelement(WebDriver driver, String objectName, String data)
	{
		List<String> urlList = new ArrayList<>();
		try
		{
			By bylocator = getObject.getFinalByLocator(driver, objectName, data, jsonObjectRepo);
			List<WebElement> listMatchingElements = driver.findElements(bylocator);

			for (int i = 0; i < listMatchingElements.size(); i++) {

				WebElement object = listMatchingElements.get(i);

				if(object.getAttribute("href") != null) {
					urlList.add(object.getAttribute("href"));
				}else if(object.getAttribute("src") != null) {
					urlList.add(object.getAttribute("src"));
				}
			}

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : Received URL List: " +urlList.toString());

		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting list: ", e);
		}

		/** just to maintain the same return format -- returning string */
		String urls = urlList.toString().replace("[", "").replace("]", "");
		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " +" received urls : "+urls);

		return urls;
	}


	/** User will supply the data -- which will have comma separated urls 
	 * 
	 * @param urlList
	 * @return
	 */
	public String verifybrokenlinks(WebDriver driver, String objectName, String data)
	{
		List<String> urlList = Arrays.asList(data.split(","));

		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : "+ " Received url list to verify: "+urlList.toString());

		if(urlList.size() < 1) {
			return skip_status+ " No Links Were Supplied To Check. ";
		}else {
			List<String> brokenList = handler.getBrokenLinks(urlList);

			if(brokenList.size() > 0) {
				return failed_status+" Broken Links: "+brokenList.toString();
			}else {
				return passed_status+" No Broken Links Found. ";
			}
		}
	}


	/** Keyword to execute java script, in case object name is supplied then its assumed then object is required to execute the supplied js
	 * if not provided then execute js and return output.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String executejavascript(WebDriver driver, String objectName, String data)
	{
		/**
		 * Sample code, if javascript has to be executed on a webelement:
		 * js.executeScript("arguments[0].click()", webelement);
		 */

		Object objJsOutput;
		try
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : " +" Executing java script: "+data);

			/** for sync */
			Thread.sleep(1000);

			String javaScript = data;
			JavascriptExecutor js = (JavascriptExecutor) driver;

			/** just to bring browser back in focus - can be removed later on */
			objJsOutput = js.executeScript("alert('Hi')");
			driver.switchTo().alert().accept();

			if(!objectName.isEmpty()) {
				By bylocator = getObject.getFinalByLocator(driver, objectName, data, jsonObjectRepo);
				webelement = driver.findElement(bylocator);	

				if(webelement != null){
					objJsOutput = js.executeScript(javaScript, webelement);
					return passed_status + "Java Script Executed. ";
				}else {
					return failed_status + "Element found NULL, Java Script Not Executed. ";
				}
			}		
			else{
				/** if object name is not supplied then execute the supplied js and return the output */
				objJsOutput = js.executeScript(javaScript);
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : " + " javascript output is: "+objJsOutput);

				return String.valueOf(objJsOutput);
			}
		}
		catch(Exception ex)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while executing java script: "+data +" for supplied element: "+webelement, ex);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status+ "Java Script Executed With Errors. ";
		}
	}

	/** this keyword will return the network requests
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String getproxylog(WebDriver driver, String objectName, String data)
	{
		String result= "";

		String har = TestSuiteClass.AUTOMATION_HOME+"/HAR-"+TestSuiteClass.UNIQ_EXECUTION_ID.get()+"-"+new Date().getTime();
		try{
			if(proxyServer != null){
				proxyServer.getHar().writeTo(new File(har));
				LineIterator it = FileUtils.lineIterator(new File(har));
				StringBuffer str = new StringBuffer();

				while(it.hasNext())
				{
					str.append(it.next());
				}

				JSONParser jsonParser = new JSONParser();
				org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonParser.parse(str.toString());
				org.json.simple.JSONObject log=(org.json.simple.JSONObject) jsonObject.get("log");
				org.json.simple.JSONArray array=(org.json.simple.JSONArray) log.get("entries");

				for(int i=0; i<array.size(); i++){
					org.json.simple.JSONObject finalObject=(org.json.simple.JSONObject) array.get(i);
					org.json.simple.JSONObject request=(org.json.simple.JSONObject) finalObject.get("request");
					org.json.simple.JSONObject response=(org.json.simple.JSONObject) finalObject.get("response");
					org.json.simple.JSONObject content=(org.json.simple.JSONObject) response.get("content");

					String requestedUrl="";
					String status ="";
					String responseText ="";
					String headers ="";
					try{
						requestedUrl=request.get("url").toString();
					}catch(NullPointerException e){

					}

					try{
						status=response.get("status").toString();
					}catch(NullPointerException e){

					}

					try{
						responseText=content.get("text").toString();
					}catch(NullPointerException e){

					}

					try{
						headers=handler.getHeaders(request);
					}catch(NullPointerException e){

					}

					result = result + "Request ==> " + requestedUrl +
							"\nStatus ==> " + status +
							"\nResponse ==> " + responseText +
							"\nHeaders ==> " + headers + "\n";
				}
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Logs are generated for the test case");	

				/** stopping server after getting har file */
				proxyServer.stop();
			}else{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Proxy server wasn't started. ");
			}
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occured while getting the log file", e);
		}

		return result;
	}


	/** This method will return all the console logs, later on this can be refined - like just to get errors etc. 
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String getbrowserconsolelogs(WebDriver driver, String objectName, String data)
	{
		String result = "";

		if(driver instanceof FirefoxDriver){

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " firefix driver doen't support console logs. ");
			result=warning_status +" can not get log in firefox.";

			return result;

		}else{

			try
			{
				LogEntries en = driver.manage().logs().get(LogType.BROWSER);

				for(LogEntry e : en)
				{
					result = result + e.getLevel() + " ==> " + e.getMessage() + "\n";
				}
			}catch (Exception e) {
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occurred while getting console browser logs. ", e);
			}
			return result;
		}
	}

	/** this keyword will return status as skip all for halting following steps
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String skipfollowingsteps(WebDriver driver, String objectName, String data){
		String result=skip_status;
		try{
			result=skip_following_steps;
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Expected result matched hence skipping following steps ");

		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : skip status set ", e);
		}
		return result;
	}


	/** this keyword is used to hover on webelement
	 * @param driver
	 * @param objectName
	 * @param data
	 */
	public String hoveronwebelement_usingaction(WebDriver driver, String objectName, String data){
		String result ="";

		try
		{
			webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

			//Commented the action class
			Actions hover=new Actions(driver);
			hover.moveToElement(webelement).build().perform();

			result = passed_status + " hover successful using action class. ";

		}catch (Exception e) {
			result = failed_status + " hover unsuccessful due to some error ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occurred while hover ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}

	/** this keyword is used to scroll on the webpage
	 * @param driver
	 * @param objectName
	 * @param data
	 */
	public String scrollpagedown(WebDriver driver, String objectName, String data){
		String result ="";

		try
		{
			/** in case no object is supplied the also execute the js to move page down, expect coordinates in data like - 0, 100  */
			if(objectName.isEmpty()) {

				//execute js to bring element into view
				String javaScript = "window.scrollBy("+ data +");";
				handler.executeJavaScript(driver, javaScript, webelement);
				result = passed_status + " Scroll successfully using javascript. ";
			}
			else {
				webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

				//execute js to bring element into view
				String javaScript = "window.scrollBy(0, 100);";
				handler.executeJavaScript(driver, javaScript, webelement);
				result = passed_status + " Scroll successfully using javascript. ";
			}
		}catch (Exception e) {
			result = failed_status + " Scroll unsuccessful due to some error ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occurred while scrolling ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}


	/** this keyword is used to hover on webelement using java script
	 * @param driver
	 * @param objectName
	 * @param data
	 */
	public String hoveronwebelement_usingjs(WebDriver driver, String objectName, String data){
		String result ="";

		try
		{
			webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);

			/** javascript for the mouse over event */
			String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover', "
					+ "true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " executing js to hover: "+mouseOverScript);
			((JavascriptExecutor) driver).executeScript(mouseOverScript, webelement);

			result = passed_status + " hover successful using js. ";

		}catch (Exception e) {
			result = failed_status + " hover unsuccessful due to some error ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occurred while hover ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}

	/** get current page url.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String getcurrentpageurl(WebDriver driver, String objectName, String data){
		try
		{
			return driver.getCurrentUrl();

		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occurred getting url.", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + " couldn't get driver url. ";
		}
	}

	/** click using javaScript
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String clickusingjs(WebDriver driver, String objectName, String data)
	{
		Object objJsOutput;
		try
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : " +" Executing java script: ");

			String javaScript = "arguments[0].click()";
			webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);
			JavascriptExecutor js = (JavascriptExecutor) driver;

			if(webelement != null)
			{				
				/** this is strange - in case supplied element is found and xpath is locator then use pure java script to click. */
				if(jsonObjectRepo.getJSONObject(objectName).getString("identifierName").equalsIgnoreCase("xpath")) {

					/** get the locator value from repo --> after replacing data also . if there is any */
					String xpathLocator = getObject.getLocatorValueFromObjectRepo(objectName, data, jsonObjectRepo);
					javaScript = "document.evaluate(\""+xpathLocator+"\" ,document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue.click();";
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " executing js: "+javaScript );
				objJsOutput = js.executeScript(javaScript, webelement);
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " executed js: "+javaScript + " and js output - : "+objJsOutput);
				return passed_status + "Java Script Executed. ";					
			}
			else
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " null element found, skipping execute js. ");

				/** Taking screenshot during exception */
				CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

				return failed_status + "Null Element Received, Java Script Execution Skipped. ";
			}
		}
		catch(Exception ex)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while executing java script: "+data +" for supplied element: "+webelement, ex);
			return failed_status + "Java Script Executed With Errors: "+ex.getMessage();
		}
	}

	/** get the selected value from drop down.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String  getselectedvaluefromdropdown(WebDriver driver, String objectName,String data) {

		String selectedValue="";

		try {
			webelement = getObject.getFinalWebElement(driver, objectName, data, jsonObjectRepo);
			Select selctedelement= new Select(webelement);
			selectedValue= selctedelement.getFirstSelectedOption().getText();

		}catch(Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :  exception occurred  while  getting selected value " + e);
		}

		return selectedValue;
	}


	/** this method will pass / fail based on console log failures.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String verifybrowserconsole(WebDriver driver, String objectName, String data)
	{
		String ConsoleLogs = "";
		String result = "";

		try
		{
			LogEntries en = driver.manage().logs().get(LogType.BROWSER);

			for(LogEntry e : en)
			{
				/** excluding INFO and FINE log level, adding only SEVERE, WARNING */
				if(
						(e.getMessage().contains("api.lenskart.com") || e.getMessage().contains("www.lenskart.com"))
						&& e.getLevel().toString().equalsIgnoreCase("ERROR")
						)
				{
					ConsoleLogs = ConsoleLogs + e.getLevel() + " ==> " + e.getMessage() + "\n";
					result = failed_status + " Failed  because getting error in console error: "+ConsoleLogs;

					break;  
				}

			}
			if(result.contains(failed_status)) {
				return result;
			}else {
				result = passed_status + " did not find any error in browser console. ";
			}

		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occured while checking browser console logs. ", e);
			result = failed_status + "Exception occurred while checking browser console logs. ";
		}
		return result;
	}


	/** This keyword closes the browsers tab opened by automation code.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	
	public  String closetab  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Closing current browser tab. ");

			/** kill emulator for appium driver */
			if(driver instanceof AppiumDriver<?>) {
				String deviceUDID = SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().get(TestSuiteClass.UNIQ_EXECUTION_ID.get()).getDeviceUDID();
				new GenericMethodsLib().killEmulator(deviceUDID);
			}
			else{
				
				/** for normal browser */
				driver.close();
			}

			result = passed_status+ "Current Browser tab closed successfully.";
		}catch(Exception e)
		{
			result = warning_status + "Couldn't close browser tab. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while closing browser tab. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


}