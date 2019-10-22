package com.ftf.phi.pages;

import android.os.Bundle;

import com.ftf.phi.R;
import com.ftf.phi.application.Page;
import com.ftf.phi.application.Phi;

public class FriendCamera extends Page {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_camera);
	}

	@Override
	public void swipeRight(){
		onBackPressed();
	}

	@Override
	public void pinchOut(){
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		Phi.getInstance().setPage(Feed.class, R.anim.none_in, R.anim.top_out);
	}
}
