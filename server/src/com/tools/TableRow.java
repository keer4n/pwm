package com.tools;

import java.math.BigInteger;

public class TableRow {
	
	private int noOfCols;
	private Cipher[] indexCol= null;
	private EncryptedKeyTuple outputEncryptedKeyTuple = null;
	private BigInteger[] decrypted = null;
	
	public TableRow(EncryptedKeyTuple k) {
		this.noOfCols = k.getSize();
		indexCol = new Cipher[this.noOfCols];
		decrypted = new BigInteger[this.noOfCols];
	}
	
	public void setDecryptedIndexCol(int i, BigInteger k) {
		this.decrypted[i] = k;
	}
	
	public BigInteger getDecryptedIndexCol(int i) {
		return this.decrypted[i];
	}
	
	public int getNoOfCols() {
		return noOfCols;
	}
	
	public Cipher getIndexCol(int i) {
		return indexCol[i];
	}
	
	
	public EncryptedKeyTuple getOutput() {
		return outputEncryptedKeyTuple;
	}
	
	public void setIndexCol(Cipher c, int colNumber) {
		this.indexCol[colNumber] =c;
	}
	
	public void setOutput(EncryptedKeyTuple k) {
		this.outputEncryptedKeyTuple = k;
	}
	
	
}
