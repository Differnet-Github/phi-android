package com.ftf.phi.pages;

import android.os.Bundle;

import com.ftf.phi.R;
import com.ftf.phi.TouchManager;

public class Message extends TouchManager {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
	}

	@Override
	public void swipeRight(){
		onBackPressed();
		overridePendingTransition( R.anim.right_in, R.anim.right_out);
	}
}