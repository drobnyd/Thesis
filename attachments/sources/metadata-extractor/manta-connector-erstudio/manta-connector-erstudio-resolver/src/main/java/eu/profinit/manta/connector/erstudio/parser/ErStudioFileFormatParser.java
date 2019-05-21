package eu.profinit.manta.connector.erstudio.parser;

import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;
import eu.profinit.manta.connector.erstudio.model.impl.csv.CsvRowDefinitionImpl;
import eu.profinit.manta.connector.erstudio.model.impl.csv.CsvRowImpl;
import eu.profinit.manta.connector.erstudio.model.impl.csv.CsvTableImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Assuming correct structure of .dm1 file - CSV tables separated by an empty line.
 * When expecting a table but newline is found, it's the end of a file.
 * In the file format the first line of a table entry is the name of the table.
 * Then there's a row with names of columns and on the following lines there's 0 or more rows with content
 *
 * @author ddrobny
 */
public class ErStudioFileFormatParser {
    private final String encoding;
    private BufferedReader reader;
    private String currentLine;

    public ErStudioFileFormatParser(String encoding) {
        this.encoding = encoding;
    }

    private CsvTableImpl parseNextTable() throws IOException {
        // Then there's definition of columns
        String tableName = currentLine;
        currentLine = reader.readLine();
        CsvRowDefinitionImpl rowDefinition = new CsvRowDefinitionImpl(parseCsvRow());
        CsvTableImpl table = new CsvTableImpl(tableName, rowDefinition);
        currentLine = reader.readLine();// Start of entries of the table. If this line is empty it has no contents
        while (currentLine != null && !currentLine.isEmpty()) {
            table.addNextRow(new CsvRowImpl(rowDefinition, parseCsvRow()));
            currentLine = reader.readLine();
        }
        return table;
    }

    /**
     * Loads CSV tables from file.
     * @param modelDefinition .dm1 file to load the tables from.
     * @return loaded tables.
     * @throws IOException when problem with reading {@code modelDefinition}.
     */
    public Map<String, CsvTable> readFile(File modelDefinition) throws IOException {
        // Looks like UTF-8 is the correct charset
        this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(modelDefinition), encoding));
        Map<String, CsvTable> tables = new HashMap<>();
        while ((currentLine = reader.readLine()) != null) {
            CsvTableImpl nextTable = parseNextTable();
            tables.put(nextTable.getName(), nextTable);
        }
        reader.close();
        reader = null;
        return tables;
    }

    /**
     * Extracts values from a CSV line, may read multiple lines of input in the case that newlines are under quotation
     * @return Parsed values from comma separated line format
     */
    private List<String> parseCsvRow() throws IOException {
        List<String> words = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        CsvParserStates state = CsvParserStates.INITIAL;
        char currentChar;
        int positionOfChar = 0;
        // Deterministic Finite Automaton
        while (true) { // Read until the whole data is not accepted(or rejected) by DFA
            switch (state) {
            case INITIAL:
                if (positionOfChar == currentLine.length()) {
                    state = CsvParserStates.FINAL;
                    break;
                } // End of one correct CSV line with data, the row should be done
                currentChar = currentLine.charAt(positionOfChar++);
                switch (currentChar) {
                case '"':
                    state = CsvParserStates.QUOTATION;
                    break;
                case ',':
                    state = CsvParserStates.VALUE_ACCEPTED;
                    break;
                default:
                    stringBuilder.append(currentChar);
                }
                break;
            case QUOTATION:
                while (positionOfChar == currentLine.length()) {
                    currentLine = reader.readLine();
                    stringBuilder.append('\n');
                    positionOfChar = 0;
                } // End of line is under quotation, next line has to be loaded, assuming correct file format so the next file exists
                currentChar = currentLine.charAt(positionOfChar++);
                switch (currentChar) {
                case '"':
                    state = CsvParserStates.ESCAPED;
                    break;
                default:
                    stringBuilder.append(currentChar);
                }
                break;
            case ESCAPED:
                if (positionOfChar == currentLine.length()) {
                    state = CsvParserStates.FINAL;
                    break;
                }
                currentChar = currentLine.charAt(positionOfChar++);
                switch (currentChar) {
                case '"':
                    state = CsvParserStates.QUOTATION;
                    stringBuilder.append(currentChar); // Add the escaped quotation
                    break;
                case ',':
                    state = CsvParserStates.VALUE_ACCEPTED;
                    break;
                default:
                    state = CsvParserStates.INITIAL;
                    stringBuilder.append(currentChar);
                    break;
                }
                break;
            case VALUE_ACCEPTED:
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
