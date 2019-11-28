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

import java.security.NoSuchAlgorithmException;

public class Accounts extends Page {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accounts);

		AccountManager.getInstance().getAccounts((String[] accounts) -> {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			for(int i = accounts.length - 1; i > -1; i--){
				AccountLogin login = new AccountLogin();
				login.setAccount(accounts[i]);
				fragmentTransaction.add(R.id.accounts, login);
			}
			fragmentTransaction.commit();


			final Button newAccount = this.findViewById(R.id.new_account);
			newAccount.setOnClickListener(v -> {
				//TODO: pop up fragment for account creation then redirect page after creation
			});
		});
	}

	public void swipeRight(){
		Phi.getInstance().setPage(Settings.class, R.anim.right_in, R.anim.right_out);
	}
}
