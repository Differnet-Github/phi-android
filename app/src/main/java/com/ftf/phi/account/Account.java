package com.ftf.phi.account;

import com.ftf.phi.account.auth.KeyGenerator;
import com.ftf.phi.account.keys.Archive;
import com.ftf.phi.account.keys.DisposableKey;
import com.ftf.phi.account.keys.MasterKey;
import com.ftf.phi.account.network.Network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.SecretKey;

public class Account {

	File directory;

	KeyGenerator authorization;

	MasterKey masterKey;
	DisposableKey disposableKey;

	Archive archive;

	Network network;

	byte[] accessKey;

	Body body;

	public Account(){
		authorization = new KeyGenerator();

		masterKey = new MasterKey();
		disposableKey = new DisposableKey(masterKey);

		archive = new Archive(masterKey);

		network = new Network(disposableKey);

		try{
			body = new Body(disposableKey);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		javax.crypto.KeyGenerator keyGen = null;
		try {
			keyGen = javax.crypto.KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyGen.init(256); // for example
		accessKey = keyGen.generateKey().getEncoded();

		try {
			this.save();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Account(File directory){
		this.directory = directory;
		this.reload();
	}

	public void reload(){
		File account = new File(this.directory.getPath() + "account.json");

		Scanner scanner = null;
		JSONObject fileData;
		try {
			scanner = new Scanner(account);
			scanner.useDelimiter("\\Z");
			fileData = new JSONObject(scanner.next());

			KeyGenerator auths = new KeyGenerator(fileData.getJSONObject("auth"));

			this.masterKey = new MasterKey(fileData.getJSONObject("master"));
			this.disposableKey= new DisposableKey(this.masterKey, fileData.getJSONObject("disposable"));

			this.archive = new Archive(this.masterKey);

			this.network = new Network(this.disposableKey, fileData.getJSONArray("network"));

			this.accessKey = fileData.getString("access").getBytes();

			this.body = new Body(disposableKey, fileData.getJSONObject("body"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(scanner != null){
				scanner.close();
			}
		}
	}

	public String getID(){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			return md.digest(masterKey.exportPublic()).toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void save() throws JSONException, IOException {
		JSONObject data = new JSONObject();
		data.put("auth", this.authorization.asJSON());
		data.put("master", this.masterKey.asJSON());
		data.put("disposable", this.disposableKey.asJSON());
		data.put("archive", this.archive.asJSON());
		data.put("network", this.network.asJSON());
		data.put("access", this.accessKey.toString());
		data.put("body", this.body.asJSON());

		File account = new File(this.directory.getPath() + "account.json");

		FileOutputStream writer = null;
		writer = new FileOutputStream(account);
		writer.write(data.toString().getBytes());
	}
}