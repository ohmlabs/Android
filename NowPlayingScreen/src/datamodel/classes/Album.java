package datamodel.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentUris;
import android.net.Uri;

public class Album {
	
	long albumID;
	
	String albumName;
	
	Artist artist;
	
	List<Song> songs;
	
	Uri artworkUri;
	
	public Album(long albumID, String albumName, Artist artist) {
		
		List <Song> songs = Collections.synchronizedList(new ArrayList<Song>());
		
		this.setID(albumID);
		
		this.setName(albumName);
		
		this.setArtist(artist);
		
		this.setSongsList(songs);
		
		this.setArtworkUri(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumID));
	
	}
	
	public long getID() {
		
		return albumID;
	
	}
	public void setID(long albumID){
		
		this.albumID = albumID;
	
	}
	
	public String getName() {
		
		return albumName;
	
	}
	
	public void setName(String albumName) {
		
		this.albumName = albumName;
	
	}
	
	public Artist getArtist() {
		
		return artist;
	
	}
	
	public void setArtist(Artist artist){
		
		this.artist = artist;
	
	}
	
	public void addSong(Song song) {
		
		songs.add(song);
	
	}
	public void setSongsList(List<Song> songslist){
		
		this.songs = songslist;
		
	}
	public List<Song> getSongs() {
		
		return songs;
	
	}

	public void setArtworkUri(Uri artworkuri) {
		
		this.artworkUri = artworkuri;
	
	}

	public Uri getArtworkUri() {
		
		return artworkUri;
	
	}
	
}