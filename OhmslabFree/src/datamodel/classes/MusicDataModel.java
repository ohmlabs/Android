package datamodel.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.net.Uri;


public class MusicDataModel {
	
	List<Artist> allArtists = null;
	
	List<Album> allAlbums = null;
	
	HashMap<Long, Playlist> allPlaylists = null;
	
	List<Song> allSongs = null;
	
	List<String> artistList = null;
	
	List<String> albumList = null;
	
	List<Song> queue = null;
	
	List<Song> nowPlaying = null;
	
	int nowPlayingPosition = 0;
	
	HashMap<Long,Song> songHash;
	
	public MusicDataModel() {
		
		songHash = new HashMap<Long, Song>();

		allPlaylists = new HashMap<Long, Playlist>();
		
		allArtists = Collections.synchronizedList(new ArrayList<Artist>());
		
		allAlbums = Collections.synchronizedList(new ArrayList<Album>());
		
		allSongs = Collections.synchronizedList(new ArrayList<Song>());
		
		albumList = Collections.synchronizedList(new ArrayList<String>());
		
		artistList = Collections.synchronizedList(new ArrayList<String>());
		
		queue = Collections.synchronizedList(new ArrayList<Song>());
		
		nowPlaying = Collections.synchronizedList(new ArrayList<Song>());
		
	}

	public Song addSong(long songID, String songName, String albumName, long albumID, String artistName, long artistID, long duration, int track, String dataFile, int count) {
		
		int featpos = artistName.toLowerCase().indexOf("feat.");
		
		int ftpos = artistName.toLowerCase().indexOf("ft.");
		
		int featurepos = artistName.toLowerCase().indexOf("featuring");
		
		if (featpos > 0){
			songName = songName.concat(artistName.substring(featpos - 1));
			artistName = artistName.substring(0, featpos - 1);
		}
		
		else if(ftpos > 0){
			songName = songName.concat(artistName.substring(ftpos - 1));
			artistName = artistName.substring(0, ftpos - 1);
		}
		
		else if(featurepos > 0){
			songName = songName.concat(artistName.substring(featurepos - 1));
			artistName = artistName.substring(0, featurepos - 1);
		}
			
		Artist artist = new Artist(artistName, artistID);
		
		Album album = new Album(albumID, albumName, artist);
		
		Song song = new Song(songID, songName, artistName, albumName, albumID, dataFile);
		
		int artistindex = artistList.indexOf(artistName);

		if (artistindex >= 0){
			
			int albumindex = allArtists.get(artistindex).albumnames.indexOf(albumName);
			
			if (albumindex >= 0){
			
				allArtists.get(artistindex).addSong(song);
				
				allArtists.get(artistindex).albums.get(albumindex).addSong(song);
				
			}
			
			else{
				
				allArtists.get(artistindex).albumnames.add(albumName);
				
				albumList.add(albumName);
				
				album.addSong(song);
				
				allArtists.get(artistindex).addAlbum(album);
				
				allArtists.get(artistindex).addSong(song);
			
			}
		
		}
		
		else {
			
			album.addSong(song);
			
			artist.addAlbum(album);
			
			artist.addSong(song);
			
			artistList.add(artistName);
			
			allArtists.add(artist);
			
			allAlbums.add(album);
		
		}
		
		allSongs.add(song);
		
		songHash.put(songID, song);
		
		return song;
		
	}
	
	public void addPlaylist(long playlistID, String playlistName, Uri artworkuri) {
		
		Playlist playlist = new Playlist(playlistID, playlistName, artworkuri);
		
		allPlaylists.put(playlistID, playlist);
		
	}
	
	public List<Artist> getArtists() {
		
		return allArtists;
	
	}
	
	public List<String> getArtistNames(){
		
		return artistList;
		
	}
	
	public void addToQueue(Song song){
		this.queue.add(song);
	}
	
	public void addToQueue(List<Song> list){
		for(Song song: list){
			this.queue.add(song);
		}
	}
	
	public List<Album> getAlbums() {
		
		return allAlbums ;
	
	}
	
	public HashMap<Long, Playlist> getPlaylists() {
		
		return allPlaylists;
	
	}
	
	public List<Song> getSongs() {
		
		return allSongs;
	
	}
	
	public List<Song> getQueue(){
		
		return queue;
	
	}
	
	public List<Song> getNowPlaying(){
		
		return nowPlaying;
	
	}
	
	public void setNowPlaying(List<Song> nowplaying){
		
		this.nowPlaying = nowplaying;
	
	}
	
	public int getNowPlayingPosition(){
		
		return nowPlayingPosition;
	
	}
	
	public void setNowPlayingPosition(int position){
		
		this.nowPlayingPosition = position;
	
	}
	
	public HashMap<Long, Song> getSongHash(){
		return songHash;
	}
	
	public void setQueue(List<Song> queue){
		this.queue = queue;
	}
	
	
	

}