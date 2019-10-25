package com.ftf.phi.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class Restful {
	private Route baseRoute;

	public Restful(){
		baseRoute = new Route();
	}

	public void packet(JSONObject data, OutputStream writer) throws JSONException {
		try {
			baseRoute.packet(data.getString("uri"), data, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addMethod(String route, String method, Method callback){
		this.baseRoute.addMethod(route.substring(1), method, callback);
	}

	//Function for creating routes
	public void get(String route, Method method){
		this.addMethod(route, "get", method);
	}
	public void put(String route, Method method){
		this.addMethod(route, "put", method);
	}
	public void post(String route, Method method){
		this.addMethod(route, "post", method);
	}
	public void delete(String route, Method method){
		this.addMethod(route, "delete", method);
	}
}

class Route {

	private static boolean matchRoute(String route1, String route2){
		//TODO: fancy route formatting
		return route1.equals(route2);
	}

	private String route;
	private Route[] subRoutes;

	private Method get;
	private Method put;
	private Method post;
	private Method delete;

	Route(){
		this("");
	}

	private Route(String route){
		int end = route.indexOf("/");
		if(end != -1){
			this.subRoutes = new Route[]{ new Route(route.substring(end)) };
			this.route = route.substring(0, end);
		}
		else{
			this.route = route;
		}
	}

	void packet(String route, JSONObject data, OutputStream writer) throws IOException, JSONException {
		//Check to see if there is more to the route
		int end = route.indexOf("/");
		if(end != -1){
			//If there is more to the route:
			///Go through all subRoutes and see if any of them match the next part of the route
			String nextStep = route.substring(0, end);
			for(int i = this.subRoutes.length - 1; i > -1; i--){
				Route subRotute = this.subRoutes[i];
				//If the sub route matches the target route then use it
				if(matchRoute(subRotute.route, nextStep)){
					subRotute.packet(route.substring(end), data, writer);
					return;
				}
			}
			//If we didn't find a route write a 404 error
			writer.write("{\"stat\":404}".getBytes());
		}
		else {
			//If we are at the end of the route find the method that we want to use
			Method targetMethod = null;
			switch(data.getString("method")){
				case "get":
					targetMethod = this.get;
					break;
				case "put":
					targetMethod = this.put;
					break;
				case "post":
					targetMethod = this.post;
					break;
				case "delete":
					targetMethod = this.delete;
					break;
			}
			//If the method was not found then send a 405 error
			if(targetMethod == null){
				writer.write("{\"stat\":405}".getBytes());
			}
			//If it was found then run the target method
			else{
				try {
					targetMethod.run(data, writer);
				}
				catch(Exception e) {
					writer.write("{\"stat\":500}".getBytes());
				}
			}
		}
	}

	void addMethod(String route, String method, Method callback){
		//Check to see if there is more to the route
		int end = route.indexOf("/");
		if(end != -1){
			//If there is more to the route:
			///Go through all subRoutes and see if any of them match the next part of the route
			String nextStep = route.substring(0, end);
			for(int i = this.subRoutes.length - 1; i > -1; i--){
				Route subRotute = this.subRoutes[i];
				//If the sub route matches the target route then use it
				if(matchRoute(subRotute.route, nextStep)){
					subRotute.addMethod(route.substring(end), method, callback);
					return;
				}
			}
			//If we didn't find the route then create it and add it to the array
			Route newRoute = new Route(route);
			newRoute.addMethod(route.substring(end), method,callback);
			Route[] newSubRoutes = new Route[this.subRoutes.length + 1];
			System.arraycopy(this.subRoutes, 0, newSubRoutes, 0, this.subRoutes.length);
			newSubRoutes[this.subRoutes.length] = newRoute;
			this.subRoutes = newSubRoutes;
		}
		else {
			//If we are at the end of the route find the method that we want to use
			switch(method){
				case "get":
					this.get = callback;
					break;
				case "put":
					this.put = callback;
					break;
				case "post":
					this.post = callback;
					break;
				case "delete":
					this.delete = callback;
					break;
			}
		}
	}
}

