package eu.profinit.manta.connector.erstudio.model;

import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;

import java.util.Map;

/**
 * ER/Studio's .DM1 file loaded into data structure with no processing besides of loading its plain structure.
 * Allows read-only access to one to one representation of the .DM1 file that consists of multiple {@link CsvTable}s.
 *
 * @author ddrobny
 */
public interface ErStudioFile {
    /**
     * Get a specific table by its name.
     * @param tableName Name of searched table.
     * @return Table with {@code tableName} if such exists, otherwise null.
     */
    CsvTable getCsvTable(String tableName);

    /**
     * Get the whole .DM1's structure.
     * @return Map where keys are names of stored {@link CsvTable}s and values the tables themselves.
     */
    Map<String, CsvTable> getAllCsvTables();
}
