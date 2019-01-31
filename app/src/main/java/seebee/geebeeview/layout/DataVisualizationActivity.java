package seebee.geebeeview.layout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import seebee.geebeeview.R;
import seebee.geebeeview.adapter.FilterAdapter;
import seebee.geebeeview.adapter.TextHolderAdapter;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.graphs.ChartDataValue;
import seebee.geebeeview.graphs.OverviewEntry;
import seebee.geebeeview.graphs.StackedBarChartValueFormatter;
import seebee.geebeeview.containers.StringConstants;
import seebee.geebeeview.model.account.Dataset;
import seebee.geebeeview.model.consultation.School;
import seebee.geebeeview.model.monitoring.PatientRecord;
import seebee.geebeeview.model.monitoring.Record;
import seebee.geebeeview.model.monitoring.ValueCounter;
import seebee.geebeeview.containers.ColorThemes;
import seebee.geebeeview.sidebar.DataVisualizationSidebar;
import seebee.geebeeview.containers.General;
import seebee.geebeeview.spinner.CustomSpinnerAdapter;
import seebee.geebeeview.spinner.CustomSpinnerItem;


public class DataVisualizationActivity extends AppCompatActivity
        implements AddFilterDialogFragment.AddFilterDialogListener,
        AddDatasetDialogFragment.AddDatasetDialogListener, FilterAdapter.FilterAdapterListener, TextHolderAdapter.TextListener {

    private final int INDEX_OVERVIEW = 0;
    private final int INDEX_NATIONAL = 1;
    private final int INDEX_MUNICIPAL = 2;
//    private final int INDEX_PIE = 2;
    private final int INDEX_SCATTER = 3;

    private static final String FILTER_EQUALS = "=";
    private static final String FILTER_LESS_THAN = "<";
    private static final String FILTER_GREATER_THAN = ">";
    private static final String FILTER_GREATER_THAN_EQUAL_TO = ">=";
    private static final String FILTER_LESS_THAN_EQUAL_TO = "<=";

    private static float WEIGHT_FILTER_PROMPT = 0.06125f;
    private static float WEIGHT_GRAPH_OVERVIEW_FULL = 0.85f;
    private static float WEIGHT_GRAPH_OVERVIEW_SHRINK = WEIGHT_GRAPH_OVERVIEW_FULL-WEIGHT_FILTER_PROMPT;

    private float overviewHeightIncrease = 0f;
    private float highlightPercentThreshold = 60f; // In Overview, highlight percent greater than 60f
    private static final String TAG = "DataVisualActivity";
    public static final float VALUE_TEXT_SIZE = 14f;
    public static final float DESCRIPTION_TEXT_SIZE = 16f;
    public static final float AXIS_TEXT_SIZE = 14f;
    public static final float LEGEND_TEXT_SIZE = 16f;

    public static final int CHART_AXIS_TEXT_COLOR = Color.GRAY;
    public static final int CHART_LEGEND_TEXT_COLOR = Color.GRAY;
    public static final int CHART_DESC_TEXT_COLOR = Color.GRAY;
    public static final int CHART_VALUE_TEXT_COLOR = Color.GRAY;
    private int currentRecordColumn = 0;

    private String ageEquator, ageValue, genderValue, gradeLevelValue; // For filtering

    private String strFilterTemplate;
    private String strRemove;

    private SubtitleMode subtitleMode;
    private boolean isFilterEnabled;

    ArrayList<String> datasetList, filterList;
    TextHolderAdapter datasetAdapter;
    FilterAdapter filterAdapter;
    RecyclerView rvDataset, rvFilter;
    Button btnAddDataset, btnAddFilter, btnViewPatientList, btnViewHPIList;
    // RelativeLayout graphLayoutLeft, graphLayoutRight; /* space where graph will be set on */
    // RelativeLayout graphLayoutCenter; // added

    ImageView ivBMIRef, ivVALRef, ivVARRef, ivCOLORRef, ivHEARLRef, ivHEARRRef, ivGMRef, ivFMDRef, ivFMNRef, ivFMHRef;

    ScrollView scrollGraphOverview;
    ConstraintLayout contGraphOverviewParent, contFilterPrompt;
    ArrayList<ConstraintLayout> llBarSpecificLabels;

    RelativeLayout
            graphBMI,
            graphVisualAcuityLeft, graphVisualAcuityRight, graphColorBlindness,
            graphHearingLeft, graphHearingRight,
            graphGrossMotor,
            graphFineMotorDominant, graphFineMotorNon, graphFineMotorHold;


    HorizontalBarChart stackedBMI,
            stackedVisualAcuityLeft, stackedVisualAcuityRight, stackedColorBlindness,
            stackedHearingLeft, stackedHearingRight,
            stackedGrossMotor,
            stackedFineMotorDominant, stackedFineMotorNon, stackedFineMotorHold;

    TextView tvRightScrollTitle, tvRightSubtitle, tvFilterPrompt;
    ImageView ivRightSubtitleImage, ivRightSubtitleColor;
    Drawable imgRightSubtitleHighest, imgRightSubtitleTarget;

    RelativeLayout graphSpecificBarSingle;
    BarChart barSpecific;

    int schoolID;
    String schoolName, date;
    PieChart pieChartLeft, pieChartRight;
    HorizontalBarChart stackedBarChart;
    ArrayList<OverviewEntry> overviewEntries;
    ArrayList<RelativeLayout> graphStackedBarCharts;

    ArrayList<String> recordColumns;
    private String[] xDataContainer; // Only to be used by specific bar chart

    ScatterChart scatterChart;
    BubbleChart bubbleChart;
    ArrayList<PatientRecord> recordsLeft, recordsRight;
    String[] xData, possibleAge;
    int[] yDataLeft, yDataRight;

    ArrayList<Dataset> datasets;
    /* attributes for addFilterDialog */
    ArrayList<String> gradeLevels;
    private TextView tvDataset, tvFilter, tvChart, tvRightChart, tvDataHeader, tvDataHeaderYear, tvSpecificTitle;
    private Spinner spRecordColumn, spChartType, spRightChart;
    private String recordColumn = "BMI", rightChartContent = "National Profile";

    private String chartType = "Overview";
    private String titleScrollRightOverview, titleScrollRightNational, subtitleHighest, subtitleTarget;

    private ViewGroup.LayoutParams paramsLeft, paramsRight, paramsCenter, paramsStacked;
    private ArrayList<ViewGroup.LayoutParams> paramsOverview;

    private int offsetTopBottom;
    private int offsetLeftRight;
    private float offsetPercent = 0.0f;

    private float offsetYDivider = 8f;
    private String provinceName, municipalityName;

    private DataVisualizationSidebar sidebarManager;

    private enum SubtitleMode {
        SHOW_HIGHEST,
        SHOW_TARGET;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        // Place layout width/height retrieval here to avoid returning 0
        ivBMIRef.getLayoutParams().height = ivBMIRef.getWidth();
        ivVALRef.getLayoutParams().height = ivVALRef.getWidth();
        ivVARRef.getLayoutParams().height = ivVARRef.getWidth();
        ivCOLORRef.getLayoutParams().height = ivCOLORRef.getWidth();

        ivHEARLRef.getLayoutParams().height = ivHEARLRef.getWidth();
        ivHEARRRef.getLayoutParams().height = ivHEARRRef.getWidth();
        ivGMRef.getLayoutParams().height = ivGMRef.getWidth();

        ivFMDRef.getLayoutParams().height = ivFMDRef.getWidth();
        ivFMNRef.getLayoutParams().height = ivFMNRef.getWidth();
        ivFMHRef.getLayoutParams().height = ivFMHRef.getWidth();
        adjustGraphOverviewAppearance();

        addDatasetRefresh();
//        if(spChartType != null && spChartType.getSelectedItemPosition() == 0) {
//            showGraphOverview(); // Remember to make scroll view visible (set invisible in onCreate)
//        }
        if (spChartType != null) {
//            ((CustomSpinnerAdapter) spChartType.getAdapter()).adjustFontSize(100); // TODO font size
            spinnerRefresh();
        }

    }
    private void showFilterPrompt() {
        this.showFilterPrompt("");
    }

    private void showFilterPrompt(String strFilter) {
        this.contFilterPrompt.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                0,
                WEIGHT_GRAPH_OVERVIEW_SHRINK
        );
        this.contGraphOverviewParent.setLayoutParams(param);
        tvFilterPrompt.setText(strFilterTemplate+strFilter+strRemove);
    }
    private void hideFilterPrompt() {
        this.contFilterPrompt.setVisibility(View.GONE);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                0,
                WEIGHT_GRAPH_OVERVIEW_FULL
        );
        this.contGraphOverviewParent.setLayoutParams(param);
    }



    private int computePercent(float value, float percent) {
        return Math.round(value*percent);
    }

    private int computePercentHalf(float value, float percent) {
        return Math.round(value*percent/2f);
    }
//    private void refreshChartParams() {
//        if(paramsLeft != null) {
//            offsetTopBottom = computePercent(graphLayoutLeft.getHeight(), offsetPercent);
//            offsetLeftRight = computePercent(graphLayoutLeft.getWidth(), offsetPercent);
//
//            paramsLeft.height = graphLayoutLeft.getHeight()-offsetTopBottom; // ViewGroup.LayoutParams.MATCH_PARENT;
//            paramsLeft.width = graphLayoutLeft.getWidth()-offsetLeftRight; // ViewGroup.LayoutParams.MATCH_PARENT;
//        }
//        if(paramsRight != null) {
//            offsetTopBottom = computePercent(graphLayoutRight.getHeight(), offsetPercent);
//            offsetLeftRight = computePercent(graphLayoutRight.getWidth(), offsetPercent);
//
//            paramsRight.height = graphLayoutRight.getHeight()-offsetTopBottom; // ViewGroup.LayoutParams.MATCH_PARENT;
//            paramsRight.width = graphLayoutRight.getWidth()-offsetLeftRight; // ViewGroup.LayoutParams.MATCH_PARENT;
//        }
//        if(paramsCenter != null) {
//            offsetTopBottom = computePercent(graphLayoutCenter.getHeight(), offsetPercent);
//            offsetLeftRight = computePercent(graphLayoutCenter.getWidth(), offsetPercent);
//
////            paramsCenter.height = Math.round(height+(height/4f)); // ViewGroup.LayoutParams.MATCH_PARENT;
//            paramsCenter.height = graphLayoutCenter.getHeight()-offsetTopBottom; // ViewGroup.LayoutParams.MATCH_PARENT;
//            paramsCenter.width = graphLayoutCenter.getWidth()-offsetLeftRight; //ViewGroup.LayoutParams.MATCH_PARENT;
//        }
//    }

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
        tvRightChart = (TextView) findViewById(R.id.tv_dv_right_chart);
        btnAddDataset = (Button) findViewById(R.id.btn_add_dataset);
        btnAddFilter = (Button) findViewById(R.id.btn_add_filter);
        btnViewPatientList = (Button) findViewById(R.id.btn_view_patient_list);
        btnViewHPIList = (Button) findViewById(R.id.btn_view_hpi_list);
        rvDataset = (RecyclerView) findViewById(R.id.rv_dv_dataset);
        rvFilter = (RecyclerView) findViewById(R.id.rv_dv_filter);
//        graphLayoutLeft = (RelativeLayout) findViewById(R.id.graph_container_left);
//        graphLayoutRight = (RelativeLayout) findViewById(R.id.graph_container_right);
//        graphLayoutCenter = (RelativeLayout) findViewById(R.id.graph_container_center); // Added

        scrollGraphOverview = (ScrollView) findViewById(R.id.scroll_graph_overview);
        contGraphOverviewParent = (ConstraintLayout) findViewById(R.id.cont_graph_overview_spacing);
        contFilterPrompt = (ConstraintLayout) findViewById(R.id.cont_right_items_prompt);
        hideGraphOverview(); // Initially make invisible

        // TODO Add Here
        graphBMI = (RelativeLayout) findViewById(R.id.graph_container_bmi);
        graphVisualAcuityLeft = (RelativeLayout) findViewById(R.id.graph_container_va_left);
        graphVisualAcuityRight = (RelativeLayout) findViewById(R.id.graph_container_va_right);
        graphColorBlindness = (RelativeLayout) findViewById(R.id.graph_container_va_color);

        graphHearingLeft = (RelativeLayout) findViewById(R.id.graph_container_hearing_left);
        graphHearingRight = (RelativeLayout) findViewById(R.id.graph_container_hearing_right);

        graphGrossMotor = (RelativeLayout) findViewById(R.id.graph_container_gross_motor); // TODO new graph
        graphFineMotorDominant = (RelativeLayout) findViewById(R.id.graph_container_fine_dominant);
        graphFineMotorNon = (RelativeLayout) findViewById(R.id.graph_container_fine_non);
        graphFineMotorHold = (RelativeLayout) findViewById(R.id.graph_container_fine_hold);

        graphSpecificBarSingle = (RelativeLayout) findViewById(R.id.graph_specific_bar_single);

        spRecordColumn = (Spinner) findViewById(R.id.sp_record_column);
        spChartType = (Spinner) findViewById(R.id.sp_chart_type);
        spRightChart = (Spinner) findViewById(R.id.sp_right_chart_content);

        tvDataHeader = (TextView) findViewById(R.id.tv_data_header);
        tvDataHeaderYear = (TextView) findViewById(R.id.tv_data_header_year);

        ivBMIRef = (ImageView) findViewById(R.id.iv_bmi_size_ref);

        ivVALRef = (ImageView) findViewById(R.id.iv_va_left_size_ref);
        ivVARRef = (ImageView) findViewById(R.id.iv_va_right_size_ref);
        ivCOLORRef = (ImageView) findViewById(R.id.iv_va_color_size_ref);

        ivHEARLRef = (ImageView) findViewById(R.id.iv_hearing_left_size_ref);
        ivHEARRRef = (ImageView) findViewById(R.id.iv_hearing_right_size_ref);
        ivGMRef = (ImageView) findViewById(R.id.iv_gross_motor_size_ref); // TODO new graphs

        ivFMDRef = (ImageView) findViewById(R.id.iv_fine_dominant_size_ref);
        ivFMNRef = (ImageView) findViewById(R.id.iv_fine_non_size_ref);
        ivFMHRef = (ImageView) findViewById(R.id.iv_fine_hold_size_ref);

        tvSpecificTitle = (TextView) findViewById(R.id.tv_specific_title);

        llBarSpecificLabels = new ArrayList<>();
        llBarSpecificLabels.add((ConstraintLayout) findViewById(R.id.cl_item_1_1));
        llBarSpecificLabels.add((ConstraintLayout) findViewById(R.id.cl_item_1_2));
        llBarSpecificLabels.add((ConstraintLayout) findViewById(R.id.cl_item_1_3));

        llBarSpecificLabels.add((ConstraintLayout) findViewById(R.id.cl_item_2_1));
        llBarSpecificLabels.add((ConstraintLayout) findViewById(R.id.cl_item_2_2));
        llBarSpecificLabels.add((ConstraintLayout) findViewById(R.id.cl_item_2_3));

        llBarSpecificLabels.add((ConstraintLayout) findViewById(R.id.cl_item_3_1));
        llBarSpecificLabels.add((ConstraintLayout) findViewById(R.id.cl_item_3_2));
        llBarSpecificLabels.add((ConstraintLayout) findViewById(R.id.cl_item_3_3));

        llBarSpecificLabels.add((ConstraintLayout) findViewById(R.id.cl_item_4_1));
        llBarSpecificLabels.add((ConstraintLayout) findViewById(R.id.cl_item_4_2));
        llBarSpecificLabels.add((ConstraintLayout) findViewById(R.id.cl_item_4_3));

        tvRightScrollTitle = findViewById(R.id.tv_name_r);
        tvRightSubtitle = findViewById(R.id.tv_subtitle);
        tvRightSubtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSubtitleMode();
            }
        });
        ivRightSubtitleImage = findViewById(R.id.iv_subtitle_image);
        ivRightSubtitleColor = findViewById(R.id.iv_subtitle_color);

        imgRightSubtitleHighest = ContextCompat.getDrawable(this, R.drawable.img_visualize_icon);
        imgRightSubtitleTarget = ContextCompat.getDrawable(this, R.drawable.img_star_icon);

        titleScrollRightOverview = getResources().getString(R.string.overview_title);
        titleScrollRightNational = getResources().getString(R.string.national_title);

        subtitleHighest = getResources().getString(R.string.highlight_highest);
        subtitleTarget = getResources().getString(R.string.highlight_target);
        this.subtitleMode = SubtitleMode.SHOW_HIGHEST;

        strFilterTemplate = getResources().getString(R.string.filter_template)+" ";
        strRemove = getResources().getString(R.string.click_to_remove);
        tvFilterPrompt = findViewById(R.id.tv_subtitle_prompt);
        initializeStackedGraphOverview();
        initializeStackGraphOnClickListener();

        resetFilterParameters();

        // TODO Set default font

        contFilterPrompt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickRemoveFilter();
            }
        });
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
//        recordsRight = new ArrayList<>();
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
//                recordColumn = "BMI";
            }
        });

        /* prepare record so that it can be plotted immediately */
        prepareChartData();
        createCharts();



        String[] charts = getResources().getStringArray(R.array.chart_type_array);

        // Image spinner
        ArrayList<CustomSpinnerItem> list = new ArrayList<>();
        list.add(new CustomSpinnerItem(charts[0],R.drawable.img_circle_trans_greenhighlight)); // TODO change chart icons
        list.add(new CustomSpinnerItem(charts[1],R.drawable.img_circle_trans_greenhighlight));
        list.add(new CustomSpinnerItem(charts[2],R.drawable.img_circle_trans_greenhighlight));
//        list.add(new CustomSpinnerItem(charts[2],R.drawable.img_circle_trans_greenhighlight));
//        list.add(new CustomSpinnerItem(charts[3],R.drawable.img_circle_trans_greenhighlight));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this,
                R.layout.custom_spinner_image, R.id.tv_spinner, list);
        spChartType.setAdapter(adapter);

        // Text spinner
//        ArrayAdapter<String> spChartAdapter = new ArrayAdapter<>(this,
//                R.layout.custom_spinner, getResources().getStringArray(R.array.chart_type_array));


        spChartType.setAdapter(adapter);
        spChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chartType = ((CustomSpinnerItem)(parent.getItemAtPosition(position))).getText();
                clickChartType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /* Default chart is pie chart */
                // ** Edited, default chart is Overview
//                chartType = "Overview";
//                chartType = ((CustomSpinnerItem)(parent.getItemAtPosition(0))).getText(); // On nothing selected, show Overview (first item)
//                showGraphOverview();

//                graphLayoutLeft.addView(pieChartLeft);
//                // adjust size of layout
//                ViewGroup.LayoutParams params = pieChartLeft.getLayoutParams();
//                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        });


        tvDataHeader.setText(schoolName);
        tvDataHeaderYear.setText("Record Date: "+date);
        setupSidebarFunctionality();
        hideFilterPrompt();
    }

    public void clickRemoveFilter() {
        removeAllFilters();
        hideFilterPrompt();
        spinnerRefresh();
    }

    public void clickChartType() {
        // TODO Remove all views
        removeGraphViews();
        hideGraphOverview();
        loadSubtitleMode();
        spinnerSelect();
        addDataSet();
    }
    public void initializeStackGraphOnClickListener() {
        // recordIndex order based on spRecordColumn order
        graphBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSpecificBarChart(0);
            }
        });


        graphVisualAcuityLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSpecificBarChart(1);
            }
        });
        graphVisualAcuityRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSpecificBarChart(2);
            }
        });
        graphColorBlindness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSpecificBarChart(3);
            }
        });



        graphHearingLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSpecificBarChart(4);
            }
        });
        graphHearingRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSpecificBarChart(5);
            }
        });


        graphGrossMotor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSpecificBarChart(6);
            }
        });


        graphFineMotorDominant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSpecificBarChart(7);
            }
        });


        graphFineMotorNon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSpecificBarChart(8);
            }
        });


        graphFineMotorHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSpecificBarChart(9);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void resetFilterParameters() {
        isFilterEnabled = false;
//        ageEquator = "";
//        ageValue = "";
//        genderValue = "N/A";
//        gradeLevelValue = "N/A";
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Called when back button is pressed

//        addDatasetRefresh();
//        refreshCharts();
//        Log.e("RESUME", "resumed");
    }

    private void spinnerRefresh() {
        removeGraphViews();
        hideGraphOverview();

        spinnerSelect();

//        refreshChartParams();
        addDataSet();
    }

    private void spinnerSelect() {
        int position = spChartType.getSelectedItemPosition();
        getSpinnerFunction(position);
    }
    private void spinnerOverview() {
        showGraphOverview();
        if(spRecordColumn != null) {
            spRecordColumn.setVisibility(General.getVisibility(false));
        }
    }

    private void getSpinnerFunction(int position) {
        switch(position) {

            case INDEX_OVERVIEW:
                spinnerOverview();
                break;

            case INDEX_MUNICIPAL:
                spinnerMunicipal();
                break;

            case INDEX_NATIONAL:
                spinnerNational();
                break;
        }
    }
    private void spinnerMunicipal() {
        spinnerOverview();
        prepareRightChartRecords(INDEX_MUNICIPAL);
        refreshCharts();
    }

    private void spinnerNational() {
        spinnerOverview();
        prepareRightChartRecords(INDEX_NATIONAL);
        refreshCharts();
    }

    public void hideGraphOverview() {
        scrollGraphOverview.setVisibility(General.getVisibility(false));
    }

    public void showGraphOverview() {
        scrollGraphOverview.setVisibility(General.getVisibility(true));
    }

    public void adjustGraphOverviewAppearance() {
        if(overviewEntries != null && overviewEntries.get(0).getStackedBarChart() != null) {
            computeOverviewParams(overviewEntries, paramsOverview);
        }
    }

    public void removeGraphViews() {

        // TODO add all graphs
        graphBMI.removeAllViews();
        graphVisualAcuityLeft.removeAllViews();
        graphVisualAcuityRight.removeAllViews();
        graphColorBlindness.removeAllViews();
        graphHearingLeft.removeAllViews();
        graphHearingRight.removeAllViews();
        graphGrossMotor.removeAllViews(); // TODO new graph
        graphFineMotorDominant.removeAllViews();
        graphFineMotorNon.removeAllViews();
        graphFineMotorHold.removeAllViews();

        graphSpecificBarSingle.removeAllViews();
    }

    // TODO EDIT
    public ArrayList<Drawable> getChartIcons(String[] chartTypes) {

//        ArrayList<CustomSpinnerItem> allList = new ArrayList<CustomSpinnerItem>();
//        CustomSpinnerItem item = new CustomSpinnerItem();
        ArrayList<Drawable> iconList = new ArrayList<Drawable>();

        for(int i = 0; i < chartTypes.length; i++) {
            iconList.add(ContextCompat.getDrawable(this, R.drawable.img_circle_trans_greenhighlight));
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
        int position = spChartType.getSelectedItemPosition();

        if(overviewEntries != null && overviewEntries.size() > 0 && overviewEntries.get(0).getStackedBarChart() != null) {
            prepareChartData();
            if(position == INDEX_OVERVIEW) {
                clearOverviewCharts();
            } else if(position == INDEX_MUNICIPAL || position == INDEX_NATIONAL) {
                clearNationalCharts();
            } else if (position == INDEX_SCATTER) {
                scatterChart.clear();
            }
            addDataSet();
        }


//        if(pieChartLeft != null) { // TODO check this condition
//            prepareChartData();
//            if(position == INDEX_OVERVIEW) {
//                clearOverviewCharts();
//            } else if(position == INDEX_PIE) {
//                pieChartLeft.clear();
//            } else if(position == INDEX_NATIONAL) {
//                clearNationalCharts();
//            } else if (position == INDEX_SCATTER) {
//                scatterChart.clear();
//            }
//            addDataSet();
//        }
    }

    // Clear all overview charts
    private void clearOverviewCharts() {
        for(int i = 0; i < overviewEntries.size(); i++) {
            overviewEntries.get(i).getStackedBarChart().clear();
        }
    }

    // Clear all overview charts
    private void clearNationalCharts() { // TODO
        clearOverviewCharts();
    }

    private void createCharts() {
        pieChartLeft = createPieChart();
        pieChartRight = createPieChart();

//        createBarChart();
//        createScatterChart();

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
//        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setWordWrapEnabled(true);
        l.setTextSize(R.dimen.context_text_size); // TODO Dynamic Font
        l.setTextColor(CHART_LEGEND_TEXT_COLOR);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
//        pieChart.getLegend().setEntries(); // TODO Set stack entry text

        pieChart.getLegend().setEnabled(true); // TODO Temporarily removed legends
        pieChart.setBackgroundColor(Color.WHITE);
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
        addDatasetRefresh();
    }

    public void prepareRecord() {
//        if(recordsRight != null && recordsRight.size() == 0) {
//            prepareRightChartRecords(INDEX_NATIONAL);
//        }
        if(recordsRight == null) {
            recordsRight = new ArrayList<>();
//            prepareRightChartRecords(INDEX_NATIONAL);

        }

        prepareRecord(recordsLeft);
        prepareRecord(recordsRight);
    }

    public void addDatasetRefresh() { // TODO spinner dynamic
        hideGraphOverview();
        removeGraphViews();
        int position = spChartType.getSelectedItemPosition();

        // NOTE: DO NOT UNCOMMENT the code below.
//        if(position == 0){ // Overview
//            showGraphOverview();
//            graphLayoutCenter.setVisibility(General.getVisibility(false));
//            if(spRecordColumn != null) {
//                spRecordColumn.setVisibility(General.getVisibility(false));
//            }
//        } else
//        if(position == INDEX_PIE || position == INDEX_SCATTER) { // Pie Chart TODO commented out
////            preparePieChart();
//            getSpinnerFunction(position);
//        }
//        else if (position == 2) { // Bar Chart
//            prepareBarChart();
//        }
//        else if (position == 3) { // Scatter Chart TODO Remove?
//            prepareScatterChart();
//        }

        // hide control of right chart for scatter and bubble plot
        // **edited, only show on Pie
//        if(position == INDEX_PIE) { // TODO commented out
//            spRightChart.setVisibility(View.VISIBLE);
//            tvRightChart.setVisibility(View.VISIBLE);
//        } else {
//            spRightChart.setVisibility(View.GONE);
//            tvRightChart.setVisibility(View.GONE);
//        }

//        refreshChartParams();
        refreshCharts();
    }

    private void prepareRecord(ArrayList<PatientRecord> records/*String schoolName, String date*/){
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get patient records from database */
        //Log.d(TAG, "number of dataset = "+datasetList.size());
        records.clear();
        for(int i = 0; i < datasetList.size(); i++) {
            String dataset = datasetList.get(i);
            String school = dataset.substring(0, dataset.indexOf("("));
            String date = dataset.substring(dataset.indexOf("(")+1,dataset.indexOf(")"));
            //Log.d(TAG, "dataset to be added: "+dataset);
            int schoolId = getSchoolId(school, date);
            //Log.d(TAG, "schoolId = "+schoolId);
            if(schoolId != -1) {
                records.addAll(getBetterDb.getRecordsFromSchool(schoolId, date));
                Log.d(TAG, "added dataset: "+dataset);
            }
        }

        // TODO records right equivalent
//        if(recordsRight == null) {
//            prepareRightChartRecords(INDEX_NATIONAL);
//        }

        /* close database after query */
        getBetterDb.closeDatabase();
    }

//    private void prepareRightChartRecords() {
//        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
//        /* ready database for reading */
//        try {
//            getBetterDb.openDatabaseForRead();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        /* get patient records from database */
//        String year = date.substring(date.length()-4);
//        if(rightChartContent.contains("National")) {
//            recordsRight = getBetterDb.getAllRecordsOnYear(year);
//        } else if(rightChartContent.contains("Region")){
//            recordsRight = getBetterDb.getRecordsFromRegionOnYear(schoolID, year);
//        } else if(rightChartContent.contains("Province")) {
//            recordsRight = getBetterDb.getRecordsFromProvinceOnYear(schoolID, year);
//        } else { // same municipality
//            recordsRight = getBetterDb.getRecordsFromMunicipalityOnYear(schoolID, year);
//        }
//        Log.v(TAG, "right records size: "+recordsRight.size());
//        /* close database after query */
//        getBetterDb.closeDatabase();
//    }

    private void prepareRightChartRecords(int comparisonType) {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get patient records from database */
        String year = date.substring(date.length()-4);

        switch(comparisonType) {
            case INDEX_NATIONAL:
                recordsRight = getBetterDb.getAllRecordsOnYear(year);
                break;
            case INDEX_MUNICIPAL:
                recordsRight = getBetterDb.getRecordsFromMunicipalityOnYear(schoolID, year);
                break;
            default:
        }
//        if(rightChartContent.contains("National")) {
//            recordsRight = getBetterDb.getAllRecordsOnYear(year);
//        } else if(rightChartContent.contains("Region")){
//            recordsRight = getBetterDb.getRecordsFromRegionOnYear(schoolID, year);
//        } else if(rightChartContent.contains("Province")) {
//            recordsRight = getBetterDb.getRecordsFromProvinceOnYear(schoolID, year);
//        } else { // same municipality
//            recordsRight = getBetterDb.getRecordsFromMunicipalityOnYear(schoolID, year);
//        }

        /* close database after query */
        getBetterDb.closeDatabase();

        if(isFilterEnabled) {
            filterComparisonRecords();
        }
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
        recordColumns = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.record_column_array))); // Initialize columns to appear
//        listRecordColumns = new ArrayList<String>(Arrays.asList(recordColumns));

        // TODO add additional overviews here + corresponding params
//        stackedBarCharts = new ArrayList<>();
        overviewEntries = new ArrayList<>();
        graphStackedBarCharts = new ArrayList<>();

        TextView title, focusTitle, focusValue;

        // TODO Add new graph here
        // BMI
        title = findViewById(R.id.tv_bmi_graph_focus_title); // Note: R.id. naming was inverted, but this is correct (_focus_title is title)
        focusTitle = findViewById(R.id.tv_bmi_graph_title);
        focusValue = findViewById(R.id.tv_end_bmi);
        overviewEntries.add(new OverviewEntry(stackedBMI, title, focusTitle, focusValue));
        graphStackedBarCharts.add(graphBMI);

        // Visual Acuity Left
        title = findViewById(R.id.tv_va_left_graph_focus_title);
        focusTitle = findViewById(R.id.tv_va_left_graph_title);
        focusValue = findViewById(R.id.tv_end_va_left);
        overviewEntries.add(new OverviewEntry(stackedVisualAcuityLeft, title, focusTitle, focusValue));
        graphStackedBarCharts.add(graphVisualAcuityLeft);

        // Visual Acuity Right
        title = findViewById(R.id.tv_va_right_graph_focus_title);
        focusTitle = findViewById(R.id.tv_va_right_graph_title);
        focusValue = findViewById(R.id.tv_end_va_right);
        overviewEntries.add(new OverviewEntry(stackedVisualAcuityRight, title, focusTitle, focusValue));
        graphStackedBarCharts.add(graphVisualAcuityRight);

        // Color Vision
        title = findViewById(R.id.tv_va_color_graph_focus_title);
        focusTitle = findViewById(R.id.tv_va_color_graph_title);
        focusValue = findViewById(R.id.tv_end_va_color);
        overviewEntries.add(new OverviewEntry(stackedColorBlindness, title, focusTitle, focusValue));
        graphStackedBarCharts.add(graphColorBlindness);

        // Hearing Left
        title = findViewById(R.id.tv_hearing_left_graph_focus_title);
        focusTitle = findViewById(R.id.tv_hearing_left_graph_title);
        focusValue = findViewById(R.id.tv_end_hearing_left);
        overviewEntries.add(new OverviewEntry(stackedHearingLeft, title, focusTitle, focusValue));
        graphStackedBarCharts.add(graphHearingLeft);


        // Hearing Right
        title = findViewById(R.id.tv_hearing_right_graph_focus_title);
        focusTitle = findViewById(R.id.tv_hearing_right_graph_title);
        focusValue = findViewById(R.id.tv_end_hearing_right);
        overviewEntries.add(new OverviewEntry(stackedHearingRight, title, focusTitle, focusValue));
        graphStackedBarCharts.add(graphHearingRight);


        // Gross Motor
        title = findViewById(R.id.tv_gross_motor_graph_focus_title);
        focusTitle = findViewById(R.id.tv_gross_motor_graph_title);
        focusValue = findViewById(R.id.tv_end_gross_motor);
        overviewEntries.add(new OverviewEntry(stackedGrossMotor, title, focusTitle, focusValue));
        graphStackedBarCharts.add(graphGrossMotor);


        // Fine Motor (Dominant)
        title = findViewById(R.id.tv_fine_dominant_graph_focus_title);
        focusTitle = findViewById(R.id.tv_fine_dominant_graph_title);
        focusValue = findViewById(R.id.tv_end_fine_dominant);
        overviewEntries.add(new OverviewEntry(stackedFineMotorDominant, title, focusTitle, focusValue));
        graphStackedBarCharts.add(graphFineMotorDominant);


        // Fine Motor (Non-dominant)
        title = findViewById(R.id.tv_fine_non_graph_focus_title);
        focusTitle = findViewById(R.id.tv_fine_non_graph_title);
        focusValue = findViewById(R.id.tv_end_fine_non);
        overviewEntries.add(new OverviewEntry(stackedFineMotorNon, title, focusTitle, focusValue));
        graphStackedBarCharts.add(graphFineMotorNon);

        // Fine Motor (Hold)
        title = findViewById(R.id.tv_fine_hold_graph_focus_title);
        focusTitle = findViewById(R.id.tv_fine_hold_graph_title);
        focusValue = findViewById(R.id.tv_end_fine_hold);
        overviewEntries.add(new OverviewEntry(stackedFineMotorHold, title, focusTitle, focusValue));
        graphStackedBarCharts.add(graphFineMotorHold);
    }

    private void computeOverviewParams(ArrayList<OverviewEntry> chartsEntries, ArrayList<ViewGroup.LayoutParams> params) {
        params = new ArrayList<>();
        HorizontalBarChart chart;
        for(int i = 0; i < chartsEntries.size(); i++) {
            chart = chartsEntries.get(i).getStackedBarChart();
            params.add(chart.getLayoutParams());

            params.get(i).height = graphStackedBarCharts.get(i).getHeight()+(int)overviewHeightIncrease; // ViewGroup.LayoutParams.MATCH_PARENT; TODO ADD CHART HEIGHT

            params.get(i).width = ViewGroup.LayoutParams.MATCH_PARENT;
            chart.setY(chart.getY()-(overviewHeightIncrease/2f));

//            chart.setX(computePercentHalf(chart.getWidth(), offsetPercent));
//            chart.setY(computePercentHalf(chart.getHeight(), offsetPercent)/offsetYDivider);
        }
    }

    private void addDataSet() {
        if(spChartType != null) {
            int position = spChartType.getSelectedItemPosition();
            switch(position) {
                case INDEX_OVERVIEW:
                    prepareOverviewChartData();
                    loadSpecificBarChart(this.currentRecordColumn);
                    break;
                case INDEX_NATIONAL:
                    spRightChart.setSelection(0);
                    prepareNationalChartData();
                    loadSpecificBarChart(this.currentRecordColumn);
                    break;
                case INDEX_MUNICIPAL:
                    spRightChart.setSelection(3);
                    prepareNationalChartData();
                    loadSpecificBarChart(this.currentRecordColumn);
                    break;
//                case INDEX_PIE:
//                    preparePieChartData(pieChartLeft, yDataLeft);
//                    preparePieChartData(pieChartRight, yDataRight);
//                    break;
                case INDEX_SCATTER:
//                    prepareScatterChartData();
                    break;
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


    /**
     * Create a stacked bar graph for each entry in overviewEntries.
     */
    private void prepareOverviewChartData() {
        for(int i = 0; i < overviewEntries.size(); i++) {
            // Initialize the stackedBarChart variable of the overviewEntry
            overviewEntries.get(i).setStackedBarChart(prepareStackedOverview(recordColumns.get(i), overviewEntries.get(i)));

            // Then add the initialized entry to graphStackedBarCharts so that it will appear on screen
            graphStackedBarCharts.get(i).addView(overviewEntries.get(i).getStackedBarChart());
        }
        // Then adjust the graph container's size
        computeOverviewParams(overviewEntries, paramsOverview);
    }
    private void prepareNationalChartData() {
        for(int i = 0; i < overviewEntries.size(); i++) {
            // Initialize the stackedBarChart variable of the overviewEntry
            overviewEntries.get(i).setStackedBarChart(prepareNationalOverview(recordColumns.get(i), overviewEntries.get(i)));

            // Then add the initialized entry to graphStackedBarCharts so that it will appear on screen
            graphStackedBarCharts.get(i).addView(overviewEntries.get(i).getStackedBarChart());
        }
        // Then adjust the graph container's size
        computeOverviewParams(overviewEntries, paramsOverview);
    }
    private void loadSpecificBarChart(int recordIndex) { // TODO Implement
        this.currentRecordColumn = recordIndex;
        if(this.barSpecific != null) {
            this.barSpecific.clear();
        }

        if(spChartType != null) {
            int position = spChartType.getSelectedItemPosition();

            this.graphSpecificBarSingle.removeAllViews();
            switch(position) {
                case INDEX_OVERVIEW:
                    prepareSpecificBarChartDataOverview(recordColumns.get(recordIndex));
                    break;
                case INDEX_NATIONAL:
                case INDEX_MUNICIPAL:
                    prepareSpecificBarChartDataNational(recordColumns.get(recordIndex));
                    break;
                default:
                    prepareSpecificBarChartDataOverview(recordColumns.get(recordIndex));
            }
            tvSpecificTitle.setText(recordColumns.get(recordIndex));
        }

    }

    private void prepareSpecificBarChartDataNational(String recordType) {
        ChartDataValue chartDataValue = prepareChartData(recordType);

        this.barSpecific = new BarChart(this);
        this.barSpecific.setOnChartValueSelectedListener(getOverviewOnChartValueSelectedListener()); // TODO Remove (?)
        this.graphSpecificBarSingle.addView(this.barSpecific);


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



        ArrayList<BarEntry> yValues1 = getMergedBarValues(pDataSchool, recordType);
        ArrayList<BarEntry> yValues2 = getMergedBarValues(pDataNational, recordType);

        // TODO Use this for unmerged bar graphs
//        ArrayList<BarEntry> yValues1 = getUnmergedBarValues(pDataSchool);
//        ArrayList<BarEntry> yValues2 = getUnmergedBarValues(pDataNational);



        /* create bar chart dataset */
        BarDataSet barDataSetSchool = new BarDataSet(yValues1, "");
        BarDataSet barDataSetNational = new BarDataSet(yValues2, "");

        // Set colors
        barDataSetSchool.setColors(ColorThemes.getMergedStackedColorSet(recordType));
//        barDataSetNational.setColors(ColorThemes.getMergedStackedColorSet(recordType));
        barDataSetNational.setColors(General.getColorSetByCount(barDataSetNational.getEntryCount(), ColorThemes.cNational));
//        barDataSetSchool.setColors(ColorThemes.getStackedColorSet(recordType));
//        barDataSetNational.setColors(ColorThemes.getStackedColorSet(recordType));


        BarData barData = new BarData(barDataSetSchool, barDataSetNational);
//        BarData barData = new BarData(barDataSetList);
        //BarData barData = new BarData(xData, barDataSet);
        this.barSpecific.setData(barData);
        this.barSpecific.getAxisLeft().setEnabled(true);
        adjustSpecificBarChartParams();

        formatBarSpecificAppearanceNational(barDataSetSchool, barDataSetNational, chartDataValue.getxData());


        // Adds "National Equivalent" in legend
        String[] barLabels = StringConstants.getMergedLabels(recordType, chartDataValue.getxData());
        ArrayList<String> barLabelList = new ArrayList<>(Arrays.asList(barLabels));

        switch(spChartType.getSelectedItemPosition()) {
            case INDEX_MUNICIPAL:
                barLabelList.add("Municipal Equivalent");
                break;
            case INDEX_NATIONAL:
                barLabelList.add("National Equivalent");
                break;
            default:
                barLabelList.add("National Equivalent");
        }


        String[] barLabelWithNational = General.convertToString(barLabelList);

        int[] legendColors = ColorThemes.getMergedStackedColorSet(recordType);
        ArrayList<Integer> colorList = General.convertToInteger(legendColors);
        colorList.add(ColorThemes.cNational); // TODO change national color
        int[] legendColorsWithNational = General.convertToInteger(colorList);

        prepareBarSpecificLegend(this.barSpecific, barLabelWithNational, legendColorsWithNational);


        float groupSpace = 0.06f; // TODO declare properly somewhere, maybe make constant
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f;
        barData.setBarWidth(barWidth); // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"

        barSpecific.groupBars(-0.5f, groupSpace, barSpace); // perform the "explicit" grouping

//        barSpecific.invalidate(); // refresh
//        barSpecific.setTouchEnabled(true);

// TODO color change on click
//        int targetValueIndex = 0;
//        int targetNameIndex = getRecordColumn(recordType);
//        for(int i = 0; i < chartDataValue.getxData().length; i++) {
//            if(chartDataValue.getxData()[i].equals(ValueCounter.targetValueIndices[targetNameIndex])) { // Find target value to be highlighted later
//                targetValueIndex = i;
//            }
//        }
//        int targetColor = ColorThemes.getStackedColorSet(recordType)[targetValueIndex];
//        barSpecific.getData().getDataSets().get(0).getColors().set(targetValueIndex, targetColor);
//        barSpecific.getData().getDataSets().get(1).getColors().set(targetValueIndex, targetColor);
        this.barSpecific.notifyDataSetChanged();
    }

    /**
     * Returns a list of BarEntries that merge certain values (e.g. in Visual Acuity, instead of the 20/200, 20/100 <...> it merges values so that it
     * will be Normal, Near-normal, Moderate Loss <...>
     * @param pData
     * @param recordType
     * @return
     */
    public ArrayList<BarEntry> getMergedBarValues(float[] pData, String recordType) {
        ArrayList<BarEntry> yBarValues = new ArrayList<>();
        ArrayList<Float> mergeContainer = new ArrayList<>();

        float mergeSum = 0f;
        int index = 0;
        for(int i = 0; i < pData.length; i++) {

            if(StringConstants.isMergeStartingIndex(recordType, i) == StringConstants.MergeType.START) { // Check if value has to be merged (stacked, i.e. 20/25 to 20/5 in vision)
                mergeContainer = new ArrayList<>();
                mergeContainer.add(pData[i]);
                mergeSum = pData[i];
            }
            else if(StringConstants.isMergeStartingIndex(recordType, i) == StringConstants.MergeType.CONT) {
                mergeContainer.add(pData[i]);
                mergeSum += pData[i];
            }
            else if(StringConstants.isMergeStartingIndex(recordType, i) == StringConstants.MergeType.END) {

                mergeContainer.add(pData[i]);
                mergeSum += pData[i];
                yBarValues.add(new BarEntry(index, mergeSum));
                mergeSum = 0f;
                index ++;
            }
            else {
                yBarValues.add(new BarEntry(index, pData[i]));
                index ++;
            }
        }
        return yBarValues;
    }
    public ArrayList<Float> getMergedFloatValues(float[] pData, String recordType) {
        ArrayList<BarEntry> yBarValues = new ArrayList<>();
        ArrayList<Float> mergeContainer = new ArrayList<>();

        float mergeSum = 0f;
        int index = 0;
        for(int i = 0; i < pData.length; i++) {

            if(StringConstants.isMergeStartingIndex(recordType, i) == StringConstants.MergeType.START) { // Check if value has to be merged (stacked, i.e. 20/25 to 20/5 in vision)
//                mergeContainer.add(pData[i]);
                mergeSum = pData[i];
            }
            else if(StringConstants.isMergeStartingIndex(recordType, i) == StringConstants.MergeType.CONT) {
//                mergeContainer.add(pData[i]);
                mergeSum += pData[i];
            }
            else if(StringConstants.isMergeStartingIndex(recordType, i) == StringConstants.MergeType.END) {

//                mergeContainer.add(pData[i]);
                mergeSum += pData[i];
                mergeContainer.add(mergeSum);
                yBarValues.add(new BarEntry(index, mergeSum));
                mergeSum = 0f;
                index ++;
            }
            else {
                mergeContainer.add(pData[i]);
                yBarValues.add(new BarEntry(index, pData[i]));
                index ++;
            }
        }
        return mergeContainer;
    }
    public ArrayList<BarEntry> getUnmergedBarValues(float[] pData) {
        ArrayList<BarEntry> yBarValues = new ArrayList<>();

        for(int i = 0; i < pData.length; i++) {
            yBarValues.add(new BarEntry(i, pData[i]));
        }

        return yBarValues;
    }

    // Plug values into specific bar chart
    private void prepareSpecificBarChartDataOverview(String recordType) { // TODO Implement
        ChartDataValue chartDataValue = prepareChartData(recordType);

        this.barSpecific = new BarChart(this);
        this.barSpecific.setOnChartValueSelectedListener(getOverviewOnChartValueSelectedListener()); // TODO Remove (?)
        this.graphSpecificBarSingle.addView(this.barSpecific);


        // Variables to hold the converted yData (from int to float) and sum
        float[] fDataSchool, pDataSchool;
        ArrayList<Float> mDataSchool;
        float fSumSchool;

        // Convert yData to float
        fDataSchool = General.convertToFloat(chartDataValue.getyDataLeft());
//        fDataNational = General.convertToFloat(chartDataValue.getyDataRight());

        // Get yData sum
        fSumSchool = General.getArraySum(fDataSchool);

        // Convert to percentages
        pDataSchool = General.computePercentEquivalent(fDataSchool, fSumSchool);

        // Convert to merged form if applicable
        mDataSchool = getMergedFloatValues(pDataSchool, recordType);

        int index = 0;
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for(int i = 0; i < mDataSchool.size(); i++) {
            yVals1.add(new BarEntry(index, mDataSchool.get(i)));
            index ++;
        }

        /* create bar chart dataset */
        BarDataSet barDataSet = new BarDataSet(yVals1, "");
        // Set colors
        barDataSet.setColors(ColorThemes.getMergedStackedColorSet(recordType));

        List<IBarDataSet> barDataSetList = new ArrayList<>();
        barDataSetList.add(barDataSet);

        BarData barData = new BarData(barDataSetList);
        //BarData barData = new BarData(xData, barDataSet);
        this.barSpecific.setData(barData);
        this.barSpecific.getAxisLeft().setEnabled(true);
        this.barSpecific.getAxisRight().setEnabled(false);
//        this.barSpecific.getAxisRight().setDrawAxisLine(true);
        adjustSpecificBarChartParams();
        formatBarSpecificAppearance(barDataSet, chartDataValue.getxData());


//        YAxis rightAxis = barSpecific.getAxisRight();
//        YAxis leftAxis = barSpecific.getAxisLeft();
        XAxis xAxis = barSpecific.getXAxis();
        LimitLine limitLine = new LimitLine(barSpecific.getBarData().getEntryCount()-0.5f, ""); // TODO label
        limitLine.setLineColor(ColorThemes.cGray);
        limitLine.setLineWidth(0.1f);
        xAxis.addLimitLine(limitLine);

        String[] barLabels = StringConstants.getMergedLabels(recordType, chartDataValue.getxData());
        prepareBarSpecificLegend(this.barSpecific, barLabels, ColorThemes.getMergedStackedColorSet(recordType));
        this.barSpecific.notifyDataSetChanged();
    }

    private void removeLegendViews() {
        for(int i = 0; i < llBarSpecificLabels.size(); i++) {
            llBarSpecificLabels.get(i).removeAllViews();
        }
    }

    private void prepareBarSpecificLegend(BarChart chart, String[] legendText, int[] colorThemes) {
        barSpecific.getLegend().setEnabled(false); // Show/hide color legends

        // Remove old legend contents
        removeLegendViews();

        LinearLayout entry;
        TextView entryText;
        ImageView entryColor;
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int i = 0; i < legendText.length; i++) {
            entry = (LinearLayout) inflater.inflate(R.layout.item_bar_specific_legend, null);
            entryText = entry.findViewById(R.id.tv_legend_text);
            entryColor = entry.findViewById(R.id.iv_legend_color);
            entryText.setText(legendText[i]);
            entryColor.setBackgroundColor(colorThemes[i]);
            (this.llBarSpecificLabels.get(i)).addView(entry);
        }
//        List<LegendEntry> entries = new ArrayList<LegendEntry>();
//        LegendEntry entry;
//        for(int i = 0; i < legendText.length; i++) {
//            entry = new LegendEntry();
//            entry.label = legendText[i];
//            entry.formColor = colorThemes[i];
//            entries.add(entry);
//            Log.e("LEGEND", ": "+legendText[i]);
//
//        }
//        Legend legend = chart.getLegend();
//        legend.setCustom(entries);
//        barSpecific.getLegend().setWordWrapEnabled(true);
    }
    private void formatBarSpecificAppearanceNational(BarDataSet setSchool, BarDataSet setNational, String[] xLabels) {
        setSchool.setBarBorderWidth(0.5f);
        setSchool.setBarBorderColor(ColorThemes.cLightGray);

        setNational.setBarBorderWidth(0.5f);
        setNational.setBarBorderColor(ColorThemes.cLightGray);

        formatBarSpecificNationalAxis(this.barSpecific, xLabels);
    }
    private void formatBarSpecificAppearance(BarDataSet set, String[] xLabels) {
        set.setBarBorderWidth(0.5f);
        set.setBarBorderColor(ColorThemes.cLightGray);
        formatBarSpecificAxis(this.barSpecific, xLabels);
    }
    private void formatBarSpecificNationalAxis(BarChart chart, String[] xLabels) {

        chart.getDescription().setEnabled(false);

//        chart.getAxisLeft().setDrawLabels(false);
//        chart.getAxisLeft().setDrawGridLines(false);
//        chart.getAxisLeft().setDrawAxisLine(false);

        chart.getAxisRight().setDrawLabels(false);
//        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);

        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);


//        chart.setVisibleYRange(0f, 100f, leftAxis.getAxisDependency());
//        leftAxis.setLabelCount(3, true);
//        leftAxis.setAxisMinimum(0f);
//        leftAxis.setAxisMaximum(100f);


        // yAXIS
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();

        leftAxis.removeAllLimitLines();
        leftAxis.setAxisMaximum(100);
        leftAxis.setAxisMinimum(0);
        leftAxis.setLabelCount(5);

        rightAxis.removeAllLimitLines();
        rightAxis.setAxisMaximum(100);
        rightAxis.setAxisMinimum(0);
        rightAxis.setLabelCount(5);

        // xAXIS LABELS
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);



        LimitLine limitLine;
        float groupSpace = 0.06f; // TODO declare properly somewhere, maybe make constant
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"
        float linePosition = barSpace+barWidth+(groupSpace/2f);
        int barCount = (chart.getBarData().getEntryCount()/2)+1;
        int index = -1;
        for(int i = 0; i < barCount; i++) {
            limitLine = new LimitLine(index*linePosition, ""); // TODO label
            index += 2;
            limitLine.setLineColor(ColorThemes.cGray);
            limitLine.setLineWidth(0.1f);
            xAxis.addLimitLine(limitLine);
        }



        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
//        chart.setAutoScaleMinMaxEnabled(false);
        chart.setTouchEnabled(false);

//        chart.setVisibleYRangeMaximum(100f, YAxis.AxisDependency.LEFT);
//        chart.getAxisLeft().setAxisMaximum(100f);
//        chart.getAxisLeft().setAxisMinimum(0f);

    }


    private void formatBarSpecificAxis(BarChart chart, String[] xLabels) {

        chart.getDescription().setEnabled(false);

//        chart.getAxisLeft().setDrawLabels(false);
//        chart.getAxisLeft().setDrawGridLines(false);
//        chart.getAxisLeft().setDrawAxisLine(false);

//        chart.getAxisRight().setDrawLabels(false);
//        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);

        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawGridLines(false);

//        chart.getXAxis().setDrawAxisLine(false);


//        chart.setVisibleYRange(0f, 100f, leftAxis.getAxisDependency());
//        leftAxis.setLabelCount(3, true);
//        leftAxis.setAxisMinimum(0f);
//        leftAxis.setAxisMaximum(100f);


        // yAXIS
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();

        leftAxis.removeAllLimitLines();
        leftAxis.setAxisMaximum(100);
        leftAxis.setAxisMinimum(0);
        leftAxis.setLabelCount(5);

        rightAxis.removeAllLimitLines();
        rightAxis.setAxisMaximum(100);
        rightAxis.setAxisMinimum(0);
        rightAxis.setLabelCount(5);

        // xAXIS LABELS
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
//        xDataContainer = xLabels;
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, AxisBase axisBase) {
//                return xDataContainer[(int) v];
//            }
//        });


//        chart.setViewPortOffsets(0f, 0f,0f,0f);
//        xAxis.setValueFormatter(new StackedBarChartIAxisFormatter(values));
//        xAxis.setLabelCount(1, true);


        // Y Axis
//        LimitLine llStart, llEnd;
//        llStart = new LimitLine(100, "100"); // TODO label
//        llStart.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
//        llStart.setLineColor(ColorThemes.cLightGray);
//        llStart.setLineWidth(1f);
//        llStart.setTextColor(Color.BLACK);
//        llStart.setTextSize(VALUE_TEXT_SIZE);
//        leftAxis.addLimitLine(llStart);

//        llEnd = new LimitLine(100f, ""); // TODO Label
//        llEnd.setLineColor(llStart.getLineColor());
//        llEnd.setLineWidth(1f);
//        llEnd.setTextColor(llStart.getTextColor());
//        llEnd.setTextSize(VALUE_TEXT_SIZE);
//        leftAxis.addLimitLine(llEnd);



        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
//        chart.setAutoScaleMinMaxEnabled(false);
        chart.setTouchEnabled(false);

//        chart.setVisibleYRangeMaximum(100f, YAxis.AxisDependency.LEFT);
//        chart.getAxisLeft().setAxisMaximum(100f);
//        chart.getAxisLeft().setAxisMinimum(0f);

    }



    private void adjustSpecificBarChartParams() {
        ViewGroup.LayoutParams params = this.barSpecific.getLayoutParams();

        params.height = ViewGroup.LayoutParams.MATCH_PARENT;//this.graphSpecificBarSingle.getHeight(); // ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            chart.setMinimumHeight(graphStackedBarCharts.get(i).getLayoutParams().height);
//            chart.setMinimumWidth(graphStackedBarCharts.get(i).getLayoutParams().width);
//        this.barSpecific.setY(this.barSpecific.getY()-(overviewHeightIncrease/2f));
//        this.barSpecific.setBackgroundColor(Color.TRANSPARENT); // TODO Remove or make White
    }

    private void switchSubtitleMode() {
        switch (this.subtitleMode) {
            case SHOW_HIGHEST:
                this.subtitleMode = SubtitleMode.SHOW_TARGET;
                break;
            case SHOW_TARGET:
                this.subtitleMode = SubtitleMode.SHOW_HIGHEST;
                break;
            default:
                this.subtitleMode = SubtitleMode.SHOW_TARGET;
        }
        clickChartType();
//        spChartType.setSelection(spChartType.getSelectedItemPosition());
//        refreshCharts();
    }
    private void loadSubtitleMode() {
        switch (this.subtitleMode) {
            case SHOW_HIGHEST:
                highlightHighestValue();
                break;
            case SHOW_TARGET:
                highlightTargetValue();
                break;
            default:
                highlightHighestValue();
        }
    }

    private void highlightHighestValue() {
        this.subtitleMode = SubtitleMode.SHOW_HIGHEST;
        tvRightSubtitle.setText(subtitleHighest);
        tvRightSubtitle.setTextColor(ColorThemes.cSubtitleHighest);
        ivRightSubtitleColor.setBackgroundColor(ColorThemes.cSubtitleHighest);
        ivRightSubtitleImage.setImageDrawable(imgRightSubtitleHighest);
    }

    private void highlightTargetValue() {
        this.subtitleMode = SubtitleMode.SHOW_TARGET;
        tvRightSubtitle.setText(subtitleTarget);
        tvRightSubtitle.setTextColor(ColorThemes.cSubtitleTarget);
        ivRightSubtitleColor.setBackgroundColor(ColorThemes.cSubtitleTarget);
        ivRightSubtitleImage.setImageDrawable(imgRightSubtitleTarget);
    }

    private ArrayList<Float> adjustToHighlightMode(String recordType, ArrayList<Float> mData, HashMap<String, Float> mapValues, String[] barLabels) {

        switch (this.subtitleMode) {
            case SHOW_HIGHEST: // Do nothing
                Collections.sort(mData); // Sort ascending
                Collections.reverse(mData); // Reverse to get descending
                break;
            case SHOW_TARGET: // Move target value to index 0
                String targetLabel = StringConstants.getTargetLabel(recordType);

                int targetIndex = Arrays.asList(barLabels).indexOf(targetLabel); // mData.indexOf(targetValue);
                Collections.swap(mData, 0, targetIndex);

                Collections.sort(mData); // Sort ascending
                Collections.reverse(mData); // Reverse to get descending
                Float targetValue = mapValues.get(targetLabel);
                targetIndex = mData.indexOf(targetValue);
                Collections.swap(mData, 0, targetIndex);
                break;
        }
        return mData;
    }

    /**
     * For multi-value sorting (national/municipal). Sort and get highest value of mData then sort mDataNational accordingly.
     * @param recordType
     * @param mData
     * @param mapValues
     * @param mDataNational
     * @param mapValuesNational
     * @param barLabels
     * @return
     */
    private void adjustToHighlightMode(String recordType,
                                       ArrayList<Float> mData,
                                       HashMap<String, Float> mapValues,
                                       HashMap<Float, String> mapLabels,
                                       ArrayList<Float> mDataNational,
                                       HashMap<String, Float> mapValuesNational,
                                       HashMap<Float, String> mapNationalLabels,
                                       String[] barLabels) {

        switch (this.subtitleMode) {
            case SHOW_HIGHEST: // Do nothing
                Collections.sort(mData); // Sort ascending
                Collections.reverse(mData); // Reverse to get descending

                int highestindex = StringConstants.getLabelIndexOf(recordType, mapLabels.get(mData.get(0)));
                Collections.swap(mDataNational, 0, highestindex);
                break;
            case SHOW_TARGET: // Move target value to index 0
                String targetLabel = StringConstants.getTargetLabel(recordType);

                int targetIndex = Arrays.asList(barLabels).indexOf(targetLabel); // mData.indexOf(targetValue);
                Collections.swap(mData, 0, targetIndex);
                Collections.swap(mDataNational, 0, targetIndex);


//                Collections.sort(mData); // Sort ascending
//                Collections.reverse(mData); // Reverse to get descending
//                Float targetValue = mapValues.get(targetLabel);
//                targetIndex = mData.indexOf(targetValue);
//                Collections.swap(mData, 0, targetIndex);


                break;
        }
    }

    private HorizontalBarChart prepareStackedOverview(String recordType, OverviewEntry overviewEntry) {
        HorizontalBarChart chart;

        tvRightScrollTitle.setText(titleScrollRightOverview);

        ChartDataValue chartDataValue = prepareChartData(recordType);
//        Log.e("RECORD", recordType);
        chart = new HorizontalBarChart(this);
        chart.setOnChartValueSelectedListener(getOverviewOnChartValueSelectedListener()); // TODO Removed
        // Variables to hold the converted yData (from int to float) and sum
        float[] fDataSchool, fDataNational, pDataSchool;
        ArrayList<Float> mDataSchool;
        float fSumSchool;

        // Convert yData to float
        fDataSchool = General.convertToFloat(chartDataValue.getyDataLeft());

        // Get yData sum
        fSumSchool = General.getArraySum(fDataSchool);

        // Convert to percentages
        pDataSchool = General.computePercentEquivalent(fDataSchool, fSumSchool);

        // Get merged values if applicable
        mDataSchool = getMergedFloatValues(pDataSchool, recordType);



        String[] barLabels = StringConstants.getMergedLabels(recordType, chartDataValue.getxData());
//        HashMap<Float, String> mapLabels = mapValuesToLabels(mDataSchool, barLabels);
        HashMap<Float, String> mapLabels = mapValuesToLabels(mDataSchool, barLabels); // protects against labels with the same value
        HashMap<String, Float> mapValues = mapLabelsToValues(barLabels, mDataSchool);

        mDataSchool = adjustToHighlightMode(recordType, mDataSchool, mapValues, barLabels);


        // Stacked bar entries. xIndex 0 is the bottom
        List<BarEntry> entries = new ArrayList<>();
        BarEntry schoolData = new BarEntry(0f, General.convertToFloat(mDataSchool));
        entries.add(schoolData); // School

        // BarDataSet second parameter is the label
        BarDataSet set = new BarDataSet(entries, recordType);
        List<IBarDataSet> barDataSetList = new ArrayList<>();

        barDataSetList.add(set);
        BarData data = new BarData(barDataSetList); // TODO X Values
        chart.setData(data);

        overviewEntry.setStackedBarChart(chart);


        formatStackBarAppearance(
                recordType,
                overviewEntry,
                mapLabels,
                set, fDataSchool, mDataSchool); // Edit stack bar appearance

        chart.notifyDataSetChanged(); // Call this to reflect chart data changes

        return chart;
    }
    private HashMap<Float, String> mapValuesToLabels(ArrayList<Float> listValues, String[] listLabels) {
        HashMap<Float, String> map = new HashMap<>();
        for(int i = 0; i < listValues.size(); i++) {
            map.put(listValues.get(i), listLabels[i]);
        }
        return map;
    }
    private HashMap<Float, String> mapValuesToLabelsProtected(String recordType, ArrayList<Float> listValues, String[] listLabels) {
        HashMap<Float, String> map = new HashMap<>();
        for(int i = 0; i < listValues.size(); i++) {
            map.put(listValues.get(i)+StringConstants.getLabelIndexOf(recordType, listLabels[i]), listLabels[i]);
        }
        return map;
    }
    private HashMap<String, Float> mapLabelsToValues(String[] listLabels, ArrayList<Float> listValues) {
        HashMap<String, Float> map = new HashMap<>();
        for(int i = 0; i < listValues.size(); i++) {
            map.put(listLabels[i], listValues.get(i));
        }
        return map;
    }

    private HorizontalBarChart prepareNationalOverview(String recordType, OverviewEntry overviewEntry) {
        HorizontalBarChart chart;
        tvRightScrollTitle.setText(titleScrollRightNational);

        ChartDataValue chartDataValue = prepareChartData(recordType);
//        Log.e("RECORD", recordType);
        chart = new HorizontalBarChart(this);
        chart.setOnChartValueSelectedListener(getOverviewOnChartValueSelectedListener()); // TODO Removed

        // Variables to hold the converted yData (from int to float) and sum
        float[] fDataSchool, fDataNational,
                pDataSchool, pDataNational;
        ArrayList<Float> mDataSchool, mDataNational;

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


//        String targetValueLabel;
        String[] barLabels = StringConstants.getMergedLabels(recordType, chartDataValue.getxData());

        mDataSchool = getMergedFloatValues(pDataSchool, recordType);
        mDataNational = getMergedFloatValues(pDataNational, recordType);

        HashMap<Float, String> mapSchoolValues, mapNationalValues; // TODO do something about same-valued entries (i.e. Normal and Abnormal are both 50)
        HashMap<String, Float> mapSchoolLabels, mapNationalLabels;
        mapSchoolValues = mapValuesToLabels(mDataSchool, barLabels);
        mapNationalValues = mapValuesToLabels(mDataNational, barLabels);

        mapSchoolLabels = mapLabelsToValues(barLabels, mDataSchool);
        mapNationalLabels = mapLabelsToValues(barLabels, mDataNational);


        Log.e("Adjust", "1 "+mDataSchool.get(0)+" "+mDataNational.get(0));
        // Sort collections to descending order
        adjustToHighlightMode(recordType, mDataSchool, mapSchoolLabels, mapSchoolValues, mDataNational, mapNationalLabels, mapNationalValues, barLabels);
        Log.e("Adjust", "2 "+mDataSchool.get(0)+" "+mDataNational.get(0));

        String targetValueLabel = mapSchoolValues.get(mDataSchool.get(0)); // StringConstants.getTargetLabel(recordType);
        overviewEntry.getTvFocusTitle().setText(targetValueLabel);


//        Collections.sort(mDataSchool);
//        Collections.reverse(mDataSchool);
//        Collections.sort(mDataNational);
//        Collections.reverse(mDataNational);

        // Stacked bar entries. xIndex 0 is the bottom
        List<BarEntry> entries = new ArrayList<>();

//        BarEntry schoolData = new BarEntry(1f, mapSchoolLabels.get(targetValueLabel));
        BarEntry schoolData = new BarEntry(1f, mDataSchool.get(0)); // Assumes sorted
//        BarEntry schoolData = new BarEntry(1f, mDataSchool[targetValueIndex]);
        entries.add(schoolData); // School

        BarEntry nationalData = new BarEntry(0f, mDataNational.get(0)); // Assumes sorted
//        BarEntry nationalData = new BarEntry(0f, mapNationalLabels.get(targetValueLabel));
        entries.add(nationalData); // National



        // BarDataSet second parameter is the label
        BarDataSet set = new BarDataSet(entries, recordType);
        List<IBarDataSet> barDataSetList = new ArrayList<>();

        barDataSetList.add(set);
        BarData data = new BarData(barDataSetList); // TODO X Values
        chart.setData(data);
        overviewEntry.setStackedBarChart(chart);

//        formatNationalBarAppearance(
//                recordType,
//                targetValueLabel,
//                overviewEntry,
//                barLabels,
//                set, mapSchoolLabels.get(targetValueLabel), mapNationalLabels.get(targetValueLabel)); // Edit stack bar appearance

        formatNationalBarAppearance(
                recordType,
                targetValueLabel,
                overviewEntry,
                set, mDataSchool.get(0), mDataNational.get(0)); // Edit stack bar appearance, assume sorted

        chart.setDrawGridBackground(true);
        chart.setGridBackgroundColor(ColorThemes.cLightGray);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setGridColor(Color.WHITE);


//        chart.getXAxis().setLabelCount(2, true);

        chart.setBorderColor(Color.WHITE);
        chart.notifyDataSetChanged(); // Call this to reflect chart data changes


        return chart;
    }
    private void formatNationalBarAppearance(String recordName,
                                             String targetValueLabel,
                                             OverviewEntry overviewEntry,
                                             BarDataSet barData,
                                             float pDataSchool,
                                             float pDataNational) {
        HorizontalBarChart chart = overviewEntry.getStackedBarChart();
        TextView chartFocus = overviewEntry.getTvFocusTitle();
        TextView chartFocusValue = overviewEntry.getTvFocusValue();

        // Set font color
        chartFocus.setTextColor(ColorThemes.cPrimaryDark);
        barData.setValueFormatter(new StackedBarChartValueFormatter()); // Format values to ###.##% as specified i the passed parameter class

        // Set stack colors here
        barData.setColors(General.getColorSetLightGray(2)); // TODO Dynamic colors

        barData.setStackLabels(xData);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();

        chart.getLegend().setEnabled(false); // GRAPH LEGEND Show/hide stack label legend
        barData.setDrawValues(false); // Show/hide per bar labels

        int roundedPercentValueSchool = Math.round(pDataSchool);
        int roundedPercentValueNational = Math.round(pDataNational);


        // Set focus value to school value
//        chartFocusValue.setText(""+roundedPercentValueSchool+"%");
//        chartFocus.setText(StringConstants.getEditedFocusLabel(recordName, xLabels[targetValueIndex], targetValueIndex));
        chartFocus.setText(targetValueLabel);

        formatNationalBarAxis(chart);
        // TODO length check for color
//        barData.getColors().set(0, ColorThemes.getStackedColorSet(recordName)[targetValueIndex]);


        // Set school value to green
//        barData.getColors().set(0, ColorThemes.getMergedStackedColorSet(recordName)[targetValueIndex]);
        barData.getColors().set(0, ColorThemes.getMergedStackedColor(recordName, targetValueLabel));
        // Set national value to teal
        barData.getColors().set(1, ColorThemes.cNational);


        // Highlight % text if school > national
        if(pDataSchool > pDataNational) {
            // Set focus value to school value
            chartFocusValue.setText(""+roundedPercentValueSchool+"%");

//            // Set school value to green
//            barData.getColors().set(0, ColorThemes.getStackedColorSet(recordName)[targetValueIndex]);
//            // Set national value to grey
//            barData.getColors().set(1, ColorThemes.cLightGray);

            chartFocusValue.setTextColor(barData.getColors().get(0));
        }
        else {
            // Set focus value to national value
            chartFocusValue.setText(""+roundedPercentValueNational+"%");

//            // Set school value to green
//            barData.getColors().set(0, ColorThemes.cLightGray);
//            // Set national value to grey
//            barData.getColors().set(1, ColorThemes.cFail);

            chartFocusValue.setTextColor(ColorThemes.cNational);
        }
        chart.setBackgroundColor(Color.TRANSPARENT);
    }

    private int getRecordColumn(String recordName) {
        return recordColumns.indexOf(recordName);
    }

    private void formatNationalBarAxis(HorizontalBarChart chart) {
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
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);


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



        chart.setViewPortOffsets(0f, 0f,0f,0f);
//        chart.setScaleEnabled(false);
//        chart.setDoubleTapToZoomEnabled(false);
//        chart.setPinchZoom(false);
//        chart.setAutoScaleMinMaxEnabled(false);
        chart.setTouchEnabled(false);

        chart.getAxisLeft().setAxisMaximum(100f);
        chart.getAxisLeft().setAxisMinimum(0f);
//        chart.fitScreen();

    }

    /**
     * Formats the stacked bar chart appearance (for overview). Calls formatStackedBarAxis.
     * @param recordName
     * @param overviewEntry
     * @param mapLabels
     * @param barData
     * @param rawData
     * @param percentData
     */
    private void formatStackBarAppearance(String recordName, OverviewEntry overviewEntry, HashMap<Float, String> mapLabels, BarDataSet barData, float[] rawData, ArrayList<Float> percentData) {
        HorizontalBarChart chart = overviewEntry.getStackedBarChart();
        TextView chartFocus = overviewEntry.getTvFocusTitle();
        TextView chartFocusValue = overviewEntry.getTvFocusValue();

        // Set font color
        chartFocus.setTextColor(ColorThemes.cPrimaryDark);
        barData.setValueFormatter(new StackedBarChartValueFormatter()); // Format values to ###.##% as specified i the passed parameter class

        // Set stack colors here
        barData.setColors(General.getColorSetLightGray(percentData.size())); // TODO Dynamic colors

        barData.setStackLabels(xData);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();

        chart.getLegend().setEnabled(false); // GRAPH LEGEND Show/hide stack label legend
        barData.setDrawValues(false); // Show/hide per bar labels

        LimitLine line;
        float sumX = 0;
//        int highestValueIndex = 0;

        // percentData.length-1 so that the last value won't have a limit line
        for(int i = 0; i < percentData.size(); i++) {
            sumX += percentData.get(i);
            if(sumX != 0 && (i != percentData.size()-1)) { // Don't draw limit line on 0 value and don't draw the last value's line
                line = new LimitLine(sumX, "");
                line.setLineWidth(1f);
                line.setLineColor(Color.WHITE);
                leftAxis.addLimitLine(line);
            }

//            if(percentData.get(highestValueIndex) < percentData.get(i)) { // Find highest value to be highlighted later
//                highestValueIndex = i;
//            }
        }

        int roundedPercentValue = Math.round(percentData.get(0)); // Percent data is sorted in descending (on the function that called this)

        // Set focus value to highest value
        chartFocusValue.setText(""+roundedPercentValue+"%");
//        chartFocus.setText(StringConstants.getEditedFocusLabel(recordName, mapLabels.get(percentData.get(0)), highestValueIndex)); // Assume sorted
        chartFocus.setText(mapLabels.get(percentData.get(0))); // Assume sorted
//        chartFocus.setText(StringConstants.getEditedFocusLabel(recordName, mapLabels.get(percentData[highestValueIndex]), highestValueIndex));
//        chartFocus.setText(xLabels[highestValueIndex]);


        formatStackedBarAxis(chart);
//        Log.e("mapLabels", "percentData "+mapLabels.get(percentData.get(0))+" ");
        int highlightColor = ColorThemes.getMergedStackedColor(recordName, mapLabels.get(percentData.get(0))); // Assumes sorted
        // TODO length check for color
        barData.getColors().set(0, highlightColor);
        if(roundedPercentValue > highlightPercentThreshold) {
            chartFocusValue.setTextColor(highlightColor);
        }
        else {
            chartFocusValue.setTextColor(ColorThemes.cPrimaryDark);
        }


        chart.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * Edits (hides) the grid and axis of the stacked bar chart. Called by formatStackBarAppearance.
     * @param chart
     */
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
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);


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



        chart.setViewPortOffsets(0f, 0f,0f,0f);
//        chart.setScaleEnabled(false);
//        chart.setDoubleTapToZoomEnabled(false);
//        chart.setPinchZoom(false);
//        chart.setAutoScaleMinMaxEnabled(false);
        chart.setTouchEnabled(false);

        chart.getAxisLeft().setAxisMaximum(100f);
        chart.getAxisLeft().setAxisMinimum(0f);
//        chart.fitScreen();

    }

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
    public void removeAllFilters() {
        resetFilterParameters();
        clearAllFilters();
    }
    public void clearAllFilters() {
        // Clear AGE filter
        for(int i = 0; i < filterList.size(); i++) {
            if(filterList.get(i).contains("age")){
                removeFilters(recordsLeft, recordsRight, filterList.get(i));
//                removeFilter(recordsLeft, filterList.get(i));
//                removeFilter(recordsRight, filterList.get(i));
            }
        }

        // Clear GENDER filter
        for(int i = 0; i < filterList.size(); i++) {
            if(filterList.get(i).contains("gender")){
                removeFilters(recordsLeft, recordsRight, filterList.get(i));
//                removeFilter(recordsLeft, filterList.get(i));
//                removeFilter(recordsRight, filterList.get(i));
            }
        }

        // Clear GRADE LEVEL filter
        for(int i = 0; i < filterList.size(); i++) {
            if (filterList.get(i).contains("grade level")) {
                removeFilters(recordsLeft, recordsRight, filterList.get(i));
//            removeFilter(recordsLeft, filterList.get(i));
//            removeFilter(recordsRight, filterList.get(i));
            }
        }
    }

    /**
     * Re-filters the comparison records. Called after re-retrieval.
     */
    private void filterComparisonRecords() {
        filterRightRecords(this.ageEquator, this.ageValue, this.genderValue, this.gradeLevelValue);
    }

    @Override
    public void onDialogPositiveClick(AddFilterDialogFragment dialog) {
        this.ageEquator = dialog.getAgeEquator();
        this.ageValue = dialog.getAgeValue();
        this.genderValue = dialog.getGenderValue();
        this.gradeLevelValue = dialog.getGradeLevelValue();
        filterRecords(ageEquator, ageValue, genderValue, gradeLevelValue);
    }

    /**
     * Filter records as specified by filter dialog.
     * @param ageEquator
     * @param ageValue
     * @param genderValue
     * @param gradeLevelValue
     */
    public void filterRecords(String ageEquator, String ageValue, String genderValue, String gradeLevelValue) {
        //Log.d(AddFilterDialogFragment.TAG, "Filter: age "+ageEquator+" "+ageValue);
        Log.v(TAG, "grade level value = "+gradeLevelValue +"(before filtering)");
        isFilterEnabled = true;
        clearAllFilters();
//        prepareRecord();
        String strFilter = "";
        int filterCount = 0;
        /* filter records*/
        if(!ageValue.trim().contentEquals("")) {
//            for(int i = 0; i < filterList.size(); i++) {
//                if(filterList.get(i).contains("age")){
//                    removeFilters(recordsLeft, recordsRight, filterList.get(i));
////                    removeFilter(recordsLeft, filterList.get(i));
////                    removeFilter(recordsRight, filterList.get(i));
//                }
//            }
            filterRecordsByAge(recordsLeft, ageEquator, ageValue);
            filterRecordsByAge(recordsRight, ageEquator, ageValue);

            prepareFilterList("age "+ageEquator+" "+ageValue);
            strFilter += "age "+ageEquator.toString()+" "+ageValue;
            filterCount ++;
        }
        if(!genderValue.trim().contentEquals("N/A")) {
//            for(int i = 0; i < filterList.size(); i++) {
//                if(filterList.get(i).contains("gender")){
//                    removeFilters(recordsLeft, recordsRight, filterList.get(i));
////                    removeFilter(recordsLeft, filterList.get(i));
////                    removeFilter(recordsRight, filterList.get(i));
//                }
//            }
            filterRecordsByGender(recordsLeft, genderValue);
            filterRecordsByGender(recordsRight, genderValue);
            prepareFilterList("gender = "+genderValue);
            if(!strFilter.equals("")) {
                strFilter += ", ";
            }
            strFilter += genderValue.toLowerCase();
            filterCount ++;
        }
        if(!gradeLevelValue.trim().contentEquals("N/A")) {
//            for(int i = 0; i < filterList.size(); i++) {
//                if(filterList.get(i).contains("grade level")){
//                    removeFilters(recordsLeft, recordsRight, filterList.get(i));
////                    removeFilter(recordsLeft, filterList.get(i));
////                    removeFilter(recordsRight, filterList.get(i));
//                }
//            }
            filterRecordsByGradeLevel(recordsLeft, gradeLevelValue);
            filterRecordsByGradeLevel(recordsRight, gradeLevelValue);
            prepareFilterList("grade level = "+gradeLevelValue);

            if(!strFilter.equals("")) {
                if(filterCount > 1) {
                    strFilter += ", and";
                }
                else {
                    strFilter += " and";
                }
            }
            strFilter += "grade "+gradeLevelValue;
        }


        filterAdapter.notifyDataSetChanged();
        refreshCharts();
//        spinnerRefresh();
        if(!strFilter.equals("")) {
            showFilterPrompt(strFilter+". ");
        }
    }


    public void filterRightRecords(String ageEquator, String ageValue, String genderValue, String gradeLevelValue) {
        //Log.d(AddFilterDialogFragment.TAG, "Filter: age "+ageEquator+" "+ageValue);
        Log.v(TAG, "grade level value = "+gradeLevelValue +"(before filtering)");

        String strFilter = "";
        int filterCount = 0;
        /* filter records*/
        if(!ageValue.trim().contentEquals("")) {
            filterRecordsByAge(recordsRight, ageEquator, ageValue);
        }
        if(!genderValue.trim().contentEquals("N/A")) {
            filterRecordsByGender(recordsRight, genderValue);
        }
        if(!gradeLevelValue.trim().contentEquals("N/A")) {
            filterRecordsByGradeLevel(recordsRight, gradeLevelValue);
        }


        filterAdapter.notifyDataSetChanged();
        addDataSet();
    }

    private void filterRecordsByGender(ArrayList<PatientRecord> records, String genderValue) {
        Log.d(TAG, "Gender Filter: "+genderValue);
        for(int i = 0; i < records.size(); i ++) {
            if(genderValue.contentEquals("Female") && !records.get(i).getGender()) {
                records.remove(i);
                i--;

            } else if(genderValue.contentEquals("Male") && records.get(i).getGender()){
                records.remove(i);
                i--;
            }
        }
//        for(int i = 0; i < records.size(); i++) {
//            Log.d(TAG, "Filtered Gender = "+records.get(i).getGender());
//        }
    }

    private void filterRecordsByAge(ArrayList<PatientRecord> records, String filterEquator, String filterValue) {
        // sort list according to age, ascending order
        Collections.sort(records, new Comparator<PatientRecord>() {
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
        int index;
        int endIndex;
        ArrayList<PatientRecord> tempArray = new ArrayList<>();

//        if(index != -1) {
//            int endIndex = getIndexByProperty(records,value+1, FILTER_EQUALS); // TODO uncomment?
//        int endIndex = getIndexByProperty(records,value+1, FILTER_EQUALS); // For equals
//            if(filterEquator.trim().contains(FILTER_EQUALS) && endIndex == -1) {
//                tempArray.addAll(records.subList(index, records.size()-1));
//            } else {
        Log.d(TAG, "Index operator: "+ filterEquator.trim());
            switch(filterEquator.trim()) {
                case FILTER_EQUALS:
                    index = getIndexByProperty(records, value, FILTER_EQUALS);
                    Log.d(TAG, "= Index: "+ index);
                    endIndex = getIndexByProperty(records,value+1, FILTER_EQUALS);
                    if(index != -1 && endIndex != -1) {
                        tempArray.addAll(records.subList(index, endIndex));
//                        tempArray.addAll(records.subList(index, records.size()-1));
                    }
                    else if(index != -1 && endIndex == -1){
                        tempArray.addAll(records.subList(index, records.size()-1));
                    }
                    else {
                        Toast.makeText(this, "There is no one with that age!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case FILTER_LESS_THAN:
                    // List is in ASCENDING. Search for FIRST INDEX that is >= to ageValue, then use it as endIndex of sublist.
                    endIndex = getIndexByProperty(records, value, FILTER_GREATER_THAN_EQUAL_TO); // For LESS THAN
                    Log.d(TAG, "< endIndex: "+ endIndex);
                    if(records.size() > 0 && records.get(0).getAge() >= value) { // If first item is NOT less than value, no entry
                        Toast.makeText(this, "There is no one with that age!", Toast.LENGTH_SHORT).show();
                    }
                    else if(endIndex != -1) { // means all records are less than value
                        tempArray.addAll(records.subList(0, endIndex)); // "<"
                    }
                    else {
                        tempArray.addAll(records.subList(0, records.size())); // "<"
                    }
                    break;
                case FILTER_GREATER_THAN:
                    index = getIndexByProperty(records, value, FILTER_GREATER_THAN); // For GREATER THAN
                    Log.d(TAG, "> Index: "+ index);
                    // List is in ASCENDING. To get records > ageValue, get index of GREATER THAN ageValue
                    // then get all records from that (index) to end (list size)
                    // until the end.
                    if(index != -1) {
                        tempArray.addAll(records.subList(index, records.size())); // ">"
                    }
                    else {
                        Toast.makeText(this, "There is no one with that age!", Toast.LENGTH_SHORT).show();
                    }

//                    tempArray.addAll(records.subList(index, records.size() - 1)); // ">"
//                        tempArray.addAll(records.subList(index, records.size() - 1)); // ">"
                    break;
            }
//                tempArray.addAll(records.subList(index, endIndex));
//            }

            // TODO uncomment?
//            if(filterEquator.contains(FILTER_EQUALS)) { // "="
//                int endIndex = getIndexByProperty(records,value+1, FILTER_EQUALS);
//                if(endIndex == -1) {
//                    tempArray.addAll(records.subList(index, records.size()-1));
//                } else {
//                    tempArray.addAll(records.subList(index, endIndex));
//                }
//            }
//            if(filterEquator.contains(FILTER_LESS_THAN)) { // "<"
//                tempArray.addAll(records.subList(0, index));
//            } else if(filterEquator.contains(FILTER_GREATER_THAN)) { // ">"
//                tempArray.addAll(records.subList(index, records.size() - 1));
//            }
//        if(tempArray.size() == 0) {
//            Toast.makeText(this, "There is no one with that age!", Toast.LENGTH_SHORT).show();
//        }
        records.clear();
        records.addAll(tempArray);
//            for(int i = 0; i < records.size(); i++) {
//                Log.d(TAG, "Age: "+ records.get(i).getAge());
//            }
//        }
//        else {
            // TODO test only, remove
//            records.clear();
//            records.addAll(tempArray);
//            Toast.makeText(this, "There is no one with that age!", Toast.LENGTH_SHORT).show();
//        }

        // Also filter comparison equivalent
//        filterRecordsByAgeComparison(filterEquator, filterValue);
    }

    /* Get index of the first record with the specified age value*/
    private int getIndexByProperty(ArrayList<PatientRecord> records, int value, String operator) {
        boolean condition;
        Log.d(TAG, "Index: record size "+records.size());
        for(int i = 0; i < records.size(); i++) {
            condition = applyOperatorCondition(records.get(i).getAge(), operator, value);
            Log.d(TAG, "Index: "+records.get(i).getAge()+operator+value+" is "+condition);
            if(condition) {
                return i;
            }
//            if(records.get(i).getAge() == value) {
//                return i;
//            }
        }
        return -1;
    }

    public boolean applyOperatorCondition(int item1, String operator, int item2) {
        Log.d("", item1+operator+item2);
        switch(operator.trim()) {
            case FILTER_GREATER_THAN:
                return (item1 > item2);
            case FILTER_LESS_THAN:
                return (item1 < item2);
            case FILTER_EQUALS:
                return (item1 == item2);
            case FILTER_LESS_THAN_EQUAL_TO:
                return (item1 <= item2);
            case FILTER_GREATER_THAN_EQUAL_TO:
                return (item1 >= item2);
        }
        return false;
    }

    private void filterRecordsByGradeLevel(ArrayList<PatientRecord> records, String gradeLevel) {
        Log.d(TAG, "Grade Level Filter: "+gradeLevel);
        for(int i = 0; i < records.size(); i++) {
            if(records.get(i).getGradeLevel() == null || records.get(i).getGradeLevel().equals("") || !records.get(i).getGradeLevel().contentEquals(gradeLevel)) {
                //Log.d(TAG, "filter record: "+ records.get(i).getGradeLevel());
                records.remove(i);
                i--;
            }
        }
    }

    public void removeFilters(ArrayList<PatientRecord> recordsLeft, ArrayList<PatientRecord> recordsRight, String filter) {
        prepareRecord();
        Log.d(TAG, "Removed Filter: "+filter);
        filterList.remove(filter);
        removeFilter(recordsLeft, filter);
        removeFilter(recordsRight, filter);

    }
    @Override
    public void removeFilter(ArrayList<PatientRecord> records, String filter) {
//        prepareRecord();
//        Log.d(TAG, "Removed Filter: "+filter);
//        filterList.remove(filter);

        if(filterList.size() > 0) {
            String filterLeft = filterList.get(0);
            for(int i = 0; i < filterList.size(); i++) {
                filterLeft = filterList.get(i);
                Log.d(TAG, "Filter Left: "+filterLeft);
                if(filterLeft.contains("age")) {
                    filterRecordsByAge(records, String.valueOf(filterLeft.charAt(4)), filterLeft.substring(6));
                } else if(filterLeft.contains("gender")) {
                    filterRecordsByGender(records, filterLeft.substring(9));
                } else if(filterLeft.contains("grade level")) {
                    filterRecordsByGradeLevel(records, String.valueOf(filterLeft.substring(14)));
                }
            }
        }
        Log.d(TAG, "Displayed records: "+ records.size());

        refreshCharts();
    }

    @Override
    public void onDialogPositiveClick(AddDatasetDialogFragment dialog) {
        clickRemoveFilter();
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
