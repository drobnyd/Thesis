package eu.profinit.manta.connector.erstudio.model.impl;

import eu.profinit.manta.connector.erstudio.model.AbstractionLayer;
import eu.profinit.manta.connector.erstudio.model.CompositeDataObject;
import eu.profinit.manta.connector.erstudio.model.DataModel;
import eu.profinit.manta.connector.erstudio.model.ErStudioSolution;
import eu.profinit.manta.connector.erstudio.model.LogicalDataModel;
import eu.profinit.manta.connector.erstudio.model.PhysicalDataModel;
import eu.profinit.manta.connector.erstudio.model.SimpleDataObject;
import eu.profinit.manta.connector.erstudio.utils.MultiMapExtensions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 *
 */
public class ErStudioSolutionImpl implements ErStudioSolution {

    private String fileName;
    /* DataModel names must be unique */
    private Map<String, DataModel> models;
    private LogicalDataModel logicalModel;

    /* For Entities and Tables in some model pair Owner & the object's name must be unique */
    private Map<String, List<CompositeDataObject>> entities;
    private Map<String, List<CompositeDataObject>> tables;

    /* For Attributes and Columns must have unique name across their parents */
    private Map<String, List<SimpleDataObject>> attributes;
    private Map<String, List<SimpleDataObject>> columns;

    public ErStudioSolutionImpl(String fileName) {
        this.fileName = fileName;
    }

    public void setModels(Collection<DataModel> dataModels) {
        // Make a map out of the input collection, fileName being key and value the whole entry. Filter out nulls.
        this.models = dataModels.stream().filter(x -> x != null).collect(Collectors.toMap(x -> x.getName(), x -> x));
    }

    public void setEntities(Collection<CompositeDataObject> entities) {
        this.entities = MultiMapExtensions.collectionToMultiMap(entities);
    }

    public void setTables(List<CompositeDataObject> tables) {
        this.tables = MultiMapExtensions.collectionToMultiMap(tables);
    }

    public void setAttributes(List<SimpleDataObject> attributes) {
        this.attributes = MultiMapExtensions.collectionToMultiMap(attributes);
    }

    public void setColumns(List<SimpleDataObject> columns) {
        this.columns = MultiMapExtensions.collectionToMultiMap(columns);
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public List<CompositeDataObject> getAllEntities() {
        return Collections.unmodifiableList(MultiMapExtensions.flattenMultiMap(entities));
    }

    @Override
    public List<CompositeDataObject> getEntities(String entityName) {
        return Collections.unmodifiableList(entities.get(entityName));
    }

    @Override
    public List<CompositeDataObject> getAllTables() {
        return Collections.unmodifiableList(MultiMapExtensions.flattenMultiMap(tables));
    }

    @Override
    public List<CompositeDataObject> getTables(String tableName) {
        return Collections.unmodifiableList(tables.get(tableName));
    }

    @Override
    public List<SimpleDataObject> getAllAttributes() {
        return Collections.unmodifiableList(MultiMapExtensions.flattenMultiMap(attributes));
    }

    @Override
    public List<SimpleDataObject> getAttributes(String attributeName) {
        return Collections.unmodifiableList(attributes.get(attributeName));
    }

    @Override
    public List<SimpleDataObject> getAllColumns() {
        return Collections.unmodifiableList(MultiMapExtensions.flattenMultiMap(columns));
    }

    @Override
    public List<SimpleDataObject> getColumns(String columnName) {
        return Collections.unmodifiableList(columns.get(columnName));
    }

    @Override
    public List<PhysicalDataModel> getAllPhysicalModels() {
        return Collections.unmodifiableList(
                models.values().stream().filter(x -> x.getLayer() == AbstractionLayer.PHYSICAL)
                        .map(PhysicalDataModel.class::cast).collect(Collectors.toList()));
    }

    @Override
    public PhysicalDataModel getPhysicalModel(String modelName) {
        // The only model that is present in the models map but is not a physical one
        if (logicalModel != null && modelName.equals(logicalModel.getName())) {
            return null;
        }
        return (PhysicalDataModel) models.get(modelName);
    }

    @Override
    public LogicalDataModel getLogicalModel() {
        return logicalModel;
    }

    public void setLogicalModel(LogicalDataModel logicalModel) {
        this.logicalModel = logicalModel;
    }
}
