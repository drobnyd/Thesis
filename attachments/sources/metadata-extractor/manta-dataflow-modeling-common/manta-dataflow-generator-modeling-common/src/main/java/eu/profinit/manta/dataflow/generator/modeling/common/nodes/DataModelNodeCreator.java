package eu.profinit.manta.dataflow.generator.modeling.common.nodes;

import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;
import eu.profinit.manta.dataflow.model.Resource;

/**
 * Methods for creation nodes of objects from models of higher level of abstraction,
 * than the physical level - conceptual and logical.
 *
 * @author ddrobny
 */
public interface DataModelNodeCreator {

    /**
     * Creates a node representing the given system and appends it to the graph.
     * @param graph the graph to append the node to. Non-null.
     * @param system the system to be translated into node. #getName() non-empty.
     * @return a node representing the given system appended to the graph.
     */
    Node createSystemNode(Graph graph, NodeMetadata system);

    /**
     * Creates a node representing the given file and appends it to the graph.
     * @param graph the graph to append the node to. Non-null.
     * @param file the file to be translated into node. #getName() non-empty.
     * @param parent the preceding node of the result in the graph. {@code null} if a root should be created.
     * @return a node representing the given file appended to the graph.
     */
    Node createFileNode(Graph graph, NodeMetadata file, Node parent);

    /**
     * Creates a node representing the given model and appends it to the graph.
     * @param graph the graph to append the node to. Non-null.
     * @param model the model to be translated into node. #getName() non-empty.
     * @param parent the preceding node of the result in the graph. {@code null} if a root should be created.
     * @return a node representing the given model appended to the graph.
     */
    Node createDataModelNode(Graph graph, NodeMetadata model, Node parent);

    /**
     * Creates a node representing the given owner and appends it to the graph.
     * @param graph the graph to append the node to. Non-null.
     * @param owner the owner to be translated into node. #getName() non-empty.
     * @param parent the preceding node of the result in the graph. {@code null} if a root should be created.
     * @return a node representing the given owner appended it to the graph.
     */
    Node createOwnerNode(Graph graph, NodeMetadata owner, Node parent);

    /**
     * Creates a node representing the given entity and appends it to the graph.
     * @param graph the graph to append the node to. Non-null.
     * @param entity the entity to be translated into node. #getName() non-empty.
     * @param parent the preceding node of the result in the graph.
     * @return a node representing the given entity appended to the graph.
     */
    Node createEntityNode(Graph graph, NodeMetadata entity, Node parent);

    /**
     * Creates a node representing the given attribute and appends it to the graph.
     * @param graph the graph to append the node to. Non-null.
     * @param attribute the attribute to be translated into node. #getName() non-empty.
     * @param parent the preceding node of the result in the graph.
     * @return a node representing the given attribute appended to the graph.
     */
    Node createAttributeNode(Graph graph, NodeMetadata attribute, Node parent);

    /**
     * Gets the resource with which the instance creates all the nodes.
     * @return the resource with which the instance creates all the nodes.
     */
    Resource getResource();
}