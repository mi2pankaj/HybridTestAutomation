package lenskart.tests;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import framework.core.classes.WebdriverSetup;
import framework.utilities.GenericMethodsLib;
import framework.utilities.IntegerLib;

public class PageLoadTest {

	static Logger logger = Logger.getLogger(PageLoadTest.class.getName());

	static int numberOfBrowsers = Integer.parseInt(System.getProperty("number_of_browsers").toString().trim());
	static List<String> urlList = new ArrayList<>();

	@Test
	public void test() {

		try
		{
			urlList.add("https://www.lenskart.com/lenskart-gold-membership.html");
			urlList.add("https://www.lenskart.com/eyeglasses.html");
			//urlList.add("https://www.lenskart.com/sunglasses.html");
			urlList.add("https://www.lenskart.com");
			//urlList.add("https://www.lenskart.com/contact-lenses.html");
			urlList.add("https://www.lenskart.com/golden-green-full-rim-aviator-medium-size-58-vincent-chase-top-guns-vc-5158-p-c20-polarized-sunglasses.html");

			createExecutor();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void load()
	{
		try
		{
			WebDriver driver = WebdriverSetup.WebDriverSetUp("chrome", null, false);

			while(true) {

				for(int i=0; i<urlList.size(); i++) {

					try {
						if(driver == null) {
							driver = WebdriverSetup.WebDriverSetUp("chrome", null, false);
						}

						String url = urlList.get(i);

//						try {
							driver.get(url);
//						}catch (UnhandledAlertException e) {
//							driver.switchTo().alert().accept();
//						}

						logger.info("Loading url: "+url);

						int wait = IntegerLib.GetRandomNumber(2000, 1000);
						Thread.sleep(wait);

//						try {
							driver.navigate().to(url.concat("?"+String.valueOf(new Date().getTime())));
//						}catch (UnhandledAlertException e) {
//							driver.switchTo().alert().accept();
//						}

						logger.info("Re-Loading url: "+url);

						wait = IntegerLib.GetRandomNumber(4000, 1000);
						Thread.sleep(wait);

					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static Callable<Object> getTask(){

		Callable<Object> callable = new Callable<Object>() {

			@Override
			public Object call() throws Exception {

				load();

				return "LOAD TEST ";
			}
		};

		return callable;
	}


	public static void createExecutor() {

		try {

			ExecutorService executor = Executors.newFixedThreadPool(numberOfBrowsers);

			Set<Callable<Object>> tasks = new HashSet<>();

			for(int i=0; i<numberOfBrowsers; i++) {
				tasks.add(getTask());
			}

			List<Future<Object>> future = executor.invokeAll(tasks);

			while(future != null) {
				logger.info(future.toString()); 
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}

