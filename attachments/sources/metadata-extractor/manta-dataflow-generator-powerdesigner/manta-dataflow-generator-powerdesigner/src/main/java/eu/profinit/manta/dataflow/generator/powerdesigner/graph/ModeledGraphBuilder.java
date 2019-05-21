package eu.profinit.manta.dataflow.generator.powerdesigner.graph;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.model.Entity;
import eu.profinit.manta.connector.powerdesigner.model.SimpleModelObject;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.DataModelNodeCreator;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.NodeMetadataImpl;
import eu.profinit.manta.dataflow.model.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * The common implementation of graph builder for conceptual and logical data models.
 * @param <E> type of entities.
 * @param <S> type of simple objects.
 *
 * @author ddrobny
 */
public class ModeledGraphBuilder<E extends Entity<E, S>, S extends SimpleModelObject> extends AbstractGraphBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModeledGraphBuilder.class);

    /** Graph nodes creator. */
    protected DataModelNodeCreator nodeCreator;
    /** Entities to be translated to nodes with their underlying objects. */
    private Collection<E> entities;
    /** ID of the system the nodes to be created belong to. */
    private String systemId;

    public ModeledGraphBuilder(DataModel dataModel, DataModelNodeCreator nodeCreator, Collection<E> entities,
            String systemId) {

        super(dataModel, nodeCreator.getResource());
        this.nodeCreator = nodeCreator;
        this.entities = entities;
        this.systemId = systemId;
    }

    @Override
    public void buildGraph() {
        super.buildGraph();

        Node systemNode = nodeCreator.createSystemNode(graph, new NodeMetadataImpl(systemId));
        Node fileNode = nodeCreator.createFileNode(graph, new NodeMetadataImpl(dataModel.getFileName()), systemNode);
        Node modelNode = nodeCreator.createDataModelNode(graph, dataModel, fileNode);

        LOGGER.info("Processing model \"{}\" from file \"{}\".", dataModel.getName(), dataModel.getFileName());

        for (E entity : entities) {
            Node entityNode = nodeCreator.createEntityNode(graph, entity, modelNode);

            LOGGER.debug("Created entity \"{}\".\"{}\".", dataModel.getName(), entity.getName());

            for (S attribute : entity.getSimpleModelObjects()) {
                Node attributeNode = nodeCreator.createAttributeNode(graph, attribute, entityNode);

                LOGGER.debug("Created attribute \"{}\".\"{}\".\"{}\".", dataModel.getName(), entity.getName(),
                        attribute.getName());

                prepareForMapping(attribute, attributeNode);
            }
        }
    }
}
