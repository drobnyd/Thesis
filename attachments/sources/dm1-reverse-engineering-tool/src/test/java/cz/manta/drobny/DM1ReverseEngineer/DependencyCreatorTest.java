package cz.manta.drobny.DM1ReverseEngineer;

import cz.manta.drobny.DM1ReverseEngineer.table.ColumnDefinition;
import cz.manta.drobny.DM1ReverseEngineer.table.TableDefinition;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests of DependencyCreator.
 *
 * @author ddrobny
 */
public class DependencyCreatorTest {
    TestDependencyCreator dependencyCreator;

    @After
    public void tearDown() {
        dependencyCreator = null;
    }

    /**
     * Five tables, no tuple of them share a key
     */
    @Test
    public void getAllJoinableTablesGroupsNotConnectedTest() {

        // Prepare data
        List<TableDefinition> tables = new ArrayList<>();
        TableDefinition table = new TableDefinition("t1", Arrays.asList("a", "b", "c"));
        tables.add(table);
        table = new TableDefinition("t2", Arrays.asList("d", "e"));
        tables.add(table);
        table = new TableDefinition("t3", Arrays.asList("F", "g", "h", "i"));
        tables.add(table);
        table = new TableDefinition("t4", Arrays.asList("j", "k"));
        tables.add(table);
        table = new TableDefinition("t5", Arrays.asList("l"));
        tables.add(table);

        // Prepare prototypes
        List<TableDefinition> prototypes = new ArrayList<>();
        table = new TableDefinition("t1", Arrays.asList("a", "b", "c"));
        prototypes.add(table);
        table = new TableDefinition("t2", Arrays.asList("d", "e"));
        prototypes.add(table);
        table = new TableDefinition("t3", Arrays.asList("F", "g", "h", "i"));
        prototypes.add(table);
        table = new TableDefinition("t4", Arrays.asList("j", "k"));
        prototypes.add(table);
        table = new TableDefinition("t5", Arrays.asList("l"));
        prototypes.add(table);

        // Get results
        dependencyCreator = new TestDependencyCreator(tables);

        // Verify
        runAssertions(tables, prototypes);
    }

    /**
     * Two tables connected by one key
     */
    @Test
    public void getAllJoinableTablesGroupsTwoTablesConnectedTest() {
        // Prepare data
        List<TableDefinition> tables = new ArrayList<>();
        TableDefinition table = new TableDefinition("t1", Arrays.asList("a", "b", "c"));
        tables.add(table);
        table = new TableDefinition("t2", Arrays.asList("d", "c"));
        tables.add(table);

        // Prepare prototypes
        List<TableDefinition> prototypes = new ArrayList<>();
        table = new TableDefinition("t1");
        ColumnDefinition a = new ColumnDefinition("a");
        ColumnDefinition b = new ColumnDefinition("b");
        ColumnDefinition c = new ColumnDefinition("c");
        table.assignColumnDefinition(a);
        table.assignColumnDefinition(b);
        table.assignColumnDefinition(c);
        prototypes.add(table);
        table = new TableDefinition("t2");
        ColumnDefinition d = new ColumnDefinition("d");
        table.assignColumnDefinition(c);
        table.assignColumnDefinition(d);
        prototypes.add(table);

        // Get results
        dependencyCreator = new TestDependencyCreator(tables);

        // Verify
        runAssertions(tables, prototypes);
    }

    /**
     * Three tables connected by one key
     */
    @Test
    public void getAllJoinableTablesGroupsThreeTablesConnectedByOneTest() {
        // Prepare data
        List<TableDefinition> results = new ArrayList<>();
        TableDefinition table = new TableDefinition("t1", Arrays.asList("a", "b", "c"));
        results.add(table);
        table = new TableDefinition("t2", Arrays.asList("c", "d"));
        results.add(table);
        table = new TableDefinition("t3", Arrays.asList("e", "f", "g", "h", "i", "j", "c"));
        results.add(table);

        // Prepare prototypes
        List<TableDefinition> prototypes = new ArrayList<>();
        table = new TableDefinition("t1");
        ColumnDefinition a = new ColumnDefinition("a");
        ColumnDefinition b = new ColumnDefinition("b");
        ColumnDefinition c = new ColumnDefinition("c");
        table.assignColumnDefinition(a);
        table.assignColumnDefinition(b);
        table.assignColumnDefinition(c);
        prototypes.add(table);
        table = new TableDefinition("t2");
        ColumnDefinition d = new ColumnDefinition("d");
        table.assignColumnDefinition(c);
        table.assignColumnDefinition(d);
        prototypes.add(table);
        table = new TableDefinition("t3");
        ColumnDefinition e = new ColumnDefinition("e");
        ColumnDefinition f = new ColumnDefinition("f");
        ColumnDefinition g = new ColumnDefinition("g");
        ColumnDefinition h = new ColumnDefinition("h");
        ColumnDefinition i = new ColumnDefinition("i");
        ColumnDefinition j = new ColumnDefinition("j");
        table.assignColumnDefinition(e);
        table.assignColumnDefinition(f);
        table.assignColumnDefinition(g);
        table.assignColumnDefinition(h);
        table.assignColumnDefinition(i);
        table.assignColumnDefinition(j);
        table.assignColumnDefinition(c);
        prototypes.add(table);

        // Get results
        dependencyCreator = new TestDependencyCreator(results);

        // Verify
        runAssertions(results, prototypes);
    }

    /**
     * Three tables connected by two keys
     */
    @Test
    public void getAllJoinableTablesGroupsThreeTablesConnectedByTwoTest() {
        // Prepare data
        List<TableDefinition> results = new ArrayList<>();
        TableDefinition table = new TableDefinition("t1", Arrays.asList("a", "b", "c"));
        results.add(table);
        table = new TableDefinition("t2", Arrays.asList("c", "d"));
        results.add(table);
        table = new TableDefinition("t3", Arrays.asList("e", "f", "g", "h", "i", "j", "d"));
        results.add(table);

        // Prepare prototypes
        List<TableDefinition> prototypes = new ArrayList<>();
        table = new TableDefinition("t1");
        ColumnDefinition a = new ColumnDefinition("a");
        ColumnDefinition b = new ColumnDefinition("b");
        ColumnDefinition c = new ColumnDefinition("c");
        table.assignColumnDefinition(a);
        table.assignColumnDefinition(b);
        table.assignColumnDefinition(c);
        prototypes.add(table);
        table = new TableDefinition("t2");
        ColumnDefinition d = new ColumnDefinition("d");
        table.assignColumnDefinition(c);
        table.assignColumnDefinition(d);
        prototypes.add(table);
        table = new TableDefinition("t3");
        ColumnDefinition e = new ColumnDefinition("e");
        ColumnDefinition f = new ColumnDefinition("f");
        ColumnDefinition g = new ColumnDefinition("g");
        ColumnDefinition h = new ColumnDefinition("h");
        ColumnDefinition i = new ColumnDefinition("i");
        ColumnDefinition j = new ColumnDefinition("j");
        table.assignColumnDefinition(e);
        table.assignColumnDefinition(f);
        table.assignColumnDefinition(g);
        table.assignColumnDefinition(h);
        table.assignColumnDefinition(i);
        table.assignColumnDefinition(j);
        table.assignColumnDefinition(d);
        prototypes.add(table);

        // Get results
        dependencyCreator = new TestDependencyCreator(results);

        // Verify
        runAssertions(results, prototypes);
    }

    /**
     * Three tables with the same columns
     */
    @Test
    public void getAllJoinableTablesGroupsThreeTablesIdenticalContentTest() {
        // Prepare data
        List<TableDefinition> results = new ArrayList<>();
        TableDefinition table = new TableDefinition("t1", Arrays.asList("a", "b", "c"));
        results.add(table);
        table = new TableDefinition("t2", Arrays.asList("a", "b", "c"));
        results.add(table);
        table = new TableDefinition("t3", Arrays.asList("a", "b", "c"));
        results.add(table);

        // Prepare prototypes
        List<TableDefinition> prototypes = new ArrayList<>();
        table = new TableDefinition("t1");
        ColumnDefinition a = new ColumnDefinition("a");
        ColumnDefinition b = new ColumnDefinition("b");
        ColumnDefinition c = new ColumnDefinition("c");
        table.assignColumnDefinition(a);
        table.assignColumnDefinition(b);
        table.assignColumnDefinition(c);
        prototypes.add(table);
        table = new TableDefinition("t2");
        table.assignColumnDefinition(a);
        table.assignColumnDefinition(b);
        table.assignColumnDefinition(c);
        prototypes.add(table);
        table = new TableDefinition("t3");
        table.assignColumnDefinition(a);
        table.assignColumnDefinition(b);
        table.assignColumnDefinition(c);
        prototypes.add(table);

        // Get results
        dependencyCreator = new TestDependencyCreator(results);

        // Verify
        runAssertions(results, prototypes);
    }

    /**
     * Six tables with grouping to two joinable tables
     */
    @Test
    public void getAllJoinableTablesGroupsSixTablesMultipleKeys() {
        // Prepare data
        List<TableDefinition> results = new ArrayList<>();
        TableDefinition table = new TableDefinition("t1", Arrays.asList("a", "b", "c"));
        results.add(table);
        table = new TableDefinition("t2", Arrays.asList("d", "e", "a"));
        results.add(table);
        table = new TableDefinition("t3", Arrays.asList("e", "f", "g"));
        results.add(table);
        table = new TableDefinition("t4", Arrays.asList("h", "b", "i"));
        results.add(table);
        table = new TableDefinition("t5", Arrays.asList("k", "j"));
        results.add(table);
        table = new TableDefinition("t6", Arrays.asList("l", "j"));
        results.add(table);

        // Prepare prototypes
        List<TableDefinition> prototypes = new ArrayList<>();
        table = new TableDefinition("t1");
        ColumnDefinition a = new ColumnDefinition("a");
        ColumnDefinition b = new ColumnDefinition("b");
        ColumnDefinition c = new ColumnDefinition("c");
        table.assignColumnDefinition(a);
        table.assignColumnDefinition(b);
        table.assignColumnDefinition(c);
        prototypes.add(table);
        table = new TableDefinition("t2");
        ColumnDefinition d = new ColumnDefinition("d");
        ColumnDefinition e = new ColumnDefinition("e");
        table.assignColumnDefinition(d);
        table.assignColumnDefinition(e);
        table.assignColumnDefinition(a);
        prototypes.add(table);
        table = new TableDefinition("t3");
        ColumnDefinition f = new ColumnDefinition("f");
        ColumnDefinition g = new ColumnDefinition("g");
        table.assignColumnDefinition(e);
        table.assignColumnDefinition(f);
        table.assignColumnDefinition(g);
        prototypes.add(table);
        table = new TableDefinition("t4");
        ColumnDefinition h = new ColumnDefinition("h");
        ColumnDefinition i = new ColumnDefinition("i");
        table.assignColumnDefinition(h);
        table.assignColumnDefinition(b);
        table.assignColumnDefinition(i);
        prototypes.add(table);
        table = new TableDefinition("t5");
        ColumnDefinition k = new ColumnDefinition("k");
        ColumnDefinition j = new ColumnDefinition("j");
        table.assignColumnDefinition(k);
        table.assignColumnDefinition(j);
        prototypes.add(table);
        table = new TableDefinition("t6");
        ColumnDefinition l = new ColumnDefinition("l");
        table.assignColumnDefinition(l);
        table.assignColumnDefinition(j);
        prototypes.add(table);

        // Get results
        dependencyCreator = new TestDependencyCreator(results);

        // Verify
        runAssertions(results, prototypes);
    }

    private <T> void runAssertions(List<T> prototypeTables, List<T> resultTables) {
        // Assertions
        // For each table check if the following hold
        boolean hasCorrectSize = resultTables.size() == prototypeTables.size();
        assertEquals(hasCorrectSize, true);
        assertNotNull(resultTables);
        assertEquals(resultTables, prototypeTables);
    }
}