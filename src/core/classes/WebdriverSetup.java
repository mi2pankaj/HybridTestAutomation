package core.classes;

import java.io.File;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import core.utilities.FileLib;
import core.utilities.GenericUtils;
import core.utilities.httpClientWrap;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import net.lightbody.bmp.proxy.ProxyServer;
import tests.SuiteClass;

public class WebdriverSetup {


	static Logger logger = Logger.getLogger(GenericUtils.class.getName());

	/**
	 *  start appium server and create appium driver, 
	 *  create an emulator first -- not creating during test - rather being created in singletionclass initialization - so that no failure because of creation
	 *  different regex to find out emulator port from appiumLog file
	 *  
	 *  (?<=adb.exe\s-P\s5037\s-s\s).*(?=\sshell\sgetprop\sinit.svc.bootanim)
		(?<=adb.exe\s-P\s5037\s-s\s)(.*?)(?=\sshell\sgetprop\sinit.svc.bootanim)
		(?<=\s-s\s)(.*?)(?=\sshell\sgetprop\sinit.svc.bootanim)
	 *  
	 * @return
	 */
	public synchronized AndroidDriver<MobileElement> appiumEmulation() {

		AndroidDriver<MobileElement>  driver = null;

		try {
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : " +" entering method to setup appium emulation .. ");

			/** get a unique device name */
			String deviceName = SuiteClass.UNIQ_EXECUTION_ID.get().toString();

			/** setup local service first */
			AppiumDriverLocalService appiumDriverLocalService = getAppiumDriverLocalService();

			/** logic is - first local driver serivce and then hit the remote url and see if there is empty response if yes then stop earlier instance
			 * and create a new one. once this is up, create driver, max attempt = 10 */
			int attempt = 1;
			while(httpClientWrap.sendGetRequest(appiumDriverLocalService.getUrl().toString()).isEmpty() && attempt < 10) {

				appiumDriverLocalService.stop();
				Thread.sleep(1000);
				appiumDriverLocalService = getAppiumDriverLocalService();

				attempt ++;
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " restarting appium driver local service - attempt: "+attempt);
			}

			/** get appium driver capability */
			DesiredCapabilities desirecap = getAppiumDriverCapabilities(deviceName);

			/** start driver on remote url.  */
			driver = recreateAppiumDriver(appiumDriverLocalService, desirecap);

			/** setup appium driver -- implicit wait is not set up by appium driver -- setting up directly 
			 * with chrome driver server by sending post request. -- not commenting this line - coz this code will log the request in appium log */			
			driver.manage().timeouts().implicitlyWait(
					Integer.parseInt(GenericUtils.generalConfigurationProperties.getProperty("implicitDelayForWebdriver").toString()), 
					TimeUnit.SECONDS);
			setupTimeout_ChromedriverLaunchedByAppiumServer();

			/** store appium driver properties in singleton */
			storeAppiumDriverPropertiesInSingletonObject(driver);		

			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : appium driver is launched ... setting up testcase object : "+driver.getRemoteAddress());
		}catch(Exception e) {

			/** in case of any exception kill respective emulator - to avoid any hanging or test execution stuck issues .., normally testexecution id is emulator name..  */
			new GenericUtils().killEmulator(SuiteClass.UNIQ_EXECUTION_ID.get().toString());	//--need to supply device id here not the name -- correction required

			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+": exception occurred while starting the Appium Server. ",e);
		}

		return driver;
	}

	/**
	 * set up implicit timeout with chrome driver.
	 */
	public synchronized void setupTimeout_ChromedriverLaunchedByAppiumServer() {

		try {

			String chromedriverURL = getChromeDriverURLFromAppiumLogFile()+"/timeouts";
			int timeout = Integer.parseInt(GenericUtils.generalConfigurationProperties.getProperty("implicitDelayForWebdriver").toString());
			String postData = "{\"type\":\"implicit\",\"ms\":"+timeout*1000+"}";

			HashMap<Object, Object> response = new httpClientWrap().sendPostRequestWithParams(chromedriverURL, postData);

			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+": " + " response map from chromedriver timeout setup: "+response + " and url: "+chromedriverURL);
		}catch (Exception e) {
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+": " + " No Impact - error occurred while setting up timeout with chromedriver. ", e);
		}
	}


	/**
	 * storing values in singleton class.
	 * @param driver
	 */
	public synchronized void storeAppiumDriverPropertiesInSingletonObject(AppiumDriver<MobileElement> driver) {

		try
		{
			if(driver!=null) {
				/** store appium driver url in test case object */
				SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().get(SuiteClass.UNIQ_EXECUTION_ID.get()).setAppiumDriverURL(driver.getRemoteAddress().toString());

				/** set device id, session id in test case object */
				String deviceUDID = driver.getCapabilities().asMap().get("deviceUDID").toString();
				SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().get(SuiteClass.UNIQ_EXECUTION_ID.get()).setDeviceUDID(deviceUDID);
				SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().get(SuiteClass.UNIQ_EXECUTION_ID.get()).setAppiumDriverSessionId(driver.getSessionId());

				/** store chrome driver url */
				String chromerdriverURL = getChromeDriverURLFromAppiumLogFile();
				SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().get(SuiteClass.UNIQ_EXECUTION_ID.get()).setChromeDriverURLLaunchedByAppiumDriver(chromerdriverURL);
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : stored values in singleton: device - "+deviceUDID + " chrome driver: "+chromerdriverURL + " appium driver: "+driver.getRemoteAddress());
			}else {
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : not stored values in singleton: device - null appium driver received. ");
			}
		}catch (Exception e) {
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " error while storing values in sigleton: "+e.getMessage(), e);
		}
	}


	/** retry to create appium remote driver for atleast 3 attempts -- 
	 * 
	 * @param appiumDriverLocalService
	 * @param desirecap
	 * @return
	 */
	public AndroidDriver<MobileElement> recreateAppiumDriver(AppiumDriverLocalService appiumDriverLocalService, DesiredCapabilities desirecap) {

		AndroidDriver<MobileElement>  driver = null;

		int count =1;

		while(count <= 3) {

			driver = createAppiumDriver(appiumDriverLocalService, desirecap);
			if(driver  != null) {
				break;
			}

			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : Attempt ReCreate Appium Driver : " +count);
			count ++;
		}


		return driver;
	}


	/** create appium remote driver 
	 * 
	 * @param appiumDriverLocalService
	 * @param desirecap
	 * @return
	 */
	public AndroidDriver<MobileElement> createAppiumDriver(AppiumDriverLocalService appiumDriverLocalService, DesiredCapabilities desirecap) {

		AndroidDriver<MobileElement>  driver = null;

		/** start driver on remote url.  */
		try{
			driver =new AndroidDriver<MobileElement>(appiumDriverLocalService.getUrl(), desirecap);
		}catch (Exception e) {

			try {
				/** in case of exception, first find the emulator name from apiumLog file and then kill emulator then try to start driver. */
				String emulatorName = getEmulatorNameFromAppiumLogFile();
				new GenericUtils().killEmulator(emulatorName);

				driver =new AndroidDriver<MobileElement>(appiumDriverLocalService.getUrl(), desirecap);
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " - retried driver, after killing the - "+emulatorName);

			}catch (Exception e1) {
				logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " error while re-creating driver - "+e1.getMessage(), e1);
			}
		}

		return driver;
	}


	/**
	 *  get emulator name from appium log file using regex. 
	 * @return
	 */
	public String getEmulatorNameFromAppiumLogFile() {

		String emulatorName = "";

		try {
			/** appium log name is generic and set in builder setup method */
			StringBuilder content = FileLib.ReadContentOfFile(System.getProperty("user.dir")+"/appiumLog."+SuiteClass.UNIQ_EXECUTION_ID.get());
			Pattern pattern = Pattern.compile("(?<=\\s-s\\s)(.*?)(?=\\sshell\\sgetprop\\sinit.svc.bootanim)");
			Matcher matcher = pattern.matcher(content);

			if(matcher.find()) {
				emulatorName = matcher.group().trim();
			}

		}catch (Exception e) {
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " : error while getting emulator name: "+e.getMessage(), e);
		}

		logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " Got emulator name: "+emulatorName);

		return emulatorName;
	}



	/**
	 *  get chromedriver url from appium log file using regex:
	 *  (?<=timeouts\]\sto\s\[POST\s)(.*)(?=\]\swith\sbody:\s\{\"implicit\":)
	 *  
	 * @return
	 */
	public synchronized String getChromeDriverURLFromAppiumLogFile() {

		String chromeDriverURL = "";

		try {
			/** appium log name is generic and set in builder setup method */
			StringBuilder content = FileLib.ReadContentOfFile(System.getProperty("user.dir")+"/appiumLog."+SuiteClass.UNIQ_EXECUTION_ID.get());

			/** this regex will give the chromedriver url to set the timeout */
			Pattern pattern = Pattern.compile("(?<=timeouts\\]\\sto\\s\\[POST\\s)(.*)(?=\\]\\swith\\sbody:\\s\\{\\\"implicit\\\":)");
			Matcher matcher = pattern.matcher(content);

			if(matcher.find()) {
				chromeDriverURL = matcher.group().trim();
			}

			if(!chromeDriverURL.isEmpty()) {
				chromeDriverURL = chromeDriverURL.replace("/timeouts", "").trim(); // to get the actual chromedriver url
			}
		}catch (Exception e) {
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " : error while getting chrome driver url: "+e.getMessage(), e);
		}

		logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " Got chrome driver url: "+chromeDriverURL);

		return chromeDriverURL.trim();
	}


	/** apply received browser dimension
	 * 
	 * @param driver
	 * @param browserDimensionWidth
	 * @param browserDimensionHeight
	 * @return
	 */
	public static WebDriver applyBrowserDimension(WebDriver driver, String browserDimensionWidth, String browserDimensionHeight)
	{
		Dimension dimension = null;
		try

		{
			if(!browserDimensionHeight.trim().isEmpty() && !browserDimensionWidth.trim().isEmpty()) {
				dimension = new Dimension(Integer.parseInt(browserDimensionWidth), Integer.parseInt(browserDimensionHeight));				
			}else {

				if(driver instanceof RemoteWebDriver) {
					dimension =new Dimension(375, 667);

				}else if(driver instanceof FirefoxDriver) {
					dimension =new Dimension(375, 667);
				}
			}

			driver.manage().window().setSize(dimension);
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : applying browser dimension : "+browserDimensionWidth +"x"+ browserDimensionHeight);

		}catch (Exception e) {
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " : "+ e.getMessage(), e);
		}
		return driver;
	}


	/** get desired capabilities - for appium driver
	 * 
	 * @param deviceName
	 * @return
	 */
	public static DesiredCapabilities getAppiumDriverCapabilities(String deviceName)
	{
		logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " setting up appium capabilities ");

		DesiredCapabilities desirecap= DesiredCapabilities.android();
		desirecap.setCapability(AndroidMobileCapabilityType.BROWSER_NAME,BrowserType.CHROME);
		desirecap.setCapability(MobileCapabilityType.DEVICE_NAME,deviceName);
		desirecap.setCapability("avd",deviceName);
		desirecap.setCapability("avdLaunchTimeout", 600000); //600 sec == 10 min
		desirecap.setCapability("avdReadyTimeout", 600000); //600 sec == 10 min

		desirecap.setCapability("autoGrantPerrmisions", true);

		/** trying with appium reset capability to install the desired chrome version coz default chrome version is v55 */
		desirecap.setCapability(MobileCapabilityType.FULL_RESET, true);
		desirecap.setCapability(MobileCapabilityType.APP, System.getProperty("user.dir")+"/drivers/com.android.chrome_v.67.0.apk");

		desirecap.setCapability("chromedriverExecutableDir", SuiteClass.AUTOMATION_HOME.concat("/drivers"));
		desirecap.setCapability(MobileCapabilityType.SUPPORTS_JAVASCRIPT, true);

		desirecap.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 300);
		desirecap.setCapability("newCommandTimeout", 600); // 600 sec == 10 min

		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		desirecap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

		/** disable notification */
		List<String> args = new ArrayList<>();
		//		args.add("--disable-network-portal-notification");
		//		args.add("disable-infobars");
		//		args.add("--disable-notifications");
		args.add("--incognito");

		ChromeOptions options = new ChromeOptions();
		options.addArguments(args);
		desirecap.setCapability(ChromeOptions.CAPABILITY, options);

		logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " appium capabilities are being returned... "+desirecap);

		return desirecap;
	}

	/** get a service started - with one retry
	 * 
	 * @return
	 */
	public static AppiumDriverLocalService getAppiumDriverLocalService() {

		AppiumDriverLocalService service = null;

		try {
			service = getAppiumServiceBuilder().build();

			try {
				service.start();
			}catch (AppiumServerHasNotBeenStartedLocallyException e) {

				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : Appium Driver Not Started, Retrying :  " + service.getUrl());
				service = getAppiumServiceBuilder().build();
				service.start();
			}

			/** putting up some delay so that service is up */
			Thread.sleep(2500);
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : Appium Driver URL:  " + service.getUrl());

		}catch (Exception e) {
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " : " + e.getMessage(), e);
		}

		return service;
	}

	/** create appium service builder 
	 * 
	 * @return
	 */
	public static AppiumServiceBuilder getAppiumServiceBuilder() {

		AppiumServiceBuilder builder = new AppiumServiceBuilder();

		try
		{
			/** start appium server on any received free port  */
			builder.usingPort(getFreePort());
			builder.withLogFile(new File(System.getProperty("user.dir")+"/appiumLog."+SuiteClass.UNIQ_EXECUTION_ID.get()));


			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " : "+ " builder is created. ");
		}catch (Exception e) {
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " : " +e.getMessage(), e);
		}

		return builder;
	}

	/** Get chromedriver service instance.
	 * 
	 * @param chromeDriver
	 * @return
	 */
	public static ChromeDriverService getChromeDriverService(String chromeDriver)
	{
		ChromeDriverService service = null;
		try
		{
			service = new ChromeDriverService.Builder()
					.usingDriverExecutable(new File(chromeDriver))
					.usingAnyFreePort()
					.build();
			service.start();

			Thread.sleep(1000);

		}catch(Exception io){
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while starting the chrome driver service: "+ io);
		}

		return service;
	}

	/**
	 * Method is to get the device type from the testcaseobject.
	 * @return
	 */
	public static String getDeviceName(String browserInfoJson){
		String deviceType=null;
		JSONObject jsonObject=null;
		try{
			jsonObject=new JSONObject(browserInfoJson);
			deviceType=jsonObject.getString("browser");
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Device type is :"+deviceType);
		}catch(Exception e){
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to get device type", e);
		}
		return deviceType;
	}

	/** get a free port 
	 * 
	 * @return
	 */
	public synchronized static int getFreePort(){

		int port=0;

		try
		{
			/** get a free port */
			ServerSocket socket = new ServerSocket(0);
			port = socket.getLocalPort();
			socket.close();
		}catch (Exception e) {
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() +  " : "+e.getMessage(), e);
		}

		return port;
	}

	/** this is used to emulate the mobile browser on desktop 
	 * 
	 * @param deviceType
	 * @return
	 */
	public static WebDriver invokeBrowser(String browserInfoJson, ProxyServer proxyServer){

		WebDriver driver =null;

		String browserDimensionWidth = "";
		String browserDimensionHeight = "";
		String appiumRun="";

		try{
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " entering invoke browser method --");

			boolean normalMode = false;
			JSONObject jsonObject = new JSONObject(browserInfoJson);

			String deviceType = jsonObject.get("deviceType").toString().trim();
			String browser  = jsonObject.getString("browser").toString().trim();

			/** get browser dimensions in pixels */
			try{ browserDimensionWidth = jsonObject.getString("browserDimensionWidth").toString().trim();}catch (JSONException e) {}
			try{ browserDimensionHeight = jsonObject.getString("browserDimensionHeight").toString().trim();}catch (JSONException e) {}
			try {appiumRun=jsonObject.getString("appiumRun").toString().trim();}catch (JSONException e) {}

			try {
				boolean handleNotificationForAppiumRequired = Boolean.parseBoolean(jsonObject.getString("handleNotificationForAppiumRequired").toString().trim());
				SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().get(SuiteClass.UNIQ_EXECUTION_ID.get())
				.setHandleNotificationForAppiumRequired(handleNotificationForAppiumRequired);

			}catch (JSONException e) {
				/** in case of no specification then consider it true. */
				SingletonTestObject.getSingletonTestObject().getTestCaseObjectMap().get(SuiteClass.UNIQ_EXECUTION_ID.get()).setHandleNotificationForAppiumRequired(true);
			}

			/** find out on which mode the browser will run */
			try{
				if(jsonObject.get("normalMode").toString().equalsIgnoreCase("yes")){
					normalMode = true;
				}else{
					normalMode = false;
				}
			}catch (JSONException e) {normalMode = false;}

			if(deviceType.equalsIgnoreCase("Desktop") || deviceType.equalsIgnoreCase("")){

				driver=WebDriverSetUp(browser ,proxyServer, normalMode);
			}else{

				driver=mobileEmulation(browser, deviceType, proxyServer, normalMode, browserDimensionWidth, browserDimensionHeight,appiumRun);
			}
		}catch(Exception e){
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to invoke browser", e);
		}

		return driver;
	}

	/**
	 * This method  will invoke set chrome mobile emulation
	 * @param objectName 
	 * @param data 
	 * @return chrome@@mobile@@Apple Iph 
	 */
	public static WebDriver mobileEmulation(String browser, String deviceName, ProxyServer proxyServer, boolean normalMode, 
			String browserDimensionWidth, String browserDimensionHeight, String appiumRun){

		WebDriver driver=null;
		String chromeDriver = null;
		System.out.println("os name is "+System.getProperty("os.name"));
		try {

			if(appiumRun.trim().equalsIgnoreCase("Yes")) {

				driver=new WebdriverSetup().appiumEmulation();	
			}
			else {

				try{
					if(browser.equalsIgnoreCase("chrome")){

						if(System.getProperty("os.name").matches("^Windows.*")){
							chromeDriver = SuiteClass.AUTOMATION_HOME.concat("/drivers/windows/chromedriver.exe");
						}else if (System.getProperty("os.name").matches("Linux.*")){
							chromeDriver = SuiteClass.AUTOMATION_HOME.concat("/drivers/linux/chromedriver");
						}else {
							chromeDriver = SuiteClass.AUTOMATION_HOME.concat("/drivers/mac/chromedriver");
						}
						/** create chrome driver service */
						ChromeDriverService service = retryChromeDriverService(chromeDriver);

						if(service != null && service.isRunning()){
							HashMap<String, String> mobileEmulation = new HashMap<String, String>();
							mobileEmulation.put("deviceName", deviceName);

							HashMap<String, Object> chromeOptions=new HashMap<String, Object>();
							chromeOptions.put("mobileEmulation", mobileEmulation);

							ChromeOptions options = new ChromeOptions();
							//options.addArguments("--kiosk");

							options.addArguments("disable-infobars");
							options.addArguments("--disable-notifications");
							if(!normalMode){
								options.addArguments("incognito");
							}

							DesiredCapabilities capabilities = DesiredCapabilities.chrome();
							capabilities.setCapability(ChromeOptions.CAPABILITY, options);
							capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

							if(proxyServer!=null){
								capabilities.setCapability(CapabilityType.PROXY, proxyServer.seleniumProxy());
							}

							/** enable logging */
							LoggingPreferences logPrefs = new LoggingPreferences();
							logPrefs.enable(LogType.BROWSER, Level.ALL);
							capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

							try{
								driver = new RemoteWebDriver(service.getUrl(), capabilities);
							}catch (SessionNotCreatedException e) 
							{
								/** if session is not created successfully then re-try to create it. Calling recursion */
								logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver session not setup, retrying ... ");

								driver = mobileEmulation(browser, deviceName, proxyServer, normalMode, browserDimensionWidth, browserDimensionHeight,appiumRun);
							}
						}else{
							logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver service seems not started while setting up driver ... ");
						}

					}else if(browser.equalsIgnoreCase("Firefox")){

						logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Firefox mobile emulation is being setup");

						String firfoxDriverPath;
						if(System.getProperty("os.name").toLowerCase().startsWith("windows")) {
							firfoxDriverPath =SuiteClass.AUTOMATION_HOME.concat("/drivers/windows/geckodriver.exe");
						}else if(System.getProperty("os.name").toLowerCase().startsWith("linux")){
							firfoxDriverPath =SuiteClass.AUTOMATION_HOME.concat("/drivers/linux/geckodriver");

						}else{
							firfoxDriverPath =SuiteClass.AUTOMATION_HOME.concat("/drivers/mac/geckodriver");
						}

						System.setProperty("webdriver.gecko.driver",firfoxDriverPath);
						String user_agent = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_2_1 like Mac OS X) AppleWebKit/602.4.6 (KHTML, like Gecko) Version/10.0 Mobile/14D27 Safari/602.1";		
						FirefoxOptions options = new FirefoxOptions();
						FirefoxProfile profile = new FirefoxProfile();
						profile.setPreference("general.useragent.override", user_agent);	

						/** if nonrmal mode - no, then open incognito mode*/
						if(!normalMode) {
							profile.setPreference("browser.private.browsing.autostart",true);
							logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " - "+ " FF incognito mode. ");
						}

						//for windows,,, 
						profile.setPreference("security.sandbox.content.level", 5);

						/** custom profile */
						options.setProfile(profile);

						if(proxyServer!=null) {
							options.setCapability(CapabilityType.PROXY, proxyServer.seleniumProxy());
						}

						try {
							driver=new FirefoxDriver(options);
						}catch (Exception e) {
							logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " - " +" retrying to create the firefix driver .. , received exception was- "+e.getMessage(), e);
							driver=new FirefoxDriver(options);
						}
					}

					/** apply browser dimension  */
					driver = applyBrowserDimension(driver, browserDimensionWidth, browserDimensionHeight);
					int implicitDelayForWebdriver = Integer.parseInt(GenericUtils.generalConfigurationProperties.getProperty("implicitDelayForWebdriver").toString());

					/** setting up implicit driver delay */
					driver.manage().timeouts().implicitlyWait(implicitDelayForWebdriver, TimeUnit.SECONDS);

				}catch(Exception e){
					logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Error while creating mobile browser emulation. ", e);
				}
			}
		}catch(Exception e) {
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+": exception occurred while starting the mobile emmulator: ",e);

		}
		return driver;
	}

	/** This method will attempt to start chrome driver service, earler we were using recursion for retry that may result in
	 * infinite loops, now limiting max attempts to 10.
	 * 
	 * @param chromeDriver
	 * @return
	 */
	public static ChromeDriverService retryChromeDriverService(String chromeDriver) 
	{
		ChromeDriverService service = null;

		int i = 0;
		while(i <= 10)
		{
			if(service != null)
			{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver service is started yet, attempt: "+i);
				break;
			}
			else
			{
				service = getChromeDriverService(chromeDriver);
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver service is not started yet, attempt: "+i);
			}

			i++;
		}

		/** wait for chrome driver to start */ 
		if(service != null)
		{
			waitForChromeDriverToStart(service);
		}

		return service;
	}

	/**
	 * Method is to start proxy server to capture network traffic.
	 * @return
	 */
	@SuppressWarnings({ "deprecation" })
	public ProxyServer startProxyServer(String browerInfoJson){

		ProxyServer proxyServer=null;
		String isProxy=null;
		try{
			isProxy=getProxyInfo(browerInfoJson);
			if(isProxy.equalsIgnoreCase("yes")){

				proxyServer=new ProxyServer();
				proxyServer.start();
				proxyServer.setCaptureHeaders(true);
				proxyServer.setCaptureContent(true);
				proxyServer.newHar("requests_"+new Date().getTime());

				/** check if there is any proxy property file with name proxy.properties is found in properties folder of AUTOMATION_HOME then use it else
				 * use the supplied params */
				PropertiesConfiguration proxyProperties = getProxyProperties();
				if(proxyProperties !=null) {

					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " found proxy property - laoding domains ..");

					/** get all domains */
					Iterator<String> domains = proxyProperties.getKeys();

					while (domains.hasNext()) {

						/** get all domain and ip address from config */
						String domain = domains.next();
						String ipAddress = proxyProperties.getProperty(domain).toString().trim();

						/** remap proxy server with received address */
						proxyServer.remapHost(domain, ipAddress);
					}

				}else {

					/** creating two variables - one for lenskart.com and another one for api.lenskart.com */
					String serverIP_Lenskart=System.getProperty("host_ip_www");
					String serverIP_Api=System.getProperty("host_ip_api");

					/** setting up proxy for api-preprod.com also if current test env is preprod and there is no ip address is received for api.lenskart.com so that 
					 * all api requests go to live */
					if((SuiteClass.currentTestEnv.equalsIgnoreCase("preprod")) && (serverIP_Api == null || serverIP_Api.trim().isEmpty()))
					{
						serverIP_Api = SuiteClass.liveIpAddress_Lenskart_Com;
					}

					/** map server ip if passed as not null - separating condition so that atleast one can be applied - */
					if(serverIP_Lenskart !=null && !serverIP_Lenskart.trim().isEmpty())
					{
						proxyServer.remapHost("www.lenskart.com", serverIP_Lenskart);
						proxyServer.remapHost("lenskart.com", serverIP_Lenskart);
						logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : host entry is set in proxy server for lenskart.com ="+serverIP_Lenskart);
					}

					/** map server ip if passed as not null - separating condition so that atleast one condition can be applied - */
					if(serverIP_Api != null && !serverIP_Api.trim().isEmpty())
					{
						proxyServer.remapHost("api.lenskart.com", serverIP_Api);
						logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : host entry is set in proxy server for api ="+serverIP_Api);
					}

					if((SuiteClass.currentTestEnv.equalsIgnoreCase("preprod")) && (serverIP_Api == null || serverIP_Api.trim().isEmpty()))
					{
						proxyServer.remapHost("api-preprod.lenskart.com", SuiteClass.liveIpAddress_Lenskart_Com);
						proxyServer.remapHost("api.lenskart.com", SuiteClass.liveIpAddress_Lenskart_Com);

						logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : host entry is set in proxy server for api and api-preprod ="+SuiteClass.liveIpAddress_Lenskart_Com);
					}
				}

				/** block optimizely if passed from maven -- any flag */
				String block_optimizely = "";
				try{block_optimizely=System.getProperty("block_optimizely").trim();}catch (NullPointerException e) {}

				if(block_optimizely != null && !block_optimizely.isEmpty() && block_optimizely.equalsIgnoreCase("Yes")) {
					proxyServer.remapHost("www.googletagmanager.com", "127.0.0.1");
					proxyServer.remapHost("cdn.optimizely.com", "127.0.0.1");
					proxyServer.remapHost("secure.livechatinc.com", "127.0.0.1");
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : " + " blocking optimizely .. ");
				}else {
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : " + " Not blocking optimizely .. ");
				}

				/** block live chat */
				proxyServer.remapHost("secure.livechatinc.com", "127.0.0.1");

				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Port Started On:"+proxyServer.getPort());
			}else{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : No need to set proxy server:");
			}
		}catch(Exception e){
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to start proxy server", e);
		}
		return proxyServer;
	}


	/** load proxy property file if defined.  
	 * 
	 * @return
	 */
	public PropertiesConfiguration getProxyProperties() {

		PropertiesConfiguration proxyProperties = null;
		try {

			File proxyConfigFile = new File(SuiteClass.AUTOMATION_HOME.concat("/properties/proxy.properties"));

			/** read file - only if it is not empty */
			if(FileLib.ReadContentOfFile(proxyConfigFile.getAbsolutePath()).toString().isEmpty()) {
				return proxyProperties;
			}

			proxyProperties = new PropertiesConfiguration(proxyConfigFile);
		}catch (Exception e) {
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get() + " - "+e.getMessage(), e);
		}

		return proxyProperties;
	}



	/** This method waits for chrome driver to start, earlier we were putting infinite loop for wait, now limiting 10 attempts.
	 * 
	 * @param service
	 */
	public static void waitForChromeDriverToStart(ChromeDriverService service)
	{
		int i = 0;

		/** wait until chrome driver server is started -- maximum 10 attempts */
		while(i <= 10)
		{
			String output = httpClientWrap.sendGetRequest((service.getUrl().toString()));
			if(output.isEmpty())
			{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver is not started yet, attempt: "+i);
				try {Thread.sleep(1000);} catch (InterruptedException e) {}
			}
			else
			{
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver is started, exiting loop at attempt: "+i);
				try {Thread.sleep(1000);} catch (InterruptedException e) {}
				break;
			}

			i++;
		}
	}

	/***
	 * This method initialize the webdriver based on supplied browser type. New Way implemented for Chrome Driver:
	 * Now we'll start the chrome server and then wait until server is started and then create a remote driver.
	 * @param browser
	 * @param capabilities
	 * @return
	 */

	public static WebDriver WebDriverSetUp (String browser, ProxyServer proxyServer, boolean normalMode) 
	{

		WebDriver driver = null;
		try
		{
			if(browser.equalsIgnoreCase("FireFox"))
			{
				String firfoxDriverPath;
				FirefoxOptions options=new FirefoxOptions();

				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Firefox is being setup");

				if(System.getProperty("os.name").toLowerCase().startsWith("window")) {
					firfoxDriverPath =SuiteClass.AUTOMATION_HOME.concat("/drivers/windows/geckodriver.exe");
				}else if(System.getProperty("os.name").toLowerCase().startsWith("linux")){
					firfoxDriverPath =SuiteClass.AUTOMATION_HOME.concat("/drivers/linux/geckodriver");
				}
				else {
					firfoxDriverPath =SuiteClass.AUTOMATION_HOME.concat("/drivers/mac/geckodriver");
				}

				System.setProperty("webdriver.gecko.driver",firfoxDriverPath);

				/** when ever normal mode is false then it launch  the firefox in Private mode */
				if(!normalMode) {
					options.addArguments("-private");
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " - " + " starting browser in Private mode. ");
				}
				/** setting the browser proxy for firefox */
				if(proxyServer!=null) {
					options.setCapability(CapabilityType.PROXY, proxyServer.seleniumProxy());
				}

				try {
					driver = new FirefoxDriver(options);
				}catch (WebDriverException e) {
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " - retrying to create driver ==> ");
					driver = new FirefoxDriver(options);
				}

			}
			else if (browser.equalsIgnoreCase("Chrome")) 
			{
				String chromeDriver = null;
				if(System.getProperty("os.name").matches("^Windows.*"))
				{
					chromeDriver = SuiteClass.AUTOMATION_HOME.concat("/drivers/windows/chromedriver.exe");
				}
				else if(System.getProperty("os.name").matches("Linux.*")){
					chromeDriver = SuiteClass.AUTOMATION_HOME.concat("/drivers/linux/chromedriver");
				}
				else
				{
					chromeDriver = SuiteClass.AUTOMATION_HOME.concat("/drivers/mac/chromedriver");
				}

				/** create chrome driver service */
				ChromeDriverService service = retryChromeDriverService(chromeDriver);				

				if(service != null && service.isRunning())
				{
					DesiredCapabilities cap = DesiredCapabilities.chrome();
					ChromeOptions options = new ChromeOptions();

					options.addArguments("disable-infobars");
					options.addArguments("--disable-notifications");

					if(!normalMode){
						options.addArguments("incognito");
						logger.info(SuiteClass.UNIQ_EXECUTION_ID.get() + " - " + " starting browser in incognito mode. ");
					}

					/** disable notification */
					Map<String, Object> xyz = new HashMap<String, Object>();
					xyz.put("profile.default_content_setting_values.notifications", 2);
					options.setExperimentalOption("prefs", xyz);

					cap.setCapability(ChromeOptions.CAPABILITY, options);

					if(proxyServer != null) {
						cap.setCapability(CapabilityType.PROXY, proxyServer.seleniumProxy());
					}

					/** enable logging */
					LoggingPreferences logPrefs = new LoggingPreferences();
					logPrefs.enable(LogType.BROWSER, Level.ALL);
					cap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

					try{
						driver = new RemoteWebDriver(service.getUrl(), cap);
					}catch (SessionNotCreatedException e) 
					{
						/** if session is not created successfully then re-try to create it. Calling recursion */
						logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver session not setup, retrying ... ");

						driver = WebDriverSetUp(browser, proxyServer, normalMode);
					}
					catch (WebDriverException e) 
					{
						/** if session is not created successfully then re-try to create it. Calling recursion */
						logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver session not setup coz of webdriver exception, retrying ... ");

						driver = WebDriverSetUp(browser, proxyServer, normalMode);
					}
				}
				else
				{
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver service seems not started while setting up driver ... ");
				}

				/** browsing google.com to check if driver is launched successfully */
				try{driver.get("http://www.google.com");}catch(NoSuchWindowException n)
				{
					logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome browser was closed coz of unknown reason, retrying ... ");

					driver = WebDriverSetUp(browser, proxyServer, normalMode);
				}
			}
			else 
			{	
				logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : No Support For: "+browser +" Browser. ");
			}			

			int implicitDelayForWebdriver = Integer.parseInt(GenericUtils.generalConfigurationProperties.getProperty("implicitDelayForWebdriver").toString());

			/** setting up implicit driver delay */
			driver.manage().timeouts().implicitlyWait(implicitDelayForWebdriver, TimeUnit.SECONDS);
			//driver.manage().deleteAllCookies();

			/** no action needed here, just catching exception */
			try {
				/** maximize according to os */
				if(System.getProperty("os.name").matches("^Windows.*")){
					driver.manage().window().maximize();
				}else{
					driver.manage().window().fullscreen();
				}
			}catch (Exception e) {}
		}
		catch (Exception e)
		{
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while setting up browser: " + browser, e);
		} 

		return driver;
	}

	/**
	 * Method is to get the device type from the testcaseobject.
	 * @return
	 */
	public static String getProxyInfo(String browserInfoJson){
		String isProxy=null;
		JSONObject jsonObject=null;
		try{
			jsonObject=new JSONObject(browserInfoJson);
			isProxy=jsonObject.getString("proxy");
			logger.info(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Proxy is :"+isProxy);
		}catch(Exception e){
			logger.error(SuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to get Proxy info", e);
		}
		return isProxy;
	}

}
