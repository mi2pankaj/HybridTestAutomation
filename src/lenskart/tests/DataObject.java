package lenskart.tests;

public class DataObject {

	/**
	 * This class contains all keys which are supposed to be in dataObject json file. variable name should be same as json keys 
	 * and variable name should follow camel case like desktop_TC_ID or email_id not Desktop_TC_ID.
	 * 
	 * now advantage is - if json is increasing - we just need to add that mapping by adding respective constructors .. thats it 
	 */

	String email_id;
	String desktop_TC_ID;
	String order_Id;	

	String vsm_Execution;
	String mobile_TC_ID;
	
	String order_total;
    String power_type;
	String left_power;
	String right_power;

	public String getOrder_total() {
		return order_total;
	}
	public void setOrder_total(String order_total) {
		this.order_total = order_total;
	}
	public String getEmail_id() {
		return email_id;
	}
	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}
	public String getDesktop_TC_ID() {
		return desktop_TC_ID;
	}
	public void setDesktop_TC_ID(String desktop_TC_ID) {
		this.desktop_TC_ID = desktop_TC_ID;
	}
	public String getOrder_Id() {
		return order_Id;
	}
	public void setOrder_Id(String order_Id) {
		this.order_Id = order_Id;
	}
	public String getVsm_Execution() {
		return vsm_Execution;
	}
	public void setVsm_Execution(String vsm_Execution) {
		this.vsm_Execution = vsm_Execution;
	}
	public String getMobile_TC_ID() {
		return mobile_TC_ID;
	}
	public void setMobile_TC_ID(String mobile_TC_ID) {
		this.mobile_TC_ID = mobile_TC_ID;
	}
	public String getPower_type() {
		return power_type;
	}
	public void setPower_type(String power_type) {
		this.power_type = power_type;
	}
	public String getLeft_power() {
		return left_power;
	}
	public void setLeft_power(String left_power) {
		this.left_power = left_power;
	}
	public String getRight_power() {
		return right_power;
	}
	public void setRight_power(String right_power) {
		this.right_power = right_power;
	}
}
