package customviews.classes;

import java.util.List;

import datamodel.classes.Song;

public interface QueueDialogListener {
	void onShuffleClick(List<Song> songlist);
	void onSaveClick(List<Song> songlist);
	void onEditClick(List<Song> songlist);
	void onToPlaylistClick(List<Song> songlist);
}
