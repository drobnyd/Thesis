package eu.profinit.manta.connector.powerdesigner.resolver.build.desc;

/**
 * Names of selected XML elements. They are the same for CDM and LDM.
 *
 * @author ddrobny
 */
public class HighLevelElementsDescription extends AbstractElementsDescription {
    /** The only instance of the class. */
    private static HighLevelElementsDescription ourInstance = new HighLevelElementsDescription();

    private HighLevelElementsDescription() {
        super("o:EntityAttribute", "o:Entity", "//o:Entity[@Id]");
    }

    /**
     * Gets the only instance of the class.
     * @return the only instance of the class.
     */
    public static HighLevelElementsDescription getInstance() {
        return ourInstance;
    }
}
