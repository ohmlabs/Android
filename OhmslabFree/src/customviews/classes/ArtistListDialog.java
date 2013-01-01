package customviews.classes;

import com.ohmfree.NewMusicGallery;
import com.ohmfree.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ArtistListDialog extends Dialog{
	
	ListView list;
	public ArtistListDialog(Context context, int position, NewMusicGallery.ArtistGalleryAdapter artistlistadapter) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.artist_list_dialog);
		setCanceledOnTouchOutside(true);
		
		list = (ListView)findViewById(R.id.list);
		list.setAdapter(artistlistadapter);
		list.setSelection(position);
		
		
	}

	public void setListListener(OnItemClickListener listener){
		list.setOnItemClickListener(listener);
	}
}
