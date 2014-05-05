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
    	
		while(true)
		{
			try {
		
			    URL url = new URL("http://www.google.com");
			    URLConnection conn = url.openConnection();
			    	conn.connect();
			  //internet connection is alive
			      	w.Start();
			     
			  } 
			  catch (Exception e) {
			   // no internet connection. 
			  }
		
		//waiting and trying again, no internet connection found.
			try
				{
				Thread.sleep(600000);
				}
			catch(Exception e)
				{
					
				}
		
		
		
		}
		
		
		
		
		
	}

}
