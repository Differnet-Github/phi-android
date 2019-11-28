package com.ftf.phi.account.keys.auth;

import com.ftf.phi.account.keys.auth.factors.password.Password;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/* Methods are just lists of factors used to encrypt the target key */
public class Method {
	// Default Cipher for the key
	private static final String CIPHER = "PBKDF2withHmacSHA256";
	//TODO: user a actual key mixer
	// Default mixer for the key
	private static final String MERGE = "PBKDF2withHmacSHA256";

	// Array of all of the factors
	private Factor[] factors;

	// The key we want to decrypt
	private byte[] key;

	// Cipher that was used
	private String cipher;
	// Key mixer that was used
	private String merge;

	// Create a method from a single factor
	public Method(Factor factor){
		this(new Factor[]{factor});
	}
	// Create a method from an array of factors
	public Method(Factor[] factors){
		this(factors, CIPHER, MERGE);
	}
	// Create a method from a array of factors with a cipher and a merge
	public Method(Factor[] factors, String cipher, String merge){
		this.factors = factors;
		this.cipher = cipher;
		this.merge = merge;
	}

	// Import a method from json
	public Method(JSONObject fileData) throws JSONException {
		JSONArray factors = fileData.getJSONArray("factors");
		this.factors = new Factor[factors.length()];
		for(int i = factors.length() - 1; i > -1; i--){
			JSONObject jsonFactor = factors.getJSONObject(i);
			Factor factor = null;
			switch(jsonFactor.getString("type")){
				case "password":
					factor = new Password(jsonFactor);
			}
			if(factor != null){
				this.factors[i] = factor;
			}
		}

		this.key = fileData.getString("key").getBytes();
		this.cipher = fileData.getString("cipher");
		this.merge = fileData.getString("merge");
	}

	// Set the encrypted key
	public void setKey(byte[] key) {
		try {
			getHalfKey((byte[] halfKey) -> {
				try {
				Cipher cipher = Cipher.getInstance(this.cipher);
				cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(halfKey, "AES"));
				this.key = cipher.doFinal(this.key);
				}
				catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
					e.printStackTrace();
				}
			});
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	// Mix together all of the keys into a half way key
	private void getHalfKey(Consumer<byte[]> callback) throws NoSuchAlgorithmException {
		// Run through all of the factors
		final byte[][] keys = new byte[this.factors.length][];
		for(int i = this.factors.length - 1; i > -1; i--){
			final int finalI = i;
			this.factors[i].getKey((byte[] authToken) -> {
				keys[finalI] = authToken;
			});
		}
		// Mix the keys from the factors
		//TODO: Support more key mixing algs
		byte[] halfKey = null;
		switch(this.merge){
			case "":
				//TODO: key merging
				break;
			default:
				throw new NoSuchAlgorithmException();
		}
		callback.accept(halfKey);
	}

	// Get the encrypted key
	public void getKey(Consumer<byte[]> callback) throws NoSuchAlgorithmException {
		getHalfKey((byte[] halfKey) -> {
			try {
				Cipher cipher = Cipher.getInstance(this.cipher);
				cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(halfKey, "AES"));
				callback.accept(cipher.doFinal(this.key));
			}
			catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
				e.printStackTrace();
			}
		});
	}

	// get the method as a JSONObject
	public JSONObject asJSON() throws JSONException {
		JSONObject json = new JSONObject();

		JSONArray factors = new JSONArray();
		for(int i = this.factors.length - 1; i > -1; i--){
			factors.put(i, this.factors[i].asJSON());
		}
		json.put("factors", factors);

		json.put("key", this.key.toString());
		json.put("cipher", this.cipher);
		json.put("merge", this.merge);

		return json;
	}
}
