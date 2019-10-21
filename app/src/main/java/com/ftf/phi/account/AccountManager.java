package com.ftf.phi.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/* The account manager is in charge of all things that have to do with account
 * This includes:
 * 	Reading and writing files for accounts
 * 	Running the services for accounts
 * 	Getting posts associated with accounts
 * 	Getting messages associated with accounts
 * 	Locking and unlocking accounts
 */
public class AccountManager extends Service {

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private ArrayList<Account> accounts = new ArrayList();

	//When the manager is created we want to load the accounts from memory
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
	public String createAccount(){
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

	public String[] getAccounts(){
		//TODO: return account list
		return new String[0];
	}

	public Account getAccount(String id){
		//TODO: return the target account
		return null;
	}
}