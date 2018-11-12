package lenskart.tests;

import java.io.Serializable;

public class OrderDetails implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String orderDetail;

	public String getOrderDetail() {
		return orderDetail;
	}
	public void setOrderDetail(String orderDetail) {
		this.orderDetail = orderDetail;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
