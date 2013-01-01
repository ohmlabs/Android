package customviews.classes;

import datamodel.classes.Playlist;

public interface PlaylistDialogListener {
	void onShuffleClick(Playlist playlist);
	void onAddToQueueClick(Playlist playlist);
	void onEditClick(Playlist playlist);
}
