package eu.profinit.manta.dataflow.generator.powerdesigner.graph;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.model.SimpleModelObject;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;
import eu.profinit.manta.dataflow.model.Resource;
import eu.profinit.manta.dataflow.model.impl.GraphImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base implementation of the translation of a general data model to its graph representation.
 *
 * @author ddrobny
 */
public abstract class AbstractGraphBuilder implements GraphBuilder {
    /** Data model which graph representation is being built. */
    protected DataModel dataModel;
    /** Resource specifying origin of the data model. */
    protected Resource resource;
    /** Output graph where Logical Model representation is stored */
    protected Graph graph;
    /** Pairs ObjectID with its node representation */
    protected Map<String, Node> idToSimpleObjectNode = new HashMap<>();
    /** Pairs node and all ObjectIDs that are mapped to the node. */
    protected Map<Node, List<String>> nodeToMappedIds = new HashMap<>();

    public AbstractGraphBuilder(DataModel dataModel, Resource resource) {
        this.dataModel = dataModel;
        this.resource = resource;
    }

    @Override
    public void buildGraph() {
        graph = new GraphImpl(resource);
    }

    @Override
    public Graph getGraph() {
        return graph;
    }

    @Override
    public Map<String, Node> getSimpleObjectNodesByObjectId() {
        return idToSimpleObjectNode;
    }

    @Override
    public Map<Node, List<String>> getNodesWithMappedIds() {
        return nodeToMappedIds;
    }

    /**
     * Registers node representation of the simple object that has been built,
     * so the node can be later found by the ObjectID of the attribute.
     *
     * Also marks the ObjectIDs of objects linked to the simple object, so the mapping can be fully reconstructed
     * once the mapped objects get translated to nodes.
     * Works only with simple objects as maps-to edges may lead only between them.
     *
     * @param simpleModelObject the simple object whose node registering.
     * @param simpleNode the node representation of the simpleModelObject.
     */
    protected void prepareForMapping(SimpleModelObject simpleModelObject, Node simpleNode) {
        // Expose the attribute's ObjectID so other objects may be mapped to it
        idToSimpleObjectNode.put(simpleModelObject.getObjectId(), simpleNode);

        if (simpleModelObject.hasMappings()) {
            // Link the node with all IDs mapped to it so their nodes can be found once they are created.
            List<String> mappedIds = new ArrayList<>();
            mappedIds.addAll(simpleModelObject.getAllMappedObjects());
            nodeToMappedIds.put(simpleNode, mappedIds);
        }
    }
}
