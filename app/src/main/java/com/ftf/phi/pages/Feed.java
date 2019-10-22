package com.ftf.phi.pages;

import android.os.Bundle;

import com.ftf.phi.R;
import com.ftf.phi.application.Page;
import com.ftf.phi.application.Phi;
import com.ftf.phi.pages.accounts.Accounts;

public class Feed extends Page {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed);
	}

	@Override
	public void swipeRight(){
		this.onBackPressed();
	}

	public void swipeLeft(){
		Phi.getInstance().setPage(Messages.class, R.anim.left_in, R.anim.left_out);
	}

	@Override
	public void pinchIn(){
		Phi.getInstance().setPage(FriendCamera.class, R.anim.top_in, R.anim.none_out);
	}

	@Override
	public void pinchOut(){
		Phi.getInstance().setPage(FriendQR.class, R.anim.top_in, R.anim.none_out);
	}

	@Override
	public void onBackPressed(){
		Phi.getInstance().setPage(Accounts.class, R.anim.right_in, R.anim.right_out);
	}
}
