package framework.core.grid.configuration;

import java.util.List;

public class GridConfigurationMapping {

	private Hub hub;
	private List<Node> nodes;

	public Hub getHub() {
		return hub;
	}
	public void setHub(Hub hub) {
		this.hub = hub;
	}

	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
}
