package datacontrollers.classes;



import com.ohmfree.R;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class PlaylistDialogAdapter extends CursorAdapter{
	
	
	private LayoutInflater mInflater;
	
	private int mPlaylistId;
	private int mPlaylistName;
	

	public PlaylistDialogAdapter(Context context, Cursor c) {
		super(context, c);
		
		mPlaylistId = c.getColumnIndex(MediaStore.Audio.Playlists._ID);
		mPlaylistName = c.getColumnIndex(MediaStore.Audio.Playlists.NAME);
		
		mInflater = LayoutInflater.from(context);
	}
	
	
	


	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		TextView playlistname = (TextView)view.findViewById(R.id.song_title);
		
		playlistname.setText(cursor.getString(mPlaylistName));
		
		view.setId(cursor.getInt(mPlaylistId));
		
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.music_gallery_song_item, null);
	}
	
}

