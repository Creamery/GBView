package seebee.geebeeview.graphs;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class ProtectedLineChartRenderer extends DataRenderer {

    public ProtectedLineChartRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
    }

    @Override
    public void initBuffers() {

    }

    @Override
    public void drawData(Canvas canvas) {

    }

    @Override
    public void drawValues(Canvas canvas) {
    }

    @Override
    public void drawExtras(Canvas canvas) {

    }

    @Override
    public void drawHighlighted(Canvas canvas, Highlight[] highlights) {

    }
}