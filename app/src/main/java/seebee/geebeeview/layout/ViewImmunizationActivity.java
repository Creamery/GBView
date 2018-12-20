package seebee.geebeeview.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import seebee.geebeeview.R;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.model.consultation.Patient;
import seebee.geebeeview.model.monitoring.Record;

public class ViewImmunizationActivity extends AppCompatActivity {
    private static final String TAG = "ImmunizationActivity";
    private Context context;
    /* layout components */
    private TextView tvTitle, tvName;
    private Spinner spDate;
    private ImageView ivImmunizaiton;
    /* needed values */
    private int patientId;
    private Patient patient;
    private ArrayList<Record> records;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_immunization);

        /* connect activity to layout components */
        tvTitle = (TextView) findViewById(R.id.tv_vi_title);
        tvName = (TextView) findViewById(R.id.tv_vi_name);
        spDate = (Spinner) findViewById(R.id.sp_vi_date);
        ivImmunizaiton = (ImageView) findViewById(R.id.iv_vaccination);
        /* get fonts from assets */
        Typeface chawpFont = Typeface.createFromAsset(getAssets(), "font/chawp.ttf");
        Typeface rudimentFont = Typeface.createFromAsset(getAssets(), "font/rudiment.ttf");
        /* set font of text */
        tvTitle.setTypeface(chawpFont);
        tvName.setTypeface(chawpFont);

        /* get patient id */
        patientId = getIntent().getIntExtra(Patient.C_PATIENT_ID, 0);
        if(patientId == 0) {
            finish();
        }


        prepareRecords();
        //sortRecords();
        prepareSpinner();

        /* display patient name */
        tvName.setText(patient.getLastName()+", "+patient.getFirstName());
    }

    private void prepareSpinner() {
        List<String> dateList = new ArrayList<>();
        ArrayList<Record> temp = new ArrayList<>();
        for(int i = 0; i < records.size(); i++) {
            if(records.get(i).getVaccination() != null) {
                dateList.add(records.get(i).getDateCreated());
                temp.add(records.get(i));
            }
        }
        records.clear();
        records.addAll(temp);

        if(records.size() == 0) {
            finish();
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.custom_spinner, dateList);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
        spDate.setAdapter(spinnerAdapter);

        spDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Record record = records.get(position);
                Bitmap bmp = BitmapFactory.decodeByteArray(record.getVaccination(), 0, record.getVaccination().length);
                if(bmp != null) {
                    ivImmunizaiton.setImageBitmap(Bitmap.createScaledBitmap(bmp, ivImmunizaiton.getWidth(),
                            ivImmunizaiton.getHeight(), false));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                bmp = BitmapFactory.decodeByteArray(records.get(0).getVaccination(), 0, records.get(0).getVaccination().length);

                ivImmunizaiton.setImageBitmap(Bitmap.createScaledBitmap(bmp, ivImmunizaiton.getWidth(),
                        ivImmunizaiton.getHeight(), false));
                //ivImmunizaiton.setImageBitmapReset(bmp, 0, true);
            }
        });

    }

    private void prepareRecords() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get hpi list from database */
        if(patientId != 0) {
            patient = getBetterDb.getPatient(patientId);
            records = getBetterDb.getRecords(patientId);
        } else {
            Toast.makeText(this, "No Immunization record available!", Toast.LENGTH_SHORT).show();
        }
        /* close database after insert */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of records = " + records.size());
    }

//    private void prepareImageView() {
//        // if image size is too large.Then scale image.
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//
//        // provide image path for create Bitmap from image.
//        // myBitmap = BitmapFactory.decodeFile( //provide here image file path//);
////Or
//       // myBitmap = BitmapFactory.decodeResource(//provider here resource //);
//
//        if (options.outWidth > 3000 || options.outHeight > 2000) {
//            options.inSampleSize = 4;
//        } else if (options.outWidth > 2000 || options.outHeight > 1500) {
//            options.inSampleSize = 3;
//        } else if (options.outWidth > 1000 || options.outHeight > 1000) {
//            options.inSampleSize = 2;
//        }
//        options.inJustDecodeBounds = false;
//
////        myBitmap = BitmapFactory.decodeResource//provider here resource //);
////        ivLargeImage.setImageBitmapReset(myBitmap, 0, true);
//    }
/*
    private void sortRecords() {
        ArrayList<Record> temporaryArray = new ArrayList<>();
        Record tempRecord;
        tempRecord = returnEarlierRecord(records.get(0), records.get(1));
        int repeat = records.size();
        for(int i = 0; i < repeat; i++) {
            for(int j = records.size()-1; j < 0; j--) {
                tempRecord = returnEarlierRecord(records.get(j-1), records.get(j));
            }
            temporaryArray.add(0, tempRecord);
            records.remove(tempRecord);
        }
        records.clear();
        records.addAll(temporaryArray);
    }

    private Record returnEarlierRecord(Record recordA, Record recordB) {
        String dateA, dateB;
        dateA = recordA.getDateCreated();
        dateB = recordB.getDateCreated();
        String earlierDate = dateA;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        int aYear, bYear, aMonth, bMonth, aDay, bDay;
        try {
            Date date = sdf.parse(dateA);
            Calendar cDateA, cDateB;
            cDateA = Calendar.getInstance();
            cDateB = Calendar.getInstance();
            cDateA.setTimeInMillis(date.getTime());
            date = sdf.parse(dateB);
            cDateB.setTimeInMillis(date.getTime());
            aYear = cDateA.get(Calendar.YEAR);
            bYear = cDateB.get(Calendar.YEAR);
            if(aYear > bYear) {
                earlierDate = dateB;
            } else if(aYear == bYear) {
                aMonth = cDateA.get(Calendar.MONTH);
                bMonth = cDateB.get(Calendar.MONTH);
                if(aMonth > bMonth) {
                    earlierDate = dateB;
                } else if (aMonth == bMonth) {
                    aDay = cDateA.get(Calendar.DATE);
                    bDay = cDateB.get(Calendar.DATE);
                    if(aDay > bDay) {
                        earlierDate = dateB;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(recordB.getDateCreated().contentEquals(earlierDate)) {
            return recordB;
        }
        return recordA;
    }
*/
}
