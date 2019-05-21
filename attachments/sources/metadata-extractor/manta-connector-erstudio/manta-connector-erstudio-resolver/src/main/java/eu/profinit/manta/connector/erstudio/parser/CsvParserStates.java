package eu.profinit.manta.connector.erstudio.parser;

/**
 * Enumeration representing possible states of finite state machine that is accepting correct CSV rows
 *
 * @author ddrobny
 */
public enum CsvParserStates {/**
 * Getting there from the initial state after the last value that has to be accepted.
 * Table is completed.
 */
FINAL,
    /**
     * Initial state of each value entry, not in quotation, also can get there from end of quotation- that is decided in the escaped state
     */
    INITIAL,
    /**
     * After introducing the first quote from the initial state
     */
    QUOTATION,
    /**
     * After receiving quote from quoted state it's either escaped quote(when the next character is quote as well)
     * and it returns to the quotation state, or quotation ends and the next character is anything but quote char,
     * so the transition goes to the initial state
     */
    ESCAPED,
    /**
     * After correct accepting of one value that is not the last one in the row
     */
    VALUE_ACCEPTED,
    /**
     *  Indicating malformed CSV file format
     */
    DEAD_END}
