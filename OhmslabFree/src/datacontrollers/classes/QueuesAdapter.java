package datacontrollers.classes;


import com.ohmfree.MusicDatabaseHelper;
import com.ohmfree.MusicUtils;
import com.ohmfree.R;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class QueuesAdapter extends CursorAdapter{
	
	
	private LayoutInflater mInflater;
	
	private int mSongName;
	private int mArtistName;
	private int mAlbumId;
	private int mPosId;
	
	private Bitmap albumart;

	public QueuesAdapter(Context context, Cursor c) {
		super(context, c);
		
		mPosId = c.getColumnIndex(MusicDatabaseHelper.COLUMN_ID); 
		mSongName = c.getColumnIndex(MusicDatabaseHelper.COLUMN_SONG_NAME);
		mArtistName = c.getColumnIndex(MusicDatabaseHelper.COLUMN_ARTIST_NAME);
		mAlbumId = c.getColumnIndex(MusicDatabaseHelper.COLUMN_ALBUM_ID);
		
		mInflater = LayoutInflater.from(context);
	}


	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView songName = (TextView)view.findViewById(R.id.songName);
		TextView artistName = (TextView)view.findViewById(R.id.artistName);
		ImageView albumArt = (ImageView)view.findViewById(R.id.drag);
		
		songName.setText(cursor.getString(mSongName));
		artistName.setText(cursor.getString(mArtistName));
		
		albumart = MusicUtils.getArtwork(context, ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), cursor.getLong(mAlbumId)));
        
        
        if(albumart != null){
        	
        	albumArt.setImageBitmap(albumart);
        }
        else{
        	albumArt.setImageResource(MusicUtils.colors[MusicUtils.randgen.nextInt(7)]);
        }
		view.setId(cursor.getInt(mPosId));
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.drag_and_drop_list_item, null);
	}
	
}

