
package com.ohmslab;

import java.util.List;
import datamodel.classes.SongList;

interface IOhmPlaybackService
{
    int getNowPlayingPosition();
    boolean isPlaying();
    void playSong(int position);
    boolean playPause();
    void prev();
    void next();
    int duration();
    int position();
    void setNowPlayingList(inout SongList songlist);
   	SongList getNowPlayingList();
    int seek(int pos);
    void setNowPlayingPosition(int position);
    void setShuffleMode(int shufflemode, inout SongList songlist);
    int getShuffleMode();
    void setRepeatMode(int repeatmode);
    int getRepeatMode();
}

