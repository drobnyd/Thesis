package eu.profinit.manta.dataflow.generator.powerdesigner.graph;

import eu.profinit.manta.connector.common.connections.Connection;
import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.model.Layer;
import eu.profinit.manta.connector.powerdesigner.model.conceptual.ConceptualDataModel;
import eu.profinit.manta.connector.powerdesigner.model.logical.LogicalDataModel;
import eu.profinit.manta.dataflow.generator.modeling.common.connections.DatabaseConnector;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.DataModelNodeCreator;
import eu.profinit.manta.dataflow.generator.powerdesigner.graph.connections.ConnectionResolver;

/**
 * Factory choosing the right concrete GraphBuilder for data models
 *
 * @author ddrobny
 */
public class GraphBuilderFactory {
    /** Node creator needed for physical graph building. */
    private DatabaseConnector databaseConnector;
    /** Resolver finding corresponding database connections for physical data models. */
    private ConnectionResolver connectionResolver;
    /** Node creator needed for conceptual graph building. */
    private DataModelNodeCreator conceptualNodeCreator;
    /** Node creator needed for logical graph building. */
    private DataModelNodeCreator logicalNodeCreator;
    /** The ID of the system which the created data models belong to. */
    private String systemId;

    /**
     * Sets the database connector.
     * @param databaseConnector the database connector to set.
     */
    public void setDatabaseConnector(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    /**
     * Sets the connection resolver.
     * @param connectionResolver the connection resolver to set.
     */
    public void setConnectionResolver(ConnectionResolver connectionResolver) {
        this.connectionResolver = connectionResolver;
    }

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
     * Sets ID of the system.
     * @param systemId the ID to be set.
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * Gets the right graph builder for the given data model.
     * @param dataModel the data model to choose graph builder for.
     * @return the right graph builder for the given data model. If the data model is unknown {@code null}.
     */
    public GraphBuilder getGraphBuilder(DataModel dataModel) {
        GraphBuilder graphBuilder;

        switch (Layer.getLayer(dataModel.getFileName())) {

        case CONCEPTUAL:
            graphBuilder = new ModeledGraphBuilder<>(dataModel, conceptualNodeCreator,
                    ((ConceptualDataModel) dataModel).getCompositeModelObjects(), systemId);
            break;

        case LOGICAL:
            graphBuilder = new ModeledGraphBuilder<>(dataModel, logicalNodeCreator,
                    ((LogicalDataModel) dataModel).getCompositeModelObjects(), systemId);
            break;

        case PHYSICAL:
            Connection connection = connectionResolver.getConnection(dataModel.getName());
            graphBuilder = new PhysicalGraphBuilderImpl(dataModel, databaseConnector, connection);
            break;

        default:
            return null;
        }

        return graphBuilder;
    }
}
