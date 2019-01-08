package seebee.geebeeview.graphs;

public class ChartDataValue {
    String[] xData;
    int[] yDataLeft, yDataRight;

    public String[] getxData() {
        return xData;
    }

    public void setxData(String[] xData) {
        this.xData = xData;
    }

    public int[] getyDataLeft() {
        return yDataLeft;
    }

    public void setyDataLeft(int[] yDataLeft) {
        this.yDataLeft = yDataLeft;
    }

    public int[] getyDataRight() {
        return yDataRight;
    }

    public void setyDataRight(int[] yDataRight) {
        this.yDataRight = yDataRight;
    }
}
