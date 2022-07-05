package com.nf.mobile.web.hybrid.driver;

import java.io.IOException;
import java.util.Hashtable;

import com.nf.mobile.web.hybrid.util.Constants;
import com.nf.mobile.web.hybrid.util.Xls_Reader;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class DriverScript {

	ExtentTest test;
	AppKeywords keywords;
	public DriverScript(ExtentTest test) {
		this.test = test;
		keywords =  new AppKeywords(test);
	}

	public void executeKeywords(String testUnderExecution, Hashtable<String,String> testData, String filename ) throws IOException, InterruptedException {

		String keywords_sheet = "Keywords";
		Xls_Reader xls = new Xls_Reader(filename);
		int rows = xls.getRowCount(keywords_sheet);
		int count=0;


		//reading data from Data sheet_________________________________________________________________________________________
		for(int rnum=2; rnum <=rows; rnum++) {
			
			String tcid = xls.getCellData(keywords_sheet, Constants.TCID_COL , rnum);

			if(tcid.equals(testUnderExecution)) {
				
				System.out.println("XYZ" + count);
				count++;

				String description = xls.getCellData(keywords_sheet, Constants.DESCRIPTION_COL , rnum);
				String viewType = xls.getCellData(keywords_sheet,Constants.VIEWTYPE_COL, rnum);
				String keyword = xls.getCellData(keywords_sheet, Constants.KEYWORD_COL , rnum);
				String object = xls.getCellData(keywords_sheet, Constants.OBJECT_COL , rnum);
				String dataKey = xls.getCellData(keywords_sheet, Constants.DATA_COL , rnum);
				//String dataKey_a = xls.getCellData(keywords_sheet, Constants.DATA_COL_a , rnum);
				String object_a = xls.getCellData(keywords_sheet, Constants.OBJECT_COL_a , rnum);
				String object_b = xls.getCellData(keywords_sheet, Constants.OBJECT_COL_b , rnum);
				String pack = xls.getCellData(keywords_sheet, Constants.pack , rnum);
				String activity = xls.getCellData(keywords_sheet, Constants.activity , rnum);


				test.log(LogStatus.INFO, "Executing   Scenario for    :             " + tcid + "              :             Test Case :                     " + description);
				String execution_result = ""; 

				if(keyword.equalsIgnoreCase("openApp"))
				{
					execution_result=keywords.openApp(object);
				}
				else if(keyword.equalsIgnoreCase("clicking"))
				{
					execution_result=keywords.clicking(object);
				}
				else if(keyword.equalsIgnoreCase("type"))
				{
					execution_result=keywords.type(object,testData.get(dataKey));
				}
				else if(keyword.equalsIgnoreCase("app_clearContent"))
				{
					execution_result=keywords.app_clearContent(object);
				}
				else if(keyword.equalsIgnoreCase("closeApp"))
				{
					execution_result=keywords.closeApp();
				}
				else if(keyword.equalsIgnoreCase("verifyText"))
				{
					execution_result=keywords.verifyText(object, testData.get(dataKey));
				}
				else if(keyword.equalsIgnoreCase("appSpecificKeywords"))
				{
					execution_result=keywords.appSpecificKeywords();
				}
				else if(keyword.equalsIgnoreCase("WaitForSometime"))
				{
					execution_result=keywords.WaitForSometime(object);
				}
				else if(keyword.equalsIgnoreCase("scrollFromElementToElement"))
				{
					execution_result=keywords.scrollFromElementToElement(object_a, object_b);
				}
				else if(keyword.equalsIgnoreCase("scrollTobottom"))
				{
					execution_result=keywords.scrollTobottom();
				}
				else if(keyword.equalsIgnoreCase("hideKeyboard"))
				{
					execution_result=keywords.hideKeyboard();
				}
				else if(keyword.equalsIgnoreCase("PressAndroidbackbutton"))
				{
					execution_result=keywords.PressAndroidbackbutton();
				}
				else if(keyword.equalsIgnoreCase("verifyOTP"))
				{
					execution_result=keywords.verifyOTP(pack, activity);
				}
				else if(keyword.equalsIgnoreCase("RareActions"))
				{
					execution_result=keywords.RareActions(object);
				}
				else if(keyword.equals("tapByCoordinates"))
				{
					execution_result=keywords.tapByCoordinates(117,1917);

				}

				//test.log(LogStatus.INFO, "Execution Result for test step  "  + " : " + description + " : " + "   is   :  " + execution_result);
				if(execution_result.equals(Constants.PASS)) {
					keywords.reportPass(execution_result, description);
				}
				if(!execution_result.equals(Constants.PASS)) {
					keywords.reportFailure(execution_result, description);
				}

			}


		}


	}
	public AppKeywords getKeywords() {
		return keywords;
	}

}

