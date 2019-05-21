package eu.profinit.manta.connector.erstudio.utils;

import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;

/**
 * Methods for dealing with strings consistently.
 *
 * @author ddrobny.
 */
public class StringTranslator {

    /**
     * Translates empty strings to null object, to be consistent with string creating policy when parsing {@link CsvRow}
     * @param string to be checked.
     * @return nonempty string, same as {@code string} otherwise {@code null}.
     */
    public static String processXmlString(String string) {
        if (string.isEmpty()) {
            return null;
        }
        return string;
    }
}
