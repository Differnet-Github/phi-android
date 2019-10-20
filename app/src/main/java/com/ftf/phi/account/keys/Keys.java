package com.ftf.phi.account.keys;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Keys {
	protected boolean encrypted;

	protected PrivateKey privateKey;
	protected byte[] ePrivateKey;

	protected PublicKey publicKey;

	protected void genKeys(int size) {
		try{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(size);
			KeyPair pair = keyGen.generateKeyPair();

			this.privateKey = pair.getPrivate();
			this.publicKey = pair.getPublic();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	//TODO: better naming for exception
	public void lock(byte[] key) throws Exception {
		if(this.encrypted){
			throw new Exception("Key already encrypted");
		}
		else{
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));

			this.ePrivateKey = cipher.doFinal(this.privateKey.getEncoded());

			this.privateKey = null;
		}
	}

	public void unlock(byte[] key) throws Exception {
		if(this.encrypted){
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
			this.privateKey = (PrivateKey) new X509EncodedKeySpec(cipher.doFinal(this.ePrivateKey));

			this.ePrivateKey = null;
		}
		else {
			throw new Exception("Key already encrypted");
		}
	}

	public byte[] encrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
		return cipher.doFinal(data);
	}

	//TODO: Better exception naming
	public byte[] decrypt(byte[] data) throws Exception {
		if(this.encrypted){
			throw new Exception();
		}
		else {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
			return cipher.doFinal(data);
		}
	}

	//TODO: Better exception naming
	public byte[] sign(byte[] data) throws Exception {
		if(this.encrypted){
			throw new Exception();
		}
		else {
			Signature signature = Signature.getInstance("SHA1withRSA", "BC");
			signature.initSign(this.privateKey);
			signature.update(data);
			return signature.sign();
		}
	}

	public boolean verify(byte[] data, byte[] signature) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature checker = Signature.getInstance("SHA1withRSA", "BC");
		checker.initVerify(this.publicKey);
		checker.update(data);
		return checker.verify(signature);
	}

	public void importPublic(byte[] publicKey){
		this.publicKey = (PublicKey) new X509EncodedKeySpec(publicKey);
	}

	public void importPrivate(byte[] privateKey){
		if(this.encrypted){
			this.privateKey = (PrivateKey) new X509EncodedKeySpec(privateKey);
		}
		else{
			this.ePrivateKey = privateKey;
		}
	}

	public byte[] exportPublic(){
		return this.publicKey.getEncoded();
	}

	public byte[] exportPrivate(){
		if(encrypted){
			return this.ePrivateKey;
		}
		else{
			return this.privateKey.getEncoded();
		}
	}
}
