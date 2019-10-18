package com.ftf.phi.pages;

import android.os.Bundle;
import android.util.Log;

import com.ftf.phi.R;
import com.ftf.phi.TouchManager;

public class Settings extends TouchManager {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		Log.d("account", "Created Settings Activity");
	}

	@Override
	public void swipeLeft(){
		onBackPressed();
		overridePendingTransition( R.anim.left_in, R.anim.left_out);
	}
}
