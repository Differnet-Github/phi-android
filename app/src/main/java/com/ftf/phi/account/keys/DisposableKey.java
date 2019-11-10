package com.ftf.phi.account.keys;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

/* DisposableKeys are just like Master keys but they are signed */
public class DisposableKey extends Key implements Savable {
	private long timestamp;
	private byte[] signature;

	private String signer;

	// Create a DisposableKey from a master
	public DisposableKey(Key master) throws NoSuchAlgorithmException {
		super();
		this.setMaster(master);
	}

	// Import a DisposableKey
	public DisposableKey(Key master, JSONObject fileData) throws JSONException {
		super(fileData);
		//TODO: import signature
		//TODO: check signature
	}

	// Set the master key for this key
	private void setMaster(Key master){
		try{
			// Update the time
			this.timestamp = new Date().getTime();

			// Sign it
			this.signer = master.getSigner();
			this.signature = master.sign(this.getSignable());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Check the signature on this key
	private boolean check(Key master){
		try{
			return master.verify(this.getSignable(), this.signature);
		}
		catch (Exception e){
			return false;
		}
	}

	// Get the key in a signable format
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

	// Export the DisposableKey as a string
	@Override
	public String export() throws JSONException {
		JSONObject json = super.asJSON();

		json.put("timestamp", this.timestamp);

		json.put("signer", this.signer);
		json.put("signature", this.signature);

		return json.toString();
	}
}
