package com.nf.mobile.web.hybrid.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
//import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;
//import java.util.Properties;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.nf.mobile.web.hybrid.util.Constants;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.appium.java_client.MobileBy;
import io.appium.java_client.TouchAction;
//import io.appium.java_client.MobileElement;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.remote.MobileCapabilityType;
//import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
//import org.apache.commons.codec.binary.Base64;

public class GenericKeywords<AndroidElement> {

	Properties prop;
	AndroidDriver aDriver = null;
	ExtentTest test;
	AppiumDriverLocalService service = AppiumDriverLocalService.buildDefaultService();
	DesiredCapabilities capabilities = new DesiredCapabilities();

	public GenericKeywords(ExtentTest test) {
		this.test=test;
	}

	/*------------------------------------------------Launch App -------------------------------------------------*/


	public String openApp(String apkfile) throws IOException, InterruptedException {

		test.log(LogStatus.INFO, "opening the app for the given apk file : " + apkfile);

		prop = new Properties();
		String path = Constants.PROPERTIES_FILE;

		try
		{
			FileInputStream fs = new FileInputStream(path);
			prop.load(fs);
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		File app = new File(prop.getProperty(apkfile));
		capabilities.setCapability("deviceName",prop.getProperty("deviceName"));
		capabilities.setCapability("platformVersion",prop.getProperty("platformVersion"));
		capabilities.setCapability("platformName",prop.getProperty("platformName"));
		capabilities.setCapability("app", app.getAbsolutePath());
		capabilities.setCapability(MobileCapabilityType.NO_RESET, "true");// don't clear the cache
		capabilities.setCapability(MobileCapabilityType.FULL_RESET, "false");// don't un-install
		service =	AppiumDriverLocalService.buildService( new AppiumServiceBuilder().usingDriverExecutable(new File(Constants.NODEJS_PATH)) .withAppiumJS(new File(Constants.APPIUM_PATH))) ;
		service.start();
		try {
			aDriver =  new AndroidDriver(new URL(prop.getProperty("hubURL")), capabilities);
			Thread.sleep(7000);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		aDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		//test.log(LogStatus.INFO, "Opened app successfully");
		return Constants.PASS;
	}


	/*------------------------------------------------Click Action-------------------------------------------------*/


	public String clicking(String locatorKey) throws IOException, InterruptedException {
		test.log(LogStatus.INFO, "Clicking on button :  " + locatorKey);
		aDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		((WebElement) getElement(locatorKey)).click();
		return Constants.PASS;
	}


	/*------------------------------------------------fetch locator action-------------------------------------------------*/	


	public AndroidElement getElement(String locatorKey) throws IOException, InterruptedException {
		AndroidElement e=null;
		try{
			if(locatorKey.endsWith("_xpath"))
			{
				e = (AndroidElement) aDriver.findElement(By.xpath(prop.getProperty(locatorKey)));
				
			}
			else if(locatorKey.endsWith("_id"))
			{
				e = (AndroidElement) aDriver.findElement(By.id(prop.getProperty(locatorKey)));
			
			}
			else
			{
				test.log(LogStatus.INFO, "Locator not found");
				//reportFailure("Locator not found ");
			}

		}
		catch(Exception ex)
		{
			test.log(LogStatus.INFO, ex.getMessage());
			//reportFailure("Test failed   " + ex.getMessage() );
		}
		return  e;
	}
	


	/*------------------------------------------------ Type Action-------------------------------------------------*/	


	
	public String type(String locatorKey, String data) throws IOException, InterruptedException {

	
		test.log(LogStatus.INFO, "Performing Type action for locator :  "+locatorKey);
		((WebElement) getElement(locatorKey)).sendKeys(data);	
		return Constants.PASS;
	}


	/*------------------------------------------------ Verify Text-------------------------------------------------*/


	
	public String verifyText(String locatorKey, String expectedText) throws IOException, InterruptedException
	{
		test.log(LogStatus.INFO,"Verifying text for  locator : " + locatorKey);
		String actualtext = ((WebElement) getElement(locatorKey)).getText();
		if(actualtext.equals(expectedText))
			return Constants.PASS;
		else{
			test.log(LogStatus.FAIL, "Test failed as the actual and expcted text Did not match, actual text is:  " + actualtext + "expected text is: " + expectedText);
			return "Fail - Text Did not match " + actualtext;
		}
	}


	/*------------------------------------------------Close App-------------------------------------------------*/	

	

	public String closeApp()
	{
		test.log(LogStatus.INFO,"Closing the App");
		if(aDriver!=null)
			aDriver.quit();
		test.log(LogStatus.INFO,"App closed successfully");
		service.stop();
		return Constants.PASS;
	}


	/*------------------------------------------------Report Failure-------------------------------------------------*/	
	


	public void reportFailure(String execution_result, String description) throws IOException, InterruptedException
	{
		takeScreenshot(execution_result);
		test.log(LogStatus.INFO, "Execution Result for test step  "  + " : " + description + " : " + "   is   :  " + execution_result);
		//test.log(LogStatus.FAIL, failureMsg);
		Assert.fail(execution_result);
	}

	

	/*------------------------------------------------Report Success-------------------------------------------------*/	
	


	public void reportPass(String execution_result, String description) throws IOException, InterruptedException
	{
		takeScreenshot(execution_result);
		test.log(LogStatus.INFO, "Execution Result for test step  "  + " : " + description + " : " + "   is   :  " + execution_result);
		//test.log(LogStatus.PASS, passMsg);
	}


	/*------------------------------------------------Take screenshot-------------------------------------------------*/	
	
	

	public void takeScreenshot(String execution_result) throws IOException, InterruptedException
	{

		Date d = new Date();
if(execution_result=="PASS") {
		String screenshotFile=d.toString().replace(":", "_").replace(" ","_")+".png";
		//String path = Constants.REPORT_PATH+"screenshots\\Success\\"+screenshotFile;
		String path = Constants.Successfull_tests_screens+screenshotFile;
		File scrFile = ((TakesScreenshot)aDriver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File(path));
		} catch (IOException e) {

			e.printStackTrace();
		}
		test.log(LogStatus.INFO,"Snapshot below: ("+screenshotFile+")"+
				test.addScreenCapture(path));
}
else {
	String screenshotFile=d.toString().replace(":", "_").replace(" ","_")+".png";
	//String path = Constants.REPORT_PATH+"screenshots\\Failure\\"+screenshotFile;
	String path = Constants.failed_tests_screens+screenshotFile;
	File scrFile = ((TakesScreenshot)aDriver).getScreenshotAs(OutputType.FILE);
	try {
		FileUtils.copyFile(scrFile, new File(path));
	} catch (IOException e) {

		e.printStackTrace();
	}
	test.log(LogStatus.INFO,"Snapshot below: ("+screenshotFile+")"+
			test.addScreenCapture(path));
}

	}
	
	
	
	/*--------------------------------------------------------WaitForSometime------------------------------------------------------------------- 	*/
	
	
	
	public String WaitForSometime(String locatorkey) throws InterruptedException, IOException{
		

		WebDriverWait allwait = new WebDriverWait(aDriver, 30);
		//allwait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(prop.getProperty(locatorkey))));
		
		if(locatorkey.endsWith("_xpath"))
		{
			allwait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(prop.getProperty(locatorkey))));
			
		}
		else if(locatorkey.endsWith("_id"))
		{
			allwait.until(ExpectedConditions.presenceOfElementLocated(By.id(prop.getProperty(locatorkey))));
		
		}

		return Constants.PASS;
		
		
		}
	
	
	
	/*********************************************verifyOTP***************************************************************************/
	
	
	
	public String verifyOTP( String pack, String activity)
	{
		
		String packageName = prop.getProperty(pack);
		String activityName = prop.getProperty(activity);
		
		WebDriverWait wait = new WebDriverWait(aDriver, 30);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@text='Verify OTP']")));
		aDriver.runAppInBackground(Duration.ofSeconds(-1));
		aDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);	
		
		aDriver.startActivity(new Activity(packageName, activityName));
//-------------------------------------------------------------------------------------------read otp
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String otp = aDriver.findElement(By.xpath("//*[contains(@text,\"Welcome to Zadinga. Your OTP for registration is\")]")).getText().split(" is")[1];
		String New_OTP = otp.replaceAll("\\D+","");

		//test.log(LogStatus.INFO,"OTP is");
		//test.log(LogStatus.INFO, New_OTP);				
		aDriver.terminateApp("com.android.mms");
		//test.log(LogStatus.INFO,"messaging app terminated successfully, now launching zadinga once again..");
		
		
		//re-launching Zadinga

		aDriver.activateApp("com.nowfloats.zadinga.development");
		//aDriver.startActivity(new Activity("com.nowfloats.zadinga.development", "com.nowfloats.zadinga.onboarding.ui.RegistrationActivity"));
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		
	//	test.log(LogStatus.INFO,"able to launch Zadinga successfully");
		
		
		char[] ch = new char[New_OTP.length()];
		 for (int i = 0; i < New_OTP.length(); i++) 
		 {
	            ch[i] = New_OTP.charAt(i);         
	           // test.log(LogStatus.INFO,"printing ch[i]");
	         if(i==0) 
	         {
	           try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        	 String digit = String.valueOf(ch[0]);
	        	// test.log(LogStatus.INFO,"clicking on 1st input digit");
	        	 aDriver.findElement(By.xpath("//android.widget.EditText[@resource-id='com.nowfloats.zadinga.development:id/otp_edittext1']")).click();
	        	 // test.log(LogStatus.INFO,"clicked on 1st input digit");
		     aDriver.findElement(By.xpath("//android.widget.EditText[@resource-id='com.nowfloats.zadinga.development:id/otp_edittext1']")).sendKeys(digit);
	         }
	         else if(i==1) 
	         {
	        	 String digit = String.valueOf(ch[1]);
	        	 aDriver.findElement(By.xpath("//android.widget.EditText[@resource-id='com.nowfloats.zadinga.development:id/otp_edittext2']")).click();
	        	 aDriver.findElement(By.xpath("//android.widget.EditText[@resource-id='com.nowfloats.zadinga.development:id/otp_edittext2']")).sendKeys(digit);
	         }
	         else if(i==2) 
	         {
	        	 String digit = String.valueOf(ch[2]);
	        	 aDriver.findElement(By.xpath("//android.widget.EditText[@resource-id='com.nowfloats.zadinga.development:id/otp_edittext3']")).click();
	        	 aDriver.findElement(By.xpath("//android.widget.EditText[@resource-id='com.nowfloats.zadinga.development:id/otp_edittext3']")).sendKeys(digit);
	         }
	         else if(i==3) 
	         {
	        	 String digit = String.valueOf(ch[3]);
	        	 aDriver.findElement(By.xpath("//android.widget.EditText[@resource-id='com.nowfloats.zadinga.development:id/otp_edittext4']")).click();
	        	 aDriver.findElement(By.xpath("//android.widget.EditText[@resource-id='com.nowfloats.zadinga.development:id/otp_edittext4']")).sendKeys(digit);
	         }
	         else if(i==4)
	         {
	        	 String digit = String.valueOf(ch[4]);
	        	 aDriver.findElement(By.xpath("//android.widget.EditText[@resource-id='com.nowfloats.zadinga.development:id/otp_edittext5']")).click();
	        	 aDriver.findElement(By.xpath("//android.widget.EditText[@resource-id='com.nowfloats.zadinga.development:id/otp_edittext5']")).sendKeys(digit);
	         }
	         
		 }
	   
return Constants.PASS;
		
		
	}
	
	/*----------------------------------------------------------------------scrollTobottom-------------------------------------------------------------------------------------------------*/
	
	public String scrollTobottom()
	{
	
	
		test.log(LogStatus.INFO, "Performing scroll action");
		
		
			
		Dimension dim = aDriver.manage().window().getSize();
		int width = dim.width;
		int height = dim.height;
		System.out.println("Width - "+ width);
		System.out.println("Height - "+ height);
		
		
		int middleX = width/2;
		int startY = (int)(height * .9);
		int endY = (int)(height * .1);
		
		TouchAction act = new TouchAction(aDriver);
		
		act
		.press(PointOption.point(middleX,startY))
		.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
		.moveTo(PointOption.point(middleX,endY))
		.release()
		.perform();
	
		return Constants.PASS;
		
	}
	/*----------------------------------------------------------------------scrollFromElementToElement-------------------------------------------------------------------------------------------------*/
	
	public String scrollFromElementToElement(String locatorKey_a, String locatorKey_b)
	{
	
	
		test.log(LogStatus.INFO, "Performing scroll action");
		
		
		WebElement efrom= aDriver.findElement(By.xpath(prop.getProperty(locatorKey_a)));
		WebElement eto= aDriver.findElement(By.xpath(prop.getProperty(locatorKey_b)));
		
	
		int efrom_x = ((WebElement) efrom).getLocation().x;
		test.log(LogStatus.INFO, "value of x coordinate of from element is: " + "   " + efrom_x);
		int efrom_y = ((WebElement) efrom).getLocation().y;
		int eto_x = ((WebElement) eto).getLocation().x;
		int eto_y = ((WebElement) eto).getLocation().y;
		TouchAction act = new TouchAction(aDriver);

		act.press(PointOption.point(efrom_x,efrom_y))
		.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
		.moveTo(PointOption.point(eto_x,eto_y))
		.release()
		.perform();
	
		return Constants.PASS;
		
	}
	
	
/*----------------------------------------------------------------------hideKeyboard---------------------------------------------------------------------------------------------------------*/
	
	public String hideKeyboard()
	{
		boolean isKeyboardShown = aDriver.isKeyboardShown();

if(isKeyboardShown==true) {
		//aDriver.pressKey(new KeyEvent().withKey(AndroidKey.BACK));
	aDriver.hideKeyboard();
}
else {
	test.log(LogStatus.INFO, "keyboard isn't shown");
	}
		return Constants.PASS;
	}
	
	/*******************************************************Rare actions**************************************************************************/
public String RareActions(String locatorkey) throws InterruptedException, IOException{
		
	AndroidElement e=null;
	try{
		if(locatorkey.endsWith("_xpath"))
		{
			e = (AndroidElement) aDriver.findElement(By.xpath(prop.getProperty(locatorkey)));
			if(e != null) {
				aDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				((WebElement) getElement(locatorkey)).click();
				//return Constants.PASS;
			}
			
		}
		else if(locatorkey.endsWith("_id"))
		{
			e = (AndroidElement) aDriver.findElement(By.id(prop.getProperty(locatorkey)));
			if(e != null) {
				aDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				((WebElement) getElement(locatorkey)).click();
				//return Constants.PASS;
			}
		}
		else
		{
			test.log(LogStatus.INFO, "Locator not found");
			//reportFailure("Locator not found ");
		}

	}
	catch(Exception ex)
	{
		test.log(LogStatus.INFO, ex.getMessage());
		//reportFailure("Test failed   " + ex.getMessage() );
	}
return Constants.PASS;
		}
	
/*----------------------------------------------------------------------PressAndroidbackbutton---------------------------------------------------------------------------------------------------------*/

public String PressAndroidbackbutton()
{
	
	aDriver.navigate().back();
	return Constants.PASS;

}
	
	/*---------------------------------------------------------------getActivityName---------------------------------------------------------------------------------------------------------*/
	
	/*
	
	public String getActivityName()
	{
		test.log(LogStatus.INFO, "activity name is:");
		test.log(LogStatus.INFO, aDriver.currentActivity());
		return Constants.PASS;
	}
	*/
	
	/**********************************************************End of functions********************************************************************/
	}

