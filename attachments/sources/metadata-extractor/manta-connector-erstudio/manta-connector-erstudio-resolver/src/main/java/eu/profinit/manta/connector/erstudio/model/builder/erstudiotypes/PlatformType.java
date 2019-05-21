package eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes;

import eu.profinit.manta.connector.erstudio.model.AbstractionLayer;
import eu.profinit.manta.connector.erstudio.model.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of database platforms ER/Studio is working with.
 *
 * @author ddrobny
 */
public enum PlatformType {LOGICAL(0, "Logical", ConnectionType.UNKNOWN), UNKNOWN(-1, "Unknown Platform",
        ConnectionType.UNKNOWN), IBM_DB2_UDB_10_x(95, "IBM DB2 UDB 10.x", ConnectionType.DB2), IBM_DB2_AS_400_4_x(48,
        "IBM DB2 AS/400 4.x", ConnectionType.DB2), IBM_DB2_AS_400_5_x(62, "IBM DB2 AS/400 5.x",
        ConnectionType.DB2), IBM_DB2_Common_Server(9, "IBM DB2 Common Server", ConnectionType.DB2), IBM_DB2_UDB_5_x(32,
        "IBM DB2 UDB 5.x", ConnectionType.DB2), IBM_DB2_UDB_6_x(38, "IBM DB2 UDB 6.x",
        ConnectionType.DB2), IBM_DB2_UDB_7_x(44, "IBM DB2 UDB 7.x", ConnectionType.DB2), IBM_DB2_UDB_8_x(59,
        "IBM DB2 UDB 8.x", ConnectionType.DB2), IBM_DB2_UDB_9_x(74, "IBM DB2 UDB 9.x",
        ConnectionType.DB2), IBM_DB2_UDB_for_OS_390_5_x(40, "IBM DB2 UDB for OS/390 5.x",
        ConnectionType.DB2), IBM_DB2_UDB_for_OS_390_6_x(39, "IBM DB2 UDB for OS/390 6.x",
        ConnectionType.DB2), IBM_DB2_UDB_for_OS_390_7_x(46, "IBM DB2 UDB for OS/390 7.x",
        ConnectionType.DB2), IBM_DB2_UDB_for_OS_390_8_x(69, "IBM DB2 UDB for OS/390 8.x",
        ConnectionType.DB2), IBM_DB2_UDB_for_OS_390_9_x(78, "IBM DB2 UDB for OS/390 9.x",
        ConnectionType.DB2), IBM_DB2_UDB_for_OS_390_10_x(90, "IBM DB2 UDB for OS/390 10.x",
        ConnectionType.DB2), IBM_DB2_UDB_for_OS_390_11_x(109, "IBM DB2 UDB for OS/390 11.x",
        ConnectionType.DB2), Firebird_1_5(98, "Firebird 1.5", ConnectionType.FIERBIRD), Firebird_2_x(99, "Firebird 2.x",
        ConnectionType.FIERBIRD), Generic_DBMS(51, "Generic DBMS", ConnectionType.GENERIC), Greenplum_4_x(92,
        "Greenplum 4.x", ConnectionType.GREENPLUM), Hitachi_HiRDB(45, "Hitachi HiRDB",
        ConnectionType.HITACHI), Hive_0_12_0_13(102, "Hive 0.12-0.13", ConnectionType.HIVE), Informix_SE(7,
        "Informix SE", ConnectionType.INFORMIX), Informix_ONLINE(6, "Informix ONLINE",
        ConnectionType.INFORMIX), Informix_9_x(65, "Informix 9.x", ConnectionType.INFORMIX), InterBase(10, "InterBase",
        ConnectionType.INTERBASE), InterBase_2007(81, "InterBase 2007", ConnectionType.INTERBASE), InterBase_2009(83,
        "InterBase 2009", ConnectionType.INTERBASE), InterBase_XE(96, "InterBase XE",
        ConnectionType.INTERBASE), InterBase_XE3(97, "InterBase XE3", ConnectionType.INTERBASE), Microsoft_Access(14,
        "Microsoft Access", ConnectionType.ACCESS), Microsoft_Access_2_0(15, "Microsoft Access 2.0",
        ConnectionType.ACCESS), Microsoft_Access_95(16, "Microsoft Access 95",
        ConnectionType.ACCESS), Microsoft_Access_97(30, "Microsoft Access 97",
        ConnectionType.ACCESS), Microsoft_Access_2000(42, "Microsoft Access 2000",
        ConnectionType.UNKNOWN), Microsoft_SQL_Server_4_x(1, "Microsoft SQL Server 4.x",
        ConnectionType.MSSQL), Microsoft_SQL_Server_6_x(3, "Microsoft SQL Server 6.x",
        ConnectionType.MSSQL), Microsoft_SQL_Server_7_x(33, "Microsoft SQL Server 7.x",
        ConnectionType.MSSQL), Microsoft_SQL_Server_2000(41, "Microsoft SQL Server 2000",
        ConnectionType.MSSQL), Microsoft_SQL_Server_2005(68, "Microsoft SQL Server 2005",
        ConnectionType.MSSQL), Microsoft_SQL_Server_2008(82, "Microsoft SQL Server 2008",
        ConnectionType.MSSQL), Microsoft_SQL_Server_2012(89, "Microsoft SQL Server 2012",
        ConnectionType.MSSQL), Microsoft_SQL_Server_2014(101, "Microsoft SQL Server 2014",
        ConnectionType.MSSQL), Microsoft_SQL_Server_2016(108, "Microsoft SQL Server 2016",
        ConnectionType.MSSQL), Microsoft_SQL_Server_2017(111, "Microsoft SQL Server 2017",
        ConnectionType.MSSQL), Microsoft_Azure_SQL_DB(107, "Microsoft Azure SQL DB",
        ConnectionType.AZURE), Microsoft_Visual_FoxPro_2_x(20, "Microsoft Visual FoxPro 2.x",
        ConnectionType.FOXPRO), Microsoft_Visual_FoxPro_3_x(28, "Microsoft Visual FoxPro 3.x",
        ConnectionType.FOXPRO), Microsoft_Visual_FoxPro_5_x(31, "Microsoft Visual FoxPro 5.x",
        ConnectionType.FOXPRO), MongoDB_2_4_3_0(103, "MongoDB 2.4-3.0", ConnectionType.MONGODB), MongoDB_3_2_3_6(110,
        "MongoDB 3.2-3.6", ConnectionType.MONGODB), MySQL_3_x(58, "MySQL 3.x", ConnectionType.MYSQL), MySQL_4_x(60,
        "MySQL 4.x", ConnectionType.MYSQL), MySQL_5_x(73, "MySQL 5.x", ConnectionType.MYSQL), Netezza_4_6(85,
        "Netezza 4.6", ConnectionType.NETEZZA), Netezza_5_0(86, "Netezza 5.0", ConnectionType.NETEZZA), Netezza_6_0(88,
        "Netezza 6.0", ConnectionType.NETEZZA), Netezza_7_0(94, "Netezza 7.0", ConnectionType.NETEZZA), Oracle_7_x(8,
        "Oracle 7.x", ConnectionType.ORACLE), Oracle_8_x(34, "Oracle 8.x", ConnectionType.ORACLE), Oracle_9i(47,
        "Oracle 9i", ConnectionType.ORACLE), Oracle_10g(66, "Oracle 10g", ConnectionType.ORACLE), Oracle_11g(77,
        "Oracle 11g", ConnectionType.ORACLE), Oracle_12c(100, "Oracle 12c", ConnectionType.ORACLE), PostgreSQL_8_0(72,
        "PostgreSQL 8.0", ConnectionType.POSTGRESQL), PostgreSQL_9_x(91, "PostgreSQL 9.x",
        ConnectionType.POSTGRESQL), Sybase_ASA_6_0(35, "Sybase ASA 6.0", ConnectionType.SYBASE), Sybase_ASA_7_0(55,
        "Sybase ASA 7.0", ConnectionType.SYBASE), Sybase_ASA_8_0(56, "Sybase ASA 8.0",
        ConnectionType.SYBASE), Sybase_ASA_9_0(64, "Sybase ASA 9.0", ConnectionType.SYBASE), Sybase_ASA_10_0(79,
        "Sybase ASA 10.0", ConnectionType.SYBASE), Sybase_Adaptive_Server_IQ_12_5(49, "Sybase Adaptive Server IQ 12.5",
        ConnectionType.SYBASE), Sybase_Adaptive_Server_IQ_15(104, "Sybase Adaptive Server IQ 15",
        ConnectionType.SYBASE), Sybase_Adaptive_Server_IQ_16(105, "Sybase Adaptive Server IQ 16",
        ConnectionType.SYBASE), Sybase_System_10(2, "Sybase System 10", ConnectionType.SYBASE), Sybase_ASE_11_0(11,
        "Sybase ASE 11.0", ConnectionType.SYBASE), Sybase_ASE_11_5(37, "Sybase ASE 11.5",
        ConnectionType.SYBASE), Sybase_ASE_11_9(36, "Sybase ASE 11.9", ConnectionType.SYBASE), Sybase_ASE_12_0(43,
        "Sybase ASE 12.0", ConnectionType.SYBASE), Sybase_ASE_12_5(54, "Sybase ASE 12.5",
        ConnectionType.SYBASE), Sybase_ASE_15(75, "Sybase ASE 15", ConnectionType.SYBASE), Sybase_Watcom_SQL(4,
        "Sybase Watcom SQL", ConnectionType.SYBASE), Sybase_SQL_Anywhere_5(12, "Sybase SQL Anywhere 5",
        ConnectionType.SYBASE), NCR_Teradata_V2R4(57, "NCR Teradata V2R4", ConnectionType.TERADATA), NCR_Teradata_V2R5(
        61, "NCR Teradata V2R5", ConnectionType.TERADATA), NCR_Teradata_V2R6(71, "NCR Teradata V2R6",
        ConnectionType.TERADATA), Teradata_12_0(84, "Teradata 12.0", ConnectionType.TERADATA), Teradata_13_x(87,
        "Teradata 13.x", ConnectionType.TERADATA), Teradata_14_x(93, "Teradata 14.x",
        ConnectionType.TERADATA), Teradata_15_x(106, "Teradata 15.x", ConnectionType.TERADATA);

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformType.class);
    private static final Map<Integer, PlatformType> idToName = new HashMap<>();

    static {
        for (PlatformType type : PlatformType.values()) {
            idToName.put(type.getPlatformId(), type);
        }
    }

    /** Name of database platform */
    private final String platformName;
    /** Unique identifier of platform, if it's greater than 0 it's a physical platform */
    private final int platformId;

    private final ConnectionType connectionType;

    /**
     *
     * @param platformId ID of a database platform
     * @param platformName Name of a database platform
     */
    PlatformType(final int platformId, final String platformName, final ConnectionType connectionType) {
        this.platformName = platformName;
        this.platformId = platformId;
        this.connectionType = connectionType;
    }

    public static PlatformType valueOf(int i) {
        if (!idToName.containsKey(i)) {
            LOGGER.warn("Platform with id {} is not supported", i);
            return UNKNOWN;
        }
        return idToName.get(i);
    }

    /**
     * Returns type {@link AbstractionLayer} based on type of {@link PlatformType}.
     * @param type Which layer of abstraction we want to find out.
     * @return Type {@link AbstractionLayer}.
     */
    public static AbstractionLayer getLayer(PlatformType type) {
        if (type.getPlatformId() == 0) {
            return AbstractionLayer.LOGICAL;
        } else {
            return AbstractionLayer.PHYSICAL;
        }
    }

    public int getPlatformId() {
        return platformId;
    }

    @Override
    public String toString() {
        return platformName;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }}
