package eu.profinit.manta.dataflow.generator.powerdesigner.graph;

import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;

import java.util.List;
import java.util.Map;

/**
 * Common logic of translating a data model to its graph representation.
 *
 * @author ddrobny
 */
public interface GraphBuilder {

    /**
     * Builds the graph representation.
     */
    void buildGraph();

    /**
     * Gets the built graph of a data model once the building took place.
     * @return the built graph of a data model.
     */
    Graph getGraph();

    /**
     * Gets pairs simple objects and their graph node representation, once the building took place.
     * @return pairs simple objects and their graph node representation.
     */
    Map<String, Node> getSimpleObjectNodesByObjectId();

    /**
     * Gets pairs graph node and ObjectIDs of the objects that are mapped to the node, once the building took place.
     * @return pairs graph node and ObjectIDs of the objects that are mapped to the node.
     */
    Map<Node, List<String>> getNodesWithMappedIds();
}
