package com.ohmfree;

import java.util.List;

import com.google.gdata.data.youtube.VideoEntry;
import datacontrollers.classes.YoutubeAdapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class YoutubeDialog extends Dialog{

	List<VideoEntry> searchlist;
	String searchquery = null;
	ListView youtubelist = null;
	EditText querytext = null;
	YoutubeAdapter adapter = null;
	Button cancel_button;
	
	YoutubeDialogListener listener = null;
	
	public YoutubeDialog(Context context, String query, List<VideoEntry> list) {
		super(context, R.style.dialog_theme);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_youtube);
		
		this.searchlist = list;
		searchquery = query.trim().replace(" ", "+");
		searchquery = "http://gdata.youtube.com/feeds/api/videos?q=" + searchquery +"&alt=json&max-results=10&v=2";

		
		buildDialog(query);
		
	}
	private void buildDialog(String query){
		querytext = (EditText)findViewById(R.id.searchQuery);
		querytext.setText(query);
		
		cancel_button = (Button)findViewById(R.id.cancelButton);
		cancel_button.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dismiss();
				
			}

			
			
		});
		youtubelist = (ListView)findViewById(R.id.videoList);
		
		youtubelist.setAdapter(adapter);
		
		youtubelist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				listener.onItemClicked(position);
				dismiss();
			}
			
		});

	}
	
	public void setYoutubeDialogListener(YoutubeDialogListener dlistener){
		this.listener = dlistener;
	}
	


}
