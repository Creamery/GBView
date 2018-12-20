package seebee.geebeeview.layout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
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
import seebee.geebeeview.model.account.Dataset;
import seebee.geebeeview.model.consultation.School;
import seebee.geebeeview.model.monitoring.PatientRecord;
import seebee.geebeeview.model.monitoring.Record;
import seebee.geebeeview.model.monitoring.ValueCounter;


public class DataVisualizationActivity extends AppCompatActivity
        implements AddFilterDialogFragment.AddFilterDialogListener,
        AddDatasetDialogFragment.AddDatasetDialogListener, FilterAdapter.FilterAdapterListener, TextHolderAdapter.TextListener {
    private static final String TAG = "DataVisualActivity";

    ArrayList<String> datasetList, filterList;
    TextHolderAdapter datasetAdapter;
    FilterAdapter filterAdapter;
    RecyclerView rvDataset, rvFilter;
    Button btnAddDataset, btnAddFilter, btnViewPatientList, btnViewHPIList;
    RelativeLayout graphLayoutLeft, graphLayoutRight; /* space where graph will be set on */
    int schoolID;
    String schoolName, date;
    PieChart pieChartLeft, pieChartRight;
    BarChart barChart;
    ScatterChart scatterChart;
    BubbleChart bubbleChart;
    ArrayList<PatientRecord> recordsLeft, recordsRight;
    String[] xData, possibleAge;
    int[] yDataLeft, yDataRight;
    ArrayList<Dataset> datasets;
    /* attributes for addFilterDialog */
    ArrayList<String> gradeLevels;
    private TextView tvTitle, tvDataset, tvFilter, tvChart, tvData, tvRightChart;
    private Spinner spRecordColumn, spChartType, spRightChart;
    private String recordColumn = "BMI", rightChartContent = "National Profile";
    private String chartType = "Pie Chart";

    private String provinceName, municipalityName;

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

        tvTitle = (TextView) findViewById(R.id.tv_data_visualization_title);
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
        spRecordColumn = (Spinner) findViewById(R.id.sp_record_column);
        spChartType = (Spinner) findViewById(R.id.sp_chart_type);
        spRightChart = (Spinner) findViewById(R.id.sp_right_chart_content);

        /* get fonts from assets */
        Typeface chawpFont = Typeface.createFromAsset(getAssets(), "font/chawp.ttf");
        Typeface chalkFont = Typeface.createFromAsset(getAssets(), "font/DJBChalkItUp.ttf");
        /* set font of text */
        tvTitle.setTypeface(chawpFont);
        tvDataset.setTypeface(chalkFont);
        tvFilter.setTypeface(chalkFont);
        tvChart.setTypeface(chalkFont);
        tvData.setTypeface(chalkFont);
        tvRightChart.setTypeface(chalkFont);
        btnAddDataset.setTypeface(chawpFont);
        btnAddFilter.setTypeface(chawpFont);
        btnViewHPIList.setTypeface(chawpFont);
        btnViewPatientList.setTypeface(chawpFont);

        /* set listener for button view hpi list */
        btnViewHPIList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                AddFilterDialogFragment addFilterDialog = new AddFilterDialogFragment();
                addFilterDialog.setGradeLevels(gradeLevels);
                addFilterDialog.show(getFragmentManager(), AddFilterDialogFragment.TAG);
            }
        });

        btnAddDataset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        ArrayAdapter<String> spChartAdapter = new ArrayAdapter<>(this,
                R.layout.custom_spinner, getResources().getStringArray(R.array.chart_type_array));
        spChartType.setAdapter(spChartAdapter);
        spChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chartType = parent.getItemAtPosition(position).toString();
                ViewGroup.LayoutParams paramsLeft, paramsRight;
                graphLayoutLeft.removeAllViews();
                graphLayoutRight.removeAllViews();
                if(position == 0){
                    graphLayoutLeft.addView(pieChartLeft);
                    graphLayoutRight.addView(pieChartRight);
                    // adjust size of layout
                    paramsLeft = pieChartLeft.getLayoutParams();
                    paramsRight = pieChartRight.getLayoutParams();

                    paramsRight.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    paramsRight.width = ViewGroup.LayoutParams.MATCH_PARENT;
                } else if(position == 1) {
                    /* add bar chart to layout */
                    graphLayoutLeft.addView(barChart);
                    /* adjust the size of the bar chart */
                    paramsLeft = barChart.getLayoutParams();
                } else if (position == 2) {
                    graphLayoutLeft.addView(scatterChart);
                    /* adjust the size of the bar chart */
                    paramsLeft = scatterChart.getLayoutParams();
                } else {
                    graphLayoutLeft.addView(bubbleChart);
                    /* adjust the size of the bar chart */
                    paramsLeft = bubbleChart.getLayoutParams();
                }

                if(!chartType.contains("Pie")) {
                    graphLayoutRight.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 0f));
                } else {
                    graphLayoutRight.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                }

                paramsLeft.height = ViewGroup.LayoutParams.MATCH_PARENT;
                paramsLeft.width = ViewGroup.LayoutParams.MATCH_PARENT;

                // hide control of right chart for scatter and bubble plot
                if(chartType.contains("Scatter") || chartType.contains("Bubble")) {
                    spRightChart.setVisibility(View.GONE);
                    tvRightChart.setVisibility(View.GONE);
                } else {
                    spRightChart.setVisibility(View.VISIBLE);
                    tvRightChart.setVisibility(View.VISIBLE);
                }

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
    }

    private void refreshCharts() {
        /* change the contents of the chart */
        if(pieChartLeft != null) {
            prepareChartData();
            if(chartType.contentEquals("Pie Chart")) {
                pieChartLeft.clear();
            } else if(chartType.contentEquals("Bar Chart")) {
                barChart.clear();
            } else if (chartType.contentEquals("Scatter Chart")) {
                scatterChart.clear();
            } else {
                bubbleChart.clear();
            }
            addDataSet();
        }
    }

    private void createCharts() {
        //graphLayoutLeft.setBackgroundColor(Color.LTGRAY);
        pieChartLeft = createPieChart();
        pieChartRight = createPieChart();

        createBarChart();
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

    private OnChartValueSelectedListener getOnChartValueSelectedListener() {
        return new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                // display msg when value selected
                if(entry == null)
                    return;
                if(chartType.contains("Scatter") && recordColumn.contains("BMI")) {
                    Toast.makeText(DataVisualizationActivity.this,
                            "BMI of a child " + possibleAge[entry.getXIndex()] + " years old = " + entry.getVal(),
                            Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "BMI of a child " + possibleAge[entry.getXIndex()] + " years old = " + entry.getVal());
                } else {
                    Toast.makeText(DataVisualizationActivity.this,
                            xData[entry.getXIndex()] + " = " + entry.getVal() + " children",
                            Toast.LENGTH_SHORT).show();
                    Log.v(TAG, xData[entry.getXIndex()] + " = " + entry.getVal() + " children");
                }
/*                if(!recordColumn.contentEquals("BMI")) {
                    Intent intent = new Intent(getBaseContext(), PatientListActivity.class);
                    intent.putExtra(School.C_SCHOOL_ID, schoolID);
                    intent.putExtra(School.C_SCHOOLNAME, schoolName);
                    intent.putExtra(Record.C_DATE_CREATED, date);
                    intent.putExtra("column", ValueCounter.convertRecordColumn(recordColumn));
                    intent.putExtra("value", xData[entry.getXIndex()]);
                    startActivity(intent);
                } */
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
        l.setTextSize(R.dimen.context_text_size);
        l.setTextColor(Color.WHITE);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

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

    private void addDataSet() {
        if(chartType.contentEquals("Pie Chart")) {
            preparePieChartData(pieChartLeft, yDataLeft);
            preparePieChartData(pieChartRight, yDataRight);
        } else if(chartType.contentEquals("Bar Chart")){
            prepareBarChartData();
        } else if(chartType.contentEquals("Scatter Chart")) {
            prepareScatterChartData();
        } else {
            prepareBubbleChartData();
        }
    }

    private ArrayList<Entry> createEntries(int[] yData) {
        ArrayList<Entry> yVals1 = new ArrayList<>();

        for(int i = 0; i < yData.length; i++) {
            yVals1.add(new Entry(yData[i], i));
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
            bubbleData = new BubbleData(xData, bubbleDataSetList);
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

            bubbleData = new BubbleData(possibleAge, bubbleDataSetList);
            // customize legend
            Legend legend = bubbleChart.getLegend();
            int color[] = new int[xData.length];
            for(int k = 0; k < xData.length; k++) {
                color[k] = colors.get(k);
            }
            legend.setCustom(color, xData);
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
            scatterData = new ScatterData(xData, scatterDataSetList);
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
                        yVals1.add(new Entry(patientRecord.getBMIResult(), j));
                        scatterDataSet = new ScatterDataSet(yVals1, xData[category]);
                        scatterDataSet.setColor(colors.get(category));
                        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
                        scatterDataSetList.add(scatterDataSet);
                    }
                }
            }
            Log.v(TAG, "Scatter List Size: "+scatterDataSetList.size());
            scatterData = new ScatterData(possibleAge, scatterDataSetList);
            Legend legend = scatterChart.getLegend();
            int color[] = new int[xData.length];
            for(int k = 0; k < xData.length; k++) {
                color[k] = colors.get(k);
            }
            legend.setCustom(color, xData);
        }

        scatterChart.setData(scatterData);

        scatterChart.getAxisLeft().setEnabled(false);
        customizeChart(scatterChart, scatterChart.getAxisRight());

    }

    private void prepareBarChartData() {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for(int i = 0; i < yDataLeft.length; i++) {
            yVals1.add(new BarEntry(yDataLeft[i], i));
        }

        ArrayList<BarEntry> yVals2 = new ArrayList<>();
        for(int i = 0; i < yDataRight.length; i++) {
            yVals2.add(new BarEntry(yDataRight[i], i));
        }

        /* create bar chart dataset */
        BarDataSet barDataSet = new BarDataSet(yVals1, "Left Chart");
        BarDataSet barDataSet2 = new BarDataSet(yVals2, rightChartContent);
        ArrayList<Integer> colors = getColorPalette();
        barDataSet.setColor(colors.get(0));
        barDataSet2.setColor(colors.get(1));
        /*BarDataSet barDataSet1 = new BarDataSet(yVals1, "");
        barDataSet.setColor(colors.get(0)); */
        List<IBarDataSet> barDataSetList = new ArrayList<>();
        barDataSetList.add(barDataSet);
        barDataSetList.add(barDataSet2);
        BarData barData = new BarData(xData, barDataSetList);
        //BarData barData = new BarData(xData, barDataSet);
        barChart.setData(barData);
        barChart.getAxisLeft().setEnabled(false);
        customizeChart(barChart, barChart.getAxisRight());
    }

    private void preparePieChartData(PieChart pieChart, int[] yData) {
        ArrayList<Entry> yVals1 = createEntries(yData);

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);
        /* add colors to chart */
        dataSet.setColors(getColorPalette());

        // instantiate pie data object now
        PieData data = new PieData(xData, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.GRAY);

        pieChart.setData(data);

        pieChart.setDescription(recordColumn);
        pieChart.setDescriptionTextSize(20f);
        pieChart.setDescriptionColor(Color.WHITE);
//        Description description = new Description();
//        description.setText(recordColumn);
//        description.setTextSize(20f);
//        description.setTextColor(Color.WHITE);
//        pieChart.setDescription(description);
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
        chart.setDescription(recordColumn);
        chart.setDescriptionTextSize(20f);
        chart.setDescriptionColor(Color.WHITE);
        chart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());

        ChartData chartData = chart.getData();
        List<IDataSet> iDataSetList = chartData.getDataSets();
        /* customize value lable for each dataset */
        for(int i = 0; i < iDataSetList.size(); i++) {
            iDataSetList.get(i).setValueTextSize(20f);
            iDataSetList.get(i).setValueTextColor(Color.WHITE);
        }
        /* customize axis labels */
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(20f);
        xAxis.setTextColor(Color.WHITE);
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, AxisBase axisBase) {
//                Log.v(TAG, "Float Value: "+v);
//                return xData[(int) v];
//            }
//        });
        yAxis.setTextSize(20f);
        yAxis.setTextColor(Color.WHITE);
        /* customize legend */
        Legend legend = chart.getLegend();
        legend.setTextSize(20f);
        legend.setTextColor(Color.WHITE);
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
