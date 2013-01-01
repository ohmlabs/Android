package com.ohmslab;



import org.json.JSONException;
import org.json.JSONObject;

import com.ohmslab.facebook.BaseRequestListener;
import com.ohmslab.facebook.LoginButton;
import com.ohmslab.facebook.SessionEvents;
import com.ohmslab.facebook.SessionEvents.AuthListener;
import com.ohmslab.facebook.SessionEvents.LogoutListener;
import com.ohmslab.facebook.SessionStore;
import com.facebook.android.Facebook;
import com.facebook.android.AsyncFacebookRunner;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class LoginScreen extends Activity{
	private LoginButton mLoginButton;
    private TextView userName;
    private ImageView backButton;
    private ApplicationObject ohm;
    private AsyncFacebookRunner mAsyncRunner;
    private ImageView postButton;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

		
        ohm = (ApplicationObject)getApplicationContext();
        mLoginButton = (LoginButton) findViewById(R.id.loginLogout);
        userName = (TextView)findViewById(R.id.userName);
        backButton = (ImageView)findViewById(R.id.backButton);
        postButton = (ImageView)findViewById(R.id.postButton);
        
        postButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(ohm.facebook.isSessionValid()){
					Intent ohmintent = new Intent(LoginScreen.this,YoutubeScreen.class);
					startActivity(ohmintent);
				}
				else{
					Toast.makeText(getApplicationContext(), "Sign In First Please", Toast.LENGTH_SHORT).show();
				}
			}
        	
        });
        backButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				Intent ohmintent = new Intent(LoginScreen.this,NowPlayingScreen.class);
				startActivity(ohmintent);
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				
			}
        	
        });
       	mAsyncRunner = new AsyncFacebookRunner(ohm.facebook);

        SessionStore.restore(ohm.facebook, this);

        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
        mLoginButton.init(this, ohm.facebook);
        if(ohm.facebook.isSessionValid()){
        	mAsyncRunner.request("me", new SampleRequestListener());
        	
        }
        else{
        	userName.setText("Not Logged In");
        }
	}
	  @Override
	    protected void onActivityResult(int requestCode, int resultCode,
	                                    Intent data) {
	        ohm.facebook.authorizeCallback(requestCode, resultCode, data);
	    }
	public class SampleAuthListener implements AuthListener {

        public void onAuthSucceed() {
        		
            	mAsyncRunner.request("me", new SampleRequestListener());
            
        }

        public void onAuthFail(String error) {
        }
    }

    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
        }

        public void onLogoutFinish() {
            userName.setText("Not Logged In");
        }
    }
    public class SampleRequestListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            try {
                JSONObject json = Util.parseJson(response);
                final String name = json.getString("name");
                LoginScreen.this.runOnUiThread(new Runnable() {
                    public void run() {
                    	userName.setText(name);
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }
   
}
