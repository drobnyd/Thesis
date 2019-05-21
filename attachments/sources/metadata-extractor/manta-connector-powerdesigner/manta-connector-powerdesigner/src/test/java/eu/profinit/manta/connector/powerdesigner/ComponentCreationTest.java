package eu.profinit.manta.connector.powerdesigner;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests if logical groups of data model files are create correctly and if obsolete path are resolved accordingly.
 *
 * @author ddrobny
 */
public class ComponentCreationTest {
    private static String RESOURCES_PATH = "src/test/resources/";
    private static String COMPONENTS_DIR = "component-test/";
    private static String COMPONENTS_EXPECTED = "componentCountExpected.properties";
    private PowerDesignerXmlComponentReader reader;

    public ComponentCreationTest() {
        reader = new PowerDesignerXmlComponentReader();
    }

    @Test
    public void componentCountTest() {
        reader.setInputFile(new File(RESOURCES_PATH + COMPONENTS_DIR));
        int componentRead = 0;
        while (reader.canRead()) {
            reader.read();
            // Read returns one component after another while can read
            componentRead++;
        }
        try (InputStream input = new FileInputStream(RESOURCES_PATH + COMPONENTS_DIR + COMPONENTS_EXPECTED)) {
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            String expectedString = prop.getProperty("componentCount");
            assertEquals(Integer.parseInt(expectedString), componentRead);
        } catch (IOException ex) {
            fail("Property file with expectation was not loaded correctly.");
        }
    }
}
