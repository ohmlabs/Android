package datamodel.classes;



import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable{
	
	long songID;
	
	String songName;
	
	String artistName;
	
	String albumName;
	
	long albumID;
	
	String dataFile;
	
	Uri artworkuri;
	
	public Song(long songID, String songName, String artistName, String albumName, long albumID, String dataFile) {
		
		this.setID(songID);
		
		this.setName(songName);
		
		this.setArtistName(artistName);
		
		this.setAlbumName(albumName);
		
		this.setDataFile(dataFile);
		
		this.setAlbumId(albumID);
		
		this.setArtworkUri(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumID));
      
	
	}
	
	public Song(Parcel in){
		readFromParcel(in);
	}
	
	public void setID(long songId){
		
		this.songID = songId;
	
	}
	
	public long getID() {
		
		return songID;
	
	}
	
	public String getName() {
		
		return songName;
	
	}
	
	public void setName(String songName) {
		this.songName = songName;
	}

	public void setDataFile(String dataFile) {
		
		this.dataFile = dataFile;
	
	}

	public String getDataFile() {
		
		return dataFile;
	
	}

	public void setArtistName(String ArtistName) {
		
		this.artistName = ArtistName;
	
	}

	public String getArtistName() {
		
		return artistName;
	
	}

	public void setAlbumName(String AlbumName) {
		
		this.albumName = AlbumName;
	
	}

	public String getAlbumName() {
		
		return albumName;
	
	}

	public void setArtworkUri(Uri artworkuri) {
		
		this.artworkuri = artworkuri;
	
	}

	public Uri getArtworkUri() {
		
		return artworkuri;
	
	}
	
	public long getAlbumId(){
		
		return this.albumID;
	
	}
	
	public void setAlbumId(long albumID){
		
		this.albumID = albumID;
	
	}
	public void readFromParcel(Parcel in) {
		//in.readParcelable(Song.class.getClassLoader());
		this.setID(in.readLong());
		this.setName(in.readString());	
		this.setArtistName(in.readString());
		this.setAlbumName(in.readString());
		long albumid = in.readLong();
		this.setDataFile(in.readString());
		this.setAlbumId(albumid);
		this.setArtworkUri(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumid));
	
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(songID);
		dest.writeString(songName);
		dest.writeString(artistName);
		dest.writeString(albumName);
		dest.writeLong(albumID);
		dest.writeString(dataFile);
		
	}
	public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        public Song createFromParcel(Parcel source) {
        	long id = source.readLong();
    		String songname = source.readString();
    		String artistname = source.readString();
    		String albumname = source.readString();
    		long albumid = source.readLong();
    		String datafile = source.readString();
            return new Song(id, songname, artistname, albumname, albumid, datafile);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }

    };



	
}