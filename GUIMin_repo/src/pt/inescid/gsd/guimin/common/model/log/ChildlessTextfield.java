package pt.inescid.gsd.guimin.common.model.log;

import java.io.Serializable;

public class ChildlessTextfield implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2545255831618780135L;

	private String widgetid;
	
	private String text;
	
	public ChildlessTextfield(String widgetid, String text) {
		super();
		this.widgetid = widgetid;
		this.text = text;
	}

	public String getWidgetid() {
		return widgetid;
	}

	public String getText() {
		return text;
	}

	public void setWidgetid(String widgetid) {
		this.widgetid = widgetid;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
	
	
}
