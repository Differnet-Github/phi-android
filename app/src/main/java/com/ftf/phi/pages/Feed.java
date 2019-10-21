package com.ftf.phi.pages;

import android.content.Intent;
import android.os.Bundle;

import com.ftf.phi.R;
import com.ftf.phi.application.Page;

public class Feed extends Page {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed);
	}

	@Override
	public void swipeRight(){
		onBackPressed();
		overridePendingTransition( R.anim.right_in, R.anim.right_out);
	}

	public void swipeLeft(){
		Intent myIntent = new Intent(getApplicationContext(), Messages.class);
		startActivity(myIntent);
		overridePendingTransition( R.anim.left_in, R.anim.left_out);
	}

	@Override
	public void pinchIn(){
		Intent myIntent = new Intent(getApplicationContext(), FriendCamera.class);
		startActivity(myIntent);
		overridePendingTransition( R.anim.top_in, R.anim.none_out);
	}

	@Override
	public void pinchOut(){
		Intent myIntent = new Intent(getApplicationContext(), FriendQR.class);
		startActivity(myIntent);
		overridePendingTransition( R.anim.top_in, R.anim.none_out);
	}
}
