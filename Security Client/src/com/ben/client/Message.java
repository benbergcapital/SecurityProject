package com.ben.client;


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

	private int type;
	private String message;
	private String colour;
	private String size;
	private byte[] image;
	private String name;
	private Long receivingTime;
	// constructor
	public Message(int type, String message) {
		this.type = type;
		this.message = message;
		this.colour = colour;
		this.size = size;
	}
	
	Message(int type, byte[] image) {
		this.type = type;
		this.image = image;

	}
	

	// getters
	public int getType() {
		return type;
	}
	public String getMessage() {
		return message;
	}
	
	public byte[] getImage(){
		return image;
	}
	
}
