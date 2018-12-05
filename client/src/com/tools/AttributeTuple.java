package com.tools;

import java.math.BigInteger;

public class AttributeTuple {

	// can be BigInteger.ONE or BigInteger.TEN
	//BigInteger.ONE for attribute present
	//BigInteger.TEN for attribute absent
	
	BigInteger[] attribute = null;
	
	public AttributeTuple() {
		this.attribute = new BigInteger[3];
	}
	
	public void setAttribute(BigInteger c, int i) {
		this.attribute[i] = c;
	}

	public void set(int i) {
		this.attribute[i] = BigInteger.ONE;
	}

	public void unset(int i){
		this.attribute[i] = BigInteger.TEN;
	}
	
	public BigInteger getAttribute(int i) {
		return this.attribute[i];
	}

}
