package com.ftf.phi.pages.accounts;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ftf.phi.R;
import com.ftf.phi.account.Account;

public class AccountLogin extends Fragment {
	Account account;

	public void setAccount(Account account){
		this.account = account;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.account_login, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
