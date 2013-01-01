package com.ohmfree;




import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;



public class FaceBookActivity extends Activity {

    Facebook facebook = new Facebook("170031159703872");
    private SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*
         * Get existing access_token if any
         */
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }
        
        /*
         * Only call authorize if the access_token has expired.
         */
        if(!facebook.isSessionValid()) {

            facebook.authorize(this, new String[] {}, new DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("access_token", facebook.getAccessToken());
                    editor.putLong("access_expires", facebook.getAccessExpires());
                    editor.commit();
                    
                    facebook.dialog(FaceBookActivity.this, "feed", new PostDialogListener());
                   
                }
    
                @Override
                public void onFacebookError(FacebookError error) {
                	
                }
    
                @Override
                public void onError(DialogError e) {
                	
                }
    
                @Override
                public void onCancel() {}
            });
        }
        else{
        	facebook.dialog(this, "feed", new PostDialogListener());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
        
       
    }
    
    public void navigateHome(){
    	Intent homeintent = new Intent(this, NowPlayingScreen.class);
    	startActivity(homeintent);
    }
    public abstract class BaseDialogListener implements DialogListener {
        @Override
        public void onFacebookError(FacebookError e) {
            e.printStackTrace();
        }
        @Override
        public void onError(DialogError e) {
            e.printStackTrace();
        }
        @Override
        public void onCancel() {
        }
    }

    public class PostDialogListener extends BaseDialogListener {
        @Override
        public void onComplete(Bundle values) {
            /*final String postId = values.getString("post_id");
            if (postId != null) {
                showToast("Message posted on the wall.");
            } else {
                showToast("No message posted on the wall.");
            }*/
        	navigateHome();
        }
    }
}
