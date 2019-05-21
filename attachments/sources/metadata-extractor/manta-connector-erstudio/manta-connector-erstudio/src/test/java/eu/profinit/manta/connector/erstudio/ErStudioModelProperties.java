package eu.profinit.manta.connector.erstudio;

/**
 * Class capturing tested aspects of a data model.
 *
 * @author ddrobny
 */
public class ErStudioModelProperties {
    private long entityCount;
    private long physicalModelCount;
    private long attributeCount;
    private long tableCount;
    private long columnCount;
    private String logicalModelName;

    public long getEntityCount() {
        return entityCount;
    }

    public void setEntityCount(long entityCount) {
        this.entityCount = entityCount;
    }

    public long getPhysicalModelCount() {
        return physicalModelCount;
    }

    public void setPhysicalModelCount(long physicalModelCount) {
        this.physicalModelCount = physicalModelCount;
    }

    public long getAttributeCount() {
        return attributeCount;
    }

    public void setAttributeCount(long attributeCount) {
        this.attributeCount = attributeCount;
    }

    public long getTableCount() {
        return tableCount;
    }

    public void setTableCount(long tableCount) {
        this.tableCount = tableCount;
    }

    public long getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(long columnCount) {
        this.columnCount = columnCount;
    }

    public String getLogicalModelName() {
        return logicalModelName;
    }

    public void setLogicalModelName(String logicalModelName) {
        this.logicalModelName = logicalModelName;
    }
}
