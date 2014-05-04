package com.ben.client;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class ConnectionManager {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		boolean connection=false;
		WorkerClass w = new WorkerClass();
    	
		while(!connection)
		{
		try {
			w.Start();
		    URL url = new URL("http://www.google.com");
		    URLConnection conn = url.openConnection();

		    if(conn.getContentLength() == -1){
		       //no internet connection
		    	connection = false;
		    	Thread.sleep(600000);
		    }
		    else
		    {
		    	//internet
		    	connection = true;
		//    	WorkerClass w = new WorkerClass();
		//    	w.Start();
		    }
		  } 
		  catch (Exception e) {
		   
		      e.printStackTrace();
		  }
		}
		//start service.
		
		
		
		
	}

}
