package framework.utilities;

import java.util.HashMap;
import java.util.Map;

public class EmailTemplate {
	
	private String mobileTestPassed = "";
	private String mobileTestFailed = "";
	private String mobileTestTotalCases = "";
	private String mobileExecutionTime = "";
	private Map<String, String> mobile_ownerWiseTestResult = new HashMap<String, String>();
	
	private String desktopTestPassed = "";
	private String desktopTestFailed = "";
	private String desktopTestTotalCases = "";
	private String desktopExecutionTime = "";
	private Map<String, String> desktop_ownerWiseTestResult = new HashMap<String, String>();
	
	private String vsmTestPassed = "";
	private String vsmTestFailed = "";
	private String vsmTestTotalCases = "";
	private String vsmExecutionTime = "";	
	private Map<String, String> vsm_ownerWiseTestResult = new HashMap<String, String>();
	
	
	public String getVsmTestPassed() {
		return vsmTestPassed;
	}
	public void setVsmTestPassed(String vsmTestPassed) {
		this.vsmTestPassed = vsmTestPassed;
	}
	public String getVsmTestFailed() {
		return vsmTestFailed;
	}
	public void setVsmTestFailed(String vsmTestFailed) {
		this.vsmTestFailed = vsmTestFailed;
	}
	public String getVsmTestTotalCases() {
		return vsmTestTotalCases;
	}
	public void setVsmTestTotalCases(String vsmTestTotalCases) {
		this.vsmTestTotalCases = vsmTestTotalCases;
	}
	public String getVsmExecutionTime() {
		return vsmExecutionTime;
	}
	public void setVsmExecutionTime(String vsmExecutionTime) {
		this.vsmExecutionTime = vsmExecutionTime;
	}

	public Map<String, String> getMobile_ownerWiseTestResult() {
		return mobile_ownerWiseTestResult;
	}
	public void setMobile_ownerWiseTestResult(Map<String, String> mobile_ownerWiseTestResult) {
		this.mobile_ownerWiseTestResult = mobile_ownerWiseTestResult;
	}
	public Map<String, String> getDesktop_ownerWiseTestResult() {
		return desktop_ownerWiseTestResult;
	}
	public void setDesktop_ownerWiseTestResult(Map<String, String> desktop_ownerWiseTestResult) {
		this.desktop_ownerWiseTestResult = desktop_ownerWiseTestResult;
	}
	
	public String getMobileTestPassed() {
		return mobileTestPassed;
	}
	public void setMobileTestPassed(String mobileTestPassed) {
		this.mobileTestPassed = mobileTestPassed;
	}
	public String getMobileTestFailed() {
		return mobileTestFailed;
	}
	public void setMobileTestFailed(String mobileTestFailed) {
		this.mobileTestFailed = mobileTestFailed;
	}
	public String getMobileTestTotalCases() {
		return mobileTestTotalCases;
	}
	public void setMobileTestTotalCases(String mobileTestTotalCases) {
		this.mobileTestTotalCases = mobileTestTotalCases;
	}
	public String getMobileExecutionTime() {
		return mobileExecutionTime;
	}
	public void setMobileExecutionTime(String mobileExecutionTime) {
		this.mobileExecutionTime = mobileExecutionTime;
	}
	public String getDesktopTestPassed() {
		return desktopTestPassed;
	}
	public void setDesktopTestPassed(String desktopTestPassed) {
		this.desktopTestPassed = desktopTestPassed;
	}
	public String getDesktopTestFailed() {
		return desktopTestFailed;
	}
	public void setDesktopTestFailed(String desktopTestFailed) {
		this.desktopTestFailed = desktopTestFailed;
	}
	public String getDesktopTestTotalCases() {
		return desktopTestTotalCases;
	}
	public void setDesktopTestTotalCases(String desktopTestTotalCases) {
		this.desktopTestTotalCases = desktopTestTotalCases;
	}
	public String getDesktopExecutionTime() {
		return desktopExecutionTime;
	}
	public void setDesktopExecutionTime(String desktopExecutionTime) {
		this.desktopExecutionTime = desktopExecutionTime;
	}
	public Map<String, String> getVsm_ownerWiseTestResult() {
		return vsm_ownerWiseTestResult;
	}
	public void setVsm_ownerWiseTestResult(Map<String, String> vsm_ownerWiseTestResult) {
		this.vsm_ownerWiseTestResult = vsm_ownerWiseTestResult;
	}
}
