package cz.manta.drobny.DM1ReverseEngineer.table;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Metadata of a table.
 *
 * @author ddrobny
 */
public class TableDefinition implements Definition {
    private HashSet<ColumnDefinition> columnDefinitions = new HashSet<>();
    private String tableName;

    /**
     * @param columns of the table, will be linked with the new TableDefinition
     * @param tableName name of the TableDefinition instance that is being created
     */
    public TableDefinition(Collection<ColumnDefinition> columns, String tableName) {
        this.tableName = tableName;
        columnDefinitions = new HashSet<>(columns);
        columns.stream()
                .forEach(columnDefinition -> assignColumnDefinition(columnDefinition)); // Link table with every column
    }

    /**
     * @param tableName name of the TableDefinition instance that is being created
     * @param columns names of columns that are going to be created as instances of ColumnDefinition. Will be assigned to the new TableDefinition instance
     */
    public TableDefinition(String tableName, Collection<String> columns) {
        this.tableName = tableName;

        columns.stream().forEach(columnName -> {
            ColumnDefinition columnDefinition = new ColumnDefinition(columnName);
            assignColumnDefinition(columnDefinition);
        }); // Link table with every column
    }

    public TableDefinition(String tableName) {
        this.tableName = tableName;
    }

    public HashSet<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitions;
    }

    public String getName() {
        return tableName;
    }

    /**
     * Assigns the table to the column and also the columns to the table
     * @param columnDefinition the ColumnDefinition object to be assigned to the table
     */
    public void assignColumnDefinition(ColumnDefinition columnDefinition) {
        columnDefinition.addUsage(this);
        columnDefinitions.add(columnDefinition);
    }

    /**
     * @param columnDefinition will be removed from table's columns
     */
    public void removeColumnDefinition(ColumnDefinition columnDefinition) {
        columnDefinitions.remove(columnDefinition);
    }

    /**
     * @return List of ColumnDefinition s that may be used for joining two or more tables so are considered to be keys
     */
    public List<ColumnDefinition> getKeys() {
        return columnDefinitions.stream().filter(columnDefinition -> columnDefinition.numberOfOccurrences() > 1)
                .collect(Collectors.toList());
    }

    /**
     * object is equal if is TableDefinition with same names and same columns
     * @param obj compared object
     * @return if the two instances are considered to be the same
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TableDefinition)) {
            return false;
        }
        TableDefinition objTable = (TableDefinition) obj;
        if (!this.tableName.equals(objTable.getName())) {
            return false;
        }
        if (!columnDefinitions.equals(objTable.getColumnDefinitions())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return tableName.hashCode();
    }

    /**
     * @return Declaration of table name and columns in CSV format
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getName());
        stringBuilder.append('\n');
        for (ColumnDefinition columnDefinition : getColumnDefinitions()) {
            stringBuilder.append(columnDefinition.getName());
            stringBuilder.append(',');
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append('\n');
        return stringBuilder.toString();
    }

}
