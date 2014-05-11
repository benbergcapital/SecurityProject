package com.ben.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.ben.common.Message;

public class FileTransfer extends Thread{
	ObjectOutputStream sOutput;
	String filename="";
//	String directory ="";
	FileTransfer(ObjectOutputStream sOutput,String filename) {
		 this.sOutput = sOutput;
		 this.filename = filename;
	//	 this.directory = directory;
	}
	
	 public void run()
	 {
		 try
			{
	
			 File file = new File(filename);
			 
			  byte[] b = new byte[(int) file.length()];
			  FileInputStream fileInputStream = new FileInputStream(file);
              fileInputStream.read(b);
              sOutput.writeObject(new Message(Message.ACK,"Sending File "+filename+"("+b.length+"bytes)"));
              sOutput.writeObject(new Message(Message.FILE,filename,b));
              
              fileInputStream.close();
              
			}
		 catch (Exception e)
		 {
			System.out.println(e.toString()); 
		
			try {
				sOutput.writeObject(new Message(Message.MESSAGE,e.toString()));
			} catch (IOException e1) {
				System.out.println(e.toString()); 
			}
			 
		 }
			
	 }
	
	
	
}
