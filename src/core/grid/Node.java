package core.grid;

import java.util.TreeMap;

public class Node {

	private String ipAddress;
	private String remote_url;
	private String parameters;
	private TreeMap<String, Integer> executor_size;
	
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getRemote_url() {
		return remote_url;
	}
	public void setRemote_url(String remote_url) {
		this.remote_url = remote_url;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public TreeMap<String, Integer> getExecutor_size() {
		return executor_size;
	}
	public void setExecutor_size(TreeMap<String, Integer> executor_size) {
		this.executor_size = executor_size;
	}

}
