package poc.jackson.mapper;

import java.util.List;
import java.util.Map;

/**
 * create a sample class structure matching to json structure 
 * 
 * @author pankaj.katiyar
 *
 */
public class SampleEmployee {

	int id;
	String name;
	boolean permanent;
	SampleEmployeeAddress address;
	long [] phoneNumbers;
	String role;
	List<String> cities;
	Map<String, String> properties;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isPermanent() {
		return permanent;
	}
	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}
	public SampleEmployeeAddress getAddress() {
		return address;
	}
	public void setAddress(SampleEmployeeAddress address) {
		this.address = address;
	}
	public long[] getPhoneNumbers() {
		return phoneNumbers;
	}
	public void setPhoneNumbers(long[] phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public List<String> getCities() {
		return cities;
	}
	public void setCities(List<String> cities) {
		this.cities = cities;
	}
	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

}
