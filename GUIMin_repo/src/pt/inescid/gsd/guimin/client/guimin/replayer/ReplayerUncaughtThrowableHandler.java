package pt.inescid.gsd.guimin.client.guimin.replayer;



import java.lang.Thread.UncaughtExceptionHandler;

public class ReplayerUncaughtThrowableHandler implements  UncaughtExceptionHandler{

	private Replayer rep;
	
	public ReplayerUncaughtThrowableHandler(Replayer rep){
		super();
		this.rep = rep;
	}
	
	public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace();
		
		rep.handleThrowable(e);
		
	}
}
