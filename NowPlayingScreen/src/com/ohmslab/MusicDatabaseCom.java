package com.ohmslab;

import java.util.ArrayList;
import java.util.List;

import datamodel.classes.Song;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

public class MusicDatabaseCom {

	private SQLiteDatabase database;
	private MusicDatabaseHelper dbHelper;
	
	private ContentResolver resolver;

	private String[] NowPlayingListColumns = { 
			MusicDatabaseHelper.COLUMN_ID,
			MusicDatabaseHelper.COLUMN_SONG_ID,
			MusicDatabaseHelper.COLUMN_SONG_NAME, 
			MusicDatabaseHelper.COLUMN_ARTIST_NAME, 
			MusicDatabaseHelper.COLUMN_ALBUM_NAME,
			MusicDatabaseHelper.COLUMN_ALBUM_ID, 
			MusicDatabaseHelper.COLUMN_DATA};
	
	private String[] NowPlayingSongColumns = { 
			MusicDatabaseHelper.COLUMN_ID,
			MusicDatabaseHelper.COLUMN_SONG_ID,
			MusicDatabaseHelper.COLUMN_SONG_NAME, 
			MusicDatabaseHelper.COLUMN_ARTIST_NAME, 
			MusicDatabaseHelper.COLUMN_ALBUM_NAME,
			MusicDatabaseHelper.COLUMN_ALBUM_ID, 
			MusicDatabaseHelper.COLUMN_DATA, 
			MusicDatabaseHelper.COLUMN_LIST_POSITION,
			MusicDatabaseHelper.COLUMN_PLAYBACK};
	
	private String[] QueueColumns = { 
			MusicDatabaseHelper.COLUMN_ID,
			MusicDatabaseHelper.COLUMN_SONG_ID,
			MusicDatabaseHelper.COLUMN_SONG_NAME, 
			MusicDatabaseHelper.COLUMN_ARTIST_NAME, 
			MusicDatabaseHelper.COLUMN_ALBUM_NAME,
			MusicDatabaseHelper.COLUMN_ALBUM_ID, 
			MusicDatabaseHelper.COLUMN_DATA};
	
	private String[] HistoryColumns = { 
			MusicDatabaseHelper.COLUMN_ID,
			MusicDatabaseHelper.COLUMN_SONG_ID,
			MusicDatabaseHelper.COLUMN_SONG_NAME, 
			MusicDatabaseHelper.COLUMN_ARTIST_NAME, 
			MusicDatabaseHelper.COLUMN_ALBUM_NAME,
			MusicDatabaseHelper.COLUMN_ALBUM_ID, 
			MusicDatabaseHelper.COLUMN_DATA, 
			MusicDatabaseHelper.COLUMN_TIMESTAMP};
	
	private String[] PlaylistArtworkColumns = { 
			MusicDatabaseHelper.COLUMN_ID,
			MusicDatabaseHelper.COLUMN_PLAYLIST_ID,
			MusicDatabaseHelper.COLUMN_ARTWORK_URI};
	
	private String[] MusicColumns = {
			MediaStore.Audio.Media._ID, 
			MediaStore.Audio.Media.ALBUM, 
			MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.ARTIST, 
			MediaStore.Audio.Media.ARTIST_ID,
			MediaStore.Audio.Media.DURATION, 
			MediaStore.Audio.Media.IS_MUSIC, 
			MediaStore.Audio.Media.TRACK, 
			MediaStore.Audio.Media.DATA, 
			MediaStore.Audio.Media.DISPLAY_NAME, 
			MediaStore.Audio.Media.TITLE};
	
	public MusicDatabaseCom(Context context) {
		dbHelper = new MusicDatabaseHelper(context);
		resolver = context.getContentResolver();
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public Cursor getMusicCursor(){
		
	return resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MusicColumns, null, null, MediaStore.Audio.Media.ARTIST + " ASC");
		
	}
	
	public Uri getPlaylistArtworkUri(long id){
		
		Cursor artworkCursor = database.query(MusicDatabaseHelper.TABLE_PLAYLIST_ARTWORK,
				PlaylistArtworkColumns, MusicDatabaseHelper.COLUMN_PLAYLIST_ID + "=" + id, null,
				null, null, null);
		Uri uri = null;
		
		if(artworkCursor.moveToFirst()){
		String artwork = artworkCursor.getString(artworkCursor.getColumnIndex(MusicDatabaseHelper.COLUMN_ARTWORK_URI));
		
		
		if(artwork!=null){
		uri = Uri.parse(artwork);
		}
		
		}
		return uri;
	}
	
	public void setPlaylistArtworkUri(long id, Uri artwork){
		
		database.delete(MusicDatabaseHelper.TABLE_PLAYLIST_ARTWORK, MusicDatabaseHelper.COLUMN_PLAYLIST_ID + " = " + id, null);
		
		if(artwork!=null){
			ContentValues values = new ContentValues();
			values.put(MusicDatabaseHelper.COLUMN_PLAYLIST_ID, id);
			values.put(MusicDatabaseHelper.COLUMN_ARTWORK_URI, artwork.toString());
		
			database.insert(MusicDatabaseHelper.TABLE_PLAYLIST_ARTWORK, null, values);
		}
	}
	
	public Cursor getPlaylistCursor(long id){
		if(id>0){
			Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
			
			return resolver.query(membersUri, null, null, null, null);
			
		}
		else{
			return resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Playlists.NAME);
		}
			
	}
	
	public void renamePlaylist(long playlistid, String newname){
		ContentValues values = new ContentValues(1);
        values.put(MediaStore.Audio.Playlists.NAME, newname);
        resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                values,
                MediaStore.Audio.Playlists._ID + "=?",
                new String[] { Long.valueOf(playlistid).toString()});
	}
	
	public long newPlaylist(String name){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.NAME, name);
        resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
        return (long)idForplaylist(name);
	}
	
	public void clearPlaylist(Context context, Long playlistId){
		ContentResolver resolver = context.getContentResolver();
	    Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
	    resolver.delete(uri, null, null);
	}
	
	public void setNowPlayingSong(Song song, int position){
		database.delete(MusicDatabaseHelper.TABLE_NOW_PLAYING_SONG, null, null);
		
		ContentValues values = new ContentValues();
		values.put(MusicDatabaseHelper.COLUMN_SONG_NAME, song.getName());
		values.put(MusicDatabaseHelper.COLUMN_SONG_ID, song.getID());
		values.put(MusicDatabaseHelper.COLUMN_ALBUM_NAME, song.getAlbumName());
		values.put(MusicDatabaseHelper.COLUMN_ARTIST_NAME, song.getArtistName());
		values.put(MusicDatabaseHelper.COLUMN_ALBUM_ID, song.getAlbumId());
		values.put(MusicDatabaseHelper.COLUMN_DATA, song.getDataFile());
		values.put(MusicDatabaseHelper.COLUMN_LIST_POSITION, position);
		
		database.insert(MusicDatabaseHelper.TABLE_NOW_PLAYING_SONG, null, values);
		
	}
	 private long idForplaylist(String name) {
	        Cursor c = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
	                new String[] { MediaStore.Audio.Playlists._ID },
	                MediaStore.Audio.Playlists.NAME + "=?",
	                new String[] { name },
	                MediaStore.Audio.Playlists.NAME);
	       	long id = -1;
	        if (c != null) {
	            c.moveToFirst();
	            if (!c.isAfterLast()) {
	                id = c.getInt(0);
	            }
	        }
	        c.close();
	        return id;
	    }
	public Song getNowPlayingSong(){
		
		Cursor cursor = database.query(MusicDatabaseHelper.TABLE_NOW_PLAYING_SONG,
				NowPlayingSongColumns, null, null,
				null, null, null);
		
		cursor.moveToFirst();
		
		Song song = new Song(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5), cursor.getString(6));
			
		return song;
	
		
	}
	
	public int getNowPlayingSongPosition(){
		
		Cursor cursor = database.query(MusicDatabaseHelper.TABLE_NOW_PLAYING_SONG,
				NowPlayingSongColumns, null, null,
				null, null, null);
		
		cursor.moveToFirst();
		
		return cursor.getInt(7);
		
	}
	
	public int getNowplayingSongPlayback(){
		
		Cursor cursor = database.query(MusicDatabaseHelper.TABLE_NOW_PLAYING_SONG,
				NowPlayingSongColumns, null, null,
				null, null, null);
		
		cursor.moveToFirst();
		
		return cursor.getInt(8);
	}
	
	public Cursor getHistoryCursor(){
		
		return database.query(MusicDatabaseHelper.TABLE_HISTORY,
				HistoryColumns, null, null, null, null, MusicDatabaseHelper.COLUMN_TIMESTAMP);
	}
	
	public void addSongToHistory(Song song){
		
		ContentValues values = new ContentValues();
		values.put(MusicDatabaseHelper.COLUMN_TIMESTAMP, System.currentTimeMillis());
		values.put(MusicDatabaseHelper.COLUMN_SONG_NAME, song.getName());
		values.put(MusicDatabaseHelper.COLUMN_SONG_ID, song.getID());
		values.put(MusicDatabaseHelper.COLUMN_ALBUM_NAME, song.getAlbumName());
		values.put(MusicDatabaseHelper.COLUMN_ARTIST_NAME, song.getArtistName());
		values.put(MusicDatabaseHelper.COLUMN_ALBUM_ID, song.getAlbumId());
		values.put(MusicDatabaseHelper.COLUMN_DATA, song.getDataFile());
		
		database.insert(MusicDatabaseHelper.TABLE_HISTORY, null,
				values);
	}
	
	public void clearHistory(){
		
		database.delete(MusicDatabaseHelper.TABLE_HISTORY, null, null);
		
	}
	
	public Cursor getQueueCursor(){
		
		
		Cursor cursor = database.query(MusicDatabaseHelper.TABLE_QUEUE,
				QueueColumns, null, null, null, null, null);
	
		return cursor;
		
	}
	
	public void addSongToQueue(Song song){
		
		ContentValues values = new ContentValues();
		values.put(MusicDatabaseHelper.COLUMN_SONG_NAME, song.getName());
		values.put(MusicDatabaseHelper.COLUMN_SONG_ID, song.getID());
		values.put(MusicDatabaseHelper.COLUMN_ALBUM_NAME, song.getAlbumName());
		values.put(MusicDatabaseHelper.COLUMN_ARTIST_NAME, song.getArtistName());
		values.put(MusicDatabaseHelper.COLUMN_ALBUM_ID, song.getAlbumId());
		values.put(MusicDatabaseHelper.COLUMN_DATA, song.getDataFile());
		
		database.insert(MusicDatabaseHelper.TABLE_QUEUE, null,
				values);
	}
	
	public void setQueue(List<Song> songlist){
		clearQueue();
		addListToQueue(songlist);
	}
	
	public void addListToQueue(final List<Song> songlist){
		
				
				for(int i = 0; i<songlist.size(); i++){
					
					ContentValues values = new ContentValues();
					values.put(MusicDatabaseHelper.COLUMN_SONG_NAME, songlist.get(i).getName());
					values.put(MusicDatabaseHelper.COLUMN_SONG_ID, songlist.get(i).getID());
					values.put(MusicDatabaseHelper.COLUMN_ALBUM_NAME, songlist.get(i).getAlbumName());
					values.put(MusicDatabaseHelper.COLUMN_ARTIST_NAME, songlist.get(i).getArtistName());
					values.put(MusicDatabaseHelper.COLUMN_ALBUM_ID, songlist.get(i).getAlbumId());
					values.put(MusicDatabaseHelper.COLUMN_DATA, songlist.get(i).getDataFile());
					
					database.insert(MusicDatabaseHelper.TABLE_QUEUE, null,
							values);
					}
		
	}
	
	public void clearQueue(){
		database.delete(MusicDatabaseHelper.TABLE_QUEUE, null, null);
	}
	
	public void clearNowPlaying(){
		database.delete(MusicDatabaseHelper.TABLE_NOW_PLAYING_LIST, null, null);
	}
	
	public void addListToNowPlaying(List<Song> songlist){
		for(Song song: songlist){
			
			ContentValues values = new ContentValues();
			values.put(MusicDatabaseHelper.COLUMN_SONG_NAME, song.getName());
			values.put(MusicDatabaseHelper.COLUMN_SONG_ID, song.getID());
			values.put(MusicDatabaseHelper.COLUMN_ALBUM_NAME, song.getAlbumName());
			values.put(MusicDatabaseHelper.COLUMN_ARTIST_NAME, song.getArtistName());
			values.put(MusicDatabaseHelper.COLUMN_ALBUM_ID, song.getAlbumId());
			values.put(MusicDatabaseHelper.COLUMN_DATA, song.getDataFile());
			
			database.insert(MusicDatabaseHelper.TABLE_NOW_PLAYING_LIST, null,
					values);
			}
	}
	
	public void setNowPlaying(List<Song> songlist){
		clearNowPlaying();
		addListToNowPlaying(songlist);
	}
	
	public List<Song> getNowPlaying(){
		
		List<Song> nowPlaying = new ArrayList<Song>();
		
		Cursor cursor = database.query(MusicDatabaseHelper.TABLE_NOW_PLAYING_LIST,
				NowPlayingListColumns, null, null,
				null, null, null);
		
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			Song dbsong = new Song(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5), cursor.getString(6));
			nowPlaying.add(dbsong);
			cursor.moveToNext();
		}
		
		cursor.close();
		return nowPlaying;
		
		
	}
	
	public List<Song> getQueue(){
		List<Song> queue = new ArrayList<Song>();
		
		Cursor cursor = database.query(MusicDatabaseHelper.TABLE_QUEUE,
				QueueColumns, null, null,
				null, null, null);
		
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			Song dbsong = new Song(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5), cursor.getString(6));
			queue.add(dbsong);
			cursor.moveToNext();
		}
		
		cursor.close();
		return queue;
		
	}
	
	public void clearPlaylist(Context context, long plid) {
	    
	    Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", plid);
	    context.getContentResolver().delete(uri, null, null);
	    
	}
	
	 public void addListToPlaylist(Context context, List<Song> songlist, long playlistId){
		   String[] cols = new String[] {
	                "count(*)"
	        };
	        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
	        Cursor cur = context.getContentResolver().query(uri, cols, null, null, null);
	        cur.moveToFirst();
	        final int base = cur.getInt(0);
	        cur.close();
	        for(Song song: songlist){
	        	long audioId = song.getID();
	        	ContentValues values = new ContentValues();
		        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, (base + audioId));
		        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
		        context.getContentResolver().insert(uri, values);
	        }
	       
	   }
	 
	 public void deletePlaylist(Context context, long playlistId){
		 Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, playlistId);
         context.getContentResolver().delete(uri, null, null);
         Toast.makeText(context, "Playlist Deleted", Toast.LENGTH_SHORT).show();
	 }
 
	 public void addSongToPlaylist(long audioId, long playlistId) {

	        String[] cols = new String[] {
	                "count(*)"
	        };
	        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
	        Cursor cur = resolver.query(uri, cols, null, null, null);
	        cur.moveToFirst();
	        final int base = cur.getInt(0);
	        cur.close();
	        ContentValues values = new ContentValues();
	        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, (base + audioId));
	        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
	        resolver.insert(uri, values);
	    }
	
}
