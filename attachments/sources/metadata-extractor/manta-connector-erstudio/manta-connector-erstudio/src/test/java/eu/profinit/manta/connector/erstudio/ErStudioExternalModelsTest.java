package eu.profinit.manta.connector.erstudio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.profinit.manta.connector.erstudio.model.CompositeDataObject;
import eu.profinit.manta.connector.erstudio.model.DataModel;
import eu.profinit.manta.connector.erstudio.model.ErStudioSolution;
import eu.profinit.manta.connector.erstudio.model.PhysicalDataModel;
import eu.profinit.manta.connector.erstudio.model.SimpleDataObject;
import eu.profinit.manta.connector.erstudio.model.impl.ErStudioFileModelImpl;
import eu.profinit.manta.connector.erstudio.model.impl.ErStudioSolutionImpl;
import eu.profinit.manta.connector.erstudio.model.impl.LogicalDataModelImpl;
import eu.profinit.manta.connector.erstudio.model.impl.PhysicalDataModelImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Basic testing of ER/Studio's external objects.
 *
 * @author ddrobny
 */
@RunWith(Parameterized.class)
public class ErStudioExternalModelsTest {
    private static final String INPUT_PATH = "src/test/resources/externalMappingTestInput/";
    /** File with expected properties of tested model */
    private static final String TEST_CONFIG_FILE = "test-properties.json";
    /** Subfolder of {@code INPUT_PATH} where tested models' source file are stored at */
    private static final String RELATIVE_MODEL_PATH = "models/";
    /** Location where is stored the .DM1 file that the tested {@link ErStudioSolution} will be loaded from */
    private final File modelLocation;
    List<ErStudioSolution> externalModels;
    private ErStudioFileModelImpl mainModel;

    public ErStudioExternalModelsTest(JsonNode nameToJsonField) throws IOException {
        ErStudioDm1Reader reader = new ErStudioDm1Reader();
        modelLocation = new File(INPUT_PATH + RELATIVE_MODEL_PATH + nameToJsonField.get("modelName").asText(null));
        mainModel = (ErStudioFileModelImpl) reader.readFile(modelLocation);
        externalModels = loadExternalModelsFromJson(nameToJsonField.get("externalModels"));
    }

    /**
     * Prepare arguments for this test class' constructor {@link ErStudioExternalModelsTest} where a single argument
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

    private List<ErStudioSolution> loadExternalModelsFromJson(JsonNode externalModelsArray) {
        // Load external models
        List<ErStudioSolution> externalModels = new ArrayList<>();

        if (!externalModelsArray.isArray()) {
            return externalModels;
        }

        for (JsonNode externalModelNode : externalModelsArray) {

            ErStudioSolutionImpl externalModel = new ErStudioSolutionImpl(externalModelNode.get("name").asText(null));
            List<DataModel> dataModels = new ArrayList<>();
            if (!externalModelNode.get("logicalModelName").isNull()) {
                externalModel.setLogicalModel(
                        new LogicalDataModelImpl(externalModelNode.get("logicalModelName").asText(null)));
                dataModels.add(externalModel.getLogicalModel());
            }
            if (externalModelNode.get("physicalModelName").isArray()) {
                for (JsonNode nameObj : externalModelNode.get("physicalModelName")) {
                    dataModels.add(new PhysicalDataModelImpl(nameObj.asText(null), null));
                }
            }
            externalModel.setModels(dataModels);
            externalModels.add(externalModel);
        }
        return externalModels;
    }

    @Test
    public void testMainModelLoad() {
        assertNotNull(mainModel);
    }

    @Test
    public void testExternalModelsCount() {
        assertEquals(mainModel.getAllExternalErStudioSolutions().size(), externalModels.size());
    }

    @Test
    public void testExternalModels() {
        for (ErStudioSolution prototype : externalModels) {
            ErStudioSolution testedExternalModel = mainModel.getExternalErStudioSolution(prototype.getFileName());
            assertNotNull(testedExternalModel);

            if (prototype.getLogicalModel() != null) {
                assertEquals(testedExternalModel.getLogicalModel().getName(), prototype.getLogicalModel().getName());
            }

            for (PhysicalDataModel physicalPrototype : prototype.getAllPhysicalModels()) {
                assertNotNull(testedExternalModel.getPhysicalModel(physicalPrototype.getName()));
            }
        }
    }

    /*
        All the following tested objects in external models should be included in the main model due to cross ErStudio
        files mapping, so when loaded correctly every one of them should return true on hasMappings method call.
    */

    @Test
    public void testColumnsExternalMappings() {
        for (ErStudioSolution externalErStudioSolution : mainModel.getAllExternalErStudioSolutions()) {
            for (SimpleDataObject column : externalErStudioSolution.getAllColumns()) {
                assertTrue(column.hasMappings());
            }
        }
    }

    @Test
    public void testAttributesExternalMappings() {
        for (ErStudioSolution externalErStudioSolution : mainModel.getAllExternalErStudioSolutions()) {
            for (SimpleDataObject attribute : externalErStudioSolution.getAllAttributes()) {
                assertTrue(attribute.hasMappings());
            }
        }
    }

    @Test
    public void testTablesExternalMappings() {
        for (ErStudioSolution externalErStudioSolution : mainModel.getAllExternalErStudioSolutions()) {
            for (CompositeDataObject table : externalErStudioSolution.getAllTables()) {
                assertTrue(table.hasMappings());
            }
        }
    }

    @Test
    public void testEntitiesExternalMappings() {
        for (ErStudioSolution externalErStudioSolution : mainModel.getAllExternalErStudioSolutions()) {
            for (CompositeDataObject entity : externalErStudioSolution.getAllTables()) {
                assertTrue(entity.hasMappings());
            }
        }
    }
}