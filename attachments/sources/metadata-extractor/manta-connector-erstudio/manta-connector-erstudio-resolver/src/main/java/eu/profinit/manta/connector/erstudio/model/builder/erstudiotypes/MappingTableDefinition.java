package eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes;

/**
 * Common structure of a table describing a mapping between objects.
 *
 * @author ddrobny
 */
public interface MappingTableDefinition {
    String getTableName();

    String getAMetaTable();

    String getBMetaTable();

    String getATableRow();

    String getBTableRow();
}
