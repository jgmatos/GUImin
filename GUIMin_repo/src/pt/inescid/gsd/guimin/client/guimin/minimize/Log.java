package pt.inescid.gsd.guimin.client.guimin.minimize;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Log the steps of the anonymization procedure

 * @author João Gouveia de Matos
 *
 */
public class Log {

	
	
	private static ArrayList<String> log = new ArrayList<String>();
	
	
	private static Long startTime = System.currentTimeMillis();
	
	
	public static void log(String entry) {
		
		log.add("[Log]["+(System.currentTimeMillis()-startTime)+"]> "+entry);
		System.err.println(entry);
	}
	
	
	
	
	public static void flush() {

		File f = new File("gaudi/");
		if(!f.exists())
			f.mkdir();
		f = new File("gaudi/guiminlogs/");
		if(!f.exists())
			f.mkdir();
		f = new File("gaudi/guiminlogs/dialogminimal/");
		if(!f.exists())
			f.mkdir();

		f = new File("gaudi/guiminlogs/dialogminimal/log"+System.currentTimeMillis()+".txt");

		try{

			FileWriter fr = new FileWriter(f);
			for(String entry: log)
				fr.write(entry+"\n");
			fr.flush();
			fr.close();
			System.err.println("[GUIMIN][LOG]-log recorded in file: " + f.getAbsolutePath());

		}catch (Exception e1){
			System.err.println("Error: " + e1.getMessage());
		}


	}
	
	
	
	
}
