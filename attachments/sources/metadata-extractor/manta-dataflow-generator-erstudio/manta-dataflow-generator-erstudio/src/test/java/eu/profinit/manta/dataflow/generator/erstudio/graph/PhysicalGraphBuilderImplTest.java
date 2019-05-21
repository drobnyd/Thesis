package eu.profinit.manta.dataflow.generator.erstudio.graph;

import eu.profinit.manta.commons.testutils.graphvis.GraphUtils;
import eu.profinit.manta.connector.common.connections.impl.ConnectionImpl;
import eu.profinit.manta.connector.erstudio.ErStudioDm1Reader;
import eu.profinit.manta.connector.erstudio.model.ErStudioFileModel;
import eu.profinit.manta.connector.erstudio.model.PhysicalDataModel;
import eu.profinit.manta.dataflow.generator.common.query.impl.DataflowQueryServiceImpl;
import eu.profinit.manta.dataflow.generator.modeling.common.connections.DatabaseConnector;
import eu.profinit.manta.dataflow.generator.modeling.common.connections.DatabaseConnectorImpl;
import eu.profinit.manta.dataflow.generator.modelutils.NodeCreatorImpl;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.impl.GraphImpl;
import eu.profinit.manta.dataflow.model.impl.ResourceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;

@RunWith(Parameterized.class)
public class PhysicalGraphBuilderImplTest extends GraphBuilderTestBase {
    private static final String OUTPUT_PATH = "target/test-classes/physical/output/";
    private static final String EXPECTED_OUTPUT_PATH = "src/test/resources/physical/expected/";

    public PhysicalGraphBuilderImplTest(File inputFile) {
        super(inputFile);
    }

    @Test
    public void buildPhysicalLayerGraphTest() throws IOException {
        ErStudioDm1Reader reader = new ErStudioDm1Reader();
        reader.setInputFile(inputFile);
        ErStudioFileModel model = reader.read();
        DatabaseConnector databaseConnector = new DatabaseConnectorImpl();
        databaseConnector.setNodeCreator(new NodeCreatorImpl());
        databaseConnector.setDataflowQueryService(new DataflowQueryServiceImpl());
        Graph graph = new GraphImpl(new ResourceImpl("DummyName", "DummyType", "DummyDesc"));
        for (PhysicalDataModel physicalModel : model.getInternalErStudioSolution().getAllPhysicalModels()) {
            PhysicalGraphBuilderImpl physicalGraphBuilder = new PhysicalGraphBuilderImpl(physicalModel,
                    databaseConnector);
            physicalGraphBuilder.setConnection(
                    new ConnectionImpl("DummyType", null, physicalModel.getName(), "databaseName", "schemaName",
                            "userName"));
            physicalGraphBuilder.buildGraph();
            graph.merge(physicalGraphBuilder.getGraph());
        }

        GraphUtils.printGraphToFile(graph, new File(OUTPUT_PATH, baseName + ".txt"));

        assertGraphEquals(graph, EXPECTED_OUTPUT_PATH);
    }
}