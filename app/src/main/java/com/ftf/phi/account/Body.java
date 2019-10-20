package com.ftf.phi.account;

import com.ftf.phi.account.keys.DisposableKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

//TODO: organize
/*
 * The Body class contains all body information for a account
 */
public class Body {
	//Disposable key for signing and verifying things
	DisposableKey disposable;

	//Key for encrypting/decrypting the contents of body
	byte[] key;
	//byte array the data is stored in while encryped
	byte[] data;

	//Generic body data
	String username;
	String bio;
	//Key for decrypting the profile picture
	byte[] photo;

	//Arrays of people
	ArrayList<User> friends = new ArrayList();
	ArrayList<User> following = new ArrayList();
	ArrayList<User> blocked = new ArrayList();

	//timestamp and signature for account
	long timestamp;
	byte[] signature;

	boolean encrypted;

	public Body(DisposableKey disposable) throws NoSuchAlgorithmException {
		this.disposable = disposable;

		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256); // for example

		this.key = keyGen.generateKey().getEncoded();

		this.username = "Username not set";
		this.bio = "No Bio Set";

		this.photo = keyGen.generateKey().getEncoded();

		this.encrypted = false;

		try {
			this.sign();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Body(DisposableKey disposable, JSONObject body) throws Exception {
		this.disposable = disposable;

		this.key = body.getString("key").getBytes();
		this.signature = body.getString("signature").getBytes();

		this.encrypted = body.getBoolean("encrypted");
		if(this.encrypted){
			this.data = body.getString("data").getBytes();
		}
		else{
			JSONObject data = body.getJSONObject("data");
			this.loadBody(data);
		}

		this.timestamp = body.getLong("timestamp");

		//TODO: Exception naming
		if(!this.verify()){
			throw new Exception();
		}
	}

	//TODO: Exception naming
	public void sign() throws Exception {
		this.signature = this.disposable.sign(this.getEncryped());
	}

	//TODO: Exception naming
	public boolean verify() throws Exception {
		return this.disposable.verify(this.getEncryped(), this.signature);
	}

	public void lock(){
		try {
			this.data = this.getEncryped();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		this.username = null;
		this.bio = null;
		this.photo = null;

		this.friends = new ArrayList();
		this.following = new ArrayList();
		this.blocked = new ArrayList();

		this.timestamp = Long.parseLong(null);

	}

	public void loadBody(JSONObject data) throws JSONException {
		this.username = data.getString("username");
		this.bio = data.getString("bio");
		//TODO: user photo key to actually get the photo
		this.photo = data.getString("photo").getBytes();

		JSONArray friends = data.getJSONArray("friends");
		for(int i = friends.length() - 1; i > -1; i--){
			this.friends.add(new User(friends.getString(i)));
		}
		JSONArray following = data.getJSONArray("following");
		for(int i = following.length() - 1; i > -1; i--){
			this.following.add(new User(following.getString(i)));
		}
		JSONArray blocked = data.getJSONArray("blocked");
		for(int i = blocked.length() - 1; i > -1; i--){
			this.blocked.add(new User(blocked.getString(i)));
		}
	}

	public void unlock() throws JSONException {
		JSONObject data = null;
		try {
			data = new JSONObject(disposable.decrypt(this.data).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.data = null;
		loadBody(data);
	}

	public byte[] getEncryped() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, JSONException {
		if(this.encrypted){
			return this.data;
		}
		else{
			return this.disposable.encrypt(this.bodyAsJSON().toString().getBytes());
		}
	}

	public JSONObject bodyAsJSON() throws JSONException {
		JSONObject body = new JSONObject();
		body.put("username", this.username);
		body.put("bio", this.bio);
		body.put("photo", this.photo.toString());

		JSONArray friends = new JSONArray();
		for(int i = this.friends.size() -1; i > -1; i--){
			friends.put(i, this.friends.get(i).getID());
		}
		body.put("friends", friends);

		JSONArray following = new JSONArray();
		for(int i = this.following.size() -1; i > -1; i--){
			following.put(i, this.following.get(i).getID());
		}
		body.put("following", following);

		JSONArray blocked = new JSONArray();
		for(int i = this.blocked.size() -1; i > -1; i--){
			blocked.put(i, this.blocked.get(i).getID());
		}
		body.put("blocked", blocked);

		body.put("timestamp", this.timestamp);
		return body;
	}

	public JSONObject asJSON() throws JSONException {
		JSONObject body = new JSONObject();
		body.put("key", this.key.toString());
		body.put("signature", this.signature.toString());
		body.put("body", this.bodyAsJSON());
		return body;
	}
}
