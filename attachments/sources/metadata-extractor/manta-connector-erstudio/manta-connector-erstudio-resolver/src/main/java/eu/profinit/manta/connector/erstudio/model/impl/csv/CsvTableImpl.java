package eu.profinit.manta.connector.erstudio.model.impl.csv;

import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;
import eu.profinit.manta.connector.erstudio.model.csv.CsvRowDefinition;
import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of CsvTable
 *
 * @author ddrobny
 */
public class CsvTableImpl implements CsvTable {
    private final CsvRowDefinition rowDefinition;
    private final String tableName;
    private final List<CsvRow> dataRows = new ArrayList<>();

    public CsvTableImpl(String name, CsvRowDefinition rowDef) {
        rowDefinition = rowDef;
        tableName = name;
    }

    /**
     * Appends last row to the table.
     * @param nextRow the row to append.
     */
    public void addNextRow(CsvRow nextRow) {
        dataRows.add(nextRow);
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public CsvRowDefinition getRowDefinition() {
        return rowDefinition;
    }

    @Override
    public int getDataRowCount() {
        return dataRows.size();
    }

    @Override
    public int getColumnCount() {
        return rowDefinition.getColumnCount();
    }

    @Override
    public int getIndexByName(String columnName) {
        return rowDefinition.getIndexByName(columnName);
    }

    @Override
    public CsvRow getDataRow(int index) {
        return dataRows.get(index);
    }

    @Override
    public String getValueFromDataRow(int rowIndex, String columnName) {
        return dataRows.get(rowIndex).getValue(columnName);
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public List<CsvRow> filter(String columnName, String conditionValue) {
        List<CsvRow> result = new ArrayList<>();
        for (CsvRow row : dataRows) {
            if (row.getValue(columnName).equals(conditionValue)) {
                result.add(row);
            }
        }
        return result;
    }

    /**
     * Returns an iterator over elements of type {@code CsvRow}.
     * @return an Iterator.
     */
    @Override
    public Iterator<CsvRow> iterator() {
        return dataRows.iterator();
    }
}
