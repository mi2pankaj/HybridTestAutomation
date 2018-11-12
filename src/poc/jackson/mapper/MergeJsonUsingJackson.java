package poc.jackson.mapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lenskart.tests.DataObject;

public class MergeJsonUsingJackson {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		TypeReference<List<DataObject>> dataObjectType = new TypeReference<List<DataObject>>() {};

		File basePath = new File("/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/src/poc/jackson/mapper");
		File[] files = basePath.listFiles();
		
		for(int i=0; i<files.length; i++)
		{
			if(files[i].isFile() && files[i].getName().startsWith("dataObject")) {
				System.out.println(files[i].getName() + files[i].getAbsolutePath());
			}
		}
		
		//1. de-serialize 
		String strDataObject1 = "/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/src/poc/jackson/mapper/dataObject"; 
		String strDataObject2 = "/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/src/poc/jackson/mapper/dataObject2";

		List<DataObject> dataObjectList1 = mapper.readValue(new File(strDataObject1), dataObjectType);
		System.out.println(dataObjectList1.size());
		
		List<DataObject> dataObjectList2 = mapper.readValue(new File(strDataObject2), dataObjectType);
		System.out.println(dataObjectList2.size());
		
		dataObjectList1.addAll(dataObjectList2);
		
		System.out.println(dataObjectList1.size());
		
		int c = 0;
		for(DataObject data : dataObjectList1)
		{
			System.out.println(c + " - " + data.getDesktop_TC_ID()  + " - " + data.getEmail_id()  +  " - " + data.getOrder_Id() + " - " + data.getMobile_TC_ID() + " - " +data.getVsm_Execution());
			c++;
		}

		System.out.println("de-serialze");
		
		//2. serialize
		mapper.writeValue(new File("/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/src/poc/jackson/mapper/dataObjectMerged"),
				dataObjectList1);
		
		System.out.println("serialze");
		
		//3. de-serailze, set object values and then serailze
		dataObjectList1 = mapper.readValue(new File("/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/src/poc/jackson/mapper/dataObjectMerged"),
				dataObjectType);
		

		for(DataObject data : dataObjectList1)
		{
			if(data.getOrder_Id().equalsIgnoreCase("100"))
			{
				data.setOrder_Id("pankaj_100");
			}
			System.out.println(c + " - " + data.getDesktop_TC_ID()  + " - " + data.getEmail_id()  +  " - " + data.getOrder_Id() + " - " + data.getMobile_TC_ID() + " - " +data.getVsm_Execution());
		}
		
		mapper.writeValue(new File("/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/src/poc/jackson/mapper/dataObjectUpdated"),
				dataObjectList1);
	}

}
