package eu.profinit.manta.connector.erstudio.model.builder;

import eu.profinit.manta.connector.erstudio.model.ErStudioFileModel;
import eu.profinit.manta.connector.erstudio.model.ErStudioSolution;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.xml.XmlComposite;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.xml.XmlErStudioSolution;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.xml.XmlModel;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.xml.XmlSimple;
import eu.profinit.manta.connector.erstudio.model.csv.CsvRow;
import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;
import eu.profinit.manta.connector.erstudio.model.impl.AbstractDataModel;
import eu.profinit.manta.connector.erstudio.model.impl.AbstractDataObject;
import eu.profinit.manta.connector.erstudio.model.impl.CompositeDataObjectImpl;
import eu.profinit.manta.connector.erstudio.model.impl.ErStudioSolutionImpl;
import eu.profinit.manta.connector.erstudio.model.impl.SimpleDataObjectImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class responsible for reconstruction of objects from an external solution.
 *
 * @author ddrobny
 */
public class ExternalSolutionBuilder extends AbstractSolutionBuilder {
    /* External Mapped Object Types */
    private static final int EXTERNAL_MODEL_TYPE = 5;
    private static final int EXTERNAL_COMPOSITE_OBJECT_TYPE = 7;
    private static final int EXTERNAL_SIMPLE_OBJECT_TYPE = 11;

    private static final String EXTERNAL_MAPPED_OBJECT_TYPE_COLUMN_NAME = "EMObj_Type";
    private static final String EXTERNAL_MAPPED_OBJECT_ID = "External_Mapped_Object_ID";
    private static final String EXTERNAL_MAPPED_OBJECT_PARENT_ID = "Parent_EMObj_ID";
    private static final String EXTERNAL_MAPPED_OBJECT_XML_DEFINITION = "User_Friendly_Display_Info";

    private static XmlObjectParser xmlParser = XmlObjectParser.getInstance();
    private int externalFileId;
    private CsvTable externalObjectsTable;

    public ExternalSolutionBuilder(int externalFileId, CsvTable externalObjectsDefinitionTable) {
        this.externalFileId = externalFileId;
        this.externalObjectsTable = externalObjectsDefinitionTable;
    }

    /**
     *
     * @return Map where the keys are unique among other AbstractModeled objects coming from any External {@link ErStudioSolution}
     * referenced from the processed {@link ErStudioFileModel}
     */
    public Map<Integer, AbstractDataObject> getIdToModeledObject() {
        Map<Integer, AbstractDataObject> result = new HashMap<>(idToSimple);
        result.putAll(idToComposite);
        return result;
    }

    @Override
    protected void createSolution() {
        assert externalObjectsTable.filter(EXTERNAL_MAPPED_OBJECT_ID, Integer.toString(externalFileId)).size() == 1;
        CsvRow fileRow = externalObjectsTable.filter(EXTERNAL_MAPPED_OBJECT_ID, Integer.toString(externalFileId))
                .get(0);
        XmlErStudioSolution xmlErStudioModel = xmlParser
                .parseXmlErStudioSolution(fileRow.getValue(EXTERNAL_MAPPED_OBJECT_XML_DEFINITION));
        result = new ErStudioSolutionImpl(xmlErStudioModel.getFileName());
    }

    @Override
    protected void loadModels() {
        List<CsvRow> modelRows = externalObjectsTable
                .filter(EXTERNAL_MAPPED_OBJECT_TYPE_COLUMN_NAME, Integer.toString(EXTERNAL_MODEL_TYPE))//Filter models
                .stream()
                .filter(x -> x.getIntValue(EXTERNAL_MAPPED_OBJECT_PARENT_ID) == externalFileId)//Only from current file
                .collect(Collectors.toList());
        for (CsvRow row : modelRows) {
            int tableId = row.getIntValue(EXTERNAL_MAPPED_OBJECT_ID);
            XmlModel xmlModel = xmlParser.parseXmlModel(row.getValue(EXTERNAL_MAPPED_OBJECT_XML_DEFINITION));
            AbstractDataModel model = createModel(xmlModel);
            trySetLogicalModel(model);
            idToModel.put(tableId, model);
        }
    }

    @Override
    protected void loadCompositeObjects() {
        List<CsvRow> compositeObjectRows = externalObjectsTable
                .filter(EXTERNAL_MAPPED_OBJECT_TYPE_COLUMN_NAME, Integer.toString(EXTERNAL_COMPOSITE_OBJECT_TYPE));
        for (CsvRow row : compositeObjectRows) {
            int tableId = row.getIntValue(EXTERNAL_MAPPED_OBJECT_ID);
            int parentId = row.getIntValue(EXTERNAL_MAPPED_OBJECT_PARENT_ID);
            if (idToModel.containsKey(parentId)) {//Avoid processing CompositeObjects from other files but the analyzed
                AbstractDataModel parentModel = idToModel.get(parentId);
                XmlComposite xmlComposite = xmlParser
                        .parseXmlComposite(row.getValue(EXTERNAL_MAPPED_OBJECT_XML_DEFINITION));
                CompositeDataObjectImpl compositeObject = createCompositeObject(xmlComposite, parentModel);
                idToComposite.put(tableId, compositeObject);
            }
        }
    }

    @Override
    protected void loadSimpleObjects() {
        List<CsvRow> simpleObjectRows = externalObjectsTable
                .filter(EXTERNAL_MAPPED_OBJECT_TYPE_COLUMN_NAME, Integer.toString(EXTERNAL_SIMPLE_OBJECT_TYPE));
        for (CsvRow row : simpleObjectRows) {
            int tableId = row.getIntValue(EXTERNAL_MAPPED_OBJECT_ID);
            int parentId = row.getIntValue(EXTERNAL_MAPPED_OBJECT_PARENT_ID);
            if (idToComposite.containsKey(parentId)) {//Avoid processing SimpleObjects from other files but the analyzed
                CompositeDataObjectImpl parentObject = idToComposite.get(parentId);
                XmlSimple xmlSimple = xmlParser.parseXmlSimple(row.getValue(EXTERNAL_MAPPED_OBJECT_XML_DEFINITION));
                SimpleDataObjectImpl simpleObject = createSimpleObject(xmlSimple, parentObject);
                idToSimple.put(tableId, simpleObject);
            }
        }
    }
}
