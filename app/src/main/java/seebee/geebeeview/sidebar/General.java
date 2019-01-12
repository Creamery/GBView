package seebee.geebeeview.sidebar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import seebee.geebeeview.R;

/**
 * A Singleton class that holds reusable functions to be used by any class, mainly for conversion or retrieval
 * Use General.getInstance() for getDefaultFont() functions
 * For conversion functions, use a static call (i.e. General.convertToFloat(...))
 */
public class General {

    private static final General INSTANCE = new General();
    private static Typeface fntRobotoRegular = null;


    private static final int[] csGray = new int[] {Color.LTGRAY, Color.GRAY, Color.DKGRAY, Color.GRAY, Color.LTGRAY};
    private static int[] csWhite = new int[] {Color.WHITE};
    private static int[] csLightGray = new int[] {ColorThemes.cLightGray};
    private static int[] csTealDefault = new int[] {ColorThemes.cTealDefault};

    public static General getInstance() {
        return INSTANCE;
    }

    public static int[] getColorSetGray() {
        return csGray;
    }

    public static int[] getColorSetWhite(int entryCount) {
        if(csWhite.length != entryCount) {
            csWhite = new int[entryCount];
            for(int i = 0; i < entryCount; i++) {
                csWhite[i] = Color.WHITE;
            }
        }
        return csWhite;
    }

    // Used for uniform colors in a stacked bar chart
    public static int[] getColorSetLightGray(int entryCount) {
        if(csLightGray.length != entryCount) {
            csLightGray = new int[entryCount];
            for(int i = 0; i < entryCount; i++) {
                csLightGray[i] = ColorThemes.cLightGray;
            }
        }
        return csLightGray;
    }

    // Used for uniform colors in a bar chart
    public static int[] getColorSetTealDefault(int entryCount) {
        if(csTealDefault.length != entryCount) {
            csTealDefault = new int[entryCount];
            for(int i = 0; i < entryCount; i++) {
                csTealDefault[i] = ColorThemes.cTealDefault;
            }
        }
        return csTealDefault;
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
