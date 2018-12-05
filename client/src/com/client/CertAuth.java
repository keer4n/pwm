package com.client;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import com.tools.*;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class CertAuth {
	
	private Socket socket  = null;
	private ServerSocket caServer = null;
	private ObjectInputStream receive = null;
	private ObjectOutputStream send = null;
	
	private File file = null;
	private FileWriter fileWriter = null;
	private FileReader fileReader = null;
	
	private Primitives prim = null;
	private CAkey caKey = null;
	private PetKey petKey = null;
	private PublicParameter pubParam = null;
	private Serverkey servKey = null;
	
	
	
	/*public String hash(BigInteger i) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.reset();
		md.update(i.toByteArray());
		return md.digest().toString();
	}*/
	
	public CertAuth(int port) throws Exception {
		
		caKey = readKeyFile();
		printParameters();
		
		caServer = new ServerSocket(port);
		System.out.println("Ca started");
		
		socket = caServer.accept();
		System.out.println("server accepted");
		receive = new ObjectInputStream(socket.getInputStream());
		send = new ObjectOutputStream(socket.getOutputStream());
		prim = new Primitives();
		caKey = new CAkey();
		petKey = new PetKey();
		pubParam = new PublicParameter();
		servKey  = new Serverkey();
	}
	
	public CertAuth() {
		prim = new Primitives();
		pubParam = new PublicParameter();
		servKey = new Serverkey();
		caKey = new CAkey();
		petKey = new PetKey();
	}

	@Command 
	public void generateKeys() throws Exception{
		BigInteger[] param = prim.genParameters();
		caKey.setP(param[0]);
		pubParam.setP(param[0]);
		servKey.setP(param[0]);
		servKey.setG(param[1]);
		caKey.setG(param[1]);
		pubParam.setG(param[1]);
		caKey.setK(param[2]);
	
		
		BigInteger[] petKeyPair = prim.genKeyPair(caKey.getP(), caKey.getG());
		caKey.setSecPet(petKeyPair[1]);
		petKey.setCaPublicKey(petKeyPair[0]);
		System.out.println("keys generated");
		
		BigInteger[] key1 = prim.genKeyPair(caKey.getP(), caKey.getG());
		pubParam.setY1(key1[0]);
		servKey.setY1(key1[0]);
		servKey.setT1(caKey.getK().subtract(key1[1]));
		//System.out.println("servKey + transformation: " + key1[1] + " "+  servKey.getT()[0] + " = " + caKey.getK());
		//System.out.println(key1[1].add(servKey.getT()[0]));
		BigInteger[] key2 = prim.genKeyPair(caKey.getP(), caKey.getG());
		pubParam.setY2(key2[0]);
		servKey.setY2(key2[0]);
		servKey.setT2(caKey.getK().subtract(key2[1]));
		BigInteger[] key3 = prim.genKeyPair(caKey.getP(), caKey.getG());
		pubParam.setY3(key3[0]);
		servKey.setY3(key3[0]);
		servKey.setT3(caKey.getK().subtract(key3[1]));
		
		writeObject(caKey);
		writeObject(pubParam);
		writeObject(petKey);
		writeObject(servKey);
		
	}
	
	@Command 
	public void generateUserKeys (String name) throws Exception {
		UserKey uk = new UserKey();
		uk.setUid(name+".user");
		UserReKey urk = new UserReKey();
		urk.setUid(name+".server");  // Changed the name to server
		
		BigInteger s1 = prim.generateRandom(256);
		urk.setS1(s1);
		BigInteger x1 = caKey.getK().subtract(s1);
		uk.setX1(x1);	
		BigInteger s2 = prim.generateRandom(256);
		urk.setS2(s2);
		BigInteger x2 = caKey.getK().subtract(s2);
		uk.setX2(x2);
		BigInteger s3 = prim.generateRandom(256);
		urk.setS3(s3);
		BigInteger x3 = caKey.getK().subtract(s3);
		uk.setX3(x3);
		
		writeObject(uk);
		writeObject(urk);
		
	}
	
/*	public void establishPetKey() throws Exception{
		send.writeObject(pubParam);
		send.writeObject(petKey);

		while(petKey.getCommonPublicKey()==null) {
			Object in =  receive.readObject();
			if(in instanceof PetKey) {
				PetKey p = (PetKey) in;
				caKey.setPubPet(p.getCommonPublicKey());
				System.out.println("establishing pet keys");
				petKey.setCommonPublicKey(p.getCommonPublicKey());

				writeKeyFile(caKey);
			}
		}
	}*/
	
	@Command 
	public void establishPet() throws Exception {
		petKey = (PetKey) readObject("pet.key");
		caKey.setPubPet(petKey.getCommonPublicKey());
		pubParam.setCommonPublicKey(petKey.getCommonPublicKey());
	}

	public Table receiveTable(Table t) {
		t.partialDecryptTable(caKey.getSecPet());
		t.exponentByRandom();
		return t;
	}

	public void writeObject(Object o) throws Exception {
		if(o instanceof CAkey) {
			file = new File("ca.key");
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(o);
			System.out.println("CA key file written");
		}
		if(o instanceof UserKey) {
			UserKey k = (UserKey) o;
			file = new File(k.getUid());
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(o);
			System.out.println("User key file written");
		}
		if(o instanceof UserReKey) {
			UserReKey k = (UserReKey) o;
			file = new File(k.getUid());
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(o);
			System.out.println("User reencryption key file written");
		}
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
		if(o instanceof PublicParameter)  {
			file = new File("public.parameter");
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(o);
			System.out.println("Public parameters file written");
		}
		if(o instanceof PetKey) {
			file = new File("pet.key");
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(o);
			System.out.println("Pet key files written");
			
		}
	}


	public Object readObject(String s) throws Exception {
		if(s.equalsIgnoreCase("ca.key")) {
			file = new File("ca.key");
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			CAkey c = (CAkey) objIn.readObject();
			System.out.println("CA key file read");
			return c;
		}
	
		if(s.equalsIgnoreCase("table")) {
			file = new File("table");
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			Table c = (Table) objIn.readObject();
			System.out.println("Parial Table file read");
			return c;
		}
		if(s.equalsIgnoreCase("public.parameter"))  {
			file = new File("public.parameter");
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			PublicParameter c = (PublicParameter) objIn.readObject();
			System.out.println("Public parameters file read");
			return c;
		}
		if(s.equalsIgnoreCase("pet.key")) {
			file = new File("pet.key");
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			PetKey c = (PetKey) objIn.readObject();
			System.out.println("Pet key file read");
			return c;
		}
		return null;
	}
	
	public void writeKeyFile(CAkey c) throws Exception {
		file = new File("ca.key");
		file.createNewFile();
		FileOutputStream fileOut = new FileOutputStream(file);
		ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
		objOut.writeObject(c);
		/*fileWriter = new FileWriter(file,true);
		fileWriter.write(c.getK().toString());
		fileWriter.write(System.lineSeparator());
		fileWriter.write(c.getP().toString());
		fileWriter.write(System.lineSeparator());
		fileWriter.write(c.getG().toString());
		fileWriter.write(System.lineSeparator());
		fileWriter.write(c.getSecPet().toString());
		fileWriter.write(System.lineSeparator());
		fileWriter.write(c.getPubPet().toString());
		fileWriter.write(System.lineSeparator());*/
		System.out.println("key file written");
	}
	
	public CAkey readKeyFile() throws Exception {
		//CAkey caKey = new CAkey();
		file = new File("ca.key");
		FileInputStream fileIn = new FileInputStream(file);
		ObjectInputStream objIn = new ObjectInputStream(fileIn);
		CAkey c = (CAkey) objIn.readObject();
		/*caKey = (CAkey) objIn.readObject();
		caKey.setK(new BigInteger(sc.nextLine()));
		caKey.setP(new BigInteger(sc.nextLine()));
		caKey.setG(new BigInteger(sc.nextLine()));
		caKey.setSecPet(new BigInteger(sc.nextLine()));
		caKey.setPubPet(new BigInteger(sc.nextLine()));
		System.out.println("read key file");*/
		return c;	
	}
	
	@Command 
	public void printParameters() throws Exception{
		System.out.println("K:" + caKey.getK());
		System.out.println("G:" +caKey.getG());
		System.out.println("P:" +caKey.getP());
		System.out.println("pubPET:" + caKey.getPubPet());
		System.out.println("secPET" + caKey.getSecPet());
	}
	
	/*public static void main(String [] args) throws Exception{
		//CertAuth ca = new CertAuth(5002);
		*//*CertAuth ca = new CertAuth();
		ca.generateKeys();
		ca.establishPet();
		ca.readKeyFile();
		ca.printParameters();
		System.out.println("this");
		while(true) {
			
		}
		*//*
		ShellFactory.createConsoleShell(">", "nothing", new CertAuth()).commandLoop();
	}*/
}
