package com.ohmfree;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ohmfree.IOhmPlaybackService;




import datamodel.classes.Song;
import datamodel.classes.SongList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;

public class OhmPlaybackService extends Service {

	public static final String PLAY =  "com.ohmfree.music.play";
	public static final String PAUSE = "com.ohmfree.music.pause";
	public static final String NEXT =  "com.ohmfree.music.next";
	public static final String PREVIOUS = "com.ohmfree.music.previous";
	public static final String ENDSONG =  "com.ohmfree.music.endsong";
	public static final String METADATA =  "com.ohmfree.music.metadata";
	public static final String METADATA_REQUEST =  "com.ohmfree.music.metadatarequest";
	public static final String SERVICECMD = "com.ohmfree.music.musicservicecommand";
    public static final String CMDNAME = "command";
	
	private int shuffle_mode = 0;
	private int repeat_mode = 0;
	
	private int playerstate = 0;
	
	public static final int PREPARED = 1;
	
	public static final int SHUFFLE_OFF = 0;
	public static final int SHUFFLE_ON = 1;
	
	public static final int REPEAT_OFF = 0;
	public static final int REPEAT_ONE = 1;
	public static final int REPEAT_ALL = 2;
	
	public List<Song> song_list;
	public List<Song> shuffled_song_list;
 	public int position_int = 0;
	
	private MediaPlayer music_player;
	//private BroadcastReceiver mUnmountReceiver = null;
   //private AudioManager mAudioManager;
	private MediaAppWidgetProvider mAppWidgetProvider = MediaAppWidgetProvider.getInstance();
    
    @Override
    public void onCreate() {
        super.onCreate();

        song_list = new ArrayList<Song>();
        shuffled_song_list = new ArrayList<Song>();
        
        music_player = new MediaPlayer();
        //mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(SERVICECMD);
        commandFilter.addAction(PLAY);
        commandFilter.addAction(PAUSE);
        commandFilter.addAction(NEXT);
        commandFilter.addAction(PREVIOUS);
        registerReceiver(mIntentReceiver, commandFilter);
        
        
        //mAppWidgetProvider.notifyChange(this, METADATA);
    }   
    
    @Override
    public void onDestroy(){
    	removeNotification();
    	music_player.stop();
    	music_player.release();
    	music_player = null;
    }
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            if (action.equals(NEXT)){
            	next();
            	
            }
            
            else if(action.equals(PLAY) || action.equals(PAUSE)){
            	playPause();
            	mAppWidgetProvider.notifyChange(OhmPlaybackService.this, METADATA);
            }
            
            else if(action.equals(PREVIOUS)){
            	previous();
            }
            else if(action.equals(METADATA_REQUEST)){
            	sendBroadcast(METADATA);
            }
            else if (MediaAppWidgetProvider.CMDAPPWIDGETUPDATE.equals(cmd)) {
                // Someone asked us to refresh a set of specific widgets, probably
                // because they were just added.
                int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                mAppWidgetProvider.performUpdate(OhmPlaybackService.this, appWidgetIds);
            }
        }
        
    };
  
    
	boolean isPlaying(){
		return music_player.isPlaying();
	}
	
	private void endSong(){
		if(repeat_mode == 1){
			playSong(position_int);
		}
		else{
			next();
		}
	}
	private boolean next(){
		
		if(position_int< song_list.size() - 1){

			position_int++;
			playSong(position_int);
			
			return true;
		}
		else{
			
			position_int = 0;
			playSong(position_int);
			return false;
		}
		
	}
	
	boolean active(){
		if(song_list.size()>0){
			return true;
		}
		else{
			return false;
		}
	}
	private int getShuffleMode(){
		return shuffle_mode;
	}
	private int getRepeatMode(){
		return repeat_mode;
	}
	private void setRepeatMode(int mode){
		repeat_mode = mode;
	}
	private void setShuffleMode(int mode, SongList shuffled_list){
		shuffle_mode = mode;
		if(mode==1){
	
			shuffled_song_list = null;
			shuffled_song_list = new ArrayList<Song>(shuffled_list.getList());
			
			playSong(position_int);
		}
		if(mode == 0){
			playSong(position_int);
		}
		
	}
	private void setNowPlayingList(SongList songlist){
		song_list = songlist.getList();
		shuffle_mode = 0;
	}
	private List<Song> getNowPlayingList(){
		return song_list;
	}
	private int getNowPlayingPosition(){
		return position_int;
	}
	private void setNowPlayingPosition(int position){
		position_int = position;
	}
	
	
	private boolean previous(){
		if(position_int>0){
			position_int--;
			playSong(position_int);
			return true;
		}
		else{
			position_int = song_list.size() - 1;
			playSong(position_int);
			return false;
		}
	}
	
	public String getTrackName(){
		if(active()){
			if(shuffle_mode==0)
				return song_list.get(position_int).getName();
			else
				return shuffled_song_list.get(position_int).getName();
		}
		else{
			return "Nothing";
		}
	}
	public String getArtistName(){
		if(active()){
			if(shuffle_mode==0)
				return song_list.get(position_int).getArtistName();
			else
				return shuffled_song_list.get(position_int).getArtistName();
		}
		else{
			return "Playing";
		}
	}
	public void playSong(int position){
		
		   position_int= position;
		
		   if(music_player!=null){
			   
			   if(music_player.isPlaying()){
				  
				   music_player.stop();

			   }
			  
			   music_player.release();
			  
			   music_player = null;
			  
		   }
		
		   music_player = new MediaPlayer();  
		   if(shuffle_mode == 0){
		   try {
			   //FileInputStream fis = getBaseContext().openFileInput(song_list.get(position_int).getDataFile().toString());
			   
			   //music_player.setDataSource(fis.getFD());
			
			   	music_player.setDataSource(song_list.get(position_int).getDataFile());
			   	
			    music_player.setOnPreparedListener(
			            new MediaPlayer.OnPreparedListener() {
			                public void onPrepared(MediaPlayer mp) {
			                    music_player.start();
			                }
			            });

			   	
			   	music_player.prepare();
			
			
				music_player.setOnCompletionListener(new OnCompletionListener() {
					  
					public void onCompletion(MediaPlayer mp){
						endSong();
						sendBroadcast(ENDSONG);
							
						
					}
				});
		
		   } 
		   catch (IllegalArgumentException e) {
			
			   e.printStackTrace();
		
		   } 
		
		   catch (IllegalStateException e) {
			
			   e.printStackTrace();
		
		   } 
		   catch (IOException e) {
			
			   e.printStackTrace();
		
		   }
		   }
		   else{
			   try {
					
				   //FileInputStream fis = getBaseContext().openFileInput(shuffled_song_list.get(position_int).getDataFile().toString());
				   
				   //music_player.setDataSource(fis.getFD());
				
				   music_player.setDataSource(shuffled_song_list.get(position_int).getDataFile());
					music_player.prepare();
				
					music_player.start();
				
					music_player.setOnCompletionListener(new OnCompletionListener() {
						  
						public void onCompletion(MediaPlayer mp){
							next();
							sendBroadcast(ENDSONG);
								
							
						}
					});
			
			   } 
			   catch (IllegalArgumentException e) {
				
				   e.printStackTrace();
			
			   } 
			
			   catch (IllegalStateException e) {
				
				   e.printStackTrace();
			
			   } 
			   catch (IOException e) {
				
				   e.printStackTrace();
			
			   }
		   }
		   mAppWidgetProvider.notifyChange(this, METADATA);
		   showNotification();
	   }
	
	public void sendBroadcast(String action){
        Intent broadcast = new Intent();
        broadcast.setAction(action);
        sendBroadcast(broadcast);
    }
	public boolean playPause(){
		if(music_player !=null){
			if(music_player.isPlaying()){
				music_player.pause();
				removeNotification();
				return false;
			}
			else{
				music_player.start();
				showNotification();
				return true;
			}
		}
		return false;
	}
		
		protected void showNotification() {
		       
		       NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		       String tickerText;
		       String song_name;
		       String artist_name;
		       
		       PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		               new Intent(this, NowPlayingScreen.class), 0);
		       if(shuffle_mode == 0){
		    	   tickerText = song_list.get(position_int).getName();
		    	   song_name = song_list.get(position_int).getName();
		    	   artist_name = "        "+song_list.get(position_int).getArtistName();
		       }
		       else{
		    	   tickerText = shuffled_song_list.get(position_int).getName();
		    	   song_name = shuffled_song_list.get(position_int).getName();
		    	   artist_name = "        "+shuffled_song_list.get(position_int).getArtistName();
		       }
		       Notification notif = new Notification(R.drawable.icon, tickerText,
		               System.currentTimeMillis());

		       notif.setLatestEventInfo(this, song_name, artist_name, contentIntent);
		       notif.flags = Notification.FLAG_ONGOING_EVENT;
		       nm.notify(R.string.app_name, notif);
		   }
		   
		   protected void removeNotification() {

		       NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		       nm.cancel(R.string.app_name);
		   }	
		   public int duration() {
	            return music_player.getDuration();
	        }

	        public int position() {
	        	return music_player.getCurrentPosition();

	        }

	        public int seek(int whereto) {
	        	music_player.seekTo(whereto);
	            return whereto;
	        }

	        public void setVolume(float vol) {
	        	music_player.setVolume(vol, vol);
	        }	   
		
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	 static class ServiceStub extends IOhmPlaybackService.Stub {
	        WeakReference<OhmPlaybackService> mService;
	        
	        ServiceStub(OhmPlaybackService service) {
	            mService = new WeakReference<OhmPlaybackService>(service);
	        }

	        public boolean isPlaying() {
	            return mService.get().isPlaying();
	        }

	        public boolean playPause() {
	           return mService.get().playPause();
	        }
	        public void prev() {
	            mService.get().previous();
	        }
	        public void next() {
	            mService.get().next();
	        }
	        public int position() {
	            return mService.get().position();
	        }
	        public int duration() {
	            return mService.get().duration();
	        }
	        public int seek(int pos) {
	            return mService.get().seek(pos);
	        }
	        public void setShuffleMode(int shufflemode, SongList songlist) {
	            mService.get().setShuffleMode(shufflemode, songlist);
	        }
	        public int getShuffleMode() {
	            return mService.get().getShuffleMode();
	        } 
	        public void setRepeatMode(int repeatmode) {
	            mService.get().setRepeatMode(repeatmode);
	        }
	        public int getRepeatMode() {
	            return mService.get().getRepeatMode();
	        }

			@Override
			public int getNowPlayingPosition(){
				return mService.get().getNowPlayingPosition();
			}

			@Override
			public void setNowPlayingPosition(int position) {
				mService.get().setNowPlayingPosition(position);
				
			}

			@Override
			public void playSong(int position) {
				mService.get().playSong(position);
				
			}

			@Override
			public void setNowPlayingList(SongList songlist) {
				mService.get().setNowPlayingList(songlist);
				
			}

			@Override
			public SongList getNowPlayingList() {
				return new SongList(mService.get().getNowPlayingList());
			}

	    }

	    @Override
	    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
	        
	    }

	    private final IBinder mBinder = new ServiceStub(this);
}
