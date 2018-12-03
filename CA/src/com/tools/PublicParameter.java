package com.tools;

import java.io.Serializable;
import java.math.BigInteger;

public class PublicParameter implements Serializable{
	static final long serialVersionUID = 3L;
	private BigInteger commonPublicKey = null;
	private BigInteger p = null;
	private BigInteger g = null;
	private BigInteger y1 = null;
	private BigInteger y2 = null;
	private BigInteger y3 = null;
	
	public BigInteger getCommonPublicKey() {
		return commonPublicKey;
	}
	public void setCommonPublicKey(BigInteger commonPublicKey) {
		this.commonPublicKey = commonPublicKey;
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
	
}
