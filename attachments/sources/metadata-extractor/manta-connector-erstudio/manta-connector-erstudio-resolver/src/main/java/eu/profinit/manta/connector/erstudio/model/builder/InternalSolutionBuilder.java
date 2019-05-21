package eu.profinit.manta.connector.erstudio.model.builder;

import eu.profinit.manta.connector.erstudio.model.builder.mapping.AbstractMapper;
import eu.profinit.manta.connector.erstudio.model.builder.mapping.InternalMapper;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.plain.CompositeObjectRowModel;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.plain.ModelsRowModel;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.plain.SimpleObjectRowModel;
import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;
import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;
import eu.profinit.manta.connector.erstudio.model.impl.AbstractDataModel;
import eu.profinit.manta.connector.erstudio.model.impl.CompositeDataObjectImpl;
import eu.profinit.manta.connector.erstudio.model.impl.ErStudioSolutionImpl;
import eu.profinit.manta.connector.erstudio.model.impl.SimpleDataObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Reconstruction of data models from a solution.
 *
 * @author ddrobny
 */
public class InternalSolutionBuilder extends AbstractSolutionBuilder {
    private final StringIdResolver stringIdResolver;
    private final String fileName;
    private final CsvTable modelTable;
    private final CsvTable compositeObjectsTable;
    private final CsvTable simpleObjectsTable;
    private CsvTable mappingTable;

    public InternalSolutionBuilder(String fileName, StringIdResolver stringIdResolver, CsvTable modelTable,
            CsvTable compositeObjectsTable, CsvTable simpleObjectsTable) {
        this.fileName = fileName;
        this.stringIdResolver = stringIdResolver;
        this.modelTable = modelTable;
        this.compositeObjectsTable = compositeObjectsTable;
        this.simpleObjectsTable = simpleObjectsTable;
    }

    public Map<Integer, CompositeDataObjectImpl> getIdToComposite() {
        return idToComposite;
    }

    public Map<Integer, SimpleDataObjectImpl> getIdToSimple() {
        return idToSimple;
    }

    @Override
    protected void createSolution() {
        result = new ErStudioSolutionImpl(fileName);
    }

    @Override
    protected void reconstructObjects() {
        super.reconstructObjects();
        // Create Mappings between internal objects
        AbstractMapper mappingCreator = new InternalMapper(mappingTable, idToComposite, idToSimple);
        mappingCreator.loadMappings();
    }

    @Override
    protected void loadSimpleObjects() {
        for (CsvRow current : simpleObjectsTable) {
            SimpleObjectRowModel info = new SimpleObjectRowModel(stringIdResolver, current);
            CompositeDataObjectImpl parentObject = idToComposite.get(info.getEntityId());
            idToSimple.put(info.getAttributeId(), createSimpleObject(info, parentObject));
        }
    }

    @Override
    protected void loadCompositeObjects() {
        for (CsvRow current : compositeObjectsTable) {
            CompositeObjectRowModel info = new CompositeObjectRowModel(stringIdResolver, current);
            AbstractDataModel parentModel = idToModel.get(info.getModelId());
            idToComposite.put(info.getEntityId(), createCompositeObject(info, parentModel));
        }
    }

    @Override
    protected void loadModels() {
        for (CsvRow current : modelTable) {
            ModelsRowModel rowModel = new ModelsRowModel(stringIdResolver, current);
            AbstractDataModel model = createModel(rowModel);
            trySetLogicalModel(model);
            idToModel.put(rowModel.getModelId(), model);
        }

    }

    public void setMappingTable(CsvTable mappingTable) {
        this.mappingTable = mappingTable;
    }
}
