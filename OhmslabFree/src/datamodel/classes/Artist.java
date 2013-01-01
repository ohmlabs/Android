package datamodel.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Artist {
	
	long artistID;
	
	String artistName;
	
	List<Album> albums = null;
	
	List<Song> allSongs = null;
	
	List<String> albumnames = null;
	
	public Artist(String artistName, long artistID) {
		
		List<Album> albumslist = Collections.synchronizedList(new ArrayList<Album>());
		
		List<Song> songslist = Collections.synchronizedList(new ArrayList<Song>());
		
		List<String> albumnameslist = Collections.synchronizedList(new ArrayList<String>());
		
		this.setAlbums(albumslist);
		
		this.setSongs(songslist);
		
		this.setName(artistName);
		
		this.setID(artistID);
		
		this.setAlbumNames(albumnameslist);
	
	}
	
	public long getID() {
		
		return artistID;
	
	}
	
	public void setID(long artistID){
		
		this.artistID = artistID;
	
	}
	
	public String getName() {
		
		return artistName;
	
	}
	
	public void setName(String artistName) {
		
		this.artistName = artistName;
	
	}
	
	public void addAlbum(Album album) {
		
		albums.add(album);
	}
	
	public void addSong(Song song) {
		
		allSongs.add(song);
	
	}
	
	public List<Album> getAlbums() {
		
		return albums;
	
	}
	
	public void setAlbums(List<Album> albumslist){
		
		this.albums = albumslist;
	
	}
	
	public void setSongs(List<Song> songslist){
		
		this.allSongs = songslist;
	
	}
	
	public List<Song> getSongs() {
		
		return allSongs;
	
	}
	
	public void setAlbumNames(List<String> albumnames){
		
		this.albumnames = albumnames;
	
	}
	
	public List<String> getAlbumNames(){
		
		return albumnames;
	
	}
	
	public void addAlbumName(String albumName){
		
		this.albumnames.add(albumName);
	
	}

}