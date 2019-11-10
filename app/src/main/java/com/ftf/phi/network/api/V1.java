package com.ftf.phi.network.api;

import com.ftf.phi.account.Files.FileManager;
import com.ftf.phi.network.Method;
import com.ftf.phi.network.Restful;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class V1 extends Restful {
	private FileManager fileSystem;

	public V1(final FileManager fileSystem){
		this.fileSystem = fileSystem;

		//GET ROUTES

		//user routes
		this.get("/v1/user", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) throws Exception {
				String[] users = fileSystem.list("/users/", false, true, false);
				JSONObject json = new JSONObject();

				json.put("stat", 200);

				JSONArray array = new JSONArray();
				for(int i = users.length - 1; i > - 1; i--){
					array.put(i, users[i]);
				}
				//TODO: filter users based on request
				json.put("users", array);

				writer.write(json.toString().getBytes());
			}
		});
		this.get("/v1/user/profile", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) throws Exception {
				String user = Arrays.toString(fileSystem.readFile("/users/" + data.getString("fingerprint") + "/profile.json"));

				JSONObject json = new JSONObject();

				json.put("stat", 200);
				json.put("user", new JSONObject(user));

				writer.write(json.toString().getBytes());
			}
		});
		this.get("/v1/user/photo", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) throws Exception {
				String photo = Arrays.toString(fileSystem.readFile("/users/" + data.getString("fingerprint") + "/photo.png"));

				JSONObject json = new JSONObject();

				json.put("stat", 200);
				json.put("photo", photo);

				writer.write(json.toString().getBytes());
			}
		});
		this.get("/v1/user/keys", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) throws Exception {
				String keys = Arrays.toString(fileSystem.readFile("/users/" + data.getString("fingerprint") + "/keys.json"));

				JSONObject json = new JSONObject();

				json.put("stat", 200);
				json.put("keys", new JSONObject(keys));

				writer.write(json.toString().getBytes());
			}
		});
		this.get("/v1/user/keys/archive", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) throws IOException {
				//TODO: implement archive request
				writer.write("{\"stat\":501}".getBytes());
			}
		});

		//post routes
		this.get("/v1/post", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) {
				//TODO: get all post and filter out based on request
			}
		});
		this.get("/v1/post/header", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) {
				//TODO: get post header
			}
		});
		this.get("/v1/post/body", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) {
				//TODO: get post body
			}
		});

		//message routes
		this.get("/v1/message", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) {
				//TODO: get all messages and filter out based on request
			}
		});
		this.get("/v1/message/header", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) {
				//TODO: get post header
			}
		});
		this.get("/v1/message/body", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) {
				//TODO: get post body
			}
		});

		//POST ROUTES

		//connection routes
		this.post("/v1/connection/initial", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) {

			}
		});
		this.post("/v1/connection/verification", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) {

			}
		});
		this.post("/v1/connection/finalize", new Method(){
			@Override
			public void run(JSONObject data, OutputStream writer) {

			}
		});
	}
}
