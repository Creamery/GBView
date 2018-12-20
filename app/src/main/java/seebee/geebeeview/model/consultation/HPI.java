package seebee.geebeeview.model.consultation;

/**
 * The HPI class represents a history of present illness (HPI).
 * It contains the HPI ID, the patient ID to which the HPI is for,
 * the text content of the HPI, and the date of creation of the HPI.
 *
 * @author Mary Grace Malana
 * @since 24/03/2016
 */
public class HPI {

    /**
     * Database table name for HPI.
     */
    public final static String TABLE_NAME = "tbl_hpi";

    /**
     * Database column name for storing {@link #hpiID}.
     */
    public final static String C_HPI_ID = "hpi_id";

    /**
     * Database column name for storing {@link #patientID}.
     */
    public final static String C_PATIENT_ID = "patient_id";

    /**
     * Database column name for storing {@link #hpiText}.
     */
    public final static String C_HPI_TEXT = "hpi";

    /**
     * Database column name for storing {@link #dateCreated}.
     */
    public final static String C_DATE_CREATED = "date_created";

    /**
     * ID of the HPI.
     */
    private int hpiID;

    /**
     * ID of the patient with the HPI.
     */
    private int patientID;

    /**
     * HPI of the patient.
     */
    private String hpiText;

    /**
     * Date when the HPI was created.
     */
    private String dateCreated;

    /**
     * Constructor. HPI constructor used after getting
     * the HPI from the database
     * @param hpiID {@link #hpiID}
     * @param patientID {@link #patientID}
     * @param hpiText {@link #hpiText}
     * @param dateCreated {@link #dateCreated}
     */
    public HPI(int hpiID, int patientID, String hpiText, String dateCreated) {
        this.hpiID = hpiID;
        this.patientID = patientID;
        this.hpiText = hpiText;
        this.dateCreated = dateCreated;
    }

    /**
     * Constructor. HPI constructor used for inserting the HPI
     * to the database.
     * @param patientID {@link #patientID}
     * @param hpiText {@link #hpiText}
     * @param dateCreated {@link #dateCreated}
     */
    public HPI(int patientID, String hpiText, String dateCreated) {
        this.patientID = patientID;
        this.hpiText = hpiText;
        this.dateCreated = dateCreated;
    }

    /**
     * Gets {@link #hpiID}.
     * @return {@link #hpiID}
     */
    public int getHpiID() {
        return hpiID;
    }

    /**
     * Gets {@link #patientID}.
     * @return {@link #patientID}
     */
    public int getPatientID() {
        return patientID;
    }

    /**
     * Gets {@link #hpiText}.
     * @return {@link #hpiText}
     */
    public String getHpiText() {
        return hpiText;
    }

    /**
     * Gets {@link #dateCreated}.
     * @return {@link #hpiID}
     */
    public String getDateCreated() {
        return dateCreated;
    }

    /**
     * Gets string that contains the {@link HPI} attributes and
     * corresponding value.
     * @return HPI info string
     */
    public String getCompleteHPIRecord(){
        return "hpiID: " + hpiID + ", patientID: " + patientID +
                ", hpiText: " + hpiText + ", dateCreated: " + dateCreated;
    }
}
