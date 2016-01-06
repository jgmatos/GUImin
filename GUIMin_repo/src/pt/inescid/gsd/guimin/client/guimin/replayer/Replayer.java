package pt.inescid.gsd.guimin.client.guimin.replayer;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import pt.inescid.gsd.utils.Location;
import pt.inescid.gsd.utils.StacktraceUtils;
import pt.inescid.gsd.guimin.client.Launcher;
import pt.inescid.gsd.guimin.client.anonymizer.GaudiReplayer;
import pt.inescid.gsd.guimin.client.anonymizer.ReplayerMonitor;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.ContainerDialog;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.EventLogged;
import pt.inescid.gsd.guimin.client.guimin.recorder.struct.WidgetDialog;
import pt.inescid.gsd.guimin.common.GaudiException;
import pt.inescid.gsd.guimin.common.GaudiGuiHierarchy;
import pt.inescid.gsd.guimin.common.model.log.GaudiEvent;

public class Replayer extends GaudiReplayer{





	private ContainerDialog chead;
	private HashSet<Integer> noReplay;



	@SuppressWarnings("unused")
	private ReplayerMonitor monitor;



	private long windowTimeout=100;
	private long eventTimeout=0;
	public void setTimeouts(long window,long event) {
		this.windowTimeout = window;
		this.eventTimeout = event;
	}









	private GaudiException F;
	private GaudiGuiHierarchy hierarchy;
	public GaudiGuiHierarchy getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(GaudiGuiHierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}

	private String lastWidgetClicked = "";












	ByteArrayOutputStream baos ;




	public Replayer(ContainerDialog chead,HashSet<Integer> noReplay,GaudiException F, String SUT){

		this.hierarchy = new GaudiGuiHierarchy();
		this.chead=chead;
		this.noReplay = noReplay;
		this.F=F;
		this.monitor = new ReplayerMonitor(this);		
		
	//	printStruct(chead);
		
	}
	
	public Replayer(ContainerDialog chead,GaudiException F, String SUT){

		this.hierarchy = new GaudiGuiHierarchy();
		this.chead=chead;
		this.F=F;
		this.monitor = new ReplayerMonitor(this);		
		
	}
	
	
	
	

	public void start() {

		
		processGUI() ;

		if(replay(chead,noReplay))
			System.out.println("F was successfull replayed");
		else
			System.out.println("could not reproduce F");

	}

	
	public void processGUI() {
		
		try {
			System.err.println("[GAUDI][GaudiEventReplayer] sleeping for "+(windowTimeout)+" ms....");
			Thread.sleep((windowTimeout));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		this.hierarchy.processGUI();
		
	}
	


	public static void launch(String SUT) {
		
		Launcher launcher = new Launcher(SUT);
		launcher.launch();
		try{
			System.out.println("[GUIMIN][REPLAYER] Application was launched. Sleeping for 7sec");
			Thread.sleep(7000);
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
		
	}
	
	
	


	private void sleep(long timeout) {
		try {
			System.err.println("[GAUDI][GaudiEventReplayer] sleeping for "+timeout+" ms....");
			if(timeout>0)
			Thread.sleep(timeout);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	
	public void handleThrowable(Throwable t) {
		if(sameLocation(t))
			success2=true;

	}

	
	private boolean sameLocation(Throwable t) {
		
		Location l1 = StacktraceUtils.getLocation(t);
		Location l2 = StacktraceUtils.getLocation(F.getException());
		
		return l1.equals(l2);
		
	}

	
	
	
	
	
	boolean first = true;

	private boolean success = false, success2=false;
	public boolean replay(ContainerDialog cnext,HashSet<Integer> noReplay) {

		
		if(first){
			sleep(5000);first=false;}
		//New Container: sleep to make sure the hierarchy is refreshed
		sleep(windowTimeout);

		WidgetDialog wnext = cnext.getWhead();
		while(wnext!=null) {

			if(!noReplay.contains(wnext.getEventId())){

				for(EventLogged e_i:wnext.getE()) {

					if(!noReplay.contains(e_i.getEventId())) {
						
						sleep(eventTimeout);currentEventId=e_i.getEventId();
						success = replayevent(e_i.getEv());//replay_ev(e_i.getEv());
						
						
						if(success||success2)
							return true;

						if(e_i.getNext()!=null){

							success = replay(e_i.getNext(),noReplay);
							if(success||success2)
								return true;

						}

					}

				}

			}
			wnext = wnext.getNext();
		}
		
		sleep(3000);//sometimes the crash takes a while
		if(success||success2)
			return true;

		return false;


	}


	
	int currentEventId = 0;
	
	
	private boolean replayevent(GaudiEvent e) {
		
		
		try {
			
			//replay e_i
			Component w_i = this.hierarchy.findWidget(e.widgetid);
			if(w_i==null){
				System.err.println("[GAUDI][Replayer] Could not find component: "+e.widgetid+". Trying again...");//TODO: fix this
				processGUI();
				sleep(5000);
				w_i = this.hierarchy.findWidget(e.widgetid);
				if(w_i==null){
					System.err.println("[GAUDI][Replayer] Could not find component again :( exiting.....");
					//Launcher.shutdownW();
				}
				else
					System.err.println("[GAUDI][Replayer] Found it!: ");

			}
//			Component c = this.hierarchy.findWidget(e.widgetid);

			if(w_i!=null/* && !e.widgetid.equals(lastWidgetClicked)*/){

				if(e.id == MouseEvent.MOUSE_PRESSED ||
						e.id == MouseEvent.MOUSE_RELEASED||
						e.id == MouseEvent.MOUSE_CLICKED){

					replayMouseEntered(e, w_i);
					this.lastWidgetClicked = e.widgetid;
					
				}
			
				if(w_i instanceof JMenu)
					replayMousePressed(e, w_i);
				else if(w_i instanceof JMenuItem) 
					replayMousePressedAndReleased(e, w_i);
				else if(w_i instanceof JButton)
					replayMousePressedAndReleased(e, w_i);
				else if (w_i instanceof JTextArea||w_i.toString().contains("TextArea")||w_i instanceof JTextField){
					replayMousePressedAndReleased(e, w_i);
					replayKeyTyped(e,w_i);
				}
				else
					replayMousePressedAndReleased(e, w_i);
				
				
				
			}
			
		}
		catch(Throwable t) {
			t.printStackTrace();//return true;//TODO: F
		}
	
		return false;
	}



	private void replayMouseEntered(GaudiEvent e,Component c) {

		AWTEvent 	newe = new MouseEvent(c,
				MouseEvent.MOUSE_ENTERED,
				e.when,
				e.modifiers,
				0,
				0,
				e.clickcount,
				false,
				e.buttonclick);
		System.err.println("[GUIMIN]-->mouse entered event at " + e.widgetid);


		try {
			EventDispatcher ed = new EventDispatcher(c, newe);
			ed.start();

			Thread.sleep(250);

			if(ed.isAlive())
				ed.interrupt();

		} catch (InterruptedException e1) {
			System.err.println("interrompida");
		}

	}


	@SuppressWarnings("unused")
	private void replayMouseClicked(GaudiEvent e,Component c) {

		AWTEvent 	newe = new MouseEvent(c,
				MouseEvent.MOUSE_CLICKED,
				e.when,
				e.modifiers,
				0,
				0,
				e.clickcount,
				false,
				e.buttonclick);
		System.err.println("[GUIMIN]["+currentEventId+"]-->mouse clicked event at " + e.widgetid);

	try {
			EventDispatcher ed = new EventDispatcher(c, newe);
			ed.start();

			Thread.sleep(250);

			if(ed.isAlive())
				ed.interrupt();

		} catch (InterruptedException e1) {
			System.err.println("interrompida");
		}

	}


	private void replayMousePressed(GaudiEvent e,Component c) {

		AWTEvent 	newe = new MouseEvent(c,
				MouseEvent.MOUSE_PRESSED,
				e.when,
				e.modifiers,
				0,
				0,
				e.clickcount,
				false,
				e.buttonclick);

		System.err.println("[GUIMIN]["+currentEventId+"]-->mouse Pressed event at " + e.widgetid);
		c.dispatchEvent(newe);
	try {
			EventDispatcher ed = new EventDispatcher(c, newe);
			ed.start();

			Thread.sleep(250);

			if(ed.isAlive())
				ed.interrupt();

		} catch (InterruptedException e1) {
			System.err.println("interrompida");
		}

	}

	private void replayMousePressedAndReleased(GaudiEvent e,Component c) {

		AWTEvent 	newe = new MouseEvent(c,
				MouseEvent.MOUSE_PRESSED,
				e.when,
				e.modifiers,
				0,
				0,
				e.clickcount,
				false,
				e.buttonclick);
		System.err.println("[GUIMIN]["+currentEventId+"]-->mouse Pressed&Released event at " + e.widgetid);

	try {
			EventDispatcher ed = new EventDispatcher(c, newe);
			ed.start();

			Thread.sleep(250);

			if(ed.isAlive())
				ed.interrupt();

		} catch (InterruptedException e1) {
			System.err.println("interrompida");
		}

		 	newe = new MouseEvent(c,
				MouseEvent.MOUSE_RELEASED,
				e.when,
				e.modifiers,
				0,
				0,
				e.clickcount,
				false,
				e.buttonclick);
	try {
			EventDispatcher ed = new EventDispatcher(c, newe);
			ed.start();

			Thread.sleep(250);

			if(ed.isAlive())
				ed.interrupt();

		} catch (InterruptedException e1) {
			System.err.println("interrompida");
		}


	}

	
	public void replayKeyTyped(GaudiEvent e,Component c) {
		
	/*	AWTEvent newe = new KeyEvent(c, KeyEvent.KEY_PRESSED, e.when, e.modifiers, e.keycode,KeyEvent.getKeyText(e.keycode).charAt(0));
		System.err.println("[GUIMIN]["+currentEventId+"]-->key typed event at " + e.widgetid+" ;; "+KeyEvent.getKeyText(e.keycode));

		try {
			EventDispatcher ed = new EventDispatcher(c, newe);
			ed.start();

			Thread.sleep(500);

			if(ed.isAlive())
				ed.interrupt();

		} catch (InterruptedException e1) {
			System.err.println("interrompida");
		}

		
		 newe = new KeyEvent(c, KeyEvent.KEY_PRESSED, e.when, e.modifiers, e.keycode,KeyEvent.getKeyText(e.keycode).charAt(0));
		System.err.println("[GUIMIN]["+currentEventId+"]-->key typed event at " + e.widgetid+" ;; "+KeyEvent.getKeyText(e.keycode));

		try {
			EventDispatcher ed = new EventDispatcher(c, newe);
			ed.start();

			Thread.sleep(500);

			if(ed.isAlive())
				ed.interrupt();

		} catch (InterruptedException e1) {
			System.err.println("interrompida");
		}*/

		Robot r;
		try {
			r = new Robot();
			r.keyPress(e.keycode);
		//	r.delay(10);
			r.keyRelease(e.keycode);
		} catch (Throwable e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	


	public void processEvent(AWTEvent arg0) {

		if((Component) arg0.getSource() instanceof javax.swing.JInternalFrame &&!this.lastWidgetClicked.equals("")){

			if(arg0.getID()==1400)
				this.hierarchy.processWidget((Component) arg0.getSource(), this.lastWidgetClicked + "/0");

		}

		if(arg0.getID() == WindowEvent.WINDOW_ACTIVATED /*&& !this.lastWidgetClicked.equals("")*/){
			System.err.println("[GUIMIN][Replayer] WINDOW ACTIVATED: "+this.lastWidgetClicked);




			if (this.hierarchy.findWidgetId((Component) arg0.getSource()) == null &&
					this.hierarchy.findWidget(this.lastWidgetClicked + "/0") == null){



				this.hierarchy.processWidget((Component) arg0.getSource(), this.lastWidgetClicked + "/0");



/*				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/

			}else if (this.hierarchy.findWidgetId((Component) arg0.getSource()) == null &&
					this.hierarchy.findWidget(this.lastWidgetClicked + "/0") != null){

				this.hierarchy.updateWidget((Component) arg0.getSource(), this.lastWidgetClicked + "/0");

			}


		}





	}








	public class EventDispatcher extends Thread {



		public Component cc;
		public AWTEvent awte;

		public EventDispatcher(Component c, AWTEvent awt) {

			cc = c;
			awte = awt;

		}

		@Override
		public void run() {
			cc.dispatchEvent(awte);
		}


	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	int ev=0;
	int [][] struct = new int [100][100];
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
	
	
	public void printStruct(ContainerDialog cnext) {
		fillStruct(cnext, 0);
		for(int i=0;i <20;i++){
			System.out.println();
			for(int j=0; j<20;j++)
				System.out.print(struct[j][i]);
		}
	}
	
	


//
//
//
//	private boolean replay_ev(GaudiEvent e) {
//
//
//		try {
//
//			//replay e_i
//			Component w_i = this.hierarchy.findWidget(e.widgetid);
//			if(w_i==null)
//				System.err.println("[GAUDI][Replayer] Could not find component: "+e.widgetid);//TODO: fix this
//
//
//
//
//
//
//			Component c = this.hierarchy.findWidget(e.widgetid);
//
//
//
//
//
//
//
//			if(c!=null && !e.widgetid.equals(lastWidgetClicked)){//TODO: resolver o duplicado: este hack nao pode estar aqui para sempre
//
//				replayMouseEntered(e, c);
//				replayMouseClicked(e, c);
//				
//				if(e.widgetid.equals("0/2/0/1/1/0/8/7"))
//					replayMousePressedAndReleased(e, c);
//				
//				if(e.id == MouseEvent.MOUSE_PRESSED){
//					this.lastWidgetClicked = e.widgetid;
//
//
//				}
//
//				if(e.id == MouseEvent.MOUSE_PRESSED ||
//						e.id == MouseEvent.MOUSE_RELEASED||
//						e.id == MouseEvent.MOUSE_CLICKED){
//
//
//
//
//					AWTEvent newe = new MouseEvent(c,
//							e.id,
//							e.when,
//							e.modifiers,
//							0,
//							0,
//							e.clickcount,
//							false,
//							e.buttonclick);
//
//
//
//
//
//					try {
//						EventDispatcher ed = new EventDispatcher(c, newe);
//						ed.start();
//
//						Thread.sleep(250);
//
//						if(ed.isAlive())
//							ed.interrupt();
//
//					} catch (InterruptedException e1) {
//						System.err.println("interrompida");
//					}
//
//
//
//					if(e.id == MouseEvent.MOUSE_PRESSED)
//						System.err.println("[GaudiServer]--> Replaying mouse pressed event at " + e.widgetid+" -> "+c);
//					else
//						System.err.println("[GaudiServer]--> Replaying mouse release event at " + e.widgetid);
//
//
//
//
//
//
//
//				}else if (e.id == KeyEvent.KEY_PRESSED){
//					this.robot.keyPress(e.keycode);
//
//					System.out.println("[GaudiServer]--> Replaying key pressed event at " + e.widgetid);
//
//				}else if (e.id == KeyEvent.KEY_RELEASED){
//					this.robot.keyRelease(e.keycode);
//
//					System.out.println("[GaudiServer]--> Replaying key released event at " + e.widgetid);
//
//				}else if (e.id == GaudiEvent.SET_TEXT_JTEXTFIELD){
//
//
//					((JTextField)c).setText(e.text);
//					AWTEvent newe;
//					for(int i=0;i<e.text.length();i++){
//						System.err.println("[GaudiServer]--> Replaying a set text at " + e.widgetid);
//						newe = new KeyEvent(c, KeyEvent.KEY_PRESSED, e.when, e.modifiers, 0, e.text.charAt(i));
//						try {
//							EventDispatcher ed = new EventDispatcher(c, newe);
//							ed.start();
//
//							Thread.sleep(500);
//
//							if(ed.isAlive())
//								ed.interrupt();
//
//						} catch (InterruptedException e1) {
//							System.err.println("interrompida");
//						}
//
//					}
//
//				}
//
//
//			}
//
//
//		}
//		catch(Throwable t) {
//
//			return true;//TODO: F
//
//		}
//
//
//
//		return false;
//
//	}
//

}
