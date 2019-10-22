package com.ftf.phi.application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Phi extends Application {
	private Page currentPage = null;

	private static Phi instance;
	public static Phi getInstance(){
		return instance;
	}

	public void setPage(Class newPage, int in, int out) {
		Intent newIntent= new Intent(getApplicationContext(), newPage);
		Activity oldPage = currentPage;
		oldPage.startActivity(newIntent);
		oldPage.overridePendingTransition(in, out);
	}

	public void onCreate() {
		super.onCreate();

		this.registerActivityLifecycleCallbacks(new TouchManager());
		this.registerActivityLifecycleCallbacks(new ActivePage());

		instance = this;
	}

	public Activity getCurrentActivity(){
		return currentPage;
	}

	public Page getCurrentPage(){
		return currentPage;
	}

	public void setCurrentActivity(Page currentPage){
		this.currentPage  = currentPage;
	}

	public void setCurrentActivity(Activity currentPage){
		this.currentPage  = (Page) currentPage;
	}
}

class ActivePage implements Application.ActivityLifecycleCallbacks {
	private Phi app;
	@Override
	public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
		app = (Phi) activity.getApplicationContext();
	}

	@Override
	public void onActivityStarted(@NonNull Activity activity) {
		app.setCurrentActivity(activity);
	}

	@Override
	public void onActivityResumed(@NonNull Activity activity) {
		app.setCurrentActivity(activity);
	}

	@Override
	public void onActivityPaused(@NonNull Activity activity) {
		clearReferences(activity);
	}

	@Override
	public void onActivityStopped(@NonNull Activity activity) {
		clearReferences(activity);
	}

	@Override
	public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(@NonNull Activity activity) {
		clearReferences(activity);
	}

	private void clearReferences(Activity activity){
		Activity currActivity = app.getCurrentActivity();
		if(activity.equals(currActivity)){
			app.setCurrentActivity(null);
		}
	}
}
