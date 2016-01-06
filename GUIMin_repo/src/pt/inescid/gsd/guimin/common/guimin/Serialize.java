package pt.inescid.gsd.guimin.common.guimin;

import pt.inescid.gsd.guimin.client.guimin.recorder.struct.ContainerDialog;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.EventLogged;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.WidgetDialog;
import pt.inescid.gsd.guimin.common.GaudiException;
import pt.inescid.gsd.guimin.common.GuiMinReport;
import pt.inescid.gsd.guimin.common.model.log.GaudiEvent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@Deprecated
public class Serialize implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<Integer> pointersToContainers;

	private ArrayList<GaudiEvent> eventlist; 

	private ArrayList<Integer> widgets;
	private HashMap<Integer,String> intToWID;

	private ArrayList<Integer> containers;
	private HashMap<Integer,String> intToCID;

	private GaudiException exception;



	public Serialize() {

		pointersToContainers = new ArrayList<Integer>();
		eventlist = new ArrayList<GaudiEvent>();
		widgets = new ArrayList<Integer>();
		containers = new ArrayList<Integer>();
		intToWID = new HashMap<Integer, String>();
		intToCID = new HashMap<Integer, String>();
		exception = null;

	}

















	public void setException(GaudiException ge) {
		this.exception = ge;
	}

	public GaudiException getException() {

		return exception;

	}







	public ArrayList<Integer> getPointersToContainers() {
		return pointersToContainers;
	}



	public void setPointersToContainers(ArrayList<Integer> pointersToContainers) {
		this.pointersToContainers = pointersToContainers;
	}



	public ArrayList<GaudiEvent> getEventlist() {
		return eventlist;
	}



	public void setEventlist(ArrayList<GaudiEvent> eventlist) {
		this.eventlist = eventlist;
	}



	public ArrayList<Integer> getWidgets() {
		return widgets;
	}



	public void setWidgets(ArrayList<Integer> widgets) {
		this.widgets = widgets;
	}



	public ArrayList<Integer> getContainers() {
		return containers;
	}



	public void setContainers(ArrayList<Integer> containers) {
		this.containers = containers;
	}








	public HashMap<Integer, String> getIntToWID() {
		return intToWID;
	}



	public void setIntToWID(HashMap<Integer, String> intToWID) {
		this.intToWID = intToWID;
	}



	public HashMap<Integer, String> getIntToCID() {
		return intToCID;
	}






	public void setIntToCID(HashMap<Integer, String> intToCID) {
		this.intToCID = intToCID;
	}

















	public void add(GaudiEvent ev,int container,String c,int widget,String w,int pointer) {

		eventlist.add(ev);
		containers.add(container);
		widgets.add(widget);
		pointersToContainers.add(pointer);
		intToWID.put(widget, w);
		intToCID.put(container, c);
		
	}
	
	static int wid=1, levelup=1;
	public static void serialize(ContainerDialog cnext,Serialize serialize,int cid) {
		
		if(serialize==null)
			serialize = new Serialize();


		WidgetDialog wnext = cnext.getWhead();
		while(wnext!=null) {
			for(EventLogged ev: wnext.getE()){
				if(ev.getNext()!=null){
					serialize.add(ev.getEv(), cid,cnext.getCid(), wid,wnext.getWidgetId(), cid+1);
					wid = wid+1;
					levelup = levelup+1;
					serialize(ev.getNext(), serialize, levelup);
				}
				else{
					serialize.add(ev.getEv(), cid,cnext.getCid(), wid,wnext.getWidgetId(), -1);
				}
			}
			wnext = wnext.getNext();
			wid = wid+1;
		}



	}


/*	public static ContainerDialog deSerialize(Serialize serialize) {


		ArrayList<EventLogged> loggedEv = new ArrayList<EventLogged>();

		//first, convert GE to EL
		for(GaudiEvent ge: serialize.getEventlist()) 
			loggedEv.add(new EventLogged(ge));

		//then Convert WID to WD
		HashMap<Integer, WidgetDialog> loggedW = new HashMap<Integer, WidgetDialog>();
		for(Integer w: serialize.getWidgets())
			if(!loggedW.containsKey(w))
				loggedW.put(w, new WidgetDialog(serialize.getIntToWID().get(w)));

		
		//convert CID to CD
		HashMap<Integer,ContainerDialog> loggedC = new HashMap<Integer, ContainerDialog>();
		for(Integer c: serialize.getContainers())
			if(!loggedC.containsKey(c))
				loggedC.put(c, new ContainerDialog(serialize.getIntToCID().get(c)));

		
		
		//Put each EL in the respective WD
		for(int i=0; i< serialize.getWidgets().size(); i++)
			loggedW.get(serialize.getWidgets().get(i)).add(loggedEv.get(i));


		//Put each WD in the respective CD
		for(int i=0; i<serialize.getContainers().size(); i++)
			loggedC.get(serialize.getContainers().get(i)).add(loggedW.get(serialize.getWidgets().get(i)));

		//put each CD in the respective EL.next
		for(int i=0; i<loggedEv.size(); i++)
			if(serialize.getPointersToContainers().get(i)>=0)
				loggedEv.get(i).setNext(loggedC.get(serialize.getPointersToContainers().get(i)));

		return loggedC.get(new Integer(1)); //return chead

	}*/


	public static Serialize extract(File location) {

		try {

			Serialize serialize = new Serialize();

			InputStream fis;

			fis = new FileInputStream(location);
			InputStream buffer = new BufferedInputStream(fis);
			ObjectInput input = new ObjectInputStream (buffer);

			GuiMinReport report = (GuiMinReport) input.readObject();
			serialize = report.getSerialize();
			input.close();




			return serialize;

		}
		catch(Throwable t) {
			t.printStackTrace();
			return null;
		}


	}




}
