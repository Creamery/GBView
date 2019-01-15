package seebee.geebeeview.containers;

public class StringConstants {

    public static final String COL_VISUAL_ACUITY_LEFT = "Visual Acuity Left";
    public static final String COL_VISUAL_ACUITY_RIGHT = "Visual Acuity Right";
    public static final String COL_COLOR_VISION = "Color Vision";

    public static final String COL_HEARING_LEFT = "Hearing Left";
    public static final String COL_HEARING_RIGHT = "Hearing Right";

    public static final String COL_GROSS_MOTOR = "Gross Motor";

    public static final String COL_FINE_DOMINANT = "Fine Motor (Dominant Hand)";
    public static final String COL_FINE_NON_DOMINANT = "Fine Motor (Non-Dominant Hand)";
    public static final String COL_FINE_HOLD = "Fine Motor (Hold)";




    // Visual Acuity (11 items) Must be equivalent to ColorThemes.csVISION
    public static String[] strVISION = new String[] {
            "Severe Loss",
            "Moderate Loss", "Moderate Loss",
            "Near-normal", "Near-normal", "Near-normal",
            "Normal", "Normal", "Normal", "Normal", "Normal"};

    public static String[] strMERGED_VISION = new String[] {
            "Severe Loss",
            "Moderate Loss",
            "Near-normal",
            "Normal"};

    public enum MergeType {
        START, CONT, END, NONE
    }

    public static String[] getMergedLabels(String recordColumn, String[] originalLabels) {
        switch(recordColumn) {
            case COL_VISUAL_ACUITY_LEFT:
            case COL_VISUAL_ACUITY_RIGHT:
                return strMERGED_VISION;

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
            case "Visual Acuity Left":
            case "Visual Acuity Right":
                return mergeVISION[index];
            default:
                return MergeType.NONE;
        }
    }
    public static String getEditedFocusLabel(String recordName, String focusLabel, int index) {
        switch (recordName) {
            case "Visual Acuity Left":
            case "Visual Acuity Right":
                return strVISION[index];
            default:
                return focusLabel;
        }
    }
}
