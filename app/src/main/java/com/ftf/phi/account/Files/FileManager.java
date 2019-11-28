package com.ftf.phi.account.Files;

import com.ftf.phi.R;
import com.ftf.phi.account.keys.DisposableKey;
import com.ftf.phi.account.keys.Key;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/* The file manager class is in charge of encrypting and decrypt files for each account */
public class FileManager {
	// Key size of the file system key when a new one is made
	private static final int KEY_SIZE = 256;

	// The root of the accounts file system
	private File root;

	// Information for encrypting
	// lock is the algorithm used to encrypt the key
	private String lock;
	// cipher is the algorithm used to encrypt the filesystem
	private String cipher;

	// Information about the key
	// locked is just a boolean value to say if key is encrypted or decrypted
	private boolean locked;
	// key is the key to encrypt and decrypt the filesystem
	private byte[] key;

	// timestamp is the time the key was created at
	private long timestamp;

	// this is just signature data
	private String signer;
	private byte[] signature;

	public FileManager() throws Exception {
		// create a master and disposable key for the account
		Key master = new Key();
		DisposableKey disposable = new DisposableKey(master);

		this.lock = disposable.getCipher();
		this.cipher = "AES/ECB/PKCS5Padding";

		// create a file system key
		this.newKey(disposable);

		// save the filesystem keys
		this.save(disposable);
		this.writeFile("master.json", master, true);
		this.writeFile("disposable.json", disposable, true);
	}

	public FileManager(File root) throws Exception {
		this.root = root;
		// If the folder already exist import the filesystem
		if(root.exists()){
			// the files setting is in a json format
			JSONObject json = new JSONObject(Arrays.toString(this.readFile("files.json", true)));

			// Load all of the things
			this.lock = json.getString("lock");
			this.cipher = json.getString("cipher");

			this.key = json.getString("key").getBytes();
			this.locked = json.getBoolean("locked");

			this.timestamp = json.getLong("timestamp");

			this.signer = json.getString("signer");
			this.signature = json.getString("signature").getBytes();
		}
		// If it does not exist create a file system
		else {
			throw new Exception();
		}
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

	// Get a path in its encrypted format
	private File getPath(String path, boolean raw) throws Exception {
		// If it is raw then its just appended to the root
		if(raw){
			return new File(this.root.getAbsolutePath(), path);
		}
		// If it isnt raw then we need to break the key into its parts and encrypt them
		else{
			String newPath = "";
			String[] chunks = path.split("/");
			for(int i = chunks.length - 1; i > -1; i--){
				newPath += "/" + Arrays.toString(this.encrypt(chunks[i].getBytes()));
			}
			// Append the encrypted path to the root path
			return new File(this.root.getAbsolutePath(), newPath);
		}
	}

	//List files in directory
	public String[] list(String path) throws Exception {
		return this.list(path, true, false, false, false);
	}
	public String[] list(String path, boolean file, boolean folder, boolean walk) throws Exception {
		return this.list(path, file, folder, walk, false);
	}
	public String[] list(String path, boolean file, boolean folder, boolean walk, boolean raw) throws Exception {
		File[] files = this.list(this.getPath(path, raw), file, folder, walk);
		String[] paths = new String[files.length];
		if(!raw){
			for(int i = files.length - 1; i > -1; i--){
				String newPath = "";
				String[] chunks = paths[i].substring(this.root.getAbsolutePath().length()).split("/");
				for(int j = 0; j < chunks.length; j++){
					newPath += Arrays.toString(this.decrypt(chunks[j].getBytes())) + "/";
				}
				paths[i] = newPath;
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

	//Read target file
	public byte[] readFile(String path) throws Exception {
		return this.readFile(path, false);
	}
	public byte[] readFile(String file, boolean raw) throws Exception {
		File f = this.getPath(file, raw);
		Scanner scanner = new Scanner(f);
		scanner.useDelimiter("\\Z");
		byte[] bytes = scanner.next().getBytes();
		if(raw){
			return bytes;
		}
		else {
			return this.decrypt(bytes);
		}
	}

	//Write target file
	public void writeFile(String path, Savable data) throws Exception {
		writeFile(path, data, false);
	}
	public void writeFile(String path, Savable data, boolean raw) throws Exception {
		writeFile(path, data.export().getBytes(), false);
	}
	public void writeFile(String path, byte[] data) throws Exception {
		this.writeFile(path, data, false);
	}
	public void writeFile(String path, byte[] data, boolean raw) throws Exception {
		byte[] bytes = data;
		if(!raw){
			bytes = this.encrypt(bytes);
		}
		File file = this.getPath(path, raw);
		FileOutputStream writer = new FileOutputStream(file);
		writer.write(bytes);
	}

	//Test if file exist
	public boolean exist(String path) throws Exception {
		return this.exist(path, false);
	}
	public boolean exist(String path, boolean encrypted) throws Exception {
		return this.getPath(path, encrypted).exists();
	}

	//Delete a file
	public void unlink(String path) throws Exception {
		this.unlink(path, false);
	}
	public void unlink(String path, boolean encrypted) throws Exception {
		this.getPath(path, encrypted).delete();
	}

	//Lock the file system
	void lock(DisposableKey disposable) throws NoSuchPaddingException, NoSuchAlgorithmException {
		// We only need to lock if it is unlocked
		if(!this.locked){
			this.key = disposable.encrypt(this.key);
			this.locked = true;
			this.lock = disposable.getCipher();
		}
	}
	//Unlock the file system with the key
	void unlock(DisposableKey disposable) throws Exception {
		// We only need to unlock if it is locked
		if(locked){
			if(this.lock != disposable.getCipher()){
				throw new Exception();
			}
			this.key = disposable.decrypt(this.key);
			this.locked = false;
		}
	}

	public void newKey(DisposableKey disposable){
		//TODO: create new filesystem key
	}

	public void save(DisposableKey disposable) throws Exception {
		// set the time
		this.timestamp = new Date().getTime();

		// sign the filesystem
		this.signer = disposable.getSigner();
		this.signature = disposable.sign(this.getSignable());
		//TODO: save file.json
	}

	//Get the file system key in a signable form
	private byte[] getSignable() throws Exception {
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

		if(!locked){
			//TODO: name exception better
			throw new Exception();
		}

		byte[] signable = new byte[key.length + timeBytes.length];
		System.arraycopy(key, 0, signable, 0, key.length);
		System.arraycopy(timeBytes, 0, signable, key.length, timeBytes.length);

		return signable;
	}
}
