package eu.profinit.manta.connector.erstudio.model.builder;

import eu.profinit.manta.connector.erstudio.model.AbstractionLayer;
import eu.profinit.manta.connector.erstudio.model.DataModel;
import eu.profinit.manta.connector.erstudio.model.ErStudioSolution;
import eu.profinit.manta.connector.erstudio.model.LogicalDataModel;
import eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes.PlatformType;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.CompositeCreationProperties;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.ModelCreationProperties;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.NameableByLayer;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.SimpleCreationProperties;
import eu.profinit.manta.connector.erstudio.model.impl.AbstractDataModel;
import eu.profinit.manta.connector.erstudio.model.impl.AbstractObject;
import eu.profinit.manta.connector.erstudio.model.impl.CompositeDataObjectImpl;
import eu.profinit.manta.connector.erstudio.model.impl.ErStudioSolutionImpl;
import eu.profinit.manta.connector.erstudio.model.impl.LogicalDataModelImpl;
import eu.profinit.manta.connector.erstudio.model.impl.OwnerImpl;
import eu.profinit.manta.connector.erstudio.model.impl.PhysicalDataModelImpl;
import eu.profinit.manta.connector.erstudio.model.impl.PlatformImpl;
import eu.profinit.manta.connector.erstudio.model.impl.SimpleDataObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base implementation building of solution that consists of multiple physical and a logical model.
 *
 * @author ddrobny
 */
abstract class AbstractSolutionBuilder {
    /** ID identifying Logical Data Model */
    public static final int LOGICAL_DATA_MODEL_TYPE = 1;
    /* 2 - Plain Physical mode, 5 - Physical model reverse engineered from an existing database */
    /** ID identifying meaning Physical Data Model */
    private static final Collection<Integer> PHYSICAL_DATA_MODEL_TYPES = Collections
            .unmodifiableCollection(Arrays.asList(2, 5));
    /** Both Physical and Logical models are stored here since they are coming from the same csv table and their IDs are disjoint*/
    protected Map<Integer, AbstractDataModel> idToModel = new HashMap<>();
    /** Single and compulsory logical model of the diagram */
    protected LogicalDataModel logicalModel;
    /** Both Entities and Tables are stored here since they are coming from the same csv table and their IDs are disjoint */
    protected Map<Integer, CompositeDataObjectImpl> idToComposite = new HashMap<>();
    /** Both Attributes and Columns are stored here since they are coming from the same csv table and their IDs are disjoint */
    protected Map<Integer, SimpleDataObjectImpl> idToSimple = new HashMap<>();
    protected ErStudioSolutionImpl result;
    private Logger LOGGER = LoggerFactory.getLogger(AbstractSolutionBuilder.class);

    /**
     * Builds the solution.
     */
    public void buildSolution() {
        // Reconstruct the objects in solution
        reconstructObjects();

        // Set the resulting data structures
        setModels();
        setLogicalModel();
        setTables();
        setEntities();
        setAttributes();
        setColumns();
    }

    /**
     * Gets the solution that was built.
     * @return the solution that was built.
     */
    public ErStudioSolution getSolution() {
        return result;
    }

    protected void reconstructObjects() {
        createSolution();
        // Create Models
        loadModels();
        // Create Entities and Tables
        loadCompositeObjects();
        // Create Attributes and Columns
        loadSimpleObjects();
    }

    abstract protected void createSolution();

    abstract protected void loadSimpleObjects();

    abstract protected void loadCompositeObjects();

    abstract protected void loadModels();

    protected SimpleDataObjectImpl createSimpleObject(SimpleCreationProperties properties,
            CompositeDataObjectImpl parentObject) {
        String name = hasCorrectParent(parentObject) ? getNameByLayer(parentObject.getLayer(), properties) : null;
        if (name == null) { // Name is never null if the object is correct
            return null;
        }
        SimpleDataObjectImpl result = new SimpleDataObjectImpl(name, parentObject.getLayer());
        result.setDefinition(properties.getDefinition());
        result.setNote(properties.getNote());
        return parentObject.addSubObject(result);
    }

    protected CompositeDataObjectImpl createCompositeObject(CompositeCreationProperties properties,
            AbstractDataModel parentModel) {
        String name = hasCorrectParent(parentModel) ? getNameByLayer(parentModel.getLayer(), properties) : null;
        if (name == null) { // Name is never null if the object is correct
            return null;
        }
        CompositeDataObjectImpl result = new CompositeDataObjectImpl(name, parentModel.getLayer());
        result.setDefinition(properties.getDefinition());
        result.setNote(properties.getNote());
        OwnerImpl owner = parentModel.getOwner(properties.getOwnerName());
        return owner.addCompositeObject(result);
    }

    protected AbstractDataModel createModel(ModelCreationProperties properties) {
        AbstractDataModel result;
        if (isLogicalModel(properties.getModelType())) {
            result = new LogicalDataModelImpl(properties.getName());
        } else if (isPhysicalModel(properties.getModelType())) {
            PlatformType type = PlatformType.valueOf(properties.getPlatformId());
            result = new PhysicalDataModelImpl(properties.getName(),
                    new PlatformImpl(type.toString(), type.getConnectionType()));
        } else {
            LOGGER.debug("DataModel with ModelType num " + properties.getModelType()
                    + " is not supported, won't be created. Same for its underlying objects.");
            return null;
        }
        return result;
    }

    protected boolean hasCorrectParent(AbstractObject parentObject) {
        if (parentObject == null) {
            // TODO treating differently irrelevant and not interesting objects
            // keeping ids that are seen but not processed vs failures
            // also distinction between simple and composite object should be made
            LOGGER.trace("Object won't be created, it is either irrelevant or its parent object cannot be obtained");
            return false;
        }
        return true;
    }

    /**
     * Gets name of the object based on the object's layer.
     * @param layer layer to decide by.
     * @param nameable object whose name we're getting.
     * @return name of the object based on the object's layer.
     * If the object's layer which name we're getting is unknown return {@code null}.
     */
    protected String getNameByLayer(AbstractionLayer layer, NameableByLayer nameable) {
        switch (layer) {
        case LOGICAL:
            return nameable.getLogicalName();
        case PHYSICAL:
            return nameable.getPhysicalName();
        default:
            // Name is never null if the object is correct
            LOGGER.error("The object's parent has an unknown abstraction layer");
            return null;
        }
    }

    private boolean isPhysicalModel(int modelType) {
        return PHYSICAL_DATA_MODEL_TYPES.contains(modelType);
    }

    private boolean isLogicalModel(int modelType) {
        return modelType == LOGICAL_DATA_MODEL_TYPE;
    }

    protected void trySetLogicalModel(DataModel model) {
        if (model != null && model.getLayer() == AbstractionLayer.LOGICAL && model instanceof LogicalDataModel) {
            logicalModel = (LogicalDataModel) model;
        }
    }

    protected void setLogicalModel() {
        result.setLogicalModel(logicalModel);
    }

    protected void setModels() {
        result.setModels(idToModel.values().stream().filter(x -> x != null).collect(Collectors.toList()));
    }

    protected void setAttributes() {
        result.setAttributes(
                idToSimple.values().stream().filter(x -> x != null && x.getLayer() == AbstractionLayer.LOGICAL)
                        .collect(Collectors.toList()));
    }

    protected void setColumns() {
        result.setColumns(
                idToSimple.values().stream().filter(x -> x != null && x.getLayer() == AbstractionLayer.PHYSICAL)
                        .collect(Collectors.toList()));
    }

    protected void setEntities() {
        result.setEntities(
                idToComposite.values().stream().filter(x -> x != null && x.getLayer() == AbstractionLayer.LOGICAL)
                        .collect(Collectors.toList()));
    }

    protected void setTables() {
        result.setTables(
                idToComposite.values().stream().filter(x -> x != null && x.getLayer() == AbstractionLayer.PHYSICAL)
                        .collect(Collectors.toList()));
    }

}
