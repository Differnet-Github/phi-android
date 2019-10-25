package com.ftf.phi.account.keys.auth;

import com.ftf.phi.ByteCallback;
import com.ftf.phi.account.keys.auth.factors.password.Password;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Method {
	private static final String CIPHER= "PBKDF2withHmacSHA256";
	private static final String MERGE= "PBKDF2withHmacSHA256";

	Factor[] factors;

	byte[] key;

	String cipher;
	String merge;

	public Method(Factor factor){
		this(new Factor[]{factor}, CIPHER, MERGE);
	}

	public Method(Factor[] factors, String cipher, String merge){
		this.factors = factors;
		this.cipher = cipher;
		this.merge = merge;
	}

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

	public void setKey(byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance(this.cipher);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getHalfKey(), "AES"));
		this.key = cipher.doFinal(this.key);
	}

	private byte[] getHalfKey() throws NoSuchAlgorithmException {
		final byte[][] keys = new byte[this.factors.length][];
		for(int i = this.factors.length - 1; i > -1; i--){
			final int finalI = i;
			this.factors[i].getKey(new ByteCallback() {
				@Override
				public void call(byte[] authToken) {
					keys[finalI] = authToken;
				}
			});
		}
		//TODO: Support more key merging algs
		byte[] halfKey = null;
		switch(this.merge){
			case "":
				//TODO: key merging
				break;
			default:
				throw new NoSuchAlgorithmException();
		}
		return halfKey;
	}

	public byte[] getKey() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance(this.cipher);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(getHalfKey(), "AES"));
		return cipher.doFinal(this.key);
	}

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
