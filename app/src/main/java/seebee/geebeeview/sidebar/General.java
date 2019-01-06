package seebee.geebeeview.sidebar;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;

import seebee.geebeeview.R;

/**
 * A Singleton class that holds reusable functions to be used by any class, mainly for conversion or retrieval
 * Use General.getInstance() for getDefaultFont() functions
 * For conversion functions, use a static call (i.e. General.convertToFloat(...))
 */
public class General {

    private static final General INSTANCE = new General();
    private static Typeface fntRobotoRegular = null;

    public static General getInstance() {
        return INSTANCE;
    }


    /**
     * Returns the percent equivalent of the passed parameter [range of 0-100]
     * @param rawData
     * @param rawDataSum
     * @return
     */
    public static float[] computePercentEquivalent(float[] rawData, float rawDataSum) {
        float[] percentData = new float[rawData.length];

        for(int i = 0; i < rawData.length; i++) {
            percentData[i] = (rawData[i]/rawDataSum)*100f;
        }

        return percentData;
    }

    /**
     * Converts the passed parameter to a float[]
     * @param intData
     * @return
     */
    public static float[] convertToFloat(int[] intData) {
        float[] floatData = new float[intData.length];

        for(int i = 0; i < intData.length; i++) {
            floatData[i] = (float) intData[i];
        }

        return floatData;
    }

    /**
     * Returns the sum of the passed parameter
     * @param array
     * @return
     */
    public static float getArraySum(float[] array) {
        float sum = 0;
        for(int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
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
