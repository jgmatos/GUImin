package pt.inescid.gsd.guimin.client.guimin.minimize;

import pt.inescid.gsd.guimin.client.Launcher;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.ContainerDialog;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.GUIStruct;
import pt.inescid.gsd.guimin.client.guimin.replayer.Replayer;
import pt.inescid.gsd.guimin.client.guimin.replayer.ReplayerUncaughtThrowableHandler;
import pt.inescid.gsd.guimin.common.GaudiException;
import pt.inescid.gsd.guimin.common.GuiMinReportExperimental;

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
import java.util.HashSet;

public class ReplayAgent {




	private static Replayer replayer;

	private static ContainerDialog chead;

	private static String SUT;

	private static GaudiException F;

	private static Launcher launcher;


	public static void setF(GaudiException F) {
		ReplayAgent.F = F;
	}

	public static void setHead(ContainerDialog chead) {
		ReplayAgent.chead=chead;
	}

	public static void setSUT(String SUT) {
		ReplayAgent.SUT = SUT;
	}


	public static void test() {


		ignition();
		System.out.println("[GaudiClient]-Starting uncaught exceptions catcher...");

		System.err.println("\n\n\n\n\n\n\n\n REPLAYING NEW EVENT SEQUENCE \n\n\n\n\n\n\n\n");

		Thread.setDefaultUncaughtExceptionHandler(new ReplayerUncaughtThrowableHandler(replayer));





		if(replayer.replay(chead,getExclude())) // replay the event sequence except for the ones in set exclude
			System.err.println("\n\n\n\n\n\n\n\n F was successfull replayed \n\n\n\n\n\n\n\n");
		else
			System.err.println("\n\n\n\n\n\n\n\n could not reproduce F\n\n\n\n\n\n\n\n");

		shutdown();

	}


	private static void ignition() {

		replayer = new Replayer(chead, F, SUT);

		Thread.setDefaultUncaughtExceptionHandler(new ReplayerUncaughtThrowableHandler(replayer));


		launcher = new Launcher(SUT);
		launcher.launch();
		
		try{
			Thread.sleep(10000);
		}
		catch(Throwable t) {

		}
		
		replayer.processGUI();
	}

	private static void shutdown() {
		Launcher.shutdownW();
		try{
			Thread.sleep(2000);
		}
		catch(Throwable t) {

		}
	}



	public static void logExclude(HashSet<Integer> exclude) {

		File f = new File("gaudi/");
		if(!f.exists())
			f.mkdir();
		f = new File("gaudi/guiminlogs/");
		if(!f.exists())
			f.mkdir();
		f = new File("gaudi/guiminlogs/dialogminimal/");
		if(!f.exists())
			f.mkdir();

		f = new File("gaudi/guiminlogs/dialogminimal/exclude.txt");

		try{

			OutputStream file = new FileOutputStream(f);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(exclude);
			output.close();
			System.err.println("[GUIMIN][Anonymize]-run recorded in file: " + f.getAbsolutePath());

		}catch (Exception e1){
			System.err.println("Error: " + e1.getMessage());
		}


	}

	public static void load(String SUT) {

		setSUT(SUT);
		//GuiMinReportExperimental exp = GUIStruct.extract(new File("./gaudi/guiminlogs/lastguiminlog.txt"));
		GuiMinReportExperimental exp = GUIStruct.extract(new File("./gaudi/guiminlogs/dialogminimal/sandbox/report.txt"));
		Min.setF(exp.getGaudiException());
		Min.setHead(exp.gethead());

	}

	private static HashSet<Integer> getExclude() {



		try {


			InputStream fis;

			fis = new FileInputStream("gaudi/guiminlogs/dialogminimal/exclude.txt");
			InputStream buffer = new BufferedInputStream(fis);
			ObjectInput input = new ObjectInputStream (buffer);

			@SuppressWarnings("unchecked")
			HashSet<Integer> exclude = (HashSet<Integer>) input.readObject();
			input.close();




			return exclude;

		}
		catch(Throwable t) {
			t.printStackTrace();
			return null;
		}




	}



	




}
