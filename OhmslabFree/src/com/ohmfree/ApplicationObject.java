package com.ohmfree;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.facebook.android.Facebook;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.util.ServiceException;
import com.ohmfree.FaceBookActivity.BaseDialogListener;
import com.ohmfree.MusicUtils.ServiceToken;
import com.ohmfree.IOhmPlaybackService;




import datamodel.classes.Album;
import datamodel.classes.MusicDataModel;
import datamodel.classes.Playlist;
import datamodel.classes.Song;
import datamodel.classes.SongList;
import datamodel.classes.YoutubeVideo;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class ApplicationObject extends Application {
	
	public MusicDataModel music_data_model;
	public MusicDatabaseCom musicDB;
	public Vibrator vibrator;
	
	Handler handler = new Handler();
	Runnable dismissProgress;
	ProgressDialog data_progress;
    IOhmPlaybackService mService = null;
    MediaPlayer media_player = null;
    List<VideoEntry> list;
    String searchquery;
    String facebook_post;
    String facebook_photo;
	
    boolean data_built = false;
	public List<Song> np_list = null;
	public List<Song> shuffled = null;
	public int np_position = 0;
	int playbackposition = 0;
	public Facebook facebook = null;
    public SharedPreferences mPrefs;
    OnFinishedListener listener;
	public boolean shuffle_on = false;
	public boolean repeat_on = false;
	
	public List<Song> edit_list = null;
	public String edit_list_name = null;
	int screen = 0;
	int last_screen = 0;
	boolean queueEditing = false;
	Cursor playlistcursor;

	boolean tokens = false;
	ServiceToken token = null;
	
	
	
	public int artistposition = 0;
	int albumposition = 0;
	int musictabposition = 0;
	int songposition = 0;
	int playlistposition = 0;
	public long current_playlist_id = -1;


	
	public static int NOW_PLAYING_SCREEN = 0;
	public static int PLAYLISTS_SCREEN = 1;
	public static int MUSIC_SCREEN = 2;
	public static int QUEUE_SCREEN = 3;
	public static int SEARCH_SCREEN = 4;
	public static int EDIT_SCREEN = 5;
	
	public void setOnFinishedListener(OnFinishedListener onlistener){
		this.listener = onlistener;
	}
	public boolean getDataState(){
		return data_built;
	}
	public void setDataState(boolean state){
		this.data_built = state;
	}
	
	Playlist selected_playlist = null;
	
	@Override
	public void onCreate() {
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		//facebook = new Facebook("170031159703872");
		if(!tokens){
			token = MusicUtils.bindToService(this, serviceConn);
			tokens = true;
		}
	}
	
public boolean setCurrentPlaylistId(long id){
		if(id<0){
			playlistcursor = musicDB.getPlaylistCursor(-1);
			playlistcursor.moveToFirst();
			current_playlist_id = playlistcursor.getLong(playlistcursor.getColumnIndex("_id"));	
			return false;
		}	
		else
		current_playlist_id = id;
		
		return true;
	}
	
	   
	   public boolean buildDataModel(){
		   
		   
		   dismissProgress = new Runnable(){

				@Override
				public void run() {
					data_progress.dismiss();
					
				}
	    		
	    	};
	    	Runnable buildDataModel = new Runnable() {
				
				@Override
				public void run() {
					
					music_data_model = new MusicDataModel();
					
					musicDB = new MusicDatabaseCom(getApplicationContext());
					
					musicDB.open();
					
					getMusicData();
					
					getPlaylistData();
					   
					music_data_model.setNowPlaying(musicDB.getNowPlaying());
					
					music_data_model.setQueue(musicDB.getQueue());
		            
					handler.post(dismissProgress);
					
					
				}
				
			
			};
			
			new Thread(buildDataModel).start();
		   
			return true;
	   }
	   
	   public boolean getPlaylistData(){
		   
			Cursor cursor = musicDB.getPlaylistCursor(0);
			    
			if(cursor!=null){
			    	
				cursor.moveToFirst();
				
				for(int r=0 ; r<cursor.getCount(); r++, cursor.moveToNext()){

				    long id = cursor.getLong(cursor.getColumnIndex("_id"));
				    
				    String name = cursor.getString(cursor.getColumnIndex("name"));
				    
				    music_data_model.addPlaylist(id, name, musicDB.getPlaylistArtworkUri(id));
				
				    Cursor membersCursor = musicDB.getPlaylistCursor(id);
				      
				    if(membersCursor!=null){
					        
				    	membersCursor.moveToFirst();
					        
					        
				    	for(int s= 0; s<membersCursor.getCount(); s++, membersCursor.moveToNext()){
					        	
				    		Song song = music_data_model.getSongHash().get(membersCursor.getLong(membersCursor.getColumnIndex("audio_id")));
					        	
					        music_data_model.getPlaylists().get(id).addSong(song);
					        
					        
				    	}
				    	
					        
				    	membersCursor.close();
				        
				    }
				         
				}

			}
			    
			cursor.close();
		  
			return true;
	   }
	  
	   
	   public boolean getMusicData() {
		
		   
		   Cursor songCursor = musicDB.getMusicCursor();
		   
		   if(songCursor!=null){
		   int songIDIndex = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
		
		   int albumIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
	
		   int albumIDIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
		
		   int artistIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
		
		   int artistIDIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
		
		   int durationIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
		
		   int isMusicIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
		
		   int trackIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);
		
		   int dataIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
		
		   int displayNameIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
		
		   int titleIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
		
		   songCursor.moveToFirst();
		
		   while (!songCursor.isAfterLast()) {
			
			   long songID = songCursor.getLong(songIDIndex);
			
			   String albumName = songCursor.getString(albumIndex);
			
			   long albumID = songCursor.getLong(albumIDIndex);
			
			   String artistName = songCursor.getString(artistIndex);
		
			   long artistID = songCursor.getLong(artistIDIndex);
			
			   long duration = songCursor.getLong(durationIndex);
			
			   int isMusic = songCursor.getInt(isMusicIndex);
			
			   int track = songCursor.getInt(trackIndex);
			
			   String data = songCursor.getString(dataIndex);
			
			   String displayName = songCursor.getString(displayNameIndex);
			
			   String title = songCursor.getString(titleIndex);
			
			   int count = 0;   
			
			   if (isMusic > 0) {
				
				   if (title == null || title == "") {
					
					   music_data_model.addSong(songID, displayName, albumName, albumID, /*albumArt,*/ artistName, artistID, duration, track, data, count);
					
					   count++;
				
				   } 
			
				   else {
					
					   music_data_model.addSong(songID, title, albumName, albumID, /*albumArt,*/ artistName, artistID, duration, track, data, count);
					
					   count++;
				
				   }
			
			   }
						
			   songCursor.moveToNext();
		
		   }
		   
		
		   songCursor.close();
		   
		   Collections.sort(music_data_model.getAlbums(), new Comparator<Object>(){
			   	 
		        public int compare(Object o1, Object o2) {
		            Album p1 = (Album) o1;
		            Album p2 = (Album) o2;
		           return p1.getName().compareToIgnoreCase(p2.getName());
		        }

		    });
		   
			Collections.sort(music_data_model.getSongs(), new Comparator<Object>(){
			   	 
		        public int compare(Object o1, Object o2) {
		            Song p1 = (Song) o1;
		            Song p2 = (Song) o2;
		           return p1.getName().compareToIgnoreCase(p2.getName());
		        }

		    });	
			
		   return true;
		   }
		return false;
	   }

	   public void savePlaylist(final long id, final Context context, final List<Song> newList){
		   Runnable buildDataModel = new Runnable() {
				
				@Override
				public void run() {
					musicDB.clearPlaylist(context, id);
					musicDB.addListToPlaylist(context, newList, id);
		            
					handler.post(dismissProgress);
					
				}
				
			
			};
			
			new Thread(buildDataModel).start();
	   }
	   
	   public void setEditList(String name, List<Song> list){
		   edit_list_name = name;
		   edit_list = list;
	   }
	   
	   public void saveQueue(final List<Song> newList){
		   Runnable buildDataModel = new Runnable() {
				
				@Override
				public void run() {
					musicDB.clearQueue();
					musicDB.addListToQueue(newList);
					music_data_model.setQueue(newList);
					handler.post(dismissProgress);
		
				}
				
			
			};
			
			new Thread(buildDataModel).start();
	   }

	   public MusicDataModel getModel() {
		
		   return music_data_model;
	
	   }
	  
	   public void setNowPlaying(List<Song> songlist, int position){
		 
		   this.np_list = songlist;
		   this.shuffle_on = false;
		   this.np_position = position;
		   
		   //this.musicDB.setNowPlaying(songlist);
		   
		   //this.musicDB.setNowPlayingSong(songlist.get(position), position);
		   
		   try {
				mService.setNowPlayingList(new SongList(songlist));
				mService.playSong(position);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
		  
		   
	   }
	   
	   boolean nextSong(){
		   try {
			mService.next();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		   if(np_position<np_list.size()-1){
				
				np_position++;
					
				return true;
			}
			else{
				
				np_position = 0;
				
				return false;
			}
			
		}
	  
	   public long createPlaylist(String name){
		   long id = musicDB.newPlaylist(name);
		   getModel().addPlaylist(id, name, null);
	
		   return id;
	   }

	   public void renamePlaylist(long id, String newname){
	
		   getModel().getPlaylists().get(id).setName(newname);
		   musicDB.renamePlaylist(id, newname);
	   }

public void addToPlaylist(Song song, long playlistid){
	music_data_model.getPlaylists().get(playlistid).addSong(song);
	musicDB.addSongToPlaylist(song.getID(), playlistid);
}


public void addToQueue(Song song){
	musicDB.addSongToQueue(song);
	getModel().getQueue().add(song);
}

public void addToQueue(List<Song> songlist){
	musicDB.addListToQueue(songlist);
	for(Song song: songlist){
		getModel().getQueue().add(song);
	}
}

public void addToPlaylist(List<Song> songlist, long playlistid){
	
	for(Song song: songlist){
		music_data_model.getPlaylists().get(playlistid).addSong(song);
	}
	
	musicDB.addListToPlaylist(getApplicationContext(), songlist, playlistid);
}

public void shufflelist(List<Song> list){

	np_list = list;
	
	shuffle_on = true;
	shuffled = null;
	shuffled = new ArrayList<Song>(list);
	
	Collections.shuffle(shuffled);
	
	try {
		mService.setNowPlayingList(new SongList(list));
		mService.setShuffleMode(1, new SongList(shuffled));
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}


ServiceConnection serviceConn = new ServiceConnection() {

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder binder) {
		mService = IOhmPlaybackService.Stub.asInterface(binder);
		
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		 mService = null;
	}

	
};





}