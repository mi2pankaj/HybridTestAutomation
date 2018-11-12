package poc;

import java.io.File;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import framework.utilities.FileLib;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.ProxyServer;


/**
 * @author pankaj.katiyar
 *	HOW TO INTEGRATE PROXY IN FRAMEWORK - WE NEED TO DEFINE THE DRIVER TYPE BEFORE STARTING THE TESTS, THAT WILL BE EXECUTION CONTROL SHEET --> DRIVER TYPE
 *	FURTHER NEED TO CHECK WITH RISHI, HOW ABOUT THIS STRUCTURE 
 *	
 *	{
	"browser": "iPhone 6",
	"proxy": "yes"
	}
 *
 */


@SuppressWarnings("deprecation")
public class PoC_ProxyTest_Working_v2_0 {

	@Test	
	public static void main() throws Exception {
		// TODO Auto-generated method stub

		/**** Use this 2.1 and onwards dependency ****/
		//		BrowserMobProxyServer browserMobProxy = new BrowserMobProxyServer();
		//		Set<CaptureType> set = new HashSet<>();
		//		set.add(CaptureType.REQUEST_HEADERS);
		//		browserMobProxy.setHarCaptureTypes(set);

		//		browserMobProxy.setTrustAllServers(true);
		//		browserMobProxy.setMitmManager(ImpersonatingMitmManager.builder().trustAllServers(true).build());
		//		browserMobProxy.start(8080);		
		//		browserMobProxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT, CaptureType.RESPONSE_HEADERS);

		/**** Use this upto 2.0 dependency ****/ 
		ProxyServer browserMobProxy = new ProxyServer(8080);
		//		browserMobProxy.	setTrustAllServers(true);
		browserMobProxy.start();
		browserMobProxy.setCaptureHeaders(true);
		browserMobProxy.setCaptureContent(true);

		System.out.println("Port Started On: "+browserMobProxy.getPort());
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir").concat("/tpt/drivers/chromedriver"));

		WebDriver driver = getDriver_CapProxy(browserMobProxy);
		browserMobProxy.newHar("HAR");

		driver.get("http://www.lenskart.com");
		//		driver.get("http://stackoverflow.com/");
		//		driver.get("http://google.com/");

		Thread.sleep(2000);

		driver.quit();
		browserMobProxy.stop();

		String har = System.getProperty("user.dir").concat("/har");
		browserMobProxy.getHar().writeTo(new File(har));

		getFile(har);
	}


	/**** Use this 2.1 and onwards dependency ****/
	public static WebDriver getDriver_CapProxy(BrowserMobProxyServer browserMobProxy) throws UnknownHostException
	{
		System.out.println(" 2.1 Dependency ");
		DesiredCapabilities cap = new DesiredCapabilities();
//		ChromeOptions options = new ChromeOptions();
//
//		//options.addArguments("--ignore-certificate-errors");
//		//options.addArguments("--proxy-server=http://localhost:"+browserMobProxy.getPort());
//
//		cap.setCapability(ChromeOptions.CAPABILITY, options);
		cap.setCapability(CapabilityType.PROXY, ClientUtil.createSeleniumProxy(browserMobProxy));

		WebDriver driver = new ChromeDriver(cap);

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver;
	}

	/**** Use this upto 2.0 dependency ****/ 
	public static WebDriver getDriver_CapProxy(ProxyServer browserMobProxy) throws UnknownHostException
	{
		System.out.println(" 2.0 Dependency ");

		DesiredCapabilities cap = new DesiredCapabilities();
//		ChromeOptions options = new ChromeOptions();
//
//		//options.addArguments("--ignore-certificate-errors");
//		//options.addArguments("--proxy-server=http://localhost:"+browserMobProxy.getPort());
//
//		cap.setCapability(ChromeOptions.CAPABILITY, options);
		cap.setCapability(CapabilityType.PROXY, browserMobProxy.seleniumProxy());		

		WebDriver driver = new ChromeDriver(cap);

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver;
	}


	public static void getFile(String har) throws JSONException
	{
		//		String har = "/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/har";
		StringBuilder s = FileLib.ReadContentOfFile(har);
		JSONObject json = new JSONObject(s.toString());

		JSONArray jsonArray = json.getJSONObject("log").getJSONArray("entries");

		for(int i=0; i<jsonArray.length(); i++)
		{
			JSONObject object = jsonArray.getJSONObject(i);

			JSONObject request = object.getJSONObject("request");
			JSONObject response = object.getJSONObject("response");

			if(request.getString("url").contains("api.lenskart.com"))
			{
				System.out.println(request.getString("url") + " --- " + response.getString("status"));
			}	
		}
	}

}

