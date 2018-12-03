package com.tools;

import java.math.BigInteger;

import com.tools.Serverkey;

public class Table {
	
	private TableRow[] row = null;
	private int noOfRows;
	private EncryptedKeyTuple key;
	private Primitives prim;
	private PublicParameter pk;
	//private Serverkey sk;
	
	public Table(EncryptedKeyTuple k, PublicParameter pk ) {

		noOfRows = (int) Math.pow(2, k.getSize());
		this.key = k;
		this.prim = new Primitives();
		this.pk = pk;
		//this.sk = sk;
	}

	
	/**
	 * to be run by the server
	 * @param sk
	 */
	public void generateTable(Serverkey sk) {
		int half = noOfRows;
		for(int i = 0; i < key.getSize(); i++) {
			for(int j = 0; j < noOfRows; j++) {
				half = half/2;
				for(int k = 0; k < half; k++)
					row[j].setIndexCol(prim.elgamalEncrypt(pk.getP(),pk.getG(),pk.getCommonPublicKey(),BigInteger.ONE),i);
				for(int k = 0; k < half; k++)
					row[j].setIndexCol(prim.elgamalEncrypt(pk.getP(),pk.getG(),pk.getCommonPublicKey(),BigInteger.TEN),i);
				
			}
		}
		
		for(int i = 0; i < noOfRows; i++) {
				row[i].setOutput(blindOutput(i,sk));
		}
	}
	
	/**
	 * to be run by main server
	 * @param a
	 */
	public void divideByClientTuple(AttributeTuple a) {
		for(int i = 0; i < noOfRows; i++) {
			for(int j = 0; j < 3; j++) {
				this.row[i].setIndexCol(prim.divide(this.row[i].getIndexCol(j), a.getAttribute(j), pk.getP()), j);
			}
		}
	}
	
	/**
	 * to be run by both servers
	 */
	public void exponentByRandom() {
		for(int i = 0; i < noOfRows; i++) {
			for (int j = 0; j < 3; j++) {
				this.row[i].setIndexCol(prim.exponentByRandom(this.row[i].getIndexCol(j), pk.getP()), j);
			}
		}
	}
	
	/**
	 * to be run by the CA or pet pair server
	 * @param seck
	 */
	public void partialDecryptTable(BigInteger seck) {
		for(int i = 0; i < noOfRows; i++) {
			for (int j=0; j<3; j++) {
				this.row[i].setIndexCol(prim.serverReencrypt(pk.getP(), seck, this.row[i].getIndexCol(j)), j);
			}
		}
	}
	
	/**
	 * to be run by main server 
	 * @param seck
	 */
	public void fullDecryption(BigInteger seck) {
		for(int i =0; i < noOfRows; i++) {
			for (int j = 0; j < 3; j++) {
				this.row[i].setDecryptedIndexCol(j, prim.elgamalDecrypt(pk.getP(), seck, this.row[i].getIndexCol(j)));
			}
		}
	}
	
	/**
	 * returns the selected EncryptedKeyTuple, to be run by main server
	 * @return
	 */
	public EncryptedKeyTuple getTupleForClient() {
		for(int i = 0; i < noOfRows; i++) {
			if(this.row[i].getDecryptedIndexCol(0).compareTo(BigInteger.ONE) == 0 &&
					this.row[i].getDecryptedIndexCol(1).compareTo(BigInteger.ONE) == 0 &&
					this.row[i].getDecryptedIndexCol(2).compareTo(BigInteger.ONE) == 0) {
				return this.row[i].getOutput();
			}
		}
		return null;
	}
	
	/**
	 * blind the output encryptedKeytuple
	 * @param index
	 * @param sk
	 * @return
	 */
	public EncryptedKeyTuple blindOutput(int index, Serverkey sk) {
		int bitCount = key.getSize();
		String bitRep = Integer.toBinaryString(index);
		if(bitRep.length()!=bitCount) {
			for(int i = 0; i<= bitCount - bitRep.length(); i++) {
				bitRep = '0' +bitRep;
			}
		}
		BigInteger[] random = prim.generateRandomMultiplier(Integer.bitCount(index), pk.getP());
		
		EncryptedKeyTuple ret = new EncryptedKeyTuple();
		
			if(bitRep.charAt(0) == '0') {
				Cipher enc = prim.elgamalEncrypt(pk.getP(), pk.getG(), pk.getY1(), BigInteger.ONE);
				ret.setElement(prim.serverTransform(pk.getP(), sk.getT1(), enc), 0);
				
			}else {
				ret.setElement(prim.blinder(pk.getP(), pk.getG(), pk.getY1(), sk.getT1(), key.getElement(0), random[0]), 0);
			}
			
			if(bitRep.charAt(1) == '0') {
				Cipher enc = prim.elgamalEncrypt(pk.getP(), pk.getG(), pk.getY2(), BigInteger.ONE);
				ret.setElement(prim.serverTransform(pk.getP(), sk.getT2(), enc), 1);
				
			}else {
				ret.setElement(prim.blinder(pk.getP(), pk.getG(), pk.getY2(), sk.getT2(), key.getElement(1), random[1]), 1);
			}
			if(bitRep.charAt(2) == '0') {
				Cipher enc = prim.elgamalEncrypt(pk.getP(), pk.getG(), pk.getY3(), BigInteger.ONE);
				ret.setElement(prim.serverTransform(pk.getP(), sk.getT3(), enc), 2);
				
			}else {
				ret.setElement(prim.blinder(pk.getP(), pk.getG(), pk.getY3(), sk.getT3(), key.getElement(2), random[2]), 2);
			}
	
		return ret;
		
	}
	
	
	/*public void setTableRow(TableRow t, int i) {
		this.row[i] = t;
	}
	
	public TableRow getTableRow(int i) {
		return this.row[i];
	}*/

}
