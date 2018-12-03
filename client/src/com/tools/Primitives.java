package com.tools;
import java.net.*; 
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.jce.interfaces.ElGamalPrivateKey;
import org.bouncycastle.math.Primes;
import org.bouncycastle.crypto.generators.*;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.io.*; 
import java.security.*;
import java.util.Random;
import java.math.BigInteger;
  
/**
 * @author keer4n
 *
 */
public class Primitives 
{ 
    // initialize socket and input output streams 
    /*private Socket socket            = null; 
    private DataInputStream  input   = null; 
    private DataOutputStream out     = null; */
	
    SecureRandom r = new SecureRandom();
   ElGamalParametersGenerator params = new ElGamalParametersGenerator();
   ElGamalParameters pr ;
    ElGamalKeyPairGenerator pairGen = new ElGamalKeyPairGenerator();
    private ElGamalKeyGenerationParameters  a;
    Random rnd = new Random();
    AsymmetricCipherKeyPair key;
    
   // ElGamalKeyParameters pubk  = new ElGamalPublicKeyParameters(new BigInteger("123"), pr);
    //ElGamalKeyParameters prk = new ElGamalPrivateKeyParameters(new BigInteger("456"), pr);
   
    ElGamalEngine e = new ElGamalEngine();

	//ElGamalEngine d = new ElGamalEngine();
   public BigInteger []  genParameters() {
	   	params.init(512, 10, r);
   		pr = params.generateParameters();
   		BigInteger[] ret = new BigInteger[3];
   		BigInteger g = pr.getG();
   		BigInteger p = pr.getP();
   		BigInteger K = new BigInteger( 512,rnd);
   		ret[0] = p;
   		ret[1] = g;
   		ret[2] = K;
   		return ret;
   }
   
   public BigInteger[] genKeyPair(BigInteger p, BigInteger g) {

   		BigInteger secKey = new BigInteger(256,rnd);
   		BigInteger pubKey = g.modPow(secKey,p);
   		BigInteger ret[] = new BigInteger[2];
   		ret[0] = pubKey;
   		ret[1] = secKey;
   		return ret;
   }
   
   /*public void test1() {
	   System.out.println("running");
	   BigInteger[] param = genParameters();
	   System.out.println("running parameters" + param[0] + param[1]);
	   BigInteger[] keys = genKeyPair(param[0], param[1]);
	   System.out.println("running key pair");
	   Cipher c1 = elgamalEncrypt(param[0],param[1],keys[0], new BigInteger("3"));
	   System.out.println("running encryption");
	   Cipher c2 = elgamalEncrypt(param[0],param[1],keys[0], BigInteger.TEN);
	   System.out.println("running encryption");
	   Cipher c = homomorphicMultiply(c1,c2,param[0]);
	   System.out.println("running multiplication");
	   BigInteger d = elgamalDecrypt(param[0],keys[1],c);
	   System.out.println(d);
   }*/
   
   
/*    public  void testProc() {
    	
    	params.init(512, 10, r);
    	pr = params.generateParameters();
    	
    	BigInteger g = pr.getG();
    	System.out.println("g="+g);
    	BigInteger p = pr.getP();
    	System.out.println("p="+p);
    	
    	BigInteger x = new BigInteger(256,rnd);
    	
    	BigInteger y = g.modPow(x, p);
    	
    	BigInteger m = new BigInteger("kiran".getBytes());
    	
    	BigInteger c[] = elgamalEncrypt(p, g, y, m);
    	System.out.println("kE,y =" + c[0]+" ,"+ c[1]);
    	
    	BigInteger d = elgamalDecrypt(p, x, c);
    	System.out.println("d = " + d.toByteArray());
    	System.out.println("d = " + new String(d.toByteArray()));
    	
    	BigInteger K = new BigInteger( 512,rnd);
    	System.out.println(K);
    	
    	BigInteger x = new BigInteger(256,rnd);
    	System.out.println("x="+x);
    	BigInteger pubk = g.modPow(x, p);
    	
    	BigInteger s = K.subtract(x);
    	System.out.println("s="+s);
    	
    	BigInteger message = new BigInteger("kiran".getBytes());
    	System.out.println(message.bitLength());
    	System.out.println("message = " + message.toByteArray());
    	System.out.println("message = " + new String(message.toByteArray()));
    	
    	BigInteger cipher[] = elgamalEncrypt(p, g, pubk, message);
    	System.out.println("cipher = " + cipher[1].toByteArray());
    	System.out.println("cipher = " + new String(cipher[1].toByteArray()));
    	
    	BigInteger cipher1[] = serverTransform(p, s, cipher);
    	System.out.println("cipher1 = " + cipher1[1].toByteArray());
    	System.out.println("cipher1 = " + new String(cipher1[1].toByteArray()));
    	
    	BigInteger blind[] = blinder(p, g, pubk, s, cipher1, BigInteger.ONE);
    	System.out.println("blinder = " + blind[1].toByteArray());
    	System.out.println("blinded = " + new String(blind[1].toByteArray()));
    	
 
    	BigInteger x1 = new BigInteger(256,rnd);
    	System.out.println("x1="+x1);
    	BigInteger s2 = K.subtract(x1);
    	System.out.println("s2="+s2);
    	System.out.println("sum="+x1.add(s2));
    	
    
    	BigInteger cipher2[] = serverReencrypt(p, s2, blind);
    	System.out.println("cipher2 = " + cipher2[1].toByteArray());
    	System.out.println("cipher2 = " + new String(cipher2[1].toByteArray()));
    	
    	System.out.println("sum="+x.add(s));
    	
    	BigInteger dec = elgamalDecrypt(p, x1, cipher2);
    	System.out.println("dec = " + dec.toByteArray());
    	System.out.println("dec = " + new String(dec.toByteArray()));
    	
    	BigInteger xx = new BigInteger(256,rnd);
    	BigInteger pubkx = g.modPow(xx, p);
    	BigInteger messagex = new BigInteger("kiran".getBytes());
    	BigInteger[] cipherx = elgamalEncrypt(p, g, pubkx, messagex);
    	System.out.println("cipher0 " +cipherx[0]);
    	System.out.println("cipher1 " +cipherx[1]);
    
    	System.out.println(Primes.isMRProbablePrime(p, r, 1000));
    	
    	System.out.println(pet(p, x, xx, cipher, cipherx));
    	
    	BigInteger y = g.modPow(x, p);
    	String m = "k";
    	
    	BigInteger ca = g.modPow(new BigInteger("1234"), p);
    	
    	BigInteger cb = y.mod(new BigInteger("1234")).multiply(new BigInteger("123")).mod(p);
    	
    	BigInteger cb1 = cb.multiply(ca.mod(s)).mod(p);
    	
    	BigInteger cb2 = cb1.divide(ca.mod(s2)).mod(p);
    	
    	BigInteger dec = cb2.divide(ca.mod(x1)).mod(p);
    	System.out.println("out");
    	System.out.println("dec = "+dec);
    	
    	
    			a = new ElGamalKeyGenerationParameters(r,pr);
    	pairGen.init(a);
    	key = pairGen.generateKeyPair();
    	
    	ElGamalPrivateKeyParameters prk = (ElGamalPrivateKeyParameters) key.getPrivate();
    	System.out.println(prk.getX());
    	
    	
    	
    	BigInteger xu = prk.getX();
    	BigInteger su = K.subtract(xu);
    	
    	ElGamalPublicKeyParameters pubk = (ElGamalPublicKeyParameters) key.getPublic();
    	System.out.println(pubk.getY());
    	
    	e.init(true, pubk);
    	
    	String s = "kiran";
    	byte [] ba = s.getBytes();
    	System.out.println(new String(ba));
    	byte[] k = e.processBlock(ba, 0, ba.length);
    	System.out.println(k);
    	
    	ElGamalPrivateKeyParameters pubs = new ElGamalPrivateKeyParameters(su, pr);

    	ElGamalPrivateKeyParameters pubx = new ElGamalPrivateKeyParameters(xu, pr);
    	e.init(false,pubs);
    	byte [] intermidiate = e.processBlock(k iate));
    	e.init(false, pubx);
    	System.out.println(new String(e.processBlock(intermidiate,0,intermidiate.length)));
    	//e.init(false, key.getPrivate());
    	//System.out.println(new String(e.processBlock(k,0,k.length)));
    	
    }*/
    
    
    /**
     * This method returns the elgamal encryption of a message given as a BigInteger.
     * @param p 				modulus of the group
     * @param g 				generator of the group
     * @param pubk 		public key of the intended receiver
     * @param message 	the message to be sent as bigInteger
     * @return 					two element array of BigInteger with ephemeral key and cipher of message.
     */
    public Cipher elgamalEncrypt(BigInteger p,BigInteger g, BigInteger pubk, BigInteger message) {
    	BigInteger i = new BigInteger(100,rnd); //randomness
    	BigInteger kE = g.modPow(i, p); // ephemeral key
    	BigInteger kM = pubk.modPow(i, p); // masking key
    	//BigInteger [] r = new BigInteger[2];
    	Cipher r = new Cipher();
    	r.setAlpha(kE); 
    	r.setBeta(message.multiply(kM).mod(p));
    	return r;
    	
    }
    
    /**
     * This method transforms the cipher to an intermediate cipher using the secret transformation key of the server.
     * @param p 			modulus of the group
     * @param seck 		secret transformation key of server
     * @param cipher 	encrypted message received from the sender
     * @return 				a two element array of BigInteger, intermediate cipher text
     */
    public Cipher serverTransform(BigInteger p, BigInteger seck, Cipher cipher) {
    	//BigInteger [] r = new BigInteger[2];
    	Cipher r = new Cipher();
    	r.setAlpha(cipher.getAlpha());
    	r.setBeta(cipher.getBeta().multiply(cipher.getAlpha().modPow(seck, p)).mod(p));
    	return r;	
    }
    
    /**
     * This method re-encrypts the result of the transformation for a particular recipient.
     * @param p 			modulus of the group
     * @param seck 		secret re-encryption key for a particular recipient
     * @param cipher 	blinded intermediate cipher
     * @return 				a two element array of BigInteger, cipher text for the user to be decrypted
     */
    public Cipher serverReencrypt(BigInteger p, BigInteger seck, Cipher cipher) {
    	//BigInteger [] r = new BigInteger[2];
    	Cipher r = new Cipher();
    	r.setAlpha(cipher.getAlpha());
    	r.setBeta(cipher.getBeta().multiply(cipher.getAlpha().modInverse(p).modPow(seck,p)).mod(p));
    	return r;	
    }
    
   /* public BigInteger clientDecrypt(BigInteger p, BigInteger seck, BigInteger[] cipher) {
    	BigInteger r = cipher[1].multiply(cipher[0].modInverse(p).modPow(seck, p)).mod(p);
    	return r;
    }*/
    
    
    
    /**
     * This method decrypts a ciphertext encrypted under Elgamal Encryption Scheme. 
     * @param p				modulus of the group
     * @param seck			secret key of the user
     * @param cipher		two element array of BigInteger containing the ephemeral key and cipher
     * @return					a big integer representing the plain text			
     */
    public BigInteger elgamalDecrypt(BigInteger p, BigInteger seck, Cipher cipher) {
    	BigInteger kM = cipher.getAlpha().modPow(seck, p);
    	BigInteger r = cipher.getBeta().multiply(kM.modInverse(p)).mod(p);
    	return r;
    }
    
    public Cipher homomorphicMultiply(Cipher c1, Cipher c2, BigInteger p) {
    	Cipher r = new Cipher();
    	r.setAlpha(c1.getAlpha().multiply(c2.getAlpha()).mod(p));
    	r.setBeta(c1.getBeta().multiply(c2.getBeta()).mod(p));
    	return r;
    }
    
    public Cipher divide(Cipher c1, Cipher c2, BigInteger p) {
    	Cipher r = new Cipher();
    	r.setAlpha(c1.getAlpha().multiply(c2.getAlpha().modInverse(p).mod(p)));
    	r.setBeta(c1.getBeta().multiply(c2.getBeta().modInverse(p).mod(p)));
    	return r;
    }
    
    public Cipher exponentByRandom(Cipher c, BigInteger p) {
    	BigInteger i = new BigInteger(100,rnd);
    	Cipher r = new Cipher();
    	r.setAlpha(c.getAlpha().modPow(i, p));
    	r.setBeta(c.getBeta().modPow(i, p));
    	return r;
    }
    
    /**
     * This method blinds the cipher text with some blinding factor using homorphic properties of the elgamal system.
     * @param p 			modulus of the group
     * @param g			generator of the group
     * @param pubk 	public key of the server 
     * @param seck 		secret transformation key of the server 
     * @param cipher 	a two element array of BigInteger, containing cipher to be blinded.
     * @param factor 	blinding factor
     * @return 				a two element array of BigInteger, blinded cipher
     */
    public Cipher blinder(BigInteger p, BigInteger g, BigInteger pubk, BigInteger seck, Cipher cipher, BigInteger factor) {
 	
    	Cipher c1  = elgamalEncrypt(p, g, pubk, factor); 
    	Cipher c2 = serverTransform(p,seck,c1);
    //	BigInteger[] r = new BigInteger[2];
    	Cipher r = new Cipher();
    	r.setAlpha(c2.getAlpha().multiply(cipher.getAlpha()).mod(p));
    	r.setBeta(c2.getBeta().multiply(cipher.getBeta()).mod(p));
    	return r;
      	
    }
    
/*    public boolean pet(BigInteger p, BigInteger seck1, BigInteger seck2, BigInteger[] c1, BigInteger[] c2) {
    	BigInteger cipher[] = new BigInteger[2];
    	cipher[0] = c1[0];
    	cipher[1] = c1[1].multiply(c2[1].modInverse(p)).mod(p);
    	BigInteger dec1 = elgamalDecrypt(p, seck1, cipher);
    	cipher[0] = c2[0].modInverse(p);
    	cipher[1] = dec1;
    	BigInteger dec2 = elgamalDecrypt(p, seck2, cipher);
    	return dec2.equals(BigInteger.ONE);
    }*/
    
    public BigInteger[] generateRandomMultiplier(int count , BigInteger p) {
    	BigInteger[] ret = new BigInteger[count];
    	ret[0] = new BigInteger(100,rnd);
    	BigInteger inv = ret[0];
    	for (int i = 1; i < count-1; i++) {
    		ret[i] = new BigInteger(100,rnd);
    		inv = inv.multiply(ret[i]).mod(p);
    	}
    	ret[count-1] = inv.modInverse(p);
    	return ret;
    }
    
    public BigInteger generateRandom(int bits) {
    	return new BigInteger(bits,rnd);
    }
    
/*    public boolean petServerSend(BigInteger p, BigInteger[] ciph) {
    	
    }
    
    public boolean petClientSend() {
    	
    }*/
  
    public Primitives() 
    { 
        //test1();
    } 
  
   /*public static void main(String args[]) 
    { 
    
        Primitives pri = new Primitives(); 
     
    } */
} 