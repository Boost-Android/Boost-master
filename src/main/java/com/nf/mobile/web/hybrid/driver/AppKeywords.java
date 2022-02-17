package com.nf.mobile.web.hybrid.driver;

import com.nf.mobile.web.hybrid.util.Constants;
import com.relevantcodes.extentreports.ExtentTest;

public class AppKeywords extends GenericKeywords{
	
	public AppKeywords(ExtentTest test) {
		super(test);
	}

	public String appSpecificKeywords() {
		System.out.println("doing some specific action in app");
		return Constants.PASS;
	}
	
}
		
