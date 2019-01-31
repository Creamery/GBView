package seebee.geebeeview.graphs;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class PatientChartIAxisFormatter implements IAxisValueFormatter {

    private String[] mValues;

    public PatientChartIAxisFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if(value == -1)
            return "No data";
        else {
            return mValues[(int) value];
        }
    }
}