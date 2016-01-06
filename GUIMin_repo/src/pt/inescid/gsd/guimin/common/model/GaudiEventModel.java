package pt.inescid.gsd.guimin.common.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map.Entry;

public class GaudiEventModel implements Serializable{

	private static final long serialVersionUID = 1L;

	final static public String mouseEvent = "mouseevent";
	final static public String keyEvent = "keyevent";
	final static public String windowOpenEvent = "openevent";
	
	private String widgetid;
	private String eventtype;
	private LinkedList<String> listeners;
	
	public GaudiEventModel(String id, String eventype){
		this.widgetid = id;
		this.eventtype = eventype;
		this.setListeners(new LinkedList<String>());
	}

	public String getWidgetid() {
		return widgetid;
	}

	public void setWidgetid(String widgetid) {
		this.widgetid = widgetid;
	}

	public String getEventtype() {
		return eventtype;
	}

	public void setEventtype(String eventtype) {
		this.eventtype = eventtype;
	}

	public void setListeners(LinkedList<String> list) {
		this.listeners = list;
	}

	public LinkedList<String> getListeners() {
		return listeners;
	}
	
public void printMe(int space){	
		
		String s = addSpace("", space);
		s += "<event>\n";
		s = addSpace(s, space);
		s += "  <type> " + this.eventtype + "</type>\n";
		s = addSpace(s, space);
		s += "  <listeners>\n";
		for(String o : this.listeners){
			s = addSpace(s, space);
			s += "    " + o +"\n";
		}
		s = addSpace(s, space);
		s += "  </listeners>\n";
		s = addSpace(s, space);
		s += "</event>";
		System.out.println(s);
		
	}
	
	public String addSpace(String s, int i){
		for(int j = 0; j < i; j++){
			s += " ";
		}
		return s;
	}
	
	
}
