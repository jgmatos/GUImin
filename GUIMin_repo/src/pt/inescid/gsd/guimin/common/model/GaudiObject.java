package pt.inescid.gsd.guimin.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class GaudiObject implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;
	private GaudiObject parent;
	private LinkedList<GaudiObject> children;
	private HashMap<String, Object> properties;
	private LinkedList<GaudiEventModel> eventlist;
	
	public GaudiObject(String id){
		this.id = id;
		this.parent = null;
		this.children = new LinkedList<GaudiObject>();
		this.properties = new HashMap<String, Object>();
		this.eventlist = new LinkedList<GaudiEventModel>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GaudiObject getParent() {
		return parent;
	}

	public void setParent(GaudiObject parent) {
		this.parent = parent;
	}

	public LinkedList<GaudiObject> getChildren() {
		return children;
	}

	public void setChildren(LinkedList<GaudiObject> children) {
		this.children = children;
	}
	
	public void addChildren(GaudiObject o){
		this.children.add(o);
	}

	public HashMap<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, Object> properties) {
		this.properties = properties;
	}
	
	public void addProperties(String s, Object value){
		this.properties.put(s, value);
	}
	
	public LinkedList<GaudiEventModel> getEventList() {
		return eventlist;
	}

	public void setProperties(LinkedList<GaudiEventModel> eventlist) {
		this.eventlist = eventlist;
	}
	
	public void addEvent(GaudiEventModel e){
		this.eventlist.add(e);
	}
	
	public void printMe(int space){	
		
		String s = addSpace("", space);
		s += "<component>\n";
		s = addSpace(s, space);
		s += "  <id> " + this.id + "</id>\n";
		s = addSpace(s, space);
		s += "  <properties>\n";
		for(Entry<String, Object> o : this.properties.entrySet()){
			s = addSpace(s, space);
			s += "    <"+ o.getKey() +"> " + o.getValue().toString() + " </"+ o.getKey() +">\n";
		}
		s = addSpace(s, space);
		s += "  </properties>\n";
		s = addSpace(s, space);
		s += "  <eventlist>";
		System.out.println(s);
		s = "";
		for(GaudiEventModel o : this.eventlist){
			o.printMe(space + 4);
		}
		s = addSpace(s, space);
		s += "  </eventlist>\n";
		s = addSpace(s, space);
		s += "  <children>";
		System.out.println(s);
		s = "";
		for(GaudiObject o : this.children){
			o.printMe(space + 4);
		}
		s = addSpace(s, space);
		s += "  </children>\n";
		s = addSpace(s, space);
		s += "</component>";
		System.out.println(s);
		
	}
	
	public String addSpace(String s, int i){
		for(int j = 0; j < i; j++){
			s += " ";
		}
		return s;
	}
	
}
