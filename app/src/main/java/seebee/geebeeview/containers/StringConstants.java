package seebee.geebeeview.containers;

public class StringConstants {

    // For tabs in patient view
    public static final String COL_HEIGHT = "Height";
    public static final String COL_WEIGHT = "Weight";
    // Record columns
    public static final String COL_BMI = "BMI";
    public static final String COL_VA_LEFT = "Visual Acuity Left";
    public static final String COL_VA_RIGHT = "Visual Acuity Right";
    public static final String COL_COLOR_VISION = "Color Vision";

    public static final String COL_HEAR_LEFT = "Hearing Left";
    public static final String COL_HEAR_RIGHT = "Hearing Right";

    public static final String COL_GROSS_MOTOR = "Gross Motor";

    public static final String COL_FINE_DOMINANT = "Fine Motor (Dominant Hand)";
    public static final String COL_FINE_NON_DOMINANT = "Fine Motor (Non-Dominant Hand)";
    public static final String COL_FINE_HOLD = "Fine Motor (Hold)";

    public static final String MSG_LOADING_GRAPHS = "Loading graphs. Please wait...";
    // Target values
    public static final String TAR_BMI = "Normal";

    public static final String TAR_VA = "Normal";
    public static final String TAR_COLOR_VISION = "Normal";

    public static final String TAR_HEAR = "Normal";

    public static final String TAR_GROSS_MOTOR = "Pass";
    public static final String TAR_FINE_DOMINANT = "Pass";
    public static final String TAR_FINE_NON_DOMINANT = "Pass";
    public static final String TAR_FINE_HOLD = "Hold";

    // Indices
    public static final int INDEX_HEART = 0;
    public static final int INDEX_EYE = 1;
    public static final int INDEX_EAR = 2;
    public static final int INDEX_BODY = 3;
    public static final int INDEX_HAND = 4;

    // Visual Acuity (11 items) Must be equivalent to ColorThemes.csVISION
    public static String[] strVISION = new String[] {
            "Severe Loss",
            "Moderate Loss", "Moderate Loss",
            "Near-normal", "Near-normal", "Near-normal",
            "Normal", "Normal", "Normal", "Normal", "Normal"};

    public static String[] strVISION_MERGED = new String[] {
            "Severe Loss",
            "Moderate Loss",
            "Near-normal",
            "Normal"};
    private static String[] strHEARING_MERGED = {
            "Normal",
            "Mild Loss",
            "Moderate Loss",
            "Moderately-Severe Loss",
            "Severe Loss",
            "Profound Loss"};
    public enum MergeType {
        START, CONT, END, NONE
    }


    public static String getTargetLabel(String recordColumn) {
        switch(recordColumn) {

            case COL_BMI:
                return TAR_BMI;

            case COL_VA_LEFT:
            case COL_VA_RIGHT:
                return TAR_VA;

            case COL_COLOR_VISION:
                return TAR_COLOR_VISION;

            case COL_HEAR_LEFT:
            case COL_HEAR_RIGHT:
                return TAR_HEAR;

            case COL_GROSS_MOTOR:
                return TAR_GROSS_MOTOR;

            case COL_FINE_DOMINANT:
            case COL_FINE_NON_DOMINANT:
                return TAR_FINE_DOMINANT;

            case COL_FINE_HOLD:
                return TAR_FINE_HOLD;

            default:
                return TAR_BMI;
        }
    }


    public static String[] getMergedLabels(String recordColumn, String[] originalLabels) {
        switch(recordColumn) {
            case COL_VA_LEFT:
            case COL_VA_RIGHT:
                return strVISION_MERGED;
        }
        return originalLabels;
    }

    public static MergeType[] mergeVISION = new MergeType[] {
            MergeType.NONE,
            MergeType.START, MergeType.END,
            MergeType.START, MergeType.CONT, MergeType.END,
            MergeType.START, MergeType.CONT, MergeType.CONT, MergeType.CONT, MergeType.END};

    public static MergeType isMergeStartingIndex(String recordName, int index) {
        switch (recordName) {
            case COL_VA_LEFT:
            case COL_VA_RIGHT:
                return mergeVISION[index];

            default:
                return MergeType.NONE;
        }
    }
    public static String getEditedFocusLabel(String recordName, String focusLabel, int index) {
        switch (recordName) {
            case COL_VA_LEFT:
            case COL_VA_RIGHT:
                return strVISION_MERGED[index];

            default:
                return focusLabel;
        }
    }
}
