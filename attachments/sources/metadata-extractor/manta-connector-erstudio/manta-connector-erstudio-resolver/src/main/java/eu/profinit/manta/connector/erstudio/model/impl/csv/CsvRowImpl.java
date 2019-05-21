package eu.profinit.manta.connector.erstudio.model.impl.csv;

import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;
import eu.profinit.manta.connector.erstudio.model.csv.CsvRowDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of CsvRow.
 *
 * @author ddrobny
 */
public class CsvRowImpl implements CsvRow {
    private final CsvRowDefinition rowDefinition;
    /** Values of the row in the same order as in source CSV table */
    private final List<String> values = new ArrayList<>();

    public CsvRowImpl(CsvRowDefinition rowDef) {
        rowDefinition = rowDef;
    }

    public CsvRowImpl(CsvRowDefinition rowDef, Collection<String> values) {
        rowDefinition = rowDef;
        addAllValues(values);
    }

    private void addAllValues(Collection<String> values) {
        for (String value : values) {
            addNextValue(value);
        }
    }

    private void addNextValue(String value) {
        values.add(value);
    }

    @Override
    public CsvRowDefinition getCsvRowDefinition() {
        return rowDefinition;
    }

    @Override
    public int getValueCount() {
        return values.size();
    }

    @Override
    public String getValue(int index) {
        return values.get(index);
    }

    @Override
    public String getValue(String columnName) {
        int index = rowDefinition.getIndexByName(columnName);
        return values.get(index);
    }

}
