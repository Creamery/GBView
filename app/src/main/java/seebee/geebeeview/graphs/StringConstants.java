package seebee.geebeeview.graphs;

public class StringConstants {

    // Visual Acuity (11 items) Must be equivalent to ColorThemes.csVISION
    public static String[] strVISION = new String[] {
            "Severe Loss",
            "Moderate Loss", "Moderate Loss",
            "Near-normal", "Near-normal", "Near-normal",
            "Normal", "Normal", "Normal", "Normal", "Normal"};

    public enum MergeType {
        START, CONT, END, NONE
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
