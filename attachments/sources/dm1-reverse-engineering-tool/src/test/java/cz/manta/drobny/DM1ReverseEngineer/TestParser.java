package cz.manta.drobny.DM1ReverseEngineer;

import cz.manta.drobny.DM1ReverseEngineer.parser.Parser;

import java.util.Scanner;

/**
 * Class for parsing mocked input of .dm1 file format.
 *
 * @author ddrobny
 */
public class TestParser extends Parser {

    public TestParser(Scanner modelDefinition) {
        super(null); // Not creating file while mocking
        this.fileScanner = modelDefinition;
    }

    /**
     * Overridden because the mocked scanner cannot be reset since it's not provided by file
     */
    @Override
    protected void setScanner() {
        if (fileScanner.hasNextLine()) {
            return; // The first reading is all right
        }
        throw new UnsupportedOperationException("Cannot reset scanner in the test class");
    }
}
