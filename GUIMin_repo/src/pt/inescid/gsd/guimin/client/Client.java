package pt.inescid.gsd.guimin.client;

import java.io.File;
import java.util.HashSet;

import pt.inescid.gsd.guimin.client.guimin.minimize.Anonymize;
import pt.inescid.gsd.guimin.client.guimin.minimize.ReplayAgent;
import pt.inescid.gsd.guimin.client.guimin.recorder.GMINRecorder;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.ContainerDialog;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.GUIStruct;
import pt.inescid.gsd.guimin.client.guimin.replayer.Replayer;
import pt.inescid.gsd.guimin.client.guimin.replayer.ReplayerUncaughtThrowableHandler;
import pt.inescid.gsd.guimin.client.recorder.exception.GuiUncaughtExceptionHandler;
import pt.inescid.gsd.guimin.common.GuiMinReportExperimental;

/**
 * The main class of Client, use as a command line tool.
 * 
 * @author Joao Gouveia de Matos / GSD INESC-ID 
 *
 */
public class Client {


	public static SomeOptions someOptions;

	public static void main(String[] args) {

		Client.someOptions = new SomeOptions();

		
		//HELP
		if(args.length == 1 && args[0].equals("-h")){
			printHelp();
			System.exit(0);
		}

		//Just launch the application. No monitoring
		else if(args.length >= 2 && args[0].equals("-launch")){

			System.out.println("[GaudiClient]-Normal Launching...");
			Launcher launcher = new Launcher(args[1], args);
			launcher.launch();

		}

		//Launch the application and monitor it. User input will be recorded.
		else if(args.length >= 2 && args[0].equals("-record")) {

			System.out.println("[GaudiClient]-Creating launcher...");
			Launcher launcher = new Launcher(args[1], args);

			System.out.println("[GaudiClient]-Creating recorder...");
			//Recorder er = new Recorder(args[1]);
			GMINRecorder er = new GMINRecorder(args[1]);

			System.out.println("[GaudiClient]-Starting uncaught exceptions catcher...");
			Thread.setDefaultUncaughtExceptionHandler(new GuiUncaughtExceptionHandler(er));

			System.out.println("[GaudiClient]-Lauching application...");
			launcher.launch();
			er.startRecording();

		}
		
		//Launch the application and replay the trace provided.
		else if(args.length >= 2 && args[0].equals("-replay")) {

			System.out.println("[GaudiClient]-Creating launcher...");
			Launcher launcher = new Launcher(args[4], args);

			GuiMinReportExperimental exp = GUIStruct.extract(new File(args[1]));
			ContainerDialog chead = exp.gethead();

			GUIStruct struct = new GUIStruct(chead);

			struct.printStruct(chead);

			Replayer replayer = new Replayer(chead, new HashSet<Integer>(), exp.getGaudiException(),args[4]);

			if(args.length==5) {
				replayer.setTimeouts(Long.valueOf(args[2]),Long.valueOf(args[3]));
			}

			System.out.println("[GaudiClient]-Starting uncaught exceptions catcher...");
			Thread.setDefaultUncaughtExceptionHandler(new ReplayerUncaughtThrowableHandler(replayer));

			System.out.println("[GaudiClient]-Lauching application...");
			launcher.launch();
			try {
				replayer.start();
			} catch (Throwable e) {
				e.printStackTrace();
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					System.err.println("###FINITO####");
				}
			});


		}
		
		//minimize a failure-inducing trace using the GUImin algorithm
		else if(args.length >= 2 && args[0].equals("-dialogminimal")) {

			System.out.println("[GaudiClient]-Extracting file: "+args[1]);
			GuiMinReportExperimental exp = GUIStruct.extract(new File(args[1]));

			Anonymize anon = new Anonymize(args[2],exp);
			anon.start();
			
		}
		
		//minimize a failure-inducing trace using the ddmin algorithm (less efficient)
		else if(args.length >= 2 && args[0].equals("-ddmin")) {//TODO: not implemented yet

			System.out.println("[GaudiClient]-Extracting file: "+args[1]);
			GuiMinReportExperimental exp = GUIStruct.extract(new File(args[1]));

			Anonymize anon = new Anonymize(args[2],exp);
			anon.ddmin();
			
		}
		
		//Perform a test
		else if(args.length >= 2 && args[0].equals("-guimintest")) {

			ReplayAgent.load(args[1]);
			ReplayAgent.test();
			
		}
		
		//Invalid
		else {
			System.out.println("[GaudiClient]-Invalid usage.");
			printHelp();
			System.exit(0);
		}

	}

	public static void printHelp(){
		System.out.println("### GaudiClient usage:");
		System.out.println("Usage Help\t\t: java -jar Client.jar -h");
		System.out.println("Launch app\t\t: java -jar Client.jar -launch MainClass");
		System.out.println("Launch, Monitor and Log \t\t: java -jar Client.jar -record MainClass");
		System.out.println("Replay trace\t\t: java -jar Client.jar -launch MainClass");
		System.out.println("Minimize trace\t: java -jar Client.jar -dialogminimal tracefile MainClass");
	}

}
