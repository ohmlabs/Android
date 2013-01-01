package datamodel.classes;


import android.graphics.Bitmap;

import com.google.gdata.data.youtube.VideoEntry;

public class YoutubeVideo {
	
	VideoEntry vEntry;
	Bitmap vBitmap;
	
	public YoutubeVideo(VideoEntry entry, Bitmap bitmap){
		setVideoEntry(entry);
	}
	
	public void setVideoEntry(VideoEntry entry){
		this.vEntry = entry;
	}
	
	public VideoEntry getVideoEntry(){
		return this.vEntry;
	}
	
	public void setBitmap(Bitmap bitmap){
		this.vBitmap = bitmap;
	}
	
	public Bitmap getBitmap(){
		return this.vBitmap;
	}
	
}
