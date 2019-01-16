package seebee.geebeeview.containers;

import android.graphics.Color;

/**
 * Specify colors here
 */
public class ColorThemes {

    public static int cPrimaryDark = 0xFF004D40;


    public static int cNational = Color.rgb(29, 233, 182); // TODO change this to change national signature color


    public static int cGray = Color.rgb(105, 105, 105);
    public static int cLightGray = Color.rgb(240, 240, 240);
    public static int cTealDefault = Color.rgb(29, 233, 182);
    public static int cNA = Color.rgb(212, 212, 212);
    public static int cCyanAccent = Color.rgb(0, 229, 255);

    // BMI (5 items = Underweight, Normal, Overweight, Obese, N/A)
    public static int cBMI_Underweight = Color.rgb(253, 212, 0);
    public static int cBMI_Normal = Color.rgb(116, 227, 4);
    public static int cBMI_Overweight = Color.rgb(253, 139, 0);
    public static int cBMI_Obese = Color.rgb(253, 54, 0);
    public static int cBMI_NA = cCyanAccent;




    // Hearing Left & Right (6 items = Normal Hearing, Mild Hearing Loss, Moderate Hearing Loss, Moderately Severe Hearing Loss, Severe Hearing Loss, Profound Hearing Loss)
    public static int cHEARING_Normal = cBMI_Normal;
    public static int cHEARING_Mild = Color.rgb(200, 227, 4);// cBMI_Underweight;

    public static int cHEARING_Moderate = Color.rgb(253, 239, 0);// cBMI_Underweight;
    public static int cHEARING_ModSevere = Color.rgb(253, 186, 0);

    public static int cHEARING_Severe = cBMI_Overweight;// Color.rgb(253, 100, 0);
    public static int cHEARING_Profound = cBMI_Obese;

    // Visual Acuity Left & Right (11 items = 20/200 || 20/100, 20/70 || 20/50, 20/40, 20/30 || 20/25, 20/20, 20/15, 20/10, 20/5)
    // Re-using colors
    public static int cVISION_Normal = cBMI_Normal; // [6, 7, 8, 9, 10] = 20/25 , 20/20, 20/15, 20/10, 20/5 // Normal Vision
    public static int cVISION_Mild = cBMI_Underweight; // [3, 4, 5] = 20/50, 20/40, 20/30 // Near-normal Vision
    public static int cVISION_Moderate = cBMI_Overweight; // [1, 2] = 20/100, 20/70  // Moderate Low Vision
    public static int cVISION_Severe = cBMI_Obese; // [0] = 20/200 // Severe Low Vision


    // Color Vision (2 items = Normal, Abnormal)
    // Gross Motor (3 items = Pass, Fail, N/A)
    // Fine Motor Dominant & Non (2 items = Pass, Fail)
    // Fine Motor Hold (2 items = Hold, Not Hold)

    public static int cPass = Color.rgb(116, 227, 4);
    public static int cFail = Color.rgb(253, 54, 0);





    // Underweight, Normal, Overweight, Obese, N/A
    public static int[] csBMI = new int[] {cBMI_Underweight, cBMI_Normal, cBMI_Overweight, cBMI_Obese, cBMI_NA};

    // Normal Hearing, Mild Hearing Loss, Moderate Hearing Loss, Moderately Severe Hearing Loss, Severe Hearing Loss, Profound Hearing Loss
    public static int[] csHEARING = new int[] {cHEARING_Normal, cHEARING_Mild, cHEARING_Moderate, cHEARING_ModSevere, cHEARING_Severe, cHEARING_Profound};


    // Visual Acuity (11 items)
    public static int[] csVISION = new int[] {
            cVISION_Severe,
            cVISION_Moderate, cVISION_Moderate,
            cVISION_Mild, cVISION_Mild, cVISION_Mild,
            cVISION_Normal, cVISION_Normal, cVISION_Normal, cVISION_Normal, cVISION_Normal};

    public static int[] cstackVISION = new int[] {
            cVISION_Severe,
            cVISION_Moderate,
            cVISION_Mild,
            cVISION_Normal};


    // Gross Motor (3 items = Pass, Fail, N/A)
    public static int[] csTERNARY = new int[] {cPass, cFail, cBMI_NA};

    // Color Vision (2 items = Normal, Abnormal)
    // Fine Motor Dominant & Non (2 items = Pass, Fail)
    // Fine Motor Hold (2 items = Hold, Not Hold)
    public static int[] csBINARY = new int[] {cPass, cFail};


    public static int[] getMergedStackedColorSet(String recordColumn) {
        switch (recordColumn) {

            case "Visual Acuity Left":
            case "Visual Acuity Right":
                return cstackVISION;

            default:
                return getStackedColorSet(recordColumn);
        }
    }


    public static int[] getStackedColorSet(String recordColumn) {
        switch (recordColumn) {
            case "BMI":
                return csBMI;

            case "Visual Acuity Left":
            case "Visual Acuity Right":
                return csVISION;

            case "Hearing Left":
            case "Hearing Right":
                return csHEARING;

            case "Gross Motor":
                return csTERNARY;

            case "Color Vision":
            case "Fine Motor (Dominant Hand)":
            case "Fine Motor (Non-Dominant Hand)":
            case "Fine Motor (Hold)":
                return csBINARY;

            default:
                return csVISION;
        }
    }

}
