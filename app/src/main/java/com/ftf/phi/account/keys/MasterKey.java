package com.ftf.phi.account.keys;


import com.ftf.phi.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MasterKey extends Keys {
	public MasterKey(){
		genKeys(R.integer.defualt_masterkey_size);
	}

	public MasterKey(int keySize){
		genKeys(keySize);
	}

	public MasterKey(JSONObject masterKey) throws JSONException {
		this.encrypted = masterKey.getBoolean("encrypted");

		this.importPublic(masterKey.getString("public").getBytes());
		this.importPrivate(masterKey.getString("private").getBytes());
	}

	public JSONObject asJSON() throws JSONException {
		JSONObject data = new JSONObject();
		//TODO: check if this should be in PEM
		data.put("public", this.exportPublic().toString());
		data.put("private", this.exportPrivate().toString());
		data.put("encrypted", this.encrypted);
		return data;
	}
}