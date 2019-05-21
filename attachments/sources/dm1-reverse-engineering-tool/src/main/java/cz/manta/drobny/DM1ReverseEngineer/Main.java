package cz.manta.drobny.DM1ReverseEngineer;

import cz.manta.drobny.DM1ReverseEngineer.parser.Parser;
import cz.manta.drobny.DM1ReverseEngineer.table.TableDefinition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * Main class.
 *
 * @author ddrobny
 */
public class Main {
    public static void main(String[] args) {
        Parser parser;
        File model = new File(args[0]);

        if (!model.exists()){
            System.err.println("The specified input file does not exist.");
            return;
        }

        parser = new Parser(model);
        //  Table metaclass = parser.getTable("MetaClass_Member");
        //  Table submodelObject = parser.getTable("Submodel_SchemaObj_Link");
        //  List<AbstractMap.SimpleEntry<Column,Column>> related = RelationFinderByTableContents.getSubsetColumns(metaclass,submodelObject);
        //  RelationFinderByTableContents relationFinder = new RelationFinderByTableContents(parser.getTables());
        //  List<Integer> ids = relationFinder.getAscendingNumberColumn("Entity","EntityId");
        //  Set<Table> candidates = relationFinder.getTableByNumOfEntries(130,130);

        TablesToSQLConverter converter;
        List<TableDefinition> tableDefinitions = parser.getTableDefinitions();

        try {
            converter = new TablesToSQLConverter(tableDefinitions, new FileOutputStream("MetaModel.sql"));
        } catch (FileNotFoundException e) {
            System.err.println("Output could not be written.");
            return;
        }

        converter.writeMySQLSource(model);
    }

    /**
     * Writes loaded data structures to file
     * @param data set of data objects to write out
     * @param whereToWrite name of output file
     * @throws FileNotFoundException
     */
    private static <T> void writeIterableToFile(Iterable<T> data, String whereToWrite) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(whereToWrite);
        for (T entry : data) {
            writer.println(entry.toString());
        }
        writer.close();
    }

}
