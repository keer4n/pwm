package com.tools;

import java.io.Serializable;
import java.math.BigInteger;

public class Serverkey implements Serializable{
	static final long serialVersionUID = 1L;
	private BigInteger p,g,secPet,pubPet = null;
	private BigInteger y1,y2,y3;
	private BigInteger t1,t2,t3;
	public BigInteger getY1() {
		return y1;
	}
	public void setY1(BigInteger y1) {
		this.y1 = y1;
	}
	public BigInteger getY2() {
		return y2;
	}
	public void setY2(BigInteger y2) {
		this.y2 = y2;
	}
	public BigInteger getY3() {
		return y3;
	}
	public void setY3(BigInteger y3) {
		this.y3 = y3;
	}
	public BigInteger getT1() {
		return t1;
	}
	public void setT1(BigInteger t1) {
		this.t1 = t1;
	}
	public BigInteger getT2() {
		return t2;
	}
	public void setT2(BigInteger t2) {
		this.t2 = t2;
	}
	public BigInteger getT3() {
		return t3;
	}
	public void setT3(BigInteger t3) {
		this.t3 = t3;
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
