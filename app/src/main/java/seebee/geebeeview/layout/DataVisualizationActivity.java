package seebee.geebeeview.layout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import seebee.geebeeview.R;
import seebee.geebeeview.adapter.FilterAdapter;
import seebee.geebeeview.adapter.TextHolderAdapter;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.graphs.ChartDataValue;
import seebee.geebeeview.graphs.StackedBarChartIAxisFormatter;
import seebee.geebeeview.graphs.StackedBarChartValueFormatter;
import seebee.geebeeview.model.account.Dataset;
import seebee.geebeeview.model.consultation.School;
import seebee.geebeeview.model.monitoring.PatientRecord;
import seebee.geebeeview.model.monitoring.Record;
import seebee.geebeeview.model.monitoring.ValueCounter;
import seebee.geebeeview.sidebar.ColorThemes;
import seebee.geebeeview.sidebar.DataVisualizationSidebar;
import seebee.geebeeview.sidebar.General;
import seebee.geebeeview.spinner.CustomSpinnerAdapter;
import seebee.geebeeview.spinner.CustomSpinnerItem;


public class DataVisualizationActivity extends AppCompatActivity
        implements AddFilterDialogFragment.AddFilterDialogListener,
        AddDatasetDialogFragment.AddDatasetDialogListener, FilterAdapter.FilterAdapterListener, TextHolderAdapter.TextListener {
    private static final String TAG = "DataVisualActivity";
    public static final float VALUE_TEXT_SIZE = 14f;
    public static final float DESCRIPTION_TEXT_SIZE = 16f;
    public static final float AXIS_TEXT_SIZE = 14f;
    public static final float LEGEND_TEXT_SIZE = 16f;

    public static final int CHART_AXIS_TEXT_COLOR = Color.GRAY;
    public static final int CHART_LEGEND_TEXT_COLOR = Color.GRAY;
    public static final int CHART_DESC_TEXT_COLOR = Color.GRAY;
    public static final int CHART_VALUE_TEXT_COLOR = Color.GRAY;

    ArrayList<String> datasetList, filterList;
    TextHolderAdapter datasetAdapter;
    FilterAdapter filterAdapter;
    RecyclerView rvDataset, rvFilter;
    Button btnAddDataset, btnAddFilter, btnViewPatientList, btnViewHPIList;
    RelativeLayout graphLayoutLeft, graphLayoutRight; /* space where graph will be set on */
    RelativeLayout graphLayoutCenter; // added

    RelativeLayout
            graphBMI,
            graphVisualAcuityLeft, graphVisualAcuityRight, graphColorBlindness,
            graphHearingLeft, graphHearingRight,
            graphGrossMotor,
            graphFineMotorDominant, graphFineMotorNon;

    HorizontalBarChart stackedBMI,
            stackedVisualAcuityLeft, stackedVisualAcuityRight, stackedColorBlindness,
            stackedHearingLeft, stackedHearingRight,
            stackedGrossMotor,
            stackedFineMotorDominant, stackedFineMotorNon;

    int schoolID;
    String schoolName, date;
    PieChart pieChartLeft, pieChartRight;
    BarChart barChart;

    HorizontalBarChart stackedBarChart;
    ArrayList<HorizontalBarChart> stackedBarCharts;
    ArrayList<RelativeLayout> graphStackedBarCharts;
    String[] recordColumns;

    ScatterChart scatterChart;
    BubbleChart bubbleChart;
    ArrayList<PatientRecord> recordsLeft, recordsRight;
    String[] xData, possibleAge;
    int[] yDataLeft, yDataRight;


    ArrayList<Dataset> datasets;
    /* attributes for addFilterDialog */
    ArrayList<String> gradeLevels;
    private TextView tvDataset, tvFilter, tvChart, tvData, tvRightChart, tvDataHeader;
    private Spinner spRecordColumn, spChartType, spRightChart;
    private String recordColumn = "BMI", rightChartContent = "National Profile";

    private String chartType = "Pie Chart";

    private ViewGroup.LayoutParams paramsLeft, paramsRight, paramsCenter, paramsStacked;
    private ArrayList<ViewGroup.LayoutParams> paramsOverview;

    private int offsetTopBottom;
    private int offsetLeftRight;
    private float offsetPercent = 0.0f;

    private float offsetYDivider = 8f;
    private String provinceName, municipalityName;

    private DataVisualizationSidebar sidebarManager;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        // Place layout width/height retrieval here to avoid returning 0
    }

    private int computePercent(float value, float percent) {
        return Math.round(value*percent);
    }

    private int computePercentHalf(float value, float percent) {
        return Math.round(value*percent/2f);
    }
    private void refreshChartParams() {
        if(paramsLeft != null) {
            offsetTopBottom = computePercent(graphLayoutLeft.getHeight(), offsetPercent);
            offsetLeftRight = computePercent(graphLayoutLeft.getWidth(), offsetPercent);

            paramsLeft.height = graphLayoutLeft.getHeight()-offsetTopBottom; // ViewGroup.LayoutParams.MATCH_PARENT;
            paramsLeft.width = graphLayoutLeft.getWidth()-offsetLeftRight; // ViewGroup.LayoutParams.MATCH_PARENT;
        }
        if(paramsRight != null) {
            offsetTopBottom = computePercent(graphLayoutRight.getHeight(), offsetPercent);
            offsetLeftRight = computePercent(graphLayoutRight.getWidth(), offsetPercent);

            paramsRight.height = graphLayoutRight.getHeight()-offsetTopBottom; // ViewGroup.LayoutParams.MATCH_PARENT;
            paramsRight.width = graphLayoutRight.getWidth()-offsetLeftRight; // ViewGroup.LayoutParams.MATCH_PARENT;
        }
        if(paramsCenter != null) {
            offsetTopBottom = computePercent(graphLayoutCenter.getHeight(), offsetPercent);
            offsetLeftRight = computePercent(graphLayoutCenter.getWidth(), offsetPercent);

            float height = graphLayoutCenter.getHeight()-offsetTopBottom;
//            paramsCenter.height = Math.round(height+(height/4f)); // ViewGroup.LayoutParams.MATCH_PARENT;
            paramsCenter.height = graphLayoutCenter.getHeight()-offsetTopBottom+10; // ViewGroup.LayoutParams.MATCH_PARENT; TODO ADD CHART HEIGHT
            paramsCenter.width = graphLayoutCenter.getWidth()-offsetLeftRight; //ViewGroup.LayoutParams.MATCH_PARENT;
        }
//        if(paramsStacked != null) {
//            offsetTopBottom = computePercent(graphBMI.getHeight(), offsetPercent);
//            offsetLeftRight = computePercent(graphBMI.getWidth(), offsetPercent);
//
//            float height = graphBMI.getHeight()-offsetTopBottom;
////            paramsCenter.height = Math.round(height+(height/4f)); // ViewGroup.LayoutParams.MATCH_PARENT;
//            paramsCenter.height = graphBMI.getHeight()-offsetTopBottom+10; // ViewGroup.LayoutParams.MATCH_PARENT; TODO ADD CHART HEIGHT
//            paramsCenter.width = graphBMI.getWidth()-offsetLeftRight; //ViewGroup.LayoutParams.MATCH_PARENT;
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_visualization);
        // lock orientation of activity to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // get extras (schoolName & date)
        schoolName = getIntent().getStringExtra(School.C_SCHOOLNAME);
        schoolID = getIntent().getIntExtra(School.C_SCHOOL_ID, 0);
        date = getIntent().getStringExtra(Record.C_DATE_CREATED);

//        tvTitle = (TextView) findViewById(R.id.tv_data_visualization_title);
        tvDataset = (TextView) findViewById(R.id.tv_dv_dataset);
        tvFilter = (TextView) findViewById(R.id.tv_dv_filter);
        tvChart = (TextView) findViewById(R.id.tv_dv_chart);
        tvData = (TextView) findViewById(R.id.tv_dv_data);
        tvRightChart = (TextView) findViewById(R.id.tv_dv_right_chart);
        btnAddDataset = (Button) findViewById(R.id.btn_add_dataset);
        btnAddFilter = (Button) findViewById(R.id.btn_add_filter);
        btnViewPatientList = (Button) findViewById(R.id.btn_view_patient_list);
        btnViewHPIList = (Button) findViewById(R.id.btn_view_hpi_list);
        rvDataset = (RecyclerView) findViewById(R.id.rv_dv_dataset);
        rvFilter = (RecyclerView) findViewById(R.id.rv_dv_filter);
        graphLayoutLeft = (RelativeLayout) findViewById(R.id.graph_container_left);
        graphLayoutRight = (RelativeLayout) findViewById(R.id.graph_container_right);
        graphLayoutCenter = (RelativeLayout) findViewById(R.id.graph_container_center); // Added


        // TODO Add Here
        graphBMI = (RelativeLayout) findViewById(R.id.graph_container_bmi); // Added
        graphVisualAcuityLeft = (RelativeLayout) findViewById(R.id.graph_container_va_left); // Added
        graphVisualAcuityRight = (RelativeLayout) findViewById(R.id.graph_container_va_right); // Added
        graphColorBlindness = (RelativeLayout) findViewById(R.id.graph_container_va_color); // Added

        spRecordColumn = (Spinner) findViewById(R.id.sp_record_column);
        spChartType = (Spinner) findViewById(R.id.sp_chart_type);
        spRightChart = (Spinner) findViewById(R.id.sp_right_chart_content);

        tvDataHeader = (TextView) findViewById(R.id.tv_data_header);


        // TODO Set default font
        /* get fonts from assets */
//        Typeface chawpFont = Typeface.createFromAsset(getAssets(), "font/chawp.ttf");
//        Typeface chalkFont = Typeface.createFromAsset(getAssets(), "font/DJBChalkItUp.ttf");
        /* set font of text */
//        tvDataset.setTypeface(chalkFont);
//        tvFilter.setTypeface(chalkFont);
//        tvChart.setTypeface(chalkFont);
//        tvData.setTypeface(chalkFont);
//        tvRightChart.setTypeface(chalkFont);
//        btnAddDataset.setTypeface(chawpFont);
//        btnAddFilter.setTypeface(chawpFont);
//        btnViewHPIList.setTypeface(chawpFont);
//        btnViewPatientList.setTypeface(chawpFont);

        /* set listener for button view hpi list */
        btnViewHPIList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sidebarManager.isSidebarOpen()) {
                    sidebarManager.getBtnOpenSidebar().performClick();
                }

                if(getHPIs() > 0) {
                    Intent intent = new Intent(getBaseContext(), HPIListActivity.class);
                    intent.putExtra(School.C_SCHOOL_ID, schoolID);
                    intent.putExtra(School.C_SCHOOLNAME, schoolName);
                    startActivity(intent);
                }
            }
        });

        /* set button for view patient list */
        btnViewPatientList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sidebarManager.isSidebarOpen()) {
                    sidebarManager.getBtnOpenSidebar().performClick();
                }

                Intent intent = new Intent(getBaseContext(), PatientListActivity.class);
                intent.putExtra(School.C_SCHOOL_ID, schoolID);
                intent.putExtra(School.C_SCHOOLNAME, schoolName);
                intent.putExtra(Record.C_DATE_CREATED, date);
                startActivity(intent);
                //Log.v(TAG, "started PatientListActivity");
            }
        });

        /* ready recycler view list for dataset */
        datasetList = new ArrayList<>();
        datasetAdapter = new TextHolderAdapter(datasetList, this);
        RecyclerView.LayoutManager dLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvDataset.setLayoutManager(dLayoutManager);
        rvDataset.setItemAnimator(new DefaultItemAnimator());
        rvDataset.setAdapter(datasetAdapter);

        /* initialized the fitlered list */
        recordsLeft = new ArrayList<>();
        getGradeLevels();
        getDatasetList();
        addDatasetToList(schoolName, date);

        /* ready recycler view list for filters */
        filterList = new ArrayList<>();
        filterAdapter = new FilterAdapter(filterList, this);
        RecyclerView.LayoutManager fLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvFilter.setLayoutManager(fLayoutManager);
        rvFilter.setItemAnimator(new DefaultItemAnimator());
        rvFilter.setAdapter(filterAdapter);

        prepareFilterList(null);

        btnAddFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sidebarManager.isSidebarOpen()) {
                    sidebarManager.getBtnOpenSidebar().performClick();
                }

                AddFilterDialogFragment addFilterDialog = new AddFilterDialogFragment();
                addFilterDialog.setGradeLevels(gradeLevels);
                addFilterDialog.show(getFragmentManager(), AddFilterDialogFragment.TAG);
            }
        });

        btnAddDataset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sidebarManager.isSidebarOpen()) {
                    sidebarManager.getBtnOpenSidebar().performClick();
                }

                AddDatasetDialogFragment addDatasetDialog = new AddDatasetDialogFragment();
                addDatasetDialog.setDatasetList(datasets);
                addDatasetDialog.show(getFragmentManager(), AddDatasetDialogFragment.TAG);
            }
        });

        final ArrayAdapter<String> spRecordAdapter = new ArrayAdapter<>(this,
                R.layout.custom_spinner, getResources().getStringArray(R.array.record_column_array));
        spRecordColumn.setAdapter(spRecordAdapter);
        spRecordColumn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recordColumn = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(),
                        "Data Displayed: " + recordColumn,
                        Toast.LENGTH_SHORT).show();
                refreshCharts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                recordColumn = "BMI";
            }
        });

        /* prepare record so that it can be plotted immediately */
        prepareChartData();
        createCharts();


//        ArrayAdapter<String> spChartAdapter = new ArrayAdapter<>(this,
//                R.layout.custom_spinner, getResources().getStringArray(R.array.chart_type_array));
//        ArrayAdapter<String> spChartAdapter = new ArrayAdapter<>(this,
//                R.layout.custom_spinner_image, R.id.tv_spinner, getResources().getStringArray(R.array.chart_type_array));

        // TODO spinner
//        String[] chartNames = getResources().getStringArray(R.array.chart_type_array);
//        int chartIcons[] = {R.drawable.img_templogo, R.drawable.img_templogo, R.drawable.img_templogo, R.drawable.img_templogo};

//        CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(getApplicationContext(),chartIcons,chartNames);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.custom_spinner_image, R.id.tv_spinner, getResources().getStringArray(R.array.chart_type_array));

//        spChartType.setAdapter(new CustomSpinnerAdapter(this, R.layout.custom_spinner_image, getSpinnerList()));

        String[] charts = getResources().getStringArray(R.array.chart_type_array);
        ArrayList<CustomSpinnerItem> list=new ArrayList<>();
        list.add(new CustomSpinnerItem(charts[0],R.drawable.img_templogo));
        list.add(new CustomSpinnerItem(charts[1],R.drawable.img_templogo));
        list.add(new CustomSpinnerItem(charts[2],R.drawable.img_templogo));
        list.add(new CustomSpinnerItem(charts[3],R.drawable.img_templogo));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this,
                R.layout.custom_spinner_image, R.id.tv_spinner, list);


        spChartType.setAdapter(adapter);
        spChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                chartType = ((CustomSpinnerAdapter)parent.getAdapter()).getItem(position);
                chartType = ((CustomSpinnerItem)(parent.getItemAtPosition(position))).getText();
//                Log.e("CHART", chartType);


//                graphLayoutLeft.removeAllViews();
//                graphLayoutRight.removeAllViews();
//                graphLayoutCenter.removeAllViews();
                // TODO Remove all views
                removeGraphViews();

                if(position == 0){ // Pie Chart
                    graphLayoutLeft.addView(pieChartLeft);
                    graphLayoutRight.addView(pieChartRight);
                    // adjust size of layout
                    paramsLeft = pieChartLeft.getLayoutParams();
                    pieChartLeft.setX(computePercentHalf(graphLayoutLeft.getWidth(), offsetPercent));
                    pieChartLeft.setY(computePercentHalf(graphLayoutLeft.getHeight(), offsetPercent)/offsetYDivider);

                    paramsRight = pieChartRight.getLayoutParams();
                    pieChartRight.setX(computePercentHalf(graphLayoutRight.getWidth(), offsetPercent));
                    pieChartRight.setY(computePercentHalf(graphLayoutRight.getHeight(), offsetPercent)/offsetYDivider);

//                    paramsLeft.height = graphLayoutLeft.getHeight(); // ViewGroup.LayoutParams.MATCH_PARENT;
//                    paramsLeft.width = graphLayoutLeft.getWidth(); // ViewGroup.LayoutParams.MATCH_PARENT;

//                    paramsRight.height = graphLayoutRight.getHeight(); // ViewGroup.LayoutParams.MATCH_PARENT;
//                    paramsRight.width = graphLayoutRight.getWidth(); // ViewGroup.LayoutParams.MATCH_PARENT;

                } else if(position == 1) { // Bar Chart
                    /* add bar chart to layout */
                    graphLayoutCenter.addView(barChart); // TODO edited
                    /* adjust the size of the bar chart */
                    paramsCenter = barChart.getLayoutParams();
                    barChart.setX(computePercentHalf(graphLayoutCenter.getWidth(), offsetPercent));
                    barChart.setY(computePercentHalf(graphLayoutCenter.getHeight(), offsetPercent)/offsetYDivider);


//                    // Stacked bar TODO: trial only, remove
//                    graphLayoutCenter.addView(stackedBarChart); // TODO edited
//                    /* adjust the size of the bar chart */
//                    paramsCenter = stackedBarChart.getLayoutParams();
//                    stackedBarChart.setX(computePercentHalf(graphLayoutCenter.getWidth(), offsetPercent));
//                    stackedBarChart.setY(computePercentHalf(graphLayoutCenter.getHeight(), offsetPercent)/offsetYDivider);
                } else if (position == 2) { // TODO PUT BACK TO Scatter
                    // TODO REMOVE START
                    // Stacked bar TODO: trial only, remove



                    // TODO Removed (latest)
//                    graphLayoutCenter.addView(stackedBarChart); // TODO edited
//                    /* adjust the size of the bar chart */
//                    paramsCenter = stackedBarChart.getLayoutParams();
//                    stackedBarChart.setX(computePercentHalf(graphLayoutCenter.getWidth(), offsetPercent));
//                    stackedBarChart.setY(computePercentHalf(graphLayoutCenter.getHeight(), offsetPercent)/offsetYDivider);
//                    // TODO REMOVE END


                    /*
                    graphLayoutCenter.addView(scatterChart); // TODO edited
                    // adjust the size of the bar chart
                    paramsCenter = scatterChart.getLayoutParams();
                    scatterChart.setX(computePercentHalf(graphLayoutCenter.getWidth(), offsetPercent));
                    scatterChart.setY(computePercentHalf(graphLayoutCenter.getHeight(), offsetPercent)/offsetYDivider);
                    */
                } else {
                    graphLayoutCenter.addView(bubbleChart); // TODO edited

                    /* adjust the size of the bar chart */
                    paramsCenter = bubbleChart.getLayoutParams();
                    bubbleChart.setX(computePercentHalf(graphLayoutCenter.getWidth(), offsetPercent));
                    bubbleChart.setY(computePercentHalf(graphLayoutCenter.getHeight(), offsetPercent)/offsetYDivider);
//                    paramsCenter.height = graphLayoutCenter.getHeight(); // ViewGroup.LayoutParams.MATCH_PARENT;
//                    paramsCenter.width = graphLayoutCenter.getWidth(); //ViewGroup.LayoutParams.MATCH_PARENT;
                }

                // hide control of right chart for scatter and bubble plot
                // TODO edited (added "Bar" constraint)
                if(chartType.contains("Scatter") || chartType.contains("Bubble") || chartType.contains("Bar")) {
                    spRightChart.setVisibility(View.GONE);
                    tvRightChart.setVisibility(View.GONE);
                } else {
                    spRightChart.setVisibility(View.VISIBLE);
                    tvRightChart.setVisibility(View.VISIBLE);
                }
                refreshChartParams();
                addDataSet();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /* Default chart is pie chart */
                chartType = "Pie Chart";
                graphLayoutLeft.addView(pieChartLeft);
                // adjust size of layout
                ViewGroup.LayoutParams params = pieChartLeft.getLayoutParams();
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        });

        ArrayAdapter<String> spRightChartAdapter = new ArrayAdapter<>(this,
                R.layout.custom_spinner, getResources().getStringArray(R.array.right_chart_content_array));
        spRightChart.setAdapter(spRightChartAdapter);
        spRightChart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rightChartContent = parent.getItemAtPosition(position).toString();
                Log.v(TAG, "Right Chart Content: "+rightChartContent);
                prepareRightChartRecords();
                refreshCharts();
                // todo: add code here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                rightChartContent = parent.getItemAtPosition(0).toString();
            }
        });

        tvDataHeader.setText(schoolName);

        setupSidebarFunctionality();
        initializeStackedGraphOverview();
    }

    public void removeGraphViews() {

        graphLayoutLeft.removeAllViews();
        graphLayoutRight.removeAllViews();
        graphLayoutCenter.removeAllViews();

        // TODO add all graphs
        graphBMI.removeAllViews();

        graphVisualAcuityLeft.removeAllViews();
        graphVisualAcuityRight.removeAllViews();
        graphColorBlindness.removeAllViews();
    }

    // TODO EDIT
    public ArrayList<Drawable> getChartIcons(String[] chartTypes) {

//        ArrayList<CustomSpinnerItem> allList = new ArrayList<CustomSpinnerItem>();
//        CustomSpinnerItem item = new CustomSpinnerItem();
        ArrayList<Drawable> iconList = new ArrayList<Drawable>();

        for(int i = 0; i < chartTypes.length; i++) {
            iconList.add(ContextCompat.getDrawable(this, R.drawable.img_templogo));
        }
//        item.setData(getResources().getStringArray(R.array.chart_type_array)[0],R.drawable.img_templogo);
//        allList.add(item);
//        item = new CustomSpinnerItem();
//        item.setData(getResources().getStringArray(R.array.chart_type_array)[1], R.drawable.img_templogo);
//        allList.add(item);
//        item = new CustomSpinnerItem();
//        item.setData(getResources().getStringArray(R.array.chart_type_array)[2], R.drawable.img_templogo);
//        allList.add(item);
//        item = new CustomSpinnerItem();
//        item.setData(getResources().getStringArray(R.array.chart_type_array)[3], R.drawable.img_templogo);
//        allList.add(item);

        return iconList;
    }
    public void setupSidebarFunctionality () {
        // TODO About, Immun, HPI functionality
        sidebarManager = new DataVisualizationSidebar(
                (Button) findViewById(R.id.btn_show_sidebar_icons),
                (Button) findViewById(R.id.btn_sidebar_open_extend),
                (Button) findViewById(R.id.btn_add_dataset),
                (Button) findViewById(R.id.btn_add_filter),
                (Button) findViewById(R.id.btn_view_hpi_list), // HPI LIST
                (Button) findViewById(R.id.btn_view_patient_list), // PATIENT LIST
                (ConstraintLayout) findViewById(R.id.cont_sidebar_blank_hide),
                (Button) findViewById(R.id.btn_back));

        sidebarManager.setItemsSidebarExtend(new ArrayList<ConstraintLayout>());
        sidebarManager.getItemsSidebarExtend().add((ConstraintLayout)findViewById(R.id.sidebar_extend_body_bg_hide));
//        sidebarManager.getItemsSidebarExtend().add((ConstraintLayout)findViewById(R.id.cont_about_extend_hide));
//        sidebarManager.getItemsSidebarExtend().add((ConstraintLayout)findViewById(R.id.cont_hpi_extend_hide));
//        sidebarManager.getItemsSidebarExtend().add((ConstraintLayout)findViewById(R.id.cont_immunization_extend_hide));
        sidebarManager.getItemsSidebarExtend().add(sidebarManager.getContSidebarBlank());

        this.sidebarManager.getBtnSidebarBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        this.sidebarManager.getContSidebarBlank().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If sidebar is open, close it by clicking on the openSidebar button
                if(sidebarManager.isSidebarOpen()) {
                    sidebarManager.getBtnOpenSidebar().performClick();
                }
            }
        });

        this.sidebarManager.getBtnOpenSidebar().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sidebarManager.toggleSidebar();

                // Setting of Visibility has to be done here (not in PatientSidebar or SidebarParent class, or it won't appear
                for(int i = 0; i < sidebarManager.getItemsSidebarExtend().size(); i++) {
                    sidebarManager.getItemsSidebarExtend().get(i).setVisibility(General.getVisibility(sidebarManager.isSidebarOpen()));

                    if(sidebarManager.isSidebarOpen()) {
                        Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_left);
                        sidebarManager.getItemsSidebarExtend().get(i).startAnimation(animSlideDown);
                    }
                    else {
                        Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out_left);
                        sidebarManager.getItemsSidebarExtend().get(i).startAnimation(animSlideDown);
                    }
                }

                // Toast.makeText(ViewPatientActivity.this, sidebarManager.isSidebarOpen()+" "+sidebarManager.getItemsSidebarExtend().size()+" "+sidebarManager.getItemsSidebarExtend().get(0).getVisibility(), Toast.LENGTH_SHORT).show();
            }
        });

        this.sidebarManager.getBtnOpenSidebarExtend().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // If sidebar is closed, open it by clicking on the openSidebar button
//                if(!sidebarManager.isSidebarOpen()) {
                    sidebarManager.getBtnOpenSidebar().performClick();
//                }
            }
        });
        // Hide initially
        for(int i = 0; i < sidebarManager.getItemsSidebarExtend().size(); i++) {
            sidebarManager.getItemsSidebarExtend().get(i).setVisibility(General.getVisibility(false));
        }
    }

    private void refreshCharts() {
        /* change the contents of the chart */
        if(pieChartLeft != null) {
            prepareChartData();
            if(chartType.contentEquals("Pie Chart")) {
                pieChartLeft.clear();
            } else if(chartType.contentEquals("Bar Chart")) {
                barChart.clear(); // TODO add stacked here

            } else if (chartType.contentEquals("Scatter Chart")) { // TODO EDITED FOR TESTING ONLY, CHANGE BACK TO SCATTER
//                scatterChart.clear(); // TODO ENABLE THIS
                stackedBarChart.clear();
                // TODO clear all overview (or not)
                clearOverviewCharts();
            } else {
                bubbleChart.clear();
            }
            addDataSet();
        }
    }

    // Clear all overview charts
    private void clearOverviewCharts() {
        for (HorizontalBarChart chart : stackedBarCharts) {
            chart.clear();
        }
    }
    private void createCharts() {
        //graphLayoutLeft.setBackgroundColor(Color.LTGRAY);
        pieChartLeft = createPieChart();
        pieChartRight = createPieChart();

        createBarChart();

        createStackedBarChart();

        createScatterChart();
        createBubbleChart();

        addDataSet();
    }

    /* change the contents of xData and yDataLeft */
    private void prepareChartData() {
        ValueCounter valueCounter = new ValueCounter(recordsLeft);
        ValueCounter valueCounterRight = new ValueCounter(recordsRight);
        possibleAge = valueCounter.getPossibleAge();
        switch (recordColumn) {
            default:
            case "BMI":
                xData = valueCounter.getLblBMI();
                yDataLeft = valueCounter.getValBMI();
                yDataRight = valueCounterRight.getValBMI();
                break;
            case "Visual Acuity Left":
                xData = valueCounter.getLblVisualAcuity();
                yDataLeft = valueCounter.getValVisualAcuityLeft();
                yDataRight = valueCounterRight.getValVisualAcuityLeft();
                break;
            case "Visual Acuity Right":
                xData = valueCounter.getLblVisualAcuity();
                yDataLeft = valueCounter.getValVisualAcuityRight();
                yDataRight = valueCounterRight.getValVisualAcuityRight();
                break;
            case "Color Vision":
                xData = valueCounter.getLblColorVision();
                yDataLeft = valueCounter.getValColorVision();
                yDataRight = valueCounterRight.getValColorVision();
                break;
            case "Hearing Left":
                xData = valueCounter.getLblHearing();
                yDataLeft = valueCounter.getValHearingLeft();
                yDataRight = valueCounterRight.getValHearingLeft();
                break;
            case "Hearing Right":
                xData = valueCounter.getLblHearing();
                yDataLeft = valueCounter.getValHearingRight();
                yDataRight = valueCounterRight.getValHearingRight();
                break;
            case "Gross Motor":
                xData = valueCounter.getLblGrossMotor();
                yDataLeft = valueCounter.getValGrossMotor();
                yDataRight = valueCounterRight.getValGrossMotor();
                break;
            case "Fine Motor (Dominant Hand)":
                xData = valueCounter.getLblFineMotor();
                yDataLeft = valueCounter.getValFineMotorDom();
                yDataRight = valueCounterRight.getValFineMotorDom();
                break;
            case "Fine Motor (Non-Dominant Hand)":
                xData = valueCounter.getLblFineMotor();
                yDataLeft = valueCounter.getValFineMotorNonDom();
                yDataRight = valueCounterRight.getValFineMotorNonDom();
                break;
            case "Fine Motor (Hold)":
                xData = valueCounter.getLblFineMotorHold();
                yDataLeft = valueCounter.getValFineMotorHold();
                yDataRight = valueCounterRight.getValFineMotorHold();
                break;
        }
    }

    private ChartDataValue prepareChartData(String recordName) {
        ChartDataValue chartDataValue = new ChartDataValue();

        ValueCounter valueCounter = new ValueCounter(recordsLeft);
        ValueCounter valueCounterRight = new ValueCounter(recordsRight);
        possibleAge = valueCounter.getPossibleAge();

        switch (recordName) {
            default:
            case "BMI":
                chartDataValue.setxData(valueCounter.getLblBMI());
                chartDataValue.setyDataLeft(valueCounter.getValBMI());
                chartDataValue.setyDataRight(valueCounterRight.getValBMI());
                break;

            case "Visual Acuity Left":
                chartDataValue.setxData(valueCounter.getLblVisualAcuity());
                chartDataValue.setyDataLeft(valueCounter.getValVisualAcuityLeft());
                chartDataValue.setyDataRight(valueCounterRight.getValVisualAcuityLeft());
                break;

            case "Visual Acuity Right":
                chartDataValue.setxData(valueCounter.getLblVisualAcuity());
                chartDataValue.setyDataLeft(valueCounter.getValVisualAcuityRight());
                chartDataValue.setyDataRight(valueCounterRight.getValVisualAcuityRight());
                break;

            case "Color Vision":
                chartDataValue.setxData(valueCounter.getLblColorVision());
                chartDataValue.setyDataLeft(valueCounter.getValColorVision());
                chartDataValue.setyDataRight(valueCounterRight.getValColorVision());
                break;

            case "Hearing Left":
                chartDataValue.setxData(valueCounter.getLblHearing());
                chartDataValue.setyDataLeft(valueCounter.getValHearingLeft());
                chartDataValue.setyDataRight(valueCounterRight.getValHearingLeft());
                break;

            case "Hearing Right":
                chartDataValue.setxData(valueCounter.getLblHearing());
                chartDataValue.setyDataLeft(valueCounter.getValHearingRight());
                chartDataValue.setyDataRight(valueCounterRight.getValHearingRight());
                break;

            case "Gross Motor":
                chartDataValue.setxData(valueCounter.getLblGrossMotor());
                chartDataValue.setyDataLeft(valueCounter.getValGrossMotor());
                chartDataValue.setyDataRight(valueCounterRight.getValGrossMotor());
                break;

            case "Fine Motor (Dominant Hand)":
                chartDataValue.setxData(valueCounter.getLblFineMotor());
                chartDataValue.setyDataLeft(valueCounter.getValFineMotorDom());
                chartDataValue.setyDataRight(valueCounterRight.getValFineMotorDom());
                break;

            case "Fine Motor (Non-Dominant Hand)":
                chartDataValue.setxData(valueCounter.getLblFineMotor());
                chartDataValue.setyDataLeft(valueCounter.getValFineMotorNonDom());
                chartDataValue.setyDataRight(valueCounterRight.getValFineMotorNonDom());
                break;

            case "Fine Motor (Hold)":
                chartDataValue.setxData(valueCounter.getLblFineMotorHold());
                chartDataValue.setyDataLeft(valueCounter.getValFineMotorHold());
                chartDataValue.setyDataRight(valueCounterRight.getValFineMotorHold());
                break;
        }
        return chartDataValue;
    }


    // TODO implement onvalue select for overview
    private OnChartValueSelectedListener getOverviewOnChartValueSelectedListener() {
        return new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {

            }

            @Override
            public void onNothingSelected() {

            }
        };
    }
    private OnChartValueSelectedListener getOnChartValueSelectedListener() {
        return new OnChartValueSelectedListener() {
//            @Override TODO Deprecated
//            public void onValueSelected(Entry entry, int i, Highlight highlight) {
//                // display msg when value selected
//                if(entry == null)
//                    return;
//                if(chartType.contains("Scatter") && recordColumn.contains("BMI")) {
//                    Toast.makeText(DataVisualizationActivity.this,
//                            "BMI of a child " + possibleAge[entry.getXIndex()] + " years old = " + entry.getVal(),
//                            Toast.LENGTH_SHORT).show();
//                    Log.v(TAG, "BMI of a child " + possibleAge[entry.getXIndex()] + " years old = " + entry.getVal());
//                } else {
//                    // TODO EDIT TOAST
//                    Toast.makeText(DataVisualizationActivity.this,
//                            xData[entry.getXIndex()] + " = " + entry.getVal() + " children",
//                            Toast.LENGTH_SHORT).show();
//                    Log.v(TAG, xData[entry.getXIndex()] + " = " + entry.getVal() + " children");
//                }
//            }

            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                // display msg when value selected
                // TODO Retrieve age
                if(entry == null)
                    return;
                if(chartType.contains("Scatter") && recordColumn.contains("BMI")) {
                    Toast.makeText(DataVisualizationActivity.this,
                            "BMI of a child "/* + possibleAge[entry.getXIndex()]*/ + " years old = " + entry.getData(),
                            Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "BMI of a child "/* + possibleAge[entry.getXIndex()]*/ + " years old = " + entry.getData());
                } else {
                    // TODO EDIT TOAST
                    Toast.makeText(DataVisualizationActivity.this,
                            /*xData[entry.getXIndex()] + */" = " + entry.getData() + " children",
                            Toast.LENGTH_SHORT).show();
                    Log.v(TAG, /*xData[entry.getXIndex()] + */" = " + entry.getData() + " children");
                }
            }

            @Override
            public void onNothingSelected() {

            }
        };
    }

    private void createBubbleChart() {
        bubbleChart = new BubbleChart(this);

/*      bubbleChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                if(!recordColumn.contains("BMI")) {
                    BubbleEntry bubbleEntry = (BubbleEntry) entry;
                    Toast.makeText(DataVisualizationActivity.this,
                            xData[entry.getXIndex()] + " = " + bubbleEntry.getSize() + " children",
                            Toast.LENGTH_SHORT).show();
                    Log.v(TAG, xData[entry.getXIndex()] + " = " + bubbleEntry.getSize() + " children");
                } else {
                    BubbleEntry bubbleEntry = (BubbleEntry) entry;
                    Toast.makeText(DataVisualizationActivity.this,
                            "Average BMI of children " + bubbleEntry.getXIndex() + " years old = " + bubbleEntry.getSize(),
                            Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Average BMI of children " + bubbleEntry.getXIndex() + " years old = " + bubbleEntry.getSize());
                }
            }

            @Override
            public void onNothingSelected() {

            }
        }); */
    }

    private void createScatterChart() {
        scatterChart = new ScatterChart(this);
        scatterChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());
    }

    private ArrayList<HorizontalBarChart> createOverviewCharts(ArrayList<HorizontalBarChart> charts) {
        for (HorizontalBarChart chart: charts) {
            chart = new HorizontalBarChart(this);
            chart.setOnChartValueSelectedListener(getOverviewOnChartValueSelectedListener());
        }
        return charts;
    }

    private void createStackedBarChart() {
        /* create bar chart */
        stackedBarChart = new HorizontalBarChart(this);
        // set a chart value selected listener
        stackedBarChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());
    }

    private void createBarChart() {
        /* create bar chart */
        barChart = new BarChart(this);
        // set a chart value selected listener
        barChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());
    }

    /* prepare values specifically for piechart only */
    private PieChart createPieChart() {
        /* add pie chart */
        PieChart pieChart = new PieChart(this);

        // configure pie chart
        pieChart.setUsePercentValues(true);

        // enable hole and configure
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(100);

        // enable rotation of the chart by touch
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        // set a chart value selected listener
        pieChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());

        // customize legends
        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setWordWrapEnabled(true);
        l.setTextSize(R.dimen.context_text_size); // TODO Dynamic Font
        l.setTextColor(CHART_LEGEND_TEXT_COLOR);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        pieChart.getLegend().setEnabled(false); // TODO Temporarily removed legends
        return pieChart;
    }

    private void prepareFilterList(String filter) {
        /* specify the filters used for this visualization */
        if(filter == null) {
            filterList.add("N/A");
        } else {
            filterList.remove("N/A");
            //filterList.clear();
            filterList.add(filter);
        }
        filterAdapter.notifyDataSetChanged();
    }

    private void addDatasetToList(String schoolName, String date) {
        /* specify the school and date from which the visualization data comes from */
        String dataset = schoolName+"("+date+")";
        if(!datasetList.contains(dataset)) {
            datasetList.add(dataset);
        }

        Log.v(TAG, "number of datasets: " + datasetList.size());
        datasetAdapter.notifyDataSetChanged();
        /* get records of patients taken in specified school and date from database */
        prepareRecord();
        refreshCharts();
    }

    private void prepareRecord(/*String schoolName, String date*/){
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get patient records from database */
        //Log.d(TAG, "number of dataset = "+datasetList.size());
        recordsLeft.clear();
        for(int i = 0; i < datasetList.size(); i++) {
            String dataset = datasetList.get(i);
            String school = dataset.substring(0, dataset.indexOf("("));
            String date = dataset.substring(dataset.indexOf("(")+1,dataset.indexOf(")"));
            //Log.d(TAG, "dataset to be added: "+dataset);
            int schoolId = getSchoolId(school, date);
            //Log.d(TAG, "schoolId = "+schoolId);
            if(schoolId != -1) {
                recordsLeft.addAll(getBetterDb.getRecordsFromSchool(schoolId, date));
                Log.d(TAG, "added dataset: "+dataset);
            }
        }

        if(recordsRight == null) {
            prepareRightChartRecords();
        }
        /* close database after query */
        getBetterDb.closeDatabase();
    }

    private void prepareRightChartRecords() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get patient records from database */
        String year = date.substring(date.length()-4);
        if(rightChartContent.contains("National")) {
            recordsRight = getBetterDb.getAllRecordsOnYear(year);
        } else if(rightChartContent.contains("Region")){
            recordsRight = getBetterDb.getRecordsFromRegionOnYear(schoolID, year);
        } else if(rightChartContent.contains("Province")) {
            recordsRight = getBetterDb.getRecordsFromProvinceOnYear(schoolID, year);
        } else { // same municipality
            recordsRight = getBetterDb.getRecordsFromMunicipalityOnYear(schoolID, year);
        }
        Log.v(TAG, "right records size: "+recordsRight.size());
        /* close database after query */
        getBetterDb.closeDatabase();
    }

    private int getHPIs() {
        int hpiSize = 0;
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get hpi list from database */
        if(schoolID != 0) {
            hpiSize = getBetterDb.getHPIsFromSchool(schoolID).size();
        }

        if(hpiSize == 0){
            Toast.makeText(this, "No HPI record available!", Toast.LENGTH_SHORT).show();
        }
        /* close database after insert */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of HPIs = " + hpiSize);
        return hpiSize;
    }

    private int getSchoolId(String schoolName, String date) {
        //Log.d(TAG, "schoolName: "+schoolName);
        //Log.d(TAG, "date: "+date);
        for(int i = 0; i < datasets.size(); i++) {
            if(datasets.get(i).getSchoolName().contentEquals(schoolName)
                    && datasets.get(i).getDateCreated().contentEquals(date)) {
                return datasets.get(i).getSchoolID();
            }
        }
        return -1;
    }

    private void getGradeLevels() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        gradeLevels = new ArrayList<>();
        gradeLevels.add("N/A");
        /* get grade level list from database */
        gradeLevels.addAll(getBetterDb.getGradeLevelsFromSchool(schoolID));
        /* close database after query */
        getBetterDb.closeDatabase();
    }

    private void getDatasetList() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get datasetList from database */
        datasets = getBetterDb.getAllDatasets();
        /* close database after query */
        getBetterDb.closeDatabase();
    }

    private ArrayList<Integer> getColorPalette() {
        ArrayList<Integer> colors = new ArrayList<>();

        for(int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        return colors;
    }

    private void initializeStackedGraphOverview() {
//        recordColumn = spRecordColumn.getItemAtPosition(0).toString();
        recordColumns = getResources().getStringArray(R.array.record_column_array_num); // Initialize columns to appear

        // TODO add additional overviews here + corresponding params
        stackedBarCharts = new ArrayList<>();
        graphStackedBarCharts = new ArrayList<>();

        // TODO Add stack
        stackedBarCharts.add(stackedBMI);
        graphStackedBarCharts.add(graphBMI);

        stackedBarCharts.add(stackedVisualAcuityLeft);
        graphStackedBarCharts.add(graphVisualAcuityLeft);

        stackedBarCharts.add(stackedVisualAcuityRight);
        graphStackedBarCharts.add(graphVisualAcuityRight);

        stackedBarCharts.add(stackedColorBlindness);
        graphStackedBarCharts.add(graphColorBlindness);


//        stackedBarCharts = createOverviewCharts(stackedBarCharts); // TODO Initialize

        // TODO Then add as View
//        graphBMI.addView(stackedBarCharts.get(0));
//        graphVisualAcuityLeft.addView(stackedBarCharts.get(1));
//        graphVisualAcuityRight.addView(stackedBarCharts.get(2));
//        graphColorBlindness.addView(stackedBarCharts.get(3));

//        computeOverviewParams(stackedBarCharts, paramsOverview);
//        refreshCharts();
    }

    private void computeOverviewParams(ArrayList<HorizontalBarChart> charts, ArrayList<ViewGroup.LayoutParams> params) {
        params = new ArrayList<>();

        for(int i = 0; i < charts.size(); i++) {

            params.add(charts.get(i).getLayoutParams());

            offsetTopBottom = computePercent(graphLayoutCenter.getHeight(), offsetPercent);
            offsetLeftRight = computePercent(graphLayoutCenter.getWidth(), offsetPercent);

            params.get(i).height = graphStackedBarCharts.get(i).getHeight()-offsetTopBottom+10; // ViewGroup.LayoutParams.MATCH_PARENT; TODO ADD CHART HEIGHT
            params.get(i).width = graphStackedBarCharts.get(i).getWidth()-offsetLeftRight; //ViewGroup.LayoutParams.MATCH_PARENT;


            charts.get(i).setX(computePercentHalf(charts.get(i).getWidth(), offsetPercent));
            charts.get(i).setY(computePercentHalf(charts.get(i).getHeight(), offsetPercent)/offsetYDivider);
        }
    }



    private void addDataSet() {
        if(chartType.contentEquals("Scatter Chart")) { // TODO change to "Overview"
            prepareOverviewChartData();
        }
        else {
            // TODO edit graph
            if(chartType.contentEquals("Pie Chart")) {
                preparePieChartData(pieChartLeft, yDataLeft);
                preparePieChartData(pieChartRight, yDataRight);
            } else if(chartType.contentEquals("Bar Chart")){
                prepareBarChartData();
            } else if(chartType.contentEquals("Scatter Chart")) { // TODO TESTING ONLY, PUT BACK TO prepareScatterChartData
//            prepareScatterChartData();
//                prepareStackedBarChartData(); // TODO Remove stacked  bar chart
                prepareOverviewChartData();
            } else {
                prepareBubbleChartData();
            }
        }
    }

    private ArrayList<PieEntry> createPieEntries(int[] yData) {
        ArrayList<PieEntry> yVals1 = new ArrayList<>();

        for(int i = 0; i < yData.length; i++) {
            yVals1.add(new PieEntry(yData[i], i));
        }

        return yVals1;
    }

    private ArrayList<Entry> createEntries(int[] yData) {
        ArrayList<Entry> yVals1 = new ArrayList<>();

        for(int i = 0; i < yData.length; i++) {
            yVals1.add(new Entry(i, yData[i]));
//            yVals1.add(new Entry(yData[i], i)); TODO edited
        }

        return yVals1;
    }

    private void prepareBubbleChartData() {
        ArrayList<BubbleEntry> yVals1 = new ArrayList<>();
        ArrayList<BubbleEntry> yVals2 = new ArrayList<>();
        ArrayList<Integer> colors = getColorPalette();
        List<IBubbleDataSet> bubbleDataSetList = new ArrayList<>();
        BubbleData bubbleData;
//        String year = date.substring(date.length() - 4);
        if(!recordColumn.contains("BMI")) {
            for (int i = 0; i < yDataLeft.length; i++) {
            /* BubbleEntry(xpos, ypos, size)  */
                yVals1.add(new BubbleEntry(i, 1, yDataLeft[i]));
            }
            for (int i = 0; i < yDataRight.length; i++) {
            /* BubbleEntry(xpos, ypos, size)  */
                yVals2.add(new BubbleEntry(i, 0, yDataRight[i]));
            }

            BubbleDataSet bubbleDataSet = new BubbleDataSet(yVals1, "Chart Left");
            BubbleDataSet bubbleDataSet2 = new BubbleDataSet(yVals2, rightChartContent);
            bubbleDataSet.setColor(colors.get(0));
            bubbleDataSet2.setColor(colors.get(1));
            bubbleDataSet.setDrawValues(true);
            bubbleDataSet2.setDrawValues(true);

            bubbleDataSetList.add(bubbleDataSet2);
            bubbleDataSetList.add(bubbleDataSet);

            // TODO deprecated
//            bubbleData = new BubbleData(xData, bubbleDataSetList);
            bubbleData = new BubbleData(bubbleDataSetList);

            bubbleChart.getLegend().resetCustom();
        } else {
            ValueCounter valueCounter = new ValueCounter(recordsLeft);
            ArrayList<ValueCounter.BMICounter> bmiCounters = valueCounter.getBMISpecial();
            int category, age;
            ValueCounter.BMICounter counter;
            BubbleDataSet bubbleDataSet;
            for(int i = 0; i < bmiCounters.size(); i++) {
                counter = bmiCounters.get(i);
                yVals1 = new ArrayList<>();
                for(int j = 0; j < possibleAge.length; j++) {
                    age = Integer.valueOf(possibleAge[j]);
                    if(age == counter.getAge()) {
                        category = ValueCounter.getBMICategoryIndex(counter.getCategory());
                        Log.v(TAG, "Bubble Entry: "+age+"\t"+bmiCounters.get(i).getBMI()+"\t"+bmiCounters.get(i).getCount()+"\t"+category);
                        yVals1.add(new BubbleEntry(j, bmiCounters.get(i).getBMI(), bmiCounters.get(i).getCount()));
                        bubbleDataSet = new BubbleDataSet(yVals1, xData[category]);
                        bubbleDataSet.setColor(colors.get(category));
                        bubbleDataSetList.add(bubbleDataSet);
                    }
                }
            }
            Log.v(TAG, "Bubble List Size: "+bubbleDataSetList.size());

            // TODO deprecated
//            bubbleData = new BubbleData(possibleAge, bubbleDataSetList);
            bubbleData = new BubbleData(bubbleDataSetList);


            // customize legend
            Legend legend = bubbleChart.getLegend();
            int color[] = new int[xData.length];
            for(int k = 0; k < xData.length; k++) {
                color[k] = colors.get(k);
            }


//            legend.setCustom(color, xData); TODO deprecated
            LegendEntry entry;
            ArrayList<LegendEntry> entries = new ArrayList<LegendEntry>();
            for(int i = 0; i < color.length; i++) {
                entry = new LegendEntry();
                entry.formColor = color[i];
                entry.label = xData[i];
                entries.add(entry);
            }
        }

        bubbleChart.setData(bubbleData);
        bubbleChart.getAxisLeft().setEnabled(false);
        customizeChart(bubbleChart, bubbleChart.getAxisRight());
    }

    private void prepareScatterChartData() {
        ArrayList<Entry> yVals1, yVals2;
        ArrayList<Integer> colors = getColorPalette();

        List<IScatterDataSet> scatterDataSetList = new ArrayList<>();
        ScatterData scatterData;
        if(!recordColumn.contains("BMI")) {
            yVals1 = createEntries(yDataLeft);
            yVals2 = createEntries(yDataRight);

            ScatterDataSet scatterDataSet = new ScatterDataSet(yVals1, "Chart Left");
            ScatterDataSet scatterDataSet2 = new ScatterDataSet(yVals2, rightChartContent);
            /* set the shape of drawn scatter point. */
            scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
            scatterDataSet2.setScatterShape(ScatterChart.ScatterShape.TRIANGLE);
            scatterDataSet.setColor(colors.get(0));
            scatterDataSet2.setColor(colors.get(1));

            scatterDataSetList.add(scatterDataSet);
            scatterDataSetList.add(scatterDataSet2);

//            scatterData = new ScatterData(xData, scatterDataSetList); // TODO deprecated
            scatterData = new ScatterData(scatterDataSetList);


            scatterChart.getLegend().resetCustom();
        } else {
            PatientRecord patientRecord; int age, category;
            ScatterDataSet scatterDataSet;
            for(int i = 0; i < recordsLeft.size(); i++) {
                yVals1 = new ArrayList<>();
                patientRecord = recordsLeft.get(i);
                for(int j = 0; j < possibleAge.length; j++) {
                    age = Integer.valueOf(possibleAge[j]);
                    if(patientRecord.getAge() == age) {
                        category = ValueCounter.getBMICategoryIndex(patientRecord.getBMIResultString());
//                        yVals1.add(new Entry(patientRecord.getBMIResult(), j)); TODO edited
                        yVals1.add(new Entry(j, patientRecord.getBMIResult()));
                        scatterDataSet = new ScatterDataSet(yVals1, xData[category]);
                        scatterDataSet.setColor(colors.get(category));
                        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
                        scatterDataSetList.add(scatterDataSet);
                    }
                }
            }
            Log.v(TAG, "Scatter List Size: "+scatterDataSetList.size());

            scatterData = new ScatterData(scatterDataSetList);
//            scatterData = new ScatterData(possibleAge, scatterDataSetList); TODO deprecated

            Legend legend = scatterChart.getLegend();
            int color[] = new int[xData.length];
            for(int k = 0; k < xData.length; k++) {
                color[k] = colors.get(k);
            }
//            legend.setCustom(color, xData); TODO deprecated
            LegendEntry entry;
            ArrayList<LegendEntry> entries = new ArrayList<LegendEntry>();
            for(int i = 0; i < color.length; i++) {
                entry = new LegendEntry();
                entry.formColor = color[i];
                entry.label = xData[i];
                entries.add(entry);
            }

        }

        scatterChart.setData(scatterData);

        scatterChart.getAxisLeft().setEnabled(false);
        customizeChart(scatterChart, scatterChart.getAxisRight());

    }

    private void prepareBarChartData() {

        int index = 0;
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for(int i = 0; i < yDataLeft.length; i++) {
            yVals1.add(new BarEntry(index, yDataLeft[i]));
            index += 2;
        }

        index = 1;
        ArrayList<BarEntry> yVals2 = new ArrayList<>();
        for(int i = 0; i < yDataRight.length; i++) {
            yVals2.add(new BarEntry(index, yDataRight[i]));
            index += 2;
        }

        /* create bar chart dataset */
        BarDataSet barDataSet = new BarDataSet(yVals1, "School");
        BarDataSet barDataSet2 = new BarDataSet(yVals2, rightChartContent);
        ArrayList<Integer> colors = getColorPalette();
        barDataSet.setColor(colors.get(0));
        barDataSet2.setColor(colors.get(1));

        /*BarDataSet barDataSet1 = new BarDataSet(yVals1, "");
        barDataSet.setColor(colors.get(0)); */
        List<IBarDataSet> barDataSetList = new ArrayList<>();
        barDataSetList.add(barDataSet);
        barDataSetList.add(barDataSet2);

//        BarData barData = new BarData(xData, barDataSetList); TODO deprecated
        BarData barData = new BarData(barDataSetList);


        //BarData barData = new BarData(xData, barDataSet);
        barChart.setData(barData);
        barChart.getAxisLeft().setEnabled(false);
        customizeChart(barChart, barChart.getAxisRight());

    }
    private void prepareOverviewChartData() {
        for(int i = 0; i < 4; i++) { // TODO change 4 to recordColumns.length


            stackedBarCharts.set(i, prepareStackedOverview(recordColumns[i], stackedBarCharts.get(i)));
            graphStackedBarCharts.get(i).addView(stackedBarCharts.get(i));

            graphStackedBarCharts.get(i).setX(computePercentHalf(graphStackedBarCharts.get(i).getWidth(), offsetPercent));
            graphStackedBarCharts.get(i).setY(computePercentHalf(graphStackedBarCharts.get(i).getHeight(), offsetPercent)/offsetYDivider);



        }
        computeOverviewParams(stackedBarCharts, paramsOverview);
    }

    private HorizontalBarChart prepareStackedOverview(String recordType, HorizontalBarChart chart) {
        ChartDataValue chartDataValue = prepareChartData(recordType);

        chart = new HorizontalBarChart(this);
        chart.setOnChartValueSelectedListener(getOverviewOnChartValueSelectedListener());

        // Variables to hold the converted yData (from int to float) and sum
        float[] fDataSchool, fDataNational, pDataSchool, pDataNational;
        float fSumSchool, fSumNational;

        // Convert yData to float
        fDataSchool = General.convertToFloat(chartDataValue.getyDataLeft());
        fDataNational = General.convertToFloat(chartDataValue.getyDataRight());

        // Get yData sum
        fSumSchool = General.getArraySum(fDataSchool);
        fSumNational = General.getArraySum(fDataNational);

        // Convert to percentages
        pDataSchool = General.computePercentEquivalent(fDataSchool, fSumSchool);
        pDataNational = General.computePercentEquivalent(fDataNational, fSumNational);


        // Stacked bar entries. xIndex 0 is the bottom
        List<BarEntry> entries = new ArrayList<>();
        BarEntry schoolData = new BarEntry(0f, pDataSchool);
        entries.add(schoolData); // School

        // BarDataSet second parameter is the label
        BarDataSet set = new BarDataSet(entries, recordType);
        List<IBarDataSet> barDataSetList = new ArrayList<>();

        barDataSetList.add(set);
        BarData data = new BarData(barDataSetList); // TODO X Values
        chart.setData(data);

        formatStackBarAppearance(recordType, chart, set, fDataSchool, pDataSchool); // Edit stack bar appearance
        chart.notifyDataSetChanged();
        return chart;
    }


    private void prepareStackedBarChartData() {
        this.prepareStackedBarChartDataPercentages();
//        this.prepareStackedBarChartDataRaw();
    }

    // Alters the values to be in percent (0 to 100)
    private void prepareStackedBarChartDataPercentages() {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        // Variables to hold the converted yData (from int to float) and sum
        float[] fDataSchool, fDataNational, pDataSchool, pDataNational;
        float fSumSchool, fSumNational;

        // Convert yData to float
        fDataSchool = General.convertToFloat(yDataLeft);
        fDataNational = General.convertToFloat(yDataRight);

        // Get yData sum
        fSumSchool = General.getArraySum(fDataSchool);
        fSumNational = General.getArraySum(fDataNational);

        // Convert to percentages
        pDataSchool = General.computePercentEquivalent(fDataSchool, fSumSchool);
        pDataNational = General.computePercentEquivalent(fDataNational, fSumNational);


        // TODO Remove, PRINTING for validation only
//        for(int i = 0; i < yDataLeft.length; i++) {
//            Log.e("YVAL_School", fDataSchool[i]+"");
//            Log.e("YVAL_National", fDataNational[i]+"");
//        }

        // Stacked bar entries. xIndex 0 is the bottom
        List<BarEntry> entries = new ArrayList<>();
        BarEntry schoolData = new BarEntry(0f, pDataSchool);
        entries.add(schoolData); // School

        // BarDataSet second parameter is the label
        BarDataSet set = new BarDataSet(entries, recordColumn);
        List<IBarDataSet> barDataSetList = new ArrayList<>();

        barDataSetList.add(set);
        BarData data = new BarData(barDataSetList); // TODO X Values
        stackedBarChart.setData(data);

        formatStackBarAppearance(recordColumn, stackedBarChart, set, fDataSchool, pDataSchool); // Edit stack bar appearance
        stackedBarChart.notifyDataSetChanged();
    }

    private void formatStackBarAppearance(String recordName, HorizontalBarChart chart, BarDataSet barData, float[] rawData, float[] percentData) {
        barData.setValueFormatter(new StackedBarChartValueFormatter()); // Format values to ###.##% as specified i the passed parameter class

        // Set stack colors here
        barData.setColors(General.getColorSetLightGray(percentData.length)); // TODO Dynamic colors

        barData.setStackLabels(xData);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();

        chart.getLegend().setEnabled(false); // Show/hide stack label legend
//        stackedBarChart.getLegend().setMaxSizePercent(5);
        formatStackedBarAxis(chart);

        LimitLine line;
        float sumX = 0;
        int highestValueIndex = 0;

        // percentData.length-1 so that the last value won't have a limit line
        for(int i = 0; i < percentData.length; i++) {
            sumX += percentData[i];
            if(sumX != 0 && (i != percentData.length-1)) { // Don't draw limit line on 0 value and don't draw the last value's line
                line = new LimitLine(sumX, "");
                line.setLineWidth(1f);
                line.setLineColor(Color.WHITE);
                leftAxis.addLimitLine(line);
            }

            if(percentData[highestValueIndex] < percentData[i]) { // Find highest value to be highlighted later
                highestValueIndex = i;
            }
        }


        // TODO length check for color
        barData.getColors().set(highestValueIndex,ColorThemes.getStackedColorSet(recordName)[highestValueIndex]);
    }

    private void formatStackedBarAxis(HorizontalBarChart chart) {
        chart.getDescription().setEnabled(false);

        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setDrawAxisLine(false);

        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);

        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);


        // X Axis
//        String[] values = new String[] {"School"};
//        XAxis xAxis = chart.getXAxis();
//        xAxis.setValueFormatter(new StackedBarChartIAxisFormatter(values));
//        xAxis.setLabelCount(1, true);

        // Y Axis
        YAxis leftAxis = chart.getAxisLeft();

        LimitLine llStart, llEnd;
        llStart = new LimitLine(0f, ""); // TODO label
        llStart.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        llStart.setLineColor(Color.GRAY);
        llStart.setLineWidth(1f);
        llStart.setTextColor(Color.BLACK);
        llStart.setTextSize(VALUE_TEXT_SIZE);
        leftAxis.addLimitLine(llStart);

        llEnd = new LimitLine(100f, ""); // TODO Label
        llEnd.setLineColor(llStart.getLineColor());
        llEnd.setLineWidth(1f);
        llEnd.setTextColor(llStart.getTextColor());
        llEnd.setTextSize(VALUE_TEXT_SIZE);
        leftAxis.addLimitLine(llEnd);
    }


    // TODO backup code, delete when done
//    private void prepareStackedBarChartData() {
//        this.prepareStackedBarChartDataPercentages();
////        this.prepareStackedBarChartDataRaw();
//    }
//
//
//
//    // Alters the values to be in percent (0 to 100)
//    private void prepareStackedBarChartDataPercentages() {
//        ArrayList<BarEntry> yVals1 = new ArrayList<>();
//
//        // Variables to hold the converted yData (from int to float) and sum
//        float[] fDataSchool, fDataNational, pDataSchool, pDataNational;
//        float fSumSchool, fSumNational;
//
//
//
//        // Convert yData to float
//        fDataSchool = General.convertToFloat(yDataLeft);
//        fDataNational = General.convertToFloat(yDataRight);
//
//        // Get yData sum
//        fSumSchool = General.getArraySum(fDataSchool);
//        fSumNational = General.getArraySum(fDataNational);
//
//        // Convert to percentages
//        fDataSchool = General.computePercentEquivalent(fDataSchool, fSumSchool);
//        fDataNational = General.computePercentEquivalent(fDataNational, fSumNational);
//
//
//        // TODO Remove, PRINTING for validation only
//        for(int i = 0; i < yDataLeft.length; i++) {
//            Log.e("YVAL_School", fDataSchool[i]+"");
//            Log.e("YVAL_National", fDataNational[i]+"");
//        }
//
//        // Stacked bar entries. xIndex 0 is the bottom
//        List<BarEntry> entries = new ArrayList<>();
////        entries.add(new BarEntry(fDataNational, 0)); // National
////        entries.add(new BarEntry(fDataSchool, 1)); // School
//        entries.add(new BarEntry(0f, fDataNational)); // National
//        entries.add(new BarEntry(1f, fDataSchool)); // School
//
//
//
//        // BarDataSet second parameter is the label
//        BarDataSet set = new BarDataSet(entries, recordColumn);
//        List<IBarDataSet> barDataSetList = new ArrayList<>();
////        set.setValueFormatter(); TODO VALUE FORMATTER
//        // Set stack colors here
//        set.setColors(new int[] {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.MAGENTA}); // TODO Dynamic colors
//        set.setStackLabels(xData);
//        barDataSetList.add(set);
//
////        BarData data = new BarData(new String[]{"National", "School"}, barDataSetList); // TODO X Values
//        BarData data = new BarData(barDataSetList); // TODO X Values
//
//
//        formatStackedBarAxis();
//        stackedBarChart.setData(data);
//    }

//    private void formatStackedBarAxis() {
//        stackedBarChart.getAxisLeft().setDrawLabels(false);
//        stackedBarChart.getAxisLeft().setDrawGridLines(false);
//        stackedBarChart.getAxisLeft().setDrawAxisLine(false);
//
//        stackedBarChart.getAxisRight().setDrawLabels(false);
//        stackedBarChart.getAxisRight().setDrawGridLines(false);
//        stackedBarChart.getAxisRight().setDrawAxisLine(false);
//
//        stackedBarChart.getXAxis().setDrawLabels(false);
//        stackedBarChart.getXAxis().setDrawGridLines(false);
//        stackedBarChart.getXAxis().setDrawAxisLine(false);
//
//        // X Axis
//        String[] values = new String[] {"National", "School"};
//        XAxis xAxis = stackedBarChart.getXAxis();
//        xAxis.setValueFormatter(new StackedBarChartIAxisFormatter(values));
//        xAxis.setLabelCount(2);
//
//        // Y Axis
//        YAxis leftAxis = stackedBarChart.getAxisLeft();
//
//        LimitLine llStart, llEnd;
//        llStart = new LimitLine(0f, "0");
//        llStart.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
//        llStart.setLineColor(Color.LTGRAY);
//        llStart.setLineWidth(2f);
//        llStart.setTextColor(Color.BLACK);
//        llStart.setTextSize(VALUE_TEXT_SIZE);
//        leftAxis.addLimitLine(llStart);
//
//        llEnd = new LimitLine(100f, "100");
//        llEnd.setLineColor(Color.LTGRAY);
//        llEnd.setLineWidth(2f);
//        llEnd.setTextColor(Color.BLACK);
//        llEnd.setTextSize(VALUE_TEXT_SIZE);
//        leftAxis.addLimitLine(llEnd);
//    }
//    private void prepareStackedBarChartDataRaw() { // TODO delete
//        ArrayList<BarEntry> yVals1 = new ArrayList<>();
//
//        float[] fDataLeft = new float[yDataLeft.length];
//        float[] fDataRight = new float[yDataRight.length];
//        for(int i = 0; i < yDataLeft.length; i++) {
//            fDataLeft[i] = (float)yDataLeft[i];
//            Log.e("YVAL_l", fDataLeft[i]+"");
//        }
//        for(int i = 0; i < yDataRight.length; i++) {
//            fDataRight[i] = (float)yDataRight[i];
//            Log.e("YVAL_r", fDataRight[i]+"");
//        }
//
//
//        for(int i = 0; i < yDataLeft.length; i++) {
//            yVals1.add(new BarEntry(i, fDataLeft));
//        }
//        ArrayList<BarEntry> yVals2 = new ArrayList<>();
//        for(int i = 0; i < yDataRight.length; i++) {
//            yVals2.add(new BarEntry(i, fDataRight));
//        }
//
//        List<BarEntry> entries = new ArrayList<BarEntry>();
//        entries.add(new BarEntry(0, fDataLeft));
//        entries.add(new BarEntry(1, fDataRight));
//
//        // TODO deprecated
////        for(int i = 0; i < yDataLeft.length; i++) {
////            yVals1.add(new BarEntry(fDataLeft, i));
////        }
////        ArrayList<BarEntry> yVals2 = new ArrayList<>();
////        for(int i = 0; i < yDataRight.length; i++) {
////            yVals2.add(new BarEntry(fDataRight, i));
////        }
////
////        List<BarEntry> entries = new ArrayList<BarEntry>();
////        entries.add(new BarEntry(fDataLeft, 0));
////        entries.add(new BarEntry(fDataRight, 1));
//
//        BarDataSet set = new BarDataSet(entries, recordColumn);
//        List<IBarDataSet> barDataSetList = new ArrayList<>();
//        set.setColors(new int[] {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.MAGENTA});
//        set.setStackLabels(xData);
//        barDataSetList.add(set);
//
//        // TODO deprecated
////        BarData data = new BarData(new String[]{"local", "national"}, barDataSetList);
//        BarData data = new BarData(barDataSetList);
////        data.getDataSetLabels()[0] = "School";
////        data.getDataSetLabels()[1] = "National"; TODO commented
//
//
//
//        stackedBarChart.setData(data);
//
//    }
    private void preparePieChartData(PieChart pieChart, int[] yData) {
//        ArrayList<Entry> yVals1 = createEntries(yData); TODO deprecated
        List<PieEntry> yVals1 = createPieEntries(yData);

        // create pie data set
//        PieDataSet dataSet = new PieDataSet(yVals1, ""); TODO deprecated
        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);
        /* add colors to chart */
        dataSet.setColors(getColorPalette());

        // instantiate pie data object now
//        PieData data = new PieData(xData, dataSet); // TODO deprecated
        PieData data = new PieData(dataSet);

        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(VALUE_TEXT_SIZE); // TODO Pie chart label size

        data.setValueTextColor(Color.GRAY);

        pieChart.setData(data);

//        pieChart.setDescription(recordColumn); TODO deprecated
//        pieChart.setDescriptionTextSize(DESCRIPTION_TEXT_SIZE);
//        pieChart.setDescriptionColor(CHART_DESC_TEXT_COLOR);

        Description description = new Description();
        description.setText(recordColumn);
        description.setTextSize(DESCRIPTION_TEXT_SIZE);
        description.setTextColor(CHART_DESC_TEXT_COLOR);
        pieChart.setDescription(description);


        // undo all highlights
        pieChart.highlightValues(null);

        // update pie chart
        pieChart.invalidate();
    }

    private void customizeChart(Chart chart, YAxis yAxis) {
//        Description description = new Description();
//        description.setText(recordColumn);
//        description.setTextSize(20f);
//        description.setTextColor(Color.WHITE);
//        chart.setDescription(description);



        Description description = new Description();
        description.setText(recordColumn);
        description.setTextSize(DESCRIPTION_TEXT_SIZE);
        description.setTextColor(CHART_DESC_TEXT_COLOR);
        chart.setDescription(description);

//        chart.setDescription(recordColumn); TODO deprecated
//        chart.setDescriptionTextSize(DESCRIPTION_TEXT_SIZE);
//        chart.setDescriptionColor(CHART_DESC_TEXT_COLOR);


        chart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());

        ChartData chartData = chart.getData();
        List<IDataSet> iDataSetList = chartData.getDataSets();
        /* customize value lable for each dataset */
        for(int i = 0; i < iDataSetList.size(); i++) {
            iDataSetList.get(i).setValueTextSize(VALUE_TEXT_SIZE); // TODO Value text size
            iDataSetList.get(i).setValueTextColor(CHART_VALUE_TEXT_COLOR);
        }
        /* customize axis labels */
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(AXIS_TEXT_SIZE);
        xAxis.setTextColor(CHART_AXIS_TEXT_COLOR);
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, AxisBase axisBase) {
//                Log.v(TAG, "Float Value: "+v);
//                return xData[(int) v];
//            }
//        });
        yAxis.setTextSize(AXIS_TEXT_SIZE);
        yAxis.setTextColor(CHART_AXIS_TEXT_COLOR);
        /* customize legend */
        Legend legend = chart.getLegend();
        legend.setTextSize(LEGEND_TEXT_SIZE);
        legend.setTextColor(CHART_LEGEND_TEXT_COLOR);
    }

    @Override
    public void onDialogPositiveClick(AddFilterDialogFragment dialog) {
        String ageEquator, ageValue, genderValue, gradeLevelValue;
        ageEquator = dialog.getAgeEquator();
        ageValue = dialog.getAgeValue();
        genderValue = dialog.getGenderValue();
        gradeLevelValue = dialog.getGradeLevelValue();

        //Log.d(AddFilterDialogFragment.TAG, "Filter: age "+ageEquator+" "+ageValue);
        Log.v(TAG, "grade level value = "+gradeLevelValue +"(before filtering)");
        /* filter records*/
        if(!ageValue.contentEquals("")) {
            for(int i = 0; i < filterList.size(); i++) {
                if(filterList.get(i).contains("age")){
                    removeFilter(filterList.get(i));
                }
            }
            filterRecordsByAge(ageEquator, ageValue);
            prepareFilterList("age "+ageEquator+" "+ageValue);
        }
        if(!genderValue.contentEquals("N/A")) {
            for(int i = 0; i < filterList.size(); i++) {
                if(filterList.get(i).contains("gender")){
                    removeFilter(filterList.get(i));
                }
            }
            filterRecordsByGender(genderValue);
            prepareFilterList("gender = "+genderValue);
        }
        if(!gradeLevelValue.contentEquals("N/A")) {
            for(int i = 0; i < filterList.size(); i++) {
                if(filterList.get(i).contains("grade level")){
                    removeFilter(filterList.get(i));
                }
            }
            filterRecordsByGradeLevel(gradeLevelValue);
            prepareFilterList("grade level = "+gradeLevelValue);
        }
        filterAdapter.notifyDataSetChanged();
        refreshCharts();
    }

    private void filterRecordsByGender(String genderValue) {
        Log.d(TAG, "Gender Filter: "+genderValue);
        for(int i = 0; i < recordsLeft.size(); i ++) {
            if(genderValue.contentEquals("Female") && !recordsLeft.get(i).getGender()) {
                recordsLeft.remove(i);
                i--;

            } else if(genderValue.contentEquals("Male") && recordsLeft.get(i).getGender()){
                recordsLeft.remove(i);
                i--;
            }
        }
//        for(int i = 0; i < recordsLeft.size(); i++) {
//            Log.d(TAG, "Filtered Gender = "+recordsLeft.get(i).getGender());
//        }
    }

    private void filterRecordsByAge(String filterEquator, String filterValue) {
        // sort list according to age, ascending order
        Collections.sort(recordsLeft, new Comparator<PatientRecord>() {
            @Override
            public int compare(PatientRecord o1, PatientRecord o2) {
                if(o1.getAge() > o2.getAge()) {
                    return 1;
                } else if(o1.getAge() < o2.getAge()) {
                    return -1;
                }
                return 0;
            }
        });
        int value = Integer.valueOf(filterValue);
        int index = getIndexByProperty(value);
        Log.d(TAG, "Index: "+ index);
        ArrayList<PatientRecord> tempArray = new ArrayList<>();
        if(index != -1) {
            if(filterEquator.contains("=")) {
                int endIndex = getIndexByProperty(value+1);
                if(endIndex == -1) {
                    tempArray.addAll(recordsLeft.subList(index, recordsLeft.size()-1));
                } else {
                    tempArray.addAll(recordsLeft.subList(index, endIndex));
                }
            }
            if(filterEquator.contains("<")) {
                tempArray.addAll(recordsLeft.subList(0, index));
            } else if(filterEquator.contains(">")) {
                tempArray.addAll(recordsLeft.subList(index, recordsLeft.size() - 1));
            }
            recordsLeft.clear();
            recordsLeft.addAll(tempArray);
//            for(int i = 0; i < recordsLeft.size(); i++) {
//                Log.d(TAG, "Age: "+ recordsLeft.get(i).getAge());
//            }
        } else {
            Toast.makeText(this, "There is no one with that age!", Toast.LENGTH_SHORT).show();
        }
    }
    /* Get index of the first record with the specified age value*/
    private int getIndexByProperty(int value) {
        for(int i = 0; i < recordsLeft.size(); i++) {
            if(recordsLeft.get(i).getAge() == value) {
                return i;
            }
        }
        return -1;
    }

    private void filterRecordsByGradeLevel(String gradeLevel) {
        Log.d(TAG, "Grade Level Filter: "+gradeLevel);
        for(int i = 0; i < recordsLeft.size(); i++) {
            if(!recordsLeft.get(i).getGradeLevel().contentEquals(gradeLevel)) {
                //Log.d(TAG, "filter record: "+ recordsLeft.get(i).getGradeLevel());
                recordsLeft.remove(i);
                i--;
            }
        }
    }

    @Override
    public void removeFilter(String filter) {
        prepareRecord();
        Log.d(TAG, "Removed Filter: "+filter);
        filterList.remove(filter);

        if(filterList.size() > 0) {
            String filterLeft = filterList.get(0);
            for(int i = 0; i < filterList.size(); i++) {
                filterLeft = filterList.get(i);
                Log.d(TAG, "Filter Left: "+filterLeft);
                if(filterLeft.contains("age")) {
                    filterRecordsByAge(String.valueOf(filterLeft.charAt(4)), filterLeft.substring(6));
                } else if(filterLeft.contains("gender")) {
                    filterRecordsByGender(filterLeft.substring(9));
                } else if(filterLeft.contains("grade level")) {
                    filterRecordsByGradeLevel(String.valueOf(filterLeft.substring(14)));
                }
            }
        }
        Log.d(TAG, "Displayed records: "+ recordsLeft.size());

        refreshCharts();
    }

    @Override
    public void onDialogPositiveClick(AddDatasetDialogFragment dialog) {
        int selectedDatasetIndex = dialog.getSelectedDatasetIndex();
        Dataset dataset = datasets.get(selectedDatasetIndex);
        //dataset.printDataset();
        addDatasetToList(dataset.getSchoolName(), dataset.getDateCreated());
    }

    @Override
    public void removeDataset(String dataset) {
        datasetList.remove(dataset);
        prepareRecord();
        refreshCharts();
    }
}
