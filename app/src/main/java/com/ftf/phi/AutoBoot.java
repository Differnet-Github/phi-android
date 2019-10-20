package com.ftf.phi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ftf.phi.account.AccountManager;

public class AutoBoot extends BroadcastReceiver {
	public void onReceive(Context context, Intent arg1)
	{
		Intent intent = new Intent(context, AccountManager.class);
		context.startService(intent);
	}
}
