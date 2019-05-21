package cz.manta.drobny.DM1ReverseEngineer.parser;

import cz.manta.drobny.DM1ReverseEngineer.table.ColumnDefinition;
import cz.manta.drobny.DM1ReverseEngineer.table.Table;
import cz.manta.drobny.DM1ReverseEngineer.table.TableDefinition;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Reads .dm1 file format textually and creates objects based on its contents
 *
 * @author ddrobny
 */
public class Parser {
    private boolean parseEmpty = true;
    protected Scanner fileScanner;
    private File modelDefinition; // .dm1 file

    public Parser(File modelDefinition) {
        this.modelDefinition = modelDefinition;
    }

    /**
     * Set scanner for the sake of (re)reading the modelDefinition
     */
    protected void setScanner() {
        try {
            this.fileScanner = new Scanner(modelDefinition);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtains tables from a .dm1 file
     * @return Tables filled with their contents
     */
    public List<Table> getTables() {
        setScanner();
        List<Table> tables = new ArrayList<>();
        while (fileScanner.hasNextLine()) {

            Table nextTable = parseTable((columns, tableName) -> new Table(tableName, columns),
                    (table, row) -> table.addRow(row));

            if (nextTable != null) {
                tables.add(nextTable);
            }
        }
        fileScanner.close();
        return tables;
    }

    /**
     * In cases where contents of tables are irrelevant and we want to obtain metadata only.
     * @return metadata of tables.
     */
    public List<TableDefinition> getTableDefinitions() {
        setScanner();
        List<TableDefinition> tables = new ArrayList<>();
        while (fileScanner.hasNextLine()) {

            TableDefinition nextTable = parseTable((columns, tableName) -> {
                List<ColumnDefinition> columnDefinitions = new ArrayList<>();
                columns.forEach(parsed -> columnDefinitions.add(new ColumnDefinition(parsed)));
                return new TableDefinition(columnDefinitions, tableName);
            }, (table, row) -> {
            });
            if (nextTable != null) {
                tables.add(nextTable);
            }
        }
        fileScanner.close();
        return tables;
    }

    /**
     * Find a table with specific name in the .dm1 file format
     * @param tableName name of table we look for
     * @return instance of the Table class with the specified name
     */
    public Table getTable(String tableName) {
        setScanner();
        Table result = null;

        while (fileScanner.hasNextLine()) {
            result = parseTable((columns, name) -> new Table(name, columns), (table, row) -> table.addRow(row));
            if (result != null && result.getName().equals(tableName)) {
                break;
            }
        }

        fileScanner.close();
        return result;
    }

    /**
     * Goes through a table in .dm1 file format and based on policies defined by tableFactory and row addition creates an instance of T
     * representing the table as a data structure.
     * @param tableFactory method returning a new instance of table of class T
     * @param rowAddition a strategy for handling next row to a table
     * @param <T> class representing a table in the .dm1 file format
     * @return
     */
    private <T> T parseTable(BiFunction<List<String>, String, T> tableFactory,
            BiConsumer<T, List<String>> rowAddition) {
        // Assuming correct contents of .md1 it ends after the last entry, once it got here there is supposed to a table
        // In the file format the first line of a table entry is name of the table
        String tableName = fileScanner.nextLine();
        String columnNames = fileScanner.nextLine(); // Then there's definition of columns
        List<String> columns = getValuesFromCSVString(columnNames);
        T table = tableFactory.apply(columns, tableName);

        String line = fileScanner.nextLine();// Start of entries of the table. If this line is empty it has no contents
        if (line.isEmpty()) {
            if (parseEmpty) {
                return table;
            }
            return null;
        }

        while (!line.isEmpty()) {
            rowAddition.accept(table, getValuesFromCSVString(line));
            if (!fileScanner.hasNextLine()) {
                break;
            }
            line = fileScanner.nextLine();
        }
        return table;
    }

    /**
     * Extracts values from a CSV line, may read multiple lines of input in the case that newlines are under quotation
     * @param data to be parsed
     * @return Parsed values from comma separated line format
     */
    private List<String> getValuesFromCSVString(String data) {
        List<String> words = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        CsvParserStates state = CsvParserStates.INITIAL;
        char currentChar;
        int positionOfChar = 0;
        // Finite Automaton
        while (true) { // Read until the whole data is not accepted(or rejected) by the automaton
            switch (state) {
            case INITIAL:
                if (positionOfChar == data.length()) {
                    state = CsvParserStates.FINAL;
                    break;
                } // End of one correct CSV line with data, the row should be done
                currentChar = data.charAt(positionOfChar++);
                stringBuilder.append(currentChar);
                switch (currentChar) {
                case '"':
                    state = CsvParserStates.QUOTATION;
                    break;
                case ',':
                    state = CsvParserStates.VALUE_ACCEPTED;
                    break;
                }
                break;
            case QUOTATION:
                while (positionOfChar == data.length()) {
                    data = fileScanner.nextLine();
                    stringBuilder.append('\n');
                    positionOfChar = 0;
                } // End of line is under quotation, next line has to be loaded, assuming correct file format so the next file exists
                currentChar = data.charAt(positionOfChar++);
                stringBuilder.append(currentChar);
                switch (currentChar) {
                case '"':
                    state = CsvParserStates.ESCAPED;
                    break;
                }
                break;
            case ESCAPED:
                if (positionOfChar == data.length()) {
                    state = CsvParserStates.FINAL;
                    break;
                }
                currentChar = data.charAt(positionOfChar++);
                stringBuilder.append(currentChar);
                switch (currentChar) {
                case '"':
                    state = CsvParserStates.QUOTATION;
                    break;
                case ',':
                    state = CsvParserStates.VALUE_ACCEPTED;
                    break;
                default:
                    state = CsvParserStates.INITIAL;
                    break;
                }
                break;
            case VALUE_ACCEPTED:
                stringBuilder.deleteCharAt(stringBuilder.length() - 1); // Remove separating comma from the value
                words.add(stringBuilder.toString()); // Add the accepted value to the result
                stringBuilder = new StringBuilder(); // Reset
                state = CsvParserStates.INITIAL;
                break;
            case FINAL:
                words.add(stringBuilder.toString()); // Add the accepted value to the result
                return words; // One row of table finalized
            }
        }
    }
}
