package eu.profinit.manta.dataflow.generator.modeling.common.connections;

import eu.profinit.manta.connector.common.connections.Connection;
import eu.profinit.manta.dataflow.generator.common.query.DataflowQueryService;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.NodeMetadata;
import eu.profinit.manta.dataflow.generator.modelutils.NodeCreator;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;
import eu.profinit.manta.dataflow.model.Resource;

/**
 * Provides access to database dictionary nodes by their description.
 * Also disposes of fallback node creation methods for the case, when the described node counterpart is unreachable.
 *
 * @author ddrobny
 */
public interface DatabaseConnector {

    /**
     * Sets the general dataflowService.
     * @param dataflowService to set.
     */
    void setDataflowQueryService(DataflowQueryService dataflowService);

    /**
     * The node creator for creating database nodes.
     * @param nodeCreator to be set.
     */
    void setNodeCreator(NodeCreator nodeCreator);

    /**
     * Gets the resource of the connection.
     * @param connection to get the resource from.
     * @return resource of the connection.
     */
    Resource getDatabaseResource(Connection connection);

    /**
     * Attempts to retrieve a node for a server, database, schema and table hierarchy based on the passed values
     * using the dictionary. If the retrieval is successful, the hierarchy is added to the output graph.
     *
     * @param connection connection attributes. Must be non-null.
     * @param table details of the created table. Its name must be non-blank.
     * @param dbName name of the table's database or {@code null} if it is not known.
     * @param schemaName name of the table's schema or {@code null} if it is not known.
     * @param graph the output graph.
     * @return Newly created node for the table or {@code null} if the retrieval was not successful.
     */
    Node createTableNode(Connection connection, NodeMetadata table, String dbName, String schemaName, Graph graph);

    /**
     * Attempts to deduce a node for a server, database, schema and table hierarchy based on the passed values
     * using the dictionary. If the retrieval is successful, the hierarchy is added to the output graph.
     *
     * @param connection connection attributes. Must be not null.
     * @param table details of the created table. Its name must be non-blank.
     * @param dbName name of the table's database or {@code null} if it is not known.
     * @param schemaName name of the table's schema or {@code null} if it is not known.
     * @param graph the output graph.
     * @return Newly created node for the table or {@code null} if the deduction was not successful.
     */
    Node deduceTableNode(Connection connection, NodeMetadata table, String dbName, String schemaName, Graph graph);

    /**
     * Attempts to retrieve a node for a column based on the passed values using the dictionary.
     * If the retrieval is successful, the hierarchy is added to the output graph.
     *
     * @param connection connection attributes. Must be not null.
     * @param column details of the created column. Its name must be non-blank.
     * @param tableNode parent node of the newly created column node.
     * @param graph the output graph.
     * @return Newly created node for the column or {@code null} if the deduction was not successful.
     */
    Node createColumnNode(Connection connection, NodeMetadata column, Node tableNode, Graph graph);

    /**
     * Attempts to retrieve a node for a column based on the passed values using the dictionary.
     * If the retrieval is successful, the hierarchy is added to the output graph.
     *
     * @param connection connection attributes. Must be not null.
     * @param column details of the created column. Its name must be non-blank.
     * @param tableNode parent node of the newly created column node.
     * @param graph the output graph.
     * @return Newly created node for the table or {@code null} if the deduction was not successful.
     */
    Node deduceColumnNode(Connection connection, NodeMetadata column, Node tableNode, Graph graph);

}
