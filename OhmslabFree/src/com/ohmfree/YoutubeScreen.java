package com.ohmfree;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;


import datacontrollers.classes.PostDialogListener;
import datacontrollers.classes.YoutubeAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class YoutubeScreen extends Activity{

	ApplicationObject ohm = null;
	ListView youtubelist = null;
	EditText querytext = null;
	YoutubeAdapter adapter = null;
	ImageView searchbutton;
	List<VideoEntry> searchlist;
	List<Bitmap> videothumbs;
	String searchquery = null;
	ProgressDialog searchDialog;
	Handler handler;
	YouTubeService service;
	String queryTextContents;
	

   VideoEntry facebook_post;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube_screen);
		ohm = (ApplicationObject)getApplicationContext();
		

		buildUI();
		searchYoutube();
		
	}
	
	private void buildUI(){
		service = new YouTubeService("Ohm_Youtube_Service");
		searchquery = ohm.np_list.get(ohm.np_position).getName() + " by " + ohm.np_list.get(ohm.np_position).getArtistName();
		searchquery ="http://gdata.youtube.com/feeds/api/videos?q=" + searchquery.trim().replace(" ", "+") + "&max-results=10&v=2";
		
		handler = new Handler();
		
		querytext = (EditText)findViewById(R.id.searchQuery);
		querytext.setHint("Search Youtube");
		querytext.setText(ohm.np_list.get(ohm.np_position).getName() + " by " + ohm.np_list.get(ohm.np_position).getArtistName());
		
		searchbutton = (ImageView)findViewById(R.id.searchButton);
		searchbutton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				searchYoutube();
				
			}
			
		});
		
		youtubelist = (ListView)findViewById(R.id.videoList);
		
		youtubelist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				setFacebookLink(position);
				facebookPost();
				
			}
			
		});
		
	}
	
	private void fillList(){
		adapter = new YoutubeAdapter(this, searchlist, videothumbs);
		youtubelist.setAdapter(adapter);
	}
	
	private void searchYoutube(){
		searchquery ="http://gdata.youtube.com/feeds/api/videos?q=" + querytext.getText().toString().trim().replace(" ", "+") + "&max-results=10&v=2";
		
		searchDialog = ProgressDialog.show(YoutubeScreen.this, "Searching Youtube", querytext.getText().toString(), true);
		
		videothumbs = null;
		videothumbs = new ArrayList<Bitmap>();
		searchlist = null;
		searchlist = new ArrayList<VideoEntry>();
		   
		   final Runnable dismissProgress = new Runnable(){

				@Override
				public void run() {
					searchDialog.dismiss();
					fillList();
				}
	    		
	    	};
	    	Runnable search = new Runnable() {
				
				@Override
				public void run() {
			
					try {
						VideoFeed videoFeed = service.getFeed(new URL(searchquery), VideoFeed.class);
						for(VideoEntry videoEntry : videoFeed.getEntries() ) {
							searchlist.add(videoEntry);
							videothumbs.add(getBitmap(videoEntry.getMediaGroup().getThumbnails().get(0).getUrl()));
						  }
					}
						catch (Exception e) {
						     Log.e("Exception", "exception", e);                    
						}
					
					handler.post(dismissProgress);
				}
				
			
			};
			
			new Thread(search).start();
		
		
		
	}
	private void setFacebookLink(int position){
		facebook_post = null;
		facebook_post = searchlist.get(position);
	}
	
	private void facebookPost(){
		Bundle params = new Bundle();
        params.putString("link", facebook_post.getMediaGroup().getYouTubeContents().get(0).getUrl());
        params.putString("picture", facebook_post.getMediaGroup().getThumbnails().get(0).getUrl());
        params.putString("caption", facebook_post.getMediaGroup().getUploader());
        params.putString("name", facebook_post.getTitle().getPlainText());
        if(facebook_post.getMediaGroup().getDescription().getPlainTextContent().length() > 50)
        	params.putString("description", facebook_post.getMediaGroup().getDescription().getPlainTextContent().substring(0, 50));
        else
        	params.putString("description", facebook_post.getMediaGroup().getDescription().getPlainTextContent());
        ohm.facebook.dialog(this, "feed",params, new PostDialogListener(){
        	@Override
        	public void onComplete(Bundle value){
        	}
        	
        	@Override
        	public void onCancel(){
        		
        	}
        });
	}
	
	private Bitmap getBitmap(String url){
		try {
			return BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
