package pt.inescid.gsd.guimin.client.anonymizer;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;

public class ReplayerMonitor implements AWTEventListener {

	private GaudiReplayer gr;
	
	public static final long RECORDING_EVENT_MASK = 
    	AWTEvent.MOUSE_EVENT_MASK
        | 	AWTEvent.MOUSE_MOTION_EVENT_MASK
        | 	AWTEvent.MOUSE_WHEEL_EVENT_MASK
        |	AWTEvent.KEY_EVENT_MASK
        |	AWTEvent.WINDOW_EVENT_MASK
        | 	AWTEvent.WINDOW_FOCUS_EVENT_MASK
        | 	AWTEvent.WINDOW_STATE_EVENT_MASK
        | 	AWTEvent.ADJUSTMENT_EVENT_MASK
        | 	AWTEvent.INVOCATION_EVENT_MASK
        | 	AWTEvent.RESERVED_ID_MAX
        | 	AWTEvent.TEXT_EVENT_MASK
        | 	AWTEvent.PAINT_EVENT_MASK
        | 	AWTEvent.HIERARCHY_EVENT_MASK
        | 	AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK
        | 	AWTEvent.COMPONENT_EVENT_MASK
        | 	AWTEvent.CONTAINER_EVENT_MASK
        | 	AWTEvent.FOCUS_EVENT_MASK
        // required for non-standard input 
        | 	AWTEvent.INPUT_METHOD_EVENT_MASK
        // For java.awt.Choice selections
        | 	AWTEvent.ITEM_EVENT_MASK
        // required to capture MenuItem actions
        | 	AWTEvent.ACTION_EVENT_MASK;
	 
	 
	 
	 public ReplayerMonitor(GaudiReplayer r){
		 this.gr = r;
		 Toolkit.getDefaultToolkit().addAWTEventListener(this, RECORDING_EVENT_MASK);
	 }


	@Override
	public void eventDispatched(AWTEvent arg0) {
		this.gr.processEvent(arg0);		
	}
	
}
