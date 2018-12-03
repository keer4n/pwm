package com.client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
/**
 * @author keer4n
 *
 */
public class Client 
{ 
    // initialize socket and input output streams 
    private Socket socket            = null; 
    //private DataInputStream  input   = null; 
    //private DataOutputStream out     = null; 
    private File file = null;
    private Scanner scanner = null;
    private ObjectOutputStream send = null;
    private ObjectInputStream  receive = null;
   
    
    public Client(String address, int port) 
    { 
        // establish a connection 
        try
        { 
            socket = new Socket(address, port); 
            System.out.println("Connected");  

        	send = new ObjectOutputStream(socket.getOutputStream());
        	receive = new ObjectInputStream(socket.getInputStream());
            // takes input from terminal 
         //   input  = new DataInputStream(System.in); 
                // sends output to the socket 
      //      out    = new DataOutputStream(socket.getOutputStream()); 
           
        } 
        catch(Exception e) 
        { 
            System.out.println(e); 
        } 
        
      
    } 
    
    public void close() {
    	  try
          { 
              send.close();
             // input.close(); 
            //  out.close(); 
              socket.close(); 
    
          } 
          catch(IOException i) 
          { 
              System.out.println(i); 
          } 
    }
    
    public void receive() throws Exception{
    	//ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
    	String in2 = (String) receive.readObject();
    	System.out.println(in2);
    	//in.close();
    }
    public void send() throws Exception {
    	send = new ObjectOutputStream(socket.getOutputStream());		
    }
    
  
    
    public String readFile(String s) throws Exception {
    	file = new File(s);
    	scanner = new Scanner(file);
    	String payload = null;
    	send.writeUTF("incoming");
    	while(scanner.hasNext()) {
    		payload = scanner.nextLine();
    		send.writeUTF(payload);
    	}
    	send.writeUTF("EOF");
    	return "file sent";
    }
    
    public void send(Object o) throws Exception{
     	send.flush();
    	 send.writeObject(o);
    
    }
    
    

  
    /*public static void main(String args[]) 
    { 
    
        Client client = new Client("127.0.0.1", 5001); 
        Shell.createConsoleShell("client> ", "Enter '?list' to list all commands", client).commandLoop();
     
    } */
} 