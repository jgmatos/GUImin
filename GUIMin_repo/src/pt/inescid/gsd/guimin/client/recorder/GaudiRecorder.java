package pt.inescid.gsd.guimin.client.recorder;

import java.awt.AWTEvent;
import java.util.Calendar;

import pt.inescid.gsd.guimin.client.Client;

public abstract class GaudiRecorder {

	protected boolean recording;
	protected String name;
	
	protected long startrecording;
	protected long timelastevent;
	
	public GaudiRecorder(String name){
		this.name = name;
		this.recording = false;
	}
	
	public void startRecording(){
		
		try {
			System.err.println("[GAUDI][GaudiRecorder] sleeping for "+Client.someOptions.getTimeout()+" ms....");
			Thread.sleep(Client.someOptions.getTimeout());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("[GaudiRecorder]-Start Recording");
		this.startrecording = Calendar.getInstance().getTimeInMillis();
		this.timelastevent = this.startrecording;
		this.recording = true;
	}
	
	public void stopRecording(){
		System.out.println("[GaudiRecorder]-Stop Recording");
		this.recording = false;
	}
	
	abstract public void recordEventSequence(Exception e);
	
	abstract public void printSequence();

	abstract public void processEvent(AWTEvent arg0);
}
