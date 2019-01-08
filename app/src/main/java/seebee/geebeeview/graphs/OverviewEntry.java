package seebee.geebeeview.graphs;

import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;

public class OverviewEntry {
    private HorizontalBarChart stackedBarChart;
    private TextView tvTitle;
    private TextView tvFocusTitle;
    private TextView tvFocusValue;

    public OverviewEntry(HorizontalBarChart chart) {
        this.setStackedBarChart(chart);
    }

    public OverviewEntry(HorizontalBarChart chart, TextView title, TextView focusTitle, TextView focusValue) {
        this.setStackedBarChart(chart);
        this.setTvTitle(title);
        this.setTvFocusTitle(focusTitle);
        this.setTvFocusValue(focusValue);
    }

    public HorizontalBarChart getStackedBarChart() {
        return stackedBarChart;
    }

    public void setStackedBarChart(HorizontalBarChart stackedBarChart) {
        this.stackedBarChart = stackedBarChart;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public void setTvTitle(TextView tvTitle) {
        this.tvTitle = tvTitle;
    }

    public TextView getTvFocusTitle() {
        return tvFocusTitle;
    }

    public void setTvFocusTitle(TextView tvFocusTitle) {
        this.tvFocusTitle = tvFocusTitle;
    }

    public TextView getTvFocusValue() {
        return tvFocusValue;
    }

    public void setTvFocusValue(TextView tvFocusValue) {
        this.tvFocusValue = tvFocusValue;
    }
}
