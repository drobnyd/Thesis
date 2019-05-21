package cz.manta.drobny.DM1ReverseEngineer;

import cz.manta.drobny.DM1ReverseEngineer.parser.Parser;
import cz.manta.drobny.DM1ReverseEngineer.table.Column;
import cz.manta.drobny.DM1ReverseEngineer.table.ColumnDefinition;
import cz.manta.drobny.DM1ReverseEngineer.table.Table;
import cz.manta.drobny.DM1ReverseEngineer.table.TableDefinition;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Translator of objects to SQL.
 *
 * @author ddrobny
 */
public class TablesToSQLConverter {
    private static final String IDENTITIES_TABLE_NAME = "Identity_Value";
    private static final int TABLE_NAME_COLUMN_POSITION = 1;
    private static final int KEY_NAME_COLUMN_POSITION = 2;
    private static final Map<String, Set<String>> MAPPED_COLUMNS; // Columns that represent the same but in different tables have different names, Keys are necessarily Primary keys of some table, defined in IDENTITIES_TABLE NAME
    private static final List<AbstractMap.SimpleEntry<String, String>> EMPIRICAL_TABLES_WITH_PRIMARY_KEYS; // Those pairs of table-key that are not logically named but we have assumed that the key is primary key of the corresponding table

    static {
        MAPPED_COLUMNS = new HashMap<>();
        Set<String> mapped = new HashSet<>();
        mapped.add("Global_User_ID");
        MAPPED_COLUMNS.put("UniqueObject_Id", mapped);
        mapped = new HashSet<>();
        mapped.add("A_Model_ID");
        mapped.add("B_Model_ID");
        MAPPED_COLUMNS.put("Model_ID", mapped);
        mapped = new HashSet<>();
        mapped.add("A_MetaTable_ID");
        mapped.add("B_MetaTable_ID");
        mapped.add("A_Meta_Table_ID");
        mapped.add("B_Meta_Table_ID");
        MAPPED_COLUMNS.put("MetaTable_ID", mapped);

        EMPIRICAL_TABLES_WITH_PRIMARY_KEYS = new ArrayList<>();
        EMPIRICAL_TABLES_WITH_PRIMARY_KEYS.add(new AbstractMap.SimpleEntry<>("ObjectDefinition", "ObjectType_Id"));
    }

    private PrintWriter writer;
    private Map<String, TableDefinition> tables;
    private Map<String, ColumnDefinition> uniqueColumnDefinitions;

    public TablesToSQLConverter(List<TableDefinition> tables, OutputStream outputStream) {
        writer = new PrintWriter(outputStream);
        this.tables = tables.stream().collect(Collectors.toMap(table -> table.getName(), table -> table));
    }

    /**
     * Write to the output SQL commands for creating table that .dm1 file format consists of together with defining of primary, unique and foreign keys
     * @param fromModel .dm1 file
     */
    public void writeMySQLSource(File fromModel) {
        for (TableDefinition entry : tables.values()) {  // Write to the file SQL commands to create the tables
            writer.println(getTableCreationMySQLString(entry));
        }
        defineKeys(fromModel); // Create SQL commands for defining primary and foreign keys
        writer.close();
    }

    /**
     * Writes definitions of primary, unique and foreign keys in SQL using the writer
     * @param modelDefinition name of the file where the .dm1 file is defined
     */
    private void defineKeys(File modelDefinition) {
        Parser parser = new Parser(modelDefinition);
        // Get the table where primary keys of tables in the .dm1 file are defined
        Table identitiesTable = parser.getTable(IDENTITIES_TABLE_NAME);

        // Create dependencies on tables and columns
        DependencyCreator dependencyCreator = new DependencyCreator(tables.values());

        // Key columns with their names that haven't been assigned yet
        Map<String, ColumnDefinition> unresolvedKeys = dependencyCreator.getKeys().stream()
                .collect(Collectors.toMap(entry -> entry.getName(), entry -> entry));

        // Go through pairs of tableName-keyName and make the key with KeyName primary key in the table with tableName
        uniqueColumnDefinitions = dependencyCreator.getUniqueColumnDefinitions();
        writeIdentityKeys(assignPrimaryKeysToTables(identitiesTable), unresolvedKeys);
        writeUnresolvedKeys(unresolvedKeys);
    }

    /**
     * Assign to tables their primary keys. Either explicitly mentioned in the .dm1 file structure, or implicitly guess them
     * @param identitiesTable table from .dm1 file structure that says in what column are stored IDs of tables
     * @return pairs of tables with their primary keys
     */
    private List<AbstractMap.SimpleEntry<String, String>> assignPrimaryKeysToTables(Table identitiesTable) {

        List<AbstractMap.SimpleEntry<String, String>> result = new ArrayList<>();
        HashMap<String, TableDefinition> tableWithoutPrimaryKey = new HashMap<>(tables);

        getPrimaryKeys(identitiesTable, tableWithoutPrimaryKey, result);
        getEmpiricalPrimaryKeys(tableWithoutPrimaryKey, result);
        inferPrimaryKeys(tableWithoutPrimaryKey, result);

        return result;
    }

    private void getEmpiricalPrimaryKeys(HashMap<String, TableDefinition> tableWithoutPrimaryKey,
            List<AbstractMap.SimpleEntry<String, String>> result) {

        for (AbstractMap.SimpleEntry<String, String> entry : EMPIRICAL_TABLES_WITH_PRIMARY_KEYS) {

            if (tableWithoutPrimaryKey.containsKey(entry.getKey())) {
                result.add(entry);
                tableWithoutPrimaryKey.remove(entry.getKey());
            }
        }
    }

    /**
     * Gets the the primary keys by table in .dm1 file format that assigns ID columns to tables
     */
    private void getPrimaryKeys(Table identitiesTable, HashMap<String, TableDefinition> tableWithoutPrimaryKey,
            List<AbstractMap.SimpleEntry<String, String>> result) {

        // Get the pairs lying on those positions in every row
        Column tableNames = identitiesTable.getColumns().get(TABLE_NAME_COLUMN_POSITION);

        Column keyNames = identitiesTable.getColumns().get(KEY_NAME_COLUMN_POSITION);

        // Add the values from identitiesTable
        for (int i = 0; i < tableNames.getNumOfRows(); i++) {
            result.add(new AbstractMap.SimpleEntry<>(tableNames.getValue(i), keyNames.getValue(i)));
            tableWithoutPrimaryKey.remove(tableNames.getValue(i));
        }
    }

    /**
     *  Try to infer Primary Keys from names
     */
    private void inferPrimaryKeys(HashMap<String, TableDefinition> tableWithoutPrimaryKey,
            List<AbstractMap.SimpleEntry<String, String>> result) {

        for (Map.Entry<String, TableDefinition> entry : tableWithoutPrimaryKey.entrySet()) {

            for (ColumnDefinition columnDefinition : entry.getValue().getColumnDefinitions()) {

                if ((entry.getKey() + "id").equalsIgnoreCase(columnDefinition.getName()) || (entry.getKey() + "_id")
                        .equalsIgnoreCase(columnDefinition.getName())) {

                    // Columns name is table name and some variation of ID at the end
                    result.add(new AbstractMap.SimpleEntry<>(entry.getKey(), columnDefinition.getName()));
                    break;
                }
            }
        }
    }

    /**
     * Writes the rest of columns based on what join can be done, but are not primary keys.
     * Chooses one table in usages of the column definitions of the key, sets as the origin of the key - it's the unique one there and foreign in every other
     * @param unresolvedKeys Keys that haven't been assigned yet as a primary key to any table
     */
    private void writeUnresolvedKeys(Map<String, ColumnDefinition> unresolvedKeys) {
        for (Map.Entry<String, ColumnDefinition> key : unresolvedKeys.entrySet()) {
            defineKey(key.getValue().getWhereUsed().iterator().next().getName(), key.getKey(), unresolvedKeys,
                    this::getUniqueConstraintKeyMySQLString);
        }
    }

    /**
     * Define primary keys and corresponding foreign keys of tables in .dm1 file format
     */
    private void writeIdentityKeys(List<AbstractMap.SimpleEntry<String, String>> pairs,
            Map<String, ColumnDefinition> unresolvedKeys) {
        // Entry is tableName-columnName
        for (AbstractMap.SimpleEntry<String, String> pair : pairs) {
            defineKey(pair.getKey(), pair.getValue(), unresolvedKeys,
                    this::getPrimaryKeyAdditionMySQLString); // Update Primary Key
            unresolvedKeys.remove(pair.getValue()); // The key has been resolved
            linkForeignKeysWithDifferentlyNamedPrimaryKeys(pair.getKey(), pair.getValue(), unresolvedKeys);
        }
    }

    /**
     * Writes foreign key definitions.
     * If there's a primary key that has a different name in some table it's stored in MAPPED_COLUMNS, based on that Primary-Foreign key relation is created.
     * @param primaryTableName the name of the table containing the primary key column.
     * @param primaryKeyName the name of the primary key column.
     * @param unresolvedKeys the candidates that haven't been assigned yet.
     */
    private void linkForeignKeysWithDifferentlyNamedPrimaryKeys(String primaryTableName, String primaryKeyName,
            Map<String, ColumnDefinition> unresolvedKeys) {
        if (MAPPED_COLUMNS.containsKey(primaryKeyName)) {
            for (String foreignKeyName : MAPPED_COLUMNS.get(primaryKeyName)) {

                if (unresolvedKeys.containsKey(foreignKeyName)) { // If the mapped column is a key

                    // Get the tables where the foreign key with different name is used
                    HashSet<TableDefinition> whereUsedForeignKey = unresolvedKeys.get(foreignKeyName).getWhereUsed();

                    for (TableDefinition table : whereUsedForeignKey) {
                        writer.println(
                                getForeignKeyAdditionMySQLString(table.getName(), foreignKeyName, primaryTableName,
                                        primaryKeyName));
                    }
                    unresolvedKeys.remove(foreignKeyName); // Does not have to be resolved anymore

                } else {
                    // Check if such column really exists
                    if (uniqueColumnDefinitions.get(foreignKeyName) != null) {
                        // Not a key candidate, so must be used only in one table
                        assert uniqueColumnDefinitions.get(foreignKeyName).getWhereUsed().size() == 1;

                        // Get the name of the only table
                        String foreignKeysTableName = uniqueColumnDefinitions.get(foreignKeyName).getWhereUsed()
                                .iterator().next().getName();
                        writer.println(
                                getForeignKeyAdditionMySQLString(foreignKeysTableName, foreignKeyName, primaryTableName,
                                        primaryKeyName));
                    }
                }
            }
        }
    }

    /**
     * Writes SQL code using the writer for defining the key as a primary one in the specified table and as primary and
     * in the rest of the tables where the key is used as foreign key
     * @param primaryTableName Name of the table where the key is primary
     * @param keyName Name of the key
     * @param unresolvedKeys Keys that haven't been assigned yet as a primary key to any table
     */
    private void defineKey(String primaryTableName, String keyName, Map<String, ColumnDefinition> unresolvedKeys,
            BiFunction<String, String, String> keyCreator) {
        if (tables.containsKey(primaryTableName)) {
            writer.println(keyCreator.apply(primaryTableName, keyName));
        }
        // The definition of primary key has to be before foreign keys. But added only if the table exists
        // A key is set as primary only if it's explicitly stated in the identity columns in the .dm1 file format
        if (!unresolvedKeys.containsKey(keyName)) {
            return; // If the key hasn't been used for joining so the whereUsed field is non existing
        }
        for (TableDefinition table : unresolvedKeys.get(keyName).getWhereUsed()) {
            if (!table.getName().equals(primaryTableName)) {
                writer.println(getForeignKeyAdditionMySQLString(table.getName(), keyName, primaryTableName, keyName));
            }
        }
    }

    /**
     * @param tableName
     * @param keyName
     * @return String of SQL code that creates Primary key
     */
    private String getPrimaryKeyAdditionMySQLString(String tableName, String keyName) {
        return "ALTER TABLE " + tableName + "\nADD PRIMARY KEY(" + keyName + ");\n";
    }

    /**
     * @param tableName
     * @param keyName
     * @return String of SQL code that creates unique constraint on the key
     */
    private String getUniqueConstraintKeyMySQLString(String tableName, String keyName) {
        return "ALTER TABLE " + tableName + "\nADD UNIQUE(" + keyName + ");\n";
    }

    /**
     * @return String of SQL code that creates Foreign key
     */
    private String getForeignKeyAdditionMySQLString(String tableName, String foreignKeyName, String linkWithTableName,
            String primaryKeyName) {
        return "ALTER TABLE " + tableName + "\nADD CONSTRAINT FK_" + tableName + linkWithTableName + "\nFOREIGN KEY ("
                + foreignKeyName + ") REFERENCES " + linkWithTableName + '(' + primaryKeyName + ");\n";
    }

    /**
     * @param table
     * @return String of SQL code that creates Table
     */
    private String getTableCreationMySQLString(TableDefinition table) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE ");
        stringBuilder.append(table.getName());
        stringBuilder.append(" (\n");
        for (ColumnDefinition columnDefinition : table.getColumnDefinitions()) {
            stringBuilder.append(columnDefinition.getName());
            stringBuilder.append(" VARCHAR(50),\n");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 2);
        stringBuilder.append(");\n");
        return stringBuilder.toString();
    }

}
