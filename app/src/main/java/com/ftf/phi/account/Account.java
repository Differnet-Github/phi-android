package com.ftf.phi.account;

import com.ftf.phi.account.keys.Archive;
import com.ftf.phi.account.keys.DisposableKey;
import com.ftf.phi.account.keys.Key;
import com.ftf.phi.network.Restful;
import com.ftf.phi.network.api.V1;
import com.ftf.phi.network.service.Service;
import com.ftf.phi.network.service.ip.IPService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Account {

	private File directory;

	private Key master;
	private DisposableKey disposable;

	private Archive archive;

	private Restful api;
	private Service[] services;

	private Files files;

	Account(File directory) throws Exception {
		this.directory = directory;

		//Get the file that is hosting the account
		File account = new File(this.directory.getPath() + "account.json");

		//Set up a scanner
		Scanner scanner = null;
		try {
			//Read the file into a json object
			scanner = new Scanner(account);
			scanner.useDelimiter("\\Z");
			JSONObject fileData = new JSONObject(scanner.next());

			//Turn the json object into the useful classes
			this.master = new Key(fileData.getJSONObject("master"));
			this.disposable = new DisposableKey(master, fileData.getJSONObject("disposable"));
			this.archive = new Archive(master, fileData.getJSONArray("archive"));
			this.files = new Files(fileData.getJSONObject("files"), this.directory.getPath(), disposable);

			//Create all of the services
			JSONArray network = fileData.getJSONArray("network");
			this.services = new Service[network.length()];
			for(int i = network.length() - 1; i > -1; i--){
				Service service = null;
				switch(network.getJSONObject(i).getString("type")){
					case "tor":
					case "i2p":
						//TODO: tor and i2p services
						break;
					case "ip":
						service = new IPService(this.api);
						break;
				}
				if(service != null){
					this.services[i] = service;
				}
			}
		}
		catch (FileNotFoundException e){
			//If we didn't find a file then close the scanner
			if(scanner != null){
				scanner.close();
			}

			//If we didn't fine a file then this is a new account and we need to initialize all of its attributes
			this.master = new Key();
			this.disposable = new DisposableKey(master);
			this.archive = new Archive(master);
			this.files = new Files(disposable, this.directory.getPath());

			//TODO: create tor service for new account
			this.services = new Service[]{new IPService(api)};
		}

		this.api = new V1(this.files);
	}

	public void changeDisposable() throws Exception {
		//Unlock the file system
		this.files.unlock(this.disposable);
		//Add the current disposable key to the archive
		this.archive.add(this.disposable);

		//Create a new disposable key
		this.disposable = new DisposableKey(master);
		//Give the file system the new disposable key and make a new key for it
		this.files.updateDisposable(this.disposable);
		this.files.regenKey();
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

	void save() throws JSONException, IOException {
		JSONObject data = new JSONObject();

		data.put("master", this.master.asJSON());
		data.put("disposable", this.disposable.asJSON());

		data.put("archive", this.archive.asJSON());

		//TODO: save network info

		data.put("files", this.files.asJSON());

		File account = new File(this.directory.getPath() + "account.json");

		FileOutputStream writer = new FileOutputStream(account);
		writer.write(data.toString().getBytes());
	}
}