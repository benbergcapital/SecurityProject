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
				    	//internet connection is alive, lets start connecting to the server
				      w.Start();
				     
				    	  
				    	  //Process conencted to server, now sit here and see if a disconnection happens
				    	  //If a disconnection happens, we want the loop to continue until the connection is restored.
				      
				     
			  } 
			  catch (Exception e) {
				   // no internet connection. 
					  try 
					  	{
						  	Thread.sleep(600000);
					  	} 
					  catch (InterruptedException e1) 
					  	{
					  		e1.printStackTrace();
					  	}
			  }
		
		//waiting and trying again, no internet connection found.
			
		
		
		
		}
		
		
		
		
		
	}

}
