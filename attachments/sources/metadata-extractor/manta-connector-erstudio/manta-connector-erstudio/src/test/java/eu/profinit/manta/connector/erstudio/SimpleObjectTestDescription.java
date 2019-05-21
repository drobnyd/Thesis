package eu.profinit.manta.connector.erstudio;

/**
 * Class capturing tested aspects of a simple object.
 *
 * @author ddrobny
 */
public class SimpleObjectTestDescription extends ModeledObjectTestDescription {
    private CompositeObjectTestDescription superObject;

    public SimpleObjectTestDescription(CompositeObjectTestDescription superObject) {
        this.superObject = superObject;
    }

    public CompositeObjectTestDescription getSuperObject() {
        return superObject;
    }
}
