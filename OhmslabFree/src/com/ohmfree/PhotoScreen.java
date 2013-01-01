package com.ohmfree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;



import net.londatiga.android.CropOption;
import net.londatiga.android.CropOptionAdapter;


import android.app.Activity;
import android.app.AlertDialog;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;

import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ImageView;

public class PhotoScreen extends Activity {
	private Uri mImageCaptureUri;
	private ImageView mImageView;
	private Uri previousImageUri;
	private boolean newImageChosen = false;
	private Bitmap bitmap;
	ContentResolver resolver;
	
	ApplicationObject ohm;
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.crop_main);
        
        ohm = (ApplicationObject)getApplicationContext();
        resolver = getContentResolver();
        if(ohm.current_playlist_id<0){
        	Intent homeintent = new Intent(PhotoScreen.this, NowPlayingScreen.class);
        	startActivity(homeintent);
        }
        previousImageUri = ohm.musicDB.getPlaylistArtworkUri(ohm.current_playlist_id);
        mImageCaptureUri = ohm.musicDB.getPlaylistArtworkUri(ohm.current_playlist_id);
        
        final String [] items			= new String [] {"Take with camera", "Take from phone"};				
		ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
		AlertDialog.Builder builder		= new AlertDialog.Builder(this);
		
		builder.setTitle("Select Image");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int item ) { //pick from camera
				if (item == 0) {
					Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			        createDirIfNotExists("Ohm_Playlist_Artwork");
					
					mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Ohm_Playlist_Artwork/","ohm_playlist_art_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
					
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);
						
						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { //pick from file
					Intent intent = new Intent();
					
	                intent.setType("image/*");
	                intent.setAction(Intent.ACTION_GET_CONTENT);
	                
	                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
				}
			}
		} );
		
		final AlertDialog dialog = builder.create();
		
		Button button 	= (Button) findViewById(R.id.btn_crop);
		mImageView		= (ImageView) findViewById(R.id.iv_photo);
		
		mImageView.setImageURI(mImageCaptureUri);
		
		Button rotateButton = (Button)findViewById(R.id.btn_rotate);
		
		rotateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mImageCaptureUri!=null){
				try {
					bitmap = MediaStore.Images.Media.getBitmap(resolver, mImageCaptureUri);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, 
				                              bitmap.getWidth(), bitmap.getHeight(), 
				                              matrix, true);
				
				bitmap = rotated;
				
				 mImageView.setImageBitmap(bitmap);
				 	File file = new File(mImageCaptureUri.getPath());
		            file.delete();
		            newImageChosen = true;
		            mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Ohm_Playlist_Artwork/","ohm_playlist_art_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
		            File f = new File(mImageCaptureUri.getPath()); 
		            
		            try {
			            FileOutputStream out = new FileOutputStream(f);
			            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
		            } catch (Exception e) {
			            e.printStackTrace();
		            }
				
			}
				else{
					Toast.makeText(getApplicationContext(), "No Image to Rotate", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		Button doneButton = (Button)findViewById(R.id.btn_done);
		
		doneButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				if(newImageChosen){
					ohm.music_data_model.getPlaylists().get(ohm.current_playlist_id).setUri(mImageCaptureUri);
	   				
	   				ohm.musicDB.setPlaylistArtworkUri(ohm.current_playlist_id, mImageCaptureUri);
				}
				else{
					ohm.music_data_model.getPlaylists().get(ohm.current_playlist_id).setUri(previousImageUri);
	   				
	   				ohm.musicDB.setPlaylistArtworkUri(ohm.current_playlist_id, previousImageUri);
				}
				Intent doneintent = new Intent(PhotoScreen.this, PlaylistsScreen.class);
				startActivity(doneintent);
			}
		});
		
		button.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				dialog.show();
			}
		});
    }
    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
       
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != RESULT_OK) return;
	   
	    switch (requestCode) {
		    case PICK_FROM_CAMERA:
		    	newImageChosen = true;
		    	doCrop();
		    	
		    	break;
		    	
		    case PICK_FROM_FILE: 
		    	mImageCaptureUri = data.getData();

		    	newImageChosen = true;
		    	doCrop();
	    
		    	break;	    	
	    
		    case CROP_FROM_CAMERA:	    	
		        Bundle extras = data.getExtras();
	
		        if (extras != null) {	        	
		            Bitmap photo = extras.getParcelable("data");

			    	newImageChosen = true;
		            mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Ohm_Playlist_Artwork/","ohm_playlist_art_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
		            
		            File f = new File(mImageCaptureUri.getPath()); 
		            
		            try {
			            FileOutputStream out = new FileOutputStream(f);
			            photo.compress(Bitmap.CompressFormat.JPEG, 70, out);
		            } catch (Exception e) {
			            e.printStackTrace();
		            }
		            mImageView.setImageURI(mImageCaptureUri);
			     
		        }
	
		        break;

	    }
	}
    
    private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
    	
    	Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
        
        int size = list.size();
        
        if (size == 0) {	        
        	Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();
        	
            return;
        } else {
        	intent.setData(mImageCaptureUri);
            
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            
        	if (size == 1) {
        		Intent i 		= new Intent(intent);
	        	ResolveInfo res	= list.get(0);
	        	
	        	i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
	        	
	        	startActivityForResult(i, CROP_FROM_CAMERA);
        	} else {
		        for (ResolveInfo res : list) {
		        	final CropOption co = new CropOption();
		        	
		        	co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
		        	co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
		        	co.appIntent= new Intent(intent);
		        	
		        	co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        	
		            cropOptions.add(co);
		        }
	        
		        CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);
		        
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle("Choose Crop App");
		        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
		            public void onClick( DialogInterface dialog, int item ) {
		                startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
		            }
		        });
	        
		        builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
		            @Override
		            public void onCancel( DialogInterface dialog ) {
		               
		                if (mImageCaptureUri != null ) {
		                    getContentResolver().delete(mImageCaptureUri, null, null );
		                    mImageCaptureUri = null;
		                }
		            }
		        } );
		        
		        AlertDialog alert = builder.create();
		        
		        alert.show();
        	}
        }
	}
}