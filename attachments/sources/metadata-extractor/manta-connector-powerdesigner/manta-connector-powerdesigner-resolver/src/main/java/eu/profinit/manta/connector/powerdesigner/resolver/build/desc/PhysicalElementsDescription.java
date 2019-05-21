package eu.profinit.manta.connector.powerdesigner.resolver.build.desc;

/**
 * Names of selected XML elements of physical data model.
 *
 * @author ddrobny
 */
public class PhysicalElementsDescription extends AbstractElementsDescription {
    private static PhysicalElementsDescription ourInstance = new PhysicalElementsDescription();

    private PhysicalElementsDescription() {
        super("o:Column", "o:Table", "//o:Table[@Id]");
    }

    /**
     * Gets the only instance of the class.
     * @return the only instance of the class.
     */
    public static PhysicalElementsDescription getInstance() {
        return ourInstance;
    }
}
