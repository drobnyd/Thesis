package eu.profinit.manta.dataflow.generator.erstudio.graph;

import eu.profinit.manta.connector.common.connections.Connection;
import eu.profinit.manta.connector.erstudio.model.AbstractionLayer;
import eu.profinit.manta.connector.erstudio.model.CompositeDataObject;
import eu.profinit.manta.connector.erstudio.model.DataModel;
import eu.profinit.manta.connector.erstudio.model.DataObject;
import eu.profinit.manta.connector.erstudio.model.Owner;
import eu.profinit.manta.connector.erstudio.model.PhysicalDataModel;
import eu.profinit.manta.connector.erstudio.model.SimpleDataObject;
import eu.profinit.manta.dataflow.generator.modeling.common.connections.DatabaseConnector;
import eu.profinit.manta.dataflow.model.Edge;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;
import eu.profinit.manta.dataflow.model.impl.GraphImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete graph builder out of a physical model.
 *
 * @author ddrobny
 */
public class PhysicalGraphBuilderImpl implements PhysicalGraphBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhysicalGraphBuilderImpl.class);

    /** Logical Model's attributes to be linked with the physical's ones */
    private Map<SimpleDataObject, Node> attributes;
    /** Referential Physical Model*/
    private PhysicalDataModel physicalModel;
    /** Connection to the database where {@code physicalModel} should be deployed */
    private Connection connection;
    /** Connector for getting objects from a deployed database */
    private DatabaseConnector databaseConnector;
    /** Output graph */
    private GraphImpl graph;

    public PhysicalGraphBuilderImpl(PhysicalDataModel physicalModel, DatabaseConnector connector) {
        this.physicalModel = physicalModel;
        this.databaseConnector = connector;
        this.attributes = new HashMap<>();
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void setMappedAttributes(Map<SimpleDataObject, Node> attributeToNode) {
        this.attributes = attributeToNode;
    }

    /**
     * Builds {@link Graph} from a {@link DataModel}.
     * After construction can be accessed via {@link #getGraph()}
     */
    @Override
    public void buildGraph() {
        LOGGER.info("Processing physical model \"{}\".", physicalModel.getName());
        graph = new GraphImpl(databaseConnector.getDatabaseResource(connection));
        for (Owner schema : physicalModel.getOwners()) {
            proceedTablesCreation(schema);
        }
    }

    /**
     * Creates {@link Graph} representation of every table that belongs to {@code schema} and appends it to {@code graph}.
     * Tries to obtain the tables from database dictionary by {@code databaseConnector} and {@code connection}
     * if that is not possible table is deduced.
     * @param schema schema whose tables will be processed.
     */
    private void proceedTablesCreation(Owner schema) {

        for (CompositeDataObject table : schema.getAllCompositeObjects()) {
            // Database name is always set from connection
            Node tableNode = databaseConnector.createTableNode(connection, table, null, schema.getName(), graph);

            if (tableNode == null) {
                LOGGER.debug("Unable to reach table \"{}\".\"{}\".\"{}\" in dictionary, deducing it.",
                        physicalModel.getName(), schema.getName(), table.getName());

                tableNode = databaseConnector.deduceTableNode(connection, table, null, schema.getName(), graph);
            } else {
                LOGGER.debug("Obtained table \"{}\".\"{}\".\"{}\" from dictionary.", physicalModel.getName(),
                        schema.getName(), table.getName());
            }

            if (tableNode == null) {
                LOGGER.info("Unable to deduce table \"{}\".\"{}\".\"{}\". It won't be included in output.",
                        physicalModel.getName(), schema.getName(), table.getName());
                return;
            }

            proceedColumnsCreation(table, tableNode);
        }
    }

    /**
     * Creates {@link Graph} representation of every column that belongs to {@code table} and appends it to {@code graph}.
     * Tries to obtain the columns from database dictionary by {@code databaseConnector} and {@code connection}
     * if that is not possible column is deduced.
     * @param table table whose columns will be processed.
     * @param tableNode {@link Graph} representation of {@code table}.
     */
    private void proceedColumnsCreation(CompositeDataObject table, Node tableNode) {
        for (SimpleDataObject column : table.getAllSubObjects()) {
            Node columnNode = databaseConnector.createColumnNode(connection, column, tableNode, graph);

            if (columnNode == null) {
                LOGGER.debug("Unable to reach column \"{}\".\"{}\".\"{}\".\"{}\" in dictionary, deducing it.",
                        physicalModel.getName(), tableNode.getParent().getName(), table.getName(), column.getName());

                columnNode = databaseConnector.deduceColumnNode(connection, column, tableNode, graph);
            } else {
                LOGGER.debug("Obtained column \"{}\".\"{}\".\"{}\".\"{}\" from dictionary.", column.getName(),
                        physicalModel.getName(), tableNode.getParent().getName(), table.getName(), column.getName());
            }

            if (column.hasMappings()) {
                mapToAttributes(column, columnNode);
            }
        }
    }

    private void mapToAttributes(SimpleDataObject column, Node columnNode) {
        // Iterate through logical attributes that are mapped to the column
        for (DataObject attribute : column.getAllMappedObjects()) {
            // Make sure it really is an attribute
            if (attribute.getLayer() != AbstractionLayer.LOGICAL) {
                LOGGER.debug("Column \"{}\".\"{}\".\"{}\".\"{}\" won't be mapped to the object \"{}\". Manta supports "
                                + "only mapping between Logical and Physical layer.", physicalModel.getName(),
                        columnNode.getParent().getParent().getName(), columnNode.getParent().getName(),
                        column.getName(), attribute.getName());
                return;
            }

            // Obtain Node representation of the attribute
            Node attributeNode = attributes.get(attribute);

            if (attributeNode == null) {
                LOGGER.debug(
                        "Column \"{}\".\"{}\".\"{}\".\"{}\" could not be mapped to the attribute \"{}\". The attribute"
                                + " could not be reached.", physicalModel.getName(),
                        columnNode.getParent().getParent().getName(), columnNode.getParent().getName(),
                        column.getName(), attribute.getName());

                return;
            }

            graph.merge(attributeNode);
            graph.addEdge(attributeNode, columnNode, Edge.Type.MAPS_TO);
        }
    }

    /**
     * Accesses {@link Graph} that has been created by {@link #buildGraph()}.
     * Method {@link #buildGraph()} must be called before. If it wasn't no {@link Graph} to be returned exists.
     * @return {@link Graph} representation of a {@link DataModel}.
     * Method {@link #buildGraph()} must be called before. If it wasn't built before, null is returned.
     */
    @Override
    public Graph getGraph() {
        return graph;
    }

}
