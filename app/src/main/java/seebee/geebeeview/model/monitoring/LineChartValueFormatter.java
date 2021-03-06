package seebee.geebeeview.model.monitoring;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
//import com.github.mikephil.charting.formatter.ValueFormatter;
//import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import seebee.geebeeview.containers.StringConstants;


/**
 * Created by Joy on 7/18/2017.
 */

public class LineChartValueFormatter  {
    //private final static float[] visualAcuity = {-200f, -100f, -70f, -50f, -40f, -30f, -30f, -25f, 20f, -15f, -10f, -5f};
    private final static String TAG = "LineChartValueFormatter";

    public static IAxisValueFormatter getYAxisValueFormatterBMI(final IdealValue idealValue) {
        return new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return ConvertBMI(v, idealValue);
            }
        };
    }

    // TODO deprecated
//    public static YAxisValueFormatter getYAxisValueFormatterBMI(final IdealValue idealValue) {
//        return new YAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, YAxis yAxis) {
//                return ConvertBMI(v, idealValue);
//            }
//        };
//    }

    private static String ConvertBMI(float v, IdealValue idealValue) {
        return v+"";
//        String result = "";
//        if(idealValue != null) {
//            if (v <= idealValue.getN3SD()) {
//                result = "Severe Thinness";
//            } else if (v > idealValue.getN3SD() && v <= idealValue.getN2SD()) {
//                result = "Thinness";
//            } else if (v > idealValue.getN2SD() && v < idealValue.getP1SD()) {
//                result = "Normal";
//            } else if (v >= idealValue.getP1SD() && v < idealValue.getP2SD()) {
//                result = "Overweight";
//            } else if (v >= idealValue.getP2SD()) {
//                result = "Obese";
//            }
//        }
//        return result;
    }
    public static float ConvertVisualAcuity(String visualAcuity) {
        float value;
        int lowestValue = StringConstants.VA_LOWEST_VALUE;
        switch (visualAcuity) {
            case "20/200": value = 1;
                break;
            case "20/100": value = 2;
                break;
            case "20/70": value = 3;
                break;
            case "20/50": value = 4;
                break;
            case "20/40": value = 5;
                break;
            case "20/30": value = 6;
                break;
            case "20/25": value = 7;
                break;
            case "20/20": value = 8;
                break;
            case "20/15": value = 9;
                break;
            case "20/10": value = 10;
                break;
            case "20/5": value = 11;
                break;

            default:
                value = 0;
        }
        return value;
    }
    // TODO delete when done (original conversion for line chart)
//    public static float ConvertVisualAcuity(String visualAcuity) {
//        float value;
//        switch (visualAcuity) {
//            case "20/200": value = -200f;
//                break;
//            case "20/100": value = -100f;
//                break;
//            case "20/70": value = -70f;
//                break;
//            case "20/50": value = -50f;
//                break;
//            case "20/40": value = -40f;
//                break;
//            case "20/30": value = -30f;
//                break;
//            case "20/25": value = -25f;
//                break;
//            default:
//            case "20/20": value = -20f;
//                break;
//            case "20/15": value = -15f;
//                break;
//            case "20/10": value = -10f;
//                break;
//            case "20/5": value = -5f;
//                break;
//        }
//        return value;
//    }

    public static IAxisValueFormatter getYAxisValueFormatterVisualAcuity() {
        Log.v(TAG, "getYAxisValueFormatter");
        return new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return ConvertVisualAcuity(v);
            }
        };
    }

    // TODO Deprecated
//    public static IAxisValueFormatter  getYAxisValueFormatterVisualAcuity() {
//        Log.v(TAG, "getYAxisValueFormatter");
//        return new YAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, YAxis yAxis) {
//                return ConvertVisualAcuity(v);
//            }
//        };
//    }

    private static String ConvertVisualAcuity(float v) {

        String result = "";
        if(v == 1) {
            result = "20/200";
        } else if(v == 2) {
            result = "20/100";
        } else if(v == 3) {
            result = "20/70";
        } else if(v == 4) {
            result = "20/50";
        } else if(v == 5) {
            result = "20/40";
        } else if(v == 6) {
            result = "20/30";
        } else if(v == 7) {
            result = "20/25";
        } else if(v == 8) {
            result = "20/20";
        } else if(v == 9) {
            result = "20/15";
        } else if(v == 10) {
            result = "20/10";
        } else if(v == 11) {
            result = "20/5";
        } else if (v == 0){
            result = "N/A";
        }
        return result;
    }
    // Original visual acuity value formatter TODO delete when done
//    private static String ConvertVisualAcuity(float v) {
//        String result;
//        if(v <= -200f) {
//            result = "20/200";
//        } else if(v <= -100f) {
//            result = "20/100";
//        } else if(v <= -70f) {
//            result = "20/70";
//        } else if(v <= -50f) {
//            result = "20/50";
//        } else if(v <= -40f) {
//            result = "20/40";
//        } else if(v <= -30f) {
//            result = "20/30";
//        } else if(v <= -25f) {
//            result = "20/25";
//        } else if(v <= -20f) {
//            result = "20/20";
//        } else if(v <= -15f) {
//            result = "20/15";
//        } else if(v <= -10f) {
//            result = "20/10";
//        } else if(v <= -5f) {
//            result = "20/5";
//        } else {
//            result = "N/A";
//        }
//        return result;
//    }

    public static IValueFormatter getValueFormatterVisualAcuity() {
        return new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return ConvertVisualAcuity(v);
            }
        };
    }

    // TODO deprecated
//    public static ValueFormatter getValueFormatterVisualAcuity() {
//        return new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
//                return ConvertVisualAcuity(v);
//            }
//        };
//    }

    public static float ConvertColorVision(String colorVision) {
        float result;
        if(colorVision.contentEquals("Abnormal")) {
            result = 0;
        } else {
            result = 1;
        }
        return result;
    }
    public static IAxisValueFormatter getYAxisValueFormatterColorVision() {
        return new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return ConvertColorVision(v);
            }
        };
    }
    // TODO deprecated
//    public static YAxisValueFormatter getYAxisValueFormatterColorVision() {
//        return new YAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, YAxis yAxis) {
//                return ConvertColorVision(v);
//            }
//        };
//    }

    private static String ConvertColorVision(float v) {
        String result = "";
        if(v == 1) {
            result = "Normal";
        } else if (v == 0) {
            result = "Abnormal";
        }
        return result;
    }

    public static IValueFormatter getValueFormatterColorVision() {
        return new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return ConvertColorVision(v);
            }
        };
    }

    // TODO derpecated
//    public static ValueFormatter getValueFormatterColorVision() {
//        return new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
//                return ConvertColorVision(v);
//            }
//        };
//    }

    public static float ConvertHearing(String hearing) {
        float result;
        switch (hearing) {
            case "Normal Hearing": result = 6;
                break;
            case "Mild Hearing Loss": result = 5;
                break;
            case "Moderate Hearing Loss": result = 4;
                break;
            case "Moderately-Severe Hearing Loss": result = 3;
                break;
            case "Severe Hearing Loss": result = 2;
                break;
            case "Profound Hearing Loss": result = 1;
                break;
            default:
                result = 0;
                break;
        }
        Log.e("GMRESULT", hearing+" "+result);
        return result;
    }

    public static String ConvertGrossMotor(float grossMotor) {
        String result = "";
        int value = (int) grossMotor;
        switch (value) {
            case 2: result = "Pass";
                break;
            case 1: result = "Fail";
                break;
            case 0:
                result = "N/A";
                break;
        }
        Log.e("GMRESULT", value+" "+result);
        return result;
    }

    // (0​ ​-​ ​Pass;​ ​1​ ​-​ ​N/A;​ ​2​ ​-​ ​Fail)
    public static int ConvertGrossMotor(String grossMotor) {
        int result;
        switch (grossMotor) {
            case "Pass": result = 2;// = "Pass";
                break;
            case "Fail": result = 1; // "Fail";
                break;
            case "N/A": result = 0;// "N/A";
                break;
            default:
                result = 0;
        }
        return result;
    }

    private static String ConvertHearing(float value) {
        String result = "";
        if(value == 6) {
            result = "Normal Hearing";
        } else if(value == 5) {
            result = "Mild Hearing Loss";
        } else if(value == 4) {
            result = "Moderate Hearing Loss";
        } else if(value == 3) {
            result = "Moderately-Severe Hearing Loss";
        } else if(value == 2) {
            result = "Severe Hearing Loss";
        } else if(value == 1){
            result = "Profound Hearing Loss";
        }
        else if(value == 0){
            result = "N/A";
        }
        return result;
    }

    public static IAxisValueFormatter getYAxisValueFormatterGrossMotor() {
        return new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return ConvertGrossMotor(v);
            }
        };
    }

    public static IAxisValueFormatter getYAxisValueFormatterHearing() {
        return new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return ConvertHearing(v);
            }
        };
    }

    public static IValueFormatter getValueFormatterHearing() {
        return new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return ConvertHearing(v);
            }
        };
    }

    // TODO deprecated
//    public static YAxisValueFormatter getYAxisValueFormatterHearing() {
//        return new YAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, YAxis yAxis) {
//                return ConvertHearing(v);
//            }
//        };
//    }
//
//    public static ValueFormatter getValueFormatterHearing() {
//        return new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
//                return ConvertHearing(v);
//            }
//        };
//    }
    public static float ConvertMotorLabel(float v) {
        float result = 0;
        if(v == 0) {
            result = 1;
        } else if (v == 1) {
            result = 0;
        }
        return result;
    }
    private static String ConvertMotor(float v) {
        String result = "";
        int value = (int) v;
        if(value == 1) {
            result = "Pass";
        } else if (value == 0) {
            result = "Fail";
        }
        return result;
    }
    public static IAxisValueFormatter getYAxisValueFormatterMotor() {
        return new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return ConvertMotor(v);
            }
        };
    }

    public static IValueFormatter getValueFormatterMotor() {
        return new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return ConvertMotor(v);
            }
        };
    }

    public static IValueFormatter getValueFormatterBMI(final IdealValue idealValue) {
        return new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return ConvertBMI(v, idealValue);
            }
        };
    }

    // TODO deprecated
//    public static YAxisValueFormatter getYAxisValueFormatterMotor() {
//        return new YAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, YAxis yAxis) {
//                return ConvertMotor(v);
//            }
//        };
//    }
//
//    public static ValueFormatter getValueFormatterMotor() {
//        return new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
//                return ConvertMotor(v);
//            }
//        };
//    }
//
//    public static ValueFormatter getValueFormatterBMI(final IdealValue idealValue) {
//        return new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
//                return ConvertBMI(v, idealValue);
//            }
//        };
//    }
}
