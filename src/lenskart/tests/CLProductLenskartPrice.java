package lenskart.tests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CLProductLenskartPrice {

	private String product_id;
	
	private String lenskartPrice;
	
	private String productUrl;

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getLenskartPrice() {
		return lenskartPrice;
	}

	public void setLenskartPrice(String lenskartPrice) {
		this.lenskartPrice = lenskartPrice;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	
}
