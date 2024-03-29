package com.ftf.phi.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TouchManager implements Application.ActivityLifecycleCallbacks {

	private final double SWIPE_LENGTH = 200;
	private final double SWIPE_ANGLE_MIN = 1;
	private final double SWIPE_ANGLE_MAX = 2;

	private final double PINCH_ANGLE = 2.5;

	private ArrayList<Touch> touches = new ArrayList<>();

	//We are storing a touch listener here so we aren't instantiating it for every activity
	private View.OnTouchListener touchListener;

	TouchManager(){
		touchListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event){
				int index = event.getActionIndex();
				int id = event.getPointerId(index);
				int action = event.getActionMasked() & MotionEvent.ACTION_MASK;

				while(touches.size() - 1 < id){
					touches.add(new Touch());
				}
				for(int i = touches.size() - 1; i > -1; i--){
					try{
						touches.get(i).update(event, i);
					}
					catch(Exception err){
						//We get an exception if the id is no longer defined
						touches.remove(i);
					}
				}

				//Get the Touches that we will be using
				Touch primary = touches.get(0);
				Touch secondary;
				try{
					secondary = touches.get(1);
				}
				catch(Exception e){
					secondary = new Touch();
					touches.add(secondary);
				}

				//Calculate swipe
				double distance = primary.distance.abs();
				double angle = Math.atan2(primary.start.x - primary.end.x, primary.start.y - primary.end.y);

				double pinchAngle = Math.acos((primary.distance.x * secondary.distance.x + primary.distance.y * secondary.distance.y)/(distance * secondary.distance.abs()));

				boolean pinchDirection = new Vector(primary.start.x - secondary.start.x, primary.start.y - secondary.start.y).abs() > new Vector(primary.end.x - secondary.end.x, primary.end.y - secondary.end.y).abs();

				boolean end = false;

				switch(action){
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
						end = true;
				}

				if(end){
					Page activePage = Phi.getInstance().getCurrentPage();
					if(distance > SWIPE_LENGTH){
						if(angle > SWIPE_ANGLE_MIN && angle < SWIPE_ANGLE_MAX){
							activePage.swipeLeft();
						}
						else if(-angle > SWIPE_ANGLE_MIN && -angle < SWIPE_ANGLE_MAX){
							activePage.swipeRight();
						}
					}
					if(primary.down ^ secondary.down){
						if(pinchAngle > PINCH_ANGLE){
							if(pinchDirection){
								activePage.pinchIn();
							}
							else {
								activePage.pinchOut();
							}
						}
					}
				}
				v.performClick();
				return true;
			}
		};
	}

	@Override
	public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
		View contentView = activity.findViewById(android.R.id.content);
		contentView.setOnTouchListener(touchListener);
	}

	@Override
	public void onActivityStarted(@NonNull Activity activity) {

	}

	@Override
	public void onActivityResumed(@NonNull Activity activity) {

	}

	@Override
	public void onActivityPaused(@NonNull Activity activity) {

	}

	@Override
	public void onActivityStopped(@NonNull Activity activity) {

	}

	@Override
	public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(@NonNull Activity activity) {

	}

	private class Touch {

		private boolean down;

		private Vector start;
		private Vector end;
		private Vector distance;

		private Touch(){
			down = false;

			start = new Vector();
			end = new Vector();
			distance = new Vector();
		}

		private void update(MotionEvent event, int id){
			int action = event.getActionMasked() & MotionEvent.ACTION_MASK;
			int index = event.findPointerIndex(id);

			float x = event.getX(index);
			float y = event.getY(index);

			switch (action){
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					//Update touch down
					this.down = true;
					//Update Start cord
					this.start = new Vector(x, y);

					//Reset all points that are not the start
					this.distance = new Vector();
					this.end = this.start;
					break;
				case MotionEvent.ACTION_MOVE:
					//Update the distance traveled
					this.distance = new Vector(this.start.x - x, this.start.y - y);
					//Update end point
					this.end = new Vector(x, y);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					if(id == event.getPointerId(event.getActionIndex())){
						//Update touch down
						this.down = false;
					}
					//Update end point
					this.end = new Vector(x, y);
					break;
			}
		}
	}
}

class Vector {
	double x;
	double y;

	Vector(){
		this.x = 0;
		this.y = 0;
	}

	Vector(double x, double y){
		this.x = x;
		this.y = y;
	}

	double abs(){
		return Math.pow(Math.pow(this.x, 2) + Math.pow(this.y, 2), 0.5);
	}
}