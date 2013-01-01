package com.ohmfree;


import java.util.List;



import datamodel.classes.Album;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MusicLibraryAlbums extends Activity {
	
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.music_library_albums);
        
		final ApplicationObject ohm = (ApplicationObject)getApplicationContext();

		ListAdapter adapter = new AlbumListAdapter(this, ohm.getModel().getAlbums());
        
		ListView albumlistv = (ListView)findViewById(R.id.list);
        
		albumlistv.setAdapter(adapter);
		
		albumlistv.setSmoothScrollbarEnabled(true);
        
		albumlistv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
				ohm.albumposition = position;
				
				ohm.artistposition = ohm.getModel().getArtistNames().indexOf(ohm.getModel().getAlbums().get(position).getArtist().getName());
				
				Intent intent = new Intent(MusicLibraryAlbums.this, MusicGallery.class);
				startActivity(intent);

				
			}
        
		});
        
		albumlistv.setSelection(ohm.albumposition);
		
	}
	
	static class ViewHolder{
		TextView artist_name;
		TextView album_name;
		ImageView album_art;
	}
public class AlbumListAdapter extends BaseAdapter{
	 
    private List<Album> allAlbums;
 
    private Context context;
    
    Bitmap albumart;
    
    public AlbumListAdapter(Context context, List<Album> allAlbums1) {
    	
    	this.context = context;
    	
    	this.allAlbums = allAlbums1;
    	
    
    }
 
    public int getCount() {
        
    	return allAlbums.size();
    
    }
 
    public Album getItem(int position) {
        
    	return allAlbums.get(position);
    
    }
    
	public long getItemId(int position) {
		
		return allAlbums.get(position).getID();
		
	}
 
    public View getView(int position, View convertView, ViewGroup parent) {
    	albumart = null;
    	ViewHolder holder;
        if(convertView == null){
        	convertView = LayoutInflater.from(context).inflate(R.layout.music_library_album_item, parent,false);
        	holder = new ViewHolder();
        	holder.artist_name = (TextView)convertView.findViewById(R.id.albumArtist);
        	holder.album_name = (TextView)convertView.findViewById(R.id.albumTitle);
        	holder.album_art = (ImageView)convertView.findViewById(R.id.albumArt);
        	
        	convertView.setTag(holder);
        }
        else{
        	
        	holder = (ViewHolder)convertView.getTag();
        }
        
        
        holder.album_name.setText(allAlbums.get(position).getName());

        holder.artist_name.setText(allAlbums.get(position).getArtist().getName());
        
        albumart = MusicUtils.getArtwork(context, allAlbums.get(position).getArtworkUri());
        
        if(albumart != null){
        	
        	holder.album_art.setImageBitmap(albumart);
        }
        else{
        	holder.album_art.setImageResource(MusicUtils.colors[position%7]);
        }
        
       
        return convertView;
    }

    
       
 
}
}
