package datamodel.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.net.Uri;

public class Playlist {
	
	long playlistID;
	
	String playlistName;
	
	List<Song> songs;
	
	Uri artworkuri = null;
	
	public Playlist(long playlistID, String playlistName, Uri artworkuri) {
		
		this.playlistID = playlistID;
		
		//this.artworkuri = artworkuri;
		
		this.playlistName = playlistName;
		
		songs = Collections.synchronizedList(new ArrayList<Song>());
	
	}
	
	public long getID() {
		
		return playlistID;
	
	}
	
	public String getName() {
		
		return playlistName;
	
	}
	
	public void setName(String playlistName) {
		
		this.playlistName = playlistName;
	
	}
	
	public void addSong(Song song) {
		
		songs.add(song);
	
	}
	
	public List<Song> getSongs() {
		
		return songs;
	
	}

	public int size() {
		
		return songs.size();
		
	}
	
	public void setUri(Uri uri){
		this.artworkuri = uri;
	}
	
	public Uri getUri(){
		return this.artworkuri;
	}
	
}