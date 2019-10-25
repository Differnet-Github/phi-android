package com.ftf.phi.account.keys.auth;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Authentication {
	private static final int KEY_BYTES = 256;

	ArrayList<Method> methods = new ArrayList<>();

	public Authentication(){}

	public Authentication(JSONArray fileData) throws JSONException {
		for(int i = fileData.length() - 1; i > -1; i--){
			methods.add(new Method(fileData.getJSONObject(i)));
		}
	}

	public byte[] getKey() throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		//TODO: let user pick what method they are going to use
		return methods.get(0).getKey();
	}

	public void add(Method method) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		if(this.methods.size() == 0){
			byte[] key = new byte[KEY_BYTES];
			SecureRandom random = new SecureRandom();
			random.nextBytes(key);

			method.setKey(key);
			this.methods.add(method);
		}
		else {
			method.setKey(this.getKey());
			this.methods.add(method);
		}
	}

	public JSONArray asJSON() throws JSONException {
		JSONArray json = new JSONArray();

		for(int i = methods.size() - 1; i > -1; i--){
			json.put(i, methods.get(i).asJSON());
		}

		return json;
	}
}
