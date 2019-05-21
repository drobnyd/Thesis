package eu.profinit.manta.connector.erstudio.model.builder.mapping;

import eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes.ExternalMappingTableDefinition;
import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;
import eu.profinit.manta.connector.erstudio.model.impl.AbstractDataObject;
import eu.profinit.manta.connector.erstudio.model.impl.CompositeDataObjectImpl;
import eu.profinit.manta.connector.erstudio.model.impl.SimpleDataObjectImpl;

import java.util.Map;

/**
 * Class mapping external objects.
 *
 * @author ddrobny
 */
public class ExternalMapper extends AbstractMapper {

    public ExternalMapper(CsvTable mappingsTable, Map<Integer, CompositeDataObjectImpl> internalIdToComposite,
            Map<Integer, SimpleDataObjectImpl> internalIdToSimple,
            Map<Integer, AbstractDataObject> idToExternalModeledObject) {

        super(ExternalMappingTableDefinition.getInstance(), mappingsTable, internalIdToComposite, internalIdToSimple);
        this.idToExternalModeledObject = idToExternalModeledObject;
    }
}
