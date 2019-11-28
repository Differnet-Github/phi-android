package com.ftf.phi.account.keys;

import com.ftf.phi.account.Files.Savable;

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

/* Archive is a list of old disposable keys that the account had */
public class Archive implements Savable {
	private ArrayList<Archived> archive = new ArrayList<>();

	// New archive constructor
	public Archive(){}

	// Import archive
	public Archive(Key master, JSONArray archive){
		// verify the contents of th imported archive
		for(int i = archive.length() - 1; i > -1; i--){
			try {
				Archived archived = new Archived(archive.getJSONObject(i));
				if(archived.verify(master)){
					this.archive.add(archived);
				}
				else{
					//TODO: give users some kind of warning
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// Add a disposable key to the archive
	public void add(Key master, DisposableKey disposable){
		try{
			this.archive.add(new Archived(master, disposable));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Export the archive as a savable string
	public String export() throws JSONException {
		JSONArray data = new JSONArray();
		for(int i = archive.size() - 1; i > -1; i--){
			data.put(i, archive.get(i).asJSON());
		}
		return data.toString();
	}
}

/* Archive objects are just single instances of archived keys */
class Archived{
	//Default algs and sizes used
	private static final String HASH = "SHA-256";
	private static final String CIPHER = "SHA-256";
	private static final int KEY_SIZE = 256;

	// The fingerprint of the disposable key
	private byte[] fingerprint;

	// Algs this arcive uses
	private String cipher;
	private String lock;
	private byte[] key;

	// Private key of this arcive
	private byte[] privateKey;

	// Time stamp saved
	private long timestamp;

	// signature on the archive
	private byte[] signature;

	// Create a new archive with a master key and a disposable key
	Archived(Key master, DisposableKey disposable) throws Exception {
		this(master, disposable, CIPHER, KEY_SIZE);
	}
	Archived(Key master, DisposableKey disposable, String cipher, int keySize) throws Exception {
		// Gen the fingerprint
		MessageDigest md = MessageDigest.getInstance(HASH);
		this.fingerprint =  md.digest(disposable.publicKey.getEncoded());

		//encrypt the private key
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

		// sign it
		this.signature = master.sign(this.getSignable());
	}

	// import a archive
	Archived(JSONObject fileData) throws JSONException {
		this.fingerprint = fileData.getString("fingerprint").getBytes();
		this.lock = fileData.getString("lock");
		this.key = fileData.getString("key").getBytes();
		this.cipher = fileData.getString("cipher");
		this.privateKey = fileData.getString("private").getBytes();
		this.timestamp = fileData.getLong("timestamp");
		this.signature = fileData.getString("signature").getBytes();
	}

	// get the archive in a signable format
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

	// verify the archive with a master key
	boolean verify(Key master){
		try {
			return master.verify(this.getSignable(), this.signature);
		} catch (Exception e) {
			return false;
		}
	}

	// export the archive as a JSONObject
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
