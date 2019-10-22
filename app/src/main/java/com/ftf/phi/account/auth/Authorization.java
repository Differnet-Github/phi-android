package com.ftf.phi.account.auth;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
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

	public void getKey(final AuthCallback callback) throws NoSuchAlgorithmException {
		final ArrayList<byte[]> keys = new ArrayList();

		for(int i = this.methods.size() - 1; i > -1; i--){
			this.methods.get(i).getKey(new AuthCallback() {
				@Override
				public void call(byte[] authToken) {
					keys.add(authToken);
					if(keys.size() == methods.size()){
						joinKeys(callback, keys);
					}
				}
			});
		}
	}

	public void joinKeys(AuthCallback callback, ArrayList<byte[]> keys){
		//TODO: join all of the keys in a nice way
		for(int i = 0; i < keys.size(); i++){
			Log.d("Key", i + ": " + keys.get(i).toString());
		}
		callback.call(keys.get(0));
	}

	public JSONArray asJSON() throws JSONException {
		JSONArray data = new JSONArray();
		for(int i = this.methods.size() - 1; i > -1; i--){
			data.put(i, this.methods.get(i).asJSON());
		}
		return data;
	}
}
