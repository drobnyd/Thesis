package eu.profinit.manta.dataflow.generator.powerdesigner.graph;

import eu.profinit.manta.commons.testutils.graphvis.GraphUtils;
import eu.profinit.manta.connector.powerdesigner.PowerDesignerXmlComponentReader;
import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.model.conceptual.ConceptualAttribute;
import eu.profinit.manta.connector.powerdesigner.model.conceptual.ConceptualDataModel;
import eu.profinit.manta.connector.powerdesigner.model.conceptual.ConceptualEntity;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.ConceptualNodeCreatorImpl;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.DataModelNodeCreator;
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
 * Tests if graphs of conceptual data models were created correctly.
 *
 * @author ddrobny
 */
@RunWith(Parameterized.class)
public class ConceptualGraphBuilderTest extends GraphBuilderTestBase {
    private static final String OUTPUT_PATH = "target/test-classes/resources/conceptual/";
    private static final String EXPECTED_OUTPUT_PATH = "src/test/resources/conceptual/expected/";

    public ConceptualGraphBuilderTest(File inputFile) {
        super(inputFile);
    }

    @Parameterized.Parameters
    public static Collection loadFileName() {
        return collectFilesToTest(new String[] { "cdm" });
    }

    @Test
    public void buildConceptualGraphTest() {
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
        if (dataModel instanceof ConceptualDataModel) {
            Resource resource = new ResourceImpl("PowerDesigner Conceptual", "Conceptual Dummy", "Logical Dummy Desc");
            DataModelNodeCreator dataModelNodeCreator = new ConceptualNodeCreatorImpl(resource);
            ModeledGraphBuilder<ConceptualEntity, ConceptualAttribute> logicalGraphBuilder = new ModeledGraphBuilder<>(
                    dataModel, dataModelNodeCreator, ((ConceptualDataModel) dataModel).getCompositeModelObjects(),
                    "Test System");
            logicalGraphBuilder.buildGraph();

            GraphUtils.printGraphToFile(logicalGraphBuilder.getGraph(), new File(OUTPUT_PATH, baseName + ".txt"));

            assertGraphEquals(logicalGraphBuilder.getGraph(), EXPECTED_OUTPUT_PATH);
        } else {
            fail();
        }
    }
}
