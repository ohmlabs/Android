package com.ohmfree;


import java.util.List;


import datamodel.classes.Artist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MusicLibraryArtists extends Activity {
	
	ApplicationObject ohm = null;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		ohm = (ApplicationObject)getApplicationContext();
		
		setContentView(R.layout.music_library_artists);
		
		ListAdapter adapter = new ArtistListAdapter(this, ohm.getModel().getArtists());
        
		ListView artistlistv = (ListView)findViewById(R.id.list);
        
		artistlistv.setAdapter(adapter);
        
		artistlistv.setSmoothScrollbarEnabled(true);
		
		artistlistv.setFastScrollEnabled(true);
		
		artistlistv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				ohm.artistposition = position;
				
				Intent intent = new Intent(MusicLibraryArtists.this, MusicGallery.class);
				startActivity(intent);

			}

        });
        
		artistlistv.setSelection(ohm.artistposition);
		
	}
        
static class ViewHolder{
		TextView artist_name;
		TextView album_count;
	}
public class ArtistListAdapter extends BaseAdapter{
	
    private List<Artist> allArtistList;
 
    private Context context;
    
    public String[] artist_az ;
 
    public ArtistListAdapter(Context context, List<Artist> allArtistList) {
    	
    	this.context = context;
    	
    	this.allArtistList = allArtistList;
    
    }
 
    public int getCount() {
        
    	return allArtistList.size();
    
    }
 
    public Artist getItem(int position) {
        
    	return allArtistList.get(position);
    
    }
    
	public long getItemId(int position) {
		
		return allArtistList.get(position).getID();
		
	}
 
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;
        if(convertView == null){
        	convertView = LayoutInflater.from(context).inflate(R.layout.music_library_artist_item, parent,false);
        	holder = new ViewHolder();
        	holder.artist_name = (TextView)convertView.findViewById(R.id.artistName);
        	holder.album_count = (TextView)convertView.findViewById(R.id.albumsNumber);
        	
        	convertView.setTag(holder);
        }
        else{
        	holder = (ViewHolder)convertView.getTag();
        }
        
        
        holder.artist_name.setText(allArtistList.get(position).getName());

        holder.album_count.setText(Integer.toString(allArtistList.get(position).getAlbums().size()) + " albums");
 
        return convertView;
    
    }

	
}

}