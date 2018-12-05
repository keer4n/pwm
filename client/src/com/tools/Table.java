package com.tools;

import java.math.BigInteger;

//import com.server.Serverkey;

public class Table {
	
	private TableRow[] row = null;
	private int noOfRows;
	private EncryptedKeyTuple key;
	private Primitives prim;
	private PublicParameter pk;
	//private Serverkey sk;
	
	public Table(EncryptedKeyTuple k, PublicParameter pk ) {

		noOfRows = (int) Math.pow(2, k.getSize());
		row = new TableRow[noOfRows];
		for(int i = 0; i < noOfRows; ++i){
			row[i] = new TableRow(k);
		}
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
			half = half/2;
			for(int j = 0; j < noOfRows; j++) {
				if((j/half)%2==0){
					//System.out.println("row["+j+"], col["+i+"] = "+0);
					row[j].setIndexCol(prim.elgamalEncrypt(pk.getP(), pk.getG(), pk.getCommonPublicKey(), BigInteger.TEN), i);
				}
				else {
					//System.out.println("row["+j+"], col["+i+"] = "+1);
					row[j].setIndexCol(prim.elgamalEncrypt(pk.getP(),pk.getG(),pk.getCommonPublicKey(),BigInteger.ONE),i);
				}
				
			}
		}
		
		for(int i = 0; i < noOfRows; i++) {
				row[i].setOutput(blindOutput(i,sk));
				//row[i].setOutput(key);
		}
	}

	public void mixRows(){
		TableRow[] temp = row.clone();
		for(int i = 0; i < noOfRows; ++i){
			row[(i+2)%7] = temp[i];
		}
	}

	/**
	 * to be run by main server
	 * @param a
	 */
	public void divideByClientTuple(EncryptedKeyTuple a) {
		for(int i = 0; i < noOfRows; i++) {
			for(int j = 0; j < 3; j++) {
				System.out.println(this.row[i].getIndexCol(j).getAlpha());
				System.out.println(a.getElement(j).getAlpha());
				this.row[i].setIndexCol(prim.divide(this.row[i].getIndexCol(j), a.getElement(j), pk.getP()), j);
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

	public TableRow getRow(int i){
		return row[i];
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
				System.out.println("Returning row index "+i);
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
		/*int bitCount = Integer.bitCount(index);
		int totalBitCount = bitCount;
		EncryptedKeyTuple ret = new EncryptedKeyTuple();
		String bitRep = Integer.toBinaryString(index);
		BigInteger totalRand = BigInteger.ONE;
		if(bitRep.length()!=3) {
			for(int i = 0; i<= 3 - bitRep.length(); i++) {
				bitRep = '0' +bitRep;
			}
		}
		System.out.println("bitrep "+bitRep);
		for(int i = 0; i < key.getSize(); ++i) {
			if(bitRep.charAt(i) == '0') {
				Cipher c = prim.elgamalEncrypt(pk.getP(), pk.getG(), pk.getY()[i], BigInteger.ONE);
				ret.setElement(prim.serverTransform(pk.getP(),sk.getT()[i],c), i);
				System.out.println("0: "+ret.getElement(i).getAlpha());
			}
			else {
				--bitCount;
				if(bitCount > 0) {
					System.out.println("if");
					BigInteger temp = prim.generateRandom(100);
					ret.setElement(prim.blinder(pk.getP(), pk.getG(), pk.getY()[i], sk.getT()[i], key.getElement(i), temp), i);
					System.out.println("1+: "+ret.getElement(i).getAlpha());
					totalRand.multiply(temp).mod(pk.getP());
				}
				else {
					System.out.println("else");
					BigInteger temp = BigInteger.ONE;
					if(totalBitCount == 1){
						System.out.println("Generating only 1 random");
						Cipher c = prim.elgamalEncrypt(pk.getP(), pk.getG(), pk.getY()[i], BigInteger.ONE);
						ret.setElement(prim.serverTransform(pk.getP(),sk.getT()[i],c), i);
						System.out.println("1-: "+ret.getElement(i).getAlpha());
						//temp = prim.generateRandom(100);
					}
					else {
						temp = totalRand.modInverse(pk.getP());
						ret.setElement(prim.blinder(pk.getP(), pk.getG(), pk.getY()[i], sk.getT()[i], key.getElement(i), temp), i);
						System.out.println("1: "+ret.getElement(i).getAlpha());
					}
				}
			}
		}*/
/*		int i =0;
		for(char s : bitRep.toCharArray()) {
			if(s == '0') {
				ret.setElement(prim.elgamalEncrypt(pk.getP(), pk.getG(), pk.getY()[i], BigInteger.ONE), i);
				i++;
			} else {

			}
		}*/
		int bitCount = key.getSize();
		String bitRep = Integer.toBinaryString(index);
		if(bitRep.length()!=bitCount) {
			for(int i = 0; i<= bitCount - bitRep.length(); i++) {
				bitRep = '0' +bitRep;
			}
		}
		BigInteger[] random = null;
		if(Integer.bitCount(index)>1)
			random = prim.generateRandomMultiplier(Integer.bitCount(index), pk.getP(), BigInteger.ONE);

		EncryptedKeyTuple ret = new EncryptedKeyTuple();
		int randomIndex = 0;
		if(Integer.bitCount(index) == 1) {
			ret.setElement(key.getElement(0), 0);
			ret.setElement(key.getElement(1), 1);
			ret.setElement(key.getElement(2), 2);
		}
		else {
			// Equivelent to code below (commented)
			for (int i = 0; i < bitRep.length(); i++) {
				if (bitRep.charAt(i) == '0') {
					Cipher en = prim.elgamalEncrypt(pk.getP(), pk.getG(), pk.getY()[i], BigInteger.ONE);
					Cipher enc = prim.serverTransform(pk.getP(),sk.getT()[i],en);
					ret.setElement(enc, i);
				} else {
					ret.setElement(prim.blinder(pk.getP(), pk.getG(), pk.getY()[i], sk.getT()[i], key.getElement(i), random[randomIndex]), i);
					randomIndex++;
				}
			}
		}
//		
//			if(bitRep.charAt(0) == '0') {
//				Cipher enc = prim.elgamalEncrypt(pk.getP(), pk.getG(), pk.getY1(), BigInteger.ONE);
//				ret.setElement(prim.serverTransform(pk.getP(), sk.getT1(), enc), 0);
//				
//			}else {
//				ret.setElement(prim.blinder(pk.getP(), pk.getG(), pk.getY1(), sk.getT1(), key.getElement(0), random[0]), 0);
//			}
//			
//			if(bitRep.charAt(1) == '0') {
//				Cipher enc = prim.elgamalEncrypt(pk.getP(), pk.getG(), pk.getY2(), BigInteger.ONE);
//				ret.setElement(prim.serverTransform(pk.getP(), sk.getT2(), enc), 1);
//				
//			}else {
//				ret.setElement(prim.blinder(pk.getP(), pk.getG(), pk.getY2(), sk.getT2(), key.getElement(1), random[1]), 1);
//			}
//			if(bitRep.charAt(2) == '0') {
//				Cipher enc = prim.elgamalEncrypt(pk.getP(), pk.getG(), pk.getY3(), BigInteger.ONE);
//				ret.setElement(prim.serverTransform(pk.getP(), sk.getT3(), enc), 2);
//				
//			}else {
//				ret.setElement(prim.blinder(pk.getP(), pk.getG(), pk.getY3(), sk.getT3(), key.getElement(2), random[2]), 2);
//			}
	
		return ret;
		
	}
	
	
	/*public void setTableRow(TableRow t, int i) {
		this.row[i] = t;
	}
	
	public TableRow getTableRow(int i) {
		return this.row[i];
	}*/

}
