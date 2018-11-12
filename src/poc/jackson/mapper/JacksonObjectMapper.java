package poc.jackson.mapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JacksonObjectMapper {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		simpleJsonMapperRead_Rishi();		
	}

	
	public static void simpleJsonMapperRead_Rishi() throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();

		Product_Baap rishi = mapper.readValue(new File("/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/src/poc/jackson/mapper/product_baap.json")
				, Product_Baap.class);
		
		System.out.println(rishi.getResult().isisTryNowAvailable());
		
	}
	
	public static void simpleJsonMapperRead() throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();

		SampleEmployee employee = mapper.readValue(new File("/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/src/poc/jackson/mapper/employee-simple-json")
				, SampleEmployee.class);

		System.out.println(employee.getId());

		for(String city : employee.getCities())
		{
			System.out.println(city);
		}

	}

	public static void simpleJsonMapperWrite() throws JsonGenerationException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		//set records
		SampleEmployee employee = setRecords();

		//write value
		mapper.writeValue(new File("/Users/pankaj.katiyar/Desktop/sample-json"), employee);

		System.out.println("Done");
	}

	public static void simpleJsonArrayMapperRead() throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<SampleEmployee>> mapType = new TypeReference<List<SampleEmployee>>() {};

		List<SampleEmployee> employeeList = mapper.readValue(
				new File("/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/src/poc/jackson/mapper/employee-json-array"),
				mapType);

		System.out.println(employeeList.get(0).getId());
	}

	public static void simpleJsonArrayMapperWrite() throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		List<SampleEmployee> employeeList = new ArrayList<>();
		employeeList.add(setRecords());
		employeeList.add(setRecords());
		employeeList.add(setRecords());
		employeeList.add(setRecords());

		mapper.writeValue(new File("/Users/pankaj.katiyar/Desktop/Automation/employee-json-array"),
				employeeList);

		System.out.println("done bhai");
	}

	public static SampleEmployee setRecords()
	{
		SampleEmployee employee = new SampleEmployee();

		//set values of json
		SampleEmployeeAddress address = new SampleEmployeeAddress();
		address.setCity("gurgaon");
		address.setStreet("Unknown");
		address.setZipcode(122003);

		List<String> cities = new ArrayList<>();
		cities.add("Gurugram");
		cities.add("Kanpur");

		long []phoneNumbers = {111111, 211221111, 2211111};

		Map<String, String> properties = new HashMap<>();
		properties.put("age", "34");
		properties.put("salary", "1000");

		employee.setAddress(address);
		employee.setCities(cities);
		employee.setId(12345);
		employee.setName("Unknown Name");
		employee.setPermanent(false);
		employee.setPhoneNumbers(phoneNumbers);
		employee.setProperties(properties);
		employee.setRole("Faltu");

		return employee;
	}
}
