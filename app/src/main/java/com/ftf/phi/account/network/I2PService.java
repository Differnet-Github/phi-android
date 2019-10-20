package com.ftf.phi.account.network;

import com.ftf.phi.account.keys.DisposableKey;

import org.json.JSONException;
import org.json.JSONObject;

public class I2PService implements Service {

	DisposableKey disposable;

	public I2PService(DisposableKey disposable, JSONObject service){
		this.updateDisposable(disposable);
	}

	@Override
	public void updateDisposable(DisposableKey disposable) {
		this.disposable = disposable;
	}

	@Override
	public void start() {
		//TODO: stop i2p service
	}

	@Override
	public void stop() {
		//TODO: start i2p service
	}

	public JSONObject asJSON() throws JSONException {
		JSONObject data = new JSONObject();
		data.put("type", "i2p");
		//TODO: get i2p service as savable json
		return data;
	}

	public JSONObject asSendableJSON(){
		JSONObject data = new JSONObject();
		//TODO: get i2p service as sendable json
		return data;
	}
}
