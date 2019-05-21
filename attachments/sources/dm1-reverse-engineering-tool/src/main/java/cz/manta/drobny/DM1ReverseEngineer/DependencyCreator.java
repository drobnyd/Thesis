package cz.manta.drobny.DM1ReverseEngineer;

import cz.manta.drobny.DM1ReverseEngineer.table.ColumnDefinition;
import cz.manta.drobny.DM1ReverseEngineer.table.TableDefinition;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Creates between tables via ColumnDefinitions, when a column with same name is used across multiple tables, only one instance of the ColumnDefinition
 * is used and it has the information about in what TableDefinition instance it is used and creates the links
 *
 * @author ddrobny
 */
public class DependencyCreator {
    protected static final List<String> BLACKLIST = Arrays.asList("A_Table_Row_ID",
            "B_Table_Row_ID");//"GUID","Model_ID","Submodel_ID","DiagramId","ModelId","SubmodelId"); - too frequent ones holding too little information.

    private Map<String, ColumnDefinition> uniqueColumnDefinitions = new HashMap<>();
    private Collection<TableDefinition> tables;

    public DependencyCreator(Collection<TableDefinition> tables) {
        this.tables = tables;
        createDependencies();
    }

    public Map<String, ColumnDefinition> getUniqueColumnDefinitions() {
        return uniqueColumnDefinitions;
    }

    /**
     * Creates between tables via ColumnDefinitions, when a column with same name is used across multiple tables, only one instance of the ColumnDefinition
     * is used and it has the information about in what TableDefinition instance it is used and creates the links
     */
    private void createDependencies() {
        for (TableDefinition tableDefinition : tables) {

            HashSet<ColumnDefinition> snapshot = new HashSet<>(tableDefinition
                    .getColumnDefinitions()); // For the sake of not changing the collection when iterating over it

            for (ColumnDefinition columnDefinition : snapshot) {

                if (isOnBlackList(columnDefinition.getName())) {
                    continue; // Don't join on the column
                }

                // The first occurrence of ColumnDefinition with this name, mark it and use this instance for every next ColumnDefinition with the very same name
                if (!uniqueColumnDefinitions.containsKey(columnDefinition.getName())) {
                    uniqueColumnDefinitions.put(columnDefinition.getName(), columnDefinition);

                } else { // If there's already a ColumnDefinition with the same name replace it with it

                    tableDefinition.removeColumnDefinition(columnDefinition);
                    tableDefinition.assignColumnDefinition(uniqueColumnDefinitions.get(columnDefinition.getName()));
                }
            }
        }
    }

    /**
     * @return ColumnDefinitions that may be used for joining two or more tables
     */
    public Collection<ColumnDefinition> getKeys() {
        return uniqueColumnDefinitions.values().stream().filter(value -> value.getWhereUsed().size() > 1)
                .collect(Collectors.toSet());
    }

    /**
     * Filters names of columns that are not specific enough to be considered keys
     * @param columnName to be examined if is blacklisted or not
     * @return if the columnName is relevant or not
     */
    protected boolean isOnBlackList(String columnName) {
        if (columnName.length() < 2 || !columnName.substring(columnName.length() - 2).equalsIgnoreCase("id")) {
            return true; // Filters not ending with ID
        }
        return BLACKLIST.contains(columnName);
    }
}
