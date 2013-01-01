package datacontrollers.classes;

import java.util.List;

import com.ohmslab.MusicUtils;
import com.ohmslab.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import datamodel.classes.Song;

public class QueueAdapter extends BaseAdapter{
	
	private List<Song> queue;
	int selectionPosition = -1;
	Bitmap albumart;
	Context context;
	
	public QueueAdapter(Context context, List<Song> queue, int position){
		this.context = context;
		this.queue = queue;
		this.selectionPosition = position;
	}
	@Override
	public int getCount() {
		return queue.size();
	}

	@Override
	public Object getItem(int position) {
		return queue.get(position);
	}

	@Override
	public long getItemId(int position) {
		return queue.get(position).getID();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		albumart = null;
    	ViewHolder holder;
        if(convertView == null){
        	convertView = LayoutInflater.from(context).inflate(R.layout.playlists_song_item, parent,false);
        	holder = new ViewHolder();
        	holder.artist_name = (TextView)convertView.findViewById(R.id.artistName);
        	holder.song_name = (TextView)convertView.findViewById(R.id.songName);
        	holder.album_art = (ImageView)convertView.findViewById(R.id.albumArt);
        	
        	convertView.setTag(holder);
        }
        else{
        	
        	holder = (ViewHolder)convertView.getTag();
        }
        
        
        holder.song_name.setText(queue.get(position).getName());

        holder.artist_name.setText(queue.get(position).getArtistName());
        
        albumart = MusicUtils.getArtwork(context, queue.get(position).getArtworkUri());
        
        if(albumart != null){
        	
        	holder.album_art.setImageBitmap(albumart);
        }
        else{
        	holder.album_art.setImageResource(MusicUtils.colors[position%7]);
        }
        
       
        return convertView;
	}
	
	public void clearQueue() {
		queue.clear();
	}
	static class ViewHolder{
		TextView artist_name;
		TextView song_name;
		ImageView album_art;
	}
	
}
 