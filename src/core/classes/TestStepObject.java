package core.classes;

import org.apache.log4j.Logger; 

/** This class is a storage of object which contains the testCaseId, testStepId, data, keyword, objectName
 * The object of this class will contain all the information of a testStep
 * 
 * @author Pankaj
 *
 */


public class TestStepObject implements Cloneable {

	Logger logger = Logger.getLogger(TestStepObject.class.getName());

	String testCaseId;
	String testStepId;

	String data;
	String keyword;
	String objectName;

	String testStepExecutionTime = "0";
	
	String testStepResult;
	String testStepBeforeRetryResult ="";

	String testStepDescription;
//	int testStepIdRowNumber;
	
	boolean pageLoadedProperly;
	String currentStepUrl;
	String testStepRetryResult;

//	TestStepObjectsRetry testStepObjectsRetry;


	public String getTestStepBeforeRetryResult() {
		return testStepBeforeRetryResult;
	}

	public void setTestStepBeforeRetryResult(String testStepBeforeRetryResult) {
		this.testStepBeforeRetryResult = testStepBeforeRetryResult;
	}

//	public TestStepObjectsRetry getTestStepObjectsRetry() {
//		return testStepObjectsRetry;
//	}
//
//	public void setTestStepObjectsRetry(TestStepObjectsRetry testStepObjectsRetry) {
//		this.testStepObjectsRetry = testStepObjectsRetry;
//	}

	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	public String getTestStepDescription() {
		return testStepDescription;
	}

	public void setTestStepDescription(String testStepDescription) {
		this.testStepDescription = testStepDescription;
	}

//	public int getTestStepIdRowNumber() {
//		return testStepIdRowNumber;
//	}
//
//	public void setTestStepIdRowNumber(int testStepIdRowNumber) {
//		this.testStepIdRowNumber = testStepIdRowNumber;
//	}

	public String getTestStepResult() {
		return testStepResult;
	}

	public void setTestStepResult(String testStepResult) {
		this.testStepResult = testStepResult;
	}

	public String getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getTestStepId() {
		return testStepId;
	}

	public void setTestStepId(String testStepId) {
		this.testStepId = testStepId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;

	}

	public String getTestStepExecutionTime() {
		return testStepExecutionTime;
	}

	public void setTestStepExecutionTime(String testStepExecutionTime) {
		this.testStepExecutionTime = testStepExecutionTime;
	}

	public boolean isPageLoadedProperly() {
		return pageLoadedProperly;
	}

	public void setPageLoadedProperly(boolean pageLoadedProperly) {
		this.pageLoadedProperly = pageLoadedProperly;
	}

	public String getCurrentStepUrl() {
		return currentStepUrl;
	}

	public void setCurrentStepUrl(String currentStepUrl) {
		this.currentStepUrl = currentStepUrl;
	}

	public String getTestStepRetryResult() {
		return testStepRetryResult;
	}

	public void setTestStepRetryResult(String testStepRetryResult) {
		this.testStepRetryResult = testStepRetryResult;
	}



}
