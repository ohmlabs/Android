package com.ohmslab;




import java.util.ArrayList;
import java.util.Collections;

import customviews.classes.ActionDialog;
import customviews.classes.AddToPlaylistsDialog;
import customviews.classes.NPSongDialogListener;
import customviews.classes.NowPlayingChangedListener;
import customviews.classes.NowPlayingSwitcher;
import datacontrollers.classes.PlaylistDialogAdapter;
import datamodel.classes.Song;
import datamodel.classes.SongList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewSwitcher.ViewFactory;
	
public class NowPlayingScreen extends Activity implements ViewFactory, MusicUtils.Defs{
    
	ApplicationObject ohm = null;
	RelativeLayout playbackcontrols = null;
	MusicReceiver np_updater;
	PlaylistDialogAdapter mAdapter;
	Bitmap albumart = null;
	Counter counter;
	boolean receiver = false;
	ImageView repeatImage;
	ImageView shuffleImage;
	TextView np_song;
	TextView np_artist;
	SeekBar progressbar;
	NowPlayingSwitcher np_switcher;
	EditText playlistname;
  
	@Override
	public void onStop(){
		super.onStop();
		
		if(receiver){
			receiver = false;
			this.unregisterReceiver(np_updater);
		}
		
		counter.stopStart();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		np_switcher.setAnimation(NOW_PLAYING_SCREEN);
		if(!receiver){
			registerMusicReceiver();
			receiver = true;
		}
		
		
		if(!ohm.getDataState()){
    		ohm.data_progress = ProgressDialog.show(NowPlayingScreen.this, "Working..", "Building Music Library", true);
    		ohm.setDataState(ohm.buildDataModel());	
    	}
		if(ohm.np_list !=null){
			try {
				ohm.np_position = ohm.mService.getNowPlayingPosition();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		updateUI();	
    	}
    	else{
    		showEmpty();
    	}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	ohm = (ApplicationObject) getApplicationContext();
    	setContentView(R.layout.now_playing_screen); 
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    	
    	if(!ohm.getDataState()){
    		ohm.data_progress = ProgressDialog.show(NowPlayingScreen.this, "Working..", "Building Music Library", true);
    		ohm.setDataState(ohm.buildDataModel());	
    	}
    	
    	buildUI();
    	
    	if(ohm.np_list !=null){
    		try {
				ohm.np_position = ohm.mService.getNowPlayingPosition();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		updateUI();	
    	}
    	else{
    		showEmpty();
    	}
	
	}
	
	private void showEmpty(){
		np_switcher.setImageResource(R.drawable.home_instructions);
		np_song.setText("Ohm Music");
		np_artist.setText("Welcome To");	
	}
	

	private void NPSongDialog(){
		if(ohm.shuffle_on){
			ActionDialog npDialog = new ActionDialog(NowPlayingScreen.this, ohm.shuffled.get(ohm.np_position));
		
			npDialog.setNPSongDialogListener(new NPSongDialogListener(){

			@Override
			public void onGoToMusicClick(Song song) {
				ohm.artistposition = ohm.getModel().getArtistNames().indexOf(song.getArtistName());
				goToMusic();
			}

			@Override
			public void onAddToPlaylistClick(Song song) {
				addToPlaylistDialog(song);
				
				
			}

			@Override
			public void onAddToQueueClick(Song song) {
				ohm.addToQueue(song);
				Toast.makeText(getApplicationContext(), song.getName() + " Added to Queue", Toast.LENGTH_SHORT).show();
				
				
			}
			
			@Override
			public void onShareClick(Song song) {
				goToLoginScreen();
				
				
			}
			
		});
		npDialog.show();
		ohm.vibrator.vibrate(20);
		}
		else{
			ActionDialog npDialog = new ActionDialog(NowPlayingScreen.this, ohm.np_list.get(ohm.np_position));
			
			npDialog.setNPSongDialogListener(new NPSongDialogListener(){

			@Override
			public void onGoToMusicClick(Song song) {
				ohm.artistposition = ohm.getModel().getArtistNames().indexOf(song.getArtistName());
				goToMusic();
			}

			@Override
			public void onAddToPlaylistClick(Song song) {
				addToPlaylistDialog(song);
				
				
			}

			@Override
			public void onAddToQueueClick(Song song) {
				ohm.addToQueue(song);
				Toast.makeText(getApplicationContext(), song.getName() + " Added to Queue", Toast.LENGTH_SHORT).show();
				
				
			}
			
			@Override
			public void onShareClick(Song song) {
				goToLoginScreen();
				
				
			}
			
		});
		npDialog.show();
		ohm.vibrator.vibrate(20);
		}
	}
	
	private void updateUI(){
		if(ohm.shuffle_on){
			shuffleImage.setAlpha(255);
			albumart = MusicUtils.getArtwork(getApplicationContext(), ohm.shuffled.get(ohm.np_position).getArtworkUri());
			np_song.setText(ohm.shuffled.get(ohm.np_position).getName());
	    	np_artist.setText(ohm.shuffled.get(ohm.np_position).getArtistName());
	    	if(albumart != null){
	   	        np_switcher.setImage(albumart, 0);
	   	    }
	   	    else{
	   	        np_switcher.setImage(null, MusicUtils.colors[(int) (ohm.shuffled.get(ohm.np_position).getID()%7)]);
	   	    }
		}
		else{
			shuffleImage.setAlpha(100);	
		
		albumart = MusicUtils.getArtwork(getApplicationContext(), ohm.np_list.get(ohm.np_position).getArtworkUri());
   	    if(albumart != null){
   	        np_switcher.setImage(albumart, 0);
   	    }
   	    else{
   	        np_switcher.setImage(null, MusicUtils.colors[(int) (ohm.np_list.get(ohm.np_position).getID()%7)]);
   	    }
   		
		np_song.setText(ohm.np_list.get(ohm.np_position).getName());
    	np_artist.setText(ohm.np_list.get(ohm.np_position).getArtistName());
		}
    	try {
		int	duration = ohm.mService.duration();
		progressbar.setMax(duration);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		 
		   
		try {
			progressbar.setProgress((int)ohm.mService.position());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		try {
			counter = null;
			counter = new Counter(ohm.mService.duration(), 1000);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		counter.start();
    	
	}

	private void addToPlaylistDialog(final Song song){
		final AddToPlaylistsDialog dialog = new AddToPlaylistsDialog(this, song, new PlaylistDialogAdapter(this, ohm.musicDB.getPlaylistCursor(-1)));
		dialog.setButtonListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				showNewPlaylistDialog(song);
				
			}
			
		});
		
		dialog.setListListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				long pid = view.getId();
				ohm.addToPlaylist(song, pid);
				dialog.dismiss();

				Toast.makeText(getApplicationContext(), song.getName() + " Added To " + ohm.getModel().getPlaylists().get(pid).getName(), Toast.LENGTH_LONG).show();
				
			}
			
		});
		dialog.show();
	}
	
	private void showNewPlaylistDialog(final Song song){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	playlistname = new EditText(this);
        alert.setView(playlistname);
        alert.setTitle("Name Your New Playlist");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	long id = ohm.createPlaylist(playlistname.getText().toString());
            	ohm.addToPlaylist(song, id);
            	Toast.makeText(getApplicationContext(), song.getName() + " Added To " + playlistname.getText().toString(), Toast.LENGTH_LONG).show();
            	}
            
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
        	 
            @Override

            public void onClick(DialogInterface d, int which) {
            		
                    d.dismiss();
                    addToPlaylistDialog(song);

            }
        });
        alert.show();
        }
	
	private void registerMusicReceiver(){
		np_updater = new MusicReceiver();
		IntentFilter filter = new IntentFilter();
        filter.addAction(OhmPlaybackService.ENDSONG);
		registerReceiver(np_updater, filter);
	}
	
	private void goToPlaylists(){
		
		if(ohm.musicDB.getPlaylistCursor(-1).getCount()>0){
			
			Intent playlistsintent = new Intent(NowPlayingScreen.this,PlaylistsScreen.class);
			
			startActivity(playlistsintent);
			
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
			else{
				Intent noplaylistsintent = new Intent(NowPlayingScreen.this,NoPlaylists.class);
				
				startActivity(noplaylistsintent);
				
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
	}
	
	private void goToMusic(){
		Intent mediaintent = new Intent(NowPlayingScreen.this,MusicGallery.class);
		
		startActivity(mediaintent);
		
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private void goToSearch(){

		Intent mediaintent = new Intent(NowPlayingScreen.this,MusicLibrary.class);
		
		startActivity(mediaintent);
	
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private void goToLoginScreen(){

		Intent mediaintent = new Intent(NowPlayingScreen.this,LoginScreen.class);
		
		startActivity(mediaintent);
	
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private void goToQueue(){
		Intent playlistsintent = new Intent(NowPlayingScreen.this,QueueScreen.class);
		
		startActivity(playlistsintent);
		
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	
	
	private boolean previousSong(){
		if(ohm.np_position>0){
			
			ohm.np_position--;
			
			try {
				ohm.mService.prev();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//sendBroadcast(OhmPlaybackService.PREVIOUS);
			
			
	   	    return true;
			}
				
		else{
			ohm.np_position = ohm.np_list.size() - 1;
			try {
				ohm.mService.prev();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
	}
	
	public boolean playPause(){
			try {
				boolean paused = ohm.mService.playPause();
				if(!paused){
					counter.stopStart();
					return false;
				}
				else{
					counter.start();
					return true;
				}
			} catch (RemoteException e) {
				
				e.printStackTrace();
			}
			return true;
		
		}
	
	
	
	
    
	private void showScrubber(){
    	if(playbackcontrols.getVisibility() == View.GONE){
    		
    		playbackcontrols.setVisibility(View.VISIBLE);
    		
    	}
    	else{
    		playbackcontrols.setVisibility(View.GONE);
    	}
    	
    }
	
	private void setShuffle(){
		np_switcher.setAnimation(NOW_PLAYING_SCREEN);
		try{
			if(ohm.mService.getShuffleMode() == 0){
				ohm.shufflelist(ohm.np_list);
				updateUI();
				shuffleImage.setAlpha(255);
				Toast.makeText(getApplicationContext(), "Shuffle On", Toast.LENGTH_SHORT).show();
			}
			else{
				ohm.setNowPlaying(ohm.np_list, ohm.np_position);
				updateUI();
				shuffleImage.setAlpha(100);
				Toast.makeText(getApplicationContext(), "Shuffle Off", Toast.LENGTH_SHORT).show();
			}
		}
			 
		catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
		}
		
	}
	private void setRepeat(){
		try{
			if(ohm.mService.getRepeatMode() == 0){
				
				ohm.mService.setRepeatMode(1);
				repeatImage.setAlpha(255);
				Toast.makeText(getApplicationContext(), "Repeat On", Toast.LENGTH_SHORT).show();
			}
			else{
			
				ohm.mService.setRepeatMode(0);
				repeatImage.setAlpha(100);
				Toast.makeText(getApplicationContext(), "Repeat Off", Toast.LENGTH_SHORT).show();
			}
		}
			 
		catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
		}
		
	}

    private void buildUI(){
    	
   
    	
        View music_nav_button = (View) findViewById(R.id.musicnavbutton); 
		music_nav_button.setOnClickListener(new View.OnClickListener() { 
			
			public void onClick (View v){
					
				goToMusic();
				
				
			};
		
		});
		
		
		ImageView search_button = (ImageView) findViewById(R.id.searchButton);
		search_button.setOnClickListener(new View.OnClickListener() { 
			
			public void onClick (View v){
				
				goToSearch();
										
			};
																	
		});
		
		

			
		
		View playlists_nav_button = (View) findViewById(R.id.playlistsnavbutton);
		playlists_nav_button.setOnClickListener(new View.OnClickListener() { 
			
			public void onClick (View v){
				
				goToPlaylists();
				
			};
																	
		});
		

		 
		View queue_nav_button = (View) findViewById(R.id.queuenavbutton);
		queue_nav_button.setOnClickListener(new View.OnClickListener() { 
			
			public void onClick (View v){
			
				goToQueue();
										
			};
																	
		});
		
		repeatImage = (ImageView)findViewById(R.id.repeat);
		repeatImage.setAlpha(100);
		repeatImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				setRepeat();
				
			}
			
		});
		shuffleImage = (ImageView)findViewById(R.id.shuffle);
		shuffleImage.setAlpha(100);
		shuffleImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				setShuffle();
				
			}
			
		});
		
		playbackcontrols = (RelativeLayout) findViewById(R.id.nowplaybackcontrols);
        np_artist = (TextView)findViewById(R.id.npartist);
        np_song =  (TextView)findViewById(R.id.npsong);
        progressbar = (SeekBar)findViewById(R.id.npplayback);
        
        np_switcher = (NowPlayingSwitcher)findViewById(R.id.now_playing_switcher);
        np_switcher.setNowPlayingChangedListener(new NowPlayingChangedListener(){

			@Override
			public void onChange(int action) {
				switch(action){
					case NEXT_SONG:
						
						if(ohm.np_list!=null){
								ohm.nextSong();
								updateUI();
						}
						else{
							Toast.makeText(getApplicationContext(), "Next Song Gesture", Toast.LENGTH_SHORT).show();
						}
						break;
					
					case PREVIOUS_SONG:
						if(ohm.np_list!=null){
								previousSong();
								updateUI();
						}
						else{
							Toast.makeText(getApplicationContext(), "Previous Song Gesture", Toast.LENGTH_SHORT).show();
						}
						break;
					
					case PLAY_PAUSE:
						
						if(ohm.np_list!=null){
						if(playPause()){
							Toast.makeText(getApplicationContext(), "Playing", Toast.LENGTH_SHORT).show();
						}
						else{
							Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_SHORT).show();
						}
						}
						else{
							ohm.shufflelist(ohm.music_data_model.getSongs());
							updateUI();
							shuffleImage.setAlpha(255);
							Toast.makeText(getApplicationContext(), "Enjoy Your Music", Toast.LENGTH_SHORT).show();
						}
						break;
					
					case SHOW_SCRUBBER:
						
						if(ohm.np_list!=null)
							showScrubber();
						else
							Toast.makeText(getApplicationContext(), "Playback Time Control Gesture", Toast.LENGTH_SHORT).show();
						
						break;
					case SHOW_DIALOG:
						if(ohm.np_list!=null)
							NPSongDialog();
						break;
				}
				
			}
        	
        });
        
       
        
        np_switcher.setFactory(this);

		progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		
			@Override
			public void onStopTrackingTouch(SeekBar seekbar) {
				counter.stopStart();
				try {
					ohm.mService.seek(seekbar.getProgress());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
														
			}
		
			@Override
			public void onStartTrackingTouch(SeekBar seekbar) {
				counter.stopStart();
				
			}
		
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromtouch) {}
																							
		});
		
		
    }

	@Override
	public View makeView() {
		
		ImageView iView = (ImageView)LayoutInflater.from(getApplicationContext()).inflate(R.layout.now_playing_song_item, np_switcher, false);
	            
	    return iView;
	}
	
	private class MusicReceiver extends BroadcastReceiver {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(OhmPlaybackService.ENDSONG)) {
	        	try{
	    			if(ohm.mService.getRepeatMode() == 0){
	        	if(ohm.np_position<ohm.np_list.size()-1){
	    			
	    			ohm.np_position++;
	        		np_switcher.setAnimation(NEXT_SONG);
	        		updateUI();
	        	}
	    			}
	    			else{
	    				updateUI();
	    			}
	        	}
	    			catch (RemoteException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	        }
	      
	        
	    }
	}
	public void sendBroadcast(String action){
        Intent broadcast = new Intent();
        broadcast.setAction(action);
        sendBroadcast(broadcast);
    }
	class Counter extends CountDownTimer 
	{
		int elapsedmillisInFuture=0;
		boolean paused = false;
		public Counter(long millisInFuture, long countDownInterval) 
		{
			super(millisInFuture, countDownInterval);
			
		}

		public void onFinish() {
		
		}
		
		public void onTick(long millsIncreasing) 
		{
			if(!paused){
			try {
				elapsedmillisInFuture = ohm.mService.position();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
			catch(IllegalStateException e){
				
			}
			
			progressbar.setProgress(elapsedmillisInFuture);
			}
			else{
				
			}
		}
		
		public void stopStart(){
			if(paused)
				paused = false;
			else
				paused = true;
		}
		 

		    
		
	}
}