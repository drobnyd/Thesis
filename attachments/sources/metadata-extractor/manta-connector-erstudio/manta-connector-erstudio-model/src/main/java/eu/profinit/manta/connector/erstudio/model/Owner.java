package eu.profinit.manta.connector.erstudio.model;

import eu.profinit.manta.dataflow.generator.modeling.common.nodes.NodeMetadata;

import java.util.Collection;

/**
 * Container of composite objects. May be also known as Schema concept on Physical Layer.
 *
 * @author ddrobny
 */
public interface Owner extends Layered, NodeMetadata {

    String getName();

    Collection<? extends CompositeDataObject> getAllCompositeObjects();

    CompositeDataObject getCompositeObject(String objectName);
}
