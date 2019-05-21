package eu.profinit.manta.dataflow.generator.erstudio.connections;

import eu.profinit.manta.connector.common.StringFileReader;
import eu.profinit.manta.connector.common.dictionary.AbstractDataDictionary;
import eu.profinit.manta.connector.common.dictionary.dao.jdbc.H2DictionaryDaoFactory;
import eu.profinit.manta.connector.oracle.OracleReader;
import eu.profinit.manta.connector.oracle.dictionary.OracleDataDictionary;
import eu.profinit.manta.connector.oracle.dictionary.dialect.OracleDialect;
import eu.profinit.manta.connector.oracle.model.dictionary.OracleResolverEntitiesFactory;
import eu.profinit.manta.connector.postgresql.PostgresqlReader;
import eu.profinit.manta.connector.postgresql.dictionary.PostgresqlDataDictionary;
import eu.profinit.manta.connector.postgresql.dictionary.dialect.PostgresqlDialect;
import eu.profinit.manta.connector.postgresql.model.dictionary.PostgresqlResolverEntitiesFactory;
import eu.profinit.manta.connector.postgresql.resolver.service.ParserServiceImpl;
import eu.profinit.manta.platform.automation.InputReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class DictionaryCreator {
    /** Output directory to store persisted dictionaries. */
    private static final File DICTIONARY_FOLDER = new File("target/test-classes/dictionary");

    public void createPostgreSqlDictionary(String dictionaryId, ParserServiceImpl parserService, File scriptFolder) {
        String dialect = "postgresql";
        AbstractDataDictionary<?> dictionary;
        try (StringFileReader stringReader = new StringFileReader()) {
            stringReader.setInputFile(FileUtils.getFile(scriptFolder, dialect));
            stringReader.setEncoding("utf8");

            File contextDirectory = new File(DICTIONARY_FOLDER, dialect);

            InputReader<?> reader;

            PostgresqlReader psqlReader = new PostgresqlReader();
            psqlReader.setResetBeforeRead(false);
            psqlReader.setIsDefaultSchemaFromFolder(true);
            psqlReader.setParserService(parserService);
            H2DictionaryDaoFactory<PostgresqlResolverEntitiesFactory> psqlDaoFactory = new H2DictionaryDaoFactory<>();
            psqlDaoFactory.setCreateMissing(true);
            PostgresqlDataDictionary psqlDict = new PostgresqlDataDictionary(
                    psqlDaoFactory.getDataDictionaryDao(contextDirectory, dictionaryId), new PostgresqlDialect(), true,
                    dictionaryId);
            psqlReader.setEntitiesFactory(psqlDict);
            psqlReader.setInputReader(stringReader);
            reader = psqlReader;
            dictionary = psqlDict;

            try {
                while (reader.canRead()) {
                    try {
                        reader.read();
                        dictionary.persist();
                    } catch (IOException e) {
                        throw new RuntimeException("Error reading script " + reader.getInputName(), e);
                    }
                }
            } finally {
                dictionary.close();
            }
        }
    }

    public void createOracleDictionary(String dictionaryId,
            eu.profinit.manta.plsql.parser.service.ParserServiceImpl parserService, File scriptFolder) {
        String dialect = "oracle";
        AbstractDataDictionary<?> dictionary;
        try (StringFileReader stringReader = new StringFileReader()) {
            stringReader.setInputFile(FileUtils.getFile(scriptFolder, dialect));
            stringReader.setEncoding("utf8");

            File contextDirectory = new File(DICTIONARY_FOLDER, dialect);

            InputReader<?> reader;

            OracleReader oraReader = new OracleReader();
            oraReader.setResetBeforeRead(false);
            oraReader.setIsDefaultSchemaFromFolder(true);
            oraReader.setParserService(parserService);
            H2DictionaryDaoFactory<OracleResolverEntitiesFactory> oraDaoFactory = new H2DictionaryDaoFactory<>();
            oraDaoFactory.setCreateMissing(true);
            OracleDataDictionary oraDict = new OracleDataDictionary(
                    oraDaoFactory.getDataDictionaryDao(contextDirectory, dictionaryId), new OracleDialect(), true,
                    dictionaryId);
            oraReader.setEntitiesFactory(oraDict);
            oraReader.setInputReader(stringReader);
            reader = oraReader;
            dictionary = oraDict;

            try {
                while (reader.canRead()) {
                    try {
                        reader.read();
                        dictionary.persist();
                    } catch (IOException e) {
                        throw new RuntimeException("Error reading script " + reader.getInputName(), e);
                    }
                }
            } finally {
                dictionary.close();
            }
        }
    }

}
