package core.classes;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

import com.mysql.jdbc.Connection;

import core.utilities.CaptureScreenShotLib;
import core.utilities.GenericMethodsLib;
import net.lightbody.bmp.proxy.ProxyServer;
import tests.TestSuiteClass;

public class RetryController {

	public static final String reloadPage ="reloadpage";

	public static final String retryPreviousStep ="retryPreviousStep";

	public static final String retryCurrentStep = "retryCurrentStep";

	public static final String noActionRequired = "doNothing";

	Logger logger = Logger.getLogger(RetryController.class.getName());

	/**
	 * This method will evaluate the action required to perform in case or retry
	 * @param driver
	 * @param description
	 * @param result
	 * @param pageSource
	 * @param url
	 * @return
	 */
	public String evaluateRetry(WebDriver driver, String result, TestStepObject testStepObject,
			List<TestStepObject> testStepObjectList, int index){

		String retryStatus = noActionRequired;

		try{

			if(!testStepObject.isPageLoadedProperly()) {
				retryStatus = reloadPage;
			}

			/**Check if test case step has passed and after that - it should redirect to a new url. <Only this condition is being handled here.> 
			 */
			else if(testStepObject.getTestStepDescription().contains("Check_Post_Success_Navigation") && result.contains("Pass")){

				// Send retry page url of previous step
				if(driver.getCurrentUrl().equalsIgnoreCase(testStepObjectList.get(index-1).getCurrentStepUrl())){
					retryStatus =retryCurrentStep;
				}else{
					retryStatus =noActionRequired;
				}
			}

			/**This condition check if previous step has passed but actually didn't redirect to the next page or overlay (not necessary the url change) and current step failed due to
			 * that, then we need to retry both current and the previous step. For e.g. On the Product page, step1- click on BuyNow is passed but cart
			 * is not displayed and step2- verify navigation cart page is displayed or not failed.
			 * 
			 * -- yet to analyze more on this -- Pankaj
			 */
			else if(testStepObject.getTestStepDescription().contains("Check_Post_Failed_Navigation") && result.contains("Fail")){ 	//verify navigation
				retryStatus = retryPreviousStep;
			}

		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " error while evaulating retry condition");
		}

		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " Final Action OnRetry Step: " +retryStatus);

		return retryStatus;
	}


	/**
	 * This method will perform action based on the condition received for retry case
	 * @param driver
	 * @param keyword
	 * @param object
	 * @param data
	 * @param connection
	 * @param jsonObjectRepo
	 * @param proxyServer
	 * @param retryAction
	 * @param action
	 * @param testStepObjectRetryList
	 * @param index
	 * @param firstResult
	 * @return
	 */
	public String retryAction(WebDriver driver, Connection connection, JSONObject jsonObjectRepo, ProxyServer proxyServer, 
			String retryAction, PerformAction action, List<TestStepObject> testStepObjectList, int index, String firstResult, TestStepObject testStepObject){

		String result ="";
		try{
			if(retryAction.equalsIgnoreCase(reloadPage)){
				result=reloadCurrentPage(driver);

			}else if(retryAction.equalsIgnoreCase(retryCurrentStep)){

				result = action.performAction(driver, testStepObject.getKeyword(), testStepObject.getObjectName(), testStepObject.getData(), 
						connection, jsonObjectRepo, proxyServer, testStepObject);
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + "Retry action "+retryAction + " is performed successfully");

			}else if(retryAction.equalsIgnoreCase(retryPreviousStep)){

				/** get the previous test step object and get all the values from this object */
				TestStepObject previousTestStepObject=testStepObjectList.get(index-1);

				/** attempt a retry */
				result = action.performAction(driver, previousTestStepObject.getKeyword(), previousTestStepObject.getObjectName(), 
						previousTestStepObject.getData(), connection, jsonObjectRepo, proxyServer, testStepObject);

				/** update the retried- result in the same test object */
				testStepObjectList.get(index-1).setTestStepRetryResult(result);

				if(result.contains("Pass")){
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + "Pervious Step execution is successful");

					result= action.performAction(driver, testStepObject.getKeyword(), testStepObject.getObjectName(), testStepObject.getData(), 
							connection, jsonObjectRepo, proxyServer, testStepObject);
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + "Current action is performed successfully");
				}else{
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " Retry previous Test step failed");
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + "Retry action "+retryAction + " is performed successfully");
			}
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " Unable to perform the retry action");
		}

		return result;
	}


	/**
	 * This method is used to reload the current page and verify if still mobbed page is getting displayed or not
	 * @param driver
	 * @return
	 */
	public String reloadCurrentPage(WebDriver driver){
		String result ="";

		try{
			String url="";

			/** not loading in case of android driver - need to find a different approach */
			try {
				url = driver.getCurrentUrl();
			}catch (NullPointerException e) {

				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + "received null driver ... ", e);
				return "Fail: "+" Null driver is received. "; 
			}

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timeStamp=Long.toString(timestamp.getTime());

			/** reload the current page*/
			driver.get(url+"?t="+timeStamp);

			/** Now check if still mobbed page is getting displayed then mark it as failed*/
			if(driver.getPageSource().contains("cloudfront.net/overcrowded.jpg") || driver.getPageSource().contains("The connection was reset") ) {
				result= "Fail: Page is still not displayed correctly";
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " mobbed page is displayed even after the page reload, page source is as - ");
			}else{

				result= "Pass: Page is displayed upon retrying";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " Page is reloaded and mobbed page is not coming the the source code ");
			}
		}catch(Exception e){

			result= "Fail: Page is not displayed correctly on reload / retry { may be error occurred while getting current page url. }. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " Unable to reload the page, error: "+ e.getMessage(), e);
		}
		return result;
	}


	/** store every data from teststep to retry step object. 
	 * 
	 * @param testStepObjectRetry
	 * @param driver
	 * @param keyword
	 * @return
	 */
	public TestStepObject storeDataForRetry(TestStepObject testStepObject, WebDriver driver)
	{
		try
		{
			if(driver != null) {

				/** in case - driver is received as not null but there is any exception while getting driver data - then */
				String url="";
				String pageSource = "";

				try {
					url = driver.getCurrentUrl();
					pageSource = driver.getPageSource();

				}catch (Exception e) {

					/** to avoid reloading a null driver */
					testStepObject.setPageLoadedProperly(true);
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " - received null driver - will not be reloading ... ", e); 
				}

				testStepObject.setCurrentStepUrl(url);

				/** using a boolean rather than storing page source to reduce object size */
				if(pageSource.contains("cloudfront.net/overcrowded.jpg") || pageSource.contains("The connection was reset")){
					testStepObject.setPageLoadedProperly(false);
				}else {
					testStepObject.setPageLoadedProperly(true);
				}
			}
		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + ":  "+e.getMessage(), e);
		}
		return testStepObject;
	}


	/**
	 * This method will perform retry based on the condition provided. 
	 * @param driver
	 * @param data
	 * @param jsonObjectRepo
	 * @param proxyServer
	 * @param action
	 * @param testStepObjectList
	 * @param index
	 * @param firstResult
	 * @param testStepObject
	 * @return
	 */
	public String checkAndPerformRetry(WebDriver driver, Connection connection, JSONObject jsonObjectRepo, ProxyServer proxyServer, 
			PerformAction action, List<TestStepObject> testStepObjectList, int index, String firstResult, TestStepObject testStepObject){

		String result = firstResult;
		try{

			/** no action needed in these cases - */
			if(testStepObject.getKeyword().equalsIgnoreCase("closebrowser") 
					|| testStepObject.getKeyword().equalsIgnoreCase("getproxylog") 
					|| testStepObject.getKeyword().equalsIgnoreCase("getbrowserconsolelogs")
					|| testStepObject.getKeyword().toLowerCase().trim().startsWith("storedata_") 
					|| testStepObject.getKeyword().toLowerCase().trim().startsWith("get_") 
					|| testStepObject.getKeyword().toLowerCase().trim().startsWith("retrievedataobject")) {
				return result;
			}
			else
			{
				/** store data for retry */
				testStepObject = storeDataForRetry(testStepObject, driver);

				/** get the action - need to perform for retry */
				String retryAction=evaluateRetry(driver, result, testStepObject, testStepObjectList, index);

				if(!retryAction.equalsIgnoreCase(RetryController.noActionRequired)){

					/** set the actual result in test step object*/
					testStepObject.setTestStepBeforeRetryResult(firstResult);

					/** set the actual result in retry test step object*/
					testStepObject.setTestStepRetryResult(firstResult);

					/** perform retry action */
					result=retryAction(driver, connection, jsonObjectRepo, proxyServer, retryAction, action, testStepObjectList, index, result, testStepObject);

					if(result.contains("Fail:")){

						/** Taking screenshot during exception */
						CaptureScreenShotLib.captureScreenShot(driver, new KeywordsExtended(connection, jsonObjectRepo, proxyServer).locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Retry - Screenshot is captured at: "+new KeywordsExtended(connection, jsonObjectRepo, proxyServer).locationToSaveSceenShot);
					}
				}
				else{
					result = firstResult;
				}

				return result;
			}
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " error occured while performing the action "+ e.getMessage(), e);
			result = "Fail: Unable to complete the checkAndPerform Retry";
		}
		return result;
	}

}
