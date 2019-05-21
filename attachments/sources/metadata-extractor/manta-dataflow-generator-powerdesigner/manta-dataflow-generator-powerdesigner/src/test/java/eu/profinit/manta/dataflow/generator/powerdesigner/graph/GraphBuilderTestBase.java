package eu.profinit.manta.dataflow.generator.powerdesigner.graph;

import eu.profinit.manta.commons.testutils.graphvis.GraphUtils;
import eu.profinit.manta.dataflow.model.Graph;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Base functionality for testing classes.
 *
 * @author ddrobny
 */
public abstract class GraphBuilderTestBase {
    /** Where all the input data models for testing are stored. */
    protected static final String INPUT_PATH = "src/test/resources/models/";
    protected static Logger LOGGER = LoggerFactory.getLogger(GraphBuilderTestBase.class);
    protected File inputFile;
    protected String baseName;

    public GraphBuilderTestBase(File inputFile) {
        this.inputFile = inputFile;
        baseName = inputFile.getName().substring(0, inputFile.getName().length() - 4);
    }

    protected static Collection collectFilesToTest(String[] extensions) {
        File file = FileUtils.getFile(INPUT_PATH);

        if (!file.isDirectory()) {
            LOGGER.error("Path {} is not a directory.", INPUT_PATH);
            return Collections.emptyList();
        }

        if (!file.canRead()) {
            LOGGER.error("Input test directory {} cannot be read.", INPUT_PATH);
            return Collections.emptyList();
        }

        Collection<File> inputFiles = FileUtils.listFiles(file, extensions, true);

        List<Object[]> result = new ArrayList<>(inputFiles.size());
        for (File inputFile : inputFiles) {
            result.add(new Object[] { inputFile });
        }
        return result;
    }

    public void assertGraphEquals(Graph graph, String EXPECTED_INPUT_PATH) {
        File expectedResultFile = FileUtils.getFile(EXPECTED_INPUT_PATH, baseName + ".txt");
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

            String actualGraph = GraphUtils.normalizeEol(GraphUtils.normalizeEol(GraphUtils.toString(graph)));

            assertEquals(String.format("Actual graph does not match the expected graph for %s", inputFile.getName()),
                    expectedGraph, actualGraph);
        } else {
            LOGGER.info("Input {} has no expected result defined (file {} does not exists) and "
                            + "so no 'actual==expected' comparison will be done", inputFile.getName(),
                    expectedResultFile.getName());
        }
    }
}
