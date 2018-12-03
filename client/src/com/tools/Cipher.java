package com.tools;

import java.io.Serializable;
import java.math.BigInteger;

public class Cipher implements Serializable{
	static final long serialVersionUID = 42L;
	private BigInteger alpha;
	private BigInteger beta;
	
	public Cipher(BigInteger alpha, BigInteger beta) {
		this.alpha = alpha;
		this.beta = beta;
	}
	

	public BigInteger getAlpha() {
		return alpha;
	}

	public void setAlpha(BigInteger alpha) {
		this.alpha = alpha;
	}

	public void setBeta(BigInteger beta) {
		this.beta = beta;
	}

}
