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
        String result;
        if(v == 1) {
            result = "Normal";
        } else {
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
        float result = 0;
        int lowestValue = StringConstants.HEARING_LOWEST_VALUE;
        switch (hearing) {
            case "Normal Hearing": result = lowestValue+1;
                break;
            case "Mild Hearing Loss": result = lowestValue-1;
                break;
            case "Moderate Hearing Loss": result = lowestValue-2;
                break;
            case "Moderately-Servere Hearing Loss": result = lowestValue-3;
                break;
            case "Severe Hearing Loss": result = lowestValue-4;
                break;
            case "Profound Hearing Loss": result = lowestValue-5;
                break;
        }
        return result;
    }

    private static String ConvertHearing(float value) {
        String result = "N/A";
        int lowestValue = StringConstants.HEARING_LOWEST_VALUE;
        if(value >= lowestValue+1) {
            result = "Normal Hearing";
        } else if(value >= lowestValue-1) {
            result = "Mild Hearing Loss";
        } else if(value >= lowestValue-2) {
            result = "Moderate Hearing Loss";
        } else if(value >= lowestValue-3) {
            result = "Moderately-Servere Hearing Loss";
        } else if(value >= lowestValue-4) {
            result = "Severe Hearing Loss";
        } else if(value >= lowestValue-5){
            result = "Profound Hearing Loss";
        }
        return result;
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

    private static String ConvertMotor(float v) {
        String result = "N/A";
        if(v == 0) {
            result = "Pass";
        } else if (v == 1) {
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
