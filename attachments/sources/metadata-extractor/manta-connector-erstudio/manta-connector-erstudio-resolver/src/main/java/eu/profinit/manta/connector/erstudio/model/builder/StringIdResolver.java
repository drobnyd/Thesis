package eu.profinit.manta.connector.erstudio.model.builder;

import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;
import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Translator between values of strings and references to them.
 *
 * @author ddrobny
 */
public class StringIdResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringIdResolver.class);
    /* String table columns */
    private static final String STRING_ID_COLUMN_NAME = "String_Id";
    private static final String STRING_CONTENT_COLUMN_NAME = "Data";

    final Map<Integer, String> idToString = new HashMap<>();

    public StringIdResolver() {
        idToString.put(0, null); // Id with 0 value has a special meaning - no string is assigned
    }

    /**
     * Loads all the strings saved in the given table, so they can be resolved after.
     * @param stringTable the table to be loaded.
     */
    public void loadStrings(CsvTable stringTable) {
        for (int i = 0; i < stringTable.getDataRowCount(); i++) {
            // From each row get String_Id as key and data - the String's contents itself
            CsvRow current = stringTable.getDataRow(i);
            String data = current.getValue(STRING_CONTENT_COLUMN_NAME);
            if (data.isEmpty()) {
                continue; // In case of SmallString table there are entries with empty data field and the content itself is saved with same id in LargeString table
            }
            idToString.put(current.getIntValue(STRING_ID_COLUMN_NAME), data);
        }
    }

    /**
     * Gets the textual value based on the string ID.
     * @param stringId the ID to search by.
     * @return the textual value of the given string ID.
     */
    public String getStringByStringId(int stringId) {
        if (stringId < 0) {
            LOGGER.error("String ID is never negative in the model");
        }
        return idToString.get(stringId);
    }

}
