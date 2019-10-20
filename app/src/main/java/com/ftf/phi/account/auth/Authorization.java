package com.ftf.phi.account.auth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Authorization {

	byte[] last;

	ArrayList<Auth> methods = new ArrayList();

	public Authorization(){
		this.last = new byte[0];
	}

	public Authorization(JSONObject auth) throws JSONException {

		this.last = auth.getString("last").getBytes();

		JSONArray methods = auth.getJSONArray("methods");
		for(int i = methods.length() - 1; i > -1; i--){
			try{
				JSONObject method = methods.getJSONObject(i);
				switch(auth.getString("type")){
					case "password":
						this.methods.add(new PasswordAuth(method));
						break;
					//TODO: implement more auth methods
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void getKey(){
		//TODO: Implement with a callback
	}

	public JSONArray asJSON() throws JSONException {
		JSONArray data = new JSONArray();
		for(int i = this.methods.size() - 1; i > -1; i--){
			data.put(i, this.methods.get(i).asJSON());
		}
		return data;
	}
}
