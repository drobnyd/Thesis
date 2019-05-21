package eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes;

/**
 * Description of an object taking part in a mapping.
 *
 * @author ddrobny
 */
public class MappingEntry {
    private final MetaTableType metaTableType;
    private final int tableRowId;

    public MappingEntry(int metaTableId, int tableRowId) {
        this.metaTableType = MetaTableType.valueOf(metaTableId);
        this.tableRowId = tableRowId;
    }

    public boolean canBeMappedTo(MappingEntry to) {
        // Objects can be mapped to each other when they are of the same known type
        if (metaTableType == to.getMetaTableType()) {
            return metaTableType != MetaTableType.UNKNOWN_TYPE;
        } else {
            // Or when at least one of them is External Objects, this type is more vague and we assume that
            // the external object(s) are of compliant type(s) that may be mapped correctly
            // TODO more precise handling of External Objects
            return metaTableType == MetaTableType.EXTERNAL_MAPPED_OBJECT
                    || to.getMetaTableType() == MetaTableType.EXTERNAL_MAPPED_OBJECT;
        }
    }

    public int getTableRowId() {
        return tableRowId;
    }

    public MetaTableType getMetaTableType() {
        return metaTableType;
    }
}
