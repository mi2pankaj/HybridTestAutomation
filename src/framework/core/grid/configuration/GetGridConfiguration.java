package framework.core.grid.configuration;

import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetGridConfiguration {

	private GridConfigurationMapping gridConfiguration;
	
	public static void main (String [] args) {
		
		System.out.println("Hub - "+new GetGridConfiguration().getGridConfiguration().getHub().getIpAddress());
		System.out.println("Node - "+new GetGridConfiguration().getGridConfiguration().getNodes().get(0).getIpAddress());
		System.out.println("Executor Mobile - "+new GetGridConfiguration().getGridConfiguration().getNodes().get(0).getExecutor_size().get("mobile"));
		System.out.println("Executor Default - "+new GetGridConfiguration().getGridConfiguration().getNodes().get(0).getExecutor_size().get("default")); 
		System.out.println("Executor Hub Default - "+new GetGridConfiguration().getGridConfiguration().getHub().getExecutor_size().get("default"));
		
	}
	
	/** load the grid configuration into class - in this constructor  
	 */
	public GetGridConfiguration() {
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			gridConfiguration = mapper.readValue(new File(System.getProperty("user.dir")+"/properties/gridConfiguration.properties"), GridConfigurationMapping.class);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GridConfigurationMapping getGridConfiguration() {
		return gridConfiguration;
	}

}



