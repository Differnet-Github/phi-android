package com.ftf.phi.account.auth;

import org.json.JSONObject;

public class FingerprintAuth implements Auth {
	@Override
	public void getKey(AuthCallback callback) {
		callback.call(null);
		//TODO: create finger print auth
	}

	public JSONObject asJSON(){
		return new JSONObject();
	}
}
