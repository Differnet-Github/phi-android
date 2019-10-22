package com.ftf.phi.pages;

import android.os.Bundle;

import com.ftf.phi.R;
import com.ftf.phi.application.Page;
import com.ftf.phi.application.Phi;
import com.ftf.phi.pages.accounts.Accounts;

public class Settings extends Page {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
	}

	@Override
	public void swipeLeft(){
		onBackPressed();
	}

	@Override
	public void onBackPressed(){
		Phi.getInstance().setPage(Accounts.class, R.anim.left_in, R.anim.left_out);
	}
}
