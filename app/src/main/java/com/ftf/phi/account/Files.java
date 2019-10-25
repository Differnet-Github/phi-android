package com.ftf.phi.account;

import com.ftf.phi.account.keys.DisposableKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class Files {
	private static final String CIPHER = "SHA-256";
	private static final int KEY_SIZE = 256;

	private String root;

	private String lock;
	private String cipher;

	private byte[] key;
	private byte[] lockedKey;

	private long timestamp;

	private String signer;
	private byte[] signature;

	//Create a new file system with a key and default cipher and key size
	Files(DisposableKey disposable, String root) throws Exception {
		this(disposable, root, CIPHER, KEY_SIZE);
	}

	//Create a new file system
	Files(DisposableKey disposable, String root, String cipher, int keySize) throws Exception {
		this.root = root;

		this.lock = disposable.getCipher();
		this.cipher = cipher;

		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(keySize);
		this.key = keyGen.generateKey().getEncoded();

		this.lockedKey = disposable.encrypt(this.key);

		this.timestamp = new Date().getTime();

		this.signer = disposable.getSigner();
		this.signature = disposable.sign(this.getSignable());
	}

	//Load a file system
	Files(JSONObject fileData, String root) throws JSONException {
		this.root = root;
		this.lock = fileData.getString("lock");
		this.cipher = fileData.getString("cipher");
		this.lockedKey = fileData.getString("key").getBytes();
		this.signer = fileData.getString("signer");
		this.signature = fileData.getString("signature").getBytes();
	}
	//Load a file system and unlock it
	Files(JSONObject fileData, String root, DisposableKey disposable) throws JSONException {
		this(fileData, root);
		try {
			this.unlock(disposable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Lock the file system
	void lock(){
		this.key = null;
	}
	//Unlock the file system with the key
	void unlock(DisposableKey key) throws Exception {
		this.key = key.decrypt(this.lockedKey);
	}
	//Encrypt bytes with the key
	private byte[] encrypt(byte[] data) throws Exception {
		if(this.key == null){
			throw new Exception();
		}
		Cipher cipher = Cipher.getInstance(this.cipher);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.key, "AES"));
		return cipher.doFinal(data);
	}
	//Decrypt bytes with the key
	private  byte[] decrypt(byte[] data) throws Exception {
		if(this.key == null){
			throw new Exception();
		}
		Cipher cipher = Cipher.getInstance(this.cipher);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this.key, "AES"));
		return cipher.doFinal(data);
	}

	//Change the disposable key
	void updateDisposable(DisposableKey newKey) throws Exception {
		if(this.key == null){
			throw new Exception();
		}
		this.lockedKey = newKey.encrypt(this.key);
	}

	//Create a new key with default cipher size and size
	public void regenKey(){
		this.regenKey(CIPHER, KEY_SIZE);
	}
	//Create a new key
	private void regenKey(String cipher, int keySize){
		//TODO: generate a key and re encrypt the files with it
	}

	//Get the file system key in a signable form
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

		byte[] signable = new byte[lockedKey.length + timeBytes.length];
		System.arraycopy(lockedKey, 0, signable, 0, lockedKey.length);
		System.arraycopy(timeBytes, 0, signable, lockedKey.length, timeBytes.length);

		return signable;
	}

	public boolean locked(){
		return key == null;
	}

	private String getPath(String path, boolean encrypted) throws Exception {
		if(encrypted){
			return this.root;
		}
		else{
			String newPath = this.root;
			String[] chunks = path.split("/");
			for(int i = chunks.length - 1; i > -1; i--){
				newPath += this.encrypt(chunks[i].getBytes()).toString() + "/";
			}
			return newPath;
		}
	}

	//List files in directory
	public String[] list(String path){
		return this.list(path, true, false, false, false);
	}
	public String[] list(String path, boolean file, boolean folder, boolean walk){
		return this.list(path, file, folder, walk, false);
	}
	public String[] list(String path, boolean file, boolean folder, boolean walk, boolean encrypted){
		File[] files = this.list(new File(this.root + path), file, folder, walk);
		String[] paths = new String[files.length];
		for(int i = files.length - 1; i > -1; i--){
			paths[i] = files[i].getAbsolutePath().substring(this.root.length());
			try {
				if(!encrypted){
					String newPath = this.root;
					String[] chunks = paths[i].split("/");
					for(int j = chunks.length - 1; j > -1; j--){
						newPath += this.encrypt(chunks[j].getBytes()).toString() + "/";
					}
					paths[i] = newPath;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return paths;
	}
	//Helper function for list
	protected File[] list(File path, boolean file, boolean folder, boolean walk){
		File[] files = path.listFiles();
		File[][] walkFiles = new File[files.length][];

		int found = 0;

		if(!file || !folder || walk){
			for(int i = files.length - 1; i > -1; i--){
				if(files[i].isFile()){
					if(!file){
						walkFiles[i] = new File[]{files[i]};
					}
				}
				else {
					if(walk){
						walkFiles[i] = this.list(files[i], file, folder, walk);
					}
					else if(folder){
						walkFiles[i] = new File[]{files[i]};
					}
				}
				found += walkFiles[i].length;
			}
		}

		int j = 0;
		files = new File[found];
		for(int i = walkFiles.length - 1; i > -1; i--){
			System.arraycopy(walkFiles[i], 0, files, j, walkFiles[i].length);
			j += walkFiles.length;
		}
		return files;
	}

	//Read from a file
	public byte[] readFile(String path) throws Exception {
		return this.readFile(path, false);
	}
	public byte[] readFile(String path, boolean encrypted) throws Exception {
		File file = new File(this.getPath(path, encrypted));
		Scanner scanner = new Scanner(file);
		scanner.useDelimiter("\\Z");
		byte[] bytes = scanner.next().getBytes();
		if(encrypted){
			return bytes;
		}
		else {
			return this.decrypt(bytes);
		}
	}

	//Write to a file
	public void writeFile(String path, byte[] data) throws Exception {
		this.writeFile(path, data, false);
	}
	public void writeFile(String path, byte[] data, boolean encryped) throws Exception {
		byte[] bytes = data;
		if(!encryped){
			bytes = this.encrypt(bytes);
		}
		File file = new File(this.getPath(path, encryped));
		FileOutputStream writer = new FileOutputStream(file);
		writer.write(bytes);
	}

	//Test if file exist
	public boolean exist(String path) throws Exception {
		return this.exist(path, false);
	}
	public boolean exist(String path, boolean encrypted) throws Exception {
		return new File(this.getPath(path, encrypted)).exists();
	}

	//Delete a file
	public void unlink(String path) throws Exception {
		this.unlink(path, false);
	}
	public void unlink(String path, boolean encrypted) throws Exception {
		new File(this.getPath(path, encrypted)).delete();
	}

	//Get the file system key as saleable json
	JSONObject asJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("lock", this.lock);
		json.put("cipher", this.cipher);
		json.put("lockedKey", this.lockedKey);
		json.put("signer", this.signer);
		json.put("signature", this.signature);

		return json;
	}
}
