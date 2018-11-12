package framework.core.grid.configuration;

import java.util.TreeMap;

public class Hub {

	private String ipAddress;
	private TreeMap<String, Integer> executor_size;
	
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public TreeMap<String, Integer> getExecutor_size() {
		return executor_size;
	}
	public void setExecutor_size(TreeMap<String, Integer> executor_size) {
		this.executor_size = executor_size;
	}

}
