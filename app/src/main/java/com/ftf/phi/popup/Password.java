package com.ftf.phi.popup;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;

import com.ftf.phi.R;
import com.ftf.phi.application.Phi;

public class Password extends Fragment {
	View view;
	public Password() {
		Activity currentActivity = Phi.getInstance().getCurrentActivity();
		this.view = currentActivity.getLayoutInflater().inflate(R.layout.password, null);
	}

	public void show(View anchorView) {
		PopupWindow popup = new PopupWindow(view, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
		popup.setFocusable(true);

		popup.setBackgroundDrawable(new ColorDrawable());

		// Using location, the PopupWindow will be displayed right under anchorView
		popup.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
	}
}
