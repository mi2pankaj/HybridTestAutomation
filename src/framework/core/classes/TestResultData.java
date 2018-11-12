package framework.core.classes;

import java.io.Serializable;

public class TestResultData implements Serializable{

	public String templateData;
	private static final long serialVersionUID = 2L;
	public String getTemplateData() {
		return templateData;
	}
	public void setTemplateData(String templateData) {
		this.templateData = templateData;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
