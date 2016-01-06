package pt.inescid.gsd.guimin.client.guimin.recorder;

import pt.inescid.gsd.guimin.client.guimin.recorder.struct.GUIStruct;
import pt.inescid.gsd.guimin.client.recorder.EventMonitor;
import pt.inescid.gsd.guimin.client.recorder.GaudiRecorder;
import pt.inescid.gsd.guimin.common.GaudiGuiHierarchy;
import pt.inescid.gsd.guimin.common.model.log.GaudiEvent;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;


public class GMINRecorder extends GaudiRecorder {



	private EventMonitor eventmonitor;
	private GaudiGuiHierarchy hierarchy;


	private GUIStruct guistruct;


	private String lastWidgetClicked = "";


	public GMINRecorder(String name) {
		super(name);
		this.setEventmonitor(new EventMonitor(this));
		this.hierarchy = new GaudiGuiHierarchy();
		guistruct = new GUIStruct();
		guistruct.addMainWindow();
	}

	public void startRecording(){
		super.startRecording();
		this.hierarchy.processGUI();
	}






	public void processEvent(AWTEvent e) {




		if(e.getID() == WindowEvent.WINDOW_ACTIVATED && !this.lastWidgetClicked.equals("")){	


			updateHierarchy(e);




		}
		else if(e.getID() == WindowEvent.WINDOW_OPENED) {// a new window was opened

		
				
			if(this.guistruct.size()>0) {

			//	updateHierarchy(e);

				String cid = this.hierarchy.findWidgetId((Component)e.getSource());
				if(cid!=null)
					guistruct.addContainerDialog(cid);
				else
					guistruct.addContainerDialog("0/2");//TODO: remove this

			}

		}
		else if(e.getID() == WindowEvent.WINDOW_GAINED_FOCUS) { // the current window was closed and we are back in one of the previous windows
//TODO: this works for some and does not work for others!
			//updateHierarchy(e);
			System.out.println("aquiiiiiii fodasse ");
			
		//	guistruct.backtractToMain();

			String cid = this.hierarchy.findWidgetId((Component)e.getSource());
			if(cid!=null){
				guistruct.containerBacktrack(cid);
				System.out.println("Returned to the previous container: "+cid);
			}
			else{
				guistruct.backtractToMain();//TODO: fix this when it happens
				System.out.println("Tried to return to the previous container but failed");
			}


		}
		else if (e.getID() == MouseEvent.MOUSE_PRESSED||e.getID()==KeyEvent.KEY_PRESSED){ //TODO: fazer este filtro na mask

			Component current = (Component) e.getSource();
			String wid = this.hierarchy.findWidgetId(current);
			if(wid != null){
				this.lastWidgetClicked = wid;
				guistruct.addEvent(new GaudiEvent(e, hierarchy), wid);
				//System.err.println("Event at " + this.lastWidgetClicked+" ;;; Added events: "+guistruct.size());
				System.err.println("Event at " + this.lastWidgetClicked+" ;;; Added events: "+guistruct.size()+" == "+guistruct.count());
			}
			else{
				System.err.println("Could not find component: "+((Component) e.getSource())+" ;; ");
				//TODO: custom interfaces
			}

		}
/*		else if(e.getID() == WindowEvent.WINDOW_DEACTIVATED) {
			//this one is triggered everytime you close or loose focus
			//do nothing
		}
		else if(e.getID() == WindowEvent.WINDOW_CLOSING || e.getID() == WindowEvent.WINDOW_CLOSED) {
			//this works when the user clicks the closing button or a cancel button
			//cant do anything here because we do not know which is going to be the new window

		}*/








	}








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

		String filename = "./gaudi/guiminlogs/" + this.name + this.timelastevent + ".txt";
		//guistruct.log(e,filename);
		guistruct.experimentallog(e, filename);

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
