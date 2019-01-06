package seebee.geebeeview.graphs;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class LineChartIValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public LineChartIValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal

    }


    @Override
    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
        // write your logic here
        return mFormat.format(v) + " $"; // e.g. append a dollar-sign
    }
}