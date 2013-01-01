package customviews.classes;

import datamodel.classes.Song;

public interface NPSongDialogListener {
	void onGoToMusicClick(Song song);
	void onAddToPlaylistClick(Song song);
	void onAddToQueueClick(Song song);
	void onShareClick(Song song);
}
