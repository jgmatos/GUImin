package pt.inescid.gsd.guimin.common.model.log;

import javax.swing.JTextField;

public class GaudiJTextField extends GaudiChildless{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5672563827521576473L;
	
	
	
	private JTextField jtextfield;
	
	
	private String text;
	
	
	
	
	public GaudiJTextField(String widgetId,JTextField jtextfield) {
		super(widgetId);
		
		this.jtextfield = jtextfield;
		
	}

	public JTextField getJtextfield() {
		return jtextfield;
	}

	public void setJtextfield(JTextField jtextfield) {
		this.jtextfield = jtextfield;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


	
	
	
	
	
}
