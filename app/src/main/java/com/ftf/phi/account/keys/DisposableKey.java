package com.ftf.phi.account.keys;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class DisposableKey extends Key {
	private long timestamp;
	private byte[] signature;

	private String signer;

	public DisposableKey(Key master) throws NoSuchAlgorithmException {
		super();
		this.setMaster(master);
	}

	public DisposableKey(Key master, JSONObject fileData) throws JSONException {
		super(fileData);
	}

	private void setMaster(Key master){
		try{
			this.timestamp = new Date().getTime();

			this.signer = master.getSigner();
			this.signature = master.sign(this.getSignable());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean check(Key master){
		try{
			return master.verify(this.getSignable(), this.signature);
		}
		catch (Exception e){
			return false;
		}
	}

	private byte[] getSignable(){
		byte[] publicBytes = this.publicKey.getEncoded();
		byte[] timeBytes = new byte[]{
				(byte) ((this.timestamp >> 56) & 0xff),
				(byte) ((this.timestamp >> 48) & 0xff),
				(byte) ((this.timestamp >> 40) & 0xff),
				(byte) ((this.timestamp >> 32) & 0xff),
				(byte) ((this.timestamp >> 24) & 0xff),
				(byte) ((this.timestamp >> 16) & 0xff),
				(byte) ((this.timestamp >> 8) & 0xff),
				(byte) ((this.timestamp >> 0) & 0xff),
		};
		byte[] signable = new byte[publicBytes.length + timeBytes.length];
		System.arraycopy(publicBytes, 0, signable, 0, publicBytes.length);
		System.arraycopy(timeBytes, 0, signable, publicBytes.length, timeBytes.length);

		return signable;
	}

	@Override
	public JSONObject asJSON() throws JSONException {
		JSONObject json = super.asJSON();

		json.put("timestamp", this.timestamp);

		json.put("signer", this.signer);
		json.put("signature", this.signature);

		return json;
	}
}
