package eu.profinit.manta.connector.erstudio.model.csv;

/**
 * Interface allowing access to a row filled with String values separated by colons
 *
 * @author ddrobny
 */
public interface CsvRow {
    /**
     * Gets number of values contained in the {@link CsvRow} object.
     * @return Number of values contained in the {@link CsvRow} object.
     */
    int getValueCount();

    /**
     * Every row must have assigned its {@link CsvRowDefinition} that stores names of columns,
     * this method gets the definition that the {@link CsvRow} object obeys.
     * @return Definition that describes names of columns of the row.
     */
    CsvRowDefinition getCsvRowDefinition();

    /**
     * Gets a value by its order in the {@link CsvRow} object.
     * @param index Position of the value we want to obtain.
     * @throws IndexOutOfBoundsException If index is getValueCount() or bigger.
     * @return Value at the {@code index} if the index is valid, otherwise an exception is thrown.
     */
    String getValue(int index);

    /**
     * Gets a value from {@link CsvRow} object by column's name that it is stored in.
     * @param columnName Name of the column which value we want to obtain.
     * @return Value stored in the column with specified name if a column with such name exists, otherwise null is returned.
     */
    String getValue(String columnName);

    /** Gets an integer value by its order in the {@link CsvRow} object.
     * @param index Position of the value we want to obtain.
     * @throws IndexOutOfBoundsException If index is getValueCount() or bigger.
     * @throws NumberFormatException If the value does not contain a parsable int.
     * @return Value at the {@code index}, otherwise an exception is thrown..
     */
    default int getIntValue(int index) {
        return Integer.parseInt(getValue(index));
    }

    /**
     * Gets an integer value from {@link CsvRow} object by column's name that it is stored in.
     * @param columnName Name of the column which value we want to obtain.
     * @throws NumberFormatException If the value does not contain a parsable int.
     * @return Value stored in the column with specified name if a column with such name exists and stores an integer,
     * otherwise NumberFormatException is thrown.
     */
    default int getIntValue(String columnName) {
        return Integer.parseInt(getValue(columnName));
    }
}
