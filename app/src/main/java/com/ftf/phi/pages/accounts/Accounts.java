package com.ftf.phi.pages.accounts;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ftf.phi.R;
import com.ftf.phi.account.Account;
import com.ftf.phi.account.AccountManager;
import com.ftf.phi.application.Page;
import com.ftf.phi.application.Phi;
import com.ftf.phi.pages.Message;
import com.ftf.phi.pages.Settings;

public class Accounts extends Page {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accounts);

		final AccountManager accountManager = new AccountManager();
		Account[] accounts = accountManager.getAccounts();

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		for(int i = accounts.length - 1; i > -1; i--){
			AccountLogin login = new AccountLogin();
			login.setAccount(accounts[i]);
			fragmentTransaction.add(R.id.accounts, login);
		}
		fragmentTransaction.commit();


		final Button newAccount = this.findViewById(R.id.new_account);
		newAccount.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				String account = accountManager.createAccount();
				Log.d("new account", account);
			}
		});
	}

	public void swipeRight(){
		Phi.getInstance().setPage(Settings.class, R.anim.right_in, R.anim.right_out);
	}
}
