package eu.profinit.manta.dataflow.generator.powerdesigner.graph;

import eu.profinit.manta.commons.testutils.graphvis.GraphUtils;
import eu.profinit.manta.connector.powerdesigner.PowerDesignerXmlComponentReader;
import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.model.logical.LogicalAttribute;
import eu.profinit.manta.connector.powerdesigner.model.logical.LogicalDataModel;
import eu.profinit.manta.connector.powerdesigner.model.logical.LogicalEntity;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.DataModelNodeCreator;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.LogicalDataModelNodeCreatorImpl;
import eu.profinit.manta.dataflow.model.Resource;
import eu.profinit.manta.dataflow.model.impl.ResourceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests if graphs of logical data models were created correctly.
 *
 * @author ddrobny
 */
@RunWith(Parameterized.class)
public class LogicalGraphBuilderTest extends GraphBuilderTestBase {
    private static final String OUTPUT_PATH = "target/test-classes/resources/logical/";
    private static final String EXPECTED_OUTPUT_PATH = "src/test/resources/logical/expected/";

    public LogicalGraphBuilderTest(File inputFile) {
        super(inputFile);
    }

    @Parameterized.Parameters
    public static Collection loadFileName() {
        return collectFilesToTest(new String[] { "ldm" });
    }

    @Test
    public void buildLogicalGraphTest() {
        PowerDesignerXmlComponentReader reader = new PowerDesignerXmlComponentReader();
        reader.setInputFile(inputFile);
        // Only the input file, no component needed.
        reader.setFilter(pathname -> pathname.equals(inputFile));
        List<DataModel> dataModels = null;
        if (reader.canRead()) {
            dataModels = reader.read();
        } else {
            fail();
        }
        assertTrue(dataModels.size() == 1);
        DataModel dataModel = dataModels.get(0);
        if (dataModel instanceof LogicalDataModel) {
            Resource resource = new ResourceImpl("PowerDesigner Logical", "Logical Dummy", "Logical Dummy Desc");
            DataModelNodeCreator dataModelNodeCreator = new LogicalDataModelNodeCreatorImpl(resource);
            ModeledGraphBuilder<LogicalEntity, LogicalAttribute> logicalGraphBuilder = new ModeledGraphBuilder<>(
                    dataModel, dataModelNodeCreator, ((LogicalDataModel) dataModel).getCompositeModelObjects(),
                    "Test System");
            logicalGraphBuilder.buildGraph();

            GraphUtils.printGraphToFile(logicalGraphBuilder.getGraph(), new File(OUTPUT_PATH, baseName + ".txt"));

            assertGraphEquals(logicalGraphBuilder.getGraph(), EXPECTED_OUTPUT_PATH);
        } else {
            fail();
        }
    }
}
