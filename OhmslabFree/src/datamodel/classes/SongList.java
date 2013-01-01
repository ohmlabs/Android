package datamodel.classes;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class SongList implements Parcelable{

	List<Song> songlist;
	ClassLoader loader;
	
	public SongList(List<Song> songs){
		songlist = songs;
		loader = Song.class.getClassLoader();
	}
	public SongList(Parcel in){
		loader = Song.class.getClassLoader();
		readFromParcel(in);
	}
	
	public List<Song> getList(){
		return songlist;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(songlist);
		
	}
	public static final Parcelable.Creator<SongList> CREATOR = new Parcelable.Creator<SongList>() {
        public SongList createFromParcel(Parcel source) {
        	
        	return new SongList(source);
        }

        public SongList[] newArray(int size) {
            throw new UnsupportedOperationException();
        }

    };

	public void readFromParcel(Parcel in) {
		songlist = new ArrayList<Song>();
		in.readList(songlist, loader);
		
	}

}
