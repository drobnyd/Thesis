package eu.profinit.manta.dataflow.generator.powerdesigner;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.DataModelNodeCreator;
import eu.profinit.manta.dataflow.generator.modelutils.AbstractGraphTask;
import eu.profinit.manta.dataflow.generator.powerdesigner.graph.GraphBuilder;
import eu.profinit.manta.dataflow.generator.powerdesigner.graph.GraphBuilderFactory;
import eu.profinit.manta.dataflow.model.Edge;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Task transforming a component of data models to corresponding graph representations.
 *
 * @author ddrobny
 */
public class PowerDesignerDataflowTask extends AbstractGraphTask<List<DataModel>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PowerDesignerDataflowTask.class);

    /** Node creator for conceptual objects. */
    private DataModelNodeCreator conceptualNodeCreator;
    /** Node creator for logical objects. */
    private DataModelNodeCreator logicalNodeCreator;
    /** Factory choosing a builder based on abstraction layer */
    private GraphBuilderFactory graphBuilderFactory;

    /**
     * Sets the conceptual node creator.
     * @param conceptualNodeCreator the node creator to set.
     */
    public void setConceptualNodeCreator(DataModelNodeCreator conceptualNodeCreator) {
        this.conceptualNodeCreator = conceptualNodeCreator;
    }

    /**
     * Sets the logical node creator.
     * @param logicalNodeCreator the node creator to set.
     */
    public void setLogicalNodeCreator(DataModelNodeCreator logicalNodeCreator) {
        this.logicalNodeCreator = logicalNodeCreator;
    }

    /**
     * Sets the graph builder factory.
     * @param graphBuilderFactory the factory to set.
     */
    public void setGraphBuilderFactory(GraphBuilderFactory graphBuilderFactory) {
        this.graphBuilderFactory = graphBuilderFactory;
    }

    @Override
    protected void doExecute(List<DataModel> dataModels, Graph outputGraph) {
        // Objects with their representations
        Map<String, Node> idToNode = new HashMap<>();
        // Node with references to which nodes should it be mapped
        Map<Node, List<String>> toBeMapped = new HashMap<>();

        for (DataModel dataModel : dataModels) {
            GraphBuilder graphBuilder = graphBuilderFactory.getGraphBuilder(dataModel);
            graphBuilder.buildGraph();
            outputGraph.merge(graphBuilder.getGraph());

            // Prepare for mappings
            idToNode.putAll(graphBuilder.getSimpleObjectNodesByObjectId());
            toBeMapped.putAll(graphBuilder.getNodesWithMappedIds());
        }

        resolveMappings(idToNode, toBeMapped, outputGraph);
    }

    private void resolveMappings(Map<String, Node> idToNode, Map<Node, List<String>> toBeMapped, Graph outputGraph) {
        // All the models are created, all links between objects should be possible to resolve
        for (Map.Entry<Node, List<String>> nodeWithMapped : toBeMapped.entrySet()) {
            Node node = nodeWithMapped.getKey();

            for (String id : nodeWithMapped.getValue()) {
                if (idToNode.containsKey(id)) {
                    // A mapping edge leads from a higher layer to lower.
                    if (getLayerPosition(node) > getLayerPosition(idToNode.get(id))) {
                        outputGraph.addEdge(node, idToNode.get(id), Edge.Type.MAPS_TO);
                    } else if (getLayerPosition(node) < getLayerPosition(idToNode.get(id))) {
                        outputGraph.addEdge(idToNode.get(id), node, Edge.Type.MAPS_TO);
                    }
                    // Otherwise NOP - mapping inside one level is not supported
                }
            }
        }
    }

    /**
     * Gets a numerical representation of an abstraction layer, the higher number the higher is the abstraction.
     * @param node node abstraction layer of which has to be evaluated.
     * @return a positive int with the layer number.
     */
    private int getLayerPosition(Node node) {
        if (node.getResource().getLayer().getType().equals(conceptualNodeCreator.getResource().getLayer().getType())) {
            return 3;
        } else if (node.getResource().getLayer().getType()
                .equals(logicalNodeCreator.getResource().getLayer().getType())) {
            return 2;
        } else if (node.getResource().getLayer().getType()
                .equals(eu.profinit.manta.dataflow.model.Layer.DEFAULT.getType())) {
            return 1;
        }
        LOGGER.error("Unknown type of resource layer type \"{}\".", node.getResource().getLayer().getType());
        return 0;
    }
}
