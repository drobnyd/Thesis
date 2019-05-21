package eu.profinit.manta.connector.erstudio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.profinit.manta.connector.erstudio.model.CompositeDataObject;
import eu.profinit.manta.connector.erstudio.model.DataModel;
import eu.profinit.manta.connector.erstudio.model.DataObject;
import eu.profinit.manta.connector.erstudio.model.ErStudioFileModel;
import eu.profinit.manta.connector.erstudio.model.ErStudioSolution;
import eu.profinit.manta.connector.erstudio.model.LogicalDataModel;
import eu.profinit.manta.connector.erstudio.model.Mappable;
import eu.profinit.manta.connector.erstudio.model.PhysicalDataModel;
import eu.profinit.manta.connector.erstudio.model.SimpleDataObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Set of ER/Studio reader tests.
 *
 * @author ddrobny
 */
@RunWith(Parameterized.class)
public class ErStudioDm1ReaderTest {
    /** Root path of resource files needed for testing*/
    private static final String INPUT_PATH = "src/test/resources/readerTestInput/";
    /** File with expected properties of tested model */
    private static final String TEST_CONFIG_FILE = "test-properties.json";
    /** Subfolder of {@code INPUT_PATH} where tested models' source file are stored at */
    private static final String RELATIVE_MODEL_PATH = "models/";
    /** Location where is stored the .DM1 file that the tested {@link ErStudioSolution} will be loaded from */
    private final File modelLocation;
    /** Values are descriptions of expected properties of logical {@link CompositeDataObject} - entities from the tested {@link ErStudioSolution} object.
     * Key is the name of the corresponding {@link CompositeDataObject} entity. */
    private final Map<String, CompositeObjectTestDescription> nameToEntityDescription = new HashMap<>();
    /** Values are descriptions of expected properties of physical {@link CompositeDataObject} - tables from the tested {@link ErStudioSolution} object.
     * Key is the name of the corresponding {@link CompositeDataObject} table.
     * The structure is simplified for testing in such a way, that names in table description have to be unique even if the tables are from
     * different physical models (unlike in real world situation)*/
    private final Map<String, CompositeObjectTestDescription> nameToTableDescription = new HashMap<>();
    /** Actual model that will be tested if it has been loaded correctly */
    private ErStudioFileModel erStudioFileModel;
    /** Internal model under the test */
    private ErStudioSolution erStudioSolution;
    /** Expected model's statistics such as number of particular objects */
    private ErStudioModelProperties erStudioModelExpectedProperties = new ErStudioModelProperties();

    public ErStudioDm1ReaderTest(JsonNode testedNode) throws IOException {
        modelLocation = new File(INPUT_PATH + RELATIVE_MODEL_PATH + testedNode.get("modelName").asText(null));
        erStudioModelExpectedProperties.setLogicalModelName(testedNode.get("logicalModelName").asText(null));
        erStudioModelExpectedProperties.setPhysicalModelCount(testedNode.get("physicalModelCount").asLong());
        erStudioModelExpectedProperties.setEntityCount(testedNode.get("entityCount").asLong());
        erStudioModelExpectedProperties.setAttributeCount(testedNode.get("attributeCount").asLong());
        erStudioModelExpectedProperties.setTableCount(testedNode.get("tableCount").asLong());
        erStudioModelExpectedProperties.setColumnCount(testedNode.get("columnCount").asLong());

        /* Entries of the following arrays are assumed to be not null
         * it doesn't make sense to add null entries, since they can be omitted instead */

        if (testedNode.get("entities") != null) {
            loadEntities(testedNode.get("entities"));
        }

        if (testedNode.get("tables") != null) {
            loadTables(testedNode.get("tables"));
        }

        /* Mappings can be defined only across layers Physical object -> Logical object || Logical object -> Physical object,
         * but not Physical object -> Physical object || Logical object -> Logical object*/

        if (testedNode.get("compositeObjectMappings") != null) {
            loadCompositeObjectMappings(testedNode.get("compositeObjectMappings"));
        }

        if (testedNode.get("simpleObjectMappings") != null) {
            loadSimpleObjectMappings(testedNode.get("simpleObjectMappings"));
        }

        loadErStudioModel();
    }

    /**
     * Prepare arguments for this test class' constructor {@link ErStudioDm1ReaderTest} where a single argument
     * handed to it is a json structure for building description of objects and their relations.
     * @return List of Maps where the map is representing a json structure for building description of objects and their relations.
     * @throws IOException Is thrown when failed to read configuration file with description of objects and their relations.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> loadModelPropertiesFileName() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(new File(INPUT_PATH + TEST_CONFIG_FILE));
        JsonNode args = node.get("testArgs");
        List<Object[]> result = new ArrayList<>();
        if (args.isArray()) {
            for (final JsonNode arg : args) {
                result.add(new Object[] { arg });
            }
        }

        return result;
    }

    /**
     * Add information about mappings to {@link SimpleObjectTestDescription}
     * @param simpleObjectMappings Json array where an entry is representing a single symmetrical mapping between two {@link SimpleDataObject}s.
     */
    private void loadSimpleObjectMappings(JsonNode simpleObjectMappings) {
        if (!simpleObjectMappings.isArray()) {
            return;
        }
        for (JsonNode mapping : simpleObjectMappings) {
            CompositeObjectTestDescription entity = nameToEntityDescription.get(mapping.get("entityName").asText(null));
            CompositeObjectTestDescription table = nameToTableDescription.get(mapping.get("tableName").asText(null));
            SimpleObjectTestDescription attribute = entity
                    .getSubObjectDescription(mapping.get("attributeName").asText(null));
            SimpleObjectTestDescription column = table.getSubObjectDescription(mapping.get("columnName").asText(null));
            attribute.addMappedObjectDescription(column);
            column.addMappedObjectDescription(attribute);
        }
    }

    /**
     * Add information about mappings to {@link CompositeObjectTestDescription}
     * @param compositeObjectMappings Json array where an entry is representing a single symmetrical mapping between two {@link CompositeDataObject}s.
     */
    private void loadCompositeObjectMappings(JsonNode compositeObjectMappings) {
        if (!compositeObjectMappings.isArray()) {
            return;
        }
        for (JsonNode currentMapping : compositeObjectMappings) {
            CompositeObjectTestDescription entity = nameToEntityDescription
                    .get(currentMapping.get("entityName").asText(null));
            CompositeObjectTestDescription table = nameToTableDescription
                    .get(currentMapping.get("tableName").asText(null));
            table.addMappedObjectDescription(entity);
            entity.addMappedObjectDescription(table);
        }
    }

    /**
     * Load descriptions of {@link CompositeDataObject} tables and their sub objects to {@code nameToTableDescription} from
     * a json array where an entry is description {@link CompositeObjectTestDescription} of a table.
     * @param tables Json array where an entry is representing {@link CompositeObjectTestDescription} of a table. Non-null.
     */
    private void loadTables(JsonNode tables) {
        if (!tables.isArray()) {
            return;
        }
        for (JsonNode currentTable : tables) {
            // Tables are assumed to be non-null
            CompositeObjectTestDescription table = createCompositeObjectDescription(currentTable);
            table.setModelName(currentTable.get("physicalModelName").asText(null));
            table.setSubObjectCount(currentTable.get("columnCount").asLong());
            if (!currentTable.get("columns").isNull()) {
                // Columns are assumed to be non-null
                for (JsonNode o1 : (currentTable.get("columns"))) {
                    table.addSubObjectDescription(createSimpleObjectDescription(o1, table));
                }
            }
            this.nameToTableDescription.put(table.getName(), table);
        }
    }

    /**
     * Load descriptions of {@link CompositeDataObject} entities and their sub objects to {@code nameToEntityDescription} from
     * a json array where an entry is description {@link CompositeObjectTestDescription} of an entity.
     * @param entities Json array where an entry is representing {@link CompositeObjectTestDescription} of an entity. Non-null.
     */
    private void loadEntities(JsonNode entities) {
        if (!entities.isArray()) {
            return;
        }
        for (JsonNode currentEntity : entities) {
            // Entities are assumed to be non-null
            CompositeObjectTestDescription entity = createCompositeObjectDescription(currentEntity);
            entity.setMappingCount(currentEntity.get("mappingCount").asLong());
            entity.setSubObjectCount(currentEntity.get("attributeCount").asLong());
            if (!currentEntity.get("attributes").isNull()) {
                // Attributes are assumed to be non-null
                for (JsonNode o1 : currentEntity.get("attributes")) {
                    entity.addSubObjectDescription(createSimpleObjectDescription(o1, entity));
                }
            }
            this.nameToEntityDescription.put(entity.getName(), entity);
        }
    }

    /**
     * Build a description of {@link SimpleDataObject} from json structure representing the resulting {@link SimpleObjectTestDescription}.
     * @param nameToField Json structure containing properties of the resulting description. Non-null.
     * @return Description of {@link SimpleDataObject}. Not null.
     */
    private SimpleObjectTestDescription createSimpleObjectDescription(JsonNode nameToField,
            CompositeObjectTestDescription superObject) {
        SimpleObjectTestDescription result = new SimpleObjectTestDescription(superObject);
        result.setName(nameToField.get("name").asText(null));
        result.setDefinition(nameToField.get("definition").asText(null));
        result.setNote(nameToField.get("note").asText(null));
        return result;
    }

    /**
     * Build a description of {@link CompositeDataObject} from json structure representing the resulting {@link CompositeObjectTestDescription}.
     * @param nameToField Json structure containing properties of the resulting description. Non-null.
     * @return Description of {@link CompositeDataObject}. Not null.
     */
    private CompositeObjectTestDescription createCompositeObjectDescription(JsonNode nameToField) {
        CompositeObjectTestDescription result = new CompositeObjectTestDescription();
        result.setName(nameToField.get("name").asText(null));
        result.setDefinition(nameToField.get("definition").asText(null));
        result.setNote(nameToField.get("note").asText(null));
        result.setOwner(nameToField.get("owner").asText(null));
        return result;
    }

    /**
     * Initialize the {@link ErStudioSolution} under the test by loading it from file
     */
    private void loadErStudioModel() throws IOException {
        ErStudioDm1Reader reader = new ErStudioDm1Reader();
        /* Get erStudioSolution */
        erStudioFileModel = reader.readFile(modelLocation);
        reader.close();
        erStudioSolution = erStudioFileModel.getInternalErStudioSolution();
    }

    /**
     * Check if the {@link ErStudioSolution} under test is valid
     */
    @Test
    public void erStudioModelTest() {
        assertNotNull(erStudioSolution);
    }

    /**
     * Check if the {@link ErStudioSolution} under has a correct {@link LogicalDataModel}
     */
    @Test
    public void logicalModelTest() {
        assertNotNull(
                "The logical model does not exist, the tested model is incorrect since every ER/Studio model must contain at least one logical model",
                erStudioSolution.getLogicalModel());// Every Diagram contains exactly one Logical DataModel
        assertEquals(erStudioModelExpectedProperties.getLogicalModelName(),
                erStudioSolution.getLogicalModel().getName());
    }

    /**
     * Check if the {@link ErStudioSolution} under test has the same amount of physical models as expected
     */
    @Test
    public void physicalModelsTest() {
        assertEquals(erStudioModelExpectedProperties.getPhysicalModelCount(),
                erStudioSolution.getAllPhysicalModels().size());
    }

    /**
     * Check if the {@link ErStudioSolution} under test has the same amount of tables as expected
     */
    @Test
    public void tableCountTest() {
        assertEquals(erStudioModelExpectedProperties.getTableCount(), erStudioSolution.getAllTables().size());
    }

    /**
     * Check if the {@link ErStudioSolution} under test has the same amount of entities as expected
     */
    @Test
    public void entityCountTest() {
        assertEquals(erStudioModelExpectedProperties.getEntityCount(), erStudioSolution.getAllEntities().size());
    }

    /**
     * Check if the {@link ErStudioSolution} under test has the same amount of attributes as expected
     */
    @Test
    public void attributeCountTest() {
        assertEquals(erStudioModelExpectedProperties.getAttributeCount(), erStudioSolution.getAllAttributes().size());
    }

    /**
     * Check if the {@link ErStudioSolution} under test has the same amount of columns as expected
     */
    @Test
    public void columnCountTest() {
        assertEquals(erStudioModelExpectedProperties.getColumnCount(), erStudioSolution.getAllColumns().size());
    }

    /**
     * Go through mappings of every entity specified in its description {@link CompositeObjectTestDescription} and check
     * if the real {@link CompositeDataObject} attribute is mapped to corresponding {@link CompositeDataObject} table.
     * The method halts on assertion if any of the mappings is not as described.
     */
    @Test
    public void entitiesMappingsTest() {
        for (CompositeObjectTestDescription entityDescription : nameToEntityDescription.values()) {
            CompositeDataObject testedEntity = getTestedEntity(entityDescription.getName(),
                    entityDescription.getOwnerName());
            // Test mapped entities
            for (ModeledObjectTestDescription mappedObject : entityDescription.getAllMappedObjectDescriptions()) {
                //CompositeDataObject must be mapped to CompositeDataObject
                assertTrue(mappedObject instanceof CompositeObjectTestDescription);
                // Mapping allowed only across layers so the mapped object must be a table
                CompositeDataObject testedTable = getTestedTable(
                        ((CompositeObjectTestDescription) mappedObject).getModelName(), mappedObject.getName(),
                        ((CompositeObjectTestDescription) mappedObject).getOwnerName());
                assertMapped(testedEntity, testedTable);
            }
        }
    }

    /**
     * Go through mappings of every attribute specified in its description {@link SimpleObjectTestDescription} and check
     * if the real {@link SimpleDataObject} attribute is mapped to corresponding {@link SimpleDataObject} column.
     * The method halts on assertion if any of the mappings is not as described.
     */
    @Test
    public void attributesMappingsTest() {
        // Go through every described entity and obtain the real one
        for (CompositeObjectTestDescription entity : nameToEntityDescription.values()) {
            CompositeDataObject testedEntity = getTestedEntity(entity.getName(), entity.getOwnerName());
            // Go through every entity's description of attribute and obtain the real one
            for (SimpleObjectTestDescription attribute : entity.getSubObjectDescription()) {
                SimpleDataObject testedAttribute = getSimpleObject(testedEntity, attribute.getName());
                for (ModeledObjectTestDescription mappedObjectDescription : attribute
                        .getAllMappedObjectDescriptions()) {
                    // SimpleDataObject must be mapped to another SimpleDataObject
                    // Get the column based its description
                    assertTrue(mappedObjectDescription instanceof SimpleObjectTestDescription);
                    SimpleObjectTestDescription mappedSimpleObjectDescription = (SimpleObjectTestDescription) mappedObjectDescription;
                    // Mapping allowed only across layers so the mapped object must be a column and its parent must be a table
                    CompositeDataObject mappedColumnParentTable = getTestedTable(
                            mappedSimpleObjectDescription.getSuperObject().getModelName(),
                            mappedSimpleObjectDescription.getSuperObject().getName(),
                            mappedSimpleObjectDescription.getSuperObject().getOwnerName());
                    SimpleDataObject testedColumn = getSimpleObject(mappedColumnParentTable,
                            mappedSimpleObjectDescription.getName());
                    // Test the description against the real one
                    assertMapped(testedAttribute, testedColumn);
                }
            }
        }
    }

    /**
     * Go through all table descriptions and check if the expected properties match the real values.
     * The method halts on assertion if any of the entities is incorrect in respect to its description.
     */
    @Test
    public void tablesTest() {
        for (CompositeObjectTestDescription table : nameToTableDescription.values()) {
            CompositeDataObject testedTable = getTestedTable(table.getModelName(), table.getName(),
                    table.getOwnerName());

            assertModeledObjectMatchesDescription(table, testedTable);
            PhysicalDataModel model = erStudioSolution.getPhysicalModel(table.getModelName());
            assertNotNull(model);
            // Check if the table under the test is present in the model
            assertNotNull(model.getOwner(table.getOwnerName()).getCompositeObject(testedTable.getName()));
            assertEquals(table.getSubObjectCount(), testedTable.getAllSubObjects().size());
        }
    }

    /**
     * Go through all entity descriptions and check if the expected properties match the real values.
     */
    @Test
    public void entitiesTest() {
        for (CompositeObjectTestDescription entityDescription : nameToEntityDescription.values()) {
            CompositeDataObject testedEntity = getTestedEntity(entityDescription.getName(),
                    entityDescription.getOwnerName());

            assertModeledObjectMatchesDescription(entityDescription, testedEntity);
            assertEquals(entityDescription.getSubObjectCount(), testedEntity.getAllSubObjects().size());
            assertEquals(entityDescription.getMappingCount(), testedEntity.getAllMappedObjects().size());
        }
    }

    /**
     * Check if expected values of properties specified in descriptions {@code nameToTableDescription} of attributes
     * hold in the created attributes of {@code erStudioSolution}
     * The method halts on assertion if any of the attributes is incorrect in respect to its description.
     */
    @Test
    public void attributesTest() {
        for (CompositeObjectTestDescription entityDescription : nameToEntityDescription.values()) {
            // Get the real parent object by its model name and name
            CompositeDataObject testedEntity = getTestedEntity(entityDescription.getName(),
                    entityDescription.getOwnerName());
            for (SimpleObjectTestDescription attributeDescription : entityDescription.getSubObjectDescription()) {
                assertSimpleObjectMatchesDescription(testedEntity, attributeDescription);
            }
        }
    }

    /**
     * Check if expected values of properties specified in descriptions {@code nameToTableDescription} of columns
     * hold in the created columns of {@code erStudioSolution}
     * The method halts on assertion if any of the column is incorrect in respect to its description.
     */
    @Test
    public void columnsTest() {
        for (CompositeObjectTestDescription tableDescription : nameToTableDescription.values()) {
            // Get the real parent object by its model name and name
            CompositeDataObject testedTable = getTestedTable(tableDescription.getModelName(),
                    tableDescription.getName(), tableDescription.getOwnerName());
            for (SimpleObjectTestDescription columnDescription : tableDescription.getSubObjectDescription()) {
                assertSimpleObjectMatchesDescription(testedTable, columnDescription);
            }
        }
    }

    /**
     * Check if the {@link SimpleDataObject} obtained by name from parentObject based on its name has all relevant
     * attributes captured by {@link SimpleObjectTestDescription} same as expected.
     * If the description doesn't match the tested object or the described object cannot be reached
     * the method halts on assertion.
     * @param parentObject Parent object of the actual {@link SimpleDataObject} that will be tested. Non-null.
     * @param childDescription Expected set of attributes to be check against tested object. Non-null.
     */
    private void assertSimpleObjectMatchesDescription(CompositeDataObject parentObject,
            SimpleObjectTestDescription childDescription) {
        SimpleDataObject testedObject = getSimpleObject(parentObject, childDescription.getName());
        assertModeledObjectMatchesDescription(childDescription, testedObject);
    }

    /**
     * Tests if the objects are mapped to each other
     * @param o1 object that should have mapping to o2
     * @param o2 object that should have mapping to o1
     */
    private <T extends DataObject & Mappable<U>, U extends DataObject & Mappable<T>> void assertMapped(T o1, U o2) {
        if (o1 == null || o2 == null) {
            // Mapping to null is forbidden, mappings of null doesn't make sense
            fail();
        }
        assertTrue(o1.isMappedTo(o2));
        assertTrue(o2.isMappedTo(o1));
    }

    /**
     * Check if the {@link DataObject} has all relevant attributes
     * captured by {@link ModeledObjectTestDescription} same as expected.
     * If the description doesn't match the tested object the method halts on assertion.
     * @param description Expected set of attributes to be check against tested object. Non-null.
     * @param tested The object under the test. Non-null.
     */
    private void assertModeledObjectMatchesDescription(ModeledObjectTestDescription description, DataObject tested) {
        assertEquals(description.getName(), tested.getName());
        assertEquals(description.getDefinition(), tested.getDefinition());
        assertEquals(description.getNote(), tested.getNote());
    }

    /* Methods for safe getting(in the way that returned objects are valid and non-null) of objects by descriptions */

    /**
     * Method for getting the only logical model that the {@link ErStudioSolution} under test contains.
     * If none is present the method halts on assertion.
     * @return The only logical model that the {@link ErStudioSolution} under test contains.
     */
    private LogicalDataModel getLogicalModel() {
        LogicalDataModel logicalModel = erStudioSolution.getLogicalModel();
        assertNotNull("The physical model could not be reached", logicalModel);
        return logicalModel;
    }

    /**
     * Method for getting a {@link PhysicalDataModel} based on its name. If the model cannot be obtained by the name,
     * the method halts on assertion.
     * @param modelName Name that is uniquely identifying the model
     * @return Valid {@link PhysicalDataModel} with the given modelName.
     */
    private PhysicalDataModel getPhysicalModel(String modelName) {
        PhysicalDataModel physicalModel = erStudioSolution.getPhysicalModel(modelName);
        assertNotNull("The physical model could not be reached", physicalModel);
        return physicalModel;
    }

    /**
     * Get {@link SimpleDataObject} based on its name and its name from the actual {@link CompositeDataObject} parent object.
     * @param parentObject Object that the described {@link SimpleDataObject} we look for belongs to. Non-null.
     * @param name Name of the result we look for. Non-null.
     * @return Valid described {@link SimpleDataObject} object.
     */
    private SimpleDataObject getSimpleObject(CompositeDataObject parentObject, String name) {
        assertNotNull(parentObject);
        SimpleDataObject result = parentObject.getSubObject(name); // Name is unique identifier in parent
        assertNotNull("Simple object that we want to test couldn't be reached", result);
        return result;
    }

    /**
     * Get Table based on its name and name of the model its stored in.
     * @param modelName Name of the {@link PhysicalDataModel} where we'll look for the result.
     * @param tableName Name of the Entity we look for. Non-null.
     * @return Valid described {@link CompositeDataObject} object.
     */
    private CompositeDataObject getTestedTable(String modelName, String tableName, String ownerName) {
        PhysicalDataModel physicalModel = getPhysicalModel(modelName);
        return getTestedCompositeObject(physicalModel, tableName, ownerName);
    }

    /**
     * Get Entity based on its name.
     * @param entityName Name of the Entity we look for in the logical model. Non-null
     * @return Valid described Entity represented by {@link CompositeDataObject}.
     */
    private CompositeDataObject getTestedEntity(String entityName, String ownerName) {
        LogicalDataModel logicalModel = getLogicalModel();
        return getTestedCompositeObject(logicalModel, entityName, ownerName);
    }

    /**
     * Getting {@link CompositeDataObject} by its searchedName in {@link DataModel} that the object belongs to.
     * If the object cannot be obtained by provided information, the method halts on assertion.
     * @param parentDataModel Model to search in. Non-null.
     * @param searchedName Name of the {@link CompositeDataObject} to search by in the model. Non-null.
     * @return Valid described {@link CompositeDataObject} object.
     */
    private CompositeDataObject getTestedCompositeObject(DataModel parentDataModel, String searchedName,
            String ownerName) {
        CompositeDataObject result = parentDataModel.getOwner(ownerName)
                .getCompositeObject(searchedName); // Name is unique identifier in model
        assertNotNull("Simple object that we want to test couldn't be reached", result);
        return result;
    }
}