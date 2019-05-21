package eu.profinit.manta.connector.erstudio.model;

import java.util.Collection;

/**
 * An ErStudioSolution interface provides an access to a Logical and set of Physical models and their underlying {@link DataObject}s.
 *
 * The interface's realization may be an internal Data Model that is loaded directly from a {@link ErStudioFile},
 * or may be an external one whose origin is in a different .DM1 file and is only some of its objects are referenced
 * from the {@link ErStudioFile} was loaded from.
 *
 * Every {@link DataModel} in {@link ErStudioSolution} must have a unique name.
 * For Entities and Tables of type {@link CompositeDataObject} belonging to some model, pair Owner &amp; the object's name must be unique.
 * For Attributes and Columns of type {@link SimpleDataObject} must have unique name across their parents of type {@link CompositeDataObject}
 *
 * @author ddrobny
 */
public interface ErStudioSolution {
    /**
     * Gets the name of the .DM1 file where the underlying {@link DataObject}s and {@link DataModel}s come from.
     * @return the name of the .DM1 file where the underlying objects come from.
     */
    String getFileName();

    /**
     * Gets every {@link CompositeDataObject} that has {@link AbstractionLayer#LOGICAL}, Entity in other words, from all {@link ErStudioSolution}'s
     * {@link LogicalDataModel}s.
     * @return every {@link CompositeDataObject} that has {@link AbstractionLayer#LOGICAL} from all {@link ErStudioSolution}'s
     * {@link LogicalDataModel}s, If none is present an empty {@link Collection} is be returned.
     */
    Collection<CompositeDataObject> getAllEntities();

    /**
     * Gets every {@link CompositeDataObject} that has {@link AbstractionLayer#LOGICAL}, Entity in other words, and {@link CompositeDataObject#getName()}
     * is equal to {@code entityName} from all {@link ErStudioSolution}'s {@link LogicalDataModel}s.
     * @param entityName The name that the result of Entity's function {@link CompositeDataObject#getName()} must be equal
     * with to be contained in the result.
     * @return Every {@link CompositeDataObject} that has {@link AbstractionLayer#LOGICAL} and {@link CompositeDataObject#getName()}
     * is equal to {@code entityName} from all {@link ErStudioSolution}'s {@link LogicalDataModel}s. If none is present an empty
     * {@link Collection} is be returned.
     */
    Collection<CompositeDataObject> getEntities(String entityName);

    /**
     * Gets every {@link CompositeDataObject} that has {@link AbstractionLayer#PHYSICAL}, Table in other words, from all {@link ErStudioSolution}'s
     * {@link PhysicalDataModel}s.
     * @return every {@link CompositeDataObject} that has {@link AbstractionLayer#PHYSICAL} from all {@link ErStudioSolution}'s
     * {@link PhysicalDataModel}s, If none is present an empty {@link Collection} is be returned.
     */
    Collection<CompositeDataObject> getAllTables();

    /**
     * Gets every {@link CompositeDataObject} that has {@link AbstractionLayer#PHYSICAL}, Table in other words, and {@link CompositeDataObject#getName()}
     * is equal to {@code tableName} from all {@link ErStudioSolution}'s {@link PhysicalDataModel}s.
     * @param tableName The name that the result of Table's function {@link CompositeDataObject#getName()} must be equal
     * with to be contained in the result.
     * @return Every {@link CompositeDataObject} that has {@link AbstractionLayer#PHYSICAL} and {@link CompositeDataObject#getName()}
     * is equal to {@code tableName} from all {@link ErStudioSolution}'s {@link PhysicalDataModel}s. If none is present an empty
     * {@link Collection} is be returned.
     */
    Collection<CompositeDataObject> getTables(String tableName);

    /**
     * Gets every {@link SimpleDataObject} that has {@link AbstractionLayer#LOGICAL}, Attribute in other words, from all {@link ErStudioSolution}'s
     * {@link LogicalDataModel}s.
     * @return every {@link SimpleDataObject} that has {@link AbstractionLayer#LOGICAL} from all {@link ErStudioSolution}'s.
     * {@link LogicalDataModel}s, If none is present an empty {@link Collection} is be returned.
     */
    Collection<SimpleDataObject> getAllAttributes();

    /**
     * Gets every {@link SimpleDataObject} that has {@link AbstractionLayer#LOGICAL}, Attribute in other words, and {@link SimpleDataObject#getName()}
     * is equal to {@code attributeName} from all {@link ErStudioSolution}'s {@link LogicalDataModel}s.
     * @param attributeName The name that the result of Entity's function {@link SimpleDataObject#getName()} must be equal
     * with to be contained in the result.
     * @return Every {@link SimpleDataObject} that has {@link AbstractionLayer#LOGICAL} and {@link SimpleDataObject#getName()}
     * is equal to {@code attributeName} from all {@link ErStudioSolution}'s {@link LogicalDataModel}s. If none is present an empty.
     * {@link Collection} is be returned.
     */
    Collection<SimpleDataObject> getAttributes(String attributeName);

    /**
     * Gets every {@link SimpleDataObject} that has {@link AbstractionLayer#PHYSICAL}, Column in other words, from all {@link ErStudioSolution}'s
     * {@link PhysicalDataModel}s.
     * @return every {@link SimpleDataObject} that has {@link AbstractionLayer#PHYSICAL} from all {@link ErStudioSolution}'s.
     * {@link PhysicalDataModel}s, If none is present an empty {@link Collection} is be returned.
     */
    Collection<SimpleDataObject> getAllColumns();

    /**
     * Gets every {@link SimpleDataObject} that has {@link AbstractionLayer#PHYSICAL}, Column in other words, and {@link SimpleDataObject#getName()}
     * is equal to {@code columnName} from all {@link ErStudioSolution}'s {@link PhysicalDataModel}s.
     * @param columnName The name that the result of Column's function {@link SimpleDataObject#getName()} must be equal
     * with to be contained in the result.
     * @return Every {@link SimpleDataObject} that has {@link AbstractionLayer#PHYSICAL} and {@link SimpleDataObject#getName()}.
     * is equal to {@code columnName} from all {@link ErStudioSolution}'s {@link PhysicalDataModel}s. If none is present an empty
     * {@link Collection} is be returned.
     */
    Collection<SimpleDataObject> getColumns(String columnName);

    /**
     * Gets every underlying {@link PhysicalDataModel} of the {@link ErStudioSolution}.
     * @return Every underlying {@link PhysicalDataModel} of the {@link ErStudioSolution}. If none is present
     * an empty {@link Collection} is returned.
     */
    Collection<PhysicalDataModel> getAllPhysicalModels();

    /**
     * Gets a {@link PhysicalDataModel} with {@link PhysicalDataModel#getName()} equal to {@code modelName}.
     * @param modelName Name that the resulting model's function result {@link PhysicalDataModel#getName()} must be equal to.
     * @return {@link PhysicalDataModel} with {@link PhysicalDataModel#getName()} equal to {@code modelName}, if there's none,
     * null is returned.
     */
    PhysicalDataModel getPhysicalModel(String modelName);

    /**
     * Gets the {@link LogicalDataModel} the object contains. {@link ErStudioSolution} may have 0 or 1 {@link LogicalDataModel}s.
     * @return {@link LogicalDataModel} the object contains. If there's none, null is returned.
     */
    LogicalDataModel getLogicalModel();

}
