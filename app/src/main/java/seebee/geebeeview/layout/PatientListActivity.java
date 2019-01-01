package seebee.geebeeview.layout;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

import seebee.geebeeview.R;
import seebee.geebeeview.adapter.DatasetAdapter;
import seebee.geebeeview.adapter.PatientListAdapter;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.model.consultation.Patient;
import seebee.geebeeview.model.consultation.School;
import seebee.geebeeview.model.monitoring.Record;
import seebee.geebeeview.sidebar.DataListSidebar;
import seebee.geebeeview.sidebar.General;
import seebee.geebeeview.sidebar.PatientListSidebar;

public class PatientListActivity extends AppCompatActivity {

    private static final String TAG = "ViewPatientActivity";

    private int schoolID;
    private String date, recordColumn, columnValue;
    private RecyclerView rvPatientList;
    private ArrayList<Patient> patientList = new ArrayList<Patient>();
    private PatientListAdapter patientListAdapter;

    private TextView tvSchoolName, tvName, tvGender, tvAge;
    private PatientListSidebar sidebarManager;
    Button btnSizeReference;
    private ConstraintLayout contTableHeader;


    public void onWindowFocusChanged(boolean hasFocus) {
        btnSizeReference = (Button) findViewById(R.id.btn_patient_about_icons);
        contTableHeader = (ConstraintLayout) findViewById(R.id.cont_table_header);

        patientListAdapter = new PatientListAdapter(patientList, date, btnSizeReference, contTableHeader);
        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(getBaseContext());
        rvPatientList.setLayoutManager(rvLayoutManager);
        rvPatientList.setItemAnimator(new DefaultItemAnimator());
        rvPatientList.setAdapter(patientListAdapter);

        preparePatientList();
        setupSidebarFunctionality();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        schoolID = getIntent().getIntExtra(School.C_SCHOOL_ID, 0);
        date = getIntent().getStringExtra(Record.C_DATE_CREATED);
        String schoolName = getIntent().getStringExtra(School.C_SCHOOLNAME);
        recordColumn = getIntent().getStringExtra("column");
        columnValue = getIntent().getStringExtra("value");

        rvPatientList = (RecyclerView) findViewById(R.id.rv_patient_list);
        tvSchoolName = (TextView) findViewById(R.id.tv_school_name);
        tvName = (TextView) findViewById(R.id.tv_pl_name);
        tvGender = (TextView) findViewById(R.id.tv_pl_gender);
        tvAge = (TextView) findViewById(R.id.tv_pl_age);

        /* get fonts from assets */
//        Typeface chawpFont = Typeface.createFromAsset(getAssets(), "font/chawp.ttf");
//        Typeface chalkFont = Typeface.createFromAsset(getAssets(), "font/DJBChalkItUp.ttf");
        /* set font of text */
//        tvSchoolName.setTypeface(chawpFont);
//        tvName.setTypeface(chalkFont);
//        tvGender.setTypeface(chalkFont);
//        tvAge.setTypeface(chalkFont);

        tvSchoolName.setText(schoolName);


    }
    public void setupSidebarFunctionality () {
        // TODO About, Immun, HPI functionality
        sidebarManager = new PatientListSidebar(
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
                if(!sidebarManager.isSidebarOpen()) {
                    sidebarManager.getBtnOpenSidebar().performClick();
                }
            }
        });
        // Hide initially
        for(int i = 0; i < sidebarManager.getItemsSidebarExtend().size(); i++) {
            sidebarManager.getItemsSidebarExtend().get(i).setVisibility(General.getVisibility(false));
        }
    }


    private void preparePatientList() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get patientList from database */
        if(recordColumn != null && columnValue != null) {
            patientList.addAll(getBetterDb.getPatientsWithCondition(schoolID, date, recordColumn, columnValue));
        } else{
            patientList.addAll(getBetterDb.getPatientsFromSchool(schoolID));
        }
        /* close database after insert */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of patients = " + patientList.size());
        patientListAdapter.notifyDataSetChanged();
    }
}
