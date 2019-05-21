package eu.profinit.manta.dataflow.generator.modeling.common.nodes;

import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;
import eu.profinit.manta.dataflow.model.NodeType;
import eu.profinit.manta.dataflow.model.Resource;

/**
 * Implementation of DataModelNodeCreator interface for creating objects of conceptual data models.
 *
 * @author ddrobny
 */
public class ConceptualNodeCreatorImpl extends AbstractDataModelNodeCreator implements DataModelNodeCreator {

    public ConceptualNodeCreatorImpl(Resource resource) {
        super(resource);
    }

    @Override
    public Node createFileNode(Graph graph, NodeMetadata file, Node parent) {
        Node node = graph.addNode(file.getName(), NodeType.CONCEPTUAL_MODELS_FILE.getId(), parent, resource);
        NodeExtensions.appendAttributes(node, file);
        return node;
    }

    @Override
    public Node createDataModelNode(Graph graph, NodeMetadata model, Node parent) {
        Node node = graph.addNode(model.getName(), NodeType.CONCEPTUAL_MODEL.getId(), parent, resource);
        NodeExtensions.appendAttributes(node, model);
        return node;
    }

    @Override
    public Node createOwnerNode(Graph graph, NodeMetadata owner, Node parent) {
        Node node = graph.addNode(owner.getName(), NodeType.CONCEPTUAL_OWNER.getId(), parent, resource);
        NodeExtensions.appendAttributes(node, owner);
        return node;
    }

    @Override
    public Node createEntityNode(Graph graph, NodeMetadata entity, Node parent) {
        Node node = graph.addNode(entity.getName(), NodeType.CONCEPTUAL_ENTITY.getId(), parent, resource);
        NodeExtensions.appendAttributes(node, entity);
        return node;
    }

    @Override
    public Node createAttributeNode(Graph graph, NodeMetadata attribute, Node parent) {
        Node node = graph.addNode(attribute.getName(), NodeType.CONCEPTUAL_ATTRIBUTE.getId(), parent, resource);
        NodeExtensions.appendAttributes(node, attribute);
        return node;
    }

}
