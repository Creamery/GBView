package seebee.geebeeview.containers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seebee.geebeeview.R;
import seebee.geebeeview.model.monitoring.ValueCounter;

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

    public static ArrayList<Integer> getColors(String recordValue, List<Entry> entries, int defaultColor) {
        ArrayList<Integer> colors = new ArrayList<>();
        int colorIndex;
        int color = -1;

        int[] colorReference;

        for(int i = 0; i < entries.size(); i++) {
            Log.e("COLORS", recordValue+" y Entry "+entries.get(i).getY());
            switch(recordValue) {
                case StringConstants.COL_VA_LEFT:
                case StringConstants.COL_VA_RIGHT:
                    colorIndex = (int) entries.get(i).getY();
                    colorReference = ColorThemes.csVISION;
                    // For vision, index 0 is N/A
                    if(colorIndex > 0)
                        color = colorReference[colorIndex - 1];
                    else if(colorIndex > -1 && colorIndex < colorReference.length) {
                        color = ColorThemes.cGray;
                    }

                    break;
                case StringConstants.COL_HEAR_LEFT:
                case StringConstants.COL_HEAR_RIGHT:
                    colorIndex = (int) entries.get(i).getY();
                    colorReference = ColorThemes.csHEARING_GRAPH;
                    // For vision, index 0 is N/A
                    if(colorIndex > 0)
                        color = colorReference[colorIndex - 1];
                    else if(colorIndex > -1 && colorIndex < colorReference.length) {
                        color = ColorThemes.cGray;
                    }

                    break;
                case StringConstants.COL_COLOR_VISION:
                case StringConstants.COL_FINE_DOMINANT:
                case StringConstants.COL_FINE_NON_DOMINANT:
                case StringConstants.COL_FINE_HOLD:
                    colorIndex = (int) entries.get(i).getY();
                    colorReference = ColorThemes.csBINARY_GRAPH;
                    if(colorIndex > -1 && colorIndex < colorReference.length) {
                        color = colorReference[colorIndex];
                    }
                    break;

                case StringConstants.COL_GROSS_MOTOR:
                    colorIndex = (int) entries.get(i).getY();
                    colorReference = ColorThemes.csTERNARY_GRAPH;
                    if(colorIndex > -1 && colorIndex < colorReference.length) {
                        color = colorReference[colorIndex];
                    }
                    break;
                default:
                    colorIndex = -1;
                    Log.e("COLORS", "Index not found");
            }

            if(colorIndex == -1) {
                color = defaultColor;
            }
            colors.add(color);

        }
        return colors;
    }


    public static float roundFloat(float number, int decimalPlaces) {
        if(decimalPlaces <= 0)
            return number;

        int places = 10*decimalPlaces;
        return (float)Math.round(number * places) / places;
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

    public static int getMaxLabelCount(String recordValue) {
        switch(recordValue) {
            case StringConstants.COL_WEIGHT:
                return ValueCounter.maxLblWeight;
            case StringConstants.COL_HEIGHT:
                return ValueCounter.maxLblHeight;
            case StringConstants.COL_BMI:
                return ValueCounter.maxLblBMI;
            case StringConstants.COL_VA_LEFT:
            case StringConstants.COL_VA_RIGHT:
                return ValueCounter.maxLblVision;
            case StringConstants.COL_COLOR_VISION:
                return ValueCounter.maxLblColor;
            case StringConstants.COL_HEAR_LEFT:
            case StringConstants.COL_HEAR_RIGHT:
                return ValueCounter.maxLblHearing;
            case StringConstants.COL_GROSS_MOTOR:
                return ValueCounter.maxLblGrossMotor;
            case StringConstants.COL_FINE_DOMINANT:
            case StringConstants.COL_FINE_NON_DOMINANT:
            case StringConstants.COL_FINE_HOLD:
                return ValueCounter.maxLblFineMotor;
        }
        return 5;
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

    public static float[] toPrimitiveFloatArray(ArrayList<Float> floatList) {
        float[] floatArray = new float[floatList.size()];
        for(int i = 0; i < floatList.size(); i++) {
            floatArray[i] = floatList.get(i);
        }
        return floatArray;
    }

    // Used for uniform colors in a bar chart
//    public static int[] getColorSetTealDefault(int entryCount) {
//        if(csTealDefault.length != entryCount) {
//            csTealDefault = new int[entryCount];
//            for(int i = 0; i < entryCount; i++) {
//                csTealDefault[i] = ColorThemes.cTealDefault;
//            }
//        }
//        return csTealDefault;
//    }

    // For other colors
    public static int[] getColorSetByCount(int entryCount, int color) {
        int[] colorSet = new int[entryCount];

        for(int i = 0; i < entryCount; i++) {
            colorSet[i] = color;
        }
        return colorSet;
    }



    /**
     * Returns the percent equivalent of the passed parameter [range of 0-100]
     * @param rawData
     * @param rawDataSum
     * @return
     */
    public static float[] computePercentEquivalent(float[] rawData, float rawDataSum) {
        float[] percentData;
        if(rawData != null) {
            percentData = new float[rawData.length];

            for(int i = 0; i < rawData.length; i++) {
                if(rawDataSum == 0) {
                    percentData[i] = 0;
                }
                else {
                    percentData[i] = (rawData[i]/rawDataSum)*100f;
                }
            }

            return percentData;
        }
        percentData = new float[]{0};
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
    public static float[] convertToFloat(ArrayList<Float> listFloat) {
        float[] floatData = new float[listFloat.size()];

        for(int i = 0; i < listFloat.size(); i++) {
            floatData[i] = (float) listFloat.get(i);
        }

        return floatData;
    }
    public static ArrayList<Float> convertToFloat(float[] floatData) {
        ArrayList<Float> listFloat = new ArrayList<>();
        for(int i = 0; i < floatData.length; i++) {
            listFloat.add(floatData[i]);
        }

        return listFloat;
    }
    public static String[] convertToString(ArrayList<String> listString) {
        String[] stringData = new String[listString.size()];

        for(int i = 0; i < listString.size(); i++) {
            stringData[i] = listString.get(i);
        }

        return stringData;
    }

    public static ArrayList<Integer> convertToInteger(int[] intData) {
        ArrayList<Integer> listInt = new ArrayList<>();

        for(int i = 0; i < intData.length; i++) {
            listInt.add(intData[i]);
        }

        return listInt;
    }

    public static int[] convertToInteger(ArrayList<Integer> listData) {
        int[] intData = new int[listData.size()];

        for(int i = 0; i < intData.length; i++) {
            intData[i] = listData.get(i);
        }

        return intData;
    }

    /**
     * Returns the sum of the passed parameter
     * @param array
     * @return
     */
    public static float getArraySum(float[] array) {
        if(array != null) {
            float sum = 0;
            for (int i = 0; i < array.length; i++) {
                sum += array[i];
            }
            return sum;
        }
        return 0;
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

    public static int getColorByGenderDark(Context context, int gender) {
        if(gender == 0) {
            return ContextCompat.getColor(context, R.color.view_patient_name_color_male_dark);
//            return getResources().getDrawable(R.drawable.img_gender_circle_fill_male);
        }
        else {

            return ContextCompat.getColor(context, R.color.view_patient_name_color_female_dark);
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
