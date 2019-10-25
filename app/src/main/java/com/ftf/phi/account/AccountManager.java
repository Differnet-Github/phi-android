package com.ftf.phi.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AccountManager extends Service {

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private ArrayList<Account> accounts = new ArrayList<>();

	//When the manager is created we want to load the accounts from memory
	@Override
	public void onCreate(){
		super.onCreate();
		File[] accountDirs = new File(getBaseContext().getFilesDir(), "accounts").listFiles();
		if(accountDirs != null){
			for(int i = accountDirs.length - 1; i > -1; i--){
				try {
					accounts.add(new Account(accountDirs[i]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		return START_STICKY;
	}

	//When the manager is destroyed we want to save all accounts
	@Override
	public void onDestroy(){
		for(int i = accounts.size() - 1; i > -1; i--){
			try {
				accounts.get(i).save();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Function to create a new account
	public String createAccount() throws Exception {
		Account newAccount = new Account();
		accounts.add(newAccount);
		return newAccount.getID();
	}

	// Function to remove an account
	public void removeAccount(String id){
		for(int i = accounts.size() - 1; i > -1; i--){
			if(accounts.get(i).getID() == id){
				accounts.remove(i);
				return;
			}
		}
	}

	public Account[] getAccounts(){
		//TODO: return account list
		Account[] accounts = new Account[this.accounts.size()];
		for(int i = this.accounts.size() - 1; i > -1; i--){
			accounts[i] = this.accounts.get(i);
		}
		return accounts;
	}

	public Account getAccount(String id){
		//TODO: return the target account
		return null;
	}
}