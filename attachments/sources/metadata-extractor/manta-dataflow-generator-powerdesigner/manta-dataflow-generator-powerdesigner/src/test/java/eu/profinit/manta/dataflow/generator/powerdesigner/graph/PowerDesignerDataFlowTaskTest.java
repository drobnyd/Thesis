package eu.profinit.manta.dataflow.generator.powerdesigner.graph;

import eu.profinit.manta.commons.testutils.graphvis.GraphUtils;
import eu.profinit.manta.connector.powerdesigner.PowerDesignerXmlComponentReader;
import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.dataflow.generator.powerdesigner.PowerDesignerDataflowTask;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Resource;
import eu.profinit.manta.dataflow.model.impl.GraphImpl;
import eu.profinit.manta.dataflow.model.impl.ResourceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Tests if mapping edges between data models were created correctly.
 *
 * @author ddrobny
 */
@RunWith(Parameterized.class)
public class PowerDesignerDataFlowTaskTest {
    protected static final String INPUT_PATH = "src/test/resources/models/";
    private static final String OUTPUT_PATH = "target/test-classes/resources/mappings/";
    private static final String EXPECTED_OUTPUT_PATH = "src/test/resources/mappings/expected/";
    private final static String SPRING_CONFIG_PATH = "spring/powerDesignerFlowTest.xml";
    private static final Logger LOGGER = LoggerFactory.getLogger(PowerDesignerDataFlowTaskTest.class);
    private static PowerDesignerDataflowTask dataflowTask;
    private static ClassPathXmlApplicationContext springContext;
    private static Resource resource;
    private static PowerDesignerXmlComponentReader powerDesignerXmlComponentReader;
    private List<DataModel> testedComponent;
    private String componentTestName;

    public PowerDesignerDataFlowTaskTest(List<DataModel> testedComponent, String componentTestName) {
        this.testedComponent = testedComponent;
        this.componentTestName = componentTestName;
    }

    @Parameterized.Parameters
    public static Collection loadComponents() {
        springContext = new ClassPathXmlApplicationContext(SPRING_CONFIG_PATH);
        dataflowTask = springContext.getBean(PowerDesignerDataflowTask.class);
        resource = new ResourceImpl("PowerDesigner", "PowerDesigner", "PowerDesigner test desc");
        powerDesignerXmlComponentReader = new PowerDesignerXmlComponentReader();
        powerDesignerXmlComponentReader.setInputFile(new File(INPUT_PATH));

        List<Object[]> result = new ArrayList<>();

        while (powerDesignerXmlComponentReader.canRead()) {
            List<DataModel> component = powerDesignerXmlComponentReader.read();
            // Create a name for a component that is can not vary across different runs of the test
            String componentName = powerDesignerXmlComponentReader.getInputName().
                    replace(".", "_").replace(",", "_").replace(" ", "");

            result.add(new Object[] { component, componentName });
        }

        return result;
    }

    @Test
    public void mappingEdgesTest() throws IOException {
        Graph graph = new GraphImpl(resource);
        dataflowTask.execute(testedComponent, graph);

        GraphUtils.printGraphToFile(graph, FileUtils.getFile(OUTPUT_PATH, componentTestName + ".txt"));
        assertMappingEdges(componentTestName);
    }

    private void assertMappingEdges(String inputBaseName) throws IOException {
        // Compare mapping edges
        File expectedResultFile = FileUtils.getFile(EXPECTED_OUTPUT_PATH, inputBaseName + ".txt");
        if (expectedResultFile.exists()) {
            String expectedResult;
            try {
                expectedResult = FileUtils.readFileToString(expectedResultFile);
            } catch (IOException e) {
                LOGGER.warn(
                        "The file with expected result for input {} was found, but there was an error during its reading."
                                + " Skipping 'actual==expected' comparison", inputBaseName);
                return;
            }

            LOGGER.info("Comparing actual result of processing to its expected form.", inputBaseName);

            String expectedEdges = GraphUtils.normalizeEol(expectedResult);

            Stream<String> lines = Files.lines(Paths.get(OUTPUT_PATH, inputBaseName + ".txt"));

            // Filer the mapping edges
            String actualEdges = lines.filter(line -> line.startsWith("MAPS_TO")).collect(Collectors.joining("\r\n"));

            lines.close();

            assertEquals(String.format("Actual graph does not match the expected graph for %s", inputBaseName),
                    expectedEdges, actualEdges);
        } else {
            LOGGER.info("Input {} has no expected result defined (file {} does not exists) and "
                    + "so no 'actual==expected' comparison will be done", inputBaseName, expectedResultFile.getName());
        }
    }
}