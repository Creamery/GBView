package seebee.geebeeview.model.monitoring;

/**
 * The PatientRecord class contains the record data of a patient including their age and gender.
 * This class was created for the sole convenience
 * of having the record with the age and gender of the patient.
 *
 * @author Stephanie Dy
 */

public class PatientRecord extends Record {

    private final String TAG = "PatientRecord";

    /* gender
     * 0 - Male
     * 1 - Female */
    private int gender;

    private int age;

    private float bmi;

    //private Record record;

    public PatientRecord (int gender, String birthday, Record record) {
        super(record.getRecordID(), record.getPatient_id(), record.getDateCreated(),
                /*record.getGradeLevel(),*/ record.getHeight(), record.getWeight(),
                record.getVisualAcuityLeft(), record.getVisualAcuityRight(), record.getColorVision(),
                record.getHearingLeft(), record.getHearingRight(), record.getGrossMotor(),
                record.getFineMotorNDominant(), record.getFineMotorDominant(), record.getFineMotorHold(),
                record.getVaccination(), record.getPatientPicture(), record.getRemarksString(), record.getRemarksAudio());
        this.gender = gender;

        setAge(birthday, record.getDateCreated());
        //this.record = record;
//
//        int height, weight;
//        height = Double.valueOf(record.getHeight()).intValue();
//        weight = Double.valueOf(record.getWeight()).intValue();
        this.bmi = BMICalculator.computeBMIMetric(Double.valueOf(getHeight()).intValue(), Double.valueOf(getWeight()).intValue());
    }

    private void setAge(String birthday, String date)  {
        age = AgeCalculator.calculateAge(birthday, date);
    }

    public boolean getGender() {
        boolean isGirl = false;
        if(gender == 1){
            isGirl = true;
        }
        return isGirl;
    }

    public int getAge() {
        return age;
    }

    public String getBMIResultString() {
        return BMICalculator.getBMIResultString(getGender(), age, bmi);
    }

    public float getBMIResult() {
        return bmi;
    }
}
