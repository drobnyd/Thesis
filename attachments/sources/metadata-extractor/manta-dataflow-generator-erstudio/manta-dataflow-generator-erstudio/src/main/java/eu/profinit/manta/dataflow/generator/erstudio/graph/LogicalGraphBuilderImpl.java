package eu.profinit.manta.dataflow.generator.erstudio.graph;

import eu.profinit.manta.connector.erstudio.model.CompositeDataObject;
import eu.profinit.manta.connector.erstudio.model.ErStudioSolution;
import eu.profinit.manta.connector.erstudio.model.LogicalDataModel;
import eu.profinit.manta.connector.erstudio.model.Owner;
import eu.profinit.manta.connector.erstudio.model.SimpleDataObject;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.DataModelNodeCreator;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.LogicalDataModelNodeCreatorImpl;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.NodeMetadata;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.NodeMetadataImpl;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;
import eu.profinit.manta.dataflow.model.Resource;
import eu.profinit.manta.dataflow.model.impl.GraphImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete graph builder out of a logical model.
 *
 * @author ddrobny
 */
public class LogicalGraphBuilderImpl implements LogicalGraphBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogicalGraphBuilderImpl.class);
    /** Fallback for entities that do not belong to any owner */
    private static final String DEFAULT_OWNER = "NAMELESS";
    /** ErStudioModel whose Logical Model is being created */
    private ErStudioSolution erStudioSolution;
    /** Builder of {@link Graph} representation for ER/Studio Logical Model's  objects */
    private DataModelNodeCreator nodeCreator;
    /** Resource for ER/Studio Logical Model's {@link Graph} components */
    private Resource resource;
    /** Output graph where Logical Model representation is stored */
    private Graph graph;
    /** Nodes that are origins of {@link eu.profinit.manta.dataflow.model.Edge.Type#MAPS_TO} edge */
    private Map<SimpleDataObject, Node> attributeToNode;
    private String systemId;

    public LogicalGraphBuilderImpl(ErStudioSolution erStudioSolution, String systemId, Resource resource) {
        this.erStudioSolution = erStudioSolution;
        this.systemId = systemId;
        this.resource = resource;
        this.nodeCreator = new LogicalDataModelNodeCreatorImpl(resource);
        this.attributeToNode = new HashMap<>();
    }

    /**
     * Builds {@link Graph} from the Logical Model. After construction can be accessed via {@link #getGraph()}
     */
    @Override
    public void buildGraph() {
        graph = new GraphImpl(resource);

        // Create root
        Node systemNode = nodeCreator.createSystemNode(graph, new NodeMetadataImpl(systemId));

        Node erStudioFileNode = nodeCreator
                .createFileNode(graph, new NodeMetadataImpl(erStudioSolution.getFileName()), systemNode);

        LogicalDataModel logicalModel = erStudioSolution.getLogicalModel();
        // Omit the logical model creation
        // In case of external models the logical model may not exist
        if (logicalModel == null) {
            return;
        }

        LOGGER.info("Processing logical model \"{}\".", logicalModel.getName());

        for (Owner owner : logicalModel.getOwners()) {
            NodeMetadata ownerFinal =
                    owner.getName() != null ? owner : new NodeMetadataImpl(DEFAULT_OWNER, owner.getMetadata());

            Node ownerNode = nodeCreator.createOwnerNode(graph, ownerFinal, erStudioFileNode);

            for (CompositeDataObject entity : owner.getAllCompositeObjects()) {
                Node entityNode = nodeCreator.createEntityNode(graph, entity, ownerNode);

                LOGGER.debug("Created entity \"{}\".\"{}\".\"{}\".", logicalModel.getName(), owner.getName(),
                        entity.getName());

                for (SimpleDataObject attribute : entity.getAllSubObjects()) {
                    Node attributeNode = nodeCreator.createAttributeNode(graph, attribute, entityNode);

                    LOGGER.debug("Created attribute \"{}\".\"{}\".\"{}\".\"{}\".", logicalModel.getName(),
                            owner.getName(), entity.getName(), attribute.getName());

                    if (attribute.hasMappings()) { // Add attributes that will be mapped to a column
                        attributeToNode.put(attribute, attributeNode);
                    }
                }
            }
        }

    }

    /**
     * Accesses {@link Graph} that has been created by {@link #buildGraph()}.
     * Method {@link #buildGraph()} must be called
     * before. If it wasn't no {@link Graph} to be returned exists.
     * @return {@link Graph} representation of the Logical Model. If it wasn't built before, null is returned.
     */
    @Override
    public Graph getGraph() {
        return graph;
    }

    /**
     * Gets pairs of every logical attribute represented as {@link SimpleDataObject} from {@link LogicalDataModel}
     * and its actual {@link Node} representation in created {@link Graph}.
     * Method {@link #buildGraph()} must be called
     * before. If it wasn't no {@link Graph} to be returned exists.
     * @return Pairs of every logical {@link SimpleDataObject} from {@link LogicalDataModel}
     * and its actual {@link Node} representation in created {@link Graph}. If the {@link Graph} wasn't built before, null is returned.
     */
    @Override
    public Map<SimpleDataObject, Node> getMappedAttributes() {
        return attributeToNode;
    }

}
