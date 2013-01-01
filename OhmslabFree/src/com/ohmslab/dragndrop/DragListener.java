package com.ohmslab.dragndrop;

import android.view.View;

public interface DragListener {

	void onStartDrag(View itemView);

	void onDrag(int x, int y);

	void onStopDrag(View itemView);
}
