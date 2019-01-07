package seebee.geebeeview.sidebar;

import android.graphics.Color;

/**
 * Specify colors here
 */
public class ColorThemes {

    public static int cLightGray = Color.rgb(240, 240, 240);

    // BMI (Underweight, Normal, Overweight, Obese, N/A)
    public static int cBMI_Underweight = Color.rgb(253, 212, 0);
    public static int cBMI_Normal = Color.rgb(116, 227, 4);
    public static int cBMI_Overweight = Color.rgb(253, 139, 0);
    public static int cBMI_Obese = Color.rgb(253, 54, 0);
    public static int cBMI_NA = Color.rgb(212, 212, 212);

    public static int[] csBMI = new int[] {cBMI_Underweight, cBMI_Normal, cBMI_Overweight, cBMI_Obese, cBMI_NA};


}
