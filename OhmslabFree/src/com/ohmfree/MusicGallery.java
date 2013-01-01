package com.ohmfree;

import java.util.List;

import com.ohmfree.MusicUtils.ServiceToken;



import customviews.classes.AToZTouchView;
import customviews.classes.ActionDialog;
import customviews.classes.AddToPlaylistsDialog;
import customviews.classes.AlbumDialogListener;
import customviews.classes.ArtistDialogListener;
import customviews.classes.ArtistFastScrolledListener;
import customviews.classes.HorizontalListView;

import datacontrollers.classes.PlaylistDialogAdapter;
import datamodel.classes.Album;
import datamodel.classes.Artist;
import datamodel.classes.Song;

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
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MusicGallery extends Activity implements MusicUtils.Defs{
	
	Gallery artist_gallery = null;	
	ListView song_list = null;
	HorizontalListView album_gallery = null;
	AToZTouchView atoz = null;
	TextView header = null;
	ImageView home_button;

	ApplicationObject ohm;
	Song selectedSong = null;
	Album selectedAlbum = null;
	Artist selectedArtist = null;
	List<Song> selectedSongList = null;
	
	EditText playlistname;
	Bitmap albumart = null;
	MusicReceiver np_updater;
	boolean receiver = false;
	
	ArtistGalleryAdapter artistsadapter = null;
	SongAdapter songAdapter = null;
	AlbumAdapter albumadapter = null;
	PlaylistDialogAdapter mAdapter;
	GalleryCounter artistloadcounter = null;

	int screenwidth = 0;
	int selectedObjectNum = SONG;


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
    		ohm.data_progress = ProgressDialog.show(MusicGallery.this, "Working..", "Building Music Library", true);
    		ohm.setDataState(ohm.buildDataModel());	
    	}

 
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
	
	private void addToPlaylistDialog(final Album album){
		final AddToPlaylistsDialog dialog = new AddToPlaylistsDialog(this, album, new PlaylistDialogAdapter(this, ohm.musicDB.getPlaylistCursor(-1)));
		dialog.setButtonListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				showNewPlaylistDialog(album);
				
			}
			
		});
		
		dialog.setListListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				long pid = view.getId();
				ohm.addToPlaylist(album.getSongs(), pid);
				dialog.dismiss();

				Toast.makeText(getApplicationContext(), album.getName() + " Added To " + ohm.getModel().getPlaylists().get(pid).getName(), Toast.LENGTH_LONG).show();
				
			}
			
		});
		dialog.show();
	}
	
	private void addToPlaylistDialog(final Artist artist){
		final AddToPlaylistsDialog dialog = new AddToPlaylistsDialog(this, artist, new PlaylistDialogAdapter(this, ohm.musicDB.getPlaylistCursor(-1)));
		dialog.setButtonListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				showNewPlaylistDialog(artist);
				
			}
			
		});
		
		dialog.setListListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				long pid = view.getId();
				ohm.addToPlaylist(artist.getSongs(), pid);
				dialog.dismiss();

				Toast.makeText(getApplicationContext(), artist.getName() + " Added To " + ohm.getModel().getPlaylists().get(pid).getName(), Toast.LENGTH_LONG).show();
				
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
	
	private void showNewPlaylistDialog(final Album album){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	playlistname = new EditText(this);
        alert.setView(playlistname);
        alert.setTitle("Name Your New Playlist");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	long id = ohm.createPlaylist(playlistname.getText().toString());
            	ohm.addToPlaylist(album.getSongs(), id);
            	Toast.makeText(getApplicationContext(), album.getName() + " Added To " + playlistname.getText().toString(), Toast.LENGTH_LONG).show();
            	}
            
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
        	 
            @Override

            public void onClick(DialogInterface d, int which) {
            		
                    d.dismiss();
                    addToPlaylistDialog(album);

            }
        });
        alert.show();
        }
	
	private void showNewPlaylistDialog(final Artist artist){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	playlistname = new EditText(this);
        alert.setView(playlistname);
        alert.setTitle("Name Your New Playlist");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	long id = ohm.createPlaylist(playlistname.getText().toString());
            	ohm.addToPlaylist(artist.getSongs(), id);
            	Toast.makeText(getApplicationContext(), artist.getName() + " Added To " + playlistname.getText().toString(), Toast.LENGTH_LONG).show();
            	}
            
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
        	 
            @Override

            public void onClick(DialogInterface d, int which) {
            		
                    d.dismiss();
                    addToPlaylistDialog(artist);

            }
        });
        alert.show();
        }
	
	private void albumDialog(){
		ActionDialog albumDialog = new ActionDialog(MusicGallery.this, selectedAlbum);
		albumDialog.setAlbumDialogListener(new AlbumDialogListener(){

			@Override
			public void onShuffleClick(Album album) {
				ohm.shufflelist(album.getSongs());
				unRegisterWithService();
				goHome();
			}

			@Override
			public void onAddToPlaylistClick(Album album) {
				addToPlaylistDialog(album);
				
				
			}

			@Override
			public void onAddToQueueClick(Album album) {
				ohm.addToQueue(album.getSongs());
				Toast.makeText(getApplicationContext(), album.getName() + " Added to Queue", Toast.LENGTH_SHORT).show();
				
				
			}
			
		});
		
		albumDialog.show();
	}
	
	private void artistDialog(){
		ActionDialog artistDialog = new ActionDialog(MusicGallery.this, selectedArtist);
		artistDialog.setArtistDialogListener(new ArtistDialogListener(){

			@Override
			public void onShuffleClick(Artist artist) {
				ohm.shufflelist(artist.getSongs());
				unRegisterWithService();
				goHome();
			}

			@Override
			public void onAddToPlaylistClick(Artist artist) {
				addToPlaylistDialog(artist);
				
				
			}

			@Override
			public void onAddToQueueClick(Artist artist) {
				ohm.addToQueue(artist.getSongs());
				Toast.makeText(getApplicationContext(), artist.getName() + " Added to Queue", Toast.LENGTH_SHORT).show();
				
				
			}
			
		});
		artistDialog.show();
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
	
	private void buildNavBar(){
		 final View shuffle_all_button = (View) findViewById(R.id.shuffleallbutton);
			
	        shuffle_all_button.setOnClickListener(new View.OnClickListener() { 
				
	        	public void onClick (View v){
	        		
	        		ohm.shufflelist(ohm.music_data_model.getSongs());
	        		unRegisterWithService();
	        		goHome();
	        		
	        	};
			
	        });
	        
	        home_button = (ImageView) findViewById(R.id.backButton);
			
	        home_button.setOnClickListener(new ImageView.OnClickListener() { 
	        	
				public void onClick (View v){
					goHome();
	 	
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
	        
	        ImageView libraryscreennav = (ImageView) findViewById(R.id.libraryButton);
			
			libraryscreennav.setOnClickListener(new ImageView.OnClickListener() { 
				
				public void onClick (View v){

					unRegisterWithService();
					
					Intent intent = new Intent(MusicGallery.this, MusicLibrary.class);
					startActivity(intent);
											
				};
			
			});
	}
	
	private void buildUI(){
		artistloadcounter = new GalleryCounter(300, 100);
		final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		artist_gallery = (Gallery) findViewById(R.id.gallery_artist);
        album_gallery = (HorizontalListView)findViewById(R.id.albumGallery);
        header = (TextView) findViewById(R.id.headerText);
        song_list = (ListView)findViewById(R.id.list);
        atoz = (AToZTouchView)findViewById(R.id.atoz);
        
        atoz.setArtistFastScrolledListener(new ArtistFastScrolledListener(){

			@Override
			public void onScroll(float screen_position) {
				scrollArtists(screen_position);
				
			}

			@Override
			public void onSelect(float screen_position) {
				scrollArtists(screen_position);
				
			}
        	
        });
        artistsadapter = new ArtistGalleryAdapter(this,ohm.music_data_model.getArtists());
        artist_gallery.setAdapter(artistsadapter);
        artist_gallery.setSelection(ohm.artistposition);

		atoz.setThumbPosition(((float)ohm.artistposition/(float)artist_gallery.getCount())*(float)screenwidth);
        
        albumadapter = new AlbumAdapter(this,ohm.music_data_model.getArtists().get(ohm.artistposition));
        album_gallery.setAdapter(albumadapter);
        
        songAdapter = new SongAdapter(this, ohm.music_data_model.getArtists().get(ohm.artistposition).getSongs());
        song_list.setAdapter(songAdapter);
        registerForContextMenu(song_list);
        song_list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				ohm.setNowPlaying(selectedSongList, position);
				goHome();
				
			}

        });
        

        artist_gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
        	
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				ohm.artistposition = position;
				selectedArtist = ohm.getModel().getArtists().get(position);
				atoz.setThumbPosition(((float)position/(float)artist_gallery.getCount())*(float)screenwidth);
				
				artistloadcounter.cancel();
				artistloadcounter = null;
				artistloadcounter = new GalleryCounter(300,100);
				artistloadcounter.start();
				
				
			
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        });
        
        album_gallery.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				setAlbumSelection(position);	
			}	
        });
        
        album_gallery.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				ohm.vibrator.vibrate(20);
				setAlbumSelection(position);
				if(selectedObjectNum == ALBUM){
        			albumDialog();
        		}
        		else if(selectedObjectNum == ARTIST){
        			artistDialog();		
        		}
        		return true;
					
				
			}
        	
        });

        
	}
   	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Display display = getWindowManager().getDefaultDisplay(); 
        screenwidth = display.getWidth();
        setContentView(R.layout.music_gallery_screen);
        ohm = (ApplicationObject) getApplicationContext();
    	if(!ohm.getDataState()){
    		ohm.data_progress = ProgressDialog.show(MusicGallery.this, "Working..", "Building Music Library", true);
    		ohm.setDataState(ohm.buildDataModel());	
    	}
        buildNavBar();
        buildUI();

   	}
   	
   	public void goHome(){
   		
   		unRegisterWithService();
		Intent ohmintent = new Intent(MusicGallery.this,NowPlayingScreen.class);
		startActivity(ohmintent);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		
	}
   	
   	@Override  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		super.onCreateContextMenu(menu, v, menuInfo);  
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		selectedObjectNum = SONG;
		String songName= ((TextView) info.targetView.findViewById(R.id.song_title)).getText().toString();
		selectedSong = selectedSongList.get(info.position);
        menu.setHeaderTitle(songName);  
        menu.add(0, v.getId(), 0, "Play");  
        menu.add(0, v.getId(), 0, "Add to Queue");
        menu.add(0, v.getId(), 0, "Add to Playlist");
    }  
	
   	@Override  
    public boolean onContextItemSelected(MenuItem item) {  
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
        if(item.getTitle()=="Play"){
        	ohm.setNowPlaying(selectedSongList, info.position);
			
			goHome();
			
        }  
       
        else if(item.getTitle()=="Add to Queue"){
        	
        	ohm.musicDB.addSongToQueue(selectedSongList.get(info.position));
        	ohm.music_data_model.addToQueue(selectedSongList.get(info.position));
            
        }  
        else if(item.getTitle()=="Add to Playlist"){
        	addToPlaylistDialog(selectedSongList.get(info.position));
        }  
        
        else {return false;}  
   
        return true;  
    
	}  
   	
   	public void scrollArtists(float screenposition) {
		if(screenposition<screenwidth)
   		artist_gallery.setSelection((int)(artist_gallery.getCount()*(screenposition/(float)screenwidth)) - 1);
	
	}
   
   	public void setAlbumSelection(int position){
   		albumadapter.setSelection(position);
		albumadapter.notifyDataSetChanged();
		if(albumadapter.getCount()>1)
		{
			if(position == 0){
				selectedObjectNum = ARTIST;
				selectedSongList = ohm.getModel().getArtists().get(ohm.artistposition).getSongs();
				header.setText("All Songs");
				songAdapter.albumSongs = selectedSongList;
				songAdapter.notifyDataSetChanged();
			}
			else{
				selectedObjectNum = ALBUM;
				selectedAlbum = selectedArtist.getAlbums().get(position - 1);
				selectedSongList =ohm.getModel().getArtists().get(ohm.artistposition).getAlbums().get(position - 1).getSongs();
				header.setText(ohm.getModel().getArtists().get(ohm.artistposition).getAlbums().get(position - 1).getName());
				songAdapter.albumSongs = selectedSongList;
				songAdapter.notifyDataSetChanged();
			}
		}
		else{
			selectedObjectNum = ALBUM;
			selectedAlbum = selectedArtist.getAlbums().get(position);
			selectedSongList = ohm.getModel().getArtists().get(ohm.artistposition).getAlbums().get(position).getSongs();
			header.setText(ohm.getModel().getArtists().get(ohm.artistposition).getAlbums().get(position).getName());
			songAdapter.albumSongs = selectedSongList;
			songAdapter.notifyDataSetChanged();
		}
    	
   		
    }
   	
   	private class GalleryCounter extends CountDownTimer 
   	{
   		
   		public GalleryCounter(long millisInFuture, long countDownInterval) 
   		{
   			super(millisInFuture, countDownInterval);
   		}

   		public void onFinish() {
   			albumadapter.artist = ohm.getModel().getArtists().get(ohm.artistposition);
   			
   			albumadapter.notifyDataSetChanged();
   			
   			
   			selectedSongList = ohm.getModel().getArtists().get(ohm.artistposition).getSongs();
   			albumadapter.setSelection(0);
   			setAlbumSelection(0);
   			
   			songAdapter.albumSongs = selectedSongList;
   			songAdapter.notifyDataSetChanged();
   			
   			album_gallery.setAdapter(albumadapter);
			
   		}
   		
   		public void onTick(long millsIncreasing) 
   		{
   		}
   		
   		
   		
   	}
   	
   	class AlbumAdapter extends BaseAdapter {
   		
   		Artist artist;
   		
   		Context context;
   		
   		Bitmap albumart;
   		
   		LayoutInflater albumInflater;
   		
   		int selectedPosition = 0;
   		
   		public AlbumAdapter(Context context, Artist artist){
   			this.context = context;
   			this.artist = artist;
   			albumInflater = LayoutInflater.from(context);
   		}

   		@Override
   		public int getCount() {
   			if(artist.getAlbums().size() == 1)
   				return 1;
   			else
   				return artist.getAlbums().size() + 1;
   			
   		}

   		@Override
   		public Object getItem(int position) {
   			
   			return position;
   		}

   		@Override
   		public long getItemId(int position) {
   			return position;
   		}
   		
   		public void setSelection(int position){
   			selectedPosition = position;
   		}

   		@Override
   		public View getView(int position, View convertView, ViewGroup parent) {
   			ViewHolder holder;
   			if(convertView==null){
   				holder = new ViewHolder();
   				convertView = albumInflater.inflate(R.layout.music_gallery_album_item, null);
   				
   				holder.album_art = (ImageView)convertView.findViewById(R.id.album_art);
   				holder.album_name = (TextView)convertView.findViewById(R.id.album_name);
   				
   				convertView.setTag(holder);
   			}
   			else{
   				holder = (ViewHolder)convertView.getTag();
   			}
   			
   			holder.album_name.setVisibility(View.VISIBLE);
   			if(getCount()>1){
   				if(position == 0){
   					holder.album_name.setText("All Songs");
   		         	holder.album_art.setImageResource(R.drawable.allsongs);
   				}
   				else{
   					holder.album_name.setText(artist.getAlbums().get(position - 1).getName());
   					
   					albumart = MusicUtils.getArtwork(context, artist.getAlbums().get(position - 1).getArtworkUri());
   		   	        
   		   	        if(albumart != null){
   		   	        	
   		   	        	holder.album_art.setImageBitmap(albumart);
   		   	        }
   		   	        else{
   		         	holder.album_art.setImageResource(MusicUtils.colors[position%7]);
   		   	        }
   				}
   				
   			}
   			else{
   			
   			holder.album_name.setText(artist.getAlbums().get(position).getName());  
   			
   			albumart = MusicUtils.getArtwork(context, artist.getAlbums().get(position).getArtworkUri());
   	        
   	        if(albumart != null){
   	        	
   	        	holder.album_art.setImageBitmap(albumart);
   	        }
   	        else{
         	holder.album_art.setImageResource(MusicUtils.colors[position%7]);
   	        }
   			}
   	    
   	        if(selectedPosition == position)
			{
				convertView.setBackgroundResource(R.drawable.playlist_border);
				holder.album_name.setVisibility(View.GONE);
			}
			else{
				convertView.setBackgroundResource(R.drawable.transparency);
			}
   			
   			return convertView;
   		}
   		
   		public class ViewHolder {
   			
   			TextView album_name;
   			ImageView album_art;
   			
   		}
   	}

   	class ArtistGalleryAdapter extends BaseAdapter{
	
    Context artistgalleryContext; 
    
    protected LayoutInflater artistInflater;
    
    List<Artist> allartists = null;
   
    public ArtistGalleryAdapter(Context context ,List<Artist> allArtists) {
        
    	artistgalleryContext = context;
    	
        artistInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        allartists = allArtists;

    }

    public int getCount() {
    	
    	return allartists.size();
    	
    }

    public Object getItem(int position) {
    	
        return position;
        
    }

    public long getItemId(int position) {
    	
    	return position;
    	
    }

    
    public View getView(int position, View convertView, ViewGroup parent) {
    
    	Artist artist = (Artist) allartists.get(position);
    	
    	convertView = artistInflater.inflate(R.layout.music_gallery_artist_item, parent, false);
    	
    	TextView tv = (TextView) convertView.findViewById(R.id.itemText);
    	
    	tv.setText(artist.getName());
    	
    	return convertView;
    	
    }
    
}

   	class SongAdapter extends BaseAdapter {
	
	protected LayoutInflater songInflater = null;
	
	List<Song> albumSongs = null;
	
	Context listcontext;
	
	int selectedPosition = -1;


	public SongAdapter(Context c , List<Song> albumsongs){
		
		listcontext = c;
		songInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.albumSongs = albumsongs;
		
	}
	
	public int getCount() { return albumSongs.size(); }
	
	public Object getItem(int position) { 
		return position; 
		}

    public boolean areAllItemsSelectable() { 
    	return false; 
    	}
    public void setList(List<Song> list){
    	
    	this.albumSongs= list;
    }
    public long getItemId(int position) {
         return position;
    }
    public void setSelection(int position){
    	this.selectedPosition = position;
    }
	
	public View getView(int position, View convertView, ViewGroup parent){
		
		final Song song = (Song) albumSongs.get(position);
		
		final ViewHolder holder; 
		
		convertView = songInflater.inflate(R.layout.music_gallery_song_item, parent, false);
		
		holder = new ViewHolder();
		
		holder.title = (TextView)convertView.findViewById(R.id.song_title);
		
		convertView.setTag(holder);
		
		holder.title.setText(song.getName());
		
		if(selectedPosition == position){
			convertView.setBackgroundResource(R.drawable.white_background);
		}	
	    return convertView;
	
	}
			
	
class ViewHolder {
		
	TextView title;
	
}


}

}


