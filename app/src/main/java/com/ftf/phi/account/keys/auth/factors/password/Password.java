package com.ftf.phi.account.keys.auth.factors.password;

import com.ftf.phi.ByteCallback;
import com.ftf.phi.account.keys.auth.Factor;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Password implements Factor {
	private static final String PASSWORD_HASH = "PBKDF2withHmacSHA256";

	private String hash;
	private byte[] salt;
	private int iterations;
	private int keyLength;

	public Password(){
		this(PASSWORD_HASH, 999999, 64, 256);
	}

	public Password(String hash, int iterations, int saltBytes, int keyLength){
		this.hash = hash;
		this.iterations = iterations;

		this.salt = new byte[saltBytes];
		SecureRandom random = new SecureRandom();
		random.nextBytes(this.salt);

		this.keyLength = keyLength;
	}

	public Password(JSONObject fileData) throws JSONException {
		this.hash = fileData.getString("hash");
		this.salt = fileData.getString("salt").getBytes();
		this.iterations = fileData.getInt("iterations");
		this.keyLength = fileData.getInt("keyLength");
	}

	@Override
	public void getKey(final ByteCallback callback) throws NoSuchAlgorithmException {
		//TODO: support more hashes
		switch(this.hash){
			case "PBKDF2withHmacSHA256":
				new Popup(new ByteCallback() {
					@Override
					public void call(byte[] password) {
						PBEKeySpec spec = new PBEKeySpec(password.toString().toCharArray(), salt, iterations, keyLength);
						SecretKeyFactory skf;

						try {
							skf = SecretKeyFactory.getInstance(hash);
							callback.call(skf.generateSecret(spec).getEncoded());
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						} catch (InvalidKeySpecException e) {
							e.printStackTrace();
						}
					}
				});
			default:
				throw new NoSuchAlgorithmException();
		}
	}

	@Override
	public JSONObject asJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("type", "password");

		json.put("hash", this.hash);
		json.put("salt", this.salt.toString());
		json.put("iterations", this.iterations);
		json.put("keyLength", this.keyLength);

		return json;
	}
}
