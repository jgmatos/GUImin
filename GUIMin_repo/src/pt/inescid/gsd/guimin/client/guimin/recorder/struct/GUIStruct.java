package pt.inescid.gsd.guimin.client.guimin.recorder.struct;

import pt.inescid.gsd.guimin.common.GaudiException;
import pt.inescid.gsd.guimin.common.GuiMinReport;
import pt.inescid.gsd.guimin.common.GuiMinReportExperimental;
import pt.inescid.gsd.guimin.common.guimin.Serialize;
import pt.inescid.gsd.guimin.common.model.log.GaudiEvent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class GUIStruct {



	private ContainerDialog chead=null,ctail=null;
	private int size=0, count=0, level=0;

	private int eventID=0;

	public GUIStruct() {

	}

	public GUIStruct(ContainerDialog chead) {

		this.chead = chead;
		//TODO: ctail

	}


	public void addContainerDialog(String cid) {

		try {

			ContainerDialog newcd = new ContainerDialog(cid,eventID);
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
			eventID++;//container

		}catch(Throwable t) {t.printStackTrace();}

	}


	public void addMainWindow() {
		addContainerDialog("0/2");
	}



	public void addEvent(GaudiEvent e,String wid) {

		EventLogged ev = new EventLogged(e,eventID);

		if(ctail.getWhead()==null){ //first widget dialog of current container
			WidgetDialog newwd = new WidgetDialog(wid,eventID);
			ctail.setWhead(newwd);
			ctail.setWtail(newwd);
			eventID++;//widget
		}
		else if(!ctail.getWtail().getWidgetId().equals(wid)){//new widget dialog
			WidgetDialog newwd = new WidgetDialog(wid,eventID);
			ctail.getWtail().setNext(newwd);
			ctail.setWtail(newwd);
			eventID++;//widget
		}

		ctail.getWtail().add(ev);
		eventID++;//event
		size++;




	}




	public ContainerDialog findContainer(String cid) {

		ContainerDialog cprev = ctail;
		while(cprev!=null) {
			
			if(cprev.getCid().equals(cid))
				return cprev;
			cprev=cprev.getPrev();
		}
		return null;

	}


	public void containerBacktrack(String cid) {

		ContainerDialog ccurrent = findContainer(cid);
		if(ccurrent!=null)
			ctail=ccurrent;
		else
			backtractToMain();//TODO: do this better
	}

	public void backtractToMain() {

		ContainerDialog ccurrent = findContainer("0/0");
		if(ccurrent!=null)
			ctail=ccurrent;

	}



	/**
	 * Returns the count of addEvent invocations 
	 * 
	 * @return
	 */
	public int size() {
		return size;
	}


	/**
	 * Iterates the structure and counts the number of events
	 * Used mostly for debug
	 * @return The event count
	 */
	public int count() {

		count = 0;
		level = 1;
		count(chead);
		return count;

	}


	private void count(ContainerDialog cnext) {

		WidgetDialog wnext = cnext.getWhead();
		while(wnext!=null) {	System.out.println("W");
			for(EventLogged el: wnext.getE()){	
				count=count+1;
				if(el.getNext()!=null){
					level = level + 1;
					count(el.getNext());
				}
			}
			wnext=wnext.getNext();
		}

	}

	public int getLevel() {

		return level;

	}


	int ev=0;
	int [][] struct ;
	private void fillStruct(ContainerDialog cnext,int level) {

		WidgetDialog wnext = cnext.getWhead();

		while(wnext!=null) {		
			for(EventLogged el: wnext.getE()){
				struct[ev][level]=ev;System.out.println("("+ev+","+level+")="+ev);
				ev=ev+1;
				if(el.getNext()!=null){
					fillStruct(el.getNext(),level+1);
				}
			}
			wnext=wnext.getNext();
		}

	}
	public void printStruct(ContainerDialog cnext) {
		count();
		struct = new int [count+1][level];
		System.out.println("------------------------------------------------------------------------------------------------------------------ Levels: "+level+" - Events: "+count);
		fillStruct(chead, 0);
		for(int i=0;i <level;i++){
			System.out.println();
			for(int j=0; j<size+1;j++){
				if(struct[j][i]>0)
					System.out.print(struct[j][i]+" ");
				else
					System.out.print(" ");
			}
		}
		System.out.println("------------------------------------------------------------------------------------------------------------------");
	}





	@Deprecated
	public void log(Exception e,String filename) {

		System.out.println("[GaudiListenerRecorder]-Recording run to file...");

		(new File("gaudi")).mkdir();
		(new File("./gaudi/guiminlogs")).mkdir();




		Serialize serialize = new Serialize();
		Serialize.serialize(chead, serialize, 1);
		serialize.setException(new GaudiException(e));
		System.out.println("banhada: \n"+serialize.getContainers()+"\n"+serialize.getWidgets()+"\n"+serialize.getEventlist());
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
			System.err.println("[GAUDI][Recorder]-run recorded in file: " + filename);

		}catch (Exception e1){
			System.err.println("Error: " + e1.getMessage());
		}





	}




	public void experimentallog(Exception e,String filename) {

		System.out.println("[GaudiListenerRecorder]-Recording run to file...");

		(new File("gaudi")).mkdir();
		(new File("./gaudi/guiminlogs")).mkdir();




		GuiMinReportExperimental report = new GuiMinReportExperimental(chead, new GaudiException(e));

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
			System.err.println("[GAUDI][Recorder]-run recorded in file: " + filename);

		}catch (Exception e1){
			System.err.println("Error: " + e1.getMessage());
		}





	}



	public static GuiMinReportExperimental extract(File location) {

		try {


			InputStream fis;

			fis = new FileInputStream(location);
			InputStream buffer = new BufferedInputStream(fis);
			ObjectInput input = new ObjectInputStream (buffer);

			GuiMinReportExperimental report=null;
			report= (GuiMinReportExperimental) input.readObject();



			input.close();




			return report;

		}
		catch(Throwable t) {
			t.printStackTrace();
			System.exit(-1);
			return null;
		}


	}










}
