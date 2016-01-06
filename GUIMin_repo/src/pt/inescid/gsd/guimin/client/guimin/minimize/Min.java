package pt.inescid.gsd.guimin.client.guimin.minimize;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import pt.inescid.gsd.guimin.client.guimin.recorder.struct.ContainerDialog;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.EventLogged;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.ItemLogged;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.WidgetDialog;
import pt.inescid.gsd.guimin.common.GaudiException;
import pt.inescid.gsd.guimin.common.GuiMinReportExperimental;






















public class Min {










	private static ContainerDialog chead;

	private static String SUT;

	private static GaudiException F;

	public static void setHead(ContainerDialog chead) {
		Min.chead=chead;
		ReplayAgent.setHead(chead);
	}

	public static void setSUT(String SUT) {
		Min.SUT = SUT;
		ReplayAgent.setSUT(SUT);
	}


	public static void setF(GaudiException F) {
		Min.F=F;
		ReplayAgent.setF(F);
	}












	//Pseudocode:
	//
	//LazyMin
	//do
	//		n = 2n < |L| ? 2n : |L|
	//	
	//		L' <- DeltaMin(L,n)
	//		if(L' != 0)
	//			L  <- L' //reduce to subset	
	//		else
	//			L <- NablaMin'(L,n)
	//
	//while(n<|L|)
	//
	//---------
	/**
	 * LazyMin Algorithm
	 * Provides no minimality guarantees
	 * Best Case: Log_2(|L|)
	 * Worst Case: 4(|L|)
	 * 
	 * @param cnext 
	 * @param L	The list 
	 */
	public static void LazyMin(ContainerDialog cnext, ArrayList<ItemLogged> L) {
		Log.log("Starting LazyMin with list L of size="+L.size());
		LazyMin(cnext,L.size(),L);
	}
	/**
	 * @param cnext 
	 * @param L	The list 
	 * @param n Size of the sublists
	 */
	public static void LazyMin(ContainerDialog cnext, int n, ArrayList<ItemLogged> L) {

		ArrayList<ItemLogged> Lprime;

		do {
			n = (n/2)>1 ? (n/2): 1;
			Log.log("LazyMin current size of sublists="+n);
			Lprime = DeltaMin(L, n);

			if(Lprime!=null)
				L = Lprime;//reduce to subset
			else
				L=LazyNablaMin(L, n);

		} while (n>1);


	}



	//
	//DDMin
	//do
	//	
	//		L' <- DeltaMin(L,n)
	//		if(L' != 0)
	//			L  <- L' //reduce to subset	
	//			if(n<|L|)
	//				n = 2n > |L| ? |L| : 2n	
	//		else 
	//			L' <- NablaMin(L,n) 
	//			if(L' != 0)
	//				L  <- L' //reduce to complement
	//			else
	//				if(n<|L|)
	//					n = 2n > |L| ? |L| : 2n
	//while(n<|L|)
	public static void ddmin(ContainerDialog cnext, int n, ArrayList<ItemLogged> L) {
		//TODO: implement the pseudocode. The following code is wrong 
	/*	ArrayList<ItemLogged> Lprime;

		do {
			n = (2*n)>L.size()? (2*n): L.size();
			Lprime=L;
			do{
				L=Lprime;
				Lprime = DeltaMin(L, n);
			}
			while(Lprime!=null);
			Lprime=L;
			do{
				L=Lprime;
				Lprime = NablaMin(L, n);
			}
			while(Lprime!=null);

		} while (n<L.size());*/
		ArrayList<ItemLogged> Lprime;
		boolean increaseGranularity = true;
		do {
			if(increaseGranularity)
				n = (n/2)>1 ? (n/2): 1;
			Log.log("DDMIN current size of sublists="+n);
			Lprime = DeltaMin(L, n);

			if(Lprime!=null){
				L = Lprime;//reduce to subset
				increaseGranularity=false;
			}
			else{
				Lprime = NablaMin(L, n);
				if(Lprime!=null){
					L=Lprime;
					increaseGranularity=false;
				}
				else
					increaseGranularity=true;
			}

		} while (n>1);
		

	}
	











	public static void Trim(ContainerDialog cnext,HashSet<Integer> trimlist) {


		WidgetDialog wnext = cnext.getWhead();


		while(wnext!=null) {		
			int i=0;

			if(trimlist.contains(wnext.getEventId())){
				//TODO: for now our way of removing widgets is to clear all events it contains,
				//it is the same thing in practice, but conceptually it is wrong
				wnext.getE().clear();
				
			}
			else {

				ArrayList<Integer> removeOnList = new ArrayList<Integer>();//maybe find a better way to do this: iterate and add the indexes of the events to be removed

				for(EventLogged el: wnext.getE()){

					if(trimlist.contains(el.getEventId())){

						removeOnList.add(i);

					}
					else if(el.getNext()!=null){

						Trim(el.getNext(),trimlist);

					}
					i++;
				}

				int aux=0;
				for(int j:removeOnList){
					wnext.getE().remove(j-aux);
					aux++;//once we remove an element from the list, the others get their indexes decremented
				}
				removeOnList.clear();i=0;

				if(wnext.getE().isEmpty()){
					//TODO: remove the widget
				}
			}

			wnext=wnext.getNext();
		}



	}




	/**
	 * Split a list L into sublists of size n and test each sublist for failure
	 * 
	 * @param L The list
	 * @param n The size of the sublists
	 * @return	The first sublist that induces the failure, or null if none induces the failure
	 */
	public static ArrayList<ItemLogged> DeltaMin(ArrayList<ItemLogged> L,int n) {


		ArrayList<ArrayList<ItemLogged>> sublists = split(L, n);

		if(sublists==null)//n>|L|
			return null;

		Log.log("DeltaMin a list of size="+L.size()+"\n"+"Amount of tests: "+sublists.size()+"\n");
		boolean success = false;

		for(ArrayList<ItemLogged> l_i: sublists) {
			HashSet<Integer> exclude = setMinus(L, l_i); //a set of events not to be replayed
			Log.log("Attempting a deltaTest of size="+l_i.size()+"; Event count: "+Anonymize.count());
			success = replay(exclude);//success = replayer.replay(chead, exclude); // replay the event sequence except for the ones in set exclude

			if(success){
				Log.log("The test was successfull. Trimming and returning. The set is now of size="+l_i.size());
				Trim(chead, exclude); // delete the events at set exclude
				saveToSandbox();//save the new F-inducing sublist to a new file at a sandbox
				return l_i; //return this failure inducing sublist
			}
		}


		return null; //none of these sublists induced the target failure

	}








	/**
	 * Split a list L into sublists of size n and test the complement of each sublist for failure
	 * 
	 * @param L The list
	 * @param n The size of the sublists
	 * @return	The first sublist complement that induces the failure, or null if none induces the failure
	 */
	public static ArrayList<ItemLogged> NablaMin(ArrayList<ItemLogged> L,int n) {

		ArrayList<ArrayList<ItemLogged>> sublists = split(L, n);

		if(sublists==null)//n>|L|
			return null;

		boolean success = false;
		for(ArrayList<ItemLogged> l_i: sublists) {
			HashSet<Integer> exclude = listToHashSet(l_i);//a set of events not to be replayed
			Log.log("Attempting a nablaTest of size="+l_i.size()+"; Event count: "+Anonymize.count());
			success = replay(exclude);//success = replayer.replay(chead, exclude);// replay the event sequence except for the ones in set exclude

			if(success){
				Log.log("The test was successfull. Trimming and returning. The set is now of size="+l_i.size()+"; Event count: "+Anonymize.count());
				Trim(chead, exclude);// delete the events at set exclude
				saveToSandbox();//save the new F-inducing sublist to a new file at a sandbox
				return listMinus(L, l_i);//return this failure inducing sublist complement
			}
		}

		return null;//none of these sublist complements induced the target failure

	}


	/**
	 * Split a list L into sublists of size n and test the complement of each sublist for failure
	 * 
	 * @param L The list
	 * @param n The size of the sublists
	 * @return	A failure inducing list L pruned from one/multiple/none sublists li of size n
	 */
	public static ArrayList<ItemLogged> LazyNablaMin(ArrayList<ItemLogged> L,int n) {

		ArrayList<ArrayList<ItemLogged>> sublists = split(L, n);

		if(sublists==null)//n>|L|
			return null;

		boolean success = false;
		for(ArrayList<ItemLogged> l_i: sublists) {
			HashSet<Integer> exclude = listToHashSet(l_i);//a set of events not to be replayed
			Log.log("Attempting a nablaTest of size="+setMinus(L, l_i).size()+"; Event count: "+Anonymize.count());
			success = replay(exclude);//success = replayer.replay(chead, exclude);// replay the event sequence except for the ones in set exclude

			if(success){
				Trim(chead, exclude);// delete the events at set exclude
				saveToSandbox();//save the new F-inducing sublist to a new file at a sandbox
				L=listMinus(L,l_i);// reduce L to the complement L\li
				Log.log("The test was successfull. Trimming and returning. The set is now of size="+L.size());
			}
		}

		return L;//A failure inducing list L pruned from one/multiple/none sublists li of size n

	}










	/**
	 * 
	 * Split a list L into sublists of size n
	 * 
	 * @param L The list
	 * @param n	The size of the sublists
	 * @return	An ArrayList with sublists of L of size n, or null if n>|L|
	 */
	public static ArrayList<ArrayList<ItemLogged>> split (ArrayList<ItemLogged> L,int n) {


		if(n > L.size())
			return null;

		ArrayList<ArrayList<ItemLogged>>  sublists = new ArrayList<ArrayList<ItemLogged>>();
		int subsetAmount = L.size()/n;

		int aux=0;
		for(int i=1; i<subsetAmount; i++){//all except last
			sublists.add(subList(L, aux, aux+n));
			aux=aux+n;
		}
		//last one: out of the loop because it may not be an even split 
		//	sublists.add((ArrayList<ItemLogged>)L.subList(aux, L.size()));
		sublists.add(subList(L, aux, L.size()));

		return sublists;


	}



	public static HashSet<Integer> setMinus(ArrayList<ItemLogged> L, ArrayList<ItemLogged> l_i) {

		HashSet<Integer> toSubstract = listToHashSet(l_i);

		HashSet<Integer> substracted = new HashSet<Integer>();
		for(ItemLogged il: L)
			if(!toSubstract.contains(il.getEventId()))
				substracted.add(il.getEventId());

		return substracted;

	}


	public static ArrayList<ItemLogged> listMinus(ArrayList<ItemLogged> L, ArrayList<ItemLogged> l_i) {

		HashSet<Integer> toSubstract = listToHashSet(l_i);

		ArrayList<ItemLogged> substracted = new ArrayList<ItemLogged>();
		for(ItemLogged il: L)
			if(!toSubstract.contains(il.getEventId()))
				substracted.add(il);

		return substracted;

	}


	public static HashSet<Integer> listToHashSet(ArrayList<ItemLogged> L) {
		HashSet<Integer> hset = new HashSet<Integer>();
		for(ItemLogged il: L)
			hset.add(il.getEventId());
		return hset;
	}



	public static ArrayList<ItemLogged> subList(ArrayList<ItemLogged> L, int from, int to) {

		ArrayList<ItemLogged> Lprime = new ArrayList<ItemLogged>();
		for(int i=from; i<to; i++)
			Lprime.add(L.get(i));
		return Lprime;

	}






































	public static boolean replay(HashSet<Integer> exclude) {


		System.out.println("[Gaudi][Min] Attempting to replay. ");

		try {

			ReplayAgent.logExclude(exclude);

			ProcessBuilder pb;
			try{

				String cp="";
				Scanner in = new Scanner(new FileReader(System.getProperty("user.dir")+"/sut_cp.txt"));
				in.useDelimiter("\n");
				cp = in.next();
				in.close();
				System.out.println("[GaudiATraceGenerator] A valid classpath was provided. ");
				pb = new ProcessBuilder("java",cp, "-jar", "GaudiClient.jar", "-guimintest", SUT);
			}
			catch(Exception e) {
				System.out.println("[GaudiATraceGenerator] No valid classpath was provided. Running default command");
				pb = new ProcessBuilder("java","-Xms1024m", "-Xmx1024m", "-jar", "GaudiClient.jar", "-guimintest", SUT);
			}

			Process process = pb.start();
			InputStream is = process.getErrorStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = "";


			/////////////////////////jmatos
			while(!br.ready())
				Thread.sleep(1000);

			boolean success = false;
			while((line=br.readLine())!=null){
				System.err.println(line);
				if(line.contains("F was successfull replayed")){
					success = true;

					System.out.println("\n\n\n\n\n F was successfull replayed chupamos\n\n\n\n\n"+exclude);
					break;
				}
				else if(line.contains("could not reproduce F")) {
					success = false;
					break;
				}
			}


			process.destroy();
			return success;
			/////////////////////////



		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return true;


	}


	public static void saveToSandbox() {


		try {


			File f = new File("gaudi/guiminlogs/dialogminimal/sandbox/report.txt");
			if(f.exists())
				f.delete();
			f = new File("gaudi/guiminlogs/dialogminimal/sandbox/report.txt");

			try{

				GuiMinReportExperimental report = new GuiMinReportExperimental(chead, F);

				OutputStream file = new FileOutputStream(f);
				OutputStream buffer = new BufferedOutputStream(file);
				ObjectOutput output = new ObjectOutputStream(buffer);
				output.writeObject(report);
				output.close();

				System.err.println("[GAUDI][Anonymize]-the following file was created in the sandbox: " + f.getAbsolutePath());

			}catch (Exception e1){
				System.err.println("Error: " + e1.getMessage());
			}

		}
		catch(Throwable t) {

		}

	}



}
