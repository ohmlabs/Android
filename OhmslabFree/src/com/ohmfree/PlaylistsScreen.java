package com.ohmfree;



import java.util.List;

import com.ohmfree.MusicUtils.ServiceToken;

import customviews.classes.ActionDialog;
import customviews.classes.AddToPlaylistsDialog;
import customviews.classes.HorizontalListView;
import customviews.classes.PlaylistDialogListener;
import datacontrollers.classes.PlaylistDialogAdapter;
import datamodel.classes.Playlist;
import datamodel.classes.Song;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class PlaylistsScreen extends Activity{

	PlaylistMediaAdapter mediaAdapter;
	PlaylistDialogAdapter mAdapter;
	PlaylistGalleryAdapter playlist_gallery_adapter;
	Bitmap albumart = null;
	MusicReceiver np_updater;
	ServiceToken token;
	EditText playlistname;
	
	boolean receiver = false;
	boolean tokens = false;
	
	ListView listv = null;
	TextView playlisttitle = null;
	TextView playlist_number = null;
	ImageView new_playlist_button;
	ImageView home_button;
	ImageView empty_playlist;
	ApplicationObject ohm = null;
	EditText playlist_name;
	HorizontalListView playlist_gallery = null;
	
	private static final int ACTION_BUTTON = 1;
	private static final int PLAYLISTS = 2;
    
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
    		ohm.data_progress = ProgressDialog.show(PlaylistsScreen.this, "Working..", "Building Music Library", true);
    		ohm.setDataState(ohm.buildDataModel());	
    	}
	}
	
	private void addToPlaylistDialog(final Playlist playlist){
		final AddToPlaylistsDialog dialog = new AddToPlaylistsDialog(this, playlist, new PlaylistDialogAdapter(this, ohm.musicDB.getPlaylistCursor(-1)));
		dialog.setButtonListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				showNewPlaylistDialog(playlist);
				
			}
			
		});
		
		dialog.setListListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				long pid = view.getId();
				ohm.addToPlaylist(playlist.getSongs(), pid);
				dialog.dismiss();

				Toast.makeText(getApplicationContext(),  "Queue Added To " + ohm.getModel().getPlaylists().get(pid).getName(), Toast.LENGTH_LONG).show();
				
			}
			
		});
		dialog.show();
	}
	private void showNewPlaylistDialog(final Playlist playlist){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	playlistname = new EditText(this);
        alert.setView(playlistname);
        alert.setTitle("Name Your New Playlist");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	long id = ohm.createPlaylist(playlistname.getText().toString());
            	ohm.addToPlaylist(playlist.getSongs(), id);
            	Toast.makeText(getApplicationContext(), playlist.getName() + " Added To " + playlistname.getText().toString(), Toast.LENGTH_LONG).show();
            	}
            
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
        	 
            @Override

            public void onClick(DialogInterface d, int which) {
            		
                    d.dismiss();
                    addToPlaylistDialog(playlist);

            }
        });
        alert.show();
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
	private void PlaylistDialog(){
		Playlist playlist = ohm.getModel().getPlaylists().get(ohm.current_playlist_id);
		playlist.setUri(ohm.musicDB.getPlaylistArtworkUri(playlist.getID()));
		ActionDialog playlistDialog = new ActionDialog(PlaylistsScreen.this, playlist);
		playlistDialog.setPlaylistDialogListener(new PlaylistDialogListener(){

			@Override
			public void onShuffleClick(Playlist playlist) {
				if(playlist.getSongs().size()>0){
				ohm.shufflelist(playlist.getSongs());
				unRegisterWithService();
				navigateHome();
				}
				else{
					Toast.makeText(getApplicationContext(), "No Songs in Playlist", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onEditClick(Playlist playlist) {
				ohm.setEditList(playlist.getName(), playlist.getSongs());
				unRegisterWithService();
				
				Intent editintent = new Intent(PlaylistsScreen.this,EditScreen.class);
				
				startActivity(editintent);
				
				
			}

			@Override
			public void onAddToQueueClick(Playlist playlist) {
				if(playlist.getSongs().size()>0){
				ohm.addToQueue(playlist.getSongs());
				Toast.makeText(getApplicationContext(), playlist.getName() + " Added to Queue", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(getApplicationContext(), "No Songs in Playlist", Toast.LENGTH_SHORT).show();
				}
				
			}

			
			
		});
		playlistDialog.show();
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
		
		if(!tokens){
			token = MusicUtils.bindToService(this, ohm.serviceConn);
			tokens = true;
		}
	}

		private void unRegisterWithService(){
		if(tokens){
			tokens = false;
			MusicUtils.unbindFromService(token);
		}
		
		if(receiver){
			receiver = false;
			this.unregisterReceiver(np_updater);
		}
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.playlists_screen);
		ohm = (ApplicationObject) getApplicationContext();
		if(!ohm.getDataState()){
    		ohm.data_progress = ProgressDialog.show(PlaylistsScreen.this, "Working..", "Building Music Library", true);
    		ohm.setDataState(ohm.buildDataModel());	
    	}
		buildNavBar();
		ohm.setCurrentPlaylistId(ohm.current_playlist_id);
		initPlaylists();
		updateUI();

	}

	
	
	private void navigateHome(){
		unRegisterWithService();
		Intent ohmintent = new Intent(PlaylistsScreen.this,NowPlayingScreen.class);
		startActivity(ohmintent);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}


	private void updateUI(){
		playlisttitle.setText(ohm.music_data_model.getPlaylists().get(ohm.current_playlist_id).getName());
		
		if(ohm.music_data_model.getPlaylists().get(ohm.current_playlist_id).size()<1){
			empty_playlist.setVisibility(View.VISIBLE);
		}
		else{
			empty_playlist.setVisibility(View.GONE);
		}
	}
	
	private void initPlaylists(){
		
		playlisttitle = (TextView)findViewById(R.id.playlistTitle);
		playlist_gallery = (HorizontalListView)findViewById(R.id.playlistGallery);
		
		registerForContextMenu(listv);
		mediaAdapter = new PlaylistMediaAdapter(this, ohm.musicDB.getPlaylistCursor(ohm.current_playlist_id));
		playlist_gallery_adapter = new PlaylistGalleryAdapter(this, ohm.musicDB.getPlaylistCursor(-1), (int)ohm.current_playlist_id);
		listv.setAdapter(mediaAdapter);
		playlist_number.setText(playlist_gallery_adapter.getCount() + " Playlists");
		playlist_gallery.setAdapter(playlist_gallery_adapter);
		playlisttitle.setText(ohm.music_data_model.getPlaylists().get(ohm.current_playlist_id).getName());
		empty_playlist = (ImageView)findViewById(R.id.emptyPlaylist);
        
		listv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				ohm.setNowPlaying(ohm.music_data_model.getPlaylists().get(ohm.current_playlist_id).getSongs(), position);

				navigateHome();
				
			}
        	
		});
		playlist_gallery.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					
					if(ohm.current_playlist_id != view.getId()){
						ohm.current_playlist_id = view.getId();
						playlist_gallery_adapter.setSelection((int)ohm.current_playlist_id);		
						playlist_gallery_adapter.notifyDataSetChanged();
						mediaAdapter.changeCursor(ohm.musicDB.getPlaylistCursor(ohm.current_playlist_id));
						updateUI();
					}	
				}
		});	
		playlist_gallery.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(ohm.current_playlist_id != view.getId()){
					ohm.current_playlist_id = view.getId();
					playlist_gallery_adapter.setSelection((int)ohm.current_playlist_id);		
					playlist_gallery_adapter.notifyDataSetChanged();
					mediaAdapter.changeCursor(ohm.musicDB.getPlaylistCursor(ohm.current_playlist_id));
					updateUI();
				}
				PlaylistDialog();
				return true;
			}
			
		});
	
	}
	
	private void buildNavBar(){

		playlist_number = (TextView)findViewById(R.id.playlistNumber);
		listv = (ListView)findViewById(R.id.list);
		
		playlist_number.setText("Playlists");
		
		new_playlist_button = (ImageView) findViewById(R.id.newButton);
        new_playlist_button.setOnClickListener(new ImageView.OnClickListener() { 
			
        	public void onClick (View v){
        		showNewPlaylistDialog();
        	};
		
        });
        
        home_button = (ImageView) findViewById(R.id.backButton);
        home_button.setOnClickListener(new ImageView.OnClickListener() { 
			
        	public void onClick (View v){
        		navigateHome();
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

	@Override  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		super.onCreateContextMenu(menu, v, menuInfo);  
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		
		String songName= ((TextView) info.targetView.findViewById(R.id.songName)).getText().toString();
		String artistName = ((TextView) info.targetView.findViewById(R.id.artistName)).getText().toString();
		
        menu.setHeaderTitle(songName +" by " + artistName);  
        menu.add(0, v.getId(), 0, "Play");  
        menu.add(0, v.getId(), 0, "Add to Queue");
        menu.add(0, v.getId(), 0, "Add to Playlist");
    }  
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {  
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
        if(item.getTitle()=="Play"){
        	
        	
        	ohm.setNowPlaying(ohm.music_data_model.getPlaylists().get(ohm.current_playlist_id).getSongs(), info.position);
			
			navigateHome();
        }  
        else if(item.getTitle()=="Add to Queue"){
        	
        	ohm.addToQueue(ohm.music_data_model.getSongHash().get(info.id));
            
        }  
        else if(item.getTitle()=="Add to Playlist"){
        	addToPlaylistDialog(ohm.music_data_model.getPlaylists().get(ohm.current_playlist_id).getSongs().get(info.position));
            
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
	
    private void showNewPlaylistDialog(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	playlist_name = new EditText(this);
        alert.setView(playlist_name);
        alert.setTitle("Name Your New Playlist");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	long id = ohm.createPlaylist(playlist_name.getText().toString());
                
            	ohm.setCurrentPlaylistId(id);
            	updateUI();
        		playlist_number.setText(ohm.musicDB.getPlaylistCursor(-1).getCount() + " Playlists");	
				playlist_gallery_adapter.setSelection((int)ohm.current_playlist_id);
				mediaAdapter.changeCursor(ohm.musicDB.getPlaylistCursor(ohm.current_playlist_id));
				
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
   

	
    protected Dialog onCreateDialog(int id) {
		Context mContext = this;
		Dialog dialog = new Dialog(mContext);
		
		switch (id){
			case PLAYLISTS:
				dialog.setContentView(R.layout.playlists_dialogue);
				mAdapter = new PlaylistDialogAdapter(this, ohm.musicDB.getPlaylistCursor(-1));
				ListView pList = (ListView)dialog.findViewById(R.id.list);
				pList.setAdapter(mAdapter);
				return dialog;
		}	
		return null;
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
	        			home_button.setImageResource(MusicUtils.colors[(int) (ohm.np_list.get(ohm.np_position).getID()%7)]);
	        		}
	        	}
	       		
	        }
	        
	        
	    }
	}

	class PlaylistGalleryAdapter extends CursorAdapter{
		
		private LayoutInflater mInflater;
		
		private int mPlaylistId;
		private int mPlaylistName;
		
		private int selectedPlaylist;

		public PlaylistGalleryAdapter(Context context, Cursor c, int selectedPlaylist) {
			super(context, c);
			
			setSelection(selectedPlaylist);
			mPlaylistId = c.getColumnIndex(MediaStore.Audio.Playlists._ID);
			mPlaylistName = c.getColumnIndex(MediaStore.Audio.Playlists.NAME);
			
			mInflater = LayoutInflater.from(context);
		}
		
		public void setSelection(int position){
			this.selectedPlaylist = position;
		}
		


		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			ImageView playlist_color_one = (ImageView)view.findViewById(R.id.playlist_color_one);
			TextView playlistname = (TextView)view.findViewById(R.id.playlist_name);
			
			view.setId(cursor.getInt(mPlaylistId));
			
			playlist_color_one.setImageResource(MusicUtils.colors[view.getId()%7]);
			
			playlistname.setVisibility(View.VISIBLE);
			
			playlistname.setText(cursor.getString(mPlaylistName));
			
			
			
			if(selectedPlaylist == view.getId())
			{
				view.setBackgroundResource(R.drawable.playlist_border);
				playlistname.setVisibility(View.GONE);
			}
			else{
				view.setBackgroundResource(R.drawable.transparency);
			}
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return mInflater.inflate(R.layout.playlists_gallery_item, null);
		}
		
	}
	
	class PlaylistMediaAdapter extends CursorAdapter{
    	
		private LayoutInflater mInflater;
    	
		private int mSongId;
		private int mSongName;
		private int mArtistName;
		private int mAlbumId;
		
		
		private Bitmap albumart;
		
		public PlaylistMediaAdapter(Context context , Cursor cursor){

			super(context, cursor);
			
			mInflater = LayoutInflater.from(context);
			mSongId = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);
			mSongName = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
			mArtistName = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			mAlbumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
    	
		}
 
    	

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView songName = (TextView)view.findViewById(R.id.songName);
			TextView artistName = (TextView)view.findViewById(R.id.artistName);
			ImageView albumArt = (ImageView)view.findViewById(R.id.albumArt);
			
			songName.setText(cursor.getString(mSongName));
			artistName.setText(cursor.getString(mArtistName));
			
			albumart = MusicUtils.getArtwork(context, ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), cursor.getLong(mAlbumId)));
			view.setId(cursor.getInt(mSongId));
	        
	        if(albumart != null){
            	
            	albumArt.setImageBitmap(albumart);
            }
            else{
            	albumArt.setImageResource(MusicUtils.colors[view.getId()%7]);
            }
			
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return mInflater.inflate(R.layout.playlists_song_item, null);
		}	
    
}

}
