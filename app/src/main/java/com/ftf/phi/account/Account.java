package com.ftf.phi.account;

import com.ftf.phi.account.Files.FileManager;
import com.ftf.phi.account.keys.DisposableKey;
import com.ftf.phi.account.keys.Key;

import org.json.JSONObject;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Account {

	private Key master;
	private DisposableKey disposable;

	private Network network;

	private FileManager files;

	Account(String account) throws Exception {
		files = new FileManager(account);

		JSONObject account = new JSONObject(Arrays.toString(files.readFile("account.json", true)));
		this.master = new Key(account.getJSONObject("master"));
		this.disposable = new DisposableKey(master, account.getJSONObject("disposable"));

		this.network = new Network(Arrays.toString(files.readFile("network.json", true)));
	}

	public void newDisposable() throws NoSuchAlgorithmException {
		this.disposable = new DisposableKey(this.master);

		this.files.setDisposable(disposable);
	}

	public void logout(){
		try {
			this.disposable.lock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void login(){
		try {
			this.disposable.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//TODO: read messages from message buffer
	}

	void save() throws Exception {
		// Save the file system keys
		files.save();
		// Save the network keys
		files.writeFile("network.json", network.export().getBytes(), true);
		// Save the account keys
		JSONObject data = new JSONObject();
		data.put("master", this.master.asJSON());
		data.put("disposable", this.disposable.asJSON());
	}
}