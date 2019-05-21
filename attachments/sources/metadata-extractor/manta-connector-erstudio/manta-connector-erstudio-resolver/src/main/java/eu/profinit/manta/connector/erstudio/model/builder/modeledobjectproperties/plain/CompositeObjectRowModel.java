package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.plain;

import eu.profinit.manta.connector.erstudio.model.builder.StringIdResolver;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.CompositeCreationProperties;
import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;

/**
 * Description of attributes of a composite object.
 *
 * @author ddrobny
 */
public class CompositeObjectRowModel extends AbstractModeledObjectRowModel implements CompositeCreationProperties {
    private static final String STRING_ID_ENTITY_NAME_COLUMN_NAME = "EntityNameId";
    private static final String STRING_ID_TABLE_NAME_COLUMN_NAME = "TableNameId";
    private static final String STRING_ID_OWNER_COLUMN_NAME = "OwnerId";

    private int stringIdOwner;
    private int stringIdTableName;
    private int stringIdEntityName;

    public CompositeObjectRowModel(StringIdResolver stringIdResolver, CsvRow row) {
        super(stringIdResolver, row);
        setFromCsvRow(row);
    }

    /**
     * Gets the ID of owner string.
     * @return the ID of owner string.
     */
    public int getStringIdOwner() {
        return stringIdOwner;
    }

    /**
     * Sets the ID of owner string.
     * @param stringIdOwner the ID of owner string to set.
     */
    public void setStringIdOwner(int stringIdOwner) {
        this.stringIdOwner = stringIdOwner;
    }

    private void setFromCsvRow(CsvRow row) {
        stringIdOwner = row.getIntValue(STRING_ID_OWNER_COLUMN_NAME);
        stringIdEntityName = row.getIntValue(STRING_ID_ENTITY_NAME_COLUMN_NAME);
        stringIdTableName = row.getIntValue(STRING_ID_TABLE_NAME_COLUMN_NAME);
    }

    @Override
    public String getOwnerName() {
        return stringIdResolver.getStringByStringId(stringIdOwner);
    }

    public String getTableName() {
        return stringIdResolver.getStringByStringId(stringIdTableName);
    }

    public String getEntityName() {
        return stringIdResolver.getStringByStringId(stringIdEntityName);
    }

    @Override
    public String getLogicalName() {
        return getEntityName();
    }

    @Override
    public String getPhysicalName() {
        return getTableName();
    }
}
