package eu.profinit.manta.connector.erstudio.model.csv;

import java.util.List;

/**
 * A single table loaded from ER/Studio's .DM1 file.
 * A {@link CsvTable} can be iterated over its rows containing data that are of type {@link CsvRow}
 *
 * @author ddrobny
 */
public interface CsvTable extends Iterable<CsvRow> {
    /**
     * Gets the name of the object.
     * @return Name of the object.
     */
    String getName();

    /**
     * Gets the definition common for each {@link CsvRow} stored in the table.
     * @return Definition common for each {@link CsvRow} stored in the table.
     */
    CsvRowDefinition getRowDefinition();

    /**
     * Gets number of {@link CsvRow}s in the table
     * @return Number of {@link CsvRow}s in the table
     */
    int getDataRowCount();

    /**
     * Gets Number of columns of {@link CsvRowDefinition} and each {@link CsvRow} stored in the table contain.
     * @return Number of columns of {@link CsvRowDefinition} and each {@link CsvRow} stored in the table contain.
     */
    int getColumnCount();

    /**
     * Gets index of a column with the {@code columnName} in a {@link CsvRow} stored in the table.
     * @param columnName Name of column which index to get.
     * @return Index of a column with the {@code columnName} in a {@link CsvRow} stored in the table.
     */
    int getIndexByName(String columnName);

    /**
     * Gets a {@link CsvRow} from the table at position of {@code index}.
     * Should be called only on tables with one or more data rows.
     * @param index If dataRows are non-empty then valid indices are from 0 to dataRowCount - 1
     * @return {@link CsvRow} from the table at position of {@code index}.
     */
    CsvRow getDataRow(int index);

    /**
     * Gets a value of a specific column from a specific row in the table.
     * @param rowIndex Index of the table's row where to look for the column.
     * @param columnName Name of the column which value to get from the {@link CsvRow} at position rowIndex.
     * @return value of the column with {@code columnName} from the row at position {@code rowIndex} from the table.
     */
    String getValueFromDataRow(int rowIndex, String columnName);

    /** Filters the rows such that column with {@code columnName} is equal to {@code conditionValue}.
     * @param columnName Name of the column that will be examines.
     * @param conditionValue Value that the columns with {@code columnName} must have to be contained in the result.
     * @return rows fulfilling the condition in the same order as in source table
     */
    List<CsvRow> filter(String columnName, String conditionValue);
}
