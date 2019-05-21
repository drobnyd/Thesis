package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.plain;

import eu.profinit.manta.connector.erstudio.model.builder.StringIdResolver;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.SimpleCreationProperties;
import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;

/**
 * Description of attributes of a simple object.
 *
 * @author ddrobny
 */
public class SimpleObjectRowModel extends AbstractModeledObjectRowModel implements SimpleCreationProperties {
    /** Unique ID of Simple Object */
    public static final String ATTRIBUTE_ID_COLUMN_NAME = "Attribute_ID";
    public static final String STRING_ID_ATTRIBUTE_NAME_COLUMN_NAME = "AttributeNameId";
    public static final String STRING_ID_COLUMN_NAME_COLUMN_NAME = "ColumnNameId";

    /** Unique ID of Simple Object */
    private int attributeId;
    private int stringIdAttributeName;
    private int stringIdColumnName;

    public SimpleObjectRowModel(StringIdResolver stringIdResolver, CsvRow row) {
        super(stringIdResolver, row);
        setFromCsvRow(row);
    }

    private void setFromCsvRow(CsvRow row) {
        attributeId = row.getIntValue(ATTRIBUTE_ID_COLUMN_NAME);
        stringIdAttributeName = row.getIntValue(STRING_ID_ATTRIBUTE_NAME_COLUMN_NAME);
        stringIdColumnName = row.getIntValue(STRING_ID_COLUMN_NAME_COLUMN_NAME);
    }

    /**
     * Gets ID of the simple object.
     * @return ID of the simple object.
     */
    public int getAttributeId() {
        return attributeId;
    }

    /**
     * Sets ID of the simple object.
     * @param attributeId ID to set.
     */
    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    /**
     * Gets ID to the string with attribute's name.
     * @return ID to the string with attribute's name.
     */
    public int getStringIdAttributeName() {
        return stringIdAttributeName;
    }

    /**
     * Sets ID to the string with name of the attribute.
     * @param stringIdAttributeName the ID to set.
     */
    public void setStringIdAttributeName(int stringIdAttributeName) {
        this.stringIdAttributeName = stringIdAttributeName;
    }

    /**
     * Gets ID to the string with column's name.
     * @return ID to the string with column's name.
     */
    public int getStringIdColumnName() {
        return stringIdColumnName;
    }

    /**
     * Sets ID to the string with name of the column.
     * @param stringIdColumnName the ID to set.
     */
    public void setStringIdColumnName(int stringIdColumnName) {
        this.stringIdColumnName = stringIdColumnName;
    }

    /**
     * Gets name of the attribute.
     * @return name of the attribute.
     */
    public String getAttributeName() {
        return stringIdResolver.getStringByStringId(stringIdAttributeName);
    }

    /**
     * Gets name of the column.
     * @return name of the column.
     */
    public String getColumnName() {
        return stringIdResolver.getStringByStringId(stringIdColumnName);
    }

    @Override
    public String getLogicalName() {
        return getAttributeName();
    }

    @Override
    public String getPhysicalName() {
        return getColumnName();
    }
}
