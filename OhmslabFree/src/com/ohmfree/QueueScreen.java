package com.ohmfree;




import java.util.List;

import com.ohmfree.MusicUtils.ServiceToken;


import customviews.classes.ActionDialog;
import customviews.classes.AddToPlaylistsDialog;
import customviews.classes.NPSongDialogListener;
import customviews.classes.QueueDialogListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import datacontrollers.classes.PlaylistDialogAdapter;
import datacontrollers.classes.QueueAdapter;
import datamodel.classes.Song;

public class QueueScreen extends Activity implements MusicUtils.Defs{

	ApplicationObject ohm;
	QueueAdapter queue_adapter = null;
	ListView listView = null;
	TextView queue_song_number = null;
	EditText playlist_name;
	Bitmap albumart = null;
	ImageView home_button;
	EditText playlistname;

	PlaylistDialogAdapter mAdapter;
	MusicReceiver np_updater;
	boolean receiver = false;
	
	@Override
	public void onStop(){
		super.onStop();
		
		unRegisterWithService();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		registerWithService();
		
		if(!ohm.getDataState()){
    		ohm.data_progress = ProgressDialog.show(QueueScreen.this, "Working..", "Building Music Library", true);
    		ohm.setDataState(ohm.buildDataModel());	
    	}
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
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
		ohm = (ApplicationObject)getApplicationContext();
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
	    setContentView(R.layout.queue_screen);
	    if(!ohm.getDataState()){
    		ohm.data_progress = ProgressDialog.show(QueueScreen.this, "Working..", "Building Music Library", true);
    		ohm.setDataState(ohm.buildDataModel());	
    	}
	    ohm.queueEditing = false;

		queue_adapter = new QueueAdapter(this, ohm.music_data_model.getQueue(), -1);
		listView = (ListView) findViewById(R.id.list);
   	    listView.setAdapter(queue_adapter);
   	    listView.setSmoothScrollbarEnabled(true);
   		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				ohm.setNowPlaying(ohm.getModel().getQueue(), position);
				
				navigateHome();
				
			}
        	
		});
   		
   		registerForContextMenu(listView);
		
   		queue_song_number = (TextView) findViewById(R.id.queue_number_songs);
   		queue_song_number.setText(queue_adapter.getCount() + " Songs");
        
   		home_button = (ImageView) findViewById(R.id.ohmButton);
        home_button.setOnClickListener(new ImageView.OnClickListener() { 
			
        	public void onClick (View v){
        	
        		navigateHome();
    			
 	
        	};
		
        });
		   
        ImageView action_button = (ImageView) findViewById(R.id.actionButton);
		
        action_button.setOnClickListener(new ImageView.OnClickListener() { 
			
        	public void onClick (View v){
        		QueueDialog();
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
	
	private void createPlaylistFromQueue(){
		long id = ohm.createPlaylist(playlist_name.getText().toString());
        ohm.addToPlaylist(ohm.getModel().getQueue(), id);   
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
	
	protected Dialog onCreateDialog(int id) {
		Context mContext = this;
		
		Dialog dialog = new Dialog(mContext);
		
		switch (id){
				
			case PLAYLISTS_DIALOG:

				dialog.setTitle("Add to Playlist");
				dialog.setContentView(R.layout.playlists_dialogue);
				
				mAdapter = new PlaylistDialogAdapter(this, ohm.musicDB.getPlaylistCursor(-1));
				
				ListView pList = (ListView)dialog.findViewById(R.id.list);
				
				
				pList.setAdapter(mAdapter);
				
				pList.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							long pid = view.getId();
							ohm.addToPlaylist(ohm.getModel().getQueue(), pid);
							removeDialog(PLAYLISTS_DIALOG);

							Toast.makeText(getApplicationContext(), "Queue Added To " + ohm.getModel().getPlaylists().get(pid).getName(), Toast.LENGTH_LONG).show();
					}
				
				});	
			
				return dialog;
				
				
		}
			
		return null;
        
		
    
	}
	
	private void QueueDialog(){
		ActionDialog queueDialog = new ActionDialog(QueueScreen.this, ohm.getModel().getQueue());
		queueDialog.setQueueDialogListener(new QueueDialogListener(){

			@Override
			public void onEditClick(List<Song> songlist) {
				if(songlist.size()>0){
				ohm.setEditList("Queue", songlist);
				unRegisterWithService();
				ohm.queueEditing = true;
				Intent editintent = new Intent(QueueScreen.this,EditScreen.class);
				startActivity(editintent);
				}
				else{
					Toast.makeText(getApplicationContext(), "No Songs in Queue" , Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onToPlaylistClick(List<Song> songlist) {
				if(songlist.size()>0){
					addToPlaylistDialog(songlist);
				}
				else{

					Toast.makeText(getApplicationContext(), "No Songs in Queue" , Toast.LENGTH_SHORT).show();
				}
				
			}

			@Override
			public void onSaveClick(List<Song> songlist) {
				if(songlist.size()>0){
				 showSavePlaylistDialog();
				}
				else{

					Toast.makeText(getApplicationContext(), "No Songs in Queue" , Toast.LENGTH_SHORT).show();
				}
				
			}

			@Override
			public void onShuffleClick(List<Song> songlist) {
				if(songlist.size()>0){
				ohm.shufflelist(ohm.getModel().getQueue());
				navigateHome();
				}
				else{

					Toast.makeText(getApplicationContext(), "No Songs in Queue" , Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		queueDialog.show();
	}
	
	private void showSavePlaylistDialog(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	playlist_name = new EditText(this);
        alert.setView(playlist_name);
        alert.setTitle("Name Your New Playlist");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.dismiss();
            	createPlaylistFromQueue();
            	
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
        	 
            @Override

            public void onClick(DialogInterface d, int which) {

                    d.dismiss();

            }
        });
        alert.show();
        }

	@Override  
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		super.onCreateContextMenu(menu, v, menuInfo);  
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		String songName= ((TextView) info.targetView.findViewById(R.id.songName)).getText().toString();
		menu.setHeaderTitle(songName);  
		menu.add(0, v.getId(), 0, "Play");  
		menu.add(0, v.getId(), 0, "Add to Playlist");
	}  
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {  
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
        if(item.getTitle()=="Play"){
			
			ohm.setNowPlaying(ohm.getModel().getQueue(), info.position);

			navigateHome();
        }   
        
        else if(item.getTitle()=="Add to Playlist"){
            addToPlaylistDialog(ohm.getModel().getQueue().get(info.position));
        }  
        
        else {return false;}  
    
        return true;  
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
		
	private void addToPlaylistDialog(final List<Song> songlist){
		final AddToPlaylistsDialog dialog = new AddToPlaylistsDialog(this, songlist, new PlaylistDialogAdapter(this, ohm.musicDB.getPlaylistCursor(-1)));
		dialog.setButtonListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				showNewPlaylistDialog(songlist);
				
			}
			
		});
		
		dialog.setListListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				long pid = view.getId();
				ohm.addToPlaylist(songlist, pid);
				dialog.dismiss();

				Toast.makeText(getApplicationContext(),  "Queue Added To " + ohm.getModel().getPlaylists().get(pid).getName(), Toast.LENGTH_LONG).show();
				
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
	
	private void showNewPlaylistDialog(final List<Song> songlist){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	playlistname = new EditText(this);
        alert.setView(playlistname);
        alert.setTitle("Name Your New Playlist");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	long id = ohm.createPlaylist(playlistname.getText().toString());
            	ohm.addToPlaylist(songlist, id);
            	Toast.makeText(getApplicationContext(),   "Queue Added To " + playlistname.getText().toString(), Toast.LENGTH_LONG).show();
            	}
            
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
        	 
            @Override

            public void onClick(DialogInterface d, int which) {
            		
                    d.dismiss();
                    addToPlaylistDialog(songlist);

            }
        });
        alert.show();
        }

	public void navigateHome(){
		unRegisterWithService();
		
		Intent ohmintent = new Intent(QueueScreen.this,NowPlayingScreen.class);
		
		startActivity(ohmintent);
		
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}


