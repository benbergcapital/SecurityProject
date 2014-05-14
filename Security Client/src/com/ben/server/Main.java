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

import com.ben.common.Message;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class Main {
	static final Logger logger = Logger.getLogger(Main.class);
	 static ArrayList<ClientThread> _listOfClients = new ArrayList<ClientThread>();
	public static void main(String[] args) {
		PropertyConfigurator.configure("/home/azureuser/SecurityServer/log4j.properties");
	//	PropertyConfigurator.configure("c:\\log4j.properties");
		AsciiGenerator A = new AsciiGenerator();
		logger.info("Starting Server. Properties file read");
		for (int i = 0; i < A.Diamond.length; ++i) 
		{
			logger.info(A.Diamond[i]);
		//  System.out.print(A.b[i]);
		///  System.out.print(A.e[i]);
	///	  System.out.print(A.n[i]);
	//	  System.out.print(A.b[i]);
	//	  System.out.print(A.e[i]);
//		  System.out.print(A.r[i]);
//		  System.out.println(A.g[i]);
		}
		Main m  = new Main();
		try{
			ListenThread L = new ListenThread(args,_listOfClients);
		L.start();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = "";

		   while (true) {
		       line = in.readLine();
		      m.HandleInput(line,in);
		   }

		 //  in.close();
		
		
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
			
			return;
		}
		
		else if(input.equals("help") && _listOfClients.size() >0)
		{
			System.out.println("Checking Connections, current total  = "+_listOfClients.size());
				for(int i = 0; i<_listOfClients.size(); i++) {
						if(!TestConnection(_listOfClients.get(i).sOutput))
						{
							
							System.out.println(_listOfClients.get(i).socket.getInetAddress()+" no longer active, removed from connections");
							_listOfClients.remove(i);
						}
						
					}
								
				if (_listOfClients.size()==0)
				{
					System.out.println("No active connections");
					return;
				}
				else
				{
					for(int i = 0; i<_listOfClients.size(); i++) {
					System.out.println(i+") "+_listOfClients.get(i).socket.getInetAddress());
					}
					
				System.out.println("Type number of connection to connect to");
				}
			try{
				String line = in.readLine();
		
				System.out.println("Connecting to"+_listOfClients.get(Integer.valueOf(line)).socket.getInetAddress()); 
				ClientThread ct = _listOfClients.get(Integer.valueOf(line));
				
				
				System.out.println("Specify type of connection:");
				System.out.println("1) Send commands");
				System.out.println("2) Request File");
			//	System.out.println("3) Change Server Status");
				line = in.readLine();
				if (line.equals("2"))
				{
					System.out.println("Specify path");
					line = in.readLine();
					ct.sOutput.writeObject(new Message(Message.FILE,line));
					System.out.println("File Requested");
					
				}
				/*		if (line.equals("3"))
				{
					System.out.println("Specify new run state");
					System.out.println("1) ACTIVE - Client remains running awaiting commands");
					System.out.println("2) STOLLEN - Client remains running awaiting commands and sending screenshots every 5 minutes");
					System.out.println("3) NOTSTOLLEN - Client shutsdown and does nothing");
					line = in.readLine();
					switch (line){
					case "1":
						
					case "2":
					default:
						break
				}
					System.out.println("File Requested");
					
				}*/
				else
				{
					
					while(line != "quit")
					{
					System.out.println("Type command to send");
					line = in.readLine();
							if (line.equals("quit"))
							{
							break;
							}
					ct.sOutput.writeObject(new Message(Message.COMMAND,line));
					System.out.println("Sent Command");
			
					}
				}
			}
			
		catch(Exception e)
		{
			logger.warn(e.toString());
		}
		}
		else
		{
			System.out.println("Type help to connect");
		}
		
		
	}
	
	private boolean TestConnection(ObjectOutputStream sOutput)
	{
		try {
			sOutput.writeObject(new Message(Message.PING));
			return true;
		} catch (IOException e) {
			return false;
			
		}
		
		
	}
}
class ListenThread extends Thread {
	String directory="";
	boolean stollen = false;
	RunState _runningState;
	static ArrayList<ClientThread> _listOfClients;
	static final Logger logger = Logger.getLogger(ListenThread.class);
	int port=5124;
ListenThread(String[] args, ArrayList<ClientThread> _listOfClients) throws IOException
{
		this._listOfClients = _listOfClients;
	if (args.length>2)	
	{
		port = Integer.valueOf(args[0]);
		directory = args[1];
		if (args[2].equals("stollen"))
			{
			//stollen =true;
			_runningState = RunState.STOLLEN;
			}
		else if (args[2].equals("notstollen"))
		{
		//stollen =true;
		_runningState = RunState.NOTSTOLLEN;
		}
		else 
		{
		//stollen =true;
		_runningState = RunState.ACTIVE;
		}
		
	}
	else
	{
		logger.info("Please specify \'java -jar thisapplication.jar [port] [path to store images] [stollen/notstollen/active]\'\n");
		logger.info("Example - \'java -jar thisapplication.jar 5124 \\home\\azureuser\\SecurityServer\\ClientScreenshots stollen \' if the pc is stollen.Type NotStollen if it is not stollen");
		logger.info("notstollen (shuts down the client app when it runs), stollen (Keeps client active sending screenshots every 5 minutes), active (keeps client active awaiting commands)");
		System.exit(0);
	}
	
		logger.info("State = "+_runningState.toString());
		logger.info("Writing Images to "+directory);
	
		
}

public void run() 
{
try{
		ServerSocket serverSocket = new ServerSocket(port);

		// infinite loop to wait for connections
		
		while(true)
		{
			logger.info("Server waiting for Clients on port " + 5124 + ".");
			Socket socket = serverSocket.accept();  	// accept connection
			logger.info(" Connection Received from  " + socket .getInetAddress() + " on port "
		             + socket .getPort() + " to port " + socket .getLocalPort() + " of "
		             + socket .getLocalAddress());
			
			ClientThread t = new ClientThread();  // make a thread of it
			boolean state = t.ClientThread(socket,_runningState,directory);
			
			if (state)
			{
				logger.info("Stream created, starting listen thread");
				t.start();
			_listOfClients.add(t);
		
		    
				
		
			}
			else
			{
				logger.info("IO stream errored, dropping connection");
			}
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
	//Boolean Stollen =true;
	RunState _runningState;
	String Hostname="";
	Message input;
	String directory;
	
	ClientThread()
	{
		return;
	}
	
	public boolean ClientThread(Socket socket, RunState _runningState2,String directory) {
		this._runningState = _runningState2;		
		this.socket = socket;
		this.directory = directory;
		/* Creating both Data Stream */
		logger.info("Thread trying to create Object Input/Output Streams");
		try
		{
			// create output first
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			sInput  = new ObjectInputStream(socket.getInputStream());
			return true;
		}
		catch (IOException e) {
			logger.warn(e.toString());
			return false;
		}
		
      
	}
	
	public void run() {
		// to loop until LOGOUT
		boolean keepGoing = true;
		
		while(keepGoing) {
			// read a String (which is an object)
			try {
				//logger.info("Awaiting messages from "+socket.getInetAddress());
				input =  (Message) sInput.readObject();
				logger.info("Received "+input.getTypeByName());
				switch(input.getType()) {

				case Message.ACK:
					logger.info("Received Ack from "+socket.getInetAddress() +" with message : "+ input.getMessage());
					if (_runningState.equals(RunState.NOTSTOLLEN))
						{
						sOutput.writeObject(new Message(Message.LOGOUT,"Ok, fine to shut down"));
						logger.info("Sent Logout message to "+socket.getInetAddress());
						}
					break;
				case Message.MESSAGE:
					logger.info("Received message :"+input.getMessage());
					break;
				case Message.LOGIN:
					Hostname = input.getMessage();
					logger.info("Received Login Message from "+Hostname);
					
					switch(_runningState)
						{
						case NOTSTOLLEN:	
							sOutput.writeObject(new Message(Message.ACK,"All is ok"));
							logger.info("Received Login Message - Sent not stollen back");
							break;
						case STOLLEN:
							sOutput.writeObject(new Message(Message.STOLLEN,"Stollen"));
							logger.info("Received Login Message - Sent Stollen message back");
							break;	
						case ACTIVE:
							sOutput.writeObject(new Message(Message.ACK,"All is ok, remain Active"));
							logger.info("Received Login Message - Sent active back");
							break;
						}
					break;
				case Message.IMAGE:
					logger.info("Received Image from " +socket.getInetAddress());
					// ImageIO.write(input., "JPG", new File("c:\\test\\test.jpg"));
					SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD HHmmss");
					Calendar cal = Calendar.getInstance();
					String path = directory + socket .getInetAddress()+"-"+sdf.format(cal.getTime())+".jpg";
					 FileOutputStream fos = new FileOutputStream(path);
					 try {
					     fos.write(input.getData());
					 }
					 finally {
						 logger.info("Wrote image to "+path);
					     fos.close();
					 }
					
					break;
				case Message.FILE:
					logger.info(input.getFilename());
					String[] array = input.getFilename().split("\\\\"); 
					logger.info("Received file from " +socket.getInetAddress());
					logger.info("Writing file to : "+array[array.length-1]);
					  FileOutputStream fileoutputstream = new FileOutputStream(array[array.length-1]);
		              fileoutputstream.write(input.getData());
					  fileoutputstream.close();
					break;
				}
			}
			catch (Exception e) {
				logger.warn(e.toString() + "("+Hostname+" : "+socket.getInetAddress()+")");
				break;				
			}
		
			
		}
	}
}
			

