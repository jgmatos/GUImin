package pt.inescid.gsd.guimin.client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;

public class SomeOptions {

	
	
	
	private  long timeout=4000;
	
	
	
	private  boolean recButtonClicks = false;
	
	
	
	private boolean recAWTEvents = false;


	public SomeOptions() {
		
		Properties properties = new Properties();
		try {
			
			properties.load(new FileReader(System.getProperty("user.dir")+"/someOptions.properties"));
			timeout = Integer.valueOf(properties.getProperty("timeout"));
			//recButtonClicks = Boolean.valueOf(properties.getProperty("recButtonClicks"));
			//recAWTEvents = Boolean.valueOf(properties.getProperty("recAWTEvents"));
			
		} catch (FileNotFoundException e) {

			System.err.println("[GAUDI][SomeOptions] options properties file not provided.");
		
		} catch (Exception e) {
			
			System.err.println("[GAUDI][SomeOptions] a problem occurred while reading the properties file.");
			
		}
		
		
	}
	

	public  long getTimeout() {
		return timeout;
	}



	public  void setTimeout(long timeout) {
		this.timeout = timeout;
	}



	public  boolean isRecButtonClicks() {
		return recButtonClicks;
	}



	public  void setRecButtonClicks(boolean recButtonClicks) {
		this.recButtonClicks = recButtonClicks;
	}


	public boolean isRecAWTEvents() {
		return recAWTEvents;
	}


	public void setRecAWTEvents(boolean recAWTEvents) {
		this.recAWTEvents = recAWTEvents;
	}
	
	
	
	
	
	
	
	
	
	
}
