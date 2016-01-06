package pt.inescid.gsd.guimin.common.model.log;

import java.io.Serializable;
import java.util.LinkedList;

public class GaudiListenerSequence implements Serializable, Comparable<GaudiListenerSequence>{

	private static final long serialVersionUID = 1L;

	private LinkedList<GaudiListener> listenerseq;
	
	public LinkedList<GaudiListener> getListenerseq() {
		return listenerseq;
	}
	
	public void setListenerseq(LinkedList<GaudiListener> list){
		this.listenerseq = list;
	}
	
	public GaudiListenerSequence(){
		this.listenerseq = new LinkedList<GaudiListener>();
	}
	
	public GaudiListenerSequence(LinkedList<GaudiListener>o){
		this.listenerseq = new LinkedList<GaudiListener>(o);
	}
	
	
	public void addListener(GaudiListener e){
		this.listenerseq.add(e);
	}
	
	//jmatos
	public void addToHead(GaudiListener e) {
		this.listenerseq.addFirst(e);
	}
	
	public GaudiListener getListener(){
		return this.listenerseq.removeFirst();
	}
	
	public String toString(){
		String res = "";
		for(GaudiListener e : this.listenerseq){
			res += e.toString();
		}
		return res;
	}
	
	public GaudiListenerSequence copyMe(){
		GaudiListenerSequence nseq = new GaudiListenerSequence();
		
		for(GaudiListener gl : this.listenerseq){
			nseq.listenerseq.addLast(gl.copyMe());
		}
		
		return nseq;
	}

	@Override
	public int compareTo(GaudiListenerSequence o) {
		if (this.listenerseq.size() > o.getListenerseq().size())
			return 1;
		else if (this.listenerseq.size() < o.getListenerseq().size())
			return -1;
		else
			return 0;
	}


}
