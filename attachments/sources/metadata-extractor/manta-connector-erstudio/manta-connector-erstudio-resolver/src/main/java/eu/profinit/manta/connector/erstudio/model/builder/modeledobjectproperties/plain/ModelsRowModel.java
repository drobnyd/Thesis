package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.plain;

import eu.profinit.manta.connector.erstudio.model.builder.StringIdResolver;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.ModelCreationProperties;
import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;

/**
 * Description of attributes of a data model object.
 *
 * @author ddrobny
 */
public class ModelsRowModel extends AbstractRowModel implements ModelCreationProperties {
    private static final String MODEL_PLATFORM_ID_COLUMN_NAME = "PlatformId";
    private static final String MODEL_TYPE_COLUMN_NAME = "ModelType";
    private static final String STRING_ID_MODEL_NAME_COLUMN_NAME = "ModelNameId";

    private int stringIdModelName;
    private int modelType;
    private int platformId;

    public ModelsRowModel(StringIdResolver stringIdResolver, CsvRow row) {
        super(stringIdResolver, row);
        setFromCsvRow(row);
    }

    @Override
    public int getModelType() {
        return modelType;
    }

    private void setFromCsvRow(CsvRow row) {
        modelType = row.getIntValue(MODEL_TYPE_COLUMN_NAME);
        stringIdModelName = row.getIntValue(STRING_ID_MODEL_NAME_COLUMN_NAME);
        platformId = row.getIntValue(MODEL_PLATFORM_ID_COLUMN_NAME);
    }

    @Override
    public String getName() {
        return stringIdResolver.getStringByStringId(stringIdModelName);
    }

    @Override
    public int getPlatformId() {
        return platformId;
    }
}
