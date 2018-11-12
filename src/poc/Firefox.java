package poc;


import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import lenskart.tests.TestSuiteClass;

public class Firefox {


	public static void main(String args[]) { 

		run();
	}

	public static void run() {

		try
		{
			String firfoxDriverPath;
			if(System.getProperty("os.name").toLowerCase().startsWith("windows")) {
				firfoxDriverPath =System.getProperty("user.dir").concat("/tpt/drivers/windows/geckodriver.exe");
			}else if(System.getProperty("os.name").toLowerCase().startsWith("linux")){
				firfoxDriverPath =System.getProperty("user.dir").concat("/tpt/drivers/linux/geckodriver");

			}else{
				firfoxDriverPath =System.getProperty("user.dir").concat("/tpt/drivers/mac/geckodriver");
			}

			System.out.println("Dirver Path - "+firfoxDriverPath);
			System.setProperty("webdriver.gecko.driver",firfoxDriverPath);
			
			String user_agent = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_2_1 like Mac OS X) AppleWebKit/602.4.6 (KHTML, like Gecko) Version/10.0 Mobile/14D27 Safari/602.1";		

			FirefoxProfile profile = new FirefoxProfile();
			profile.setPreference("general.useragent.override", user_agent);		

			DesiredCapabilities cap = DesiredCapabilities.firefox();
			cap.setCapability(FirefoxDriver.PROFILE, profile);

			WebDriver driver=new FirefoxDriver(cap);
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

			By bylocator = By.xpath("(//a[@title='Lenskart'])[1]");

			System.out.println(bylocator.toString()); 

			bylocator = By.className("a[@title='Lenskart'])");

			System.out.println(bylocator.toString());

			Dimension dimension = new Dimension(414, 736);

			driver.manage().window().setSize(dimension); 

			driver.get("https://lenskart.com");

			String js = "function replacespacedata(){var s='abc bb'; b = s.replace(\" \",\"-\"); return b;}; return replacespacedata();";

			String onbj = String.valueOf(((JavascriptExecutor)driver).executeScript(js));
			System.out.println(onbj);

			driver.close();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}


}
