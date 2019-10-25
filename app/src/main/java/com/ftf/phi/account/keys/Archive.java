package com.ftf.phi.account.keys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

//TODO: store with user and not in account
public class Archive {

	private Key master;
	private ArrayList<Archived> archive = new ArrayList<>();

	public Archive(Key master){
		this.master = master;
	}

	public Archive(Key master, JSONArray archive){
		this.master = master;

		for(int i = archive.length() - 1; i > -1; i--){
			try {
				Archived archived = new Archived(archive.getJSONObject(i));
				if(archived.verify(master)){
					this.archive.add(archived);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void add(DisposableKey disposable){
		try{
			this.archive.add(new Archived(this.master, disposable));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONArray asJSON() throws JSONException {
		JSONArray data = new JSONArray();
		for(int i = archive.size() - 1; i > -1; i--){
			data.put(i, archive.get(i).asJSON());
		}
		return data;
	}
}

class Archived{
	private static final String HASH = "SHA-256";
	private static final String CIPHER = "SHA-256";
	private static final int KEY_SIZE = 256;

	private byte[] fingerprint;

	private String cipher;
	private String lock;
	private byte[] key;

	private byte[] privateKey;

	private long timestamp;

	private byte[] signature;

	Archived(Key master, DisposableKey disposable) throws Exception {
		this(master, disposable, CIPHER, KEY_SIZE);
	}

	private Archived(Key master, DisposableKey disposable, String cipher, int keySize) throws Exception {
		MessageDigest md = MessageDigest.getInstance(HASH);
		this.fingerprint =  md.digest(disposable.publicKey.getEncoded());

		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(keySize);
		SecretKey key = keyGen.generateKey();

		this.lock = master.getCipher();
		this.key = master.encrypt(key.getEncoded());

		this.cipher = cipher;
		Cipher lockCipher = Cipher.getInstance(this.cipher);
		lockCipher.init(Cipher.ENCRYPT_MODE, key);
		this.privateKey = lockCipher.doFinal(disposable.privateKey.getEncoded());

		this.timestamp = new Date().getTime();

		this.signature = master.sign(this.getSignable());
	}

	Archived(JSONObject fileData) throws JSONException {
		this.fingerprint = fileData.getString("fingerprint").getBytes();
		this.lock = fileData.getString("lock");
		this.key = fileData.getString("key").getBytes();
		this.cipher = fileData.getString("cipher");
		this.privateKey = fileData.getString("private").getBytes();
		this.timestamp = fileData.getLong("timestamp");
		this.signature = fileData.getString("signature").getBytes();
	}

	private byte[] getSignable(){
		byte[] timeBytes = new byte[]{
				(byte) ((this.timestamp >> 56) & 0xff),
				(byte) ((this.timestamp >> 48) & 0xff),
				(byte) ((this.timestamp >> 40) & 0xff),
				(byte) ((this.timestamp >> 32) & 0xff),
				(byte) ((this.timestamp >> 24) & 0xff),
				(byte) ((this.timestamp >> 16) & 0xff),
				(byte) ((this.timestamp >> 8) & 0xff),
				(byte) ((this.timestamp) & 0xff),
		};

		byte[] signable = new byte[fingerprint.length + timeBytes.length];
		System.arraycopy(fingerprint, 0, signable, 0, fingerprint.length);
		System.arraycopy(timeBytes, 0, signable, fingerprint.length, timeBytes.length);

		return signable;
	}

	boolean verify(Key master){
		try {
			return master.verify(this.getSignable(), this.signature);
		} catch (Exception e) {
			return false;
		}
	}

	JSONObject asJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("fingerprint", Arrays.toString(this.fingerprint));
		json.put("lock", this.lock);
		json.put("key", Arrays.toString(this.key));
		json.put("cipher", this.cipher);
		json.put("private", Arrays.toString(this.privateKey));
		json.put("timestamp", this.timestamp);
		json.put("signature", Arrays.toString(this.signature));

		return json;
	}
}