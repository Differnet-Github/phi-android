package com.ftf.phi.account.keys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Archive {

	MasterKey master;
	ArrayList<Archived> archive = new ArrayList();

	public Archive(MasterKey master){
		this.master = master;
	}

	public Archive(MasterKey master, JSONArray archive){
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

	public void add(MasterKey master, DisposableKey disposable){
		try{
			this.archive.add(new Archived(master, disposable));
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

	private byte[] publicFingerprint;
	private byte[] privateKey;
	private byte[] signature;
	private byte[] key;

	private long timestamp;

	private String algorithm;

	public Archived(MasterKey master, DisposableKey disposable) throws Exception {
		this.algorithm = "SHA-256";

		MessageDigest md = MessageDigest.getInstance(this.algorithm);
		this.publicFingerprint =  md.digest(disposable.exportPublic());

		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256); // for example
		SecretKey key = keyGen.generateKey();

		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		this.privateKey = cipher.doFinal(disposable.exportPrivate());

		this.key = master.encrypt(key.getEncoded());

		this.timestamp = new Date().getTime();

		this.signature = master.sign(this.getSignable());
	}

	public Archived(JSONObject archive) throws JSONException {
		this.publicFingerprint = archive.getString("public").getBytes();
		this.privateKey = archive.getString("private").getBytes();
		this.key = archive.getString("key").getBytes();
		this.algorithm = archive.getString("alg");
		this.timestamp = archive.getLong("timestamp");
		this.signature = archive.getString("signature").getBytes();
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
				(byte) ((this.timestamp >> 0) & 0xff),
		};

		//TODO: do we really need to have this sign private and key or just public
		byte[] signable = new byte[this.publicFingerprint.length + this.privateKey.length + this.key.length + timeBytes.length];
		System.arraycopy(this.publicFingerprint, 0, signable, 0, this.publicFingerprint.length);
		System.arraycopy(this.privateKey.length, 0, signable, this.publicFingerprint.length, this.privateKey.length);
		System.arraycopy(this.key.length, 0, signable, this.publicFingerprint.length + this.privateKey.length, this.key.length);
		System.arraycopy(timeBytes, 0, signable, this.publicFingerprint.length + this.privateKey.length + this.key.length, timeBytes.length);

		return signable;
	}

	public boolean verify(MasterKey master){
		try {
			return master.verify(this.getSignable(), this.signature);
		} catch (Exception e) {
			return false;
		}
	}

	public JSONObject asJSON() throws JSONException {
		JSONObject data = new JSONObject();
		data.put("public", this.publicFingerprint.toString());
		data.put("private", this.privateKey.toString());
		data.put("key", this.key.toString());
		data.put("alg", this.algorithm);
		data.put("timestamp", this.timestamp);
		data.put("signature", this.signature.toString());
		return data;
	}
}