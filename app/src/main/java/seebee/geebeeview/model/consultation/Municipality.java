package seebee.geebeeview.model.consultation;

/**
 * The Municipality class represents a
 * Municipality with an municipality ID, name,
 * province, and regions
 *
 * @author Katrina Lacsamana
 * @author Stephanie Dy
 * @since 11/16/2017
 */
public class Municipality {

    /**
     * Database table name for Municipality.
     */
    public final static String TABLE_NAME = "tbl_municipality";

    /**
     * Database column name for storing {@link #municipalityID}.
     */
    public final static String C_MUNICIPALITY_ID = "municipality_id";

    /**
     * Database column name for storing {@link #municipalityName}.
     */
    public final static String C_NAME = "name";

    /**
     * Database column name for storing {@link #provinceName}.
     */
    public final static String C_PROVINCE_ID = "province_id";

    /**
     * Database column name for storing {@link #regionID}.
     */
    public final static String C_REGION_ID = "region_id";

    /* ID of the municipality. */
    private int municipalityID;

    /* Name of the province that municipality belongs in */
    private String provinceName;

    /* ID of the region that municipality belongs in. */
    private String regionID;

    /* name of municipality */
    private String municipalityName;

    /* Constructor. Municipality constructor used after getting
     * the Municipality from the database.
     * @param municipalityID {@link #municipalityID}
     * @param provinceName {@link #provinceName}
     * @param regionID {@link #regionID}
     * @param municipalityName {@link #municipalityName}
     */
    public Municipality(int municipalityID, String provinceName, String regionID, String municipalityName) {
        this.municipalityID = municipalityID;
        this.provinceName = provinceName;
        this.regionID = regionID;
        this.municipalityName = municipalityName;
    }

    /* Gets {@link #municipalityID}.
     * @return {@link #municipalityID}
     */
    public int getMunicipalityID() {
        return municipalityID;
    }

    /* Gets {@link #provinceName}.
     * @return {@link #provinceName}
     */
    public String getProvinceName() {
        return provinceName;
    }

    /* Gets {@link #regionID}.
     * @return {@link #regionID}
     */
    public String getRegionID() {
        return regionID;
    }

    /* Gets {@link #municipalityName}.
     * @return {@link #municipalityName}
     */
    public String getMunicipalityName() {
        return municipalityName;
    }
}
