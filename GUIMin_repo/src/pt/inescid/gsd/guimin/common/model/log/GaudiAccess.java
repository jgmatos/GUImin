package pt.inescid.gsd.guimin.common.model.log;

import java.io.Serializable;

public class GaudiAccess implements Serializable{

	private static final long serialVersionUID = 1L;

	private String widgetid;
	private Object value;
	private boolean write;
	private boolean read;
	
	GaudiAccess(String id, Serializable value, boolean write, boolean read){
		this.setWidgetid(id);
		this.setValue(value);
		this.write = write;
		this.read = read;
	}
	
	public boolean isWrite(){
		return this.write;
	}
	
	public boolean isRead(){
		return this.read;
	}

	public void setWidgetid(String widgetid) {
		this.widgetid = widgetid;
	}

	public String getWidgetid() {
		return widgetid;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public String toString(){
		if (write)
			return "write access to variable at " + this.widgetid + "\n";
		else if (read)
			return "read access to variable at " + this.widgetid + "\n";
		else
			return "invalid access";
	}
	
	public GaudiAccess copyMe(){
		GaudiAccess na = new GaudiAccess(this.widgetid, (Serializable)this.value, this.write, this.read);
		return na;
	}
}
