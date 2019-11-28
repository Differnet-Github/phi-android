package com.ftf.phi.account;

import com.ftf.phi.account.Files.Savable;
import com.ftf.phi.network.Restful;
import com.ftf.phi.network.service.Service;

import org.json.JSONObject;

public class Network implements Savable {

	private Restful api;
	private Service[] services;

	public Network(){
		//TODO: create new network manager
	}

	public Network(String json) {
		//TODO: load network manager from file data
	}

	public String export(){
		return "";
		//TODO: Export network
	}

	//TODO: network methods
}
