package com.ftf.phi.account.network;

import com.ftf.phi.account.keys.DisposableKey;

import org.json.JSONException;
import org.json.JSONObject;

public class IPService implements Service {

	DisposableKey disposable;

	public IPService(DisposableKey disposable){
		this.updateDisposable(disposable);
		//TODO: implement the rest of the service
	}

	@Override
	public void updateDisposable(DisposableKey disposable) {
		this.disposable = disposable;
	}

	@Override
	public void start() {
		//TODO: start ip service
	}

	@Override
	public void stop() {
		//TODO: stop ip service
	}

	public JSONObject asJSON() throws JSONException {
		JSONObject data = new JSONObject();
		data.put("type", "ip");
		return data;
	}

	public JSONObject asSendableJSON(){
		JSONObject data = new JSONObject();
		//TODO: get ip service as sendable json
		return data;
	}
}
