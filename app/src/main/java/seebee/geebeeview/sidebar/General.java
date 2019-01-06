package seebee.geebeeview.sidebar;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;

import seebee.geebeeview.R;

public class General {
    private static final General INSTANCE = new General();
    private static Typeface fntRobotoRegular = null;
    public static General getInstance() {
        return INSTANCE;
    }

    public static Typeface getDefaultFont(Context context) {
        if(fntRobotoRegular == null) {
            fntRobotoRegular = Typeface.createFromAsset(context.getAssets(), "font/roboto/Roboto-Regular.ttf");
        }
        return fntRobotoRegular;
      }
    public static int getColorByGender(Context context, String gender) {
        if(gender.trim().toLowerCase().contains("f")) {
            return ContextCompat.getColor(context, R.color.view_patient_name_color_female);
        }
        else {
            return ContextCompat.getColor(context, R.color.view_patient_name_color_male);
        }
    }
    public static int getColorByGender(Context context, int gender) {
        if(gender == 0) {
            return ContextCompat.getColor(context, R.color.view_patient_name_color_male);
//            return getResources().getDrawable(R.drawable.img_gender_circle_fill_male);
        }
        else {

            return ContextCompat.getColor(context, R.color.view_patient_name_color_female);
        }
    }
    public static int getVisibility(boolean visibility) {
        if(visibility) {
            return View.VISIBLE;
        }
        else {
            return View.INVISIBLE;
        }
    }
}
