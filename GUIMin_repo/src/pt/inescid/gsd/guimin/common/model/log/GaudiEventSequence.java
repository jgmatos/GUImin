package pt.inescid.gsd.guimin.common.model.log;



import java.io.Serializable;
import java.util.LinkedList;

public class GaudiEventSequence implements Serializable{

	private static final long serialVersionUID = 908482208617656225L;
	
	private LinkedList<GaudiEvent> eventseq;
	
	public LinkedList<GaudiEvent> getEventseq() {
		return eventseq;
	}
	
	public void setEventseq(LinkedList<GaudiEvent> list){
		this.eventseq = list;
	}
	
	public GaudiEventSequence(){
		this.eventseq = new LinkedList<GaudiEvent>();
	}
	
	public void addEvent(GaudiEvent e){
		this.eventseq.add(e);
	}
	
	public GaudiEvent getEvent(){
		return this.eventseq.removeFirst();
	}
	
	public String toString(){
		String res = "";
		for(GaudiEvent e : this.eventseq){
			res += e.toString();
			res += "\n";
		}
		return res;
	}
	
}
