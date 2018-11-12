/**
 * Last Changes Done on Feb 3, 2015 2:41:00 PM
 * Last Changes Done by ${author}
 * Purpose of change: 
 */
package core.utilities;


import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger; 
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import core.classes.HandlerLib;
import core.classes.SingletonTestObject;
import core.classes.TestCaseObject;
import io.appium.java_client.AppiumDriver;
import tests.SuiteClass;


public class CaptureScreenShotLib 
{

	static Logger logger = Logger.getLogger(CaptureScreenShotLib.class.getName());

	static Robot robot;


	/** Constructor is being used to initialize the robot instance, because whenever Robot object is created,   
	 * the test browser specially chrome goes into background and thus looses focus. Therefore before using the
	 * method: captureScreenShot(String locationToSaveSceenShot), call this constructor: CaptureScreenShotLib(Robot robot)
	 * in annotation @beforeTest in the actual test before setting up the browser.   
	 * 
	 * @param robot
	 */
	public CaptureScreenShotLib(Robot robot)
	{
		CaptureScreenShotLib.robot = robot;
	}


	/** This method will be used to take the browser screenshot using selenium. In the event of exception, whole screen
	 * is captured.
	 * 
	 * @param driver
	 * @param locationToSaveSceenShot
	 */
	public synchronized static void captureScreenShot(WebDriver driver, String locationToSaveSceenShot)
	{	if(driver!=null) {	
		try
		{
			boolean proceed = true;

			/** adding a check in case of Android appium driver - normally while taking a screenshot, appium server sends a get request like:
			 * http://localhost:53233/wd/hub/session/a501456f-738c-4157-98bf-5522e7ca7199/screenshot and waits for this response for a longer time, so before taking a screenshot
			 * first we'll send the request and if we get the response then only screenshot will be taken else not - to avoid hanging problem. */
			if(driver instanceof AppiumDriver<?>) {

				/** get appium driver url from testcase object */
				TestCaseObject testCaseObject = SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().get(SuiteClass.UNIQ_EXECUTION_ID.get());
				String serverURL = testCaseObject.getAppiumDriverURL();
				String sessionId = testCaseObject.getAppiumDriverSessionId().toString();

				/** this is the final url which will be sent by appium server to chrome server in case of mobile website */
				serverURL = serverURL.replace("0.0.0.0", "localhost").concat("/session/"+sessionId+"/screenshot");
				HashMap<Object, Object> response = new httpClientWrap().sendGetRequestWithParams(serverURL);

				if( (response == null || response.isEmpty()) || (Integer)response.get("statuscode") != 200 || response.get("response").toString().isEmpty()) 
				{
					proceed = false;
					try{logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+ " from url: " +serverURL + " - no screenshot will be taken in case of appium driver. received map: " +response);}catch (Exception e) {}
				}
			}

			/** normal behavior in case of webdriver */
			if(proceed) {

				String directoryForScreenShot = StringLib.splitDirectoryFromFileLocation(locationToSaveSceenShot);
				if(FileLib.CreateDirectory(directoryForScreenShot)){

					((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

					File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);				
					FileUtils.copyFile(scrFile, new File(locationToSaveSceenShot));

					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot captured and saved at: " +locationToSaveSceenShot +" and page url is = "+driver.getCurrentUrl());
				}	
			}
		} 
		catch (Exception e) 
		{
			new HandlerLib().checkIfAlertPresent(driver);
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occured while capturing browser screenshot, now capturing whole system screen.", e);

			captureScreenShot(locationToSaveSceenShot);
		}
	}
	else {
		logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+": Not taking screenshort in case of Null driver ");
	}
	}



	/** This method will be used to take the system screen screenshot using java.robot.*
	 * 
	 * @param driver
	 * @param locationToSaveSceenShot
	 */
	public synchronized static void captureScreenShot(String locationToSaveSceenShot)
	{		
		try
		{
			String directoryForScreenShot = StringLib.splitDirectoryFromFileLocation(locationToSaveSceenShot);

			if(FileLib.CreateDirectory(directoryForScreenShot))
			{
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				Rectangle screenRectangle = new Rectangle(screenSize);

				BufferedImage image = robot.createScreenCapture(screenRectangle);
				ImageIO.write(image, "png", new File(locationToSaveSceenShot));

				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot captured and saved at: " +locationToSaveSceenShot);
			}		
		} 
		catch (Exception e) 
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occured while capturing screenshot. ", e);
		}
	}

}
