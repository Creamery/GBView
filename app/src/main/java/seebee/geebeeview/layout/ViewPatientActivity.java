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
import android.support.v4.content.ContextCompat;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.renderer.LineChartRenderer;
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
import seebee.geebeeview.model.monitoring.ValueCounter;
import seebee.geebeeview.sidebar.PatientSidebar;

public class ViewPatientActivity extends AppCompatActivity {

    private static final String TAG = "ViewPatientActivity";
    private float LEGEND_TEXT_SIZE = 16f; // TODO make universal (see DataVisualization.java)
    private float AXIS_TEXT_SIZE = 14f;
    private ConstraintLayout contRecordDate;

    private TextView tvName, tvBirthday, tvDominantHand, tvAge, tvGradeLevel, tvBMI, tvPatientRemark, tvMedicalRecordTitle, tvRecordDateTitle;
    private TextView tvDate, tvHeight, tvWeight, tvVisualLeft, tvVisualRight, tvColorVision, tvHearingLeft,
            tvHearingRight, tvGrossMotor, tvFineMotorD, tvFineMotorND, tvFineMotorHold, tvRecordRemark;
    private ImageView ivPatient, ivGender;

    private ArrayList<Drawable> tabIconsSelect;
    private ArrayList<Drawable> tabIconsDeselect;
    ArrayList<Integer> listAge;
    private int genderColor, genderColorDark;
    private float generalFontSize;
    // ImageViews for the colors on the tabs
    private ImageView
            ivColorBMI, ivColorHeight, ivColorWeight,
            ivColorVAL, ivColorVAR, ivColorColor,
            ivColorHearR, ivColorHearL,
            ivColorGross,
            ivColorFineD, ivColorFineND, ivColorFineH;
    
    // ImageViews for the tab icons
    private ImageView ivImageHeart, ivImageEye, ivImageEar, ivImageBody, ivImageHand;
    private TextView tvLineChartTitle1, tvLineChartTitle2, tvLineChartTitle3;
    // Heart - BMI, Height, Weight ; Eye - VA Left, VA Right, Color Vision ; Ear - Hearing Left, Hearing Right
    // Body - Gross Motor ; Hand - Fine Dominant, Fine Non-dominant, Fine Hold
    private ConstraintLayout contHeart, contEye, contEar, contBody, contHand;

    private Button btnViewHPI, btnViewImmunization;
    private Spinner spRecordDate;
    private ConstraintLayout contAboutTitle0, contAboutTitle1, contAboutTitle2, contAboutTitle3, contAboutTitle4;


    private PatientSidebar sidebarManager;
    private ImageView ivBMIColor, ivBMIClickable;

    private LineDataSet n3Dataset_severeThinness, n2Dataset_thinness, n1Dataset_underweight,
            medianDataset_normal, p1Dataset_overweight, p2Dataset_obesity;
    private LineDataSet timelineDataset;
    private int patientID;
    private Patient patient;
    private ArrayList<Record> patientRecords, averageRecords;
//    private IdealValue idealValue;

    private RelativeLayout graphLayout1, graphLayout2, graphLayout3;
    private ConstraintLayout contGraphLayout1, contGraphLayout2, contGraphLayout3;
    private LineChart lineChart1, lineChart2, lineChart3;

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


        tvMedicalRecordTitle = findViewById(R.id.tv_scrollbar_title);
        tvRecordDateTitle = findViewById(R.id.tv_vp_date);

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
            generalFontSize = tvHeight.getTextSize();
        }
        else {
            tvHeight.setTextSize(tvWeight.getTextSize());
            generalFontSize = tvWeight.getTextSize();
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
//        tvData = (TextView) findViewById(R.id.tv_vp_data);
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

        contGraphLayout1 = findViewById(R.id.cont_chart_container1);
        contGraphLayout2 = findViewById(R.id.cont_chart_container2);
        contGraphLayout3 = findViewById(R.id.cont_chart_container3);

        tvLineChartTitle1 = findViewById(R.id.tv_patient_chart_title1);
        tvLineChartTitle2 = findViewById(R.id.tv_patient_chart_title2);
        tvLineChartTitle3 = findViewById(R.id.tv_patient_chart_title3);


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
            genderColor = General.getColorByGender(this, patient.getGender());
            genderColorDark = General.getColorByGenderDark(this, patient.getGender());
            tvName.setTextColor(genderColor);
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
        ArrayAdapter<String> spRecordAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_bold_auto,
                getResources().getStringArray(R.array.record_column_array_num_tabs));
//                getResources().getStringArray(R.array.record_column_array_num));
        spRecordColumn.setAdapter(spRecordAdapter);
        spRecordColumn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recordColumn = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(),
//                        "Displayed Data: " + parent.getItemAtPosition(position).toString(),
//                        Toast.LENGTH_SHORT).show();
                /* change the contents of the chart */
                prepareLineChartData(position);
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
        addChartToView();

        prepareLineCharts();
        prepareLineChartData(spRecordColumn.getSelectedItemPosition()); // TODO


//        Log.e("DEBUG", "Record List");

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

//        Log.e("DEBUG", "Exiting OnCreate");
    }

    private void clearAllGraphs() {
        if(lineChart1 != null) {
            lineChart1.clear();
//            lineChart1.notifyDataSetChanged();
//            lineChart1.invalidate();
        }
        if(lineChart2 != null) {
            lineChart2.clear();
//            lineChart2.notifyDataSetChanged();
//            lineChart2.invalidate();
        }
        if(lineChart3 != null) {
            lineChart3.clear();
//            lineChart3.notifyDataSetChanged();
//            lineChart3.invalidate();
        }
    }

    public void setupTabColors(Record patientRecord, String bmiText) {
        int age = patient.getAge(patientRecord.getDateCreated());
        IdealValue idealRecordValues;
//        ivColorBMI.setBackgroundColor(ColorThemes.getColor(StringConstants.COL_BMI, bmiText));
        String recordType;

        recordType = StringConstants.COL_BMI;
        ivColorBMI.setBackgroundColor(ColorThemes.getColor(recordType, bmiText));

        // if(age >= 5 && age <= 19) { // TODO return
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
            // }

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
        this.tabIconsSelect = new ArrayList<>();
        tabIconsSelect.add(ContextCompat.getDrawable(this, R.drawable.img_heart_icon_fill));
        tabIconsSelect.add(ContextCompat.getDrawable(this, R.drawable.img_visualize_icon_fill_alter));
        tabIconsSelect.add(ContextCompat.getDrawable(this, R.drawable.img_ear_icon_fill));
        tabIconsSelect.add(ContextCompat.getDrawable(this, R.drawable.img_sidebar_about_fill));
        tabIconsSelect.add(ContextCompat.getDrawable(this, R.drawable.img_hand_icon_fill));

        this.tabIconsDeselect = new ArrayList<>();
        tabIconsDeselect.add(ContextCompat.getDrawable(this, R.drawable.img_heart_icon_flip));
        tabIconsDeselect.add(ContextCompat.getDrawable(this, R.drawable.img_visualize_icon_flip_alter));
        tabIconsDeselect.add(ContextCompat.getDrawable(this, R.drawable.img_ear_icon_flip));
        tabIconsDeselect.add(ContextCompat.getDrawable(this, R.drawable.img_sidebar_about_flip));
        tabIconsDeselect.add(ContextCompat.getDrawable(this, R.drawable.img_hand_icon_flip));

        ivImageHeart = findViewById(R.id.iv_item1_image);
        ivImageEye = findViewById(R.id.iv_item2_image);
        ivImageEar = findViewById(R.id.iv_item3_image);

        ivImageBody = findViewById(R.id.iv_item4_image);
        ivImageHand = findViewById(R.id.iv_item5_image);



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


    public void refreshCharts() {
        if(lineChart1 != null) {
//            contGraphLayout1.setVisibility(View.INVISIBLE);
            lineChart1.notifyDataSetChanged();
            lineChart1.invalidate();
//            contGraphLayout1.setVisibility(View.VISIBLE);
        }
        if(lineChart2 != null) {
            lineChart2.notifyDataSetChanged();
            lineChart2.invalidate();
        }
        if(lineChart3 != null) {
            lineChart3.notifyDataSetChanged();
            lineChart3.invalidate();
        }
    }
    private void displayRecord(Record record) {
        String recordDate = record.getDateCreated();
        String[] dateCreated = spRecordDate.getSelectedItem().toString().split("/");
//        tvMedicalRecordTitle.setText("Medical Record "+dateCreated[dateCreated.length-1]);
        boolean isGirl = true;
        if(patient.getGender() != 1) {
            isGirl = false;
        }
        float bmiValue =  BMICalculator.computeBMIMetric(Double.valueOf(record.getHeight()).intValue(),
                Double.valueOf(record.getWeight()).intValue());
        String bmi = BMICalculator.getBMIResultString(isGirl,
                AgeCalculator.calculateAge(patient.getBirthday(), recordDate),
               bmiValue);
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

        tvBMI.setText(General.roundFloat(bmiValue, 2)+"");
//        Log.e("BMI", bmi);
//        tvBMI.setText(getBMIText(bmi));
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



//        int age = patient.getAge(spRecordDate.getSelectedItem().toString());
//        addLimitLineMarker(listAge.indexOf(age), age);
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


    private void hideAllGraphs() {
        if(contGraphLayout1 != null) {
            contGraphLayout1.setVisibility(View.GONE);
        }
        if(contGraphLayout2 != null) {
            contGraphLayout2.setVisibility(View.GONE);
        }
        if(contGraphLayout3 != null) {
            contGraphLayout3.setVisibility(View.GONE);
        }
    }

    private void showGraphs(int count) {
        switch(count) {
            case 3:
                contGraphLayout3.setVisibility(View.VISIBLE);
            case 2:
                contGraphLayout2.setVisibility(View.VISIBLE);
            case 1:
                contGraphLayout1.setVisibility(View.VISIBLE);
            default:
        }
    }

    private void selectTab(int tabIndex) {
        deselectAllTabs();
        switch(tabIndex) {
            case StringConstants.INDEX_HEART:
                this.ivImageHeart.setImageDrawable(tabIconsSelect.get(StringConstants.INDEX_HEART));
                contHeart.setBackgroundColor(ColorThemes.cTabSelect);
                break;
            case StringConstants.INDEX_EYE:
                this.ivImageEye.setImageDrawable(tabIconsSelect.get(StringConstants.INDEX_EYE));
                contEye.setBackgroundColor(ColorThemes.cTabSelect);
                break;
            case StringConstants.INDEX_EAR:
                this.ivImageEar.setImageDrawable(tabIconsSelect.get(StringConstants.INDEX_EAR));
                contEar.setBackgroundColor(ColorThemes.cTabSelect);
                break;
            case StringConstants.INDEX_BODY:
                this.ivImageBody.setImageDrawable(tabIconsSelect.get(StringConstants.INDEX_BODY));
                contBody.setBackgroundColor(ColorThemes.cTabSelect);
                break;
            case StringConstants.INDEX_HAND:
                this.ivImageHand.setImageDrawable(tabIconsSelect.get(StringConstants.INDEX_HAND));
                contHand.setBackgroundColor(ColorThemes.cTabSelect);
                break;
        }
    }

    private void deselectAllTabs() {
        this.ivImageHeart.setImageDrawable(tabIconsDeselect.get(StringConstants.INDEX_HEART));
        this.ivImageEye.setImageDrawable(tabIconsDeselect.get(StringConstants.INDEX_EYE));
        this.ivImageEar.setImageDrawable(tabIconsDeselect.get(StringConstants.INDEX_EAR));
        this.ivImageBody.setImageDrawable(tabIconsDeselect.get(StringConstants.INDEX_BODY));
        this.ivImageHand.setImageDrawable(tabIconsDeselect.get(StringConstants.INDEX_HAND));

        contHeart.setBackgroundColor(ColorThemes.cTabDeselect);
        contEye.setBackgroundColor(ColorThemes.cTabDeselect);
        contEar.setBackgroundColor(ColorThemes.cTabDeselect);
        contBody.setBackgroundColor(ColorThemes.cTabDeselect);
        contHand.setBackgroundColor(ColorThemes.cTabDeselect);
    }

    private void prepareLineChartData(int selectedItem) {
        clearAllGraphs();
        hideAllGraphs();
//        Log.e("SELECT", selectedItem+"");


        switch(selectedItem) {

            case StringConstants.INDEX_EAR:
//                Log.e("SELECT", "EAR");
                prepareLineChartData(lineChart1, StringConstants.COL_HEAR_LEFT);
                prepareLineChartData(lineChart2, StringConstants.COL_HEAR_RIGHT);
                showGraphs(2);
                selectTab(selectedItem);
                break;
            case StringConstants.INDEX_EYE:
//                Log.e("SELECT", "EYE");
                prepareLineChartData(lineChart1, StringConstants.COL_VA_LEFT);
                prepareLineChartData(lineChart2, StringConstants.COL_VA_RIGHT);
                prepareLineChartData(lineChart3, StringConstants.COL_COLOR_VISION);
                showGraphs(3);
                selectTab(selectedItem);
                break;
            case StringConstants.INDEX_BODY:
                prepareLineChartData(lineChart1, StringConstants.COL_GROSS_MOTOR);
                showGraphs(1);
                selectTab(selectedItem);
                break;
            case StringConstants.INDEX_HAND:
                prepareLineChartData(lineChart1, StringConstants.COL_FINE_DOMINANT);
                prepareLineChartData(lineChart2, StringConstants.COL_FINE_NON_DOMINANT);
                prepareLineChartData(lineChart3, StringConstants.COL_FINE_HOLD);
                showGraphs(3);
                selectTab(selectedItem);
                break;

            case StringConstants.INDEX_HEART:
//                Log.e("SELECT", "HEART");
                prepareLineChartData(lineChart1, StringConstants.COL_BMI);
                prepareLineChartData(lineChart2, StringConstants.COL_HEIGHT);
                prepareLineChartData(lineChart3, StringConstants.COL_WEIGHT);
                showGraphs(3);
                selectTab(selectedItem);
        }
         int age = patient.getAge(spRecordDate.getSelectedItem().toString());
         addLimitLineMarker(listAge.indexOf(age), age);

    }

    private void prepareLineChartTitle(LineChart lineChart, String recordValue) {

        if(lineChart == lineChart1) {
            tvLineChartTitle1.setText(recordValue);
        }
        else if(lineChart == lineChart2) {
            tvLineChartTitle2.setText(recordValue);
        }
        else if(lineChart == lineChart3) {
            tvLineChartTitle3.setText(recordValue);
        }
    }

    private void prepareLineChartData(LineChart lineChart, String recordValue) {
        prepareLineChartTitle(lineChart, recordValue);
        lineChart.clear();

        IdealValue idealRecordValues = null;
        LineData lineData = new LineData();
        lineData.setValueTextColor(Color.WHITE);

        lineChart.getDescription().setEnabled(false);

        // add data to line chart
        lineChart.setData(lineData);
        boolean addIdealValues = recordValue.contains(StringConstants.COL_HEIGHT) ||
                recordValue.contains(StringConstants.COL_WEIGHT) ||
                recordValue.contains(StringConstants.COL_BMI);

        if(addIdealValues) {
            StringConstants.INDEX_DATASET_TIMELINE = 6;
            StringConstants.INDEX_DATASET_AVERAGE = 7;
            StringConstants.INDEX_DATASET_PATIENT = 8;
        }
        else {
            StringConstants.INDEX_DATASET_TIMELINE = 0;
            StringConstants.INDEX_DATASET_AVERAGE = 1;
            StringConstants.INDEX_DATASET_PATIENT = 2;
        }

//        /* add dataset containing average record values from patients with same age */
//        LineDataSet averageDataset = createLineDataSet(recordValue, StringConstants.INDEX_DATASET_AVERAGE);
//        lineData.addDataSet(averageDataset); // 0
//        /* dataset containing values from patient, index 1 */
//        LineDataSet patientDataset = createLineDataSet(recordValue, StringConstants.INDEX_DATASET_PATIENT);
//        lineData.addDataSet(patientDataset); // 1



        // Adjust chart axis
        int maxLabelCount;

        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.resetAxisMaximum();
        yAxisLeft.resetAxisMinimum();
        yAxisLeft.setXOffset(10f);

        lineChart.setAutoScaleMinMaxEnabled(false);
        maxLabelCount = General.getMaxLabelCount(recordValue);
        yAxisLeft.setLabelCount(maxLabelCount);
        yAxisLeft.setAxisMinimum(-1);

        if(recordValue.equals(StringConstants.COL_BMI)) {
            yAxisLeft.setLabelCount(maxLabelCount+1);
            yAxisLeft.setAxisMaximum(ValueCounter.maxBMI);
        }
        else if(recordValue.equals(StringConstants.COL_HEIGHT)) {
            yAxisLeft.setLabelCount(maxLabelCount+1);
            yAxisLeft.setAxisMaximum(ValueCounter.maxHeight);
        }
        else if(recordValue.equals(StringConstants.COL_WEIGHT)) {
            yAxisLeft.setLabelCount(maxLabelCount+1);
            yAxisLeft.setAxisMaximum(ValueCounter.maxWeight);
        }
        else if(maxLabelCount == 3) {
            yAxisLeft.setAxisMaximum(maxLabelCount);
        }
        else if(maxLabelCount == 2) {
            yAxisLeft.setLabelCount(maxLabelCount+2);
            yAxisLeft.setAxisMaximum(maxLabelCount);
        }
        else {
            yAxisLeft.setAxisMaximum(maxLabelCount+1);
        }

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawAxisLine(false);



        // Prepare ideal values
        resetIdealDatasets();
        int highestAge = patient.getAge(spRecordDate.getItemAtPosition(spRecordDate.getCount()-1).toString());
//        Log.e("IDEALVAL", "highest age = "+highestAge);
//        Log.e("IDEALVAL", "addIdealValue = "+addIdealValues+" rec: "+recordColumn);

        // TODO Ideal values, comment out
        // if(highestAge > 5 && addIdealValues) {
        // if(highestAge >= 5 && addIdealValues) {
        // if(highestAge >= 5 && addIdealValues) {
        if(addIdealValues) {
            p2Dataset_obesity = createLineDataSet(recordValue, StringConstants.INDEX_OBESE);
            p1Dataset_overweight = createLineDataSet(recordValue, StringConstants.INDEX_OVERWEIGHT);
            medianDataset_normal = createLineDataSet(recordValue, StringConstants.INDEX_NORMAL);
            n1Dataset_underweight = createLineDataSet(recordValue, StringConstants.INDEX_UNDERWEIGHT);
            n2Dataset_thinness = createLineDataSet(recordValue, StringConstants.INDEX_THINNESS);
//            if(recordValue.equals(StringConstants.COL_BMI)) {
                n3Dataset_severeThinness = createLineDataSet(recordValue, StringConstants.INDEX_SEVERE_THINNESS);
//            }
            // if(recordValue.equals(StringConstants.COL_BMI)) {
                lineData.addDataSet(p2Dataset_obesity); // TODO 0
                lineData.addDataSet(p1Dataset_overweight); // TODO 1
                lineData.addDataSet(medianDataset_normal); // 2
                lineData.addDataSet(n1Dataset_underweight); // 3
                lineData.addDataSet(n2Dataset_thinness); // 4
                lineData.addDataSet(n3Dataset_severeThinness); // 5
//            }
//            else {
//                lineData.addDataSet(p2Dataset_obesity); // TODO 0
//                lineData.addDataSet(p1Dataset_overweight); // TODO 1
//                lineData.addDataSet(medianDataset_normal); // 2
//                lineData.addDataSet(n1Dataset_underweight); // 3
//                lineData.addDataSet(n2Dataset_thinness); // 4
            // }
        }
        // Add timeline dataset
        resetTimelineDataset();
        timelineDataset = createLineDataSet(recordValue, StringConstants.INDEX_DATASET_TIMELINE); // TODO added
        lineData.addDataSet(timelineDataset); // TODO 6

        /* add dataset containing average record values from patients with same age */
        LineDataSet averageDataset = createLineDataSet(recordValue, StringConstants.INDEX_DATASET_AVERAGE);
        lineData.addDataSet(averageDataset); // TODO 7

        /* dataset containing values from patient */
        LineDataSet patientDataset = createLineDataSet(recordValue, StringConstants.INDEX_DATASET_PATIENT);
        lineData.addDataSet(patientDataset); // TODO 8

        Record record;
        float yVal; int age;
        ArrayList<Integer> listAge = new ArrayList<>();

//        getAverageRecords(patientRecords);

        for(int i = 0; i < patientRecords.size(); i++) {
            record = patientRecords.get(i);
            /* set xValue to age of patient when record is created */
            age = patient.getAge(record.getDateCreated());
            listAge.add(age);
//            lineData.addXValue(Integer.toString(age)); // TODO deprecated, FIND replacement


            /* addIdealValues if column is either height, weight, or BMI */
            // if(addIdealValues && age >= 5 && age <= 19) {
            // if(highestAge >= 5 && addIdealValues) {
            // if(age >= 5 && addIdealValues) {
            // if(age >= 5 && addIdealValues) { // TODO return
             if(addIdealValues && ( (age >= 5 && age <= 19 ) || i == patientRecords.size()-1)) { // TODO return
                idealRecordValues = getIdealValues(recordValue, age);
                if(idealRecordValues != null) {
                    idealRecordValues.printIdealValue();
                    Log.e("IDEAL", idealRecordValues.getP2SD()+" "+idealRecordValues.getYear());
                }

                // if(addIdealValues) {
                if(idealRecordValues != null) {
                    /* add -3SD from ideal value data to patient entry, index 2 */
                    lineData.addEntry(new Entry(i, idealRecordValues.getP3SD()), StringConstants.INDEX_OBESE); // 97th/Obesity
                    /* add 2SD from ideal value data to patient entry, index 7 */
                    lineData.addEntry(new Entry(i, idealRecordValues.getP2SD()), StringConstants.INDEX_OVERWEIGHT); // 85th/Overweight

                    /* add 1SD from ideal value data to patient entry, index 6 */
                    lineData.addEntry(new Entry(i, idealRecordValues.getP1SD()), StringConstants.INDEX_NORMAL); // 50th/Normal
                    // lineData.addEntry(new Entry(i, idealRecordValues.getMedian()), StringConstants.INDEX_NORMAL); // 50th/Normal

                    /* add median of ideal value data to patient entry, index 5 */
                    lineData.addEntry(new Entry(i, idealRecordValues.getN1SD()), StringConstants.INDEX_UNDERWEIGHT); // 15th

                    /* add -1SD from ideal value data to patient entry, index 4 */
                    lineData.addEntry(new Entry(i, idealRecordValues.getN2SD()), StringConstants.INDEX_THINNESS); // 3rd/Thinness

                    /* add -2SD from ideal value data to patient entry, index 3 */
                    lineData.addEntry(new Entry(i, idealRecordValues.getN3SD()), StringConstants.INDEX_SEVERE_THINNESS); // Severe Thinness
                }
                else {
                    Log.e("IDEAL", "idealRecordValues is null");
                     /* add -3SD from ideal value data to patient entry, index 2 */
                     lineData.addEntry(new Entry(i, 0), StringConstants.INDEX_OBESE); // 97th/Obesity
                     /* add 2SD from ideal value data to patient entry, index 7 */
                     lineData.addEntry(new Entry(i, 0), StringConstants.INDEX_OVERWEIGHT); // 85th/Overweight
                     /* add -2SD from ideal value data to patient entry, index 3 */
                     lineData.addEntry(new Entry(i, 0), StringConstants.INDEX_SEVERE_THINNESS); // Severe Thinness
                     /* add -1SD from ideal value data to patient entry, index 4 */
                     lineData.addEntry(new Entry(i, 0), StringConstants.INDEX_THINNESS); // 3rd/Thinness
                     /* add median of ideal value data to patient entry, index 5 */
                     lineData.addEntry(new Entry(i, 0), StringConstants.INDEX_UNDERWEIGHT); // 15th
                     /* add 1SD from ideal value data to patient entry, index 6 */
                     lineData.addEntry(new Entry(i, 0), StringConstants.INDEX_NORMAL); // 50th/Normal
                 }
             }
            yVal = getColumnValue(recordValue, averageRecords.get(i));
//            if(recordValue.contains("Visual"))
//              Log.e("VAVG", averageRecords.get(i).getVisualAcuityLeft()+" "+getColumnValue(recordValue, averageRecords.get(i)));
            lineData.addEntry(new Entry(i, yVal), StringConstants.INDEX_DATASET_AVERAGE);
//            lineData.addEntry(new Entry(yVal, i), 1); TODO edited


            yVal = getColumnValue(recordValue, record);
            /* add patient data to patient entry, index 0 */
            lineData.addEntry(new Entry(i, yVal), StringConstants.INDEX_DATASET_PATIENT);
//            Log.e("RECORD", "patient "+recordValue+" : "+yVal);
            /* add average record values of patients of the same age, index 1 */

        }

        // Draw ideal value lines
//        if(addIdealValues) {
        // if(highestAge >= 5 && addIdealValues) {
        if(addIdealValues) {
//        if(idealRecordValues != null) {
            if(p2Dataset_obesity != null)
                customizeLineChart(listAge, recordValue, p2Dataset_obesity, StringConstants.INDEX_OBESE, ColorThemes.cBMI_Graph_Obese); // 7
            if(p1Dataset_overweight != null)
                customizeLineChart(listAge, recordValue, p1Dataset_overweight, StringConstants.INDEX_OVERWEIGHT, ColorThemes.cBMI_Graph_Overweight); // 6
            if(medianDataset_normal != null)
                customizeLineChart(listAge, recordValue, medianDataset_normal, StringConstants.INDEX_NORMAL, ColorThemes.cBMI_Graph_Normal);
            if(n1Dataset_underweight != null)
                customizeLineChart(listAge, recordValue, n1Dataset_underweight, StringConstants.INDEX_UNDERWEIGHT, ColorThemes.cBMI_Graph_Underweight);
             if(n2Dataset_thinness != null)
                customizeLineChart(listAge, recordValue, n2Dataset_thinness, StringConstants.INDEX_THINNESS, ColorThemes.cBMI_Graph_Thinness);
             if(n3Dataset_severeThinness != null)
                customizeLineChart(listAge, recordValue, n3Dataset_severeThinness, StringConstants.INDEX_SEVERE_THINNESS, ColorThemes.cBMI_Graph_SevereThinness);
       }


        customizeLineChart(listAge, recordValue, patientDataset, StringConstants.INDEX_DATASET_PATIENT, ColorThemes.cTealDefaultDark); // Patient
        customizeLineChart(listAge, recordValue, averageDataset, StringConstants.INDEX_DATASET_AVERAGE, ColorThemes.cPrimaryDark); // Average

        setLineChartValueFormatter(recordValue, lineChart, lineData, idealRecordValues);

        lineChart.getLegend().setCustom(createLegendEntries());
        lineChart.getAxisLeft().setDrawLabels(true); // TODO labels


        xAxis.setTextSize(12f);
        xAxis.setTextColor(ColorThemes.cPrimaryDark);
        yAxisLeft.removeAllLimitLines();
        LimitLine line = new LimitLine(yAxisLeft.getAxisMinimum());
        line.setLineColor(ColorThemes.cGray);
        line.setLineWidth(0.1f);
        yAxisLeft.addLimitLine(line);

        line = new LimitLine(yAxisLeft.getAxisMaximum());
        line.setLineColor(ColorThemes.cGray);
        line.setLineWidth(0.1f);
        yAxisLeft.addLimitLine(line);
        lineChart.getLegend().setEnabled(false);

        // TODO added timeline
        int timelineAge = listAge.indexOf(patient.getAge(spRecordDate.getSelectedItem().toString()));
        Log.e("TIMELINEAGE", timelineAge+" "+lineChart.getAxisLeft().getAxisMinimum()+" "+lineChart.getAxisLeft().getAxisMaximum());
        lineData.addEntry(new Entry(timelineAge, lineChart.getAxisLeft().getAxisMinimum()), StringConstants.INDEX_DATASET_TIMELINE);
        lineData.addEntry(new Entry(timelineAge, lineChart.getAxisLeft().getAxisMaximum()), StringConstants.INDEX_DATASET_TIMELINE);
        // TODO added timeline
        customizeLineChart(listAge, recordValue, timelineDataset, StringConstants.INDEX_DATASET_TIMELINE, ColorThemes.cPrimaryDark);

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        if(lineChart.isEmpty()) {
            lineChart.clear();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }

    public void resetTimelineDataset() {
        this.timelineDataset = null;
    }

    public List<LegendEntry> createLegendEntries() {
        List<LegendEntry> entries;
        entries = new ArrayList<>();

        LegendEntry entry;

        entry = new LegendEntry();
        entry.label = "Average";
        entry.formColor = ColorThemes.cPrimaryDark;
        entries.add(entry);

        entry = new LegendEntry();
        entry.label = "Patient";
        entry.formColor = genderColor;
        entries.add(entry);

        return entries;
    }

    private void resetIdealDatasets() {
        this.n3Dataset_severeThinness = null;
        this.n2Dataset_thinness = null;
        this.n1Dataset_underweight = null;
        this.medianDataset_normal = null;
        this.p1Dataset_overweight = null;
        this.p2Dataset_obesity = null;
    }
    private float getColumnValue(String recordValue, Record record) {
        float x;
        switch (recordValue) {
            default:
            case StringConstants.COL_HEIGHT: x = Double.valueOf(record.getHeight()).floatValue();
//            case "Height (in cm)": x = Double.valueOf(record.getHeight()).floatValue();
                break;
            case StringConstants.COL_WEIGHT: x = Double.valueOf(record.getWeight()).floatValue();
//            case "Weight (in kg)": x = Double.valueOf(record.getWeight()).floatValue();
                break;
            case StringConstants.COL_BMI: x = BMICalculator.computeBMIMetric(
                    Double.valueOf(record.getHeight()).intValue(),
                    Double.valueOf(record.getWeight()).intValue());
                break;
            case StringConstants.COL_VA_LEFT:
                x = LineChartValueFormatter.ConvertVisualAcuity(record.getVisualAcuityLeft());
//                Log.e("VA", "x is "+x);
                break;
            case StringConstants.COL_VA_RIGHT:
                x = LineChartValueFormatter.ConvertVisualAcuity(record.getVisualAcuityRight());
                break;
            case StringConstants.COL_COLOR_VISION:
                x = LineChartValueFormatter.ConvertColorVision(record.getColorVision());
                break;
            case StringConstants.COL_HEAR_LEFT:
                x = LineChartValueFormatter.ConvertHearing(record.getHearingLeft());
                break;
            case StringConstants.COL_HEAR_RIGHT:
                x = LineChartValueFormatter.ConvertHearing(record.getHearingRight());
                break;
            case StringConstants.COL_GROSS_MOTOR:
//                x = record.getGrossMotor();
                x = LineChartValueFormatter.ConvertGrossMotor(record.getGrossMotorString());
                Log.e("GMCHECK", "x: "+x+"   "+" string: "+record.getGrossMotorString());
                break;
            case StringConstants.COL_FINE_DOMINANT: x =  LineChartValueFormatter.ConvertMotorLabel(record.getFineMotorDominant());
                break;
            case StringConstants.COL_FINE_NON_DOMINANT: x = LineChartValueFormatter.ConvertMotorLabel(record.getFineMotorNDominant());
                break;
            case StringConstants.COL_FINE_HOLD: x = LineChartValueFormatter.ConvertMotorLabel(record.getFineMotorHold());
                break;
        }

        return x;
    }

    private void setLineChartValueFormatter(String recordValue, LineChart lineChart, LineData lineData, IdealValue idealRecordValue) {

        YAxis axisLabel = lineChart.getAxisLeft();
        if(recordValue.contains(StringConstants.COL_BMI)) {
            //lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterBMI(idealValue));
            axisLabel.setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterBMI(idealRecordValue));
//            if(idealRecordValue != null)
//                axisLabel.setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterBMI(idealRecordValue));
        } else if(recordValue.contains("Visual Acuity")) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterVisualAcuity());
            axisLabel.setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterVisualAcuity());
        } else if(recordValue.contains("Color Vision")) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterColorVision());
            axisLabel.setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterColorVision());
        } else if(recordValue.contains("Hearing")) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterHearing());
            axisLabel.setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterHearing());
        }
        else if(recordValue.equals(StringConstants.COL_GROSS_MOTOR)) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterMotor());
            axisLabel.setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterGrossMotor());
        }
        else if(recordValue.contains("Motor")) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterMotor());
            axisLabel.setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterMotor());
        } else {
            lineData.setValueFormatter(lineChart.getDefaultValueFormatter());
            axisLabel.setValueFormatter(new IAxisValueFormatter() {
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

        formatLineChartAxis(lineChart, recordValue.trim());
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private void formatLineChartAxis(LineChart chart, String recordType) {



//        switch(recordType) {
//            case StringConstants.COL_BMI:
        this.listAge = new ArrayList<>();
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
//            Log.e("AGE", strAgeList[i]);
            i++;
        } while(ageCount < 19);


        IAxisValueFormatter formatter = new PatientChartIAxisFormatter(strAgeList);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(formatter);

//        addLimitLineMarker(listAge.indexOf(age));
    }
    private LineDataSet createLineDataSet(String recordValue, int index) {
        String datasetDescription;
        if(index == StringConstants.INDEX_DATASET_PATIENT) {
            datasetDescription = "Patient";
        }
        else if(index == StringConstants.INDEX_DATASET_AVERAGE) {
            datasetDescription = "Average";
        }
        else if(index == StringConstants.INDEX_SEVERE_THINNESS) {
            datasetDescription = "Severe Thinness";
        }
        else if(index == StringConstants.INDEX_THINNESS) {
            datasetDescription = "3rd";
            if(recordValue.equals(StringConstants.COL_BMI)) {
                datasetDescription = "Thinness";
            }
        }
        else if(index == StringConstants.INDEX_UNDERWEIGHT) {
            datasetDescription = "15th";
            if(recordValue.equals(StringConstants.COL_BMI)) {
                datasetDescription = "";
            }
        }
        else if(index == StringConstants.INDEX_NORMAL) {
            datasetDescription = "50th";
            if(recordValue.equals(StringConstants.COL_BMI)) {
                datasetDescription = "Normal";
            }
        }
        else if(index == StringConstants.INDEX_OVERWEIGHT) {
            datasetDescription = "85th"; // 6
            if(recordValue.equals(StringConstants.COL_BMI)) {
                datasetDescription = "Overweight";
            }
        }
        else if(index == StringConstants.INDEX_OBESE) {
            datasetDescription = "97th"; // 7
            if(recordValue.equals(StringConstants.COL_BMI)) {
                datasetDescription = "Obesity";
            }
        }
        else if(index == StringConstants.INDEX_DATASET_TIMELINE) {
            datasetDescription = "Timeline";
        }
        else {
            datasetDescription = "Empty";
        }


        LineDataSet lineDataset = new LineDataSet(null, datasetDescription);

        return lineDataset;
    }

    private void customizeChart(Chart chart) {

//         customize line chart
//        chart.setDescription(""); TODO deprecated

        chart.setNoDataText("No data for the moment");

        // enable value highlighting
        chart.setHighlightPerTapEnabled(false);
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
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setDrawGridBackground(false);

        // disable pinch zoom
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.removeAllLimitLines();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setTextSize(10);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setDrawGridLines(true);



        YAxis yAxisRight = lineChart.getAxisRight();
//        yAxisRight.setLabelCount(yAxisRight.getLabelCount());
        yAxisRight.setTextColor(Color.TRANSPARENT);
        yAxisRight.setDrawGridLines(false);

        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false); // TODO set to true
        yAxisLeft.setEnabled(true);
//        yAxisLeft.setAxisMinimum(-2.5f);
        yAxisLeft.setGranularity(1);
//        yAxisLeft.setLabelCount(3, true);
//        yAxisLeft.setXOffset(7f);



        xAxis.setAvoidFirstLastClipping(true);



        lineChart.invalidate();
//        lineChart.setExtraOffsets(10,10,10,10);
//        lineChart.offsetTopAndBottom(50);
//        xAxis.setYOffset(10);
//        yAxisLeft.setSpaceBottom(0.5f);
//        xAxis.setEnabled(false);

    }

    public void customizeLineChart(ArrayList<Integer> listAge, String recordValue, LineDataSet lineDataSet, int index, int lineColor) {

        lineDataSet.setLineWidth(3.5f);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setCircleRadius(7f);
        lineDataSet.setCircleHoleRadius(3f);
        lineDataSet.setCircleColorHole(Color.WHITE);
        lineDataSet.setDrawValues(false);

        if(index == StringConstants.INDEX_DATASET_AVERAGE || index == StringConstants.INDEX_DATASET_PATIENT) { // Average or Patient datasets
            if(index == StringConstants.INDEX_DATASET_PATIENT) { // if Patient only, change line & circle colors

//                ArrayList<Integer> colors = General.getColors(recordValue, lineDataSet.getValues(), lineColor);
                ArrayList<Integer> colors = getColorsByIdeal(listAge, recordValue, lineDataSet.getValues(), lineColor);
                if(colors == null || colors.size() == 0) {
                    colors = General.getColors(recordValue, lineDataSet.getValues(), lineColor);
                }
                if(colors.size() > 0) {

                    lineDataSet.setCircleColors(colors);
                    lineDataSet.setColors(colors.subList(1, colors.size()));
                }
//                lineDataSet.setCircleColors(colors);
//                lineDataSet.setCircleColor(ColorThemes.cPrimaryDark);
//                lineDataSet.setCircleColor(lineColor);
//                lineDataSet.setColors(colors.subList(1, colors.size()));
                lineDataSet.setColor(lineColor);
                lineDataSet.setCircleColors(lineColor);

//                Log.e("COLORINDEX", ""+index+" avg_patient: "+StringConstants.INDEX_DATASET_AVERAGE+"_"+StringConstants.INDEX_DATASET_PATIENT);
//                lineDataSet.setDrawValues(true);
            }

            else {
                lineDataSet.setColor(lineColor);
                lineDataSet.setCircleColor(lineColor);
//                lineDataSet.setDrawCircles(false);
            }
        }
        else if(index == StringConstants.INDEX_DATASET_TIMELINE) {
            lineDataSet.setColor(lineColor);
            lineDataSet.setCircleColor(lineColor);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setVisible(true);
            // Thicken line if at start or end (since it is cut off by the viewport)
            if(spRecordDate.getSelectedItemPosition()== 0 ||
                    spRecordDate.getSelectedItemPosition() == spRecordDate.getChildCount()-1) {
                lineDataSet.setLineWidth(3.5f*2);
            }
        }
        else { // Ideal Values

            lineDataSet.setLineWidth(0.0f);
            lineDataSet.setColor(lineColor);
            lineDataSet.setCircleColor(lineColor);
            lineDataSet.setFillColor(lineColor);
            lineDataSet.setDrawValues(false);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillAlpha(255);

        }
    }

    public boolean idealDatasetsIsEmpty() {
        if(/*n3Dataset_severeThinness == null ||*/
                n2Dataset_thinness == null ||
                n1Dataset_underweight == null ||
                medianDataset_normal == null ||
                p1Dataset_overweight == null ||
                p2Dataset_obesity == null) {
            return true;
        }
        if(/*n3Dataset_severeThinness.getEntryCount() == 0 ||*/
                n2Dataset_thinness.getEntryCount() == 0 ||
                n1Dataset_underweight.getEntryCount() == 0 ||
                medianDataset_normal.getEntryCount() == 0 ||
                p1Dataset_overweight.getEntryCount() == 0 ||
                p2Dataset_obesity.getEntryCount() == 0) {
            return true;
        }
        return false;
    }

    public ArrayList<Integer> getColorsByIdeal(ArrayList<Integer> listAge, String recordValue, List<Entry> entries, int defaultColor) {
        ArrayList<Integer> idealColors = null;

        // If BMI, Height, Weight
        if(listAge != null && listAge.size() > 0 &&
                (recordValue.equals(StringConstants.COL_BMI) ||
                recordValue.equals(StringConstants.COL_HEIGHT) ||
                recordValue.equals(StringConstants.COL_WEIGHT))) {

            if(!idealDatasetsIsEmpty()) { // Dataset checker
                float yVal;
                int age;
                int index = 0;
                idealColors = new ArrayList<>();
//                Log.e("IDEALCOLORS", "sevThinness value size: "+n3Dataset_severeThinness.getEntryForIndex(0)+". entry size "+entries.size());

                // Set ideal value dataset colors
                for(int i = 0; i < entries.size(); i++) {
                    yVal = entries.get(i).getY();
                    age = listAge.get(i);

                    if(age >= 5 && age <= 19) { // TODO return TODO graph colors
                        // This part of the code is from the original.
                        if(recordValue.equals(StringConstants.COL_BMI) &&
                                index < n3Dataset_severeThinness.getEntryCount() &&
                                yVal <= n3Dataset_severeThinness.getEntryForIndex(index).getY()) {
                            idealColors.add(ColorThemes.csBMI_Graph[1]);
//                            Log.e("ENTRIES", "sv thinness "+n3Dataset_severeThinness.getEntryForIndex(index).getY());
                        }
                        else if(index < n2Dataset_thinness.getEntryCount() &&
                                yVal <= n2Dataset_thinness.getEntryForIndex(index).getY()) {
                            idealColors.add(ColorThemes.csBMI_Graph[2]);
//                            Log.e("ENTRIES", "thinness "+n2Dataset_thinness.getEntryForIndex(index).getY());
                        }
                        else if(index < n1Dataset_underweight.getEntryCount() &&
                                yVal <= n1Dataset_underweight.getEntryForIndex(index).getY()) {
                            idealColors.add(ColorThemes.csBMI_Graph[3]);
//                            Log.e("ENTRIES", "underweight "+n1Dataset_underweight.getEntryForIndex(index).getY());
                        }
//                        else if(yVal <= medianDataset_normal.getValues().get(i).getY()) {
//                            idealColors.add(ColorThemes.csBMI_Graph[4]);
//                        }
                        else if(index < p1Dataset_overweight.getEntryCount() &&
                                yVal <= p1Dataset_overweight.getEntryForIndex(index).getY()) {
                            idealColors.add(ColorThemes.csBMI_Graph[4]); // Normal if less than overweight
//                            Log.e("ENTRIES", "normal "+p1Dataset_overweight.getEntryForIndex(index).getY());
                        }
                        else if(index < p2Dataset_obesity.getEntryCount() &&
                                yVal <= p2Dataset_obesity.getEntryForIndex(index).getY()) {
                            idealColors.add(ColorThemes.csBMI_Graph[5]);
//                            Log.e("ENTRIES", "overweight "+p2Dataset_obesity.getEntryForIndex(index).getY());
                        }
                        else { // else, above obesity line
                            idealColors.add(ColorThemes.csBMI_Graph[6]);
                        }
                        index++;
                    }
                    else { // TODO return
                        idealColors.add(ColorThemes.csBMI_Graph[0]);
                    }
                }
            }
        }
        return idealColors;
    }

    public void addLimitLineMarker(int index, int age) {
        if(lineChart1 != null) {
            customizeTimelineLimitLine(lineChart1, index, age);
        }
        if(lineChart2 != null) {
            customizeTimelineLimitLine(lineChart2, index, age);
        }
        if(lineChart3 != null) {
            customizeTimelineLimitLine(lineChart3, index, age);
        }
    }

    public void customizeTimelineLimitLine(LineChart lineChart, int index, int age) {
        float limitLineWidth = 5f;
        float limitTextSize = 14f;
        int limitLineColor = Color.TRANSPARENT;
//        int limitLineColor = ColorThemes.cPrimaryDark;
        int limitLabelColor = ColorThemes.cPrimaryDark;
        String limitLabel = age+" yr";
        if(age > 1) {
            limitLabel += "s";
        }
        limitLabel += ".";

        XAxis xAxis = lineChart.getXAxis();
        xAxis.removeAllLimitLines();
        xAxis.setDrawLimitLinesBehindData(false); // TODO edit

        LimitLine limitLine = new LimitLine(index, limitLabel);
        limitLine.setTextColor(limitLabelColor);
        limitLine.setTextSize(limitTextSize);
        limitLine.setLineColor(limitLineColor);
        limitLine.setLineWidth(limitLineWidth);
//        Log.e("TIMELINE", ""+index+" "+(spRecordDate.getAdapter().getCount()-1));
        if(index == spRecordDate.getAdapter().getCount()-1) {
            limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        }
        else {
            limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        }

        xAxis.addLimitLine(limitLine);

        if(lineChart.isEmpty())
            lineChart.clear();

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private void createCharts() {
        /* create line chart */
        lineChart1 = new LineChart(this);
        customizeChart(lineChart1);

        lineChart2 = new LineChart(this);
        customizeChart(lineChart2);

        lineChart3 = new LineChart(this);
        customizeChart(lineChart3);
    }

    private void addChartToView() {

        lineChart1.clear();
        graphLayout1.addView(lineChart1);
        ViewGroup.LayoutParams params = lineChart1.getLayoutParams();
        /* match chart size to layout size */
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lineChart1.notifyDataSetChanged();
        lineChart1.invalidate();

        lineChart2.clear();
        graphLayout2.addView(lineChart2);
        params = lineChart2.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        /* match chart size to layout size */
        lineChart2.notifyDataSetChanged();
        lineChart2.invalidate();

        lineChart3.clear();
        graphLayout3.addView(lineChart3);
        params = lineChart3.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        /* match chart size to layout size */
        lineChart3.notifyDataSetChanged();
        lineChart3.invalidate();
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
            if(recordType.contains(StringConstants.COL_HEIGHT)) {
                column = Record.C_HEIGHT;
            } else if(recordType.contains(StringConstants.COL_WEIGHT)) {
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

    private void getAverageRecords(ArrayList<Record> patientRecords) {
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
                prepareLineChartData(spRecordColumn.getSelectedItemPosition());

                Record record = patientRecords.get(position);
                displayRecord(record);
                refreshCharts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                refreshCharts();
//                Record lastRecord = patientRecords.get(patientRecords.size()-1);
//                displayRecord(lastRecord);
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
