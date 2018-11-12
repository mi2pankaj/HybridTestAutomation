package lenskart.api.productApiMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductAPISubscription {

	private String name;
	
	private String duration;
	
	private List<Map<String, String>> prices = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public List<Map<String, String>> getPrices() {
		return prices;
	}

	public void setPrices(List<Map<String, String>> prices) {
		this.prices = prices;
	}

}
