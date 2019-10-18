package com.ftf.phi;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

public class TouchManager extends Activity {

	private final double SWIPE_LENGTH = 200;
	private final double SWIPE_ANGLE_MIN = 1;
	private final double SWIPE_ANGLE_MAX = 2;

	private final double PINCH_ANGLE = 2.5;

	ArrayList<Touch> touches;

	public TouchManager(){
		touches = new ArrayList();
	}

	private Touch get(int i){
		if(i >= touches.size() - 1){
			touches.add(i, new Touch());
		}
		return touches.get(i);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		int index = event.getActionIndex();
		int id = event.getPointerId(index);
		int action = event.getActionMasked() & MotionEvent.ACTION_MASK;

		this.get(id);
		for(int i = touches.size() - 1; i >= -1; i--){
			try{
				this.get(i).update(event, i);
			}
			catch(Exception err){

			}
		}

		//Get the Touches that we will be using
		Touch primary = this.get(0);
		Touch secondary = this.get(1);

		//Calculate swipe
		double distance = primary.distance.abs();
		double angle = Math.atan2(primary.start.x - primary.end.x, primary.start.y - primary.end.y);

//		Log.d("Numbers", "a1: " + primary.distance.x + "\tb1: " + secondary.distance.x + "\ta2: " + primary.distance.y + "\tb2: " + secondary.distance.y + "\tdist: " + distance);

		double pinchAngle = Math.acos((primary.distance.x * secondary.distance.x + primary.distance.y * secondary.distance.y)/(distance * secondary.distance.abs()));

		boolean pinchDirection = new Vector(primary.start.x - secondary.start.x, primary.start.y - secondary.start.y).abs() > new Vector(primary.end.x - secondary.end.x, primary.end.y - secondary.end.y).abs();

		boolean end = false;

		switch(action){
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				end = true;
		}
		this._swipe(distance, angle, end);
		if(primary.down ^ secondary.down){
			this._pinch(pinchAngle, pinchDirection, end);
		}

		return false;
	}

	public void swipeLeft(){};
	public void swipeRight(){};
	public void swipe(double length, double direction, boolean end){};

	private void _swipe(double length, double direction, boolean end) {
		if(length > SWIPE_LENGTH && end){
			if(direction > SWIPE_ANGLE_MIN && direction < SWIPE_ANGLE_MAX){
				this.swipeLeft();
			}
			else if(-direction > SWIPE_ANGLE_MIN && -direction < SWIPE_ANGLE_MAX){
				this.swipeRight();
			}
		}
		this.swipe(length, direction, end);
	}

	public void pinchIn(){};
	public void pinchOut(){};
	public void pinch(double delta, boolean direction, boolean end){};

	private void _pinch(double delta, boolean direction, boolean end) {
		if(end){
			if(delta > PINCH_ANGLE){
				if(direction){
					this.pinchIn();
					Log.d("action", "pinch in");
				}
				else {
					this.pinchOut();
					Log.d("action", "pinch out");
				}
			}
		}
		this.pinch(delta, direction, end);
	}

	class Touch {

		public boolean down;

		public Vector start;
		public Vector end;
		public Vector displacement;
		public Vector distance;

		public Touch(){
			down = false;

			start = new Vector();
			end = new Vector();
			displacement = new Vector();
			distance = new Vector();
		}

		protected void update(MotionEvent event, int id){
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
					this.displacement = new Vector();
					this.distance = new Vector();
					this.end = this.start;
					break;
				case MotionEvent.ACTION_MOVE:
					//Update Displacement from last time
					this.displacement = new Vector(distance.x - event.getX(), distance.y - event.getY());
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
	public double x;
	public double y;

	public Vector(){
		this.x = 0;
		this.y = 0;
	}

	public Vector(double x, double y){
		this.x = x;
		this.y = y;
	}

	public double abs(){
		return Math.pow(Math.pow(this.x, 2) + Math.pow(this.y, 2), 0.5);
	}
}