package eu.profinit.manta.connector.powerdesigner;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Tests loading data models from input.
 *
 * @author ddrobny
 */
@RunWith(Parameterized.class)
public class PowerDesignerXmlComponentReaderTest {
    private static Logger LOGGER = LoggerFactory.getLogger(PowerDesignerXmlComponentReaderTest.class);
    private static String INPUT_PATH = "src/test/resources/models/";
    private static String OUTPUT_PATH = "target/test-classes/model-test/";
    private static String EXPECTED_PATH = "src/test/resources/model-test/expected/";
    private PowerDesignerXmlComponentReader reader;
    private File inputModel;
    private String testName;

    public PowerDesignerXmlComponentReaderTest(File inputModel) {
        reader = new PowerDesignerXmlComponentReader();
        this.inputModel = inputModel;
        this.testName = inputModel.getName().replace(".", "_");
    }

    @Parameterized.Parameters
    public static Collection collectFilesToTest() {
        File file = FileUtils.getFile(INPUT_PATH);

        if (!file.isDirectory()) {
            LOGGER.error("Path {} is not a directory.", INPUT_PATH);
            return Collections.emptyList();
        }

        if (!file.canRead()) {
            LOGGER.error("Input test directory {} cannot be read.", INPUT_PATH);
            return Collections.emptyList();
        }

        Collection<File> inputFiles = FileUtils.listFiles(file, new String[] { "pdm", "ldm", "cdm" }, true);

        List<Object[]> result = new ArrayList<>(inputFiles.size());
        for (File inputFile : inputFiles) {
            result.add(new Object[] { inputFile });
        }
        return result;
    }

    @Test
    public void readFile() throws IOException {

        DataModel tested = reader.readFile(inputModel);

        ObjectMapper mapper = new ObjectMapper();
        File result = new File(OUTPUT_PATH + testName + ".json");
        mapper.writeValue(result, tested);

        File expected = new File(EXPECTED_PATH + testName + ".json");

        assertTrue(FileUtils.contentEquals(result, expected));
    }

}