package pt.inescid.gsd.guimin.common.model.log;

import java.io.Serializable;
import java.util.LinkedList;

public class GaudiChildlessSequence implements Serializable, Comparable<GaudiChildlessSequence>{

	private static final long serialVersionUID = 1L;

	private LinkedList<GaudiChildless> childlessseq;
	
	public LinkedList<GaudiChildless> getchildlessseq() {
		return childlessseq;
	}
	
	public void setchildlessseq(LinkedList<GaudiChildless> list){
		this.childlessseq = list;
	}
	
	public GaudiChildlessSequence(){
		this.childlessseq = new LinkedList<GaudiChildless>();
	}
	
	public GaudiChildlessSequence(LinkedList<GaudiChildless>o){
		this.childlessseq = new LinkedList<GaudiChildless>(o);
	}
	
	
	
	public void add(GaudiChildless gc) {
		this.childlessseq.add(gc);
	}
	
	
	public void addListener(GaudiChildless e){
		this.childlessseq.add(e);
	}
	
	//jmatos
	public void addToHead(GaudiChildless e) {
		this.childlessseq.addFirst(e);
	}
	
	public GaudiChildless getListener(){
		return this.childlessseq.removeFirst();
	}
	
	public String toString(){
		String res = "";
		for(GaudiChildless e : this.childlessseq){
			res += e.toString();
		}
		return res;
	}
	
	public GaudiChildlessSequence copyMe(){
		GaudiChildlessSequence nseq = new GaudiChildlessSequence();
		
		for(GaudiChildless gl : this.childlessseq){
			nseq.childlessseq.addLast(gl.copyMe());
		}
		
		return nseq;
	}

	@Override
	public int compareTo(GaudiChildlessSequence o) {
		if (this.childlessseq.size() > o.getchildlessseq().size())
			return 1;
		else if (this.childlessseq.size() < o.getchildlessseq().size())
			return -1;
		else
			return 0;
	}


}
