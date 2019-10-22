package com.ftf.phi.pages;

import android.os.Bundle;

import com.ftf.phi.R;
import com.ftf.phi.application.Page;
import com.ftf.phi.application.Phi;

public class Message extends Page {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
	}

	@Override
	public void swipeRight(){
		onBackPressed();
	}

	@Override
	public void onBackPressed(){
		Phi.getInstance().setPage(Messages.class, R.anim.right_in, R.anim.right_out);
	}
}
