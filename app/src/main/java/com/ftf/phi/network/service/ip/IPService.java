package com.ftf.phi.network.service.ip;

import com.ftf.phi.network.Restful;
import com.ftf.phi.network.service.Service;

import java.io.IOException;

//TODO: Encrypt the data to and from this service
public class IPService extends Service {
	private static final int PORT = 1507;

	private Thread serverThread;
	private IPServer server;
	private int port;

	public IPService(Restful api){
		this(api, PORT);
	}

	IPService(Restful api, int port) {
		super(api);
		this.port = port;

		//Create a thread for the server to run on
		try {
			this.server = new IPServer(api, port);
			this.serverThread = new Thread(this.server);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void stop() throws InterruptedException {
		this.server.stop();
		this.serverThread.join();
	}

	public void pause(){
		this.server.pause();
	}

	public void resume(){
		this.server.resume();
	}
}
