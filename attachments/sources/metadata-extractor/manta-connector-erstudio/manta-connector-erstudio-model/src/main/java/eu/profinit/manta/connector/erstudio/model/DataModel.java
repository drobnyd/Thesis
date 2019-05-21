package eu.profinit.manta.connector.erstudio.model;

import eu.profinit.manta.dataflow.generator.modeling.common.nodes.NodeMetadata;

import java.util.Collection;

/**
 * Interface for read only access to a {@link DataModel}, that is a set of {@link CompositeDataObject}s.
 * Pairs of {@link CompositeDataObject}s name and their {@link Owner} in a {@link DataModel} must be unique.
 * Every {@link DataModel} in {@link ErStudioSolution} must have a unique name.
 * More precise models with predefined level of abstraction - {@link LogicalDataModel} &amp; {@link PhysicalDataModel}
 * extends this contract.
 */
public interface DataModel extends Layered, NodeMetadata {

    String getName();

    Collection<? extends Owner> getOwners();

    Owner getOwner(String ownerName);

    Owner getDefaultOwner();

}
