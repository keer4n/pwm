package com.client;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import com.tools.*;
import org.w3c.dom.Attr;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;
/**
 * @author keer4n
 *
 */
public class Client {
    // initialize socket and input output streams 
    private Socket socket = null;
    //private DataInputStream  input   = null; 
    //private DataOutputStream out     = null; 
    private File file = null;
    private Scanner scanner = null;
    private ObjectOutputStream send = null;
    private ObjectInputStream receive = null;

    private PublicParameter pubParam = null;
    private Primitives prim = null;
    private AttributeTuple atttrib = null;
    private UserKey uk = null;


    public Client(String address, int port) {
        // establish a connection 
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            send = new ObjectOutputStream(socket.getOutputStream());
            receive = new ObjectInputStream(socket.getInputStream());
            // takes input from terminal 
            //   input  = new DataInputStream(System.in);
            // sends output to the socket
            //      out    = new DataOutputStream(socket.getOutputStream());

        } catch (Exception e) {
            System.out.println(e);
        }


    } 
    
/*    public void close() {
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
    
    }*/

/*    public Object readObject(String s) throws Exception {
        if(s.equalsIgnoreCase())
    }*/

    @Command
    public UserKey readKeys(String name) throws Exception {
        file = new File(name + ".user");
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        UserKey ret = (UserKey) objIn.readObject();
        uk = ret;
        return ret;
    }

    public void readParameters() throws Exception {
        //file = new File("public.parameter");
        file = new File("public.parameter");
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        PublicParameter c = (PublicParameter) objIn.readObject();
        System.out.println("Client: Public parameters file read");
        this.pubParam = c;
        System.out.println(pubParam.getCommonPublicKey());
        System.out.println(pubParam.getG());
    }

    @Command //used by receiver
    public AttributeTuple readAttribute(String name) throws Exception {
        file = new File(name + ".attrib");
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        AttributeTuple ret = (AttributeTuple) objIn.readObject();
        atttrib = ret;
        return ret;
    }

    //user by receiver
    public EncryptedKeyTuple createEncryptedAttributes() {
        EncryptedKeyTuple ret = new EncryptedKeyTuple();
        for (int i = 0; i < 3; i++) {
            ret.setElement(prim.elgamalEncrypt(pubParam.getP(), pubParam.getG(), pubParam.getCommonPublicKey(), atttrib.getAttribute(i)), i);
        }
        return ret;
    }

    //public

    @Command
    //used by sender
    public EncryptedKeyTuple createEncryptedKeyTuple(String attribRequirement, BigInteger k) throws Exception {
        EncryptedKeyTuple ret = new EncryptedKeyTuple();
        if (attribRequirement.length() != 3)
            System.out.println("try again, 1 for required, 2 for dont care and 0 for strictly not required.");
        int countRequired = 0;
        for (char c : attribRequirement.toCharArray()) {
            if (c == '1') countRequired++;
        }
        BigInteger[] subkey = null;
        if(countRequired>1)
           subkey = prim.generateRandomMultiplier(countRequired, pubParam.getP(), k);
        int i = 0;
        for (char c : attribRequirement.toCharArray()) {
            switch (c) {
                case '1':
                    ret.setElement(prim.elgamalEncrypt(pubParam.getP(), pubParam.getG(), pubParam.getY()[i], subkey[i]), i);
                    i++;
                    break;
                case '2':
                    ret.setElement(prim.elgamalEncrypt(pubParam.getP(), pubParam.getG(), pubParam.getY()[i], BigInteger.ONE), i);
                    System.out.println("got here switch");
                    i++;
                    break;
                case '0':
                    ret.setElement(prim.elgamalEncrypt(pubParam.getP(), pubParam.getG(), pubParam.getY()[i], prim.generateRandom(100)), i);
                    i++;
                    break;
            }

        }
        return ret;
    }

    //used by receiver
    public BigInteger generateSymmetricKey(EncryptedKeyTuple k) {
        BigInteger ret = BigInteger.ONE;

        for (int i = 0; i < 3; i++) {
            ret = ret.multiply(prim.elgamalDecrypt(pubParam.getP(), uk.getX()[i], k.getElement(i))).mod(pubParam.getP());
        }
        return ret;
    }

    public void readObject(String s) throws Exception{
        file =new File(s);
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        UserKey c = (UserKey) objIn.readObject();
        System.out.println("User Key file read");
        uk =c;
    }

    public Client() throws Exception{
        readParameters();
        prim = new Primitives();
        atttrib = new AttributeTuple();
        uk = new UserKey();
    }

    public EncryptedKeyTuple encryptAttributeTuple(AttributeTuple receiverAttributeTuple) {
        EncryptedKeyTuple receiverEncKeyTuple = new EncryptedKeyTuple();
        receiverEncKeyTuple.setElement(prim.elgamalEncrypt(pubParam.getP(),pubParam.getG(),pubParam.getCommonPublicKey(),receiverAttributeTuple.getAttribute(0)),0);
        receiverEncKeyTuple.setElement(prim.elgamalEncrypt(pubParam.getP(),pubParam.getG(),pubParam.getCommonPublicKey(),receiverAttributeTuple.getAttribute(1)),1);
        receiverEncKeyTuple.setElement(prim.elgamalEncrypt(pubParam.getP(),pubParam.getG(),pubParam.getCommonPublicKey(),receiverAttributeTuple.getAttribute(2)),2);
        return receiverEncKeyTuple;
    }

    public BigInteger decryptKeyTuple(EncryptedKeyTuple k) {
        BigInteger k1 = prim.elgamalDecrypt(pubParam.getP(),uk.getX1(),k.getElement(0));
        BigInteger k2 = prim.elgamalDecrypt(pubParam.getP(),uk.getX2(),k.getElement(1));
        BigInteger k3 = prim.elgamalDecrypt(pubParam.getP(),uk.getX3(),k.getElement(2));
        System.out.println(k1 + " " + k2 + " " + k3);

        return k1.multiply(k2).mod(pubParam.getP()).multiply(k3).mod(pubParam.getP());
    }
    public void printTable(Table t, int table_num){
        for(int i = 0; i < 8; ++i){
            for(int j = 0; j < 3; ++j){
                System.out .println("table " + table_num + ": "+t.getRow(i).getIndexCol(j).getAlpha());
                System.out.println(t.getRow(i).getOutput().getElement(0));
                System.out.println(t.getRow(i).getOutput().getElement(1));
                System.out.println(t.getRow(i).getOutput().getElement(2));
            }
        }
    }

    public byte[] symmetricEncrypt(BigInteger senderKey, String message) throws Exception{
        byte[] key = senderKey.toByteArray();
        byte[] dataToSend = message.getBytes();

        javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(key, "AES");
        c.init(Cipher.ENCRYPT_MODE,k);
        return c.doFinal(dataToSend);
    }

    public String symmetricDecrypt(BigInteger senderKey, byte[] encryptedMessage) throws Exception{
        byte[] key = senderKey.toByteArray();

        javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(key, "AES");
        c.init(Cipher.DECRYPT_MODE,k);
        byte[] d = c.doFinal(encryptedMessage);
        String s = new String(d);
        return s;
    }

    public BigInteger random(int i) {
        /*for(;;) {
            BigInteger rand = prim.generateRandom(i);
            if(rand.toByteArray().length == 16) {
                return rand;
            }
        }*/
        return prim.generateRandom(i);
    }

    public static void main(String [] args) throws Exception {
//        ShellFactory.createConsoleShell(">", "nothing", new Client()).commandLoop();

        CertAuth ca = new CertAuth();
        ca.generateKeys();
        Server server = new Server();
        server.readKeys();
        server.establishPet();
        ca.establishPet();
        ca.readKeyFile();
        server.readKeyFile();
        //create a sender
        Client sender = new Client();
        BigInteger senderKey = sender.random(256);
        String secret = "this is ";
        byte[] encrypted = sender.symmetricEncrypt(senderKey,secret);
        EncryptedKeyTuple msg = sender.createEncryptedKeyTuple("112",senderKey);

        EncryptedKeyTuple k = server.receiveEncryptedKeyTuple(msg);

        //create a receiver
        //ca generates the keys for receiver and reenc keys for server .
        Client receiver = new Client();
        ca.generateUserKeys("bob");
        receiver.readObject("bob.user");

        server.readObject("bob.server");

        //receiver sends the request to the server, encryptedAttributeTuple
        AttributeTuple receiverAttributeTuple = new AttributeTuple();
        receiverAttributeTuple.set(0);
        receiverAttributeTuple.unset(1);
        receiverAttributeTuple.set(2);

        EncryptedKeyTuple receiverEncKeyTuple = receiver.encryptAttributeTuple(receiverAttributeTuple);

        //server generates the table divides it by the receivers tuple and raises it to some random number
        Table table = server.generateTable(k);
        table.divideByClientTuple(receiverEncKeyTuple);
        table.exponentByRandom();


        //server sends the table to the ca
        //ca partially decrypts and raises it to random and shuffles
        //table.mixRows();;
        Table blindedTable = ca.receiveTable(table);

        //ca sends the table to server
         //server decrypts the table and does the matching
        EncryptedKeyTuple receiverKeyTuple = server.decryptTable(blindedTable);


        //does the reeccryption and sends the output tuple to the receiver
        EncryptedKeyTuple finalKeyTuple = server.reencryptTuple(receiverKeyTuple);

        //receiver decrypts and multiplies
        System.out.println(receiver.generateSymmetricKey(finalKeyTuple));
        System.out.println("Final key " + receiver.decryptKeyTuple(finalKeyTuple));
        BigInteger secretKey = receiver.decryptKeyTuple(finalKeyTuple);
        System.out.println("Decryption: " + receiver.symmetricDecrypt(secretKey,encrypted));









    }

    

  
    /*public static void main(String args[]) 
    { 
    
        Client client = new Client("127.0.0.1", 5001); 
        Shell.createConsoleShell("client> ", "Enter '?list' to list all commands", client).commandLoop();
     
    } */
} 