/**
 * Last Changes Done on Jan 16, 2015 12:06:11 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */

package core.classes;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger; 
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import core.utilities.CaptureScreenShotLib;
import core.utilities.CustomException;
import core.utilities.GenericMethodsLib;
import tests.TestSuiteClass;


/**
 * This class is to get the webelements corresponding to objects received from test cases.
 */

public class GetObjects 
{

	String objectRepository;
	String objectNameColumnName;
	String identifierName;
	String identifierValue;
	String objectLabel;

	Logger logger = Logger.getLogger(GetObjects.class.getName());


	/**
	 * This constructor defines Object Repository location and various column name.
	 */
	GetObjects()
	{
		this.objectNameColumnName = "objectName";
		this.identifierName="identifierName";
		this.identifierValue="identifierValue";
		this.objectLabel = "objectLabel";

		//logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : objectName column: "+objectNameColumnName+ ", identifierName column: "+identifierName+ ", identifierValue column: "+identifierValue + ", objectLabel column: "+objectLabel);
	}


	/**
	 * This function is to get the object definitions from repository based on
	 * the supplied objectName or objectLabel.
	 * This method throws a custom exception: CustomExceptionsLib in case there is no object found based on supplied objectName.
	 * 
	 * @param objectName
	 * @param objectNameOrLabel
	 * @param driver
	 * @return
	 */
	private WebElement getWebElementFromRepository(String objectName, WebDriver driver, By byLocator) throws CustomException
	{
		logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Got the object name : "+objectName);

		WebElement webelement = null;
		boolean objectNotFound = false;
		objectName = objectName.trim();

		try
		{	
			if(objectName.isEmpty())
			{
				objectNotFound = true;
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Supplied object was empty or this object isn't present in OR, object name: "+objectName);
			}
			else{
				/** Get webelement for the supplied definition from object repository */
				webelement = createWebElement(driver, objectName, byLocator);
			}
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting webelement from repository:" +objectRepository, e);
		}

		/** Throw a Customexception if supplied object not found in repository */
		if(objectNotFound)
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Either object: "+objectName +" was not found in repository or its definition is blank. ");
			throw new CustomException("Object: "+objectName +" was not found in repository. ");
		}
		return webelement;
	}

	/** This method will retrieve the locator and locator value of supplied the object from Object Repository Sheet and create the By locator 
	 * for this object and return that. 
	 * 
	 * @param objectName
	 * @param driver
	 * @return
	 * @throws CustomException 
	 */
	private By createStaticByLocator (String objectName, WebDriver driver, JSONObject jsonObjectRepo) throws CustomException
	{
		String identifierName = "";
		String identifierValue = "";

		By by = null;
		boolean objectNotFound = false;
		try
		{
			/************ new code to lookup object from json ************/

			/** get identifier name and value of received object name */
			try{identifierName = jsonObjectRepo.getJSONObject(objectName).getString("identifierName");}catch(JSONException e){}; 
			try{identifierValue = jsonObjectRepo.getJSONObject(objectName).getString("identifierValue");}catch(JSONException e){};

			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : identifierName: "+identifierName+ " identifierValue: "+identifierValue + " for object: "+objectName);

			if(identifierName.isEmpty() || identifierValue.isEmpty())
			{
				objectNotFound = true;
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : There was no webelement definition found in Object Repository for object name: "+objectName);
			}
			else
			{
				/** Get webelement for the supplied definition from object repository */
				by = createByLocator(objectName, identifierName, identifierValue);
			}
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting by locator from repository:" +objectRepository, e);
		}

		/** Throw a Customexception if supplied object not found in repository */
		if(objectNotFound)
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Object: "+objectName +" was not found in repository. ");
			throw new CustomException("Object: "+objectName +" was not found in repository. ");
		}
		return by;

	}

	/** This method will find the element based on the supplied byLocator.
	 *
	 * @param driver 
	 * @param objectName 
	 * @param identifierName 
	 * @param identifierValue 
	 * @return 
	 */
	@SuppressWarnings({ "finally", "unchecked", "rawtypes" })
	private WebElement createWebElement(WebDriver driver, String objectName, By byLocator)
	{
		WebElement webElementFound = null;

		String locationToSaveSceenShot = TestSuiteClass.AUTOMATION_HOME.concat("/screenshots/").concat("ErrorElement").concat("/").concat(objectName + ".png");

		try
		{

			/** Get Webdriver class dynamically */
			Class webDriverClass = Class.forName(WebDriver.class.getName());

			/** Get Webdriver Method - to be used while finding web elements */
			Method webDriverMethod = webDriverClass.getMethod("findElement", new Class[]{By.class});

			try {
				/** Invoke WebDriver method like w.findElement(By.cssSelector("v")) and collect Web Element */ 
				webElementFound = (WebElement) webDriverMethod.invoke(driver, byLocator);
			}
			catch(NullPointerException | InvocationTargetException ne){
				
				handleInvocationException(driver, byLocator, objectName);
				
				/** Retry Invoke WebDriver method like w.findElement(By.cssSelector("v")) and collect Web Element */
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : retrying for element: "+objectName );
				webElementFound = (WebElement) webDriverMethod.invoke(driver, byLocator);
			}

			/** execute java script to bring element into focus */
			new HandlerLib().executeJavaScript(driver, "arguments[0].scrollIntoView(false);", webElementFound);
		}
		catch (Exception e) 
		{
			/** Capture Screenshot */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot); 
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while finding element: "+objectName + " By Locator : " + byLocator + " screenshot: "+locationToSaveSceenShot, e);
		}
		finally
		{
			return webElementFound;
		}
	}

	/** apply explicit wait - while finding element  
	 * 
	 * @param driver
	 * @param byObjectCollectWebElement
	 */
	private void handleInvocationException(WebDriver driver, By byObjectCollectWebElement, String objectName)
	{
		if(GenericMethodsLib.generalConfigurationProperties.getProperty("isRequired_objectVisibilityExplicitDelay").toString().trim().equalsIgnoreCase("yes"))
		{
			String delay = GenericMethodsLib.generalConfigurationProperties.getProperty("objectVisibilityExplicitDelay").toString().trim();

			/** wait for x sec until the element is not present */
			new HandlerLib().applyExplicitWait(driver, byObjectCollectWebElement, new NoSuchElementException(""), Integer.parseInt(delay));

			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : invocation exception handled by applying wait for: "+ delay +" for element: "+objectName );
		}
	}

	/** This method will find the by type of web element based on the supplied identifier name and value.
	 *
	 * @param driver 
	 * @param objectName 
	 * @param identifierName 
	 * @param identifierValue 
	 * @return 
	 */
	@SuppressWarnings({ "finally", "unchecked", "rawtypes" })
	private By createByLocator(String objectName, String identifierName, String identifierValue)
	{
		By byObjectToInvokeMethod = null;
		By byObjectCollectWebElement = null;

		try
		{
			/** Get By Class Dynamically */
			Class byClass = Class.forName(By.class.getName());

			/** Get By Method - to be used while finding web elements */
			Method byMethod = byClass.getMethod(identifierName, String.class);

			/** Invoke By Method like By.cssSelector("v") and collect value */
			byObjectCollectWebElement = (By) byMethod.invoke(byObjectToInvokeMethod, identifierValue); 
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while finding element: "+objectName, e);
		}
		finally
		{
			return byObjectCollectWebElement;
		}
	}

	/** This method will retrieve the supplied the object locator and its value from Object Repository Sheet  
	 * and return the identifier name and identifier value
	 * 
	 * @param objectName
	 * @param jsonObjectRepo
	 * @return
	 * @throws CustomException
	 */
	private HashMap<String, String> getObjectLocatorNameValueFromRepository(String objectName, JSONObject jsonObjectRepo) throws CustomException
	{
		logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Got the object name : "+objectName);

		String identifierName = "";
		String identifierValue = "";

		HashMap<String, String> identifierMap = new HashMap<>();
		try
		{
			/** if user supplied object label then get the respective object name - for further processing */

			/** get identifier name and value of received object name */
			try{identifierName = jsonObjectRepo.getJSONObject(objectName).getString("identifierName");}catch(JSONException e){}; 
			try{identifierValue = jsonObjectRepo.getJSONObject(objectName).getString("identifierValue");}catch(JSONException e){};

			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : identifierName: "+identifierName+ " identifierValue: "+identifierValue + " for object: "+objectName);
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting by locator from repository:" +objectRepository, e);
		}

		/** Throw a Customexception if supplied object not found in repository */
		if(identifierName.isEmpty() || identifierValue.isEmpty())
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : There was no webelement definition found in Object Repository for object name: "+objectName);
			throw new CustomException("Object: "+objectName +" was not found in repository. ");
		}			

		/** returning identifier name and identifier value */
		identifierMap.put(identifierName, identifierValue);

		return identifierMap;
	}

	/** This is the final method to get the webelement
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @param jsonObjectRepo
	 * @return
	 * @throws CustomException
	 */
	public WebElement getFinalWebElement(WebDriver driver, String objectName, String data, JSONObject jsonObjectRepo) throws CustomException
	{
		WebElement webelement;
		By byLocator;

		try{
			/** create dynamic element */
			byLocator = createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
		}catch(CustomException e)
		{
			/** create element normally */
			byLocator=createStaticByLocator(objectName, driver, jsonObjectRepo);
		}

		/** create web element */
		webelement = new GetObjects().getWebElementFromRepository(objectName,  driver, byLocator);

		return webelement;
	}

	/** get final by locator 
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @param jsonObjectRepo
	 * @return
	 * @throws CustomException
	 */
	public By getFinalByLocator(WebDriver driver, String objectName, String data, JSONObject jsonObjectRepo) throws CustomException
	{
		By byLocator = null;

		try{
			/** create dynamic locator */
			byLocator = createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
		}catch(CustomException e)
		{
			/** create locator normally */
			byLocator = new GetObjects().createStaticByLocator(objectName, driver, jsonObjectRepo);
		}

		return byLocator;
	}

	/** Note# For all elements which needs to be created by this method, always use case in-sensitive xpath.
	 *  
	 * This method will be used to create the web element dynamically, will be
	 * used to find records in search functionality: first get the locator name
	 * and locator value from repository and then replace ~~ in locator value
	 * with the supplied data from test case and then create the web element.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 * @throws CustomException 
	 */
	private By createDynamicByLocator(WebDriver driver, String objectName, String data, JSONObject jsonObjectRepo) throws CustomException
	{
		boolean throwException = false;
		String exceptionMessage = "";
		String locatorName = "";
		String locatorValue = "";
		By byLocator = null;
	
		try
		{
			//Proceed only supplied data is not empty
			if(data.isEmpty())
			{
				throwException = true;
				exceptionMessage = "Supplied data should not be empty.";
			}
			else
			{
				/** get object locator and locator value map */
				HashMap<String, String> identifierMap = new GetObjects().getObjectLocatorNameValueFromRepository(objectName, jsonObjectRepo);
				locatorName = identifierMap.keySet().iterator().next();
				locatorValue = identifierMap.get(locatorName).trim();
	
				/** Object locator value may contain ~~ which needs to be replaced by the data supplied from test cases sheet to create locator value 
				 * dynamically using the supplied data and then create element and then do further processing.
				 */
				if(locatorValue.contains("~~"))
				{
					data = data.toLowerCase().trim();
	
					/** commenting this line */
					//locatorValue = locatorValue.replace("~~", data).trim();
	
					/** New Change - need to handle a case where xpath is like - abc[~~]/abc/lll/ab{~~}/abc/ab/a(~~) and supplied data is like - 1, 2, 3
					 * and final definition is :  abc[1]/abc/lll/ab{2}/abc/ab/a(3)  -- what happened here ?
					 * replacing the ~~ with the supplied data in sequence.   
					 * */
	
					locatorValue = getDynamicLocator(locatorValue, data);
					/*************************************************************************************/
	
					//Create a By Locator to be used in finding elements.
					byLocator = new GetObjects().createByLocator(objectName, locatorName, locatorValue);
				}
				else
				{
					/**
					 * In case if the supplied object is not dynamic then throw custom exception
					 */
					throwException = true;
					exceptionMessage = "Object: "+objectName +" is not a dynamic element. ";
				}
			}
		}
		catch(CustomException e)
		{
			logger.error(e.getMessage());
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting webelement for object: "+objectName, e);
		}
	
		if(throwException)
		{
			throw new CustomException(exceptionMessage);
		}
	
		return byLocator;
	}

	/** get dynamic xpath after replacing ~~ with actual value
	 * 
	 * @return
	 */
	private String getDynamicLocator(String locatorValue, String data)
	{
		String[] locators ;
		int locatorsCount ;
		String updatedLocatorValue = "";
		try
		{	/** if we have multiple xpath separated with pipe then need to replace input data in each xpath */
			if(locatorValue.contains("|")){
	
				locators = locatorValue.split("\\|");
	
				/** get the count of xpath */
				locatorsCount = locators.length;
	
				Set<String> set=new HashSet<String>();
	
				/** replace ~~ with data for each xpath */
				for(int i=0; i<locatorsCount; i++){
					set.add(getDynamicLocator_Original(locators[i], data));
				}
				for(String xpath : set){
					updatedLocatorValue += xpath+"|";
				}
				locatorValue =updatedLocatorValue.substring(0, updatedLocatorValue.length()-1);
			}else{
				locatorValue = getDynamicLocator_Original(locatorValue, data);
			}
		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + e.getMessage(), e );
		}
		return locatorValue.trim(); 
	}

	/** get dynamic xpath after replacing ~~ with actual value
	 * 
	 * @return
	 */
	private String getDynamicLocator_Original(String locatorValue, String data)
	{
		try
		{	
			List<String> xxx = Arrays.asList(data.split(","));
			if(xxx.size()==1)
			{
				locatorValue = locatorValue.replace("~~", xxx.get(0).trim());
			}
			else
			{
				int i=0;
				while(locatorValue.contains("~~"))
				{
					try {
						locatorValue = locatorValue.replaceFirst("~~", xxx.get(i).trim());
					}catch (ArrayIndexOutOfBoundsException e) {
						locatorValue = locatorValue.replaceFirst("~~", xxx.get(xxx.size()-1).trim());
					}
					i++;
				}	
			}
		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + e.getMessage(), e );
		}
		return locatorValue; 
	}

	/**
	 * This method will return the locator value from repo if data driven then after replacing with data. 
	 * @param objectName
	 * @param data
	 * @param jsonObjectRepo
	 * @return
	 */
	public String getLocatorValueFromObjectRepo(String objectName, String data, JSONObject jsonObjectRepo) 
	{
		String locatorValue = "";
	
		try
		{
			/** get object locator and locator value map */
			HashMap<String, String> identifierMap = new GetObjects().getObjectLocatorNameValueFromRepository(objectName, jsonObjectRepo);
			String locatorName = identifierMap.keySet().iterator().next();
			locatorValue = identifierMap.get(locatorName);
	
			/** Object locator value may contain ~~ which needs to be replaced by the data supplied from test cases sheet to create locator value 
			 * dynamically using the supplied data and then create element and then do further processing.
			 */
			if(locatorValue.contains("~~"))
			{
				data = data.toLowerCase().trim();
	
				/** New Change - need to handle a case where xpath is like - abc[~~]/abc/lll/ab{~~}/abc/ab/a(~~) and supplied data is like - 1, 2, 3
				 * and final definition is :  abc[1]/abc/lll/ab{2}/abc/ab/a(3)  -- what happened here ?
				 * replacing the ~~ with the supplied data in sequence.   
				 * */
				locatorValue = getDynamicLocator(locatorValue, data).trim();
			}
		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " error: "+e.getMessage(), e);
		}
		
		return locatorValue;
	}

}
