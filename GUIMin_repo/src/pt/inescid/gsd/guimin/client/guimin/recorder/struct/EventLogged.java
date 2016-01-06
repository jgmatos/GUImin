package pt.inescid.gsd.guimin.client.guimin.recorder.struct;

import java.io.Serializable;

import pt.inescid.gsd.guimin.common.model.log.GaudiEvent;

public class EventLogged implements Serializable,ItemLogged{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GaudiEvent ev;
	
	private ContainerDialog next;
	
	private int eventID;
	
	public EventLogged(GaudiEvent ev,int eventID) {
		
		next = null;
		this.ev = ev;
		this.eventID = eventID;
		
	}
	
	public EventLogged(GaudiEvent ev,ContainerDialog next) {
		
		this.next = next;
		this.ev = ev;
		
	}

	
	
	
	
	
	
	
	
	
	
	public GaudiEvent getEv() {
		return ev;
	}

	public void setEv(GaudiEvent ev) {
		this.ev = ev;
	}

	public ContainerDialog getNext() {
		return next;
	}

	public void setNext(ContainerDialog next) {
		this.next = next;
	}

	@Override
	public int getEventId() {
		return eventID;
	}

	
	public void setEventId(int id) {
		
		this.eventID=id;
	}
	
	
	@Override
	public EventLogged clone() {
		
		EventLogged clone = null;
		if(next!=null){
			 clone = new EventLogged(this.ev.clone(), next.clone());
		}
		else{
			 clone = new EventLogged(this.ev.clone(), null);
		}
		return clone;
	}

	
	
	
}
