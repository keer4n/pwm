package com.client;

import java.math.BigInteger;

import com.tools.Cipher;

import asg.cliche.Command;
import asg.cliche.ShellFactory;

public class Main {
	private Client client;
	public Main() {
	 client = new Client("127.0.0.1",5001);
	}
	
	@Command 
	public void sendKeys()  throws Exception{
		client.send(new Cipher(new BigInteger("1"), new BigInteger("2")));
	}
	@Command
	public void tell(String s) throws Exception {
		//client.send();
		client.send(s);
		client.receive();
	}
	public void closer() throws Exception {
		client.close();
	}
	
	public static void main(String [] args) throws Exception {
		ShellFactory.createConsoleShell(">", "nothing", new Main()).commandLoop();
	}
	
}
