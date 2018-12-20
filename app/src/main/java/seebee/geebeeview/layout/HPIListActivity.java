package seebee.geebeeview.layout;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

import seebee.geebeeview.R;
import seebee.geebeeview.adapter.HPIListAdapter;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.model.consultation.HPI;
import seebee.geebeeview.model.consultation.Patient;
import seebee.geebeeview.model.consultation.School;

public class HPIListActivity extends AppCompatActivity {

    private final String TAG = "HPIListActivity";
    RecyclerView rvHPIList;
    ArrayList<HPI> hpiList = new ArrayList<HPI>();
    HPIListAdapter hpiListAdapter;
    int schoolId, patientId;
    private TextView tvTitle, tvName, tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hpi_list);

        schoolId = getIntent().getIntExtra(School.C_SCHOOL_ID, 0);
        patientId = getIntent().getIntExtra(Patient.C_PATIENT_ID, 0);

        tvTitle = (TextView) findViewById(R.id.tv_hpi_list_title);
        tvName = (TextView) findViewById(R.id.tv_cl_name);
        tvDate = (TextView) findViewById(R.id.tv_cl_date);
        rvHPIList = (RecyclerView) findViewById(R.id.rv_hpi_list);

        /* get fonts from assets */
        Typeface chawpFont = Typeface.createFromAsset(getAssets(), "font/chawp.ttf");
        Typeface chalkFont = Typeface.createFromAsset(getAssets(), "font/DJBChalkItUp.ttf");
        /* set font of text */
        tvTitle.setTypeface(chawpFont);
        tvName.setTypeface(chalkFont);
        tvDate.setTypeface(chalkFont);

        hpiListAdapter = new HPIListAdapter(hpiList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvHPIList.setLayoutManager(mLayoutManager);
        rvHPIList.setItemAnimator(new DefaultItemAnimator());
        rvHPIList.setAdapter(hpiListAdapter);

        prepareHPIList();

    }

    private void prepareHPIList() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get hpi list from database */
        if(schoolId != 0 || patientId != 0) {
            if(schoolId != 0) {
                hpiList.addAll(getBetterDb.getHPIsFromSchool(schoolId));
            } else {
                hpiList.addAll(getBetterDb.getHPIs(patientId));
            }
        } else {
            Toast.makeText(this, "No HPI record available!", Toast.LENGTH_SHORT).show();
        }
        /* close database after insert */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of HPIs = " + hpiList.size());
        hpiListAdapter.notifyDataSetChanged();
    }
}
