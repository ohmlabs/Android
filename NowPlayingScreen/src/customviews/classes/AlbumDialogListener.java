package customviews.classes;

import datamodel.classes.Album;

public interface AlbumDialogListener {
	void onShuffleClick(Album album);
	void onAddToPlaylistClick(Album album);
	void onAddToQueueClick(Album album);
}
