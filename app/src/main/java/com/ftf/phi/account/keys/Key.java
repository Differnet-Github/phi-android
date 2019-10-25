package com.ftf.phi.account.keys;

import com.ftf.phi.R;
import com.ftf.phi.account.keys.auth.Authentication;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Key {
	private static final String KEY_FACTORY = "EC";
	private static final String CIPHER = "AES/ECB/PKCS5PADDING";
	private static final String HASH = "SHA-256";

	private String factory;

	PublicKey publicKey;

	private String cipher;
	private byte[] ePrivateKey;
	PrivateKey privateKey;

	private Authentication keys;

	public boolean locked;

	public Key() throws NoSuchAlgorithmException {
		this(R.integer.default_key_size);
	}

	private Key(int keySize) throws NoSuchAlgorithmException {
		this(keySize, KEY_FACTORY);
	}

	private Key(int keySize, String factory) throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(factory);
		keyGen.initialize(keySize);
		KeyPair pair = keyGen.generateKeyPair();

		this.factory = factory;

		this.publicKey = pair.getPublic();
		this.cipher = CIPHER;
		this.privateKey = pair.getPrivate();

		this.keys = new Authentication();

		this.locked = false;
	}

	public Key(JSONObject fileData) throws JSONException {

		this.factory = fileData.getString("factory");

		this.locked = fileData.getBoolean("locked");

		this.publicKey = (PublicKey) new X509EncodedKeySpec(fileData.getString("public").getBytes());

		this.cipher = fileData.getString("cipher");
		if(this.locked){
			this.ePrivateKey = fileData.getString("private").getBytes();
		}
		else{
			this.privateKey = (PrivateKey) new X509EncodedKeySpec(fileData.getString("private").getBytes());
		}

		this.keys = new Authentication(fileData.getJSONArray("keys"));
	}

	public void lock() throws NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
		if(!this.locked){
			Cipher cipher = Cipher.getInstance(this.cipher);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.keys.getKey(), "AES"));

			this.ePrivateKey = cipher.doFinal(this.privateKey.getEncoded());
			this.privateKey = null;
		}
	}
	public void unlock() throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		if(this.locked){
			Cipher cipher = Cipher.getInstance(this.cipher);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this.keys.getKey(), "AES"));

			this.ePrivateKey = null;
			this.privateKey = (PrivateKey) new X509EncodedKeySpec(this.privateKey.getEncoded());
		}
	}

	public byte[] encrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance(this.getCipher());
		try {
			cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
			return cipher.doFinal(data);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	public byte[] decrypt(byte[] data) throws Exception {
		if(this.locked){
			//TODO: name exception better
			throw new Exception();
		}
		Cipher cipher;
		//TODO: add more support for key types
		switch(this.factory){
			case "EC":
				cipher = Cipher.getInstance("ECIES");
				break;
			default:
				throw new NoSuchAlgorithmException();
		}
		try {
			cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
			return cipher.doFinal(data);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	public String getCipher(){
		//TODO: support more
		switch(this.factory){
			case "EC":
				return "ECIES";
		}
		return null;
	}

	boolean verify(byte[] data, byte[] signature) throws NoSuchAlgorithmException {
		Signature checker = Signature.getInstance(this.getSigner());

		try {
			checker.initVerify(this.publicKey);
			checker.update(data);
			return checker.verify(signature);
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return false;
	}
	public byte[] sign(byte[] data) throws Exception {
		if(this.locked){
			//TODO: name exception better
			throw new Exception();
		}
		Signature signature = Signature.getInstance(this.getSigner());

		try {
			signature.initSign(this.privateKey);
			signature.update(data);
			return signature.sign();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	public String getSigner(){
		//TODO: support more
		switch(this.factory) {
			case "EC":
				return "SHA256withECDSA";
		}
		return null;
	}

	public byte[] getFingerprint() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(this.privateKey.getEncoded());
	}

	public String getHasher(){
		return HASH;
	}

	public JSONObject asJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("factory", this.factory);

		json.put("public", Arrays.toString(this.publicKey.getEncoded()));

		json.put("cipher", this.cipher);
		if(this.locked){
			json.put("private", Arrays.toString(this.privateKey.getEncoded()));

		}
		else{
			json.put("private", this.ePrivateKey);
		}

		json.put("locked", this.locked);
		json.put("keys", this.keys.asJSON());

		return json;
	}
}