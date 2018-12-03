package com.tools;

import java.io.Serializable;
import java.math.BigInteger;

public class Cipher implements Serializable{
	
	static final long serialVersionUID = 42L;
	private BigInteger alpha;
	private BigInteger beta;
	private Primitives prim;
	
	public Cipher() {
	//this.prim = new Primitives();
	}

	public BigInteger getAlpha() {
		return alpha;
	}

	public void setAlpha(BigInteger alpha) {
		this.alpha = alpha;	
	}

	public BigInteger getBeta() {
		return beta;
	}

	public void setBeta(BigInteger beta) {
		this.beta = beta;
	}
	
	/*public void encrypt(BigInteger message, PublicParameter p) {
		BigInteger[] out = this.prim.elgamalEncrypt(p.getP(), p.getG(), p.getCommonPublicKey(), message);
		this.alpha = out[0];
		this.beta = out[1];
	}
	
	public BigInteger decrypt(BigInteger privateKey, PublicParameter p ) {
		BigInteger[] c = new BigInteger[2];
		c[0] = this.alpha;
		c[1] = this.beta;
		return this.prim.elgamalDecrypt(p.getP(), privateKey, c);
	}*/

}
