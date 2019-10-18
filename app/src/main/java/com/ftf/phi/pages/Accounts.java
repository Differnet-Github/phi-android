package com.ftf.phi.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ftf.phi.R;
import com.ftf.phi.TouchManager;

public class Accounts extends TouchManager {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accounts);

		final Button button1 = this.findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent myIntent = new Intent(getApplicationContext(), Feed.class);
				startActivity(myIntent);
				overridePendingTransition( R.anim.left_in, R.anim.left_out);
			}
		});

		final Button button2 = this.findViewById(R.id.button2);
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(), Feed.class);
				startActivity(myIntent);
				overridePendingTransition( R.anim.left_in, R.anim.left_out);
			}
		});
	}

	@Override
	public void swipeRight(){
		Intent myIntent = new Intent(getApplicationContext(), Settings.class);
		startActivity(myIntent);
		overridePendingTransition( R.anim.right_in, R.anim.right_out);
	}
}
