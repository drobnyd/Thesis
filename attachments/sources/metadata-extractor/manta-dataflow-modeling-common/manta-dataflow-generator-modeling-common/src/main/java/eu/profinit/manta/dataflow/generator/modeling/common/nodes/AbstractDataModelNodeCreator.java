package eu.profinit.manta.dataflow.generator.modeling.common.nodes;

import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;
import eu.profinit.manta.dataflow.model.NodeType;
import eu.profinit.manta.dataflow.model.Resource;

/**
 * Implementation of the DataModelNodeCreator methods that are not dependent on a specific level of a data model.
 *
 * @author ddrobny
 */
public abstract class AbstractDataModelNodeCreator implements DataModelNodeCreator {
    /** Resource that is used for creating nodes. */
    protected final Resource resource;

    AbstractDataModelNodeCreator(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Node createSystemNode(Graph graph, NodeMetadata system) {
        // TODO system is common - not divided to physical or logical
        Node node = graph.addNode(system.getName(), NodeType.LOGICAL_SYSTEM.getId(), null, resource);
        NodeExtensions.appendAttributes(node, system);
        return node;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

}
