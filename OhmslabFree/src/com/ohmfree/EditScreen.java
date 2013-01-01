package com.ohmfree;


import java.util.List;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ohmslab.dragndrop.DragListener;
import com.ohmslab.dragndrop.DragNDropListView;
import com.ohmslab.dragndrop.DropListener;
import com.ohmslab.dragndrop.RemoveListener;

import datamodel.classes.Song;

public class EditScreen extends Activity {
	
	ApplicationObject ohm;
	EditListAdapter edit_list_adapter = null;
	DragNDropListView listView = null;
	TextView edit_list_name = null;
	EditText playlist_name = null;
	Button doneButton = null;
	Button undoButton = null;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initScreen();
	}
	
	private void initScreen(){
		ohm = (ApplicationObject)getApplicationContext();
		setContentView(R.layout.edit_screen);
		
		edit_list_adapter = new EditListAdapter(this, ohm.edit_list);
		listView = (DragNDropListView)findViewById(R.id.list);
		listView.setAdapter(edit_list_adapter);
		edit_list_name = (TextView)findViewById(R.id.edit_list_name);
		doneButton = (Button)findViewById(R.id.doneButton);
		undoButton = (Button)findViewById(R.id.undoButton);
		
		((DragNDropListView) listView).setDropListener(mDropListener);
		((DragNDropListView) listView).setRemoveListener(mRemoveListener);
		((DragNDropListView) listView).setDragListener(mDragListener);
		
		listView.isEdit = true;
		edit_list_name.setText(ohm.edit_list_name);
		
		doneButton.setOnClickListener(new Button.OnClickListener() { 
			
        	public void onClick (View v){
        		if(ohm.edit_list_name == "Queue"){
        			ohm.data_progress = ProgressDialog.show(EditScreen.this, "Saving Changes to Queue...", "Finished Editting Queue", true);
        			ohm.saveQueue(ohm.edit_list);
        		}
        		else{
        			ohm.data_progress = ProgressDialog.show(EditScreen.this, "Saving Changes to "+ ohm.edit_list_name + "...", "Finished Editting " + ohm.edit_list_name, true);
        			ohm.savePlaylist(ohm.current_playlist_id, getApplicationContext(), ohm.edit_list);
        		}
        		doneNavigation();
        		
        	};
        });
		
		undoButton.setOnClickListener(new Button.OnClickListener() { 
			
        	public void onClick (View v){
        		Toast toast = Toast.makeText(getApplicationContext(),"Undo Feature Coming Soon", Toast.LENGTH_SHORT);
        		toast.show();
        	};
        });
		
		Button deleteButton = (Button)findViewById(R.id.deleteButton);
		if(ohm.queueEditing)
			deleteButton.setVisibility(View.GONE);
		deleteButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				confirmDeleteDialog();
				
			}
			
		});
		Button clearButton = (Button)findViewById(R.id.clearButton);
		clearButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				confirmClearDialog();
				
			}
			
		});
		Button renameButton = (Button)findViewById(R.id.renameButton);
		if(ohm.queueEditing)
			renameButton.setVisibility(View.GONE);
		renameButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				renamePlaylistDialog();
				
			}
			
		});
		
	}
	private void renamePlaylistDialog(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	playlist_name = new EditText(this);
        alert.setView(playlist_name);
        alert.setTitle("Rename " + edit_list_name.getText().toString());
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	String value = playlist_name.getText().toString().trim();
            	Toast.makeText(getApplicationContext(), "Renamed " + value, Toast.LENGTH_SHORT).show();
                ohm.renamePlaylist(ohm.current_playlist_id, value);
                edit_list_name.setText(value);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
        	 
            @Override

            public void onClick(DialogInterface d, int which) {

                    d.dismiss();

            }
        });
        alert.show();
        }
	private void confirmClearDialog(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Clear " + edit_list_name.getText().toString() + " ?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	if(ohm.queueEditing){
            		ohm.getModel().getQueue().clear();
            		ohm.musicDB.clearQueue();
            		Toast.makeText(getApplicationContext(),"Queue Cleared", Toast.LENGTH_SHORT).show();
            	}
            	else{
            	ohm.musicDB.clearPlaylist(getApplicationContext(), ohm.current_playlist_id);
            	ohm.getModel().getPlaylists().get(ohm.current_playlist_id).getSongs().clear();
			    Toast.makeText(getApplicationContext(), edit_list_name.getText().toString() + " Cleared", Toast.LENGTH_SHORT).show();
            	}
            	edit_list_adapter.notifyDataSetChanged();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener(){
        	 
            @Override

            public void onClick(DialogInterface d, int which) {

                    d.dismiss();

            }
        });
        alert.show();
        }
	private void confirmDeleteDialog(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete " + edit_list_name.getText().toString() + " ?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	ohm.musicDB.deletePlaylist(getApplicationContext(), ohm.current_playlist_id);
            	ohm.getModel().getPlaylists().remove(ohm.current_playlist_id);
            	ohm.musicDB.setPlaylistArtworkUri(ohm.current_playlist_id, null);
            	if(ohm.musicDB.getPlaylistCursor(-1).getCount()<1){
            		Intent noneintent = new Intent(EditScreen.this, NoPlaylists.class);
            		startActivity(noneintent);
            	}
            	else{
                	ohm.setCurrentPlaylistId(-1);
                	doneNavigation();
            	}
			    Toast.makeText(getApplicationContext(), edit_list_name.getText().toString() + " Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener(){
        	 
            @Override

            public void onClick(DialogInterface d, int which) {

                    d.dismiss();

            }
        });
        alert.show();
        }
	
	public void doneNavigation(){
		if(ohm.edit_list_name == "Queue"){
			Intent doneintent = new Intent(EditScreen.this,QueueScreen.class);
			startActivity(doneintent);
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}
		else{
			Intent doneintent = new Intent(EditScreen.this,PlaylistsScreen.class);
			startActivity(doneintent);
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}
	}
	
	private DropListener mDropListener = 
		new DropListener() {
	    public void onDrop(int from, int to) {
	    	if(to>edit_list_adapter.editlist.size()-1){
	    	Song song = edit_list_adapter.editlist.remove(from);
	    	
	    	edit_list_adapter.editlist.add(to - 1, song);
	    	
	    	edit_list_adapter.notifyDataSetChanged();
	    	}
	    	else{
	    		Song song = edit_list_adapter.editlist.remove(from);
		    	
		    	edit_list_adapter.editlist.add(to, song);
		    	
		    	edit_list_adapter.notifyDataSetChanged();
	    	}
	    }
	};

	private RemoveListener mRemoveListener =
	    new RemoveListener() {
	    public void onRemove(int which) {
	    	edit_list_adapter.notifyDataSetChanged();	
	    }
	};

	private DragListener mDragListener =
		new DragListener() {
			int defaultBackgroundColor;
			public void onDrag(int x, int y) {
				
			}

			public void onStartDrag(View itemView) {
				try{
					if(itemView!=null){
						itemView.setVisibility(View.GONE);
						defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
						itemView.setBackgroundResource(defaultBackgroundColor);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}

			public void onStopDrag(View itemView) {
				
				try{
					if(itemView!=null){
						itemView.setVisibility(View.VISIBLE);
						itemView.setBackgroundColor(defaultBackgroundColor);
					}	
				}catch(Exception e){
					e.printStackTrace();
					edit_list_adapter.notifyDataSetChanged();
				}
			}
		
	};
	
	private static class ViewHolder{
		TextView artist_name;
		TextView song_name;
		ImageView album_art;
		ImageView minus;
	}

	private class EditListAdapter extends BaseAdapter{

	    private List<Song> editlist;
	    private Context context;
	    Bitmap albumart;
	    
	    public EditListAdapter(Context context, List<Song> edit_list) {
	    	this.context = context;
	    	this.editlist = edit_list;
	    	
	    }

	    public int getCount() {     
	    	return this.editlist.size();
	    }
	 
	    public Song getItem(int position) { 
	    	return editlist.get(position); 
	    }
	    
		public long getItemId(int position) {
			return editlist.get(position).getID();		
		}
	 
	    public View getView(final int position, View convertView, ViewGroup parent) {
	    	albumart = null;
	    	ViewHolder holder;
	        
	    	if(convertView == null){
	        	convertView = LayoutInflater.from(context).inflate(R.layout.drag_and_drop_list_item, parent,false);
	        	holder = new ViewHolder();
	        	holder.artist_name = (TextView)convertView.findViewById(R.id.artistName);
	        	holder.song_name = (TextView)convertView.findViewById(R.id.songName);
	        	holder.album_art = (ImageView)convertView.findViewById(R.id.albumArt);
	        	holder.minus = (ImageView)convertView.findViewById(R.id.minus);
	        	convertView.setTag(holder);
	        }
	        else{
	        	holder = (ViewHolder)convertView.getTag();
	        	
	        }
	    	
	        holder.minus.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					
	                Toast.makeText(context, editlist.get(position).getName() + " Deleted!", Toast.LENGTH_SHORT).show();
					editlist.remove(position);
					notifyDataSetChanged();
				}
	        	
	        });
	        holder.song_name.setText(editlist.get(position).getName());
	        holder.artist_name.setText(editlist.get(position).getArtistName());
	        albumart = MusicUtils.getArtwork(context, editlist.get(position).getArtworkUri());
	        
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
