package datacontrollers.classes;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.youtube.VideoEntry;
import com.ohmslab.R;


import datacontrollers.classes.QueueAdapter.ViewHolder;
import datamodel.classes.Song;
import datamodel.classes.YoutubeVideo;

public class YoutubeAdapter extends BaseAdapter{
	
	private List<VideoEntry> searchlist;
	private List<Bitmap> thumbslist;
	Bitmap thumb;
	Context context;
	
	public YoutubeAdapter(Context context, List<VideoEntry> list, List<Bitmap> thumbs){
		this.context = context;
		this.searchlist = list;
		this.thumbslist = thumbs;
	}
	@Override
	public int getCount() {
		return searchlist.size();
	}

	@Override
	public Object getItem(int position) {
		return searchlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		thumb = null;
    	ViewHolder holder;
        if(convertView == null){
        	convertView = LayoutInflater.from(context).inflate(R.layout.youtube_item, parent,false);
        	holder = new ViewHolder();
        	holder.video_title = (TextView)convertView.findViewById(R.id.videoTitle);
        	holder.channel_name = (TextView)convertView.findViewById(R.id.channelName);
        	holder.view_count = (TextView)convertView.findViewById(R.id.viewCount);
        	holder.thumb_nail = (ImageView)convertView.findViewById(R.id.thumbNail);
        	
        	convertView.setTag(holder);
        }
        else{
        	
        	holder = (ViewHolder)convertView.getTag();
        }
        
        holder.video_title.setText(searchlist.get(position).getTitle().getPlainText());

        holder.channel_name.setText(searchlist.get(position).getMediaGroup().getUploader());
        
        holder.view_count.setText(searchlist.get(position).getStatistics().getViewCount() + " Views");
        
        holder.thumb_nail.setImageBitmap(thumbslist.get(position));
 
        return convertView;
	}
	
	
	static class ViewHolder{
		TextView channel_name;
		TextView video_title;
		TextView view_count;
		ImageView thumb_nail;
	}
	
}
