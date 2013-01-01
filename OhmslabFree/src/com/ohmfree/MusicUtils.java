package com.ohmfree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import com.ohmfree.IOhmPlaybackService;







import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class MusicUtils {
	
	public static interface Defs{
		
		static int SONG = 0;
		static int ALBUM = 1;
		static int ARTIST = 2;
		
		static int NEXT_SONG = 0;
		static int PREVIOUS_SONG = 1;
		static int PLAY_PAUSE = 2;
		static int SHOW_SCRUBBER = 3;
		static int SHOW_DIALOG = 4;
		
		static int PLAYLISTS_DIALOG = 4;
		static int ACTION_DIALOG = 5;
		static int NEW_PLAYLIST_DIALOG = 6;
		static int RENAME_PLAYLIST_DIALOG = 7;
		static int CLEAR_LIST_CONFIRM = 8;
		static int DELETE_PLAYLIST_CONFIRM = 9;
		
		static int NOW_PLAYING_SCREEN = 10;
		static int PLAYLISTS_SCREEN = 11;
		static int MUSIC_SCREEN = 12;
		static int SEARCH_SCREEN = 13;
		static int QUEUE_SCREEN = 14;
		static int NO_PLAYLISTS_SCREEN = 15;
		static int EDIT_LIST_SCREEN = 16;
	}
	public static IOhmPlaybackService sService = null;
	
	public static Random randgen = new Random();
	public static int[] colors = new int[]{
			R.drawable.blue,
			R.drawable.burnt,
			R.drawable.green,
			R.drawable.herb,
			R.drawable.purp,
			R.drawable.red,
			R.drawable.yellow};
	public static int[] colors1 = new int[]{
		R.color.color1,
		R.color.color2,
		R.color.color3,
		R.color.color4,
		R.color.color5,
		R.color.color6,
		R.color.color7};
	 

	static class ServiceBinder implements ServiceConnection {
        ServiceConnection mCallback;
        ServiceBinder(ServiceConnection callback) {
            mCallback = callback;
        }
        
        public void onServiceConnected(ComponentName className, android.os.IBinder service) {
            sService = IOhmPlaybackService.Stub.asInterface(service);
            
            if (mCallback != null) {
                mCallback.onServiceConnected(className, service);
            }
        }
        
        public void onServiceDisconnected(ComponentName className) {
            if (mCallback != null) {
                mCallback.onServiceDisconnected(className);
            }
            sService = null;
        }
    }
	
	public static class ServiceToken {
        ContextWrapper mWrappedContext;
        ServiceBinder mBinder;
        ServiceToken(ContextWrapper context, ServiceBinder binder) {
            mWrappedContext = context;
            mBinder = binder;
        }
    }
	
	public static ServiceToken bindToService(Context context, ServiceConnection callback) {
        
        ContextWrapper cw = new ContextWrapper(context);
        cw.startService(new Intent(cw, OhmPlaybackService.class));
        ServiceBinder sb = new ServiceBinder(callback);
        cw.bindService((new Intent()).setClass(cw, OhmPlaybackService.class), sb, 0);
        
        return new ServiceToken(cw,sb);
        
    }
	
	public static ServiceToken bindToService(Activity context, ServiceConnection callback) {
        Activity realActivity = context.getParent();
        if (realActivity == null) {
            realActivity = context;
        }
        ContextWrapper cw = new ContextWrapper(realActivity);
        cw.startService(new Intent(cw, OhmPlaybackService.class));
        ServiceBinder sb = new ServiceBinder(callback);
        cw.bindService((new Intent()).setClass(cw, OhmPlaybackService.class), sb, 0);
        
        return new ServiceToken(cw,sb);
        
    }
	
	 public static void unbindFromService(ServiceToken token) {
	       
	        token.mWrappedContext.unbindService(token.mBinder);
	        
	    }

	public static Bitmap getArtwork(Context context, Uri artworkuri) {
	    
	    ContentResolver res = context.getContentResolver();
	    
	    if (artworkuri != null) {
	        ParcelFileDescriptor fd = null;
	        try {
	            fd = res.openFileDescriptor(artworkuri, "r");
	            
	            Bitmap b = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
	            
	            
	            return b;
	        } catch (FileNotFoundException e) {
	        } finally {
	            try {
	                if (fd != null)
	                    fd.close();
	            } catch (IOException e) {
	            }
	        }
	    }
	    return null;
	}

	public static String formatmyTime(int millis) 
	{
	    String output = "00:00";
		
		boolean minFlag = false;
		int seconds = millis / 1000;
		
		int minutes = seconds / 60;
		int hour=0;
		if((minutes/60) >=1){
			minFlag = true;
			hour = (int) minutes/60;
		}
		
		seconds = seconds % 60;
		minutes = minutes % 60;

		String secondsD = String.valueOf(seconds);
		String minutesD = String.valueOf(minutes);

		if (seconds < 10)
			secondsD = "0" + seconds;
		if (minutes < 10)
			minutesD = "0" + minutes;

		if(minFlag){
			output = hour+":"+minutesD + ":" + secondsD ;
		}
		else{
			output = minutesD + ":" + secondsD ;
		}
		return output;
	}

}
