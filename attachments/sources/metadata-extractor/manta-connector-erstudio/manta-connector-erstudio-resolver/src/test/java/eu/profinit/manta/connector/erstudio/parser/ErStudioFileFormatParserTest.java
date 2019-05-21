package eu.profinit.manta.connector.erstudio.parser;

import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;
import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ErStudioFileFormatParserTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErStudioFileFormatParserTest.class);
    private static final String INPUT_PATH = "src/test/resources/parserTestInput/";
    private static final ErStudioFileFormatParser parser = new ErStudioFileFormatParser("UTF-8");
    /** How many CSV tables is in correct .dm1 file format */
    private static final int TABLE_COUNT_IN_FILE = 143; // Some of the models have extra Entity_Size_and_Position table, what makes it 144

    private final File modelLocation;

    public ErStudioFileFormatParserTest(File modelLocation) {
        this.modelLocation = modelLocation;
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

    @Test
    public void getAllCsvTablesFromFileTest() {
        Map<String, CsvTable> tables = null;
        try {
            tables = parser.readFile(modelLocation);
            for (CsvTable table : tables.values()) {
                for (CsvRow row : table) {

                    assertEquals(table.getRowDefinition().getColumnCount(), row.getValueCount());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("The file does not exist");
        } catch (IOException e) {
            e.printStackTrace();
            fail("A problem with reading occurred");
        }
        assertNotNull(tables);
        if (tables.containsKey("Entity_Size_and_Position")) {
            assertEquals(tables.size(), TABLE_COUNT_IN_FILE + 1);
        } else { // Some of the models have extra Entity_Size_and_Position table
            assertEquals(tables.size(), TABLE_COUNT_IN_FILE);
        }
    }
}