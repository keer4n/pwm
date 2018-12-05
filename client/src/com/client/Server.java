package com.client;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;

import com.tools.*;

import asg.cliche.Command;
import asg.cliche.ShellFactory;

public class Server {

	//initialize socket and input stream 
    private Socket          socket  , socketToCA = null; 
    private ServerSocket    server   = null; 
    private DataInputStream in       =  null; 
    private ObjectInputStream receive, receiveFromCA = null;

    private ObjectOutputStream send, sendToCA = null;
  
    private File file = null;
    private FileWriter fr = null;
    
    private Primitives prim = null;
    private PublicParameter pubParam = null;
    private Serverkey serverKey = null;
    
    private PetKey petKey = null;

    private EncryptedKeyTuple keyTuple = null;

    private UserReKey urk = null;
    
    public String hash(BigInteger i) throws Exception{
    	MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.reset();
		md.update(i.toByteArray());
		return md.digest().toString();
	}
    
    // constructor with port 
    public Server(int port) throws Exception 
    { 
    	serverKey = readKeyFile();
    	printParameters();
    	
    	petKey = new PetKey();
        // starts server and waits for a connection 
        try
        { 
            server = new ServerSocket(port); 
            socketToCA = new Socket("127.0.0.1",5002);
            System.out.println("Server started"); 
  
            System.out.println("Waiting for a client ..."); 
  
          //  socket = server.accept(); 
            System.out.println("Client accepted"); 
         //   receive = new ObjectInputStream(socket.getInputStream());  
            //send = new ObjectOutputStream(socket.getOutputStream());
            sendToCA = new ObjectOutputStream(socketToCA.getOutputStream());
            receiveFromCA = new ObjectInputStream(socketToCA.getInputStream());
            // takes input from the client socket 
         //   in = new DataInputStream( 
         //       new BufferedInputStream(socket.getInputStream())); 
            prim = new Primitives();
            serverKey = new Serverkey();
            
            while(true) {
           
           // receive();
      
           // Object in = receive.readObject();
            Object inFromCA = receiveFromCA.readObject();
            /*if (in instanceof Cipher) {
            	Cipher c = (Cipher) in;
            	System.out.println(c.getAlpha());
            	System.out.println(c.getBeta());
            } 
            if (in instanceof String) {
            	System.out.println(in);
            	send.writeObject(in);
            }
            */if(inFromCA instanceof PublicParameter) {
            	pubParam = (PublicParameter) inFromCA;
            	System.out.println("got public parameters");
            	serverKey.setG(pubParam.getG());
            	serverKey.setP(pubParam.getP());
            	
            }
            
            if(inFromCA instanceof PetKey) {
            	PetKey p = (PetKey) inFromCA;
            	System.out.println("got pet keys");
            	BigInteger[] petKeyPair = prim.genKeyPair(serverKey.getP(), serverKey.getG());
            	serverKey.setPubPet(petKeyPair[0]);
            	serverKey.setSecPet(petKeyPair[1]);
            	BigInteger petPubKey = serverKey.getPubPet().multiply(p.getCaPublicKey()).mod(serverKey.getP());
            	serverKey.setPubPet(petPubKey);
            	p.setCommonPublicKey(petPubKey);
            //	petKey = p;
            	System.out.println("sending pet keys");
            	sendToCA.writeObject(p);
            	this.printParameters();
            	
            	writeKeyFile(serverKey);
            }
            
            }
            //String cmd = ""; 
  
            // reads message from client until "Over" is sent 
            /*while (!cmd.equals("Over")) 
            { 
                try
                { 
                	Cipher c1 = (Cipher) (objIn.readObject());
                	System.out.println(c1.getAlpha());
                	System.out.println(c1.getBeta());
               // 	cmd = in.readUTF(); 
                    switch(cmd.toLowerCase()) {
                    case "incoming":
                    	System.out.println(writeFile("received"));
                    	break;
                    default:
                    	System.out.println("cannot understand " + cmd);
                    }
                    System.out.println(cmd); 
  
                } 
                catch(Exception i) 
                { 
                    System.out.println(i); 
                } 
            } */
           // System.out.println("Closing connection"); 
  
           
          //  in.close(); 
        } 
        catch(Exception i) 
        { 
            System.out.println(i); 
        } 
    } 
    
    public Server() {
    	prim = new Primitives();
    }
    @Command 
    public void readKeys() throws Exception {
    	
    	serverKey = (Serverkey) readObject("server.key");
    	pubParam = (PublicParameter) readObject("public.parameter");
    	petKey = (PetKey) readObject("pet.key");
    }
    @Command 
    public void establishPet() throws Exception {

    	BigInteger[] petKeyPair = prim.genKeyPair(serverKey.getP(), serverKey.getG());
    	serverKey.setPubPet(petKeyPair[0]);
    	serverKey.setSecPet(petKeyPair[1]);
    	BigInteger petPubKey = serverKey.getPubPet().multiply(petKey.getCaPublicKey()).mod(serverKey.getP());
		System.out.println("est pet"+petPubKey);
    	serverKey.setPubPet(petPubKey);
    	petKey.setCommonPublicKey(petPubKey);
    	pubParam.setCommonPublicKey(petPubKey);

    	writeObject(pubParam);
    	writeObject(petKey);
    	
    	writeKeyFile(serverKey);
    }
    
    public Object readObject(String s) throws Exception {
		if(s.equalsIgnoreCase("server.key")) {
			file = new File("server.key");
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			Serverkey c = (Serverkey) objIn.readObject();
			System.out.println("Server key file read");
			return c;
		}
	
		else if(s.equalsIgnoreCase("table")) {
			file = new File("table");
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			Table c = (Table) objIn.readObject();
			System.out.println("Parial Table file read");
			return c;
		}
		else if(s.equalsIgnoreCase("public.parameter"))  {
			file = new File("public.parameter");
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			PublicParameter c = (PublicParameter) objIn.readObject();
			System.out.println("Public parameters file read");
			return c;
		}
		else if(s.equalsIgnoreCase("pet.key")) {
			file = new File("pet.key");
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			PetKey c = (PetKey) objIn.readObject();
			System.out.println("Pet key file read");
			return c;
		}
		else  {
			file = new File(s);
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			UserReKey c = (UserReKey) objIn.readObject();
			System.out.println("UserReKey file read");
			urk = c;
			return c;
		}
	}


    
    public void writeObject(Object o) throws Exception {
		
		if(o instanceof Table) {
			file = new File("table");
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(o);
			System.out.println("Parial Table file written");
		}
		if(o  instanceof Serverkey) {
			file = new File("server.key");
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(o);
			System.out.println("Server key file written");
		}
		
		if(o instanceof PetKey) {
			file = new File("pet.key");
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(o);
			System.out.println("Pet key files written");
		}
		if(o instanceof PublicParameter) {
			file = new File("public.parameter");
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(o);
			System.out.println("Pub param key files written");

		}
	}
    
    public void receive() throws Exception {
    	receive = new ObjectInputStream(socket.getInputStream());  
        
    }
    
    public void close()  throws Exception{
    	 // close connection 
        receive.close();
        socket.close(); 
    }


    
    public String writeFile(String s) throws Exception {
    	file = new File(s);
    	file.createNewFile();
    	fr = new FileWriter(file,true);
    	String line = "";
    	while(!line.equals("EOF")) {
    		line = in.readUTF(); 
        	fr.write(line);      
        	fr.write(System.lineSeparator());   
    	}
    	fr.close();
    	return "File written";
    }
	public void writeKeyFile(Serverkey c) throws Exception {
		file = new File("server.key");
		file.createNewFile();
		FileOutputStream fileOut = new FileOutputStream(file);
		ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
		objOut.writeObject(c);
		System.out.println("key file written");
	}
	
	public Serverkey readKeyFile() throws Exception {
		file = new File("server.key");
		FileInputStream fileIn = new FileInputStream(file);
		ObjectInputStream objIn = new ObjectInputStream(fileIn);
		Serverkey c = (Serverkey) objIn.readObject();
		serverKey = c;
		return  c;
	}

	public EncryptedKeyTuple receiveEncryptedKeyTuple(EncryptedKeyTuple k){
    	keyTuple = new EncryptedKeyTuple();
    	for(int i = 0; i < 3; i++) {


			keyTuple.setElement(prim.serverTransform(pubParam.getP(), serverKey.getT()[i], k.getElement(i)), i); //transform too.
		}
    	return keyTuple;

	}

	public Table generateTable(EncryptedKeyTuple msg) {
    	Table ret = new Table(msg, pubParam);
    	ret.generateTable(serverKey);
    	return ret;
	}

	public EncryptedKeyTuple decryptTable(Table t) {
    	t.fullDecryption(serverKey.getSecPet());
    	EncryptedKeyTuple k = t.getTupleForClient();
    	return k;
	}

	public EncryptedKeyTuple reencryptTuple(EncryptedKeyTuple k) {
    	EncryptedKeyTuple ret = new EncryptedKeyTuple();
		System.out.println("urk.getS1 " + urk.getS1() + ", k.getElement = "+k.getElement(0));
    	ret.setElement(prim.serverReencrypt(pubParam.getP(),urk.getS1(),k.getElement(0)),0);
    	System.out.println("Got here");
		System.out.println("urk.getS2 " + urk.getS2() + ", k.getElement = "+k.getElement(1));
		ret.setElement(prim.serverReencrypt(pubParam.getP(),urk.getS2(),k.getElement(1)),1);
		System.out.println("Got here2");
		ret.setElement(prim.serverReencrypt(pubParam.getP(),urk.getS3(),k.getElement(2)),2);
		return ret;
	}

	@Command 
	public void printParameters() throws Exception{
		System.out.println("G:" +serverKey.getG());
		System.out.println("P:" + serverKey.getP());
		System.out.println("pubPET:" + serverKey.getPubPet());
		System.out.println("secPET:" + serverKey.getSecPet());
		System.out.println("y1: " + serverKey.getY1());
		System.out.println("y2: " + serverKey.getY2());
		System.out.println("y3: " + serverKey.getY3());
		System.out.println("t1: " + serverKey.getT1());

		System.out.println("t2: " + serverKey.getT1());
		System.out.println("t3: " + serverKey.getT1());
	
	}
  
   /* public static void main(String args[]) throws Exception
    { 
        //Server server = new Server(); 
    	ShellFactory.createConsoleShell(">", "nothing", new Server()).commandLoop();
    } 
*/
}
