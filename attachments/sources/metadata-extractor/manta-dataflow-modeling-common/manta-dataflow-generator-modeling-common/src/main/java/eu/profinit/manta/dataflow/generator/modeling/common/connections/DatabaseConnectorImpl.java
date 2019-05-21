package eu.profinit.manta.dataflow.generator.modeling.common.connections;

import eu.profinit.manta.connector.common.connections.Connection;
import eu.profinit.manta.dataflow.generator.common.query.DataflowQueryService;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.NodeExtensions;
import eu.profinit.manta.dataflow.generator.modeling.common.nodes.NodeMetadata;
import eu.profinit.manta.dataflow.generator.modelutils.NodeCreator;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;
import eu.profinit.manta.dataflow.model.NodeType;
import eu.profinit.manta.dataflow.model.Resource;
import eu.profinit.manta.dataflow.model.impl.ResourceImpl;
import org.apache.commons.lang3.StringUtils;

public class DatabaseConnectorImpl implements DatabaseConnector {
    /** General dataflowQueryService allowing access to DB Dictionary objects. Specific services have to be set. */
    private DataflowQueryService dataflowService;
    /** Database nodes creator when {@code dataflowService} fails in getting an object */
    private NodeCreator nodeCreator;

    @Override
    public void setDataflowQueryService(DataflowQueryService dataflowService) {
        this.dataflowService = dataflowService;
    }

    @Override
    public void setNodeCreator(NodeCreator nodeCreator) {
        this.nodeCreator = nodeCreator;
    }

    @Override
    public Resource getDatabaseResource(Connection connection) {
        return dataflowService.getDatabaseResource(connection);
    }

    @Override
    public Node createTableNode(Connection connection, NodeMetadata table, String dbName, String schemaName,
            Graph graph) {
        if (dataflowService != null && dataflowService.isSupported(connection)) {
            Node node = dataflowService.addObjectNode(dbName, schemaName, table.getName(), connection, graph);

            NodeExtensions.appendAttributes(node, table);
            return node;
        }

        return null;
    }

    @Override
    public Node deduceTableNode(Connection connection, NodeMetadata table, String dbName, String schemaName,
            Graph graph) {
        Resource resource = null;
        String serverName = null;

        // Try to make use of connection attributes
        if (dataflowService != null && dataflowService.isSupported(connection)) {
            resource = dataflowService.getDatabaseResource(connection);
            serverName = dataflowService.getDefaultServer(connection);
            if (StringUtils.isBlank(dbName)) {
                dbName = dataflowService.getDefaultDatabase(connection);
            }
            if (StringUtils.isBlank(schemaName)) {
                schemaName = dataflowService.getDefaultSchema(connection);
            }
        }

        if (resource == null && connection.getType() != null) {
            resource = new ResourceImpl(connection.getType(), connection.getType(), StringUtils.EMPTY);
        }

        if (StringUtils.isBlank(serverName)) {
            serverName = connection.getServerName();
        }

        if (StringUtils.isBlank(schemaName)) {
            schemaName = connection.getSchemaName();
        }

        if (StringUtils.isBlank(dbName)) {
            dbName = connection.getDatabaseName();
        }

        if (!StringUtils.isBlank(dbName)) {
            Node node = nodeCreator.createTableNode(graph, serverName, dbName, schemaName, table.getName(), resource);

            markNodeDeduced(node);
            NodeExtensions.appendAttributes(node, table);
            return node;
        }

        return null;
    }

    @Override
    public Node createColumnNode(Connection connection, NodeMetadata column, Node tableNode, Graph graph) {
        if (dataflowService != null && dataflowService.isSupported(connection)) {
            Node node = dataflowService.addColumnNode(tableNode, column.getName(), connection, graph);

            NodeExtensions.appendAttributes(node, column);
            return node;
        }

        return null;
    }

    @Override
    public Node deduceColumnNode(Connection connection, NodeMetadata column, Node tableNode, Graph graph) {
        if (!StringUtils.isBlank(column.getName())) {
            Node node = graph.addNode(column.getName(), NodeType.COLUMN.getId(), tableNode,
                    dataflowService.getDatabaseResource(connection));

            markNodeDeduced(node);
            NodeExtensions.appendAttributes(node, column);
            return node;
        }

        return null;
    }

    private void markNodeDeduced(Node node) {
        if (node != null) {
            node.addAttribute("OBJECT_SOURCE_TYPE", "MODEL");
        }
    }

}
