package seebee.geebeeview.layout;

import android.content.pm.ActivityInfo;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import seebee.geebeeview.R;
import seebee.geebeeview.adapter.DatasetAdapter;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.database.VolleySingleton;
import seebee.geebeeview.model.account.Dataset;
import seebee.geebeeview.model.consultation.School;
import seebee.geebeeview.sidebar.DataListSidebar;
import seebee.geebeeview.sidebar.DataVisualizationSidebar;
import seebee.geebeeview.sidebar.General;

public class DatasetListActivity extends AppCompatActivity {
    private final static String TAG =  "ViewDatasetListActivity";
    private static String URL_SAVE_NAME = "http://128.199.205.226/server.php";
    RecyclerView rvDataset;
    ArrayList<Dataset> datasetList = new ArrayList<>();
    DatasetAdapter datasetAdapter;
    DatabaseAdapter getBetterDb;
    private TextView tvTitle, tvSchool, tvDate;
    private DataListSidebar sidebarManager;
    Button btnRefresh;
    private ConstraintLayout contTableHeader;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        datasetAdapter = new DatasetAdapter(datasetList, btnRefresh, contTableHeader);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvDataset.setLayoutManager(mLayoutManager);
        rvDataset.setItemAnimator(new DefaultItemAnimator());
        rvDataset.setAdapter(datasetAdapter);
        prepareDatasetList();


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close sidebar if extended
                if(sidebarManager.isSidebarOpen()) {
                    sidebarManager.getBtnOpenSidebar().performClick();
                }

                prepareDatasetList();
                Toast.makeText(DatasetListActivity.this, "Refreshing list...", Toast.LENGTH_SHORT).show();
            }
        });


        setupSidebarFunctionality();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataset_list);


        // lock orientation of activity to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getBetterDb = new DatabaseAdapter(this);
        tvTitle = (TextView) findViewById(R.id.tv_patient_record_title);
        tvSchool = (TextView) findViewById(R.id.tv_dl_school);
        tvDate = (TextView) findViewById(R.id.tv_dl_date);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        rvDataset = (RecyclerView) findViewById(R.id.rv_dataset);
        contTableHeader = (ConstraintLayout) findViewById(R.id.cont_table_header);

//        datasetAdapter = new DatasetAdapter(datasetList, btnRefresh);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        rvDataset.setLayoutManager(mLayoutManager);
//        rvDataset.setItemAnimator(new DefaultItemAnimator());
//        rvDataset.setAdapter(datasetAdapter);
//        prepareDatasetList();
//
//        // get fonts from assets
////        Typeface chawpFont = Typeface.createFromAsset(getAssets(), "font/chawp.ttf");
////        Typeface chalkFont = Typeface.createFromAsset(getAssets(), "font/DJBChalkItUp.ttf");
//        /* set font of text */
////        tvTitle.setTypeface(chawpFont);
////        tvSchool.setTypeface(chalkFont);
////        tvDate.setTypeface(chalkFont);
////        btnRefresh.setTypeface(chawpFont);
//
//        btnRefresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                prepareDatasetList();
//            }
//        });
//
//
//        setupSidebarFunctionality();
    }

    public void setupSidebarFunctionality () {
        // TODO About, Immun, HPI functionality
        sidebarManager = new DataListSidebar(
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

    private void prepareDatasetList() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        //updateDatasets();
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get datasetList from database */
        //datasetAdapter.clearDatasetList();
        datasetList.clear();
        updateDatasets();
        //datasetList.addAll(getBetterDb.getAllDatasets());
        for(Dataset dataset:getBetterDb.getAllDatasets()) {
            datasetList.add(dataset);

            datasetAdapter.notifyDataSetChanged();
        }
        //Log.d("NUMBER OF DATA PLS", "WTF" + Integer.toString(getBetterDb.getAllDatasets().size()));
        /* close database after insert */
        getBetterDb.closeDatabase();

        //Log.v("DatasetListActivity", "number of datasets = " + datasetList.size());
        datasetAdapter.notifyDataSetChanged();
    }

    private void updateDatasets(){
        //Check Preliminary Data (Province, municiplalities, schools)
        updatePreliminary();

        ArrayList<Dataset> datasets;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonDataset;
                        Dataset dataset;
                        School school;
                        try {
                            JSONArray jsonDatasets = new JSONArray(response);
//                            datasetList.clear();
//                            datasetAdapter.clearDatasetList();

                            for(int i=0;i<jsonDatasets.length();i++){
                                jsonDataset = jsonDatasets.getJSONObject(i);
                                dataset = new Dataset(jsonDataset.getInt("dataset_id"), jsonDataset.getInt("school_id"),
                                        jsonDataset.getString("name"),jsonDataset.getString("date_created"), 0);
                                school = new School(jsonDataset.getInt("school_id"), jsonDataset.getString("name"), getBetterDb.getMunicipalityName(jsonDataset.getInt("citymunCode")), jsonDataset.getInt("citymunCode"));

                                getBetterDb.updateDatasetList(dataset, school);
                                //Log.e(TAG, "onResponse: " + jsonDataset.getString("name"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSON ERROR:", "SOMETHING WRONG WITH JSON PARSING");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Connection Error!",Toast.LENGTH_SHORT).show();
                        Log.e("RESOPNSE ERROR:", "IDK WHY THO");
                        error.printStackTrace();
                        //progressDialog.dismiss();
                        //on error storing the name to sqlite with status unsynced
                        //saveNameToLocalStorage(name, NAME_NOT_SYNCED_WITH_SERVER);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("request", "query-datasets");
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        //prepareDatasetList();
    }

    private void updatePreliminary(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME,
                new Response.Listener<String>() {
                    int remoteCount =  0;
                    int localCount =  0;
                    JSONObject countObj;
                    //int remoteCounts[] =  new int[3];
                    //int localCounts[] =  new int[3];
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            countObj = array.getJSONObject(0);
                            remoteCount = countObj.getInt("schoolCount");

                            //remoteCounts[0] = obj.getInt("schoolCount");
                            //remoteCounts[1] = obj.getInt("municipalityCount");
                            //remoteCounts[2] = obj.getInt("provinceCount");
                        }catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "school count json error parse");
                        }
                        localCount = getBetterDb.getSchoolCount();
                        if(localCount != remoteCount){
                            updateSchoolTable(localCount);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Connection Error!",Toast.LENGTH_SHORT).show();
                        Log.e("RESOPNSE ERROR:", "IDK WHY THO");
                        error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("request", "query-preliminary");
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateSchoolTable(final int lastSchoolIndex){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonSchool;
                        School school;
                        try{
                            JSONArray jsonSchools = new JSONArray(response);
                            for (int i = 0; i < jsonSchools.length(); i++) {
                                jsonSchool = jsonSchools.getJSONObject(i);
                                school = new School(jsonSchool.getInt("school_id"), jsonSchool.getString("name"), jsonSchool.getString("municipality"));
                                getBetterDb.updateSchools(school);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Log.e(TAG, "query-school JSON Parse Error");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Connection Error!",Toast.LENGTH_SHORT).show();
                        Log.e("RESOPNSE ERROR:", "IDK WHY THO");
                        error.printStackTrace();
                }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("request", "query-school");
                params.put("school_id", Integer.toString(lastSchoolIndex));
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    /*private void updateMunicipalityTable(int lastMunicipalityIndex){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonMunicipality;
                        School municipality;
                        try{
                            JSONArray jsonSchools = new JSONArray(response);
                            for (int i = 0; i < jsonSchools.length(); i++) {
                                jsonMunicipality = jsonSchools.getJSONObject(i);
                                municipality = new School(jsonMunicipality.getInt("school_id"), jsonMunicipality.getString("name"), jsonMunicipality.getString("municipality"));
                                getBetterDb.updateMunicipality(municiplaity);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Log.e(TAG, "query-school JSON Parse Error");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Connection Error!",Toast.LENGTH_SHORT).show();
                        Log.e("RESOPNSE ERROR:", "IDK WHY THO");
                        error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("request", "query-school");
                params.put("school_id", Integer.toString(lastMunicipalityIndex));
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }*/



}
