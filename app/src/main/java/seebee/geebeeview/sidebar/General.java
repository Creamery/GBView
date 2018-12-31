package seebee.geebeeview.sidebar;

import android.view.View;

public class General {

    public static int getVisibility(boolean visibility) {
        if(visibility) {
            return View.VISIBLE;
        }
        else {
            return View.INVISIBLE;
        }
    }
}
