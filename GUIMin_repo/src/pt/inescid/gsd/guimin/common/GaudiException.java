package pt.inescid.gsd.guimin.common;

import java.io.Serializable;



public class GaudiException implements Serializable{

	private static final long serialVersionUID = 1L;

	private Exception e;
	
	public GaudiException(Exception e){
		this.setException(e);
	}

	public void setException(Exception e) {
		this.e = e;
	}

	public Exception getException() {
		return e;
	}
	

	
	
}
