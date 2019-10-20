package com.ftf.phi.account.network;

import com.ftf.phi.account.keys.DisposableKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Network {

	ArrayList<Service> services = new ArrayList();
	DisposableKey disposable;

	public Network(DisposableKey disposable){
		this.disposable = disposable;
	}

	public Network(DisposableKey disposable, JSONArray nets) throws JSONException {
		this.disposable = disposable;
		for(int i = nets.length() - 1; i > -1; i--){
			JSONObject service = nets.getJSONObject(i);
			switch(service.getString("type")){
				case "ip":
					services.add(new IPService(this.disposable));
					break;
				case "tor":
					services.add(new TorService(this.disposable, service));
					break;
				case "i2p":
					services.add(new I2PService(this.disposable, service));
					break;
			}
		}
	}

	public void addService(Service service){
		services.add(new IPService(this.disposable));
	}

	public void updateDisposable(DisposableKey disposable){
		this.disposable = disposable;
		for(int i = services.size() - 1; i > -1; i--){
			services.get(i).updateDisposable(disposable);
		}
	}

	public void start(){
		for(int i = services.size() - 1; i > -1; i--){
			services.get(i).start();
		}
	}

	public void stop(){
		for(int i = services.size() - 1; i > -1; i--){
			services.get(i).stop();
		}
	}

	public void start(int id){
		//TODO: Start hosting network with id
	}

	public void stop(int id){
		//TODO: Stop hosting network with id
	}

	public JSONArray asJSON() throws JSONException {
		JSONArray data = new JSONArray();
		for(int i = this.services.size() - 1; i > -1; i--){
			data.put(i, this.services.get(i).asJSON());
		}
		return data;
	}

	public JSONArray asSendableJSON() throws JSONException {
		JSONArray data = new JSONArray();
		for(int i = this.services.size() - 1; i > -1; i--){
			data.put(i, this.services.get(i).asSendableJSON());
		}
		return data;
	}
}
