package eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes;

/**
 * Description of a table containing mappings between internal objects.
 *
 * @author ddrobny
 */
public class InternalMappingTableDefinition implements MappingTableDefinition {
    private static final String INTERNAL_MAPPINGS_TABLE_NAME = "Where_Used_PD";
    /* Where_Used_PD Columns */
    private static final String A_TABLE_ROW_COLUMN_NAME = "A_Table_Row_ID";
    private static final String B_TABLE_ROW_COLUMN_NAME = "B_Table_Row_ID";
    private static final String A_META_TABLE_ID_COLUMN_NAME = "A_MetaTable_ID";
    private static final String B_META_TABLE_ID_COLUMN_NAME = "B_MetaTable_ID";
    private static InternalMappingTableDefinition ourInstance = new InternalMappingTableDefinition();

    private InternalMappingTableDefinition() {
    }

    public static InternalMappingTableDefinition getInstance() {
        return ourInstance;
    }

    @Override
    public String getTableName() {
        return INTERNAL_MAPPINGS_TABLE_NAME;
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
