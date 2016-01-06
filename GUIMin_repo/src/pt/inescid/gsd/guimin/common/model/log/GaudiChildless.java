package pt.inescid.gsd.guimin.common.model.log;

import java.io.Serializable;
import java.util.ArrayList;

public class GaudiChildless implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2108593784157802747L;

	
	
	private String widgetId;
	
	
	public ArrayList<GaudiJTextField> textfields;
	
	
	public ArrayList<ChildlessTextfield> ctextfields;
	
	
	//under test: wizard
	public ArrayList<GaudiChildless> steps;
	public boolean isWizard=false;
	
	
	
	
	public GaudiChildless(String widgetId) {
		
		this.widgetId = widgetId;
		textfields = new ArrayList<GaudiJTextField>();
		steps = new ArrayList<GaudiChildless>();
		ctextfields = new ArrayList<ChildlessTextfield>();
	}




	public String getWidgetId() {
		return widgetId;
	}




	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}

	
	@Override
	public String toString() {
		
		return widgetId;
		
	}
	
	
	@Override
	public boolean equals(Object other) {
		
		
		if(other instanceof GaudiChildless)
			if(((GaudiChildless)other).toString().equals(widgetId))
				return true;
		
		return false;
	
	}
	
	
	
	@Override
	public int hashCode() {
		
		return widgetId.hashCode();
		
	}
	
	
	
	
	public GaudiChildless copyMe(){
		GaudiChildless nseq = new GaudiChildless(this.widgetId);
		
		return nseq;
	}
	
	
	
	
	
}
