package pt.inescid.gsd.guimin.common;

import java.io.Serializable;

import pt.inescid.gsd.guimin.client.guimin.recorder.struct.ContainerDialog;

public class GuiMinReportExperimental implements Serializable{


	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	private ContainerDialog chead;
	
	
	
	private GaudiException gaudiException;
	
	
	
	
	
	public GuiMinReportExperimental(ContainerDialog chead, GaudiException gaudiException) {
		this.gaudiException = gaudiException;
		this.chead = chead;
	}

	
	
	
	
	
	
	
	
	
	

	public GaudiException getGaudiException() {
		return gaudiException;
	}

	public void setGaudiException(GaudiException gaudiException) {
		this.gaudiException = gaudiException;
	}












	public ContainerDialog gethead() {
		return chead;
	}












	public void setSerialize(ContainerDialog chead) {
		this.chead = chead;
	}
	
	
	
	
	
	
	

	
	
	
}
