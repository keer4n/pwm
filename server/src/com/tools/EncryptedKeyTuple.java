package com.tools;

public class EncryptedKeyTuple {
	
	private final int SIZE = 3;
	
	public Cipher[] tuple = null;
	
	public EncryptedKeyTuple() {
		tuple = new Cipher[SIZE];
	}
	
	public void setElement(Cipher c, int index) {
		this.tuple[index] = c;
	}
	
	public Cipher getElement(int index) {
		return tuple[index];
	}
	
	public int getSize() {
		return SIZE;
	}
	
	
}
