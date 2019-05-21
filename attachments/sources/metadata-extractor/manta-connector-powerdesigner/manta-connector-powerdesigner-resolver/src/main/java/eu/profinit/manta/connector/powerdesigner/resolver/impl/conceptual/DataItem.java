package eu.profinit.manta.connector.powerdesigner.resolver.impl.conceptual;

import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractSimpleModelObject;

/** Represents an attribute like object in conceptual data model
 * that may be reused by multiple entities, while attribute belongs to a single entity.
 *
 * @author ddrobny
 */
public class DataItem extends AbstractSimpleModelObject {

    /**
     * Creates a shallow copy of the data item instance, transforming it to a corresponding
     * attribute which has its own ObjectID.
     * @param globalId the ID of the resulting attribute.
     * @return the attribute with the same attributes as the instance of data item.
     */
    public ConceptualAttributeImpl createAttribute(String globalId) {
        ConceptualAttributeImpl result = new ConceptualAttributeImpl();

        result.setCode(this.getCode());
        result.setComment(this.getComment());
        result.setKeywords(this.getKeywords());
        result.setHistory(this.getHistory());
        result.setName(this.getName());
        // ObjectID is not copied, the new attribute must get a unique one
        result.setObjectId(globalId);

        return result;
    }

}
