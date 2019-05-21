package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.xml;

import eu.profinit.manta.connector.erstudio.model.CompositeDataObject;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.CompositeCreationProperties;

import static eu.profinit.manta.connector.erstudio.utils.StringTranslator.processXmlString;

/**
 * Description of XML object that represents a {@link CompositeDataObject} coming from another .DM1 file
 *
 * @author ddrobny
 */
public class XmlComposite implements CompositeCreationProperties {
    private String entityName;
    private String tableName;
    private String definition;
    private String owner;
    private String entityType;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = processXmlString(entityName);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = processXmlString(tableName);
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

    @Override
    public String getOwnerName() {
        return owner;
    }

    public void setOwnerName(String ownerName) {
        this.owner = processXmlString(ownerName);
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = processXmlString(entityType);
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
