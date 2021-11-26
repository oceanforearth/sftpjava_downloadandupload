package com.btsl.process;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class testDate {

	public static void main(String[] args) {
		 Date now = new Date();
		SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd hh:mm:ss",Locale.US);
		String Currentdatetime = timeFormatter.format(now);
		System.out.println("Currentdatetime :"+Currentdatetime);
		//String path_progerty = System.getProperty("java.classpath");
		//System.out.println("java.classpath :"+path_progerty);
	    System.setProperty("java.classpath","./lib;%JAVA_HOME%/lib;.");
	    String path_progerty = System.getProperty("java.classpath");
		System.out.println("java.classpath :"+path_progerty);
		
		
	}
}
