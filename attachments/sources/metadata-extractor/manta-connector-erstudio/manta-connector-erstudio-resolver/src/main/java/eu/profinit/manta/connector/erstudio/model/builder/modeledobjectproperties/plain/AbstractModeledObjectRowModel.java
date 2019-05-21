package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.plain;

import eu.profinit.manta.connector.erstudio.model.builder.StringIdResolver;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.NameableByLayer;
import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;

/**
 * Common description of attributes of modeled objects.
 *
 * @author ddrobny
 */
public abstract class AbstractModeledObjectRowModel extends AbstractRowModel implements NameableByLayer {
    /** Unique ID of Composite Object */
    private static final String ENTITY_ID_COLUMN_NAME = "Entity_ID";
    private static final String STRING_ID_DEFINITION_COLUMN_NAME = "DefinitionId";
    private static final String STRING_ID_NOTE_COLUMN_NAME = "NoteId";

    private int stringIdDefinition;
    private int stringIdNote;
    private int entityId;

    public AbstractModeledObjectRowModel(StringIdResolver stringIdResolver, CsvRow row) {
        super(stringIdResolver, row);
        setFromCsvRow(row);
    }

    /**
     * Gets entity ID attribute of the object.
     * @return the entity ID.
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * Gets the definition of the object.
     * @return the definition of the object.
     */
    public String getDefinition() {
        return stringIdResolver.getStringByStringId(stringIdDefinition);
    }

    /**
     * Gets note of the object.
     * @return the note of the object.
     */
    public String getNote() {
        return stringIdResolver.getStringByStringId(stringIdNote);
    }

    private void setFromCsvRow(CsvRow row) {
        stringIdDefinition = row.getIntValue(STRING_ID_DEFINITION_COLUMN_NAME);
        stringIdNote = row.getIntValue(STRING_ID_NOTE_COLUMN_NAME);
        entityId = row.getIntValue(ENTITY_ID_COLUMN_NAME);
    }
}
