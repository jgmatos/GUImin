package pt.inescid.gsd.guimin.client.recorder.exception;


import java.lang.Thread.UncaughtExceptionHandler;

import pt.inescid.gsd.guimin.client.recorder.GaudiRecorder;

public class GuiUncaughtExceptionHandler implements  UncaughtExceptionHandler{

	private GaudiRecorder er;
	
	public GuiUncaughtExceptionHandler(GaudiRecorder er){
		super();
		this.er = er;
	}
	
	public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace();
		
		//this.er.printSequence();
		this.er.recordEventSequence((Exception) e);
	}
}
