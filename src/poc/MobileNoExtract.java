package poc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import framework.core.classes.WebdriverSetup;
import framework.googleAPI.classes.GoogleSheetAPI;


public class MobileNoExtract {

	@Test
	public void ExtractMobile() throws ParseException, InterruptedException, FileNotFoundException{
		try{
			String dir=System.getProperty("user.dir");

//			WebDriver driver=WebdriverSetup.WebDriverSetUp("Chrome", null, false);
//
//			driver.get("https://docs.google.com/spreadsheets/d/e/2PACX-1vTMmdOFdbkfGpkEIeFejrLwoTnjEhEX57KRALCaVos5OBSJksBvhJCRTRpFzsgJmNBo5VkoVoGBXY_N/pubhtml?gid=1099224828&single=true");
//
//			Thread.sleep(3000);
//			WebElement emailAddress=driver.findElement(By.xpath(".//input[@type='email']"));
//			emailAddress.sendKeys("mobileqa@valyoo.in");
//			
//			driver.findElement(By.xpath(".//div[@id='identifierNext']")).click();
//			Thread.sleep(4000);
//			
//			WebElement password=driver.findElement(By.xpath(".//input[@name='password']"));
//			password.sendKeys("webqa@123");
//			
//			driver.findElement(By.xpath(".//div[@id='passwordNext']")).click();
//			Thread.sleep(3000);
//			
//			System.setErr(new PrintStream(new File(dir+"/roster.properties")));
//			
//			String phoneNo=driver.findElement(By.xpath("//td[text()='ROSTER']/preceding-sibling::td/following-sibling::td")).getText();
			String number="";
			try{
				
				String sheetId ="1Wnd6-7bCBzmjlTr2ldWck_ekP1A37wH30TcyPhYyKwE";
				String sheetRange="A:C";
				
				List<List<Object>> record=new GoogleSheetAPI().getSpreadSheetRecords(sheetId, sheetRange);
				System.out.println("record of the roster sheet is : "+record);
				for(int i=0;i<record.size();i++){
					List<Object>values=record.get(i);
					int size=values.size();
					if(size>2){
				     number=values.get(1).toString();
				    System.out.println("phone number:"+number);
				   System.out.println("Roaster  :"+values.get(2)); 
				    
					}
				}
				}catch(Exception e){
					
				}
			System.err.println("guest_mobile_no = "+number);
//			driver.close();
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}


}
