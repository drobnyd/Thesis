package eu.profinit.manta.dataflow.generator.erstudio.graph;

import eu.profinit.manta.commons.testutils.graphvis.GraphUtils;
import eu.profinit.manta.connector.erstudio.ErStudioDm1Reader;
import eu.profinit.manta.connector.erstudio.model.ErStudioFileModel;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Resource;
import eu.profinit.manta.dataflow.model.impl.LayerImpl;
import eu.profinit.manta.dataflow.model.impl.ResourceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;

@RunWith(Parameterized.class)
public class LogicalGraphBuilderImplTest extends GraphBuilderTestBase {
    private static final String OUTPUT_PATH = "target/test-classes/logical/output/";
    private static final String EXPECTED_OUTPUT_PATH = "src/test/resources/logical/expected/";

    public LogicalGraphBuilderImplTest(File inputFile) {
        super(inputFile);
    }

    @Test
    public void buildLogicalLayerGraphTest() throws IOException {
        ErStudioDm1Reader reader = new ErStudioDm1Reader();
        reader.setInputFile(inputFile);
        ErStudioFileModel model = reader.read();
        Resource resource = new ResourceImpl("ErStudio", "ErStudio", "Logical Layer coming from ErStudio",
                new LayerImpl("Logical", "Logical"));
        LogicalGraphBuilderImpl graphBuilder = new LogicalGraphBuilderImpl(model.getInternalErStudioSolution(),
                "TestSystem", resource);
        graphBuilder.buildGraph();
        Graph graph = graphBuilder.getGraph();
        GraphUtils.printGraphToFile(graph, new File(OUTPUT_PATH, baseName + ".txt"));

        assertGraphEquals(graph, EXPECTED_OUTPUT_PATH);
    }
}