package com.ftf.phi.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.json.JSONException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class AccountManager extends Service {

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Account[] accounts;

	//When the manager is created we want to load the accounts from memory
	@Override
	public void onCreate(){
		super.onCreate();
		File[] accounts = new File(getBaseContext().getFilesDir(), "accounts").listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});

		if(accounts != null){
			this.accounts = new Account[accounts.length];

			for(int i = accounts.length - 1; i > -1; i--){
				try {
					this.accounts[i] = new Account(accounts[i]);
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
		for(int i = accounts.length - 1; i > -1; i--){
			try {
				accounts[i].save();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Function to create a new account
	public String createAccount() throws Exception {
		Account[] newArr = new Account[this.accounts.length + 1];

		System.arraycopy(accounts, 0, newArr, 0, accounts.length);
		newArr[accounts.length] = new Account();
		accounts = newArr;

		return accounts[accounts.length - 1].id;
	}

	// Function to remove an account
	public void removeAccount(String id){
		for(int i = accounts.length - 1; i > -1; i--){
			if(accounts[i].id == id){
				Account[] newArr = new Account[accounts.length + 1];
				System.arraycopy(accounts, 0, newArr, 0, i);
				System.arraycopy(accounts, i, newArr, i, accounts.length + 1 - i);
				accounts = newArr;
				return;
			}
		}
	}

	public String[] getAccounts(){
		//TODO: return account list
		String[] accounts = new String[this.accounts.length];
		for(int i = this.accounts.length - 1; i > -1; i--){
			accounts[i] = this.accounts[i].id;
		}
		return accounts;
	}

	public Account getAccount(String id){
		for(int i = this.accounts.length - 1; i > -1; i--) {
			if(this.accounts[i].id = id){
				return  this.accounts[i];
			}
		}
	}
}