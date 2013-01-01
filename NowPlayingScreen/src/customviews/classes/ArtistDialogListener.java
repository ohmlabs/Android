package customviews.classes;

import datamodel.classes.Artist;

public interface ArtistDialogListener {

	void onShuffleClick(Artist artist);
	void onAddToPlaylistClick(Artist artist);
	void onAddToQueueClick(Artist artist);
}
