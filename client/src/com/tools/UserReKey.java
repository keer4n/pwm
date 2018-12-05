package com.tools;

import java.io.Serializable;
import java.math.BigInteger;

public class UserReKey implements Serializable {
	static final long serialVersionUID = 13L;
	private BigInteger s1,s2, s3;
	private String uid;
	
	public void setUid(String name) {
		this.uid = name;
	}
	
	public String getUid() {
		return this.uid;
	}

	public BigInteger getS1() {
		return s1;
	}

	public void setS1(BigInteger s1) {
		this.s1 = s1;
	}

	public BigInteger getS2() {
		return s2;
	}

	public void setS2(BigInteger s2) {
		this.s2 = s2;
	}

	public BigInteger getS3() {
		return s3;
	}

	public void setS3(BigInteger s3) {
		this.s3 = s3;
	}

}
