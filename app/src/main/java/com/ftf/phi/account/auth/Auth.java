package com.ftf.phi.account.auth;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

public interface Auth {
	byte[] getKey(byte [] lastKey) throws NoSuchAlgorithmException;
	JSONObject asJSON() throws JSONException;
}