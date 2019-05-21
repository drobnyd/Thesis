package eu.profinit.manta.dataflow.generator.powerdesigner.graph;

import eu.profinit.manta.commons.testutils.graphvis.GraphUtils;
import eu.profinit.manta.connector.common.connections.Connection;
import eu.profinit.manta.connector.common.connections.impl.ConnectionImpl;
import eu.profinit.manta.connector.powerdesigner.PowerDesignerXmlComponentReader;
import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.model.physical.PhysicalDataModel;
import eu.profinit.manta.dataflow.generator.common.query.DataflowQueryService;
import eu.profinit.manta.dataflow.generator.common.query.impl.DataflowQueryServiceImpl;
import eu.profinit.manta.dataflow.generator.modeling.common.connections.DatabaseConnector;
import eu.profinit.manta.dataflow.generator.modeling.common.connections.DatabaseConnectorImpl;
import eu.profinit.manta.dataflow.generator.modelutils.NodeCreator;
import eu.profinit.manta.dataflow.generator.modelutils.NodeCreatorImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests if graphs of physical data models were created correctly.
 *
 * @author ddrobny
 */
@RunWith(Parameterized.class)
public class PhysicalGraphBuilderTest extends GraphBuilderTestBase {
    private static final String OUTPUT_PATH = "target/test-classes/resources/physical/";
    private static final String EXPECTED_OUTPUT_PATH = "src/test/resources/physical/expected/";

    public PhysicalGraphBuilderTest(File inputFile) {
        super(inputFile);
    }

    @Parameterized.Parameters
    public static Collection loadFileName() {
        return collectFilesToTest(new String[] { "pdm" });
    }

    @Test
    public void buildPhysicalGraphTest() {
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
        if (dataModel instanceof PhysicalDataModel) {
            DatabaseConnector databaseConnector = new DatabaseConnectorImpl();
            NodeCreator nodeCreator = new NodeCreatorImpl();
            DataflowQueryService dataflowQueryService = new DataflowQueryServiceImpl();
            databaseConnector.setNodeCreator(nodeCreator);
            databaseConnector.setDataflowQueryService(dataflowQueryService);
            Connection connection = new ConnectionImpl("DummyType", null, dataModel.getName(), "databaseName",
                    "schemaName", "userName");
            PhysicalGraphBuilderImpl physicalGraphBuilder = new PhysicalGraphBuilderImpl(dataModel, databaseConnector,
                    connection);
            physicalGraphBuilder.buildGraph();
            GraphUtils.printGraphToFile(physicalGraphBuilder.getGraph(), new File(OUTPUT_PATH, baseName + ".txt"));

            assertGraphEquals(physicalGraphBuilder.getGraph(), EXPECTED_OUTPUT_PATH);
        } else {
            fail();
        }
    }
}
