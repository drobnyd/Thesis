package eu.profinit.manta.connector.erstudio.model;

import eu.profinit.manta.dataflow.generator.modeling.common.nodes.NodeMetadata;

/**
 * An interface for accessing any data object that is modeled in some {@link DataModel}.
 * It may be either {@link CompositeDataObject} or {@link SimpleDataObject} that also extend this interface.
 */
public interface DataObject extends Layered, NodeMetadata {
    /**
     * Gets the name of the object.
     * @return Name of the object. Must not be null.
     */
    String getName();

    /**
     * Gets a note assigned to the object. May contain some description even in html format.
     * @return Note assigned to the object. Null if not present.
     */
    String getNote();

    /**
     * Gets a definition assigned to the object. Should contain some explanatory description.
     * @return Definition assigned to the object. Null if not present.
     */
    String getDefinition();
}
