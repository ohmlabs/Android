package com.ohmfree;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDatabaseHelper extends SQLiteOpenHelper{
	public static final String TABLE_QUEUE = "queue";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SONG_ID = "song_id";
	public static final String COLUMN_SONG_NAME = "song_name";
	public static final String COLUMN_ARTIST_NAME = "artist_name";
	public static final String COLUMN_ALBUM_NAME = "album_name";
	public static final String COLUMN_ALBUM_ID = "album_id";
	public static final String COLUMN_DATA = "data";
	
	public static final String TABLE_HISTORY = "history";	
	public static final String COLUMN_TIMESTAMP = "timestamp";
	
	public static final String TABLE_NOW_PLAYING_LIST = "now_playing_list";
	
	public static final String TABLE_NOW_PLAYING_SONG = "now_playing_song";
	public static final String COLUMN_PLAYBACK = "playback";
	public static final String COLUMN_LIST_POSITION = "list_position";
	
	public static final String TABLE_PLAYLIST_ARTWORK ="playlists_artwork";
	public static final String COLUMN_PLAYLIST_ID = "playlist_id";
	public static final String COLUMN_ARTWORK_URI = "artwork";
	
	private static final String DATABASE_NAME = "OhmMusic3.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE_QUEUE = "create table "
		+ TABLE_QUEUE 
		+ "( " 
		+ COLUMN_ID
		+ " integer primary key autoincrement, " 
		+ COLUMN_SONG_ID
		+ " integer, "
		+ COLUMN_SONG_NAME
		+ " text not null, "
		+ COLUMN_ARTIST_NAME 
		+ " text not null, " 
		+ COLUMN_ALBUM_NAME
		+ " text not null, "
		+ COLUMN_ALBUM_ID 
		+ " integer, " 
		+ COLUMN_DATA
		+" text not null"
		+ ");";
	
	private static final String DATABASE_CREATE_HISTORY = "create table "
		+ TABLE_HISTORY 
		+ "( " 
		+ COLUMN_ID
		+ " integer primary key autoincrement, " 
		+ COLUMN_SONG_ID
		+ " integer, "
		+ COLUMN_SONG_NAME
		+ " text not null, "
		+ COLUMN_ARTIST_NAME 
		+ " text not null, " 
		+ COLUMN_ALBUM_NAME
		+ " text not null, "
		+ COLUMN_ALBUM_ID 
		+ " integer, " 
		+ COLUMN_DATA
		+" text not null, "
		+ COLUMN_TIMESTAMP
		+ " integer"
		+ ");";
	
	private static final String DATABASE_CREATE_NOW_PLAYING_LIST = "create table "
		+ TABLE_NOW_PLAYING_LIST 
		+ "( " 
		+ COLUMN_ID
		+ " integer primary key autoincrement, " 
		+ COLUMN_SONG_ID
		+ " integer, "
		+ COLUMN_SONG_NAME
		+ " text not null, "
		+ COLUMN_ARTIST_NAME 
		+ " text not null, " 
		+ COLUMN_ALBUM_NAME
		+ " text not null, "
		+ COLUMN_ALBUM_ID 
		+ " integer, " 
		+ COLUMN_DATA
		+" text not null"
		+ ");";
	
	private static final String DATABASE_CREATE_NOW_PLAYING_SONG = "create table "
		+ TABLE_NOW_PLAYING_SONG
		+ "( " 
		+ COLUMN_ID
		+ " integer primary key autoincrement, " 
		+ COLUMN_SONG_ID
		+ " integer, "
		+ COLUMN_SONG_NAME
		+ " text not null, "
		+ COLUMN_ARTIST_NAME 
		+ " text not null, " 
		+ COLUMN_ALBUM_NAME
		+ " text not null, "
		+ COLUMN_ALBUM_ID 
		+ " integer, " 
		+ COLUMN_DATA
		+" text not null, "
		+ COLUMN_LIST_POSITION
		+" integer, "
		+ COLUMN_PLAYBACK
		+" integer"
		+ ");";
	
	private static final String DATABASE_CREATE_PLAYLIST_ARTWORK = "create table "
		+ TABLE_PLAYLIST_ARTWORK 
		+ "( " 
		+ COLUMN_ID
		+ " integer primary key autoincrement, " 
		+ COLUMN_PLAYLIST_ID
		+ " integer, "
		+ COLUMN_ARTWORK_URI
		+ " text not null "
		+ ");";
	
	public MusicDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_QUEUE);
		db.execSQL(DATABASE_CREATE_HISTORY);
		db.execSQL(DATABASE_CREATE_NOW_PLAYING_LIST);
		db.execSQL(DATABASE_CREATE_NOW_PLAYING_SONG);
		db.execSQL(DATABASE_CREATE_PLAYLIST_ARTWORK);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_QUEUE);
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_HISTORY);
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_NOW_PLAYING_LIST);
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_NOW_PLAYING_SONG);
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_PLAYLIST_ARTWORK);
		onCreate(db);
		
	}
	
	

}
