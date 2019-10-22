package com.ftf.phi.pages;

import android.os.Bundle;

import com.ftf.phi.R;
import com.ftf.phi.application.Page;
import com.ftf.phi.application.Phi;

public class FriendQR extends Page {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_qr);
	}

	@Override
	public void swipeRight(){
		onBackPressed();
	}

	@Override
	public void pinchIn(){
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		Phi.getInstance().setPage(Feed.class, R.anim.none_in, R.anim.top_out);
	}
}
