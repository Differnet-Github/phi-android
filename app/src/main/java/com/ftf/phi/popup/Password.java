package com.ftf.phi.popup;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;

import com.ftf.phi.R;
import com.ftf.phi.account.auth.AuthCallback;
import com.ftf.phi.application.Phi;

public class Password extends Fragment {
	View view;

	public Password(final AuthCallback callback){
		Activity currentActivity = Phi.getInstance().getCurrentActivity();
		this.view = currentActivity.getLayoutInflater().inflate(R.layout.password, null);

		final Button submitButton = this.view.findViewById(R.id.submit);
		submitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText password = view.findViewById(R.id.password);
				callback.call(password.getText().toString().getBytes());
			}
		});

		PopupWindow popup = new PopupWindow(view, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
		popup.setFocusable(true);

		popup.setBackgroundDrawable(new ColorDrawable());

		// Using location, the PopupWindow will be displayed right under anchorView
		popup.showAtLocation(this.view, Gravity.CENTER, 0, 0);
	}
}
