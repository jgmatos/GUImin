package pt.inescid.gsd.guimin.client.guimin.recorder.struct;

import java.io.Serializable;
import java.util.ArrayList;

public class WidgetDialog implements Serializable, ItemLogged{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String widgetId=null;
	
	private ArrayList<EventLogged> E = new ArrayList<EventLogged>();
	
	private WidgetDialog next=null;
	
	private int eventID;
	
	public WidgetDialog(String id,int eventID) {
		this.widgetId=id;
		this.eventID=eventID;
	}


	public String getWidgetId() {
		return widgetId;
	}


	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}


	public ArrayList<EventLogged> getE() {
		return E;
	}


	public void setE(ArrayList<EventLogged> e) {
		E = e;
	}


	public WidgetDialog getNext() {
		return next;
	}


	public void setNext(WidgetDialog next) {
		this.next = next;
	}
	
	
	
	
	public void add(EventLogged el) {
		E.add(el);
	}


	@Override
	public int getEventId() {
		return eventID;
	}
	
	



	public void setEventID(int eventID) {
		this.eventID = eventID;
	}


	@Override
	public WidgetDialog clone() {
		
		
		WidgetDialog clone = new WidgetDialog("clone", -1);
		
		ArrayList<EventLogged> cloneE = new ArrayList<EventLogged>();
		for(EventLogged el: E)
			cloneE.add(el.clone());
		
		clone.setE(cloneE);
		
		if(next!=null)
			clone.setNext(next.clone());
		else
			clone.setNext(null);
		
		return clone;
	}
	
	
	
}
