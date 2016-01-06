package pt.inescid.gsd.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;



public class StacktraceUtils {





	public static String getThrowable(String stacktrace) {

		Scanner scan = new Scanner(stacktrace);
		if(scan.hasNextLine())
			return scan.nextLine();

		return "";
	}

	public static String getThrowable(InputStream stream) {

		Scanner scan = new Scanner(stream);
		if(scan.hasNextLine())
			return scan.nextLine();

		return "";
	}

	public static ArrayList<Location> processStackTrace(String stacktrace) {

		ArrayList<Location> processed = new ArrayList<Location>();
		Scanner scan = new Scanner(stacktrace);

		if(scan.hasNextLine())//skip first line
			scan.nextLine();

		while(scan.hasNextLine())
			processed.add(getLocation(scan.nextLine()));

		return processed;

	}

	public static ArrayList<Location> processStackTrace(String stacktrace,String[] blacklist) {

		if(blacklist==null||blacklist.length<=0)
			return processStackTrace(stacktrace);

		
		ArrayList<Location> processed = new ArrayList<Location>();
		Scanner scan = new Scanner(stacktrace);

		if(scan.hasNextLine())//skip first line
			scan.nextLine();

		boolean valid;
		String line="";
		while(scan.hasNextLine()){
			valid = true;
			line = scan.nextLine();
			for(String s: blacklist)
				if(line.contains(s))
					valid = false;
			if(valid)
				processed.add(getLocation(line));
		}
		return processed;

	}


	public static ArrayList<Location> processStackTrace(InputStream stream) {

		ArrayList<Location> processed = new ArrayList<Location>();
		Scanner scan = new Scanner(stream);

		if(scan.hasNextLine())//skip first line
			scan.nextLine();

		while(scan.hasNextLine())
			processed.add(getLocation(scan.nextLine()));

		return processed;

	}
	
	public static ArrayList<Location> processStackTrace(InputStream stream,String [] blacklist) {

		if(blacklist==null||blacklist.length<=0)
			return processStackTrace(stream);
		
		ArrayList<Location> processed = new ArrayList<Location>();
		Scanner scan = new Scanner(stream);

		if(scan.hasNextLine())//skip first line
			scan.nextLine();

		boolean valid;
		String line="";
		while(scan.hasNextLine()){
			valid = true;
			line = scan.nextLine();
			for(String s: blacklist)
				if(line.contains(s))
					valid = false;
			if(valid)
				processed.add(getLocation(line));
		}

		return processed;

	}

	public static Location getLocation(String line) {

		return new Location(getClass(line), getLineNumber(line));

	}

	public static int getLineNumber(String line) {

		return Integer.valueOf(line.substring(line.indexOf(":")+1,line.length()-1));

	}

	public static String getClass(String line) {

		String newline="";
		newline = line.substring(line.indexOf("at ")+3);
		newline = newline.substring(0,newline.lastIndexOf("."));
		newline = newline.substring(0,newline.lastIndexOf("."));

		return newline; 

	}


	
	
	
	
	public static Location getLocation(Throwable t) {
		
		return new Location(t.getStackTrace()[0].getClassName(),t.getStackTrace()[0].getLineNumber());
		
	}
	
	





}
