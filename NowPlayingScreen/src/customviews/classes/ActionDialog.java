package customviews.classes;


import java.util.List;

import com.ohmslab.MusicUtils;
import com.ohmslab.R;



import datamodel.classes.Album;
import datamodel.classes.Artist;
import datamodel.classes.Playlist;
import datamodel.classes.Song;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ActionDialog extends Dialog{
	
	private NPSongDialogListener nplistener;
	private AlbumDialogListener albumlistener;
	private PlaylistDialogListener playlistlistener;
	private QueueDialogListener queuelistener;
	private ArtistDialogListener artistlistener;
	
	private Bitmap albumart;
	
	private static final int NP_SONG_DIALOG = 0;
	private static final int PLAYLIST_SONG_DIALOG = 1;
	private static final int QUEUE_SONG_DIALOG = 2;
	private static final int MUSIC_SONG_DIALOG = 3;
	

	public ActionDialog(Context context, final Album album) {
		super(context, R.style.dialog_theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.dialog_album);
		getWindow().setGravity(Gravity.BOTTOM);
		setCanceledOnTouchOutside(true);
			
			TextView songname = (TextView)findViewById(R.id.songName);
			TextView artistname = (TextView)findViewById(R.id.artistName);
			ImageView artwork = (ImageView)findViewById(R.id.albumArt);
			
			songname.setText(album.getName());
			artistname.setText(album.getArtist().getName());
			
			albumart = MusicUtils.getArtwork(context, album.getArtworkUri());
	        
	        if(albumart != null){
	        	
	        	artwork.setImageBitmap(albumart);
	        }
	        else{
	        	artwork.setImageResource(MusicUtils.colors[(int) (album.getID()%7)]);
	        }
	        
	        Button shuffle_button = (Button)findViewById(R.id.shuffleButton);
	        
	        shuffle_button.setOnClickListener(new Button.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					albumlistener.onShuffleClick(album);
					dismiss();
				}
	        	
	        });
	        
	        Button playlist_button = (Button)findViewById(R.id.toPlaylistButton);
	        
	        playlist_button.setOnClickListener(new Button.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					albumlistener.onAddToPlaylistClick(album);
					dismiss();
				}
	        	
	        });
	        
	        Button queue_button = (Button)findViewById(R.id.toQueueButton);
	        
	        queue_button.setOnClickListener(new View.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					albumlistener.onAddToQueueClick(album);
					dismiss();
				}
	        	
	        });
		
		
		
	}
	
	public ActionDialog(Context context, final Artist artist) {
		super(context,R.style.dialog_theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setGravity(Gravity.BOTTOM);
		setCanceledOnTouchOutside(true);
		
			setContentView(R.layout.dialog_artist);
			
			
			TextView albumNumber = (TextView)findViewById(R.id.albumNumber);
			TextView artistname = (TextView)findViewById(R.id.artistName);
			ImageView artwork = (ImageView)findViewById(R.id.albumArt);
		
			artistname.setText(artist.getName());
			
			if(artist.getAlbums().size()>1){
				artwork.setImageResource(R.drawable.allsongs);
				albumNumber.setText(artist.getAlbums().size() + " Albums");
			}
			else{	
				albumart = MusicUtils.getArtwork(context, artist.getAlbums().get(0).getArtworkUri());

				albumNumber.setText("1 Album");
				if(albumart != null){
	        	
					artwork.setImageBitmap(albumart);
				}
				else{
					artwork.setImageResource(MusicUtils.colors[(int) (artist.getID()%7)]);
				}
			
			}
			
			Button shuffle_button = (Button)findViewById(R.id.shuffleButton);
	        
	        shuffle_button.setOnClickListener(new Button.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					artistlistener.onShuffleClick(artist);
					dismiss();
				}
	        	
	        });
	        
	        Button playlist_button = (Button)findViewById(R.id.toPlaylistButton);
	        
	        playlist_button.setOnClickListener(new Button.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					artistlistener.onAddToPlaylistClick(artist);
					dismiss();
				}
	        	
	        });
	        
	        Button queue_button = (Button)findViewById(R.id.toQueueButton);
	        
	        queue_button.setOnClickListener(new View.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					artistlistener.onAddToQueueClick(artist);
					dismiss();
				}
	        	
	        });
		
		
	}
	
	public ActionDialog(Context context, final Song song) {
		super(context, R.style.dialog_theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setGravity(Gravity.BOTTOM);
		setCanceledOnTouchOutside(true);
		
			setContentView(R.layout.dialog_np);
			
			
			TextView songname = (TextView)findViewById(R.id.songName);
			TextView artistname = (TextView)findViewById(R.id.artistName);
			ImageView artwork = (ImageView)findViewById(R.id.albumArt);
			
			songname.setText(song.getName());
			artistname.setText(song.getArtistName());
			
				
			albumart = MusicUtils.getArtwork(context, song.getArtworkUri());
				
			if(albumart != null){
	        	
				artwork.setImageBitmap(albumart);
				
			}
			else{
					
				artwork.setImageResource(MusicUtils.colors[(int) (song.getID()%7)]);
				
			}
			
			Button to_music_button = (Button)findViewById(R.id.toMusicButton);
	        to_music_button.setText("More '" + song.getArtistName() + "'");
	        to_music_button.setOnClickListener(new Button.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					nplistener.onGoToMusicClick(song);
					dismiss();
				}
	        	
	        });
	        
	        Button playlist_button = (Button)findViewById(R.id.toPlaylistButton);
	        
	        playlist_button.setOnClickListener(new Button.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					nplistener.onAddToPlaylistClick(song);
					dismiss();
				}
	        	
	        });
	        
	        Button queue_button = (Button)findViewById(R.id.toQueueButton);
	        
	        queue_button.setOnClickListener(new View.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					nplistener.onAddToQueueClick(song);
					dismiss();
				}
	        	
	        });
	        
	        
		
		
	}
	
	public ActionDialog(Context context, final Playlist playlist) {
		super(context,R.style.dialog_theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setGravity(Gravity.BOTTOM);
		setCanceledOnTouchOutside(true);
		
			setContentView(R.layout.dialog_playlist);
			
			
			TextView playlistname = (TextView)findViewById(R.id.playlistName);
			TextView songnumber = (TextView)findViewById(R.id.songNumber);
			ImageView playlistart = (ImageView)findViewById(R.id.playlistArt);
			
			playlistname.setText(playlist.getName());
			songnumber.setText(playlist.getSongs().size() + " Songs");
			
			
					
			playlistart.setImageResource(MusicUtils.colors[(int) (playlist.getID()%7)]);
				
			
			
			Button shuffle_button = (Button)findViewById(R.id.shuffleButton);
	        
	        shuffle_button.setOnClickListener(new Button.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					playlistlistener.onShuffleClick(playlist);
					dismiss();
				}
	        	
	        });
	        
	        Button edit_button = (Button)findViewById(R.id.editButton);
	        
	        edit_button.setOnClickListener(new Button.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					playlistlistener.onEditClick(playlist);
					dismiss();
				}
	        	
	        });
	        
	        Button queue_button = (Button)findViewById(R.id.toQueueButton);
	        
	        queue_button.setOnClickListener(new View.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					playlistlistener.onAddToQueueClick(playlist);
					dismiss();
				}
	        	
	        	
	        });
	        
	        
		
		
	}
	
	public ActionDialog(Context context, final List<Song> songlist) {
		super(context, R.style.dialog_theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setGravity(Gravity.BOTTOM);
		setCanceledOnTouchOutside(true);
			setContentView(R.layout.dialog_queue);
			
			TextView playlistname = (TextView)findViewById(R.id.queueName);
			TextView songnumber = (TextView)findViewById(R.id.songNumber);
			
			playlistname.setText("Queue");
			songnumber.setText(songlist.size() + " Songs");
			
			
			
			
			Button shuffle_button = (Button)findViewById(R.id.shuffleButton);
	        
	        shuffle_button.setOnClickListener(new Button.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					queuelistener.onShuffleClick(songlist);
					dismiss();
				}
	        	
	        });
	        
	        Button edit_button = (Button)findViewById(R.id.editButton);
	        
	        edit_button.setOnClickListener(new Button.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					queuelistener.onEditClick(songlist);
					dismiss();
				}
	        	
	        });
	        
	        Button playlist_button = (Button)findViewById(R.id.toPlaylistButton);
	        
	        playlist_button.setOnClickListener(new View.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					queuelistener.onToPlaylistClick(songlist);
					dismiss();
				}
	        	
	        	
	        });
	        
	        Button save_button = (Button)findViewById(R.id.saveButton);
	        
	        save_button.setOnClickListener(new View.OnClickListener(){
	        	@Override
				public void onClick(View view) {
					queuelistener.onSaveClick(songlist);
					dismiss();
				}
	        	
	        });	
		
		
		
	}
	
	public void setArtistDialogListener(ArtistDialogListener listener){
		this.artistlistener = listener;
	}
	
	public void setNPSongDialogListener(NPSongDialogListener listener){
		this.nplistener = listener;
	}
	
	public void setQueueDialogListener(QueueDialogListener listener){
		this.queuelistener = listener;
	}
	
	public void setPlaylistDialogListener(PlaylistDialogListener listener){
		this.playlistlistener = listener;
	}

	public void setAlbumDialogListener(AlbumDialogListener listener){
		this.albumlistener = listener;
	}
}
