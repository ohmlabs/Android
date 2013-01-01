package customviews.classes;

import com.ohmfree.MusicUtils;
import com.ohmfree.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;

public class NowPlayingSwitcher extends ImageSwitcher implements OnGestureListener, OnDoubleTapListener, MusicUtils.Defs{
	
	GestureDetector gestureScanner;
	MotionEvent mEvent = null;		
	NowPlayingChangedListener listener;	
	Drawable albumart_drawable;	
	static final int SWIPE_MIN_DISTANCE = 100;	
	static final int SWIPE_THRESHOLD_VELOCITY = 100;
	
	public NowPlayingSwitcher(Context context) {
		super(context);
		gestureScanner = new GestureDetector(this);
		this.setInAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
	}
	
	public NowPlayingSwitcher(Context context, AttributeSet attributeset) {
		super(context, attributeset);
		gestureScanner = new GestureDetector(this);
	}
	
	public void setNowPlayingChangedListener(NowPlayingChangedListener l){
		listener = l;
	}

	public void setImage(Bitmap albumart, int color){
		if (albumart != null){
			albumart_drawable = new BitmapDrawable(albumart);
			setImageDrawable(albumart_drawable);
		}
		else{
			setImageResource(color);
		}
		return;	
	}
	
	
	public void setAnimation(int gesture){
		if(gesture == NEXT_SONG){
			this.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in));
	        this.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_left_out));
		}
		else if(gesture == PREVIOUS_SONG){
			this.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_right_in));
	        this.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_right_out));
		}
		else if(gesture == NOW_PLAYING_SCREEN){
			this.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
	        this.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
		}
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) 
	{
		return gestureScanner.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX,
			float velocityY) {
		if(event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			setAnimation(NEXT_SONG);
            listener.onChange(NEXT_SONG);
		} 
		else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			setAnimation(PREVIOUS_SONG);
            listener.onChange(PREVIOUS_SONG);	
		}
		
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		listener.onChange(SHOW_DIALOG);
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent arg0) {
			listener.onChange(PLAY_PAUSE);			
			return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent arg0) {
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		
			listener.onChange(SHOW_SCRUBBER);
		return true;
		
	}

}
