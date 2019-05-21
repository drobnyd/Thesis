package eu.profinit.manta.dataflow.generator.erstudio;

import eu.profinit.manta.commons.testutils.graphvis.GraphUtils;
import eu.profinit.manta.connector.erstudio.ErStudioDm1Reader;
import eu.profinit.manta.connector.erstudio.model.ErStudioFileModel;
import eu.profinit.manta.dataflow.generator.erstudio.connections.DictionaryCreator;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Resource;
import eu.profinit.manta.dataflow.model.impl.GraphImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.BeforeClass;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ErStudioDataflowTaskTest extends DictionaryCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErStudioDataflowTaskTest.class);
    private static final String INPUT_PATH = "src/test/resources/models/";
    private static final String OUTPUT_PATH = "target/test-classes/logical-to-physical/test-system/output/";
    private static final String EXPECTED_OUTPUT_PATH = "src/test/resources/logical-to-physical/test-system/expected/";
    private final static String SPRING_CONFIG_PATH = "spring/erStudioFlowTest.xml";
    protected static Resource resource;
    private static ErStudioDataflowTask dataflowTask;
    private static ClassPathXmlApplicationContext springContext;
    private static DictionaryCreator dictionaryCreator = new DictionaryCreator();

    /** ER/Studio file under the test */
    private File inputFile;

    public ErStudioDataflowTaskTest(File inputFile) {
        this.inputFile = inputFile;
    }

    @Parameterized.Parameters
    public static Collection loadFileName() {
        File file = FileUtils.getFile(INPUT_PATH);

        if (!file.isDirectory()) {
            LOGGER.error("Path {} is not a directory.", INPUT_PATH);
            return Collections.emptyList();
        }

        if (!file.canRead()) {
            LOGGER.error("Input test directory {} cannot be read.", INPUT_PATH);
            return Collections.emptyList();
        }

        Collection<File> inputFiles = FileUtils.listFiles(file, new String[] { "DM1", "dm1" }, true);

        List<Object[]> result = new ArrayList<>(inputFiles.size());
        for (File inputFile : inputFiles) {
            result.add(new Object[] { inputFile });
        }
        return result;
    }

    @BeforeClass
    public static void init() {
        springContext = new ClassPathXmlApplicationContext(SPRING_CONFIG_PATH);
        dataflowTask = springContext.getBean(ErStudioDataflowTask.class);
        resource = springContext.getBean("erStudioResource", eu.profinit.manta.dataflow.model.impl.ResourceImpl.class);
        // Test the edge creation independently from technology.
        /*ParserServiceImpl postgreSqlParserService = springContext
                .getBean(eu.profinit.manta.connector.postgresql.resolver.service.ParserServiceImpl.class);
        dictionaryCreator.createPostgreSqlDictionary("psql1", postgreSqlParserService, SCRIPT_PATH);*/
    }

    @Test
    public void logicalToPhysicalMappingTest() throws IOException {
        ErStudioDm1Reader reader = new ErStudioDm1Reader();
        reader.setInputFile(inputFile);
        ErStudioFileModel erStudioModel = reader.read();

        Graph result = new GraphImpl(resource);
        dataflowTask.doExecute(erStudioModel, result);

        String inputBaseName = FilenameUtils.getBaseName(inputFile.getName());

        GraphUtils.printGraphToFile(result, FileUtils.getFile(OUTPUT_PATH, inputBaseName + ".txt"));

        assertMappingEdges(inputBaseName);
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
                                + " Skipping 'actual==expected' comparison", inputFile.getName());
                return;
            }

            LOGGER.info("Comparing actual result of processing to its expected form.", inputFile.getName());

            String expectedGraph = GraphUtils.normalizeEol(expectedResult);

            Stream<String> lines = Files.lines(Paths.get(OUTPUT_PATH, inputBaseName + ".txt"));

            // we are interested only in the mapping edges
            String actualGraph = lines.filter(line -> line.startsWith("MAPS_TO")).collect(Collectors.joining("\r\n"));

            lines.close();

            assertEquals(String.format("Actual graph does not match the expected graph for %s", inputFile.getName()),
                    expectedGraph, actualGraph);
        } else {
            LOGGER.info("Input {} has no expected result defined (file {} does not exists) and "
                            + "so no 'actual==expected' comparison will be done", inputFile.getName(),
                    expectedResultFile.getName());
        }
    }
}