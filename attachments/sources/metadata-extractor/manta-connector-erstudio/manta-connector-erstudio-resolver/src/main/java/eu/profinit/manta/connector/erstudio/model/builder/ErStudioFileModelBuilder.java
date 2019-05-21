package eu.profinit.manta.connector.erstudio.model.builder;

import eu.profinit.manta.connector.erstudio.model.ErStudioFileModel;
import eu.profinit.manta.connector.erstudio.model.ErStudioSolution;
import eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes.ExternalMappingTableDefinition;
import eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes.InternalMappingTableDefinition;
import eu.profinit.manta.connector.erstudio.model.builder.mapping.AbstractMapper;
import eu.profinit.manta.connector.erstudio.model.builder.mapping.ExternalMapper;
import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;
import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;
import eu.profinit.manta.connector.erstudio.model.impl.AbstractDataObject;
import eu.profinit.manta.connector.erstudio.model.impl.ErStudioFileModelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class reconstructing objects contained in set of CSV tables.
 *
 * @author ddrobny
 */
public class ErStudioFileModelBuilder {
    /* Table names */
    private static final String MODELS_TABLE_NAME = "Model";
    private static final String ENTITIES_AND_TABLES_TABLE_NAME = "Entity";
    private static final String ATTRIBUTES_AND_COLUMNS_TABLE_NAME = "Attribute";
    private static final String EXTERNAL_MAPPED_OBJECTS_TABLE_NAME = "External_Mapped_Object";
    private static final String SMALL_STRINGS_TABLE_NAME = "SmallString";
    private static final String LARGE_STRINGS_TABLE_NAME = "LargeString";
    /** External Modeled object ID of type ER/Studio File */
    private static final int EXTERNAL_DM1_FILE_TYPE = 3;
    private static final String EXTERNAL_MAPPED_OBJECT_TYPE_COLUMN_NAME = "EMObj_Type";
    private static final String EXTERNAL_MAPPED_OBJECT_ID = "External_Mapped_Object_ID";

    // For the needs of cross model mapping
    /** Every external object that is used for mapping, either SimpleDataObject or CompositeDataObject is stored here,
     * reflecting External_Mapped_Object table */
    protected Map<Integer, AbstractDataObject> idToExternalModeledObject = new HashMap<>();
    ErStudioFileModelImpl result;
    Map<String, CsvTable> nameToTable;

    public ErStudioFileModelBuilder(Map<String, CsvTable> nameToTable, String fileName) {
        this.nameToTable = nameToTable;
        result = new ErStudioFileModelImpl(fileName);
        result.setCsvTables(nameToTable);
    }

    /**
     * Reconstructs the given objects.
     */
    public void build() {
        StringIdResolver stringIdResolver = new StringIdResolver();
        // Load string from all the tables where string are stored at
        stringIdResolver.loadStrings(nameToTable.get(SMALL_STRINGS_TABLE_NAME));
        stringIdResolver.loadStrings(nameToTable.get(LARGE_STRINGS_TABLE_NAME));

        InternalSolutionBuilder internalBuilder = new InternalSolutionBuilder(result.getFileName(), stringIdResolver,
                nameToTable.get(MODELS_TABLE_NAME), nameToTable.get(ENTITIES_AND_TABLES_TABLE_NAME),
                nameToTable.get(ATTRIBUTES_AND_COLUMNS_TABLE_NAME));

        internalBuilder.setMappingTable(nameToTable.get(InternalMappingTableDefinition.getInstance().getTableName()));

        internalBuilder.buildSolution();

        result.setInternalErStudioSolution(internalBuilder.getSolution());

        // Load external referenced objects
        result.setExternalErStudioModels(loadExternalModels());

        // Create cross model mappings
        CsvTable externalMappingTable = nameToTable.get(ExternalMappingTableDefinition.getInstance().getTableName());
        AbstractMapper mappingCreator = new ExternalMapper(externalMappingTable, internalBuilder.getIdToComposite(),
                internalBuilder.getIdToSimple(), idToExternalModeledObject);
        mappingCreator.loadMappings();
    }

    private Map<String, ErStudioSolution> loadExternalModels() {
        Map<String, ErStudioSolution> nameToExternalModel = new HashMap<>();

        CsvTable externalObjectsTable = nameToTable.get(EXTERNAL_MAPPED_OBJECTS_TABLE_NAME);

        List<CsvRow> externalFiles = externalObjectsTable
                .filter(EXTERNAL_MAPPED_OBJECT_TYPE_COLUMN_NAME, Integer.toString(EXTERNAL_DM1_FILE_TYPE));

        for (CsvRow row : externalFiles) {
            int tableId = row.getIntValue(EXTERNAL_MAPPED_OBJECT_ID);
            ExternalSolutionBuilder builder = new ExternalSolutionBuilder(tableId, externalObjectsTable);

            builder.buildSolution();

            idToExternalModeledObject.putAll(builder.getIdToModeledObject()); // Add all external ModeledObjects
            nameToExternalModel.put(builder.getSolution().getFileName(), builder.getSolution());
        }
        return nameToExternalModel;
    }

    /**
     * Gets the data structure containing reconstructed objects if build method already taken place
     * @return the data structure containing reconstructed objects.
     */
    public ErStudioFileModel getErStudioFileModel() {
        return result;
    }

}
