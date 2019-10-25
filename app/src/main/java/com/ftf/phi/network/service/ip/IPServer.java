package com.ftf.phi.network.service.ip;

import com.ftf.phi.network.Restful;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class IPServer implements Runnable {
	private ServerSocket serverSocket;

	private boolean running = true;
	private boolean paused = false;

	private Restful api;

	public IPServer(Restful api, int port) throws IOException {
		serverSocket = new ServerSocket(port);
		this.api = api;

		running = true;
	}

	public void run(){
		//TODO: pause and resume run
		while(this.running){
			try {
				new Thread(new SocketThread(serverSocket.accept(), api));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void stop(){
		this.running = false;
	}

	void pause(){
		//TODO: pause the thread
	}

	void resume(){
		//TODO: resume the thread
	}
}

class SocketThread implements Runnable {
	Socket socket;
	Restful api;
	public SocketThread(Socket socket, Restful api){
		this.socket = socket;
		this.api = api;
	}

	public void run(){
		try {
			//Create a buffer for the message
			byte[] result = new byte[0];
			byte[] buffer = new byte[1024];

			//Get the whole message
			int received = -1;
			while((received = socket.getInputStream().read(buffer, 0, buffer.length)) > -1) {
				byte[] newResult = new byte[result.length + received];
				System.arraycopy(result, 0, newResult, 0, result.length);
				System.arraycopy(buffer, 0, newResult, result.length, received);
				result = newResult;
			}

			//Turn the data into json
			JSONObject json = new JSONObject(result.toString());

			//Run the rest api
			this.api.packet(json, socket.getOutputStream());
			//Close the socket
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}