package cz.manta.drobny.DM1ReverseEngineer.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Table  representation.
 *
 * @author ddrobny
 */
public class Table {
    private String name;
    private List<Column> columns = new ArrayList<>();
    private int numberOfRows = 0;

    public Table(String tableName, Iterable<String> columnNames) {
        this.name = tableName;
        for (String columnName : columnNames) { // For each parsed column name create Column object
            columns.add(new Column(columnName));
        }
    }

    public Table(List<Column> columns, String tableName) {
        this.name = tableName;
        this.columns = columns;
    }

    /**
     * Adds values from row to appropriate columns of the table
     * @param row An entry of the table separated by commas
     */
    public void addRow(Collection<String> row) {
        assert columns.size() == row.size();
        int i = 0;
        for (String entry : row) {
            columns.get(i++).addValue(entry);
        }
        numberOfRows++;
    }

    public int getNumberOfColumns() {
        return columns.size();
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String getName() {
        return name;
    }

    /**
     * Search column based on name
     * @param columnName name of the column we look for
     * @return column with the specified name, if couldn't be found null
     */
    public Column getColumn(String columnName) {
        for (Column current : columns) {
            if (current.getName().equals(columnName)) {
                return current;
            }
        }
        return null;
    }

    /**
     * Gets column by position
     * @param index position of the column we look for
     * @return Column on specified position
     */
    public Column getColumn(int index) {
        return columns.get(index);
    }

    /**
     * Is equal iff the table name is the same and every column as well
     * @param obj compared object
     * @return if the two objects are equal or not
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Table)) {
            return false;
        }
        Table objTable = (Table) obj;
        if (!this.name.equals(objTable.getName())) {
            return false;
        }
        List<Column> objTableColumns = objTable.getColumns();
        if (this.columns.size() != objTableColumns.size()) {
            return false;
        }
        for (int i = 0; i < this.columns.size(); i++) {
            if (!this.columns.get(i).equals(objTableColumns.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reconstruction of CSV table
     * @return String representation of CSV table
     */
    @Override
    public String toString() {
        int numOfColumns = getNumberOfColumns();
        if (numOfColumns == 0) {
            return "\n"; // Completely empty table
        }
        int numOfRows = columns.get(0).getNumOfRows();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name);
        stringBuilder.append('\n');
        appendRow(stringBuilder, getColumnNames());
        for (int i = 0; i < numOfRows; i++) {
            appendRow(stringBuilder, getRow(i));
        }
        return stringBuilder.toString();
    }

    /**
     * Append row table's tow represented by list of strings to the StringBuilder object
     */
    private void appendRow(StringBuilder toStringBuilder, List<String> row) {
        int numOfColumns = row.size();
        for (int i = 0; i < numOfColumns - 1; i++) {
            toStringBuilder.append(row.get(i));
            toStringBuilder.append(',');
        }
        toStringBuilder.append(row.get(numOfColumns - 1));
        toStringBuilder.append('\n'); // Last one with no comma but with newline
    }

    /**
     * @param index of row
     * @return set of entries in specified row
     */
    private List<String> getRow(int index) {
        List<String> result = new ArrayList<>();
        for (Column column : columns) {
            result.add(column.getValue(index));
        }
        return result;
    }

    /**
     * @return Textual representation of table's columns
     */
    public List<String> getColumnNames() {
        List<String> names = new ArrayList<>();
        for (Column column : columns) {
            names.add(column.getName());
        }
        return names;
    }
}
