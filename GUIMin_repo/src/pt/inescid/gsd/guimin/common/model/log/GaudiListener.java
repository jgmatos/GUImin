package pt.inescid.gsd.guimin.common.model.log;

import java.io.Serializable;
import java.util.LinkedList;

public class GaudiListener implements Serializable{

	private static final long serialVersionUID = 1L;

	private String widgetid;
	private String listenerid;
	private LinkedList<GaudiAccess> variableAccess;

	
	private LinkedList<String> buttonclicks;
	
	
	//under test
	private boolean hasEnded;
	
	public GaudiListener(String wid, String lid){
		this.setWidgetid(wid);
		this.setListenerid(lid);
		this.variableAccess = new LinkedList<GaudiAccess>();
		buttonclicks = new LinkedList<String>();
		hasEnded = false;
	}
	
	public void addAccess(String wid, Serializable value, boolean write, boolean read){
		GaudiAccess newaccess = new GaudiAccess(wid, value, write, read);
		this.variableAccess.add(newaccess);
	}

	public void setWidgetid(String widgetid) {
		this.widgetid = widgetid;
	}

	public String getWidgetid() {
		return widgetid;
	}

	public void setListenerid(String listenerid) {
		this.listenerid = listenerid;
	}

	public String getListenerid() {
		return listenerid;
	}
	
	public LinkedList<GaudiAccess> getAccesses(){
		return this.variableAccess;
	}
	
	
	
	
	public void addButtonClick(String id) {
		if(buttonclicks.size()==0||!buttonclicks.get(buttonclicks.size()-1).equals(id))
			buttonclicks.add(id);
	}
	
	public LinkedList<String> getButtonClicks(){
		return this.buttonclicks;
	}
	
	public void clearButtonClick(String id) {
		
		LinkedList<String> cleared = new LinkedList<String>();
		for(String s: this.buttonclicks){
			if(!s.equals(id) && !s.equals(id+"/0"))
				cleared.add(s);
		}
		this.buttonclicks = cleared;
	}
	
	
	public String toString(){
		String res = "";
		res += "-Invoked Listener "+this.listenerid+" at " + this.widgetid +"\n";
		if(!this.variableAccess.isEmpty()){
			for(GaudiAccess a : this.variableAccess){
				res += "\t" + a.toString();
			}
		}
		return res;
	}
	
	public GaudiListener copyMe(){
		GaudiListener nseq = new GaudiListener(this.widgetid, this.listenerid);
		
		for(GaudiAccess ga : this.variableAccess){
			nseq.variableAccess.addLast(ga.copyMe());
		}
		
		return nseq;
	}

	
	
	public boolean ended() {
		return hasEnded;
	}
	
	public void end() {
		
		this.hasEnded = true;
	}
	
}
