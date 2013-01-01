package customviews.classes;

import java.util.List;

import com.ohmfree.MusicUtils;
import com.ohmfree.R;

import datacontrollers.classes.PlaylistDialogAdapter;
import datamodel.classes.Album;
import datamodel.classes.Artist;
import datamodel.classes.Playlist;
import datamodel.classes.Song;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AddToPlaylistsDialog extends Dialog{
	
	Bitmap albumart = null;
	
	ListView list;
	Button newplaylist;

	public AddToPlaylistsDialog(Context context, Playlist playlist, PlaylistDialogAdapter adapter) {
		super(context, R.style.dialog_theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.dialog_add_playlist);
		setCanceledOnTouchOutside(true);
		
		TextView playlistname = (TextView)findViewById(R.id.firstText);
		TextView songcount = (TextView)findViewById(R.id.secondText);
		ImageView artwork = (ImageView)findViewById(R.id.photo);
		
		list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
		newplaylist = (Button)findViewById(R.id.newPlaylist);
		
		playlistname.setText(playlist.getName());
		songcount.setText(playlist.getSongs().size() + " Songs");
		
		albumart = MusicUtils.getArtwork(context, playlist.getUri());
		
		if(albumart != null){
        	
			artwork.setImageBitmap(albumart);
			
		}
		else{
				
			artwork.setImageResource(MusicUtils.colors[(int) (playlist.getID()%7)]);
			
		}
	}
	
	public AddToPlaylistsDialog(Context context, Song song, PlaylistDialogAdapter adapter) {
		super(context, R.style.dialog_theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.dialog_add_playlist);
		setCanceledOnTouchOutside(true);
		
		TextView songname = (TextView)findViewById(R.id.firstText);
		TextView artistname = (TextView)findViewById(R.id.secondText);
		ImageView artwork = (ImageView)findViewById(R.id.photo);
		
		list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
		newplaylist = (Button)findViewById(R.id.newPlaylist);
		
		songname.setText(song.getName());
		artistname.setText(song.getArtistName());
		
		albumart = MusicUtils.getArtwork(context, song.getArtworkUri());
		
		if(albumart != null){
        	
			artwork.setImageBitmap(albumart);
			
		}
		else{
				
			artwork.setImageResource(MusicUtils.colors[(int) (song.getID()%7)]);
			
		}
	}
	
	public AddToPlaylistsDialog(Context context, List<Song> songlist, PlaylistDialogAdapter adapter) {
		super(context, R.style.dialog_theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.dialog_add_playlist);
		setCanceledOnTouchOutside(true);
		
		TextView queueTitle = (TextView)findViewById(R.id.firstText);
		TextView queueLength = (TextView)findViewById(R.id.secondText);
		ImageView artwork = (ImageView)findViewById(R.id.photo);
		
		list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
		newplaylist = (Button)findViewById(R.id.newPlaylist);
		
		queueTitle.setText("Queue");
		queueLength.setText(songlist.size() + " Songs");
		
		
				
			artwork.setImageResource(MusicUtils.colors[1]);
			
		
	}
	
	public AddToPlaylistsDialog(Context context, Album album, PlaylistDialogAdapter adapter) {
		super(context, R.style.dialog_theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.dialog_add_playlist);
		setCanceledOnTouchOutside(true);
		
		TextView albumname = (TextView)findViewById(R.id.firstText);
		TextView artistname = (TextView)findViewById(R.id.secondText);
		ImageView artwork = (ImageView)findViewById(R.id.photo);
		
		list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
		newplaylist = (Button)findViewById(R.id.newPlaylist);
		
		albumname.setText(album.getName());
		artistname.setText(album.getArtist().getName());
		
		albumart = MusicUtils.getArtwork(context, album.getArtworkUri());
		
		if(albumart != null){
        	
			artwork.setImageBitmap(albumart);
			
		}
		else{
				
			artwork.setImageResource(MusicUtils.colors[(int) (album.getID()%7)]);
			
		}
	}
	
	public AddToPlaylistsDialog(Context context, Artist artist, PlaylistDialogAdapter adapter) {
		super(context, R.style.dialog_theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.dialog_add_playlist);
		setCanceledOnTouchOutside(true);
		
		TextView artistname = (TextView)findViewById(R.id.firstText);
		TextView songnumber = (TextView)findViewById(R.id.secondText);
		ImageView artwork = (ImageView)findViewById(R.id.photo);
		
		list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
		
		newplaylist = (Button)findViewById(R.id.newPlaylist);
		
		artistname.setText(artist.getName());
		songnumber.setText(artist.getSongs().size()+ " Songs");
		
		artwork.setImageResource(R.drawable.allsongs);
	}
	
	public void setListListener(OnItemClickListener listener){
		list.setOnItemClickListener(listener);
	}
	
	public void setButtonListener(View.OnClickListener listener){
		newplaylist.setOnClickListener(listener);
	}

}
