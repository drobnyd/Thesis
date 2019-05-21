package cz.manta.drobny.DM1ReverseEngineer.table;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the name of a column and its data throughout all rows in a table.
 *
 * @author ddrobny
 */
public class Column {
    /** Name of the column. */
    private String name;
    /** Values stored in the column. */
    private List<String> values = new ArrayList<>();

    public Column(String name) {
        this.name = name;
    }

    /**
     * Adds next value to the column.
     * @param value textual field in the column.
     */
    public void addValue(String value) {
        values.add(value);
    }

    /**
     * Gets the name of the column.
     * @return the name of the column.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets value at the given index.
     * @param index index to get value from.
     * @return the value at the given index.
     */
    public String getValue(int index) {
        return values.get(index);
    }

    /**
     * Gets number of records in the column.
     * @return Number of entries in the column, not counting the compulsory definition of the column (thus can be 0 or more)
     */
    public int getNumOfRows() {
        return values.size();
    }

    /**
     * Gets all the values stored in the column.
     * @return all the values stored in the column.
     */
    public List<String> getValues() {
        return values;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Column)) {
            return false;
        }
        Column objColumn = (Column) obj;
        // Equals iff same name of every column and values in it as well
        if (!getName().equals(objColumn.getName())) {
            return false;
        }
        if (this.getNumOfRows() != objColumn.getNumOfRows()) {
            return false;
        }
        for (int i = 0; i < this.values.size(); i++) {
            if (!this.values.get(i).equals(objColumn.getValue(i))) {
                return false;
            }
        }
        return true;
    }

}
