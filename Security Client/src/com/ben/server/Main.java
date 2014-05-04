package com.ben.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.ben.client.Message;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class Main {
	static final Logger logger = Logger.getLogger(Main.class);
	 static ArrayList<ClientThread> _listOfClients = new ArrayList<ClientThread>();
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		logger.info("Starting Server. Properties file read");
		Main m  = new Main();
		try{
			ListenThread L = new ListenThread(args,_listOfClients);
		L.start();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = "";

		   while (line.equalsIgnoreCase("quit") == false) {
		       line = in.readLine();
		      m.HandleInput(line,in);
		   }

		   in.close();
		
		
		}
		catch (Exception e)
		{
			logger.warn(e.toString());
		}
	}
	private void HandleInput(String input, BufferedReader in) throws IOException
	{
		if (_listOfClients.size() ==0)
		{
			System.out.println("No Clients connected. Nothing can be done.");
			System.out.println("Starting Server. Properties file read");
			return;
		}
		
		else if(input.equals("help") && _listOfClients.size() >0)
		{
			System.out.println("Number of connections = "+_listOfClients.size());
				for(int i = _listOfClients.size(); --i >= 0;) {
					System.out.println(i+") "+_listOfClients.get(i).toString());
					}
				
				System.out.println("Type number of connection to connect to");
		//}
	///	if(input.matches("-?\\d+"))
	//		{
			try{
				String line = in.readLine();
		
				System.out.println("Connecting to"+_listOfClients.get(Integer.valueOf(line)).toString()); 
				ClientThread ct = _listOfClients.get(Integer.valueOf(line));
				System.out.println("Type command to send");
				line = in.readLine();
				ct.sOutput.writeObject(new Message(Message.COMMAND,line));
				System.out.println("Sent Command");
		}
		
			
		catch(Exception e)
		{
			logger.warn(e.toString());
		}
		}
		else
		{
			logger.info("Type help to connect");
		}
		
		
	}
}
class ListenThread extends Thread {
	String directory="";
	boolean stollen = true;
	static ArrayList<ClientThread> _listOfClients;
	static final Logger logger = Logger.getLogger(ListenThread.class);
ListenThread(String[] args, ArrayList<ClientThread> _listOfClients) throws IOException
{
		this._listOfClients = _listOfClients;
	if (args.length>1)	
	{
		directory = args[0];
		if (args[1].equals("stollen"))
			{
			stollen =true;
			}
	}
	else
	{
		logger.info("Please specify \'java -jar thisapplication.jar [path to store images] [stollen]\'\n");
		logger.info("Example - \'java -jar thisapplication.jar \\home\\pics stollen \' if the pc is stollen.Type NotStollen if it is not stollen");
	System.exit(0);
	}
	
		logger.info("Stollen = "+stollen);
		logger.info("Writing Images to "+directory);
	
		
}
public void run() 
{
try{
		ServerSocket serverSocket = new ServerSocket(5124);

		// infinite loop to wait for connections
		logger.info("Server waiting for Clients on port " + 5124 + ".");
		while(true)
		{
			Socket socket = serverSocket.accept();  	// accept connection
			
			
			ClientThread t = new ClientThread(socket,stollen,directory);  // make a thread of it
			t.start();
			_listOfClients.add(t);
			SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD HHmmss");
			Calendar cal = Calendar.getInstance();
		    logger.info(sdf.format(cal.getTime())+" Connection Received from  " + socket .getInetAddress() + " on port "
		             + socket .getPort() + " to port " + socket .getLocalPort() + " of "
		             + socket .getLocalAddress());
				
		//	sOutput.writeObject("Test response");
		
	}
}
catch(Exception e)
{
	logger.warn(e.toString());
}
	
	
	
}

}
class ClientThread extends Thread {
	// the socket where to listen/talk
	static final Logger logger = Logger.getLogger(ClientThread.class);
	Socket socket;
	ObjectInputStream sInput;
	ObjectOutputStream sOutput;
	Boolean Stollen =true;

	Message input;
	String directory;
	
	ClientThread(Socket socket, Boolean stollen,String directory) {
		this.Stollen = stollen;
		this.socket = socket;
		this.directory = directory;
		/* Creating both Data Stream */
		logger.info("Thread trying to create Object Input/Output Streams");
		try
		{
			// create output first
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			sInput  = new ObjectInputStream(socket.getInputStream());
		}
		catch (IOException e) {
			logger.warn(e.toString());
			return;
		}
		
      
	}
	public void run() {
		// to loop until LOGOUT
		boolean keepGoing = true;
		while(keepGoing) {
			// read a String (which is an object)
			try {
				logger.info("Awaiting messages from "+socket.getInetAddress());
				input =  (Message) sInput.readObject();
				logger.info("Received Message");
				switch(input.getType()) {

				case Message.ACK:
					logger.info("Received Ack from "+socket.getInetAddress() +" with message : "+ input.getMessage());
					break;
				case Message.LOGIN:
					if (Stollen)
					{
						sOutput.writeObject(new Message(Message.STOLLEN,"Stollen"));
						logger.info("Received Login Message - Sent Stollen message back");
					}
					else
					{
					sOutput.writeObject(new Message(Message.ACK,"testjessage"));
					logger.info("Received Login Message");
					}
					break;
				case Message.IMAGE:
					logger.info("Received Image from" +socket.getInetAddress());
					// ImageIO.write(input., "JPG", new File("c:\\test\\test.jpg"));
					SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD HHmmss");
					Calendar cal = Calendar.getInstance();
					String path = directory + socket .getInetAddress()+"-"+sdf.format(cal.getTime())+".jpg";
					 FileOutputStream fos = new FileOutputStream(path);
					 try {
					     fos.write(input.getImage());
					 }
					 finally {
						 logger.info("Wrote image to "+path);
					     fos.close();
					 }
					
					break;
				}
			}
			catch (Exception e) {
				logger.warn(e.toString());
				break;				
			}
		
			
		}
	}
}
			

