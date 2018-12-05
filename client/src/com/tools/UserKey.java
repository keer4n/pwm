package com.tools;

import java.io.Serializable;
import java.math.BigInteger;

public class UserKey implements Serializable {
    static final long serialVersionUID = 11L;
	private BigInteger E,x1,x2,x3;
	private String uid;
	
	public void setUid(String name) {
		this.uid = name;
	}
	
	public String getUid() {
		return this.uid;
	}

	public BigInteger getE() {
		return E;
	}

	public void setE(BigInteger e) {
		E = e;
	}

	public BigInteger getX1() {
		return x1;
	}

	public void setX1(BigInteger x1) {
		this.x1 = x1;
	}

	public BigInteger getX2() {
		return x2;
	}

	public void setX2(BigInteger x2) {
		this.x2 = x2;
	}

	public BigInteger getX3() {
		return x3;
	}

	public void setX3(BigInteger x3) {
		this.x3 = x3;
	}

	public BigInteger[] getX(){
		return new BigInteger[] {this.x1, this.x2, this.x3};
	}
}
