package com.ftf.phi.account;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;

public class AccountManager {

	private static AccountManager instance;

	public static AccountManager getInstance(){
		if(instance == null){
			instance = new AccountManager();
		}
		return instance;
	}

	private Account[] accounts;

	private AccountManager(){
		this.load();
	}

	// Load accounts from memory async
	private void load(){
		this.load(null);
	}
	private void load(Runnable callback){
		// TODO: multi thread this
		File[] accounts = new File("phi", "accounts").listFiles(new FileFilter() {
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
		if(callback != null){
			callback.run();
		}
	}

	// Create a new account
	public void create(Consumer<String> callback){
		Runnable loader = () -> {
			Account[] newArr = new Account[this.accounts.length + 1];

			System.arraycopy(accounts, 0, newArr, 0, accounts.length);
			try {
				newArr[accounts.length] = new Account();
				accounts = newArr;
				accounts[accounts.length - 1].getID(callback);
			} catch (Exception e) {
				e.printStackTrace();
				callback.accept(null);
			}
		};

		if(accounts == null){
			load(loader);
		}
		else{
			loader.run();
		}
	}

	public void delete(String id){
		delete(id, null);
	}

	// Delete an account
	public void delete(String id, Runnable callback){
		for(int i = accounts.length - 1; i > -1; i--){
			int j = i;
			accounts[i].getID((String acccountID) -> {
				if(acccountID.equals(id)){
					Account[] newArr = new Account[accounts.length + 1];
					System.arraycopy(accounts, 0, newArr, 0, j);
					System.arraycopy(accounts, j, newArr, j, accounts.length + 1 - j);
					accounts = newArr;
					if(callback != null){
						callback.run();
					}
				}
			});
		}
	}

	public void getAccounts(Consumer<String[]> callback) {
		String[] accounts = new String[this.accounts.length];
		for(int i = this.accounts.length - 1; i > -1; i--){
			int j = i;
			this.accounts[i].getID((String id) -> {
				accounts[j] = id;
			});
		}
		callback.accept(accounts);
	}

	public void getAccount(String id, Consumer<Account> callback) {
		for(int i = this.accounts.length - 1; i > -1; i--) {
			int j = i;
			this.accounts[i].getID((String accountID) -> {
				if(accountID.equals(id)){
					callback.accept(this.accounts[j]);
				}
			});
		}
	}
}