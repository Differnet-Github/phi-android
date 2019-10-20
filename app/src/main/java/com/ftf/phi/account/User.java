package com.ftf.phi.account;

public class User {
	private String fingerprint;

	public User(String fingerprint){
		this.fingerprint = fingerprint;
	}

	public String getID(){
		return this.fingerprint;
	}
}
