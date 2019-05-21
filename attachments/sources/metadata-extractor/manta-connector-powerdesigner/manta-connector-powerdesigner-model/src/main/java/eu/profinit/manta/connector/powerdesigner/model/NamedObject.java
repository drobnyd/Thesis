package eu.profinit.manta.connector.powerdesigner.model;

import eu.profinit.manta.dataflow.generator.modeling.common.nodes.NodeMetadata;

import java.util.Set;

/**
 * Interesting properties of an object from PowerDesigner's data model.
 *
 * @author ddrobny
 */
public interface NamedObject extends NodeMetadata {

    /**
     * Gets the name that should clearly convey the object's purpose to non-technical users.
     * @return the name of the object.
     */
    String getName();

    /**
     * Gets the code, which is used for generating code or scripts, may be abbreviated, and
     * should not normally include spaces.
     * By default the code is auto-generated from the name by
     * applying the naming conventions specified in the model options.
     * @return the code of the object.
     */
    String getCode();

    /**
     * Gets the comment to provide more detailed information about the object.
     * @return the comment of the object.
     */
    String getComment();

    /**
     * Gets the annotation as plain text defining the object further.
     * @return the annotation of the object.
     */
    String getAnnotation();

    /**
     * Gets the definition as plain text defining the object further.
     * @return the definition of the object.
     */
    String getDefinition();

    /**
     * Gets keywords that provide a way of loosely grouping objects through tagging.
     * @return the keywords of the object.
     */
    Set<String> getKeywords();

    /**
     * Gets the value of object's ObjectID from PowerDesigner, which is a unique identifier.
     * @return the value of object's ObjectID from PowerDesigner, which is a unique identifier.
     */
    String getObjectId();
}
