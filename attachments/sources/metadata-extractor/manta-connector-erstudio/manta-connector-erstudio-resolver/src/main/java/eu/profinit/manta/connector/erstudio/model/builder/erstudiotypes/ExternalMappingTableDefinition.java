package eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes;

/**
 * Description of a table containing mappings between external objects.
 *
 * @author ddrobny
 */
public class ExternalMappingTableDefinition implements MappingTableDefinition {
    private static final String EXTERNAL_MAPPINGS_TABLE_NAME = "Universal_Mapping";
    /* Universal_Mapping Columns */
    private static final String A_TABLE_ROW_COLUMN_NAME = "A_Table_Row_ID";
    private static final String B_TABLE_ROW_COLUMN_NAME = "B_Table_Row_ID";
    private static final String A_META_TABLE_ID_COLUMN_NAME = "A_Meta_Table_ID";
    private static final String B_META_TABLE_ID_COLUMN_NAME = "B_Meta_Table_ID";
    private static ExternalMappingTableDefinition ourInstance = new ExternalMappingTableDefinition();

    private ExternalMappingTableDefinition() {
    }

    public static ExternalMappingTableDefinition getInstance() {
        return ourInstance;
    }

    @Override
    public String getTableName() {
        return EXTERNAL_MAPPINGS_TABLE_NAME;
    }

    @Override
    public String getAMetaTable() {
        return A_META_TABLE_ID_COLUMN_NAME;
    }

    @Override
    public String getBMetaTable() {
        return B_META_TABLE_ID_COLUMN_NAME;
    }

    @Override
    public String getATableRow() {
        return A_TABLE_ROW_COLUMN_NAME;
    }

    @Override
    public String getBTableRow() {
        return B_TABLE_ROW_COLUMN_NAME;
    }
}
