package com.ohmfree;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NoPlaylists extends Activity{
	
	TextView playlist_number = null;
	ImageView new_playlist_button;
	ImageView empty_playlist;
	ImageView home_button;
	ApplicationObject ohm = null;
	EditText playlist_name;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.no_playlists);
		ohm = (ApplicationObject) getApplicationContext();
		
		playlist_number = (TextView)findViewById(R.id.playlistNumber);
		
		playlist_number.setText("No Playlists");
		
		new_playlist_button = (ImageView) findViewById(R.id.newButton);
        new_playlist_button.setOnClickListener(new ImageView.OnClickListener() { 
			
        	public void onClick (View v){
        		showNewPlaylistDialog();
        	};
		
        });
        
        home_button = (ImageView) findViewById(R.id.backButton);
        home_button.setOnClickListener(new ImageView.OnClickListener() { 
			
        	public void onClick (View v){
        		navigateHome();
        	};
		
        });
		
	}
	
	private void navigateHome(){
		Intent ohmintent = new Intent(NoPlaylists.this,NowPlayingScreen.class);
		startActivity(ohmintent);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	private int idForplaylist(String name) {
    	ContentResolver resolver = getContentResolver();
        Cursor c = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Playlists._ID },
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[] { name },
                MediaStore.Audio.Playlists.NAME);
        int id = -1;
        if (c != null) {
            c.moveToFirst();
            if (!c.isAfterLast()) {
                id = c.getInt(0);
            }
        }
        c.close();
        return id;
    }
	
	private void showNewPlaylistDialog(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	playlist_name = new EditText(this);
        alert.setView(playlist_name);
        alert.setTitle("Name Your New Playlist");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            ContentResolver resolver = getContentResolver();
            String value = playlist_name.getText().toString().trim();
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Playlists.NAME, playlist_name.getText().toString().trim());
                resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                int id = idForplaylist(value);
                ohm.music_data_model.addPlaylist(id, value, null);
                ohm.current_playlist_id = id;
                
                Intent playlistsintent = new Intent(NoPlaylists.this, PlaylistsScreen.class);
                startActivity(playlistsintent);
        		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            	
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

}
