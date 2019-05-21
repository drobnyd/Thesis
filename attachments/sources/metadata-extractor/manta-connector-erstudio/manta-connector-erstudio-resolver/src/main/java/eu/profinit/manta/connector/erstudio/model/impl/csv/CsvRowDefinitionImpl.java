package eu.profinit.manta.connector.erstudio.model.impl.csv;

import eu.profinit.manta.connector.erstudio.model.csv.CsvRowDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of CsvRow definition.
 *
 * @author ddrobny
 */
public class CsvRowDefinitionImpl implements CsvRowDefinition {
    /* Structure for quick getting index by name of column */
    private final Map<String, Integer> nameToIndex = new HashMap<>();

    public CsvRowDefinitionImpl(List<String> row) {
        for (int i = 0; i < row.size(); i++) {
            nameToIndex.put(row.get(i), i);
        }
    }

    @Override
    public int getIndexByName(String columnName) {
        assert nameToIndex.containsKey(columnName);
        return nameToIndex.get(columnName);
    }

    @Override
    public int getColumnCount() {
        return nameToIndex.size();
    }
}
