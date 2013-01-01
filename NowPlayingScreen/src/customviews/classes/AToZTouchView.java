package customviews.classes;






import com.ohmslab.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.widget.LinearLayout;

public class AToZTouchView extends LinearLayout implements OnGestureListener { 

	Context context;
	private GestureDetector gestureScanner;
	float thumb_position = 0;
	final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.az_redbar);
	private ArtistFastScrolledListener listener;
	
	public AToZTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		gestureScanner = new GestureDetector(this);
	}

	public AToZTouchView(Context context) {
		super(context);
		this.context = context;
		gestureScanner = new GestureDetector(this);
	}

	@Override
    public boolean onTouchEvent(MotionEvent event){
		//setThumbPosition(event.getX());
		//listener.onScroll(event.getRawX());
		return gestureScanner.onTouchEvent(event);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.dispatchDraw(canvas);
		canvas.drawBitmap(bitmap,thumb_position ,5,null);	
	}

    @Override
	public boolean onDown(MotionEvent e){
    	return true;
    }
	
    @Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
    	return true;
    }
	
    @Override
	public void onLongPress(MotionEvent e){
    	return;
    }
	
    @Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
    	listener.onScroll(e2.getRawX());
    	setThumbPosition(e2.getRawX());
    	return true;
    }
	
    @Override
	public void onShowPress(MotionEvent e){
    	return;
    } 
	
    @Override
	public boolean onSingleTapUp(MotionEvent event) {
    	listener.onSelect(event.getRawX());	
    	setThumbPosition(event.getRawX());
    	return true;
    }
 
    public void setThumbPosition(float position){
    	thumb_position = position;
    	invalidate();
    }
    
    public void setArtistFastScrolledListener(ArtistFastScrolledListener artistFastScrolledListener){
    	listener = artistFastScrolledListener;
    }
	

	
}


