package com.ftf.phi.account.auth;

public interface AuthCallback {
	void call(byte[] authToken);
}
