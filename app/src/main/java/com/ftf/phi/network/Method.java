package com.ftf.phi.network;

import org.json.JSONObject;

import java.io.OutputStream;

public interface Method {
	void run(JSONObject data, OutputStream writer) throws Exception;
}
