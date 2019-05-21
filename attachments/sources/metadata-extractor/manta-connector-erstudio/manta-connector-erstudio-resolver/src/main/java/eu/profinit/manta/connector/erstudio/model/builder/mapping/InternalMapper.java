package eu.profinit.manta.connector.erstudio.model.builder.mapping;

import eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes.InternalMappingTableDefinition;
import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;
import eu.profinit.manta.connector.erstudio.model.impl.CompositeDataObjectImpl;
import eu.profinit.manta.connector.erstudio.model.impl.SimpleDataObjectImpl;

import java.util.Map;

/**
 * Class mapping internal objects.
 *
 * @author ddrobny
 */
public class InternalMapper extends AbstractMapper {

    public InternalMapper(CsvTable mappingsTable, Map<Integer, CompositeDataObjectImpl> internalIdToComposite,
            Map<Integer, SimpleDataObjectImpl> internalIdToSimple) {

        super(InternalMappingTableDefinition.getInstance(), mappingsTable, internalIdToComposite, internalIdToSimple);
    }
}
