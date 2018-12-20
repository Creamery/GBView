package seebee.geebeeview.model.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joy on 6/27/2017.
 */

public class ValueCounter {
    private static String[] lblVisualAcuity = {"20/200", "20/100", "20/70", "20/50", "20/40", "20/30", "20/25", "20/20",
            "20/15", "20/10", "20/5"};
    private final String TAG = "Value Counter";
    int[] possibleAge = {4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
    int[] tempCount;
    Map<Integer, int[]> countBMIPerAge;
    private int[] valVisualAcuityLeft = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] valVisualAcuityRight = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String[] lblColorVision = {"Normal", "Abnormal"};
    private int[] valColorVision = {0, 0};
    private String[] lblHearing = {"Normal Hearing", "Mild Hearing Loss", "Moderate Hearing Loss",
            "Moderately-Servere Hearing Loss", "Severe Hearing Loss", "Profound Hearing Loss"};
    private int[] valHearingLeft = {0, 0, 0, 0, 0, 0};
    private int[] valHearingRight = {0, 0, 0, 0, 0, 0};
    private String[] lblGrossMotor = {"Pass", "Fail", "N/A"};
    private int[] valGrossMotor = {0, 0, 0};
    private String[] lblFineMotor = {"Pass", "Fail"};
    private int[] valFineMotorDom = {0, 0};
    private int[] valFineMotorNonDom = {0, 0};
    private String[] lblFineMotorHold = {"Hold", "Not Hold"};
    private int[] valFineMotorHold = {0, 0};
    private String[] lblBMI = {"Underweight", "Normal", "Overweight", "Obese", "N/A"};
    private int[] valBMI = {0, 0, 0, 0, 0};
    private ArrayList<PatientRecord> patientRecords;

    public ValueCounter(ArrayList<PatientRecord> patientRecords) {
        this.patientRecords = patientRecords;
        setValVisualAcuity();
        setValColorVision();
        setValHearing();
        setValGrossMotor();
        setValFineMotor();
        setValBMI();
    }

    public static String convertRecordColumn(String column) {
        String recordColumn = "";
        switch (column){
            case "Visual Acuity Right":
                recordColumn = Record.C_VISUAL_ACUITY_RIGHT;
                break;
            case "Visual Acuity Left":
                recordColumn = Record.C_VISUAL_ACUITY_LEFT;
                break;
            case "Color Vision":
                recordColumn = Record.C_COLOR_VISION;
                break;
            case "Hearing Left":
                recordColumn = Record.C_HEARING_LEFT;
                break;
            case "Hearing Right":
                recordColumn = Record.C_HEARING_RIGHT;
                break;
            case "Gross Motor":
                recordColumn = Record.C_GROSS_MOTOR;
                break;
            case "Fine Motor (Dominant Hand)":
                recordColumn = Record.C_FINE_MOTOR_DOMINANT;
                break;
            case "Fine Motor (Non-Dominant Hand)":
                recordColumn = Record.C_FINE_MOTOR_N_DOMINANT;
                break;
            case "Fine Motor (Hold)":
                recordColumn = Record.C_FINE_MOTOR_HOLD;
                break;
        }
        return recordColumn;
    }

    public static int ConvertMotor(String v) {
        int result = 2;
        if(v.contentEquals("Pass")) {
            result = 0;
        } else if(v.contentEquals("Fail")) {
            result = 1;
        }
        return result;
    }

    public static int ConvertHold(String v) {
        int result = 2;
        if(v.contentEquals("Hold")) {
            result = 0;
        } else if(v.contentEquals("Not Hold")) {
            result = 1;
        }
        return result;
    }

    public static int getBMICategoryIndex(String category) {
        int index;
        switch (category) {
            case "Underweight":  index = 0;
                break;
            case "Normal": index = 1;
                break;
            case "Overweight": index = 2;
                break;
            case "Obese": index = 3;
                break;
            default:
            case "N/A": index = 4;
                break;
        }
        return index;
    }

    public String[] getLblVisualAcuity() {
        return lblVisualAcuity;
    }

    private void setValVisualAcuity() {
        int[] eye; String vision;
        for(int i = 0; i < patientRecords.size(); i++) {
            countVisualAcuity(patientRecords.get(i).getVisualAcuityLeft(), "Left");
            countVisualAcuity(patientRecords.get(i).getVisualAcuityRight(), "Right");
        }
    }

    private void countVisualAcuity(String vision, String eye) {
        int[] valVision = valVisualAcuityLeft;
        if(eye.contentEquals("Right")) {
            valVision = valVisualAcuityRight;
        }
        switch(vision) {
            case "20/200": valVision[0]++;
                break;
            case "20/100": valVision[1]++;
                break;
            case "20/70": valVision[2]++;
                break;
            case "20/50": valVision[3]++;
                break;
            case "20/40": valVision[4]++;
                break;
            case "20/30": valVision[5]++;
                break;
            case "20/25": valVision[6]++;
                break;
            case "20/20": valVision[7]++;
                break;
            case "20/15": valVision[8]++;
                break;
            case "20/10": valVision[9]++;
                break;
            case "20/5": valVision[10]++;
                break;
        }
    }

    public int convertVisualAcuity(String visualAcuity) {
        int index = -1;
        for(int i = 0; i < lblVisualAcuity.length; i++) {
            if(visualAcuity.contentEquals(lblVisualAcuity[i])) {
                index = i;
            }
        }
        return index;
    }

    public ArrayList<CategoryCounter> getVisualAcuityPerAge(String eye) {
        ArrayList<CategoryCounter> categoryCounters = new ArrayList<>();
        int category;

        for(int i = 0; i < patientRecords.size(); i++) {
            if(eye.contentEquals("Left")) {
                category = convertVisualAcuity(patientRecords.get(i).getVisualAcuityLeft());
            } else {
                category = convertVisualAcuity(patientRecords.get(i).getVisualAcuityRight());
            }
            for(int j = 0; j < categoryCounters.size(); j++) {
                if(categoryCounters.get(j).category == category &&
                        categoryCounters.get(i).age == patientRecords.get(i).getAge()) {
                    categoryCounters.add(new CategoryCounter(category, patientRecords.get(i).getAge(), 1));
                } else {

                }
            }
        }
        return categoryCounters;
    }

    public int[] getValVisualAcuityLeft() {
        return valVisualAcuityLeft;
    }

    public int[] getValVisualAcuityRight() {
        return valVisualAcuityRight;
    }

    public String[] getLblColorVision() {
        return lblColorVision;
    }

    private void setValColorVision() {
        for(int i = 0; i < patientRecords.size(); i++) {
            switch (patientRecords.get(i).getColorVision()) {
                case "Normal": valColorVision[0]++;
                    break;
                case "Abnormal": valColorVision[1]++;
                    break;
            }
        }
    }

    public int[] getValColorVision() {
        return valColorVision;
    }

    public String[] getLblHearing() {
        return lblHearing;
    }

    private void setValHearing() {
        for(int i = 0; i < patientRecords.size(); i++) {
            countHearing(patientRecords.get(i).getHearingLeft(), "Left");
            countHearing(patientRecords.get(i).getHearingRight(), "Right");
        }
    }

    private void countHearing(String hearing, String ear) {
        int[] valHearing = valHearingLeft;
        if(ear.contentEquals("Right")) {
            valHearing = valHearingRight;
        }
        switch (hearing) {
            case "Normal Hearing": valHearing[0]++;
                break;
            case "Mild Hearing Loss": valHearing[1]++;
                break;
            case "Moderate Hearing Loss": valHearing[2]++;
                break;
            case "Moderately-Servere Hearing Loss": valHearing[3]++;
                break;
            case "Severe Hearing Loss": valHearing[4]++;
                break;
            case "Profound Hearing Loss": valHearing[5]++;
                break;
        }
    }

    public int[] getValHearingLeft() {
        return valHearingLeft;
    }

    public int[] getValHearingRight() {
        return valHearingRight;
    }

    public String[] getLblGrossMotor() {
        return lblGrossMotor;
    }

    private void setValGrossMotor() {
        for(int i = 0; i < patientRecords.size(); i++) {
            switch (patientRecords.get(i).getGrossMotor()) {
                case 0: valGrossMotor[0]++;
                    break;
                case 1: valGrossMotor[1]++;
                    break;
                case 2: valGrossMotor[2]++;
            }
        }
    }

    public int[] getValGrossMotor() {
        return valGrossMotor;
    }

    public String[] getLblFineMotor() {
        return lblFineMotor;
    }

    private void setValFineMotor() {
        for(int i = 0; i < patientRecords.size(); i++) {
            countFineMotor(patientRecords.get(i).getFineMotorDominant(), "Dominant");
            countFineMotor(patientRecords.get(i).getFineMotorNDominant(), "Non-Dominant");
            countFineMotor(patientRecords.get(i).getFineMotorHold(), "Hold");
        }
    }

    private void countFineMotor(int result, String hand) {
        int[] valFineMotor = valFineMotorDom;
        if(hand.contentEquals("Non-Dominant")) {
            valFineMotor = valFineMotorNonDom;
        } else if (hand.contentEquals("Hold")) {
            valFineMotor = valFineMotorHold;
        }
        switch (result) {
            case 0: valFineMotor[0]++;
                break;
            case 1: valFineMotor[1]++;
                break;
        }
    }

    public int[] getValFineMotorDom() {
        return valFineMotorDom;
    }

    public int[] getValFineMotorNonDom() {
        return valFineMotorNonDom;
    }

    public String[] getLblFineMotorHold() {
        return lblFineMotorHold;
    }

    public int[] getValFineMotorHold() {
        return valFineMotorHold;
    }

    public int[] getValBMI() {
        return valBMI;
    }

    private void setValBMI() {
        String result;
        for(int i = 0; i < patientRecords.size(); i++) {
            result = calculateBMI(patientRecords.get(i));
            switch (result) {
                case "Underweight": valBMI[0]++;
                    break;
                case "Normal": valBMI[1]++;
                    break;
                case "Overweight": valBMI[2]++;
                    break;
                case "Obese": valBMI[3]++;
                    break;
                case "N/A": valBMI[4]++;
            }
            //Log.v(TAG, "Patient No: "+i+" Result: "+result);
        }
    }

    public String[] getPossibleAge() {
        String[] possibleAgeString = new String[possibleAge.length];
        for(int i = 0; i < possibleAge.length; i++) {
            possibleAgeString[i] = String.valueOf(possibleAge[i]);
        }
        return possibleAgeString;
    }

    public Map<Integer, int[]> getCountBMIPerAge() {
        return countBMIPerAge;
    }

    public Map<Integer, float[]> getValBMIPerAge() {
        Map<Integer, float[]> bmiPerAge = new HashMap<>();
        countBMIPerAge = new HashMap<>();
        ArrayList<PatientRecord> tempRecords = new ArrayList<>();
        for(int j = 0; j < possibleAge.length; j++) {
            tempRecords.clear();
            for (int i = 0; i < patientRecords.size(); i++) {
                if (patientRecords.get(i).getAge() == possibleAge[j]) {
                    tempRecords.add(patientRecords.get(i));
                }
            }
            bmiPerAge.put(possibleAge[j], getValBMISpecial(tempRecords));
            //Log.v(TAG, "Temp Count: "+tempCount.length);
            countBMIPerAge.put(possibleAge[j], tempCount);
        }
        return bmiPerAge;
    }

    private float[] getValBMISpecial(ArrayList<PatientRecord> records) {
        float[] values = {0, 0, 0, 0, 0};
        tempCount = new int[5];
//        for(int j = 0; j < tempCount.length; j++) {
//            tempCount[j] = 0;
//        }
        String result;
        for(int i = 0; i < records.size(); i++) {
            result = calculateBMI(records.get(i));
            switch (result) {
                case "Underweight": tempCount[0]++;
                    values[0] += records.get(i).getBMIResult();
                    break;
                case "Normal": tempCount[1]++;
                    values[1] += records.get(i).getBMIResult();
                    break;
                case "Overweight": tempCount[2]++;
                    values[2] += records.get(i).getBMIResult();
                    break;
                case "Obese": tempCount[3]++;
                    values[3] += records.get(i).getBMIResult();
                    break;
                case "N/A": tempCount[4]++;
                    values[4] += records.get(i).getBMIResult();
            }
        }
        for(int j = 0; j < values.length; j++) {
            //if(tempCount[j] > 0) {
                values[j] = values[j] / tempCount[j];
            //}
            //Log.v(TAG, "Value: "+values[j]);
        }
        return values;
    }

    public ArrayList<BMICounter> getBMISpecial() {
        ArrayList<BMICounter> bmiCounter = new ArrayList<>();
        PatientRecord patientRecord = patientRecords.get(0);
        int index;
        bmiCounter.add(new BMICounter(patientRecord.getBMIResult(), patientRecord.getBMIResultString(), patientRecord.getAge(), 1));
        for(int i = 1; i < patientRecords.size(); i++) {
            index = -1;
            patientRecord = patientRecords.get(i);
            for(int j = 0; j < bmiCounter.size(); j++) {
                if(bmiCounter.get(j).bmi == patientRecord.getBMIResult() && bmiCounter.get(j).age == patientRecord.getAge()) {
                    index = j;
                }
            }
            if(index == -1) {
                bmiCounter.add(new BMICounter(patientRecord.getBMIResult(), patientRecord.getBMIResultString(), patientRecord.getAge(), 1));
            } else {
                bmiCounter.get(index).addCount();
            }
        }
        return bmiCounter;
    }

    private String calculateBMI(PatientRecord patientRecord) {
        int height, weight;
        height = Double.valueOf(patientRecord.getHeight()).intValue();
        weight = Double.valueOf(patientRecord.getWeight()).intValue();
        float bmi = BMICalculator.computeBMIMetric(height, weight);
        return BMICalculator.getBMIResultString(patientRecord.getGender(), patientRecord.getAge(), bmi);
    }

    public String[] getLblBMI() {
        return lblBMI;
    }

    public class CategoryCounter {
        int category, age, count;
        public CategoryCounter(int category, int age, int count) {
            this.category = category;
            this.age = age;
            this.count = count;
        }

        public void addCount() {
            count++;
        }
    }

    public class BMICounter {
        int count, age;
        String category;
        float bmi;
        public BMICounter(float bmi, String category, int age, int count) {
            this.bmi = bmi;
            this.category = category;
            this.age = age;
            this.count = count;
        }

        public float getBMI() {
            return bmi;
        }

        public String getCategory() {
            return category;
        }

        public int getAge() {
            return age;
        }

        public int getCount() {
            return count;
        }

        public void addCount() {
            count++;
        }
    }
}
