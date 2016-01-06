package pt.inescid.gsd.guimin.client.guimin.recorder;

import pt.inescid.gsd.guimin.client.guimin.recorder.struct.ContainerDialog;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.EventLogged;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.WidgetDialog;
import pt.inescid.gsd.guimin.client.recorder.EventMonitor;
import pt.inescid.gsd.guimin.client.recorder.GaudiRecorder;
import pt.inescid.gsd.guimin.common.GaudiException;
import pt.inescid.gsd.guimin.common.GaudiGuiHierarchy;
import pt.inescid.gsd.guimin.common.GuiMinReport;
import pt.inescid.gsd.guimin.common.guimin.Serialize;
import pt.inescid.gsd.guimin.common.model.log.GaudiEvent;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;


@Deprecated
public class Recorder extends GaudiRecorder {



	private EventMonitor eventmonitor;
	private GaudiGuiHierarchy hierarchy;




	private ContainerDialog chead=null,ctail=null;
	private EventLogged currentEvent=null;

	private String lastWidgetClicked = "";

	private boolean started = false;

	public Recorder(String name) {
		super(name);
		this.setEventmonitor(new EventMonitor(this));
		this.hierarchy = new GaudiGuiHierarchy();
		processContainer("0/2");

	}

	public void startRecording(){
		super.startRecording();
		this.hierarchy.processGUI();
	}





	public ContainerDialog getHead() {

		return this.chead;

	}


	private ContainerDialog findContainer(String cid) {

		ContainerDialog cprev = ctail;
		while(cprev!=null) {
			if(cprev.getCid().equals(cid))
				return cprev;
			cprev=cprev.getPrev();
		}
		return null;

	}


	private void processContainer(String cid) {

		System.out.println("[guimin][rec] New Container: "+cid);		
		ContainerDialog newcd = new ContainerDialog(cid,0);
		newcd.setPrev(null);

		if(chead == null)
			chead = newcd;

		if(ctail == null)
			ctail = newcd;
		else{
			newcd.setPrev(ctail);
			ctail.getWtail().getE().get(ctail.getWtail().getE().size()-1).setNext(newcd);
			ctail = newcd;
		}

	}

	private void processWidget(String wid,AWTEvent e) {

		System.out.println("[guimin][rec] New widget: "+wid);		
		EventLogged ev = new EventLogged(new GaudiEvent(e,this.hierarchy),0);
		currentEvent = ev;


		if(ctail.getWhead()==null){ //first widget dialog of current container
			WidgetDialog newwd = new WidgetDialog(wid,0);
			ctail.setWhead(newwd);
			ctail.setWtail(newwd);
		}
		else if(!ctail.getWtail().getWidgetId().equals(wid)){//new widget dialog
			WidgetDialog newwd = new WidgetDialog(wid,0);
			ctail.getWtail().setNext(newwd);
			ctail.setWtail(newwd);
		}

		ctail.getWtail().add(ev);

	}



	int count=0;
	private void count(ContainerDialog cnext) {

		WidgetDialog wnext = cnext.getWhead();

		while(wnext!=null) {		
			for(EventLogged el: wnext.getE()){		
				count=count+1;
				if(el.getNext()!=null){
					count(el.getNext());
				}
			}
			wnext=wnext.getNext();
		}

	}
	
	int ev=0;
	int [][] struct = new int [20][20];
	private void fillStruct(ContainerDialog cnext,int level) {
		
		WidgetDialog wnext = cnext.getWhead();

		while(wnext!=null) {		
			for(EventLogged el: wnext.getE()){
				ev++;
				struct[ev][level]=ev;
				if(el.getNext()!=null){
					fillStruct(el.getNext(),level+1);
				}
			}
			wnext=wnext.getNext();
		}
		
	}
	private void printStruct(ContainerDialog cnext) {
		
		for(int i=0;i <20;i++){
			System.out.println();
			for(int j=0; j<20;j++)
				System.out.print(struct[j][i]);
		}
	}


	public void processEvent(AWTEvent e) {




		if(e.getID() == WindowEvent.WINDOW_ACTIVATED && !this.lastWidgetClicked.equals("")){	


			updateHierarchy(e);




		}
		else if(e.getID() == WindowEvent.WINDOW_OPENED) {

			if(!started){
				count(chead);
				if(count>0)
					started=true;
			}
				
			if(started) {

				updateHierarchy(e);

				String cid = this.hierarchy.findWidgetId((Component)e.getSource());
				if(cid!=null)
					processContainer(cid);
				else
					processContainer("0/2");

			}

		}
		else if(e.getID() == WindowEvent.WINDOW_GAINED_FOCUS) {

			updateHierarchy(e);


			ContainerDialog ccurrent = findContainer(this.hierarchy.findWidgetId((Component)e.getSource()));

			//if(closingFlag){
				closingFlag=false;
				if(ccurrent!=null){
					ctail=ccurrent;
					System.out.println("Returned to the previous container: "+ctail.getCid());
				}
				else
					System.out.println("Tried to return to the previous container but failed");

			//}

		}
		else if (e.getID() == MouseEvent.MOUSE_PRESSED||e.getID()==KeyEvent.KEY_PRESSED){ //TODO: fazer este filtro na mask

			Component current = (Component) e.getSource();
			String wid = this.hierarchy.findWidgetId(current);
			if(wid != null){
				this.lastWidgetClicked = wid;
				processWidget(wid,e);
				count = 0;
				count(chead);
				System.err.println("Event at " + this.lastWidgetClicked+" ;;; Total events in structure: "+count);
			}
			else{
				System.err.println("Could not find component: "+((Component) e.getSource())+" ;; ");
				//TODO: custom interfaces
			}

		}
/*		else if(e.getID() == WindowEvent.WINDOW_DEACTIVATED) {
			//this one is triggered everytime you close or loose focus
			//do nothing
		}*/
		else if(e.getID() == WindowEvent.WINDOW_CLOSING || e.getID() == WindowEvent.WINDOW_CLOSED) {
			System.out.println("closingggg");
			//this works when the user clicks the closing button or a cancel button
			closingFlag=true;//cant do anything here because we do not know which is going to be the new window

		}








	}


	private boolean closingFlag=false;






	public void updateHierarchy(AWTEvent e) {

		if (this.hierarchy.findWidgetId((Component) e.getSource()) == null &&
				this.hierarchy.findWidget(this.lastWidgetClicked  + "/0") == null){

			this.hierarchy.processWidget((Component) e.getSource(), this.lastWidgetClicked + "/0");

		}else if (this.hierarchy.findWidgetId((Component) e.getSource()) == null &&
				this.hierarchy.findWidget(this.lastWidgetClicked + "/0") != null){

			this.hierarchy.updateWidget((Component) e.getSource(), this.lastWidgetClicked + "/0");
		}

	}






	public void recordEventSequence(Exception e) {
		if(!this.recording)
			return;


		this.stopRecording();

		System.out.println("[GaudiListenerRecorder]-Recording run to file...");

		(new File("gaudi")).mkdir();
		(new File("./gaudi/guiminlogs")).mkdir();

		String filename = "./gaudi/guiminlogs/" + this.name + this.timelastevent + ".txt";


		Serialize serialize = new Serialize();
		Serialize.serialize(chead, serialize, 1);
		serialize.setException(new GaudiException(e));
		System.out.println("banhada: \n"+serialize.getContainers()+"\n"+serialize.getWidgets()+"\n"+serialize.getEventlist());
		fillStruct(chead, 0);
		printStruct(chead);
		GuiMinReport report = new GuiMinReport(serialize, new GaudiException(e));

		try{
			OutputStream file = new FileOutputStream(filename);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(report);
			output.close();

			file = new FileOutputStream("./gaudi/guiminlogs/lastguiminlog.txt");
			buffer = new BufferedOutputStream(file);
			output = new ObjectOutputStream(buffer);
			output.writeObject(report);
			output.close();


			e.printStackTrace(new PrintStream("exceptionStackTrace.txt"));


		}catch (Exception e1){
			System.err.println("Error: " + e1.getMessage());
		}

		System.err.println("[GAUDI][Recorder]-run recorded in file: " + filename);


	}








	public void setEventmonitor(EventMonitor eventmonitor) {
		this.eventmonitor = eventmonitor;
	}

	public EventMonitor getEventmonitor() {
		return eventmonitor;
	}

	@Override
	public void printSequence() {
		// TODO Auto-generated method stub

	}































}
