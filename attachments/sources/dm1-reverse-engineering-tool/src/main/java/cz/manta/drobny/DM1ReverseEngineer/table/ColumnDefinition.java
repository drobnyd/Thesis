package cz.manta.drobny.DM1ReverseEngineer.table;

import java.util.HashSet;

/**
 * Represents a column holding its name and references to TableDefinitions where the column is used
 * without values stored in the column
 *
 * @author ddrobny
 */
public class ColumnDefinition implements Definition {
    /** Name of the column */
    private String name;
    /** Tables where the column definition is used. */
    private HashSet<TableDefinition> whereUsed = new HashSet<>(); // Unique TableDefinition s where the column is used

    public ColumnDefinition(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the column.
     * @return the name of the column.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the tables where the column is used.
     * @return the tables where the column is used.
     */
    public HashSet<TableDefinition> getWhereUsed() {
        return whereUsed;
    }

    /**
     * Gets the names of tables where the column is used.
     * @return names of tables where the column is used.
     */
    public HashSet<String> getWhereUsedNames() {
        HashSet<String> result = new HashSet<>();
        whereUsed.stream().forEach(table -> result.add(table.getName()));
        return result;
    }

    /**
     * Gets how many times is the column used in tables.
     * @return how many times is the column used in tables.
     */
    public int numberOfOccurrences() {
        return whereUsed.size();
    }

    /**
     * Assign TableDefinition where the column is used to the ColumnDefinition. Duplicate TableDefinitions are not stored.
     * @param table to be added to whereUsed
     */
    public void addUsage(TableDefinition table) {
        whereUsed.add(table);
    }

    /**
     * A Column definition is identical with another instance iff they share the same name and they are used in same tables
     * @param obj compared object
     * @return is the two objects can be considered the same
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ColumnDefinition)) {
            return false;
        }
        ColumnDefinition objCol = (ColumnDefinition) obj;
        if (!this.getName().equals(objCol.getName())) {
            return false;
        }
        if (!this.getWhereUsedNames().equals(objCol.getWhereUsedNames())) {
            return false; // Getting tables where used only as string because comparing on table
        }
        // objects would lead to an infinite loop
        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Assigns the table to the column and also the column to the table.
     * @param table the table to be paired with the column.
     */
    public void assignTableDefinition(TableDefinition table) {
        table.assignColumnDefinition(this);
    }
}
