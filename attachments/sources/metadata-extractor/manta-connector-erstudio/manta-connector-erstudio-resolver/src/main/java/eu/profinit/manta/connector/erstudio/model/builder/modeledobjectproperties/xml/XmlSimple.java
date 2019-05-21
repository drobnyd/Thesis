package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.xml;

import eu.profinit.manta.connector.erstudio.model.SimpleDataObject;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.SimpleCreationProperties;

import static eu.profinit.manta.connector.erstudio.utils.StringTranslator.processXmlString;

/**
 * Description of XML object that represents a {@link SimpleDataObject} coming from another .DM1 file.
 *
 * @author ddrobny
 */
public class XmlSimple implements SimpleCreationProperties {
    private String attributeName;
    private String columnName;
    private String definition;
    private String primaryKey;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = processXmlString(attributeName);
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = processXmlString(columnName);
    }

    @Override
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = processXmlString(definition);
    }

    @Override
    public String getNote() {
        return null;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = processXmlString(primaryKey);
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
