package com.ohmfree;


import com.ohmfree.MusicUtils.ServiceToken;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MusicLibrary extends TabActivity{
   
	ApplicationObject ohm;
	Bitmap albumart = null;
	ImageView home_button;
	
	MusicReceiver np_updater;
	boolean receiver = false;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
	    setContentView(R.layout.music_library_screen);  
		ohm = (ApplicationObject) getApplicationContext();
		
		if(!ohm.getDataState()){
			ohm.data_progress = ProgressDialog.show(MusicLibrary.this, "Working..", "Building Music Library", true);
			ohm.setDataState(ohm.buildDataModel());	
		}
		
		buildNavBar();
		buildTabs();
	}
	
	@Override
	public void onStop(){
		super.onStop();
	
		unRegisterWithService();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		registerWithService();
	
		
	}
	
	private void navigateHome(){
		
		unRegisterWithService();
		Intent intent1 = new Intent(MusicLibrary.this,NowPlayingScreen.class);
		startActivity(intent1);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	
	}

	private void buildNavBar(){
	
		home_button = (ImageView) findViewById(R.id.npNav);
		home_button.setOnClickListener(new ImageView.OnClickListener() { 
			public void onClick (View v){
				navigateHome();
			};
		});
	
		ImageView shuffle_all_button = (ImageView) findViewById(R.id.shuffleAllButton);
		shuffle_all_button.setOnClickListener(new View.OnClickListener() { 
			public void onClick (View v){
				ohm.shufflelist(ohm.music_data_model.getSongs());
				navigateHome();	
			};
		});
	
		ImageView music_gallery_nav_button = (ImageView) findViewById(R.id.musicNav);
		music_gallery_nav_button.setOnClickListener(new ImageView.OnClickListener() { 
		
    	public void onClick (View v){
    		unRegisterWithService();
    		Intent intent = new Intent(MusicLibrary.this, MusicGallery.class);
			startActivity(intent);							
    	};
    });
	
		if(ohm.np_list!= null){
        	if(!ohm.shuffle_on){
        	albumart = MusicUtils.getArtwork(getApplicationContext(), ohm.np_list.get(ohm.np_position).getArtworkUri());
       	    if(albumart != null){
       	        home_button.setImageBitmap(albumart);
       	    }
       	    else{
       	        home_button.setImageResource(MusicUtils.colors[(int) (ohm.np_list.get(ohm.np_position).getID())%7]);
       	    }
        	}
        	else{
        		albumart = MusicUtils.getArtwork(getApplicationContext(), ohm.shuffled.get(ohm.np_position).getArtworkUri());
	       	    if(albumart != null){
	       	        home_button.setImageBitmap(albumart);
	       	    }
	       	    else{
	       	        home_button.setImageResource(MusicUtils.colors[(int) (ohm.shuffled.get(ohm.np_position).getID())%7]);
	       	    }
        	}
        }
	}

	private void buildTabs(){
	TabHost tabHost = getTabHost(); 
	TabHost.TabSpec spec;  
	Intent intent;  

    intent = new Intent().setClass(this, MusicLibraryArtists.class);
    spec = tabHost.newTabSpec("artists").setIndicator(createTabView(this,"Artists", R.drawable.bs_artist)).setContent(intent); 
    tabHost.addTab(spec);

    intent = new Intent().setClass(this, MusicLibraryAlbums.class);
    spec = tabHost.newTabSpec("albums").setIndicator(createTabView(this,"Albums", R.drawable.bs_album)).setContent(intent);
    tabHost.addTab(spec);

    intent = new Intent().setClass(this, MusicLibrarySongs.class);
    spec = tabHost.newTabSpec("songs").setIndicator(createTabView(this,"Songs", R.drawable.bs_song)).setContent(intent);
    tabHost.addTab(spec);

    tabHost.setCurrentTab(ohm.musictabposition);
}

	private static View createTabView(final Context context, final String text, int resource) {
	
	    View view = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
	
	    TextView tv = (TextView) view.findViewById(R.id.tabText);
	
	    tv.setText(text);
	    
	    ImageView iv = (ImageView) view.findViewById(R.id.tabImage);
	    
	    iv.setImageResource(resource);
	    
	    return view;

	}

	private void registerMusicReceiver(){
	np_updater = new MusicReceiver();
	IntentFilter filter = new IntentFilter();
    filter.addAction(OhmPlaybackService.ENDSONG);
	registerReceiver(np_updater, filter);
}

	private void registerWithService(){
	if(!receiver){
		registerMusicReceiver();
		receiver = true;
	}
	

}

	private void unRegisterWithService(){
	
	
	if(receiver){
		receiver = false;
		this.unregisterReceiver(np_updater);
	}
}

	private class MusicReceiver extends BroadcastReceiver {

		@Override
    	public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(OhmPlaybackService.ENDSONG)) {
				if(ohm.nextSong()){
					albumart = MusicUtils.getArtwork(getApplicationContext(), ohm.np_list.get(ohm.np_position).getArtworkUri());
        		
					if(albumart != null){
						home_button.setImageBitmap(albumart);
					}
					else{
						home_button.setImageResource(MusicUtils.colors[(int) (ohm.np_list.get(ohm.np_position).getID())%7]);
					}
				}
			}
		}
	}

}
