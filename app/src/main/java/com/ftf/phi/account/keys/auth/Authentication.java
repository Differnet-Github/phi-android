package com.ftf.phi.account.keys.auth;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.function.Consumer;

/* Authentication is the master class for all account authentication
 * It can contain several ways of login into an account
 */
public class Authentication {
	// Bytes for the master key
	private static final int KEY_BYTES = 256;

	// Methods are used to encrypt the key
	ArrayList<Method> methods = new ArrayList<>();

	// Default constructor doesn't do anything but still works
	public Authentication(){}

	// Import Auth from account
	public Authentication(JSONArray fileData) throws JSONException {
		// Add all the methods to this class
		for(int i = fileData.length() - 1; i > -1; i--){
			this.methods.add(new Method(fileData.getJSONObject(i)));
		}
	}

	// Get the key from a target method
	public void getKey(Consumer<byte[]> callback) throws NoSuchAlgorithmException {
		this.getKey(0, callback);
	}

	public void getKey(int method, Consumer<byte[]> callback) throws NoSuchAlgorithmException {
		//TODO: let user pick what method they are going to use
		this.methods.get(method).getKey(callback);
	}

	// add a method to the authentication
	public void add(Method method) throws InvalidKeyException {
		if(this.methods.size() != 0){
			throw new InvalidKeyException("First key already defined");
		}
		byte[] key = new byte[KEY_BYTES];
		SecureRandom random = new SecureRandom();
		random.nextBytes(key);

		method.setKey(key);
		this.methods.add(method);
	}
	public void add(Method method, int hostMethod) throws NoSuchAlgorithmException{
		if(this.methods.size() == 0){
			byte[] key = new byte[KEY_BYTES];
			SecureRandom random = new SecureRandom();
			random.nextBytes(key);

			method.setKey(key);
			this.methods.add(method);
		}
		else {
			this.getKey(hostMethod, (byte[] key) -> {
				method.setKey(key);
				this.methods.add(method);
			});
		}
	}

	// Get the authentication as a string
	public JSONArray asJSON() throws JSONException {
		JSONArray json = new JSONArray();

		for(int i = methods.size() - 1; i > -1; i--){
			json.put(i, methods.get(i).asJSON());
		}

		return json;
	}
}
