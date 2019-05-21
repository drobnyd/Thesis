package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.plain;

import eu.profinit.manta.connector.erstudio.model.builder.StringIdResolver;
import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;

/**
 * Class that gets specific values from ICsvRow by column names
 * Also if there's reference to a string value, it resolves the link and saves
 * the string's data to corresponding field.
 *
 * @author ddrobny
 */
public abstract class AbstractRowModel {
    private static final String MODEL_ID_COLUMN_NAME = "ModelId";
    private static final String GLOBAL_ID_COLUMN_NAME = "Global_User_ID";

    protected final StringIdResolver stringIdResolver;
    private int modelId;
    private int globalId;

    public AbstractRowModel(StringIdResolver stringIdResolver, CsvRow row) {
        this.stringIdResolver = stringIdResolver;
        setFromCsvRow(row);
    }

    /**
     * Gets the model ID of the object.
     * @return the model ID of the object.
     */
    public int getModelId() {
        return modelId;
    }

    /**
     * Sets the model ID of the object.
     * @param modelId the model ID of the object to set.
     */
    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    /**
     * Gets the global ID of the object.
     * @return the global ID of the object.
     */
    public int getGlobalId() {
        return globalId;
    }

    /**
     * Sets the global ID of the object.
     * @param globalId the global ID to set.
     */
    public void setGlobalId(int globalId) {
        this.globalId = globalId;
    }

    private void setFromCsvRow(CsvRow row) {
        modelId = row.getIntValue(MODEL_ID_COLUMN_NAME);
        globalId = row.getIntValue(GLOBAL_ID_COLUMN_NAME);
    }

}
