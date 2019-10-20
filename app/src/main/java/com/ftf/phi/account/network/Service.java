package com.ftf.phi.account.network;

import com.ftf.phi.account.keys.DisposableKey;

import org.json.JSONException;
import org.json.JSONObject;

public interface Service {
	void start();
	void stop();
	void updateDisposable(DisposableKey disposable);
	JSONObject asJSON() throws JSONException;
	JSONObject asSendableJSON() throws JSONException;
}
