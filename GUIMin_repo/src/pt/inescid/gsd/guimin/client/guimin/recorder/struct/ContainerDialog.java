package pt.inescid.gsd.guimin.client.guimin.recorder.struct;

import java.io.Serializable;

public class ContainerDialog implements Serializable,ItemLogged{



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private WidgetDialog whead,wtail;
	
	private ContainerDialog prev;
	
	
	private String cid;
	
	
	private int eventID;
	
	
	public ContainerDialog(String cid, int eventID) {
		
		whead = null;
		wtail = null;
		prev = null;
		this.cid=cid;
		this.eventID=eventID;
		
	}

	
	
	public void add(WidgetDialog wd) {
		
		if(whead == null)
			whead = wd;
			
		if(wtail == null) {
			wtail = wd;
		}
		else {
			wtail.setNext(wd);
			wtail = wd;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public String getCid() {
		return cid;
	}



	public void setCid(String cid) {
		this.cid = cid;
	}



	public WidgetDialog getWhead() {
		return whead;
	}


	public void setWhead(WidgetDialog whead) {
		this.whead = whead;
	}


	public WidgetDialog getWtail() {
		return wtail;
	}


	public void setWtail(WidgetDialog wtail) {
		this.wtail = wtail;
	}


	public ContainerDialog getPrev() {
		return prev;
	}


	public void setPrev(ContainerDialog prev) {
		this.prev = prev;
	}





	public void setEventID(int eventID) {
		this.eventID = eventID;
	}



	@Override
	public int getEventId() {
		return eventID;
	}
	
	

	
	@Override
	public ContainerDialog clone() {
		
		ContainerDialog clone=new ContainerDialog("clone", -1);
		
		clone.setPrev(prev);
		if(whead!=null)
			clone.setWhead(whead.clone());
		else
			clone.setWhead(null);
		
/*		if(wtail!=null)
			clone.setWtail(wtail.clone());
		else
			clone.setWtail(null);*/

		
		return clone;
		
	}
	
	
}
