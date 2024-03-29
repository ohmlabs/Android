package datacontrollers.classes;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

public abstract class BaseDialogListener implements DialogListener {
    @Override
    public void onFacebookError(FacebookError e) {
        e.printStackTrace();
    }
    @Override
    public void onError(DialogError e) {
        e.printStackTrace();
    }
    @Override
    public void onCancel() {
    }
}