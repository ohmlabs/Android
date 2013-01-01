package com.ohmfree;

import java.util.List;

import com.ohmfree.MusicUtils.ServiceToken;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import datamodel.classes.Song;

public class MusicLibrarySongs extends Activity {
	
		ApplicationObject ohm;
		
		ServiceToken token = null;
		
	@Override
	public void onStop(){
			super.onStop();
			MusicUtils.unbindFromService(token);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		ohm = (ApplicationObject) getApplicationContext();
		
		setContentView(R.layout.music_library_songs);
		
		token = MusicUtils.bindToService(this, ohm.serviceConn);
		
		ListAdapter adapter = new SongListAdapter(this, ohm.getModel().getSongs());
        
		ListView songlistv = (ListView)findViewById(R.id.list);
        
		songlistv.setAdapter(adapter);
        
		songlistv.setSmoothScrollbarEnabled(true);
		
		songlistv.setFastScrollEnabled(true);
		
		songlistv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				ohm.setNowPlaying(ohm.getModel().getSongs(), position);
				
				Intent intent = new Intent(MusicLibrarySongs.this, NowPlayingScreen.class);
				startActivity(intent);

			}

        });
        
		songlistv.setSelection(ohm.songposition);
		
	}
	
	static class ViewHolder{
		TextView artist_name;
		TextView album_name;
		TextView song_name;
	}
public class SongListAdapter extends BaseAdapter{
	
    private List<Song> allSongList;
 
    private Context context;
    
    public SongListAdapter(Context context, List<Song> allSongList) {
    	
    	this.context = context;
    	
    	this.allSongList = allSongList;
    	
    
    }
 
    public int getCount() {
        
    	return allSongList.size();
    
    }
 
    public Song getItem(int position) {
        
    	return allSongList.get(position);
    
    }
    
	public long getItemId(int position) {
		
		return allSongList.get(position).getID();
		
	}
 
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;
        if(convertView == null){
        	convertView = LayoutInflater.from(context).inflate(R.layout.music_library_song_item, parent,false);
        	holder = new ViewHolder();
        	holder.artist_name = (TextView)convertView.findViewById(R.id.artistName);
        	holder.album_name = (TextView)convertView.findViewById(R.id.albumName);
        	holder.song_name = (TextView)convertView.findViewById(R.id.songTitle);
        	
        	convertView.setTag(holder);
        }
        else{
        	holder = (ViewHolder)convertView.getTag();
        }
        
        
        holder.song_name.setText(allSongList.get(position).getName());

        holder.album_name.setText(allSongList.get(position).getAlbumName());
        
        holder.artist_name.setText(allSongList.get(position).getArtistName());

 
        return convertView;
    
    }

	


 
}

}