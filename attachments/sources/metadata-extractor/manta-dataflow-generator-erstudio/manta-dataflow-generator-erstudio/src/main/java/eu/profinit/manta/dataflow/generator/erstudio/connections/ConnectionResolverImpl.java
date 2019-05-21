package eu.profinit.manta.dataflow.generator.erstudio.connections;

import eu.profinit.manta.connector.common.connections.Connection;
import eu.profinit.manta.connector.common.connections.ConnectionType;
import eu.profinit.manta.connector.common.connections.impl.ConnectionImpl;
import eu.profinit.manta.connector.erstudio.model.PhysicalDataModel;

import java.io.File;

/**
 * Pairs physical data model with corresponding database connection.
 *
 * @author ddrobny
 */
public class ConnectionResolverImpl implements ConnectionResolver {
    ConnectionSourceImpl connectionSource = new ConnectionSourceImpl();

    public ConnectionResolverImpl() {
        connectionSource.setOptional(true);
    }

    @Override
    public void setConnectionsConfig(File connectionsConfig) {
        connectionSource.setConnectionFile(connectionsConfig);
    }

    @Override
    public Connection getConnection(PhysicalDataModel model) {

        Connection connection = connectionSource.getConnection(model.getName()) != null ?
                                connectionSource.getConnection(model.getName()) :
                                new ConnectionImpl(null, "");

        connection = deduceConnectionType(connection, model);

        return connection;
    }

    /**
     * If {@code connection} doesn't contain Type of {@link Connection}, that is mandatory,
     * get it by platform of database that {@code physicalModel} represents.
     * @param connection to be checked, connection to {@code physicalModel}'s database.
     * @param physicalModel representation of database accessed by {@code connection}.
     * @return {@code connection} if Type is not missing, otherwise new instance with Type filled.
     */
    private Connection deduceConnectionType(Connection connection, PhysicalDataModel physicalModel) {

        if (connection.getType() != null) {
            return connection;
        }

        ConnectionType type = translateConnectionType(physicalModel.getPlatform().getPlatformType());

        return new ConnectionImpl(type.getId(), connection.getConnectionString(), connection.getServerName(),
                connection.getDatabaseName(), connection.getSchemaName(), connection.getUserName());
    }

    /**
     * Converts {@link eu.profinit.manta.connector.erstudio.model.ConnectionType} to {@link ConnectionType}
     * @param connectionType to be converted.
     * @return {@code connectionType} typed as {@link ConnectionType}.
     */
    private ConnectionType translateConnectionType(
            eu.profinit.manta.connector.erstudio.model.ConnectionType connectionType) {
        switch (connectionType) {

        case TERADATA:
            return ConnectionType.TERADATA;

        case ORACLE:
            return ConnectionType.ORACLE;

        case MSSQL:
            return ConnectionType.MSSQL;

        case NETEZZA:
            return ConnectionType.NETEZZA;

        case DB2:
            return ConnectionType.DB2;

        case POSTGRESQL:
            return ConnectionType.POSTGRESQL;

        case HIVE:
            return ConnectionType.HIVE;

        case UNKNOWN:
            return ConnectionType.UNKNOWN;

        default:
            return ConnectionType.UNKNOWN;

        }
    }
}
