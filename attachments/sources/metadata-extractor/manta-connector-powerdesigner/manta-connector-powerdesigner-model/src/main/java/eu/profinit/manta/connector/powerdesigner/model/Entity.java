package eu.profinit.manta.connector.powerdesigner.model;

/**
 * An entity represents an object about which you want to store information.
 * For example, in a model of a major corporation, the entities created may include Employee and Division.
 * When you generate a PDM from a CDM or LDM, entities are generated as tables.
 * It is an EER data model entity, that can be inherited from another corresponding entity.
 *
 * @author ddrobny
 */
public interface Entity<E extends eu.profinit.manta.connector.powerdesigner.model.Entity<E, S>, S extends SimpleModelObject>
        extends CompositeModelObject<S> {

    /**
     * Gets the entity that the object inherits from.
     * @return the entity that the object inherits from.
     */
    E getParent();
}
