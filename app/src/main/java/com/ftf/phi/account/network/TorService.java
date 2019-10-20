package com.ftf.phi.account.network;

import com.ftf.phi.account.keys.DisposableKey;

import org.json.JSONException;
import org.json.JSONObject;

public class TorService implements Service {

	DisposableKey disposable;

	public TorService(DisposableKey disposable, JSONObject service){
		this.updateDisposable(disposable);
		//TODO: implement the rest of the service
	}

	@Override
	public void updateDisposable(DisposableKey disposable) {
		this.disposable = disposable;
	}
	@Override
	public void start() {
		//TODO: start tor service
	}

	@Override
	public void stop() {
		//TODO: stop tor service
	}

	public JSONObject asJSON() throws JSONException {
		JSONObject data = new JSONObject();
		data.put("type", "tor");
		//TODO: get tor service as savable json
		return data;
	}

	public JSONObject asSendableJSON(){
		JSONObject data = new JSONObject();
		//TODO: get tor service as sendable json
		return data;
	}
}
