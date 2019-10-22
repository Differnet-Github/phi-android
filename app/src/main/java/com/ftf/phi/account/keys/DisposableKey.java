package com.ftf.phi.account.keys;

import android.util.Log;

import com.ftf.phi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class DisposableKey extends Keys {
	private long timestamp;
	private byte[] signature;

	public DisposableKey(MasterKey master){
		genKeys(R.integer.defualt_disposablekey_size);
		this.setMaster(master);
	}

	public DisposableKey(MasterKey master, int keySize){
		genKeys(keySize);
		this.setMaster(master);
	}

	//TODO: better exception name
	public DisposableKey(MasterKey master, JSONObject disposableKey) throws Exception {
		this.encrypted = disposableKey.getBoolean("encrypted");

		this.importPublic(disposableKey.getString("public").getBytes());
		this.importPrivate(disposableKey.getString("private").getBytes());

		if(!this.check(master)){
			throw new Exception();
		};
	}

	private byte[] getSignable(){
		byte[] publicBytes = this.exportPublic();
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

		//TODO: do we need to sign the private key
		byte[] signable = new byte[publicBytes.length + timeBytes.length];
		System.arraycopy(publicBytes, 0, signable, 0, publicBytes.length);
		System.arraycopy(timeBytes, 0, signable, publicBytes.length, timeBytes.length);

		return signable;
	}

	public boolean check(MasterKey master){
		try{
			return master.verify(this.getSignable(), this.signature);
		}
		catch (Exception e){
			return false;
		}
	}

	private void setMaster(MasterKey master) {
		try{
			this.timestamp = new Date().getTime();

			this.signature = master.sign(this.getSignable());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONObject asJSON() throws JSONException {
		JSONObject data = new JSONObject();
		//TODO: check if this should be in PEM
		data.put("public", this.exportPublic().toString());
		data.put("private", this.exportPrivate().toString());
		data.put("encrypted", this.encrypted);
		data.put("timestamp", this.timestamp);
		data.put("signature", this.signature.toString());
		return data;
	}
}
