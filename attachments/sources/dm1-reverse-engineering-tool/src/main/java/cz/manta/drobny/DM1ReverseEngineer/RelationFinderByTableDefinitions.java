package cz.manta.drobny.DM1ReverseEngineer;

import cz.manta.drobny.DM1ReverseEngineer.table.ColumnDefinition;
import cz.manta.drobny.DM1ReverseEngineer.table.Definition;
import cz.manta.drobny.DM1ReverseEngineer.table.TableDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class for querying over TableDefinition instances.
 *
 * @author ddrobny
 */
public class RelationFinderByTableDefinitions {
    Map<String, TableDefinition> tables;

    public RelationFinderByTableDefinitions(Collection<TableDefinition> tables) {
        new DependencyCreator(tables); // Creates dependencies
        this.tables = tables.stream().collect(Collectors.toMap(TableDefinition::getName, e -> e));
    }

    public Collection<List<Definition>> getDependenciesWithPaths(String rootTableName) {
        if (!tables.containsKey(rootTableName)) {
            return null;
        } // If there exists a table with the rootTable name, get it and start computing

        TableDefinition rootTable = tables.get(rootTableName);

        Map<TableDefinition, List<Definition>> result = new HashMap<>(); // Keys are names of tables, value is sequence of elements from the root table there

        List<Definition> prefix = new ArrayList<>();
        prefix.add(rootTable);
        result.put(rootTable, prefix);

        for (ColumnDefinition columnDefinition : rootTable.getKeys()) {
            findDependenciesViaColumn(prefix, columnDefinition, result);
        }
        return result.values();
    }

    public List<Definition> getPathBetween(String rootTableName, String targetName) {
        if (!tables.containsKey(rootTableName) || !tables.containsKey(targetName)) {
            return null;
        } // If there exists a table with the rootTable name, get it and start computing

        TableDefinition rootTable = tables.get(rootTableName);
        TableDefinition targetTable = tables.get(targetName);
        Map<TableDefinition, List<Definition>> result = new HashMap<>(); // Keys are names of tables, value is sequence of elements from the root table there

        result.put(rootTable, new ArrayList<>());
        List<Definition> prefix = new ArrayList<>();
        prefix.add(rootTable);

        for (ColumnDefinition columnDefinition : rootTable.getKeys()) {
            findDependenciesViaColumn(prefix, columnDefinition, result);
        }
        return result.get(targetTable);
    }

    private void findDependenciesViaColumn(List<Definition> prefix, ColumnDefinition columnDefinition,
            Map<TableDefinition, List<Definition>> dependencies) {
        for (TableDefinition tableDefinition : columnDefinition.getWhereUsed()) {

            if (dependencies.containsKey(tableDefinition)) {
                continue;
            }

            List<Definition> path = new ArrayList<>(prefix);

            path.add(columnDefinition);
            path.add(tableDefinition);
            dependencies.put(tableDefinition, path);

            for (ColumnDefinition nextColumn : tableDefinition.getColumnDefinitions()) {
                findDependenciesViaColumn(path, nextColumn, dependencies);
            }
        }
    }

    private String writePath(List<Definition> entry) {
        StringBuilder stringBuilder = new StringBuilder();
        if (entry == null) {
            return null;
        }
        for (Definition def : entry) {
            stringBuilder.append(def.getName());
            stringBuilder.append(" <-> ");
        }
        if (stringBuilder.length() > 3) {
            stringBuilder.delete(stringBuilder.length() - 4, stringBuilder.length());
        }
        return stringBuilder.toString();
    }

}
