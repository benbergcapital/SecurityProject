package com.ben.client;

public class OSValidator {
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	public boolean IsWindows()
	{
		if (OS.indexOf("win") >= 0)
			return true;
		else
			return false;
		
		
	}
	
	
}
