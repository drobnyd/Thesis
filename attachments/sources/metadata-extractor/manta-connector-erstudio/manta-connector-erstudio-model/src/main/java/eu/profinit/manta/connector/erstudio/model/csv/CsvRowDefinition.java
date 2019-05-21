package eu.profinit.manta.connector.erstudio.model.csv;

/**
 * Interface providing translation from name of a {@link CsvRow}'s column row to its actual index in the
 * ordered structure of {@link CsvRow} where the values are stored.
 * Every {@link CsvTable} and {@link CsvRow} has assigned one object of this type that secures mapping from
 * columns' names to their indices.
 *
 * @author ddrobny
 */
public interface CsvRowDefinition {
    /**
     * Translates column's name to its position.
     * @param columnName Name of the column which index is being obtained.
     * @return Position of column with {@code columnName}.
     */
    int getIndexByName(String columnName);

    /**
     * Get how many column names is defined.
     * @return How many column names is defined.
     */
    int getColumnCount();
}