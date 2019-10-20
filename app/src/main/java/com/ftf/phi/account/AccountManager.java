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

	private ArrayList<Account> accounts = new ArrayList();

	@Override
	public void onCreate(){
		super.onCreate();
		File[] accountDirs = new File(getBaseContext().getFilesDir(), "accounts").listFiles();
		for(int i = accountDirs.length - 1; i > -1; i--){
			accounts.add(new Account(accountDirs[i]));
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		return START_STICKY;
	}

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

	public String createAccount(){
		Account newAccount = new Account();
		accounts.add(newAccount);
		return newAccount.getID();
	}

	public void removeAccount(String id){
		for(int i = accounts.size() - 1; i > -1; i--){
			if(accounts.get(i).getID() == id){
				accounts.remove(i);
				return;
			}
		}
	}
}