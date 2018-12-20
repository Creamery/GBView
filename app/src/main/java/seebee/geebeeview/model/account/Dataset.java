package seebee.geebeeview.model.account;

import android.util.Log;

/**
 * The Dataset class represents a dataset
 * containing information about the health records
 * of each school and the dateCreated of checkup.
 *
 * @author Stephanie Dy
 * @since 5/31/2017.
 */

public class Dataset {
    public final static String TAG = "Dataset";

    public final static String TABLE_NAME = "tbl_dataset";

    public final static String C_ID = "dataset_id";

    public final static String C_SCHOOL_ID = "school_id";

    public final static String C_DATE_CREATED = "date_created";

    public final static String C_STATUS = "status";

    private int datasetID;

    private int schoolID;
    /* School name from where dataset was recorded */
    private String schoolName;
    /* Date when the dataset was recorded */
    private String dateCreated;
    /* Status of the dataset in device,
     * 0 = not available
     * 1 = downloaded
     */
    private int status;
    /** Constructor
     * @param schoolName {@link #schoolName}
     * @param dateCreated {@link #dateCreated}
     * @param status {@link #status}
     */
    public Dataset (int datasetID, int schoolID, String schoolName, String dateCreated, int status) {
        this.datasetID = datasetID;
        this.schoolID = schoolID;
        this.dateCreated = dateCreated;
        this.status = status;
        this.schoolName = schoolName;
    }
    /**
     * Gets {@link #schoolName}.
     * @return {@link #schoolName}
     */

    /**
     * Gets {@link #dateCreated}.
     * @return {@link #dateCreated}
     */
    public String getDateCreated() {
        return dateCreated;
    }
    /**
     * Gets {@link #status}.
     * @return {@link #status}
     */
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSchoolID() {
        return schoolID;
    }

    public void printDataset() {
        Log.d(TAG, "schoolID: "+schoolID+" dateCreated: "+ dateCreated +" status: "+status);
    }

    public int getDatasetID() {
        return datasetID;
    }

    public void setDatasetID(int datasetID) {
        this.datasetID = datasetID;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
