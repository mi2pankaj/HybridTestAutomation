package core.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCaseObject implements Cloneable, Serializable{
	

	private static final long serialVersionUID = 1L;
	
	private String testCaseId;
	private String testCaseResult;
	private String testCaseSupportedBrowserType;
	private String testCaseDescription;
	private String testCaseDataDriven;
	private String testCaseType;

	private AtomicInteger testCaseExecutionProgressStatus;
	private AtomicBoolean ifTestCaseQueued;
	private List<TestStepObject> testStepObjectsList = new ArrayList<>();

	private String testDataID;
	private String testCaseExecutionTime;
	private String ownerName;

	private String dateTime;
	private String executorMachineIpAddress;
	
	private String appiumDriverURL;
	private String chromeDriverURLLaunchedByAppiumDriver;
	private String deviceUDID;
	private boolean appiumDriverAlertHandled;
	private Object appiumDriverSessionId;
	private boolean handleNotificationForAppiumRequired;
	
	
	public String getAppiumDriverURL() {
		return appiumDriverURL;
	}

	public void setAppiumDriverURL(String appiumDriverURL) {
		this.appiumDriverURL = appiumDriverURL;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	public String getTestCaseDataDriven() {
		return testCaseDataDriven;
	}

	public void setTestCaseDataDriven(String testCaseDataDriven) {
		this.testCaseDataDriven = testCaseDataDriven;
	}

	public AtomicBoolean getIfTestCaseQueued() {
		return ifTestCaseQueued;
	}

	/** atomic flag = false means - not queued for execution and true means queued, false is set at the time of loading test case objects
	 * and true is set at the time of queuing the test case
	 * 
	 * @param ifTestCaseQueued
	 */
	public void setIfTestCaseQueued(AtomicBoolean ifTestCaseQueued) {
		this.ifTestCaseQueued = ifTestCaseQueued;
	}

	public String getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getTestCaseSupportedBrowserType() {
		return testCaseSupportedBrowserType;
	}

	public void setTestCaseSupportedBrowserType(String testCaseSupportedBrowserType) {
		this.testCaseSupportedBrowserType = testCaseSupportedBrowserType;
	}

	public String getTestCaseResult() {
		return testCaseResult;
	}

	public void setTestCaseResult(String testCaseResult) {
		this.testCaseResult = testCaseResult;
	}

	public AtomicInteger getTestCaseExecutionProgressStatus() {
		return testCaseExecutionProgressStatus;
	}

	/** atomic integer = 0 means - in progress and 1 means completed, this value to be set at the of execution. 
	 * 
	 * @param testCaseExecutionProgressStatus
	 */
	public void setTestCaseExecutionProgressStatus(AtomicInteger testCaseExecutionProgressStatus) {
		this.testCaseExecutionProgressStatus = testCaseExecutionProgressStatus;
	}

	public List<TestStepObject> gettestStepObjectsList() {
		return testStepObjectsList;
	}

	public void settestStepObjectsList(List<TestStepObject> testStepObjectsList) {
		this.testStepObjectsList = testStepObjectsList;
	}

	public String getTestCaseDescription() {
		return testCaseDescription;
	}

	public void setTestCaseDescription(String testCaseDescription) {
		this.testCaseDescription = testCaseDescription;
	}

	public String getTestDataID() {
		return testDataID;
	}

	public void setTestDataID(String testDataID) {
		this.testDataID = testDataID;
	}


	public String getTestCaseType() {
		return testCaseType;
	}


	public void setTestCaseType(String testCaseType) {
		this.testCaseType = testCaseType;
	}


	public String getTestCaseExecutionTime() {
		return testCaseExecutionTime;
	}


	public void setTestCaseExecutionTime(String testCaseExecutionTime) {
		this.testCaseExecutionTime = testCaseExecutionTime;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getExecutorMachineIpAddress() {
		return executorMachineIpAddress;
	}

	public void setExecutorMachineIpAddress(String executorMachineIpAddress) {
		this.executorMachineIpAddress = executorMachineIpAddress;
	}

	public long getSerialVersionUID() {
		return serialVersionUID;
	}

	public boolean isAppiumDriverAlertHandled() {
		return appiumDriverAlertHandled;
	}

	public void setAppiumDriverAlertHandled(boolean appiumDriverAlertHandled) {
		this.appiumDriverAlertHandled = appiumDriverAlertHandled;
	}

	public String getDeviceUDID() {
		return deviceUDID;
	}

	public void setDeviceUDID(String deviceUDID) {
		this.deviceUDID = deviceUDID;
	}

	public Object getAppiumDriverSessionId() {
		return appiumDriverSessionId;
	}

	public void setAppiumDriverSessionId(Object appiumDriverSessionId) {
		this.appiumDriverSessionId = appiumDriverSessionId;
	}

	public String getChromeDriverURLLaunchedByAppiumDriver() {
		return chromeDriverURLLaunchedByAppiumDriver;
	}

	public void setChromeDriverURLLaunchedByAppiumDriver(String chromeDriverURLLaunchedByAppiumDriver) {
		this.chromeDriverURLLaunchedByAppiumDriver = chromeDriverURLLaunchedByAppiumDriver;
	}

	public boolean isHandleNotificationForAppiumRequired() {
		return handleNotificationForAppiumRequired;
	}

	public void setHandleNotificationForAppiumRequired(boolean handleNotificationForAppiumRequired) {
		this.handleNotificationForAppiumRequired = handleNotificationForAppiumRequired;
	}

}