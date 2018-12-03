package com.ca;

import java.io.Serializable;
import java.math.BigInteger;

public class CAkey implements Serializable{
	static final long serialVersionUID = 1L;
	private BigInteger K,p,g,secPet,pubPet = null;
	public BigInteger getK() {
		return K;
	}
	public void setK(BigInteger k) {
		K = k;
	}
	public BigInteger getP() {
		return p;
	}
	public void setP(BigInteger p) {
		this.p = p;
	}
	public BigInteger getG() {
		return g;
	}
	public void setG(BigInteger g) {
		this.g = g;
	}
	public BigInteger getSecPet() {
		return secPet;
	}
	public void setSecPet(BigInteger secPet) {
		this.secPet = secPet;
	}
	public BigInteger getPubPet() {
		return pubPet;
	}
	public void setPubPet(BigInteger pubPet) {
		this.pubPet = pubPet;
	}
	
}
