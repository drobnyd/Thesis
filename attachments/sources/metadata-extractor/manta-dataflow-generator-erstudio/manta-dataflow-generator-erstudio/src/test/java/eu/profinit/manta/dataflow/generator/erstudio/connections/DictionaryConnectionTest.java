package eu.profinit.manta.dataflow.generator.erstudio.connections;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.profinit.manta.connector.common.connections.impl.ConnectionImpl;
import eu.profinit.manta.connector.erstudio.model.AbstractionLayer;
import eu.profinit.manta.connector.erstudio.model.ConnectionType;
import eu.profinit.manta.connector.erstudio.model.PhysicalDataModel;
import eu.profinit.manta.connector.erstudio.model.impl.CompositeDataObjectImpl;
import eu.profinit.manta.connector.erstudio.model.impl.PhysicalDataModelImpl;
import eu.profinit.manta.connector.erstudio.model.impl.PlatformImpl;
import eu.profinit.manta.connector.erstudio.model.impl.SimpleDataObjectImpl;
import eu.profinit.manta.connector.postgresql.resolver.service.ParserServiceImpl;
import eu.profinit.manta.dataflow.generator.erstudio.graph.PhysicalGraphBuilder;
import eu.profinit.manta.dataflow.generator.erstudio.graph.PhysicalGraphBuilderImpl;
import eu.profinit.manta.dataflow.generator.modeling.common.connections.DatabaseConnector;
import eu.profinit.manta.dataflow.model.Resource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class DictionaryConnectionTest {
    private static final String TEST_LOCATION = "src/test/resources/dictionary-connection-tests/test-system/";
    private static final String INPUT_PATH_MODELS = TEST_LOCATION + "model-mocks/";
    private static final String INPUT_PATH_SCRIPTS = TEST_LOCATION + "ddl/";
    private final static String springConfigPath = "spring/erStudioFlowTest.xml";
    private static DatabaseConnector databaseConnector;
    private static Resource resource;
    private static ClassPathXmlApplicationContext springContext;
    private static DictionaryCreator dictionaryCreator = new DictionaryCreator();
    private static ParserServiceImpl postgreSqlParserService;
    private static eu.profinit.manta.plsql.parser.service.ParserServiceImpl oracleParserService;
    private String testName;
    private String modelDefinitionFileName;
    private String ddlScriptName;
    private PhysicalDataModel physicalModel;
    private ConnectionImpl connection;

    public DictionaryConnectionTest(String testName, String connectionString, ConnectionType connectionType)
            throws IOException {
        this.testName = testName;
        modelDefinitionFileName = testName + ".json";
        ddlScriptName = testName + ".sql";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(
                new File(INPUT_PATH_MODELS + connectionType.getId().toLowerCase() + "/" + modelDefinitionFileName));
        JsonNode owners = node.get("schema");

        switch (connectionType) {
        case ORACLE:
            dictionaryCreator.createOracleDictionary(testName, oracleParserService, new File(INPUT_PATH_SCRIPTS));
            break;

        case POSTGRESQL:
            dictionaryCreator
                    .createPostgreSqlDictionary(testName, postgreSqlParserService, new File(INPUT_PATH_SCRIPTS));
            break;

        default:
            throw new UnsupportedOperationException("Unsupported database technology");
        }
        physicalModel = createPhysicalModelMock(owners, connectionType);
        connection = new ConnectionImpl(connectionType.getId(), connectionString + ddlScriptName);
    }

    @Parameterized.Parameters
    public static Collection createParams() {

        List<Object[]> result = new ArrayList<>();

        result.add(new Object[] { "psql1", "jdbc:postgresql://manta-dev1:5432/", ConnectionType.POSTGRESQL });
        result.add(new Object[] { "ora1", "jdbc:oracle:thin:@manta-dev1:1521:", ConnectionType.ORACLE });

        return result;
    }

    @BeforeClass
    public static void initClass() {
        springContext = new ClassPathXmlApplicationContext(springConfigPath);
        resource = springContext.getBean("erStudioResource", eu.profinit.manta.dataflow.model.impl.ResourceImpl.class);
        databaseConnector = springContext
                .getBean(eu.profinit.manta.dataflow.generator.modeling.common.connections.DatabaseConnectorImpl.class);
        postgreSqlParserService = springContext
                .getBean(eu.profinit.manta.connector.postgresql.resolver.service.ParserServiceImpl.class);
        oracleParserService = springContext.getBean(eu.profinit.manta.plsql.parser.service.ParserServiceImpl.class);
    }

    @Test
    public void connectionTest() {
       /* TODO
        Logger builderLogger = (Logger) LoggerFactory.getLogger(PhysicalGraphBuilderImpl.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        builderLogger.addAppender(listAppender);
        assert that no other that INFO level logs from PhysicalBuilder has occurred
        */

        // Method under the test
        PhysicalGraphBuilder graphBuilder = new PhysicalGraphBuilderImpl(physicalModel, databaseConnector);
        graphBuilder.setConnection(connection);
        graphBuilder.buildGraph();

        assertNotNull(graphBuilder.getGraph());

    }

    private PhysicalDataModel createPhysicalModelMock(JsonNode owners, ConnectionType connectionType) {
        PhysicalDataModelImpl physicalModel = new PhysicalDataModelImpl(testName,
                new PlatformImpl(testName, connectionType));

        for (JsonNode ownerDesc : owners) {
            String ownerName = ownerDesc.get("name").asText(null);
            physicalModel.addOwner(ownerName);
            for (JsonNode tableDesc : ownerDesc.get("table")) {
                CompositeDataObjectImpl table = new CompositeDataObjectImpl(tableDesc.get("name").asText(null),
                        AbstractionLayer.PHYSICAL);
                // TODO no explicit adding of the owner in modelImpl, just add an object with owner name that creates it
                physicalModel.getOwner(ownerName).addCompositeObject(table);

                for (JsonNode columnDesc : tableDesc.get("column")) {
                    SimpleDataObjectImpl column = new SimpleDataObjectImpl(columnDesc.get("name").asText(null),
                            AbstractionLayer.PHYSICAL);
                    table.addSubObject(column);
                }
            }
        }

        return physicalModel;
    }
}
