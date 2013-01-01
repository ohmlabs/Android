/*

 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ohmslab;




import datamodel.classes.Song;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PlaylistCreateDialogue extends Activity
{
    private EditText mPlaylist;
    private TextView mPrompt;
    private Button mSaveButton;
    ApplicationObject ohm ;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        ohm = (ApplicationObject) getApplicationContext();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.playlist_create_dialogue);
        getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT,
                                    WindowManager.LayoutParams.WRAP_CONTENT);

        mPrompt = (TextView)findViewById(R.id.prompt);
        mPlaylist = (EditText)findViewById(R.id.playlist);
        mSaveButton = (Button) findViewById(R.id.create);
        mSaveButton.setOnClickListener(mOpenClicked);

        ((Button)findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        
        String defaultname = "New Playlist";//icicle != null ? icicle.getString("defaultname") : makePlaylistName();
       // String promptformat = getString(R.string.create_playlist_create_text_prompt);
        String promptformat = "Enter Playlist Name";
        String prompt = String.format(promptformat, defaultname);
        mPrompt.setText(prompt);
        mPlaylist.setText(defaultname);
        mPlaylist.setSelection(defaultname.length());
       // mPlaylist.addTextChangedListener(mTextWatcher);
    }
    
    TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // don't care about this one
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // check if playlist with current name exists already, and warn the user if so.
            if (idForplaylist(mPlaylist.getText().toString()) >= 0) {
               // mSaveButton.setText(R.string.create_playlist_overwrite_text);
               // mSaveButton.setClickable(false);
            } else {
               // mSaveButton.setText(R.string.create_playlist_create_text);
               // mSaveButton.setClickable(true);
            }
        };
        public void afterTextChanged(Editable s) {
            // don't care about this one
        }
    };
    
    private int idForplaylist(String name) {
    	
    	Log.i("PLaylist ","idForplaylist " + name);
        Cursor c = query(this, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Playlists._ID },
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[] { name },
                MediaStore.Audio.Playlists.NAME);
        int id = -1;
        if (c != null) {
            c.moveToFirst();
            if (!c.isAfterLast()) {
                id = c.getInt(0);
                Log.i("PLaylist ","idForplaylist " + id);
            }
        }
        c.close();
        return id;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outcicle) {
    	Log.i("PLaylist ","onSaveInstanceState "+ mPlaylist.getText().toString());
       // outcicle.putString("defaultname", mPlaylist.getText().toString());
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }

    public String makePlaylistName() {
    	Log.i("PLaylist ","makePlaylistName ");
        //String template = getString(R.string.new_playlist_name_template);
    	String template = "PlayList";
        int num = 1;

        String[] cols = new String[] {
                MediaStore.Audio.Playlists.NAME
        };
        ContentResolver resolver = getContentResolver();
        String whereclause = MediaStore.Audio.Playlists.NAME + " != ''";
        Cursor c = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
            cols, whereclause, null,
            MediaStore.Audio.Playlists.NAME);

        String suggestedname;
        suggestedname = String.format(template, num++);
        
        // Need to loop until we've made 1 full pass through without finding a match.
        // Looping more than once shouldn't happen very often, but will happen if
        // you have playlists named "New Playlist 1"/10/2/3/4/5/6/7/8/9, where
        // making only one pass would result in "New Playlist 10" being erroneously
        // picked for the new name.
        boolean done = false;
        while (!done) {
            done = true;
            c.moveToFirst();
            while (! c.isAfterLast()) {
                String playlistname = c.getString(0);
                if (playlistname.compareToIgnoreCase(suggestedname) == 0) {
                    suggestedname = String.format(template, num++);
                    done = false;
                }
                c.moveToNext();
            }
        }
        c.close();
        return suggestedname;
    }
    
    private View.OnClickListener mOpenClicked = new View.OnClickListener() {
        public void onClick(View v) {
        	
        	
            String name = mPlaylist.getText().toString();
            if (name != null && name.length() > 0) {
                ContentResolver resolver = getContentResolver();
                int id = idForplaylist(name);
                Uri uri;
                if (id >= 0) {
                	Toast.makeText(PlaylistCreateDialogue.this, "Playlist already there.", 5000).show();
                	return;
                } else {
                	
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Audio.Playlists.NAME, name);
                    uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                    id = idForplaylist(name);
                }
                
                
                try{
                    
                    String[] cols = new String[] {
                            "count(*)"
                    };
                    resolver = getContentResolver();
                    Uri uri1 = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
                    Cursor cur = resolver.query(uri1, cols, null, null, null);
                    cur.moveToFirst();
                    final int base = cur.getInt(0);
                    Log.i("Values " ,base +"  " +cur.getInt(0) +"  id " + id);
                    cur.close();
                    
                    for(int i=0;i<ohm.music_data_model.getQueue().size();i++){
                    	Song song = ohm.music_data_model.getQueue().get(i);
                    	ContentValues values1 = new ContentValues();
	                    values1.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER,  (base + song.getID()));
	                    values1.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, song.getID());
	                    
	                    Log.i("Values " ,"Pos "+i+"  "+values1.toString() +"  "+ base +"  " +song.getID());
	                    resolver.insert(uri1, values1);
                    }
                }
                catch(Exception e){
                	e.printStackTrace();
                }
                
               
                setResult(RESULT_OK, (new Intent()).setAction(uri.toString()));
                finish();
            }
        }
    };
    
    
    
    public static void clearPlaylist(Context context, int plid) {
    	Log.i("PLaylist ","clearPlaylist " + plid);
        final String[] ccols = new String[] { MediaStore.Audio.Playlists.Members._ID };
        Cursor cursor = query(context, MediaStore.Audio.Playlists.Members.getContentUri("external", plid),
                ccols, null, null, MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
        
        if (cursor == null) {
            return;
        }
        cursor.moveToFirst();
        
        /*while (!cursor.isAfterLast()) {
            cursor.deleteRow();
        }
        cursor.commitUpdates();*/
        
        cursor.close();
        return;
    }
    
    public static Cursor query(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        try {
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
         } catch (UnsupportedOperationException ex) {
            return null;
        }
        
    }
}
