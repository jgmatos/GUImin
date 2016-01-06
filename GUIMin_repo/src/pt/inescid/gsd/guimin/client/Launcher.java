package pt.inescid.gsd.guimin.client;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import pt.inescid.gsd.guimin.common.GaudiGuiHierarchy;


/**
 * The class used to warp a launch and execution of a target application being monitored
 * by the GaudiClient.
 * 
 * @author nunocoracao
 *
 */
public class Launcher {

	private String mainclass;
	private String[] args;

	private Thread t;

	/**
	 * Launcher's constructor, it creates a represantation of the target application which
	 * can be launched.
	 * @param mc - Name of the class within the target application which has the main method.
	 * @param args - Arguments to pass to the target application
	 */
	public Launcher(String mc, String[] args){
		this.mainclass = mc;
		int i = 0;
		for(String s : args){
			if(s.equals(mc))
				break;
			i++;
		}
		this.args = Arrays.copyOfRange(args, i, args.length);
	}


	/**
	 * No args
	 * @param mc - Name of the class within the target application which has the main method.
	 */
	public Launcher(String mc){
		this.mainclass = mc;
		args = new String[0];
	}



	/**
	 * Method used to launch the target application warped inside GaudiClient.
	 * <p>
	 * The method creates a new Thread which runs the target application.
	 */
	@SuppressWarnings("unchecked")
	public void launch(){

			this.t = new Thread(new Runnable(){

			@Override
			public void run() {
				try 
				{
					Class<?> c = Class.forName(mainclass);
					Class[] argTypes = new Class[] { String[].class };
					Method main = c.getDeclaredMethod("main", argTypes);
					String[] mainArgs = Arrays.copyOfRange(args, 0, args.length);



					main.invoke(null, (Object)mainArgs);	
					Thread.sleep(10000);
				} catch (Exception x) {
					x.printStackTrace();
				}	
			}
		});

		this.t.start();
	//	t= new LaunchThread(this);
	//	t.start();
	}


	private class LaunchThread extends Thread {

		//public Object l;

		private Launcher l;

		public LaunchThread(Launcher l){
			this.l=l;
		}

		@Override
		public void run() {
			try 
			{
				Class<?> c = Class.forName(mainclass);
				Class[] argTypes = new Class[] { String[].class };
				Method main = c.getDeclaredMethod("main", argTypes);
				String[] mainArgs = Arrays.copyOfRange(args, 0, args.length);

				l.setMCO(main.invoke(c.newInstance(), (Object)mainArgs));

				Thread.sleep(10000);
			} catch (Exception x) {
				x.printStackTrace();
			}	
		}

	}



	public void closeGuiApp(GaudiGuiHierarchy h) {
		for(Component c : h.getRegisterW2K().keySet()){
			if(c != null && c instanceof Window){
				((Window)c).setEnabled(false);
				((Window)c).dispose();
			}
		}



		Thread[] l = new Thread[100];
		int n = Thread.enumerate(l);

		for (int i=0; i<n; i++) {
			if (l[i].getName().contains("AWT-Windows")) {
				l[i].interrupt();
			}
		}

	}

	public void killThread() {

		try{
			t.interrupt();
		}
		catch(Throwable t){
			return;
		}

	}

	public void killThread(String token) {

		try{

			Thread[] l = new Thread[100];
			int n = Thread.enumerate(l);

			for (int i=0; i<n; i++) {
				if (l[i].getName().contains(token)) {
					l[i].interrupt();
				}
			}
		}
		catch(Throwable t){
			return;
		}

	}





	//	
	//	Process process;
	//	
	//	
	//	public void pbuild() {
	//		
	//		
	//		ProcessBuilder pb = new ProcessBuilder("java","-Xbootclasspath/a:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/bin:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/deployment/macosx/jarbundler-1.4.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/deployment/win/launch4j:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/deployment/win/launch4j/lib/xstream.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/deployment/win/launch4j/launch4j.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/deployment/win/launcher/bin/JarWinJavaExeLauncher.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/bsh-2.0b4.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/commons-codec-1.3.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/commons-compress-20050911.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/commons-lang-2.4.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/commons-net-1.4.1.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/forms-1.2.0.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/gdata-core-1.0.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/gdata-client-1.0.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/gdata-calendar-1.0.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/jakarta-oro-2.0.8.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/jcom.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/jRegistryKey.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/l2fprod-common-tasks.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/looks-2.1.4.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/lucene-core-2.3.1.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/opencsv-1.8.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/skinlf.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/stax-1.2.0.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/stax-api-1.0.1.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/TVAnytimeAPI.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/lib/xtvd-lib-2.0.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/tvdatakit/lib/xercesImpl.jar:/Users/jmatos/mysvn/workspace/tvbrowser-rc2/junit-4.10.jar", this.mainclass);
	//		try {
	//			 process = pb.start();
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		
	//	}
	//	public void pbdestroy() {
	//		
	//		process.destroy();
	//		
	//	}

	//	
	//	Method mm;
	//	


	Object mainClassObject;
	public void setMCO(Object mco) {
		mainClassObject=mco;
	}


	public void shutdownTVB() {



		Class<?> c = null;
		try {
			
			Field f = mainClassObject.getClass().getField("mainFrame");
			f.setAccessible(true);
			c = Class.forName("tvbrowser.ui.mainframe.MainFrame");


			if(c==null){
				System.out.println("[GUIMIN][Launcher] Failed to kill app");
				return;
			}

			System.out.println("[GUIMIN][Launcher] quiting in 5 ...");
			Method[] methods = c.getMethods();
			for(Method m: methods){
				System.out.println("[GUIMIN][Launcher] quiting in 4 ...");
				if(m.getName().equals("quit")&&!m.toGenericString().contains("boolean")){
					System.out.println("[GUIMIN][Launcher] quiting in 3 ...");
					m.setAccessible(true);
					//m.invoke(c.newInstance());


					m.invoke(f.get(mainClassObject));
					System.out.println("[GUIMIN][Launcher] quiting in 2 ...");					
					Thread.sleep(5000);
					System.out.println("[GUIMIN][Launcher] quiting in 1 ...");
					break;
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}









	public static void shutdownW() {

		Window[] windows = Window.getWindows();

		for(Window w: windows){
			if(w.toString().contains("MainFrame")){
				System.out.println("[GUIMIN][Launcher] Dispatching closing event for window "+w);
				AWTEvent window_closing = new WindowEvent(w,WindowEvent.WINDOW_CLOSING);
				w.getToolkit().getSystemEventQueue().postEvent(window_closing);				
				break;
			}
		}

	}





//	public class EventDispatcher extends Thread {
//
//
//
//		public Component cc;
//		public AWTEvent awte;
//
//		public EventDispatcher(Component c, AWTEvent awt) {
//
//			cc = c;
//			awte = awt;
//
//		}
//
//		@Override
//		public void run() {
//			cc.dispatchEvent(awte);
//		}
//
//
//	}







}