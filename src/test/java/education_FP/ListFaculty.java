package education_FP;
import java.io.IOException;
import java.util.Hashtable;

import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.nf.mobile.web.hybrid.data.DataUtil;
import com.nf.mobile.web.hybrid.driver.DriverScript;
import com.nf.mobile.web.hybrid.util.Constants;
import com.nf.mobile.web.hybrid.util.ExtentManager;
import com.nf.mobile.web.hybrid.util.Xls_Reader;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;


public class ListFaculty
 {

	ExtentReports report = ExtentManager.getInstance();
	ExtentTest test = report.startTest("ListFaculty");
	DriverScript ds;
	String testCaseName="ListFaculty";//TEST CASE FOR WHICH THE DATA HAS TO BE READ
	Xls_Reader xls = new Xls_Reader(Constants.DATA_XLSX_Education);
	
	@Test(dataProvider="getData")
	public void testApp(Hashtable<String,String> data) throws IOException, InterruptedException {
		
		//-----------------------------------------------------reports-----------------------------------------------------------------------//
		
		test.log(LogStatus.INFO, "Starting the test" + testCaseName);
		test.log(LogStatus.INFO, data.toString());
		
		if(!DataUtil.isTestRunnable(xls, testCaseName) || data.get(Constants.RUNMODE_COL).equals("N")) {
			
			test.log(LogStatus.INFO, "Skipping the test as runmode is NO");
			throw new SkipException("Skipping the test as runmode is NO");
		}
			
		

		ds = new DriverScript(test);
		ds.executeKeywords(testCaseName,data,Constants.DATA_XLSX_Education); //passing the testcase that needs to be executed
		
		test.log(LogStatus.INFO, "Ending the test" + testCaseName);
		//ds.getKeywords().reportPass("Test Passed");
		

	}
	
	@AfterMethod
	public void quit() {
		if(ds!=null) {
		ds.getKeywords().closeApp();
		ds=null;
		}
		if(report!=null) {
			report.endTest(test);
			report.flush();
			report=null;
		}
	}
	
	@DataProvider
	public Object[][] getData(){
	
		return DataUtil.getData(xls, testCaseName);
}
}