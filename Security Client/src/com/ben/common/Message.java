package com.ben.common;


import java.awt.Color;
import java.io.*;
import java.util.Date;
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server. 
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no 
 * need to count bytes or to wait for a line feed at the end of the frame
 */
public class Message implements Serializable {

	
	public static final int LOGIN = 1;

	public static final int LOGOUT = 3;

	public static final int IMAGE=4;


	public static final int ACK = 2;

	public static final int STOLLEN = 5;

	public static final int COMMAND = 6;

	public static final int PING = 0;

	public static final int MESSAGE = 7;

	public static final int FILE = 9;

	private int type;
	private String message;
	private byte[] data;
	private String name;
	private String filename;
	private String directory;
	// constructor
	public Message(int type, String message) {
		this.type = type;
		this.message = message;
		
		
	}
	
	public Message(int type, byte[] image) {
		this.type = type;
		this.data = image;

	}
	

	public Message(int type) {
		this.type=type;
		// TODO Auto-generated constructor stub
	}
//for requesting the file
	
		// TODO Auto-generated constructor stub
	
//for sending the file back
	public Message(int type, String filename, byte[] b) {
		this.type = type;
		this.filename = filename;
		this.data = b;
	}

	// getters
	public int getType() {
		return type;
	}
	public String getTypeByName()
	{
		switch(type){
			case  LOGIN:
				return "LOGIN";
				
			case LOGOUT:
				return "LOGOUT";
				
			case IMAGE:
				return "IMAGE";
			
			case ACK: 
				return "ACK";
				
			case STOLLEN: 
				return "STOLLEN";
			
			case COMMAND:
				return "COMMAND";
			
			case PING: 
				return "PING";
			
			case MESSAGE: 
				return "MESSAGE";
			
			case FILE: 
				return "FILE";
			
		}
		return "unknown";
		
	}
	public String getMessage() {
		return message;
	}
	
	public byte[] getData(){
		return data;
	}
	public String getDirectory(){
		return directory;
	}
	public String getFilename(){
		return filename;
	}
}
