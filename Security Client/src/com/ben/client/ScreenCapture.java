package com.ben.client;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;

public class ScreenCapture extends Thread{
	ObjectOutputStream sOutput;
	
	 ScreenCapture(ObjectOutputStream sOutput) {
		 this.sOutput = sOutput;
		 
	 }
	 
	 public void run()
	 {
		 try
			{
		while(true)
		{
			
	   Robot robot = new Robot();
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
     // ImageIO.write(screenShot, "JPG", new File("c:\\test\\test.jpg"));
 
        
        //       System.out.println(formatter.format(now.getTime()));
       
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( screenShot, "jpg",baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
	//	sOutput.writeObject(new Message(Message.LOGIN,"testjessage"));
		sOutput.writeObject(new Message(Message.IMAGE,imageInByte));
		Thread.sleep(30000); //wait 5 minutes and take another screenshot
		
		}
		
			}
		 catch (Exception e)
		 {
			 System.out.println(e.toString());
		 }
		
		
	}

}
