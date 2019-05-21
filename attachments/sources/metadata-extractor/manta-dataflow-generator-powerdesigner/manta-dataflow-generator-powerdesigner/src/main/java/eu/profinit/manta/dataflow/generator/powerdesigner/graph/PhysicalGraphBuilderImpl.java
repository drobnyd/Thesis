package eu.profinit.manta.dataflow.generator.powerdesigner.graph;

import eu.profinit.manta.connector.common.connections.Connection;
import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.model.SimpleModelObject;
import eu.profinit.manta.connector.powerdesigner.model.physical.PhysicalDataModel;
import eu.profinit.manta.connector.powerdesigner.model.physical.PhysicalSchema;
import eu.profinit.manta.connector.powerdesigner.model.physical.PhysicalTable;
import eu.profinit.manta.dataflow.generator.modeling.common.connections.DatabaseConnector;
import eu.profinit.manta.dataflow.model.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of graph builder that translates physical data models to graph nodes.
 *
 * @author ddrobny
 */
public class PhysicalGraphBuilderImpl extends AbstractGraphBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhysicalGraphBuilderImpl.class);

    /** Node creator trying to fetch modeled objects primarily from database dictionaries. */
    private DatabaseConnector databaseConnector;
    /** Details about the database that the physical model resembles. */
    private Connection connection;

    public PhysicalGraphBuilderImpl(DataModel dataModel, DatabaseConnector databaseConnector, Connection connection) {
        super(dataModel, databaseConnector.getDatabaseResource(connection));
        this.databaseConnector = databaseConnector;
        this.connection = connection;
    }

    @Override
    public void buildGraph() {
        super.buildGraph();

        LOGGER.info("Processing model \"{}\" from file \"{}\".", dataModel.getName(), dataModel.getFileName());

        for (PhysicalSchema schema : ((PhysicalDataModel) dataModel).getAllSchemas()) {

            for (PhysicalTable table : schema.getCompositeModelObjects()) {
                // Database name is always set from a connection
                Node tableNode = databaseConnector.createTableNode(connection, table, null, schema.getName(), graph);

                // No corresponding database table found in the database dictionary
                if (tableNode == null) {
                    LOGGER.debug("Unable to reach table \"{}\".\"{}\".\"{}\" in dictionary, deducing it.",
                            dataModel.getName(), schema.getName(), table.getName());

                    tableNode = databaseConnector.deduceTableNode(connection, table, null, schema.getName(), graph);

                } else {
                    LOGGER.debug("Obtained table \"{}\".\"{}\".\"{}\" from dictionary.", dataModel.getName(),
                            schema.getName(), table.getName());
                }

                if (tableNode == null) {
                    LOGGER.info("Unable to deduce table \"{}\".\"{}\".\"{}\". It won't be included in output.",
                            dataModel.getName(), schema.getName(), table.getName());
                    return;
                }

                proceedColumnsCreation(table, tableNode);
            }
        }

    }

    /**
     * Creates node representation of every column that belongs to the given table and appends it to output graph.
     * Tries to obtain the columns from database dictionary by, if that is not possible column is deduced.
     * @param table table whose columns will be processed.
     * @param tableNode representation of the table.
     */
    private void proceedColumnsCreation(PhysicalTable table, Node tableNode) {
        for (SimpleModelObject column : table.getSimpleModelObjects()) {
            Node columnNode = databaseConnector.createColumnNode(connection, column, tableNode, graph);

            // No corresponding database column found in the DB dictionary, try to deduce it
            if (columnNode == null) {
                LOGGER.debug("Unable to reach column \"{}\".\"{}\".\"{}\".\"{}\" in dictionary, deducing it.",
                        dataModel.getName(), tableNode.getParent().getName(), table.getName(), column.getName());

                columnNode = databaseConnector.deduceColumnNode(connection, column, tableNode, graph);

            } else {
                LOGGER.debug("Obtained column \"{}\".\"{}\".\"{}\".\"{}\" from dictionary.", column.getName(),
                        dataModel.getName(), tableNode.getParent().getName(), table.getName(), column.getName());
            }

            prepareForMapping(column, columnNode);
        }
    }
}
