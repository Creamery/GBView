package seebee.geebeeview.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import seebee.geebeeview.R;
import seebee.geebeeview.layout.ViewPatientActivity;
import seebee.geebeeview.model.consultation.Patient;
import seebee.geebeeview.model.monitoring.AgeCalculator;
import seebee.geebeeview.sidebar.General;

/**
 * Created by Joy on 7/11/2017.
 */

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.PatientListViewHolder> {

    private static final String TAG = "PatientListAdapter";

    private ArrayList<Patient> patientList;
    private Context context;
    private String recordDate;
    private Button btnSizeReference;
    private ConstraintLayout contSizeReference;

    public PatientListAdapter(ArrayList<Patient> patientList, String date, Button btnReference, ConstraintLayout contReference) {
        this.patientList = patientList;
        this.recordDate = date;
        this.setBtnSizeReference(btnReference);
        this.setContSizeReference(contReference);
    }

    @Override
    public PatientListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_list_holder, parent, false);

        return new PatientListViewHolder(itemView);
    }

    public Drawable getGenderIcon(String gender) {
        if(gender.trim().toLowerCase().contains("f")) {
            return ContextCompat.getDrawable(context, R.drawable.img_sidebar_gender_f);
        }
        else {
            return ContextCompat.getDrawable(context, R.drawable.img_sidebar_gender_m);
        }
    }
    @Override
    public void onBindViewHolder(PatientListViewHolder holder, int position) {
        final Patient patient = patientList.get(position);
        String name = patient.getLastName() + ", "+patient.getFirstName();
        holder.tvPatientName.setText(name);
        holder.tvGender.setText(patient.getGenderString());
        holder.ivGender.setImageDrawable(getGenderIcon(patient.getGenderString()));
        holder.ivGenderColor.setBackgroundColor(General.getColorByGender(context, patient.getGenderString()));
        int age = AgeCalculator.calculateAge(patient.getBirthday(), recordDate);
        //Log.v(TAG, "Birthday: "+patient.getBirthday()+" RecordDate: "+recordDate);
        //Log.v(TAG, "Age: "+age);
        holder.tvAge.setText(Integer.toString(age));
        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewPatientActivity.class);
                intent.putExtra(Patient.C_PATIENT_ID, patient.getPatientID());
                context.startActivity(intent);
            }
        });


//        holder.btnStatus.getLayoutParams().width = btnRefresh.getWidth();
        holder.btnView.getLayoutParams().height = btnSizeReference.getHeight();
//        holder.contParent.getLayoutParams().height = contHeader.getHeight();
        holder.tvPatientName.getLayoutParams().height = contSizeReference.getHeight();
        holder.tvGender.getLayoutParams().height = contSizeReference.getHeight();
        holder.ivGender.getLayoutParams().height = btnSizeReference.getHeight();
        holder.tvAge.getLayoutParams().height = contSizeReference.getHeight();
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public class PatientListViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPatientName, tvGender, tvAge;
        public Button btnView;
        public ImageView ivGender, ivGenderColor;

        public PatientListViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvPatientName = (TextView) itemView.findViewById(R.id.tv_patient_name);
            tvGender = (TextView) itemView.findViewById(R.id.tv_patient_gender);
            tvAge = (TextView) itemView.findViewById(R.id.tv_patient_age);
            btnView = (Button) itemView.findViewById(R.id.btn_view_record);
            ivGender = (ImageView) itemView.findViewById(R.id.iv_dataset_gender_overlay);
            ivGenderColor = (ImageView) itemView.findViewById(R.id.iv_dataset_gender_color);
            /* get fonts from assets */
//            Typeface chawpFont = Typeface.createFromAsset(context.getAssets(), "font/chawp.ttf");
//            Typeface chalkFont = Typeface.createFromAsset(context.getAssets(), "font/DJBChalkItUp.ttf");
            /* set font of text */
//            tvPatientName.setTypeface(chalkFont);
//            tvGender.setTypeface(chalkFont);
//            tvAge.setTypeface(chalkFont);
//            btnView.setTypeface(chawpFont);
        }
    }

    public Button getBtnSizeReference() {
        return btnSizeReference;
    }

    public void setBtnSizeReference(Button btnSizeReference) {
        this.btnSizeReference = btnSizeReference;
    }

    public ConstraintLayout getContSizeReference() {
        return contSizeReference;
    }

    public void setContSizeReference(ConstraintLayout contSizeReference) {
        this.contSizeReference = contSizeReference;
    }
}
