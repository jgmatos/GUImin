package pt.inescid.gsd.guimin.common.model.log;



import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import pt.inescid.gsd.guimin.common.GaudiGuiHierarchy;


public class GaudiEvent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4226407397587011505L;

	public static final int SET_TEXT_JTEXTFIELD = 0001;

	public int id;
	public String widgetid = null;

	public int buttonclick;
	public long when;
	public int modifiers;
	public int clickcount;

	public int keycode;

	public long delaytime;

	public String text;

	public GaudiEvent(AWTEvent e, GaudiGuiHierarchy h){

		Object o = e.getSource();
		this.widgetid = h.findWidgetId((Component)o);
		this.recordEventValues(e);
	}

	public GaudiEvent(String id){

		this.widgetid = id;
	}

	public void recordEventValues(AWTEvent e){

		this.id = e.getID();

		if(e instanceof MouseEvent){
			this.buttonclick = ((MouseEvent)e).getButton();
			this.when = ((MouseEvent)e).getWhen();
			this.modifiers = ((MouseEvent)e).getModifiers();
			this.clickcount = ((MouseEvent)e).getClickCount();
		}else if(e instanceof KeyEvent){
			this.keycode = ((KeyEvent)e).getKeyCode();
			System.err.println("good: "+keycode+" ;; "+KeyEvent.getKeyText(keycode));
		}

	}


	@Override	
	public GaudiEvent clone() {
		GaudiEvent clone = new GaudiEvent(widgetid);
		clone.id=id;
		clone.buttonclick = buttonclick;
		clone.when = when;
		clone.modifiers = modifiers;
		clone.clickcount = clickcount;
		clone.keycode = keycode;
		clone.delaytime = delaytime;
		clone.text = text;
		return clone;
	}

}
