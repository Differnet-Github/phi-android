package com.ftf.phi.account.auth;

import org.json.JSONObject;

public class FingerprintAuth implements Auth {
	@Override
	public byte[] getKey(byte[] lastKey) {
		//TODO: create finger print auth
		return new byte[0];
	}

	public JSONObject asJSON(){
		return new JSONObject();
	}
}
