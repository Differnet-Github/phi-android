package com.ftf.phi.account.keys.auth;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

// Factors are just ways of logging in that can be chained together
public interface Factor {
	void getKey(Consumer<byte[]> callback) throws NoSuchAlgorithmException;
	JSONObject asJSON() throws JSONException;
}
