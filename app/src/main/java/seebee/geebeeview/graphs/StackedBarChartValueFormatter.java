package seebee.geebeeview.graphs;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class StackedBarChartValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public StackedBarChartValueFormatter() {

        // format values to 1 decimal digit
        mFormat = new DecimalFormat("###.##");
    }

    @Override
    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
        if(v == 0) {
            return "";
        }
        else {
            return mFormat.format(v) + " %";
        }
    }
}
