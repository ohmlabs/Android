/*
 * Copyright (C) 2009 The Android Open Source Project
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


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Environment;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Simple widget to show currently playing album art along
 * with play/pause and next track buttons.  
 */
public class MediaAppWidgetProvider extends AppWidgetProvider {
    
	static final String TAG = "MusicAppWidgetProvider";
    
    
    public static final String CMDAPPWIDGETUPDATE = "appwidgetupdate";

    private static MediaAppWidgetProvider sInstance;
    
    static synchronized MediaAppWidgetProvider getInstance() {
        if (sInstance == null) {
            sInstance = new MediaAppWidgetProvider();
        }
        return sInstance;
    }
    
   
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        
    	defaultAppWidget(context, appWidgetIds);
    	Intent updateIntent = new Intent(OhmPlaybackService.SERVICECMD);
        updateIntent.putExtra(OhmPlaybackService.CMDNAME,
                MediaAppWidgetProvider.CMDAPPWIDGETUPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        updateIntent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        context.sendBroadcast(updateIntent);
   
    }
    
    /**
     * Initialize given widgets to default state, where we launch Music on default click
     * and hide actions if service not running.
     */
    private void defaultAppWidget(Context context, int[] appWidgetIds) {
        
    	final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        linkButtons(context, views, false, false);
        pushUpdate(context, appWidgetIds, views);
    }
    
    private void pushUpdate(Context context, int[] appWidgetIds, RemoteViews views) {
        // Update specific list of appWidgetIds if given, otherwise default to all
        final AppWidgetManager gm = AppWidgetManager.getInstance(context);
        if (appWidgetIds != null) {
            gm.updateAppWidget(appWidgetIds, views);
        } else {
            gm.updateAppWidget(new ComponentName(context, this.getClass()), views);
        }
    }
    
    /**
     * Check against {@link AppWidgetManager} if there are any instances of this widget.
     */
    private boolean hasInstances(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, this.getClass()));
        return (appWidgetIds.length > 0);
    }

    /**
     * Handle a change notification coming over from {@link OhmPlaybackService}
     */
    
    void updateWidget(Intent intent){
    	
    }
    
    void notifyChange(OhmPlaybackService service, String what) {
    	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(service);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(service, this.getClass()));
            
        if(what.equals(OhmPlaybackService.METADATA)){
            	performUpdate(service, appWidgetIds);
               
            }
            else if(what.equals(OhmPlaybackService.PLAY)){
            	
            		performUpdate(service, appWidgetIds);
            	}
            else if(what.equals(OhmPlaybackService.PAUSE)){
            		performUpdate(service, appWidgetIds);
            	}
            }
            
        
    
    
    void performUpdate(OhmPlaybackService service, int[] appWidgetIds) {
        
        final RemoteViews views = new RemoteViews(service.getPackageName(), R.layout.widget);
        
        String titleName = service.getTrackName();
        String artistName = service.getArtistName();
        final boolean playing = service.isPlaying();
        final boolean active = service.active();
        if (playing) {
        	
            views.setImageViewResource(R.id.widgetPlayPause, R.drawable.bs_widgetpause);
            views.setTextViewText(R.id.widgetSong, titleName);
            views.setTextViewText(R.id.widgetArtist, artistName);
        } else {
            views.setImageViewResource(R.id.widgetPlayPause, R.drawable.bs_widgetplay);
        }

        linkButtons(service, views, playing, active);
        
        pushUpdate(service, appWidgetIds, views);
    }


    private void linkButtons(Context context, RemoteViews views, boolean playing, boolean active) {
        
        Intent intent;
        PendingIntent pendingIntent;
        
        intent = new Intent(context, NowPlayingScreen.class);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widgetMusic, pendingIntent);
        
        if(active){
        	if(playing){
        	intent = new Intent(OhmPlaybackService.PAUSE);
        	pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        	views.setOnClickPendingIntent(R.id.widgetPlayPause, pendingIntent);
        	}
        	else{
        		intent = new Intent(OhmPlaybackService.PLAY);
            	pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            	views.setOnClickPendingIntent(R.id.widgetPlayPause, pendingIntent);
        	}
        	intent = new Intent(OhmPlaybackService.NEXT);
        	pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        	views.setOnClickPendingIntent(R.id.widgetNext, pendingIntent);
        }
        else{
        	intent = new Intent(context, NowPlayingScreen.class);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widgetPlayPause, pendingIntent);
            
            intent = new Intent(context, NowPlayingScreen.class);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widgetNext, pendingIntent);
        }
    }
}
