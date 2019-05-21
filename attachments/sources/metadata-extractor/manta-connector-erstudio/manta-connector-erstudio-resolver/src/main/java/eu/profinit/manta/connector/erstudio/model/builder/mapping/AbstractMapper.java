package eu.profinit.manta.connector.erstudio.model.builder.mapping;

import eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes.MappingEntry;
import eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes.MappingTableDefinition;
import eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes.MetaTableType;
import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;
import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;
import eu.profinit.manta.connector.erstudio.model.impl.AbstractDataObject;
import eu.profinit.manta.connector.erstudio.model.impl.CompositeDataObjectImpl;
import eu.profinit.manta.connector.erstudio.model.impl.SimpleDataObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Common logic of creation of mappings between objects.
 *
 * @author ddrobny
 */
public abstract class AbstractMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMapper.class);
    /** Every external object that is used for mapping is stored here, reflecting External_Mapped_Object table */
    protected Map<Integer, AbstractDataObject> idToExternalModeledObject = new HashMap<>();
    private MappingTableDefinition tableDefinition;
    private CsvTable mappingsTable;
    /** Both Entities and Tables are stored here since they are coming from the same csv table and their IDs are disjoint */
    private Map<Integer, CompositeDataObjectImpl> internalIdToComposite;
    /** Both Attributes and Columns are stored here since they are coming from the same csv table and their IDs are disjoint */
    private Map<Integer, SimpleDataObjectImpl> internalIdToSimple;

    public AbstractMapper(MappingTableDefinition mappingTableDefinition, CsvTable mappingsTable,
            Map<Integer, CompositeDataObjectImpl> internalIdToComposite,
            Map<Integer, SimpleDataObjectImpl> internalIdToSimple) {

        this.tableDefinition = mappingTableDefinition;
        this.mappingsTable = mappingsTable;
        this.internalIdToComposite = internalIdToComposite;
        this.internalIdToSimple = internalIdToSimple;
    }

    public void loadMappings() {
        for (CsvRow current : mappingsTable) {
            MappingEntry a = new MappingEntry(current.getIntValue(tableDefinition.getAMetaTable()),
                    current.getIntValue(tableDefinition.getATableRow()));
            MappingEntry b = new MappingEntry(current.getIntValue(tableDefinition.getBMetaTable()),
                    current.getIntValue(tableDefinition.getBTableRow()));
            mapObjects(a, b);
        }
    }

    /**
     * Creates a symmetrical mapping between two objects.
     * @param first description of the object that will store the created mapping
     * @param second description of the target of the created mapping
     */
    protected void mapObjects(MappingEntry first, MappingEntry second) {
        if (!first.canBeMappedTo(second)) {
            LOGGER.warn(
                    "Mapping cannot be completed, the objects' types are not compatible (i.e. trying to map Table to Attribute)");
            return;
        }
        AbstractDataObject firstObj = getObject(first.getMetaTableType(), first.getTableRowId());
        AbstractDataObject secondObj = getObject(second.getMetaTableType(), second.getTableRowId());
        if (firstObj == null || secondObj == null) { // Both must be reachable
            LOGGER.warn("There's an object to be mapped that hasn't been loaded, thus it cannot be mapped");
            return;
        }
        if (!firstObj.isMappedTo(secondObj)) { // Avoid mapping to the same object more than once, symmetric relation
            firstObj.addMapping(secondObj);
            secondObj.addMapping(firstObj);
        }
    }

    /**
     * Returns an actual representation of the object described by metaTableType and tableId.
     * @param metaTableType searched object's type definition.
     * @param tableId Unique ID of searched object, no object of the same {@link MetaTableType} has the same one.
     * @return actual representation of the object described by metaTableType and tableId.
     */
    protected AbstractDataObject getObject(MetaTableType metaTableType, int tableId) {
        switch (metaTableType) {
        case SIMPLE_OBJECT:
            return internalIdToSimple.get(tableId);
        case COMPOSITE_OBJECT:
            return internalIdToComposite.get(tableId);
        case EXTERNAL_MAPPED_OBJECT:
            // External mapped type is too vague, contains multiple types in fact. More precise type can be determined
            // by checking EMObj_Type in External_Mapped_Object table
            return idToExternalModeledObject.get(tableId);
        case UNKNOWN_TYPE:
        default:
            LOGGER.warn("Unknown Meta_Table_ID {} of object to be mapped", metaTableType);
            return null;
        }
    }
}
