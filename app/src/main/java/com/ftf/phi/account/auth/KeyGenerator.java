package com.ftf.phi.account.auth;

import org.json.JSONException;
import org.json.JSONObject;

//TODO: separate out disposable and master key locks
public class KeyGenerator {

	Authorization master;
	Authorization disposable;

	public KeyGenerator(){
		master = new Authorization();
		disposable = new Authorization();
	}

	public KeyGenerator(JSONObject auths) {
		try {
			master = new Authorization(auths.getJSONObject("master"));
			disposable= new Authorization(auths.getJSONObject("disposable"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject asJSON() throws JSONException {
		JSONObject data = new JSONObject();
		data.put("master", master.asJSON());
		data.put("disposable", disposable.asJSON());
		return data;
	}
}
