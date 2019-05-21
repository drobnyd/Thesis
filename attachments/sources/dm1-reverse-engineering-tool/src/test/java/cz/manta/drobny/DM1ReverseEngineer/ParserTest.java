package cz.manta.drobny.DM1ReverseEngineer;

import cz.manta.drobny.DM1ReverseEngineer.parser.Parser;
import cz.manta.drobny.DM1ReverseEngineer.table.ColumnDefinition;
import cz.manta.drobny.DM1ReverseEngineer.table.Table;
import cz.manta.drobny.DM1ReverseEngineer.table.TableDefinition;
import org.junit.After;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Parser tests.
 *
 * @author ddrobny
 */
public class ParserTest {
    private Parser parserTested;
    // Necessary and default content for building a table
    private String tableName = "testTable\n";
    private String columnsDefinition = "c0,c1,c2,c3\n";
    private String contents = "a,b,c,d\ne,f,g,h\ni,j,k,l\n";

    /**
     * Making sure the tested table is correctly assigned in every test method by setting it to null after each execution
     */
    @After
    public void tearDown() {
        this.parserTested = null;
    }

    /**
     * Single table, trivial values
     */
    @Test
    public void getTablesBasicTest() {
        // Building the representation of the table
        List<Table> resultTables;
        System.setIn(new ByteArrayInputStream(
                (tableName + columnsDefinition + contents).getBytes())); // Mocking input from file
        this.parserTested = new TestParser(
                new Scanner(System.in)); // This line must be introduced once the System input is set
        resultTables = parserTested.getTables();

        // Create an equal table object
        List<Table> prototypeTables = new ArrayList<>();
        Table table = new Table("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        table.addRow(Arrays.asList("a", "b", "c", "d"));
        table.addRow(Arrays.asList("e", "f", "g", "h"));
        table.addRow(Arrays.asList("i", "j", "k", "l"));
        prototypeTables.add(table);

        runAssertions(prototypeTables, resultTables);
    }

    /**
     * Two tables, only one column in each of them
     */
    @Test
    public void getTablesSingleColumnsTest() {
        // Building the representation of the table
        List<Table> resultTables;
        columnsDefinition = "c0\n";
        contents = "a\ne\ni\n";
        String c1 = "aaa\neee\niii\n";
        System.setIn(new ByteArrayInputStream(
                (tableName + columnsDefinition + contents + "\n" + tableName + columnsDefinition + c1)
                        .getBytes())); // Mocking input from file
        this.parserTested = new TestParser(
                new Scanner(System.in)); // This line must be introduced once the System input is set
        resultTables = parserTested.getTables();

        // Create an equal table object
        List<Table> prototypeTables = new ArrayList<>();
        Table table = new Table("testTable", Arrays.asList("c0"));
        table.addRow(Arrays.asList("a"));
        table.addRow(Arrays.asList("e"));
        table.addRow(Collections.singletonList("i"));
        prototypeTables.add(table);
        table = new Table("testTable", Arrays.asList("c0"));
        table.addRow(Arrays.asList("aaa"));
        table.addRow(Arrays.asList("eee"));
        table.addRow(Arrays.asList("iii"));
        prototypeTables.add(table);

        runAssertions(prototypeTables, resultTables);
    }

    /**
     * Single table, variations of fields with no values
     */
    @Test
    public void getTablesEmptyFieldsTest() {
        // Building the representation of the table
        List<Table> resultTables;
        contents = ",b,c,d\ne,,g,h\ni,,,l\nm,n,o,\n,,,\n" + ",bbb,ccc,d\ne,,ggg,h\niii,,,lll\nmmm,n,o,\n,,,\n";
        System.setIn(new ByteArrayInputStream(
                (tableName + columnsDefinition + contents).getBytes())); // Mocking input from file
        this.parserTested = new TestParser(
                new Scanner(System.in)); // This line must be introduced once the System input is set
        resultTables = parserTested.getTables();

        // Create an equal table object
        List<Table> prototypeTables = new ArrayList<>();
        Table table = new Table("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        table.addRow(Arrays.asList("", "b", "c", "d"));
        table.addRow(Arrays.asList("e", "", "g", "h"));
        table.addRow(Arrays.asList("i", "", "", "l"));
        table.addRow(Arrays.asList("m", "n", "o", ""));
        table.addRow(Arrays.asList("", "", "", ""));
        table.addRow(Arrays.asList("", "bbb", "ccc", "d"));
        table.addRow(Arrays.asList("e", "", "ggg", "h"));
        table.addRow(Arrays.asList("iii", "", "", "lll"));
        table.addRow(Arrays.asList("mmm", "n", "o", ""));
        table.addRow(Arrays.asList("", "", "", ""));
        prototypeTables.add(table);

        runAssertions(prototypeTables, resultTables);
    }

    /**
     * Three tables, trivial values
     */
    @Test
    public void getTablesBasicMultipleTablesTest() {
        // Building the representation of the table and providing it to the parser via stdin that is being read by scanner
        List<Table> resultTables;
        System.setIn(new ByteArrayInputStream(
                (tableName + columnsDefinition + contents + "\n" + tableName + columnsDefinition + contents + "\n"
                        + tableName + columnsDefinition + contents + "\n").getBytes())); // Mocking input from file
        this.parserTested = new TestParser(
                new Scanner(System.in)); // This line must be introduced once the System input is set
        resultTables = parserTested.getTables();

        // Create an equal table object
        List<Table> prototypeTables = new ArrayList<>();
        Table table = new Table("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        table.addRow(Arrays.asList("a", "b", "c", "d"));
        table.addRow(Arrays.asList("e", "f", "g", "h"));
        table.addRow(Arrays.asList("i", "j", "k", "l"));
        prototypeTables.add(table);
        prototypeTables.add(table);
        prototypeTables.add(table);

        runAssertions(prototypeTables, resultTables);
    }

    /**
     * Single table, quoted strings containing commas
     */
    @Test
    public void getTablesBasicQuotationsTest() {
        // Building the representation of the table
        List<Table> resultTables;
        contents = "\"\",b,c,d\n\"in,quotation\",\"in,quotation\",\"in,quotation\",\"in,quotation\"\n\"x\",\"y\",\"z\",\"\"\n";
        System.setIn(new ByteArrayInputStream(
                (tableName + columnsDefinition + contents).getBytes())); // Mocking input from file
        this.parserTested = new TestParser(
                new Scanner(System.in)); // This line must be introduced once the System input is set
        resultTables = parserTested.getTables();

        // Create an equal table object
        List<Table> prototypeTables = new ArrayList<>();
        Table table = new Table("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        table.addRow(Arrays.asList("\"\"", "b", "c", "d"));
        table.addRow(Arrays.asList("\"in,quotation\"", "\"in,quotation\"", "\"in,quotation\"", "\"in,quotation\""));
        table.addRow(Arrays.asList("\"x\"", "\"y\"", "\"z\"", "\"\""));
        prototypeTables.add(table);

        runAssertions(prototypeTables, resultTables);
    }

    /**
     * Three tables, quotations, empty fields, regular fields, going from unquoted to quoted variations inside single field
     */
    @Test
    public void getTablesBasicQuotationsMultipleTablesTest() {
        // Building the representation of the table
        List<Table> resultTables;
        contents = "\"\",b,c,d\n\"in\",\"in\",\"in\",w\"in\"nnie\n\"x\",\"y\",\"z\",\"\"\nw\"in\",\"in\",w\"in\",\n";
        String c1 = "\"\",b,c,d\n\"in,quotation\",,\"in,quotation\",\n\"x\",\"y\",\"z\",\"\"\n";
        String c2 = ",b,c,d\ne,,g,h\ni,,,l\nm,n,o,\n,,,\n" + ",bbb,ccc,d\ne,,ggg,h\niii,,,lll\nmmm,n,o,\n,,,\n";
        System.setIn(new ByteArrayInputStream(
                (tableName + columnsDefinition + c1 + "\n" + tableName + columnsDefinition + contents + "\n" + tableName
                        + columnsDefinition + c2).getBytes())); // Mocking input from file
        this.parserTested = new TestParser(
                new Scanner(System.in)); // This line must be introduced once the System input is set
        resultTables = parserTested.getTables();

        // Create an equal table object
        List<Table> prototypeTables = new ArrayList<>();
        Table table = new Table("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        table.addRow(Arrays.asList("\"\"", "b", "c", "d"));
        table.addRow(Arrays.asList("\"in,quotation\"", "", "\"in,quotation\"", ""));
        table.addRow(Arrays.asList("\"x\"", "\"y\"", "\"z\"", "\"\""));
        prototypeTables.add(table);
        table = new Table("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        table.addRow(Arrays.asList("\"\"", "b", "c", "d"));
        table.addRow(Arrays.asList("\"in\"", "\"in\"", "\"in\"", "w\"in\"nnie"));
        table.addRow(Arrays.asList("\"x\"", "\"y\"", "\"z\"", "\"\""));
        table.addRow(Arrays.asList("w\"in\"", "\"in\"", "w\"in\"", ""));
        prototypeTables.add(table);
        table = new Table("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        table.addRow(Arrays.asList("", "b", "c", "d"));
        table.addRow(Arrays.asList("e", "", "g", "h"));
        table.addRow(Arrays.asList("i", "", "", "l"));
        table.addRow(Arrays.asList("m", "n", "o", ""));
        table.addRow(Arrays.asList("", "", "", ""));
        table.addRow(Arrays.asList("", "bbb", "ccc", "d"));
        table.addRow(Arrays.asList("e", "", "ggg", "h"));
        table.addRow(Arrays.asList("iii", "", "", "lll"));
        table.addRow(Arrays.asList("mmm", "n", "o", ""));
        table.addRow(Arrays.asList("", "", "", ""));
        prototypeTables.add(table);

        runAssertions(prototypeTables, resultTables);
    }

    /**
     * Single table, newlines inside quoted fields
     */
    @Test
    public void getTablesNewlineTest() {
        // Building the representation of the table
        List<Table> resultTables;
        contents = "\"a\naa\",\"b\nbb\",\"c\ncc\",\"\n\"\n\"e\",\"f\nf\nf\",\"\ng\ng\ng\n\",\"h\nhhh\"\n";
        System.setIn(new ByteArrayInputStream(
                (tableName + columnsDefinition + contents).getBytes())); // Mocking input from file
        this.parserTested = new TestParser(
                new Scanner(System.in)); // This line must be introduced once the System input is set
        resultTables = parserTested.getTables();

        // Create an equal table object
        List<Table> prototypeTables = new ArrayList<>();
        Table table = new Table("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        table.addRow(Arrays.asList("\"a\naa\"", "\"b\nbb\"", "\"c\ncc\"", "\"\n\""));
        table.addRow(Arrays.asList("\"e\"", "\"f\nf\nf\"", "\"\ng\ng\ng\n\"", "\"h\nhhh\""));
        prototypeTables.add(table);

        runAssertions(prototypeTables, resultTables);
    }

    /**
     * More complex test with four tables and multiple different invariants
     */
    @Test
    public void getTablesTest() {
        contents = "\"a\naa\",\"b\nbb\",\"c\ncc\",\"\n\"\n\"e\",\"f\nf\nf\",\"\ng\ng\ng\n\",\",h\n\n\n\n\"\"h\"\"\"'\"h\nh\n\"\n";
        String c1 = "\"\",b,c,d\n\"in,quo,ta\"\",\"\"tion\",,\"in,quotation\",\n\"x\",\"y\",\"z\",\"\"\n";
        String c2 = ",b,c,d\ne,,g,h\ni,,,l\nm,n,o,\n,,,\n" + ",bbb,ccc,d\ne,,ggg,h\niii,,,lll\nmmm,n,o,\n,,,\n";
        String c3 = "\"\",b,c,d\n\"in\",\"in\",\"in\",w\"in\"nnie\n\"x\",\"y\",\"z\",\"\"\nw\"in\",\"in\",w\"in\",\n";
        List<Table> resultTables;
        System.setIn(new ByteArrayInputStream(
                (tableName + columnsDefinition + contents + "\n" + tableName + columnsDefinition + c1 + "\n" + tableName
                        + columnsDefinition + c2 + "\n" + tableName + columnsDefinition + c3 + "\n")
                        .getBytes())); // Mocking input from file
        this.parserTested = new TestParser(
                new Scanner(System.in)); // This line must be introduced once the System input is set
        resultTables = parserTested.getTables();

        // Create an equal table object
        List<Table> prototypeTables = new ArrayList<>();
        Table table = new Table("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        table.addRow(Arrays.asList("\"a\naa\"", "\"b\nbb\"", "\"c\ncc\"", "\"\n\""));
        table.addRow(Arrays.asList("\"e\"", "\"f\nf\nf\"", "\"\ng\ng\ng\n\"", "\",h\n\n\n\n\"\"h\"\"\"'\"h\nh\n\""));
        prototypeTables.add(table);
        table = new Table("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        table.addRow(Arrays.asList("\"\"", "b", "c", "d"));
        table.addRow(Arrays.asList("\"in,quo,ta\"\",\"\"tion\"", "", "\"in,quotation\"", ""));
        table.addRow(Arrays.asList("\"x\"", "\"y\"", "\"z\"", "\"\""));
        prototypeTables.add(table);
        table = new Table("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        table.addRow(Arrays.asList("", "b", "c", "d"));
        table.addRow(Arrays.asList("e", "", "g", "h"));
        table.addRow(Arrays.asList("i", "", "", "l"));
        table.addRow(Arrays.asList("m", "n", "o", ""));
        table.addRow(Arrays.asList("", "", "", ""));
        table.addRow(Arrays.asList("", "bbb", "ccc", "d"));
        table.addRow(Arrays.asList("e", "", "ggg", "h"));
        table.addRow(Arrays.asList("iii", "", "", "lll"));
        table.addRow(Arrays.asList("mmm", "n", "o", ""));
        table.addRow(Arrays.asList("", "", "", ""));
        prototypeTables.add(table);
        table = new Table("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        table.addRow(Arrays.asList("\"\"", "b", "c", "d"));
        table.addRow(Arrays.asList("\"in\"", "\"in\"", "\"in\"", "w\"in\"nnie"));
        table.addRow(Arrays.asList("\"x\"", "\"y\"", "\"z\"", "\"\""));
        table.addRow(Arrays.asList("w\"in\"", "\"in\"", "w\"in\"", ""));
        prototypeTables.add(table);

        runAssertions(prototypeTables, resultTables);
    }

    /**
     * Set of assertions that are executed on every table that was parsed to determine if the process was correct
     * @param prototypeTables standard, correct set of tables
     * @param resultTables result tables that come from parsing CSV from input
     */
    private <T> void runAssertions(List<T> prototypeTables, List<T> resultTables) {
        // Assertions
        // For each table check if the following hold
        boolean hasCorrectSize = resultTables.size() == prototypeTables.size();
        assertEquals(hasCorrectSize, true);
        assertNotNull(resultTables);
        assertEquals(resultTables, prototypeTables);
    }

    // ------- Few metadata tests ----- //

    /**
     * Single table, trivial values
     */
    @Test
    public void getTableDefinitionsBasicTest() {
        // Building the representation of the table
        List<TableDefinition> resultTables;
        System.setIn(new ByteArrayInputStream(
                (tableName + columnsDefinition + contents).getBytes())); // Mocking input from file
        this.parserTested = new TestParser(
                new Scanner(System.in)); // This line must be introduced once the System input is set
        resultTables = parserTested.getTableDefinitions();
        // Create an equal table object
        List<TableDefinition> prototypeTables = new ArrayList<>();
        List<ColumnDefinition> columnDefinitions = new ArrayList<>();
        TableDefinition table = new TableDefinition("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        prototypeTables.add(table);

        runAssertions(prototypeTables, resultTables);
    }

    /**
     * Three tables, trivial values
     */
    @Test
    public void getTableDefinitionsBasicMultipleTablesTest() {
        // Building the representation of the table and providing it to the parser via stdin that is being read by scanner
        List<TableDefinition> resultTables;
        System.setIn(new ByteArrayInputStream(
                (tableName + columnsDefinition + contents + "\n" + tableName + columnsDefinition + contents + "\n"
                        + tableName + columnsDefinition + contents + "\n").getBytes())); // Mocking input from file
        this.parserTested = new TestParser(
                new Scanner(System.in)); // This line must be introduced once the System input is set
        resultTables = parserTested.getTableDefinitions();
        List<TableDefinition> prototypeTables = new ArrayList<>();
        // Create an equal table object
        TableDefinition table = new TableDefinition("testTable", Arrays.asList("c0", "c1", "c2", "c3"));
        prototypeTables.add(table);
        prototypeTables.add(table);
        prototypeTables.add(table);

        runAssertions(prototypeTables, resultTables);
    }

}