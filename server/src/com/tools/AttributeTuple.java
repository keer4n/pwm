package com.tools;

public class AttributeTuple {
	
	Cipher[] attribute = null;
	
	public AttributeTuple() {
		this.attribute = new Cipher[3];
	}
	
	public void setAttribute(Cipher c, int i) {
		this.attribute[i] = c;
	}
	
	public Cipher getAttribute(int i) {
		return this.attribute[i];
	}

}
