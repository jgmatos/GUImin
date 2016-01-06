package pt.inescid.gsd.guimin.client.guimin.minimize;

import pt.inescid.gsd.guimin.client.guimin.recorder.struct.ContainerDialog;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.EventLogged;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.ItemLogged;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.WidgetDialog;
import pt.inescid.gsd.guimin.common.GuiMinReportExperimental;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Anonymize {




	private static ContainerDialog chead;



	public Anonymize(String SUT,GuiMinReportExperimental report) {

		Anonymize.chead = report.gethead();
		Min.setHead(report.gethead());
		Min.setSUT(SUT);
		Min.setF(report.getGaudiException());

		pump(80);
	//	pump2(800);
		createSandbox(report);

	}



	public void start() {

		DialogMinimal();

	}




	public static void DialogMinimal() {

		int count = 0,countprime=0;

		//CMin();
		WMin();
		WEMin();

		do {
			count = count();
			CMin();
			WMin();
			countprime = count();
		} while (countprime<count);
		Log.log("Finish");
		Log.flush();
		log();

	}




	public void ddmin() {

		ArrayList<ItemLogged> I_u = new ArrayList<ItemLogged>();
		GETAll(chead, I_u);//all events
		Min.ddmin(chead, I_u.size(),I_u);

	}












	/**
	 * GET a list with every container at the next level
	 * 
	 * @param C the current containers
	 * @return	A list with every container connected directly with the containers at C
	 */
	public static ArrayList<ContainerDialog> GET(ArrayList<ContainerDialog> C) {

		ArrayList<ContainerDialog> Cprime = new ArrayList<ContainerDialog>();

		for(ContainerDialog c_i: C) { // check each container c_i at C
			WidgetDialog w_i = c_i.getWhead(); 
			while(w_i!=null){//iterate the widgets of c_i
				for(EventLogged e_i: w_i.getE())
					if(e_i.getNext()!=null)//for all events e_i that trigger a new container
						Cprime.add(e_i.getNext()); //add such container to the new list

				w_i=w_i.getNext();
			}
		}

		return Cprime; // A list with every container connected directly with the containers at C

	}


	/**
	 * GET a list with every container opening events
	 * 
	 * @param C the list of containers
	 * @return	A list with every container opening events within C
	 */
	public static ArrayList<ItemLogged> GETe(ArrayList<ContainerDialog> C) {

		ArrayList<ItemLogged> e_ci = new ArrayList<ItemLogged>();

		for(ContainerDialog c_i: C) { // check each container c_i at C
			WidgetDialog w_i = c_i.getWhead(); 
			while(w_i!=null){//iterate the widgets of c_i
				for(EventLogged e_i: w_i.getE())
					if(e_i.getNext()!=null)//for all events e_i that trigger a new container
						e_ci.add(e_i); //add e_i


				w_i=w_i.getNext();
			}
		}

		return e_ci; // a list with every container opening events in C

	}

	/**
	 * GET a list with every widget within a list of containers
	 * 
	 * @param C the list of containers
	 * @return	A list with every widget within C
	 */
	public static ArrayList<ItemLogged> GETw(ArrayList<ContainerDialog> C) {

		ArrayList<ItemLogged> e_w = new ArrayList<ItemLogged>();

		for(ContainerDialog c_i: C) { // check each container c_i at C
			WidgetDialog w_i = c_i.getWhead(); 
			while(w_i!=null&&!w_i.getE().isEmpty()){//iterate the widgets of c_i
				e_w.add(w_i); //add w_i
				w_i=w_i.getNext();
			}//TODO: for now our way of deleting widgets is simply to ignore the ones that are empty. Correct this later
		}

		return e_w; // a list with every widget dialogs in C

	}


	public static void GETAll(ContainerDialog cnext,ArrayList<ItemLogged> e_ci) {

		if(e_ci == null)
			e_ci= new ArrayList<ItemLogged>();

		WidgetDialog w_i = cnext.getWhead(); 
		while(w_i!=null){//iterate the widgets of c_i
			for(EventLogged e_i: w_i.getE()){
				e_ci.add(e_i);
				if(e_i.getNext()!=null)//for all events e_i that trigger a new container
					GETAll(e_i.getNext(), e_ci);
			}


			w_i=w_i.getNext();
		}

	}


	public static void CMin() {

		Log.log("\n\n\n\n\n\n\n Starting CMIN phase...\n\n\n\n\n\n\n ");
		Min.setHead(chead);

		ArrayList<ItemLogged> L = new ArrayList<ItemLogged>();
		ArrayList<ContainerDialog> C = new ArrayList<ContainerDialog>();
		C.add(chead);
		do {

			L = GETe(C);//forall e_i in C: ei.next != null
			Min.LazyMin(chead, L);
			C = GET(C);//move on to next level

		} while (!C.isEmpty());

	}


	public static void WMin() {
		Log.log("\n\n\n\n\n\n\n Starting WMIN phase...\n\n\n\n\n\n\n");
		Min.setHead(chead);

		ArrayList<ItemLogged> L = new ArrayList<ItemLogged>();
		ArrayList<ContainerDialog> C = new ArrayList<ContainerDialog>();
		C.add(chead);
		do {

			L = GETw(C);//forall w_i in C
			Min.LazyMin(chead, L);
			C = GET(C);//move on to next level

		} while (!C.isEmpty());

	}


	public static void WEMin() {
		Log.log("\n\n\n\n\n\n\n Starting WEMIN phase...\n\n\n\n\n\n\n");
		Min.setHead(chead);

		ArrayList<ItemLogged> L = new ArrayList<ItemLogged>();
		ArrayList<ContainerDialog> C = new ArrayList<ContainerDialog>();
		C.add(chead);
		ArrayList<ItemLogged> Wprime = new ArrayList<ItemLogged>();
		do {

			Wprime = GETw(C);//forall w_i in C
			for(ItemLogged wi: Wprime) {
				if(((WidgetDialog)wi).getE().size()>1) // this is our way of avoiding repeated tests.
					L.addAll(((WidgetDialog)wi).getE());
				Min.LazyMin(chead, L);
			}

			C = GET(C);//move on to next level

		} while (!C.isEmpty());

	}










	/**
	 * Iterates the structure and counts the number of events
	 * Used mostly for debug
	 * @return The event count
	 */
	public static int count() {

		count = 0;
		count(chead);
		return count;

	}
	static int count=0;

	private static void count(ContainerDialog cnext) {

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













	private static void log() {

		System.out.println("[GUIMIN][Anonymized]-Recording run to file...");

		File f = new File("gaudi/");
		if(!f.exists())
			f.mkdir();
		f = new File("gaudi/guiminlogs/");
		if(!f.exists())
			f.mkdir();
		f = new File("gaudi/guiminlogs/dialogminimal/");
		if(!f.exists())
			f.mkdir();

		f = new File("gaudi/guiminlogs/dialogminimal/dialogMinimal"+System.currentTimeMillis()+".txt");



		GuiMinReportExperimental report = new GuiMinReportExperimental(chead, null);

		try{
			OutputStream file = new FileOutputStream(f);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(report);
			output.close();

			file = new FileOutputStream("./gaudi/guiminlogs/dialogminimal/lastDialogMinimal.txt");
			buffer = new BufferedOutputStream(file);
			output = new ObjectOutputStream(buffer);
			output.writeObject(report);
			output.close();


			System.err.println("[GUIMIN][Anonymize]-run recorded in file: " + f.getAbsolutePath());

		}catch (Exception e1){
			System.err.println("Error: " + e1.getMessage());
		}





	}





	private static void createSandbox(GuiMinReportExperimental report) {

		try {

			System.out.println("[Anonymize]-creating sandbox...");

			File f = new File("gaudi/");
			if(!f.exists())
				f.mkdir();
			f = new File("gaudi/guiminlogs/");
			if(!f.exists())
				f.mkdir();
			f = new File("gaudi/guiminlogs/dialogminimal/");
			if(!f.exists())
				f.mkdir();
			f = new File("gaudi/guiminlogs/dialogminimal/sandbox");
			if(!f.exists())
				f.mkdir();

			f = new File("gaudi/guiminlogs/dialogminimal/sandbox/report.txt");
			if(f.exists())
				f.delete();
			f = new File("gaudi/guiminlogs/dialogminimal/sandbox/report.txt");

			try{
				OutputStream file = new FileOutputStream(f);
				OutputStream buffer = new BufferedOutputStream(file);
				ObjectOutput output = new ObjectOutputStream(buffer);
				output.writeObject(report);
				output.flush();
				output.close();
				buffer.close();
				file.close();
				System.err.println("[GAUDI][Anonymize]-the following file was created in the sandbox: " + f.getAbsolutePath());

			}catch (Exception e1){
				System.err.println("Error: " + e1.getMessage());
			}

		}
		catch(Throwable t) {
			t.printStackTrace();
		}
	}


	/**
	 * For testing purposes only: Take a small struct and multiply it
	 * 
	 * @param treshold Once the size of the new struct reaches threshold, the pumping stops
	 */
	private static void pump(int threshold) {

		int count=0;
		while((count=count())<threshold){
			System.err.println("[GUIMIN][Anonymize] pumping data. Current event count: "+count);
			WidgetDialog wclone = chead.getWhead().clone();//clone the first widget which should clone the entire struct
			WidgetDialog wnext = wclone;

			//remove the failure inducing container: assuming it is the last one
			while(wnext.getNext()!=null && wnext.getNext().getNext()!=null)
				wnext=wnext.getNext();

			wnext.setNext(chead.getWhead());
			chead.setWhead(wclone);
		}
		generateIds(chead);
		printids(chead);

	}
	
	private static void pump2(int threshold) {
		
		int count=0;
		while((count=count())<threshold){
			System.err.println("[GUIMIN][Anonymize] pumping data. Current event count: "+count);
			WidgetDialog wclone = chead.getWhead().clone();//clone the first widget which should clone the entire struct
			
			WidgetDialog lastCloned = wclone;
			ContainerDialog cc = new ContainerDialog("", 0);
			cc.setWhead(wclone);
			getlastW(cc, lastCloned);
			lastCloned.getE().clear();
			

			lastCloned.setNext(chead.getWhead());
			chead.setWhead(wclone);
		}
		generateIds(chead);
		printids(chead);
		
	}

	static int id=0;
	private static void generateIds(ContainerDialog cnext) {

		id = id+1;
		cnext.setEventID(id);
		WidgetDialog wnext = cnext.getWhead();

		while(wnext!=null) {
			id=id+1;wnext.setEventID(id);
			for(EventLogged el: wnext.getE()){
				id=id+1;el.setEventId(id);
				if(el.getNext()!=null){
					generateIds(el.getNext());
				}
			}
			wnext=wnext.getNext();
		}

	}
	private static void printids(ContainerDialog cnext) {

		System.out.println("EID (Container) -> "+cnext.getEventId());
		WidgetDialog wnext = cnext.getWhead();

		while(wnext!=null) {
			System.out.println("EID (widget) -> "+wnext.getEventId());
			for(EventLogged el: wnext.getE()){
				System.out.println("EID (event) -> "+el.getEventId());
				if(el.getNext()!=null){
					printids(el.getNext());
				}
			}
			wnext=wnext.getNext();
		}

	}



	
	private static void getW(ContainerDialog cnext,ArrayList<WidgetDialog> w) {

		WidgetDialog wnext = cnext.getWhead();

		while(wnext!=null) {
			w.add(wnext);
			for(EventLogged el: wnext.getE()){		
				if(el.getNext()!=null){
					getW(el.getNext(),w);
				}
			}
			wnext=wnext.getNext();
		}

	}
	
	private static void getlastW(ContainerDialog cnext,WidgetDialog w) {

		WidgetDialog wnext = cnext.getWhead();

		while(wnext!=null) {
			w=wnext;
			for(EventLogged el: wnext.getE()){		
				if(el.getNext()!=null){
					getlastW(el.getNext(),w);
				}
			}
			wnext=wnext.getNext();
		}

	}
	
	

}
