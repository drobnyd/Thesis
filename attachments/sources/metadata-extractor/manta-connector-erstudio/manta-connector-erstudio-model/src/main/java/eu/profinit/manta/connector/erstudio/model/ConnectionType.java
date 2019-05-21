package eu.profinit.manta.connector.erstudio.model;

/**
 * Types of database management systems supported by ER/Studio.
 *
 * @author ddrobny
 */
public enum ConnectionType {TERADATA("Teradata"), ORACLE("Oracle"), MSSQL("MSSQL"), NETEZZA("Netezza"), DB2(
        "DB2"), POSTGRESQL("PostgreSQL"), HIVE("Hive"), UNKNOWN("Unknown"), FIERBIRD("Firebird"), GENERIC(
        "Generic"), GREENPLUM("Greenplum"), HITACHI("Hitachi"), INFORMIX("Informix"), INTERBASE("Interbase"), ACCESS(
        "Microsoft Access"), AZURE("Microsoft Azure"), FOXPRO("FoxPro"), MONGODB("MongoDB"), MYSQL("MySQL"), SYBASE(
        "Sybase");

    private final String id;

    ConnectionType(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }}
