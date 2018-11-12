package poc;


import java.io.File;
import java.io.IOException;
import java.util.Date;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PoC_Browsermob {
	public static WebDriver driver;
	public static BrowserMobProxyServer server;

	@BeforeClass
	public void setup() throws Exception {

		server = new BrowserMobProxyServer();
		server.start(0);
		int port = server.getPort();

		server.newHar("lenskart.har");

		Proxy proxy = ClientUtil.createSeleniumProxy(server);
		proxy.setHttpProxy("localhost:"+port);
		DesiredCapabilities seleniumCapabilities = new DesiredCapabilities();
		seleniumCapabilities.setCapability(CapabilityType.PROXY, proxy);
		

		if(!server.isStarted())
		{
			System.out.println("Not running ");
			Assert.fail();
		}
		
		
		System.setProperty("webdriver.chrome.driver","/Users/rishi/Documents/myWorkSpace/Lenskart_Automation/tpt/drivers/chromedriver");
		driver = new ChromeDriver(seleniumCapabilities);
		System.out.println("Port started:" + port);
	}

	@Test
	public void teknosa_test1() throws InterruptedException {

		driver.get("http://www.lenskart.com");

		Thread.sleep(5000);
	}

	@AfterClass
	public void shutdown() {
		try {

			// Get the HAR data
			Har har = server.getHar();
			File harFile = new File("/Users/rishi/Documents/myWorkSpace/Lenskart_Automation/"+new Date().getTime());
			har.writeTo(harFile);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		driver.quit();
		server.stop();
	}
}