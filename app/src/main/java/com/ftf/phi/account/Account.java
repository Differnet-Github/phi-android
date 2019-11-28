package com.ftf.phi.account;

import com.ftf.phi.account.Files.FileManager;
import com.ftf.phi.account.keys.DisposableKey;
import com.ftf.phi.account.keys.Key;

import org.json.JSONObject;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.function.Consumer;

public class Account {

	private Key master;
	private DisposableKey disposable;

	private Network network;

	private FileManager files;

	Account() {
		try {
			this.files = new FileManager();
			this.master = new Key(new JSONObject(this.files.readFile("master.json", true).toString()));
			this.disposable = new DisposableKey(this.master, new JSONObject(this.files.readFile("disposable.json", true).toString()));

			this.network = new Network(Arrays.toString(this.files.readFile("network.json", true)));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	Account(File account) {
		try {
			this.files = new FileManager(account);

			this.master = new Key(new JSONObject(this.files.readFile("master.json", true).toString()));
			this.disposable = new DisposableKey(this.master, new JSONObject(this.files.readFile("disposable.json", true).toString()));

			this.network = new Network(Arrays.toString(this.files.readFile("network.json", true)));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void newKey(Runnable callback) {
		try{
			this.disposable = new DisposableKey(this.master);
			this.files.newKey(disposable);
			callback.run();
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public void logout(Runnable callback){
		try {
			this.disposable.lock();
			callback.run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void login(Runnable callback){
		try {
			this.disposable.unlock();
			callback.run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//TODO: read messages from message buffer
	}

	//TODO: get hash of master public key
	public void getID(Consumer<String> callback) {
		try {
			callback.accept(Arrays.toString(this.master.getFingerprint()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	void save(Runnable callback) throws Exception {
		//TODO: multi thread
		// Save the file system keys
		files.save(this.disposable);
		// Save the keys
		files.writeFile("network.json", this.network, true);
		files.writeFile("master.json", this.master, true);
		files.writeFile("disposable.json", this.disposable, true);
		callback.run();
	}
}