package com.ftf.phi.account.keys.auth;

import com.ftf.phi.ByteCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

//TODO: create more factors
public interface Factor {
	void getKey(ByteCallback callback) throws NoSuchAlgorithmException;
	JSONObject asJSON() throws JSONException;
}
