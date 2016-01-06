package pt.inescid.gsd.guimin.common;

import java.io.Serializable;

import pt.inescid.gsd.guimin.common.guimin.Serialize;

public class GuiMinReport implements Serializable{


	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	private Serialize serialize;
	
	
	
	private GaudiException gaudiException;
	
	
	
	
	
	public GuiMinReport(Serialize serialize, GaudiException gaudiException) {
		this.gaudiException = gaudiException;
		this.serialize = serialize;
	}

	
	
	
	
	
	
	
	
	
	

	public GaudiException getGaudiException() {
		return gaudiException;
	}

	public void setGaudiException(GaudiException gaudiException) {
		this.gaudiException = gaudiException;
	}












	public Serialize getSerialize() {
		return serialize;
	}












	public void setSerialize(Serialize serialize) {
		this.serialize = serialize;
	}
	
	
	
	
	
	
	

	
	
	
}
