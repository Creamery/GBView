package seebee.geebeeview.layout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import seebee.geebeeview.R;
import seebee.geebeeview.containers.ColorThemes;
import seebee.geebeeview.containers.StringConstants;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.graphs.PatientChartIAxisFormatter;
import seebee.geebeeview.model.consultation.Patient;
import seebee.geebeeview.model.monitoring.AgeCalculator;
import seebee.geebeeview.model.monitoring.BMICalculator;
import seebee.geebeeview.model.monitoring.IdealValue;
import seebee.geebeeview.model.monitoring.LineChartValueFormatter;
import seebee.geebeeview.model.monitoring.Record;
import seebee.geebeeview.containers.General;
import seebee.geebeeview.sidebar.PatientSidebar;

public class ViewPatientActivity extends AppCompatActivity {

    private static final String TAG = "ViewPatientActivity";
    private float LEGEND_TEXT_SIZE = 16f; // TODO make universal (see DataVisualization.java)
    private float AXIS_TEXT_SIZE = 14f;
    private ConstraintLayout contRecordDate;

    private TextView tvName, tvBirthday, tvDominantHand, tvAge, tvGradeLevel, tvBMI, tvPatientRemark, tvMedicalRecordTitle, tvRecordDateTitle;
    private TextView tvData, tvDate, tvHeight, tvWeight, tvVisualLeft, tvVisualRight, tvColorVision, tvHearingLeft,
            tvHearingRight, tvGrossMotor, tvFineMotorD, tvFineMotorND, tvFineMotorHold, tvRecordRemark;
    private ImageView ivPatient, ivGender;

    // ImageViews for the colors on the tabs
    private ImageView
            ivColorBMI, ivColorHeight, ivColorWeight,
            ivColorVAL, ivColorVAR, ivColorColor,
            ivColorHearR, ivColorHearL,
            ivColorGross,
            ivColorFineD, ivColorFineND, ivColorFineH;

    // Heart - BMI, Height, Weight ; Eye - VA Left, VA Right, Color Vision ; Ear - Hearing Left, Hearing Right
    // Body - Gross Motor ; Hand - Fine Dominant, Fine Non-dominant, Fine Hold
    private ConstraintLayout contHeart, contEye, contEar, contBody, contHand;

    private Button btnViewHPI, btnViewImmunization;
    private Spinner spRecordDate;
    private ConstraintLayout contAboutTitle0, contAboutTitle1, contAboutTitle2, contAboutTitle3, contAboutTitle4;


    private PatientSidebar sidebarManager;
    private ImageView ivBMIColor, ivBMIClickable;


    private int patientID;
    private Patient patient;
    private ArrayList<Record> patientRecords, averageRecords;
    private IdealValue idealValue;

    private RelativeLayout graphLayout1, graphLayout2, graphLayout3;
    private LineChart lineChart1, lineChart2, lineChart3;
//    private RadarChart radarChart;
    private Spinner spRecordColumn;
    private String recordColumn = "BMI";
    private String chartType = "Line Chart";
    private boolean bmiIsVisible = false;

    private View viewMedicalRecord;
    private boolean isMedicalRecord;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        adjustDetailFontSize();

//        tvAboutTitle1 = (TextView) findViewById(R.id.tv_about_title1);
//        tvAboutTitle2 = (TextView) findViewById(R.id.tv_about_title2);
//        tvAboutTitle3 = (TextView) findViewById(R.id.tv_about_title3);
//        tvAboutTitle4 = (TextView) findViewById(R.id.tv_about_title4);

        contAboutTitle0 = (ConstraintLayout) findViewById(R.id.cont_about_item0_image);
        contAboutTitle1 = (ConstraintLayout) findViewById(R.id.cont_about_item1_image);
        contAboutTitle2 = (ConstraintLayout) findViewById(R.id.cont_about_item2_image);
        contAboutTitle3 = (ConstraintLayout) findViewById(R.id.cont_about_item3_image);
        contAboutTitle4 = (ConstraintLayout) findViewById(R.id.cont_about_item4_image);



        tvMedicalRecordTitle = (TextView) findViewById(R.id.tv_scrollbar_title);
        tvRecordDateTitle = (TextView) findViewById(R.id.tv_vp_date);

        TextView titleSizeReference = tvMedicalRecordTitle;
        TextView sizeReference = tvRecordDateTitle;


        setTextViewChildrenHeightTo(contAboutTitle0, titleSizeReference);
        setTextViewChildrenHeightTo(contAboutTitle1, titleSizeReference);
        setTextViewChildrenHeightTo(contAboutTitle2, titleSizeReference);
        setTextViewChildrenHeightTo(contAboutTitle3, titleSizeReference);
        setTextViewChildrenHeightTo(contAboutTitle4, titleSizeReference);
    }

    public void adjustDetailFontSize() {
        if(tvWeight.getTextSize() > tvHeight.getTextSize()) {
            tvWeight.setTextSize(tvHeight.getTextSize());
        }
        else {
            tvHeight.setTextSize(tvWeight.getTextSize());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient);

        // lock orientation of activity to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /* get patient ID */
        patientID = getIntent().getIntExtra(Patient.C_PATIENT_ID, 0);
        /* connect views in layout here */
        ivPatient = (ImageView) findViewById(R.id.iv_patient);
        ivGender = (ImageView) findViewById(R.id.iv_gender);
        tvName = (TextView) findViewById(R.id.tv_name_r);
        tvBirthday = (TextView) findViewById(R.id.tv_birthday);
        tvDominantHand = (TextView) findViewById(R.id.tv_dominant_hand);
        tvAge = (TextView) findViewById(R.id.tv_patient_age);
        tvGradeLevel = (TextView) findViewById(R.id.tv_grade_level);
        tvPatientRemark = (TextView) findViewById(R.id.tv_patient_remark);
        /* connect views in layout to show record of last check up */
        tvData = (TextView) findViewById(R.id.tv_vp_data);
        tvDate = (TextView) findViewById(R.id.tv_vp_date);
        tvBMI = (TextView) findViewById(R.id.tv_bmi);
        tvHeight = (TextView) findViewById(R.id.tv_height);
        tvWeight = (TextView) findViewById(R.id.tv_weight);
        tvVisualLeft = (TextView) findViewById(R.id.tv_visual_acuity_left);
        tvVisualRight = (TextView) findViewById(R.id.tv_visual_acuity_right);
        tvColorVision = (TextView) findViewById(R.id.tv_color_vision);
        tvHearingLeft = (TextView) findViewById(R.id.tv_hearing_left);
        tvHearingRight = (TextView) findViewById(R.id.tv_hearing_right);
        tvGrossMotor = (TextView) findViewById(R.id.tv_gross_motor);
        tvFineMotorD = (TextView) findViewById(R.id.tv_fine_motor_d);
        tvFineMotorND = (TextView) findViewById(R.id.tv_fine_motor_nd);
        tvFineMotorHold = (TextView) findViewById(R.id.tv_fine_motor_hold);
        tvRecordRemark = (TextView) findViewById(R.id.tv_record_remark);
        /* connect graph container to here */
        graphLayout1 = (RelativeLayout) findViewById(R.id.patient_chart_container1);
        graphLayout2 = (RelativeLayout) findViewById(R.id.patient_chart_container2);
        graphLayout3 = (RelativeLayout) findViewById(R.id.patient_chart_container3);
        /* connect spinner here */
        spRecordColumn = (Spinner) findViewById(R.id.sp_vp_record_column);
        spRecordDate = (Spinner) findViewById(R.id.sp_record_date);
        contRecordDate = (ConstraintLayout) findViewById(R.id.cont_record_date);
        /* connect buttons */
        btnViewHPI = (Button) findViewById(R.id.btn_hpi_sidebar); // TODO
        btnViewImmunization = (Button) findViewById(R.id.btn_immunization_sidebar); // TODO


        ivBMIClickable = (ImageView) findViewById(R.id.img_patient_bmi);
        ivBMIColor = (ImageView) findViewById(R.id.img_patient_bmi_color);

        viewMedicalRecord = (View) findViewById(R.id.sectionMedicalRecord);

        setupTabFunctionality();

        /* set button so that it will go to the HPIListActivity */
        btnViewHPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close sidebar if extended
                if(sidebarManager.isSidebarOpen()) {
                    sidebarManager.getBtnOpenSidebar().performClick();
                }

                Intent intent = new Intent(v.getContext(), ViewHPIActivity.class);
                intent.putExtra(Patient.C_PATIENT_ID, patient.getPatientID());
                startActivity(intent);

            }
        });
        btnViewImmunization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasImmunization = false;
                /* check if patient has even 1 immunization record */
                for(int i = 0; i < patientRecords.size(); i++) {
                    if(patientRecords.get(i).getVaccination() != null && patientRecords.get(i).getVaccination().length > 0) {

                        //Log.println(Log.ASSERT, TAG, patientRecords.get(i).getVaccination());
                        hasImmunization = true;
                    }
                }
                if(hasImmunization) {
                    Intent intent = new Intent(v.getContext(), ViewImmunizationActivity.class);
                    intent.putExtra(Patient.C_PATIENT_ID, patient.getPatientID());
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewPatientActivity.this, R.string.no_immunizaiton, Toast.LENGTH_SHORT).show();
                }

                // Close sidebar if extended
                if(sidebarManager.isSidebarOpen()) {
                    sidebarManager.getBtnOpenSidebar().performClick();
                }
            }
        });


        patient = null;
        getPatientData();
        /* fill up the patient data */
        if(patient != null) {
            ivPatient.setImageDrawable(getDefaultImage(patient.getGender())); // TODO added default image
            ivGender.setImageDrawable(getGenderImage(patient.getGender()));
            tvName.setText(patient.getLastName()+", "+patient.getFirstName());

            tvName.setTextColor(General.getColorByGender(this, patient.getGender()));
            tvBirthday.setText(patient.getBirthday());
            tvDominantHand.setText(patient.getHandednessString());
            tvPatientRemark.setText(patient.getRemarksString());

//            Log.d("ABOUTLOG", "Birthday: "+patient.getBirthday());
//            Log.d("ABOUTLOG", "Dominant: "+patient.getHandednessString());
//            Log.d("ABOUTLOG", "Remark: "+patient.getRemarksString());

            // todo: remove after testing
//            MediaPlayer ring = MediaPlayer.create(this, R.raw.ring);
//            ring.setLooping(true);
//            ring.start();
            //convertBlobToFile(patient.getRemarksAudio());
        }

        getPatientRecords();
        getAverageRecords();

        /* set up spinner selector */
        ArrayAdapter<String> spRecordAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner,
                getResources().getStringArray(R.array.record_column_array_num_tabs));
//                getResources().getStringArray(R.array.record_column_array_num));
        spRecordColumn.setAdapter(spRecordAdapter);
        spRecordColumn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recordColumn = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(),
                        "Displayed Data: " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                /* change the contents of the chart */
                prepareLineChartData(position);
//                if(lineChart != null) {
//                    lineChart.clear();
//                    prepareLineChartData();
//                    lineChart.setDescription(recordColumn);
                    // TODO Edited (removed)
//                    Description desc = new Description();
//                    desc.setText(recordColumn);
//                    lineChart.setDescription(desc);

//                } else {
//                    radarChart.clear();
//                    prepareRadarChartData();
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //addIdealValues = false;
            }
        });
        prepareRecordDateSpinner();

        /* show details in relation to latest check up record*/
//        if(patientRecords.size() > 0) {
//            spRecordDate.setSelection(patientRecords.size()-1);
//            Record lastRecord = patientRecords.get(patientRecords.size()-1);
//            tvRecordDate.setText(lastRecord.getDateCreated());
//        }

        createCharts();
//        Log.e("DEBUG", "Entering addChartToView");
        addChartToView();
//        Log.e("DEBUG", "Entering prepareLineChart");

        prepareLineCharts();
//        Log.e("DEBUG", "Entering prepareLineChartData");
        prepareLineChartData(spRecordColumn.getSelectedItemPosition()); // TODO


        Log.e("DEBUG", "Record List");

        // Record list
        ImageView ivRecordButton = (ImageView) findViewById(R.id.iv_record_date_arrow);
        ivRecordButton.setClickable(true);
        ivRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spRecordDate.performClick();
            }
        });
        setupSidebarFunctionality();
        ivBMIClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bmiIsVisible = !bmiIsVisible;
                tvBMI.setVisibility(General.getVisibility(bmiIsVisible));
            }
        });

        Log.e("DEBUG", "Exiting OnCreate");
    }

    private void clearAllGraphs() {
        if(lineChart1 != null) {
            lineChart1.clear();
        }
        if(lineChart2 != null) {
            lineChart2.clear();
        }
        if(lineChart2 != null) {
            lineChart2.clear();
        }
    }

    public void setupTabColors(Record patientRecord, String bmiText) {
        int age = patient.getAge(patientRecord.getDateCreated());
        IdealValue idealRecordValues;
//        ivColorBMI.setBackgroundColor(ColorThemes.getColor(StringConstants.COL_BMI, bmiText));
        String recordType;

        recordType = StringConstants.COL_BMI;
        ivColorBMI.setBackgroundColor(ColorThemes.getColor(recordType, bmiText));

        if(age >= 5 && age <= 19) {
            recordType = StringConstants.COL_HEIGHT;
            idealRecordValues = getIdealValues(recordType, age);
            if(idealRecordValues !=  null) {
                float height = idealRecordValues.getMedian();
                // If patient height is higher/equal to median, green ; else, red
                if(patientRecord.getHeight() >= height) {
                    ivColorHeight.setBackgroundColor(ColorThemes.cPass);
                }
                else {
                    ivColorHeight.setBackgroundColor(ColorThemes.cFail);
                }
            }

            recordType = StringConstants.COL_WEIGHT;
            idealRecordValues = getIdealValues(recordType, age);
            if(idealRecordValues !=  null) {
                float weight = idealRecordValues.getMedian();
                // If patient weight is higher/equal to median, green ; else, red
                if(patientRecord.getWeight() >= weight) {
                    ivColorWeight.setBackgroundColor(ColorThemes.cPass);
                }
                else {
                    ivColorWeight.setBackgroundColor(ColorThemes.cFail);
                }
            }
        }

        recordType = StringConstants.COL_VA_LEFT;
        ivColorVAL.setBackgroundColor(ColorThemes.getColor(recordType, patientRecord.getVisualAcuityLeft()));
        ivColorVAR.setBackgroundColor(ColorThemes.getColor(recordType, patientRecord.getVisualAcuityRight()));

        recordType = StringConstants.COL_COLOR_VISION;
        ivColorColor.setBackgroundColor(ColorThemes.getColor(recordType, patientRecord.getColorVision()));
//        Log.e("TAB", recordType+" Color Vision "+patientRecord.getColorVision());

        recordType = StringConstants.COL_HEAR_LEFT;
        ivColorHearL.setBackgroundColor(ColorThemes.getColor(recordType, patientRecord.getHearingLeft()));
        ivColorHearR.setBackgroundColor(ColorThemes.getColor(recordType, patientRecord.getHearingRight()));
//        Log.e("TAB", "Hear Left "+patientRecord.getHearingLeft());
//        Log.e("TAB", "Hear Right "+patientRecord.getHearingRight());

        recordType = StringConstants.COL_GROSS_MOTOR;
        ivColorGross.setBackgroundColor(ColorThemes.getColor(recordType, patientRecord.getGrossMotorString()));

//        Log.e("TAB", "Gross Motor "+patientRecord.getGrossMotorString());

        recordType = StringConstants.COL_FINE_DOMINANT;
        ivColorFineD.setBackgroundColor(ColorThemes.getColor(recordType, patientRecord.getFineMotorString(patientRecord.getFineMotorDominant())));
        ivColorFineND.setBackgroundColor(ColorThemes.getColor(recordType, patientRecord.getFineMotorString(patientRecord.getFineMotorNDominant())));
        ivColorFineH.setBackgroundColor(ColorThemes.getColor(recordType, patientRecord.getFineMotorString(patientRecord.getFineMotorHold())));

//        Log.e("TAB", "Fine D "+patientRecord.getFineMotorString(patientRecord.getFineMotorDominant()));
//        Log.e("TAB", "Fine ND "+patientRecord.getFineMotorString(patientRecord.getFineMotorNDominant()));
//        Log.e("TAB", "Fine H "+patientRecord.getFineMotorString(patientRecord.getFineMotorHold()));
    }

    public boolean hasIdealValue(String recordColumn) {
            if(recordColumn.contains(StringConstants.COL_WEIGHT) || recordColumn.contains(StringConstants.COL_HEIGHT) || recordColumn.contains(StringConstants.COL_BMI)) {
                return true;
            }
            return false;
    }

    public void setupTabFunctionality() {

        ivColorBMI = findViewById(R.id.iv_item1_color);
        ivColorHeight = findViewById(R.id.iv_item1_color2);
        ivColorWeight = findViewById(R.id.iv_item1_color3);

        ivColorVAL = findViewById(R.id.iv_item2_color);
        ivColorVAR = findViewById(R.id.iv_item2_color2);
        ivColorColor = findViewById(R.id.iv_item2_color3);

        ivColorHearL = findViewById(R.id.iv_item3_color);
        ivColorHearR = findViewById(R.id.iv_item3_color2);

        ivColorGross = findViewById(R.id.iv_item4_color);

        ivColorFineD = findViewById(R.id.iv_item5_color);
        ivColorFineND = findViewById(R.id.iv_item5_color2);
        ivColorFineH = findViewById(R.id.iv_item5_color3);

        contHeart = findViewById(R.id.constraintLayout23);
        contEye = findViewById(R.id.constraintLayout24);
        contEar = findViewById(R.id.constraintLayout25);
        contBody = findViewById(R.id.constraintLayout26);
        contHand = findViewById(R.id.constraintLayout27);

        contHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spRecordColumn.setSelection(StringConstants.INDEX_HEART);
            }
        });
        contEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spRecordColumn.setSelection(StringConstants.INDEX_EYE);
            }
        });
        contEar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spRecordColumn.setSelection(StringConstants.INDEX_EAR);
            }
        });
        contBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spRecordColumn.setSelection(StringConstants.INDEX_BODY);
            }
        });
        contHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spRecordColumn.setSelection(StringConstants.INDEX_HAND);
            }
        });
    }
    public void setupSidebarFunctionality () {
        // TODO About, Immun, HPI functionality
        sidebarManager = new PatientSidebar(
                (Button) findViewById(R.id.btn_show_sidebar_icons),
                (Button) findViewById(R.id.btn_sidebar_open_extend),
                (Button) findViewById(R.id.btn_about_sidebar),
                (Button) findViewById(R.id.btn_hpi_sidebar),
                (Button) findViewById(R.id.btn_immunization_sidebar),
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

        // SETUP Medical Record and Personal Information
        isMedicalRecord = true;
        this.sidebarManager.getBtnSidebarAbout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close sidebar if extended
                if(sidebarManager.isSidebarOpen()) {
                    sidebarManager.getBtnOpenSidebar().performClick();
                }

//                isMedicalRecord = !isMedicalRecord;
//                viewMedicalRecord.setVisibility(General.getVisibility(isMedicalRecord));
//                viewPersonalInfo.setVisibility(General.getVisibility(!isMedicalRecord));
            }
        });

        // Initially show medical record and hide personal information
        viewMedicalRecord.setVisibility(General.getVisibility(isMedicalRecord));

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



    private Drawable getDefaultImage(int gender) {
        if(gender == 0) {
            return getResources().getDrawable(R.drawable.no_photo_male);
        }
        else {
            return getResources().getDrawable(R.drawable.no_photo_female);
        }
    }

    private Drawable getGenderImage(int gender) {
        if(gender == 0) {
            return getResources().getDrawable(R.drawable.img_gender_circle_fill_male);
        }
        else {
            return getResources().getDrawable(R.drawable.img_gender_circle_fill_female);
        }
    }
    // Used to resize Medical Record entries
    public void setTextViewChildrenHeightTo(ConstraintLayout vg, TextView reference) {
        try {
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                // Recursive call
                if(child instanceof TextView) {
                    ((TextView) child).setMinHeight(reference.getLayoutParams().height);
                    ((TextView) child).getLayoutParams().height = reference.getLayoutParams().height;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Used to resize Medical Record entries by FONT
    public void setTextViewChildrenFontHeightTo(ConstraintLayout vg, TextView reference) {
        try {
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                // Recursive call
                if(child instanceof TextView) {
                    ((TextView) child).setTextSize(reference.getTextSize());
//                    ((TextView) child).setMinHeight(reference.getHeight());
//                    ((TextView) child).getLayoutParams().height = reference.getHeight();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Used to resize Medical Record entries
    public void setTextViewChildrenHeightTo(LinearLayout vg, TextView reference) {
        try {
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                // Recursive call
                if(child instanceof TextView) {
                    ((TextView) child).setMinHeight(reference.getHeight());
                    ((TextView) child).getLayoutParams().height = reference.getHeight();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setTextViewHeightTo(TextView vg, TextView reference) {
        vg.setMinHeight(reference.getHeight());
    }



    private void displayRecord(Record record) {
        String recordDate = record.getDateCreated();

        boolean isGirl = true;
        if(patient.getGender() != 1) {
            isGirl = false;
        }
        String bmi = BMICalculator.getBMIResultString(isGirl,
                AgeCalculator.calculateAge(patient.getBirthday(), recordDate),
                BMICalculator.computeBMIMetric(Double.valueOf(record.getHeight()).intValue(),
                        Double.valueOf(record.getWeight()).intValue()));
        if(record.getPatientPicture() != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(record.getPatientPicture(), 0, record.getPatientPicture().length);

            try {
                //float dividend = ivPatient.getWidth() / bmp.getWidth();
                ivPatient.setImageBitmap(Bitmap.createScaledBitmap(bmp, ivPatient.getWidth(),
                        ivPatient.getWidth(), false));
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
        }

        tvBMI.setText(getBMIText(bmi));
        ivBMIColor.setImageDrawable(getBMIColor(bmi));
        tvHeight.setText(record.getHeight()+" cm");
        tvWeight.setText(record.getWeight()+" kg");

        // TODO added


        tvVisualLeft.setText(record.getVisualAcuityLeft());
        tvVisualRight.setText(record.getVisualAcuityRight());
        tvColorVision.setText(record.getColorVision());
        tvHearingLeft.setText(record.getHearingLeft());
        tvHearingRight.setText(record.getHearingRight());
        tvGrossMotor.setText(record.getGrossMotorString());
        tvFineMotorD.setText(record.getFineMotorString(record.getFineMotorDominant()));
        tvFineMotorND.setText(record.getFineMotorString(record.getFineMotorNDominant()));
        tvFineMotorHold.setText(record.getFineMotorString(record.getFineMotorHold()));
        tvRecordRemark.setText(record.getRemarksString());
        /* display age and grade level according to recordDate */
        tvAge.setText(patient.getAge(recordDate)+"");
        tvGradeLevel.setText(record.getGradeLevel());

        setupTabColors(record, getBMIText(bmi));
//        Log.d("ABOUTLOG", "GradeLevel: "+record.getGradeLevel());
    }

    // Function for BMI text setting (complete if needed)
    private String getBMIText(String text) {

//        if(text.contains("N/A")) {
//            text = "";
//        }
        return text;
    }
    private Drawable getBMIColor(String text) {
       if(text.toLowerCase().contains("under")) {
            return getResources().getDrawable(R.drawable.img_heart_overlay_underweight);
        }
        else if(text.toLowerCase().contains("normal")) {
            return getResources().getDrawable(R.drawable.img_heart_overlay_normal);
        }
        else if(text.toLowerCase().contains("over")) {
            return getResources().getDrawable(R.drawable.img_heart_overlay_overweight);
        }
        else if(text.toLowerCase().contains("obese")) {
            return getResources().getDrawable(R.drawable.img_heart_overlay_obese);
        }
        return getResources().getDrawable(R.drawable.img_heart_overlay_na);
    }


//    private void prepareRadarChartData() {
//        RadarData radarData = new RadarData();
//        ArrayList<Entry> entries;
//        Record record = patientRecords.get(0);
//        for(int i = 0; i < 5; i++) {
//            //entries.add(new Entry());
//        }
//    }


    private void prepareLineChartData(int selectedItem) {
        clearAllGraphs();
        switch(selectedItem) {

            case StringConstants.INDEX_EYE:
                prepareLineChartData(lineChart1, StringConstants.COL_VA_LEFT);
                prepareLineChartData(lineChart2, StringConstants.COL_VA_RIGHT);
                prepareLineChartData(lineChart3, StringConstants.COL_COLOR_VISION);
                break;
            case StringConstants.INDEX_EAR:
                prepareLineChartData(lineChart1, StringConstants.COL_HEAR_LEFT);
                prepareLineChartData(lineChart2, StringConstants.COL_HEAR_RIGHT);
                break;
            case StringConstants.INDEX_BODY:
                prepareLineChartData(lineChart1, StringConstants.COL_GROSS_MOTOR);
                break;
            case StringConstants.INDEX_HAND:
                prepareLineChartData(lineChart1, StringConstants.COL_FINE_DOMINANT);
                prepareLineChartData(lineChart2, StringConstants.COL_FINE_NON_DOMINANT);
                prepareLineChartData(lineChart3, StringConstants.COL_FINE_HOLD);
                break;

            case StringConstants.INDEX_HEART:
                prepareLineChartData(lineChart1, StringConstants.COL_BMI);
                prepareLineChartData(lineChart2, StringConstants.COL_HEIGHT);
                prepareLineChartData(lineChart3, StringConstants.COL_WEIGHT);
            default:
        }
    }

    private void prepareLineChartData(LineChart lineChart, String recordValue) {
        LineData lineData = new LineData();
        lineData.setValueTextColor(Color.WHITE);

        Description desc = new Description();
        desc.setText(recordValue);
        lineChart.setDescription(desc);

        // add data to line chart
        lineChart.setData(lineData);

        /* dataset containing values from patient, index 0 */
        LineDataSet patientDataset = createLineDataSet(0);
        lineData.addDataSet(patientDataset);


        /* add dataset containing average record values from patients with same age */
        lineData.addDataSet(createLineDataSet(1));

        /* add ideal values only if record */
        boolean addIdealValues = recordValue.contains("Height") || recordValue.contains("Weight") || recordValue.contains("BMI");

        if(addIdealValues) {
            LineDataSet n2Dataset, n1Dataset, medianDataset, p1Dataset, p2Dataset;
            /* dataset containing values from 2SD */
            p2Dataset = createLineDataSet(7);
            lineData.addDataSet(p2Dataset);
            /* dataset containing values from 1SD */
            p1Dataset = createLineDataSet(6);
            lineData.addDataSet(p1Dataset);
            /* dataset containing values from median */
            medianDataset = createLineDataSet(5);
            lineData.addDataSet(medianDataset);
            /* dataset containing values from -1SD, index 3 */
            n1Dataset = createLineDataSet(4);
            lineData.addDataSet(n1Dataset);
            /* dataset containing values from -2SD, index 2 */
            n2Dataset = createLineDataSet(3);
            lineData.addDataSet(n2Dataset);
            if(recordValue.contentEquals("BMI")) {
                /* dataset containing values from -3SD, index 7 */
                LineDataSet n3Dataset = createLineDataSet(2);
                lineData.addDataSet(n3Dataset);
            }
        }

        Record record;
        float yVal; int age;
        ArrayList<Entry> patientEntries = new ArrayList<>();
        ArrayList<Entry> n3Entries = new ArrayList<>();

        for(int i = 0; i < patientRecords.size(); i++) {
            record = patientRecords.get(i);

            yVal = getColumnValue(record);
            //Log.v(TAG, recordValue+": "+yVal);
            /* add patient data to patient entry, index 0 */
//            lineData.addEntry(new Entry(yVal, i), 0); TODO edited
            lineData.addEntry(new Entry(i, yVal), 0);

            /* add average record values of patients of the same age, index 1 */
            yVal = getColumnValue(averageRecords.get(i));
            lineData.addEntry(new Entry(i, yVal), 1);
//            lineData.addEntry(new Entry(yVal, i), 1); TODO edited


            /* set xValue to age of patient when record is created */
            age = patient.getAge(record.getDateCreated());
//            lineData.addXValue(Integer.toString(age)); // TODO deprecated, FIND replacement



            /* addIdealValues if column is either height, weight, or BMI */
            if(addIdealValues && age >= 5 && age <= 19) {
                getIdealValues(age);
                if(idealValue != null) {
                    idealValue.printIdealValue();
                    Log.e("IDEAL", idealValue.getP2SD()+" "+idealValue.getYear());

                }
//                //Log.v(TAG, recordValue+"(-3SD): "+idealValue.getN3SD()); TODO edited
//                /* add -2SD from ideal value data to patient entry, index 3 */
//                lineData.addEntry(new Entry(idealValue.getP2SD(), i), 2);
//                /* add -1SD from ideal value data to patient entry, index 4 */
//                lineData.addEntry(new Entry(idealValue.getP1SD(), i), 3);
//                /* add median of ideal value data to patient entry, index 5 */
//                lineData.addEntry(new Entry(idealValue.getMedian(), i), 4);
//                /* add 1SD from ideal value data to patient entry, index 6 */
//                lineData.addEntry(new Entry(idealValue.getN1SD(), i), 5);
//                /* add 2SD from ideal value data to patient entry, index 7 */
//                lineData.addEntry(new Entry(idealValue.getN2SD(), i), 6);
//                /* add -3SD from ideal value data to patient entry, index 2 */
//                lineData.addEntry(new Entry(idealValue.getN3SD(), i), 7);

                if(idealValue != null) {
                    /* add -2SD from ideal value data to patient entry, index 3 */
                    lineData.addEntry(new Entry(i, idealValue.getP2SD()), 2);
                    /* add -1SD from ideal value data to patient entry, index 4 */
                    lineData.addEntry(new Entry(i, idealValue.getP1SD()), 3);
                    /* add median of ideal value data to patient entry, index 5 */
                    lineData.addEntry(new Entry(i, idealValue.getMedian()), 4);
                    /* add 1SD from ideal value data to patient entry, index 6 */
                    lineData.addEntry(new Entry(i, idealValue.getN1SD()), 5);
                    /* add 2SD from ideal value data to patient entry, index 7 */
                    lineData.addEntry(new Entry(i, idealValue.getN2SD()), 6);
                    /* add -3SD from ideal value data to patient entry, index 2 */
                    lineData.addEntry(new Entry(i, idealValue.getN3SD()), 7);
                }
                else {
                    Log.e("IDEAL", "idealValue is null");
                }
            }
        }

        setLineChartValueFormatter(lineChart, lineData);
        lineChart.getLineData().setDrawValues(false);
        // notify chart data has changed
        lineChart.notifyDataSetChanged();

    }

    private float getColumnValue(Record record) {
        float x;
        switch (recordColumn) {
            default:
            case "Height (in cm)": x = Double.valueOf(record.getHeight()).floatValue();
                break;
            case "Weight (in kg)": x = Double.valueOf(record.getWeight()).floatValue();
                break;
            case "BMI": x = BMICalculator.computeBMIMetric(
                    Double.valueOf(record.getHeight()).intValue(),
                    Double.valueOf(record.getWeight()).intValue());
                break;
            case "Visual Acuity Left":
                x = LineChartValueFormatter.ConvertVisualAcuity(record.getVisualAcuityLeft());
                break;
            case "Visual Acuity Right":
                x = LineChartValueFormatter.ConvertVisualAcuity(record.getVisualAcuityRight());
                break;
            case "Color Vision":
                x = LineChartValueFormatter.ConvertColorVision(record.getColorVision());
                break;
            case "Hearing Left":
                x = LineChartValueFormatter.ConvertHearing(record.getHearingLeft());
                break;
            case "Hearing Right":
                x = LineChartValueFormatter.ConvertHearing(record.getHearingRight());
                break;
            case "Gross Motor": x = record.getGrossMotor();
                break;
            case "Fine Motor (Dominant Hand)": x = record.getFineMotorDominant();
                break;
            case "Fine Motor (Non-Dominant Hand)": x = record.getFineMotorNDominant();
                break;
            case "Fine Motor (Hold)": x = record.getFineMotorHold();
                break;
        }

        return x;
    }

    private void setLineChartValueFormatter(LineChart lineChart, LineData lineData) {
        if(recordColumn.contains("BMI")) {
            //lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterBMI(idealValue));
            lineChart.getAxisRight().setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterBMI(idealValue));
        } else if(recordColumn.contains("Visual Acuity")) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterVisualAcuity());
            lineChart.getAxisRight().setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterVisualAcuity());
        } else if(recordColumn.contains("Color Vision")) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterColorVision());
            lineChart.getAxisRight().setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterColorVision());
        } else if(recordColumn.contains("Hearing")) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterHearing());
            lineChart.getAxisRight().setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterHearing());
        } else if(recordColumn.contains("Motor")) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterMotor());
            lineChart.getAxisRight().setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterMotor());
        } else {
            lineData.setValueFormatter(lineChart.getDefaultValueFormatter());
            lineChart.getAxisRight().setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float v, AxisBase axisBase) {
                    return Float.toString(v);
                }
            });
            // TODO deprecated
//            lineChart.getAxisRight().setValueFormatter(new YAxisValueFormatter() {
//                @Override
//                public String getFormattedValue(float v, YAxis yAxis) {
//                    return Float.toString(v);
//                }
//            });
        }

        formatLineChartAxis(lineChart, recordColumn.trim());
        lineChart.notifyDataSetChanged();
    }

    private void formatLineChartAxis(LineChart chart, String recordType) {

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setTextSize(10);

        switch(recordType) {
            case StringConstants.COL_BMI:
                ArrayList<Integer> listAge = new ArrayList<>();
                Record record = patientRecords.get(patientRecords.size()-1);
                String recordDate = record.getDateCreated();
                int age = patient.getAge(recordDate);
                for(int i = 0; i < patientRecords.size(); i++) {
                    listAge.add(patient.getAge(patientRecords.get(i).getDateCreated()));
                }
                Collections.sort(listAge);
                int ageSize = 20-listAge.get(0);
                String[] strAgeList = new String[ageSize];

                int ageCount;
                int i = 0 ;
                do {
                    ageCount = listAge.get(0)+i;
                    strAgeList[i] = ageCount+"";
                    Log.e("AGE", strAgeList[i]);
                    i++;
                } while(ageCount < 19);
                IAxisValueFormatter formatter = new PatientChartIAxisFormatter(strAgeList);
                xAxis.setValueFormatter(formatter);
                break;
        }
    }
    private LineDataSet createLineDataSet(int index) {
        String datasetDescription;
        int lineColor;
        float lineWidth = 3f;
        boolean drawFilled = false;
        int fillColor = Color.TRANSPARENT;
//        int valueTextColor = Color.BLACK;
        switch(index) {
            default:
            case 0: datasetDescription = "Patient";
                lineColor = Color.BLUE;
                lineWidth = 5f;
                break;
            case 1: datasetDescription = "Average";
                lineColor = ColorTemplate.getHoloBlue();
                lineWidth = 4f;
                break;
            case 2: datasetDescription = "Severe Thinness";
                lineColor = Color.BLACK;
                fillColor = Color.BLACK;
                drawFilled = true;
                break;
            case 3: datasetDescription = "3rd";
                lineColor = Color.RED;
//                fillColor = Color.BLACK;
                if(recordColumn.equals("BMI")) {
                    datasetDescription = "Thinness";
                    fillColor = Color.MAGENTA;
                    drawFilled = true;
                }
                break;
            case 4: datasetDescription = "15th";
                lineColor = Color.YELLOW;
                if(recordColumn.equals("BMI")) {
                    datasetDescription = "";
                    lineColor = Color.TRANSPARENT;
//                    valueTextColor = Color.TRANSPARENT;
                }
                break;
            case 5: datasetDescription = "50th";
                lineColor = Color.GREEN;
                if(recordColumn.equals("BMI")) {
                    datasetDescription = "Normal";
                }
                break;
            case 6: datasetDescription = "85th";
                lineColor = Color.YELLOW;
                if(recordColumn.equals("BMI")) {
                    datasetDescription = "Overweight";
                    fillColor = Color.GREEN;
                    drawFilled = true;
                }
                break;
            case 7: datasetDescription = "97th";
                lineColor = Color.RED;
                if(recordColumn.equals("BMI")) {
                    datasetDescription = "Obesity";
                    fillColor = Color.YELLOW;
                    drawFilled = true;
                }
                break;
        }

        LineDataSet lineDataset = new LineDataSet(null, datasetDescription);
        lineDataset.setColor(lineColor);
        lineDataset.setLineWidth(lineWidth);
        lineDataset.setValueTextSize(10f);

        if(index == 0) {
            lineDataset.setCircleColor(Color.WHITE);
            lineDataset.setCircleRadius(3f);
            lineDataset.setFillAlpha(4);
            lineDataset.setFillColor(lineColor);
            lineDataset.setHighLightColor(Color.rgb(244, 11, 11));
        } else {
            lineDataset.setDrawCircles(false);
            lineDataset.setValueTextColor(lineColor);
            lineDataset.setDrawFilled(drawFilled);
            lineDataset.setFillColor(fillColor);
        }

        return lineDataset;
    }

    private void customizeChart(Chart chart) {

//         customize line chart
//        chart.setDescription(""); TODO deprecated

        chart.setNoDataText("No data for the moment");

//        chart.setNoDataTextDescription("No data for the moment"); TODO deprecated
//        chart.setDescriptionTextSize(20f); TODO deprecated

//        Description description = new Description();
//        description.setText("");
//        description.setTextSize(20f);
//        chart.setDescription(description);
        // enable value highlighting
        chart.setHighlightPerTapEnabled(true);
        // enable touch gestures
        chart.setTouchEnabled(true);
        // alternative background color
        chart.setBackgroundColor(Color.WHITE);
        // get legend object
        Legend l = chart.getLegend();
        // customize legend
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);
        l.setTextSize(LEGEND_TEXT_SIZE);

        // customize content of legend
        int color[] = {Color.BLUE, ColorTemplate.getHoloBlue()};
        String label[] = {"Patient", "Average"};

//        l.setCustom(color, label); TODO deprecated

        // TODO added
//        ArrayList<LegendEntry> entries = new ArrayList<>();
//        LegendEntry entry1 = new LegendEntry(), entry2 = new LegendEntry();
//        entry1.label = label[0];
//        entry1.formColor = color[0];
//        entry2.label = label[1];
//        entry2.formColor = color[1];
//        entries.add(entry1);
//        entries.add(entry2);
//        l.setCustom(entries);

        // customize xAxis
        XAxis xl = chart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setTextSize(AXIS_TEXT_SIZE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
    }


    private void prepareLineCharts() {
        prepareLineChart(lineChart1);
        prepareLineChart(lineChart2);
        prepareLineChart(lineChart3);
    }

    private void prepareLineChart(LineChart lineChart) {
        // enable draging and scalinng
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);

        // enable pinch zoom to avoid scaling x and y separately
        lineChart.setPinchZoom(true);

        YAxis yl = lineChart.getAxisLeft();
        yl.setTextColor(Color.WHITE);
        //yl.setAxisMaxValue(120f);
        yl.setDrawGridLines(true);

        YAxis y12 = lineChart.getAxisLeft();
        y12.setEnabled(false);
    }

    private void createCharts() {
        /* create line chart */
        lineChart1 = new LineChart(this);
        customizeChart(lineChart1);

        lineChart2 = new LineChart(this);
        customizeChart(lineChart2);

        lineChart3 = new LineChart(this);
        customizeChart(lineChart3);
        /* create radar chart */
//        radarChart = new RadarChart(this);
//        customizeChart(radarChart);
    }

//    private Chart getCurrentChart(){
//        Chart chart;
//        switch (chartType) {
//            default:
//            case "Line Chart": chart = lineChart;
//                break;
////            case "Radar Chart": chart = radarChart;
////                break;
//        }
//        return chart;
//    }

    private void addChartToView() {

        lineChart1.clear();
        graphLayout1.addView(lineChart1);
        ViewGroup.LayoutParams params = lineChart1.getLayoutParams();
        /* match chart size to layout size */
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lineChart1.notifyDataSetChanged();

        lineChart2.clear();
        graphLayout2.addView(lineChart2);
        params = lineChart2.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        /* match chart size to layout size */
        lineChart2.notifyDataSetChanged();

        lineChart3.clear();
        graphLayout3.addView(lineChart3);
        params = lineChart3.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        /* match chart size to layout size */
        lineChart3.notifyDataSetChanged();


//        Chart chart = getCurrentChart();
//        chart.clear();
//        graphLayout1.addView(chart);
//        ViewGroup.LayoutParams params = chart.getLayoutParams();
//        /* match chart size to layout size */
//        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        chart.notifyDataSetChanged();
    }

    private void getPatientData() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get patient from database */
        patient = getBetterDb.getPatient(patientID);
        /* close database after retrieval */
        getBetterDb.closeDatabase();
        patient.printPatient();
    }

    private void getPatientRecords() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get patient records from database */
        patientRecords = getBetterDb.getRecords(patientID);
        /* close database after retrieval */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of records: "+patientRecords.size());
    }

    private void getIdealValues(int age) {
        String column;
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get ideal value from database */
        if(recordColumn.contains("Height")) {
            column = Record.C_HEIGHT;
        } else if(recordColumn.contains("Weight")) {
            column = Record.C_WEIGHT;
        } else {
            column = "bmi";
        }
        idealValue = getBetterDb.getIdealValue(column, patient.getGender(), age);
        /* close database after retrieval */
        getBetterDb.closeDatabase();
    }

    private IdealValue getIdealValues(String recordType, int age) {
        IdealValue idealRecordValue = null;
        String column;

        // TODO optimize. Try to access DB less
        if(hasIdealValue(recordType)) {
            DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
            /* ready database for reading */
            try {
                getBetterDb.openDatabaseForRead();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            /* get ideal value from database */
            if(recordType.contains("Height")) {
                column = Record.C_HEIGHT;
            } else if(recordType.contains("Weight")) {
                column = Record.C_WEIGHT;
            } else {
                column = "bmi";
            }
            idealRecordValue = getBetterDb.getIdealValue(column, patient.getGender(), age);
            /* close database after retrieval */
            getBetterDb.closeDatabase();
        }
        return idealRecordValue;
    }

    private void getAverageRecords() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get average records from database */
        averageRecords = new ArrayList<>();
        for(int i = 0; i < patientRecords.size(); i++) {
            averageRecords.add(getBetterDb.getAverageRecord(patient, patientRecords.get(i).getDateCreated()));
        }
        /* close database after retrieval */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of average records: "+averageRecords.size());
    }

    private void prepareRecordDateSpinner() {
        List<String> recordDateList = new ArrayList<>();
        for(int i = 0; i < patientRecords.size(); i++) {
            recordDateList.add(patientRecords.get(i).getDateCreated());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.custom_spinner, recordDateList);



        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
        spRecordDate.setAdapter(spinnerAdapter);



        spRecordDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Record record = patientRecords.get(position);
                displayRecord(record);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Record lastRecord = patientRecords.get(patientRecords.size()-1);
                displayRecord(lastRecord);
            }
        });

    }

    private void convertBlobToFile(byte[] soundBytes) {
        String outputFile= Environment.getExternalStorageDirectory().getAbsolutePath() + "/output.mp3";
        File path = new File(outputFile);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(soundBytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
