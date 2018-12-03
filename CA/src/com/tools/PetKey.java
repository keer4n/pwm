package com.tools;

import java.io.Serializable;
import java.math.BigInteger;

public class PetKey implements Serializable {
	static final long serialVersionUID = 2L;
	private BigInteger caPublicKey = null;
	private BigInteger serverPublicKey = null;
	private BigInteger commonPublicKey = null;
	
/*	public void generateCommonPublicKey(BigInteger p) {
		commonPublicKey = caPublicKey.multiply(serverPublicKey).mod(p);
	}*/
	
	public BigInteger getCaPublicKey() {
		return caPublicKey;
	}
	public void setCaPublicKey(BigInteger caPublicKey) {
		this.caPublicKey = caPublicKey;
	}
	public BigInteger getServerPublicKey() {
		return serverPublicKey;
	}
	public void setServerPublicKey(BigInteger serverPublicKey) {
		this.serverPublicKey = serverPublicKey;
	}
	public BigInteger getCommonPublicKey() {
		return commonPublicKey;
	}
	public void setCommonPublicKey(BigInteger commonPublicKey) {
		this.commonPublicKey = commonPublicKey;
	}
}
