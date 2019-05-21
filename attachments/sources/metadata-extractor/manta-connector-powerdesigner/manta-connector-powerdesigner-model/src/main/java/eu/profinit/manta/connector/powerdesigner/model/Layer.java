package eu.profinit.manta.connector.powerdesigner.model;

import org.apache.commons.io.FilenameUtils;

/**
 * Possible levels of abstraction of data models and underlying objects.
 *
 * @author ddrobny
 */
public enum Layer {
    CONCEPTUAL("cdm"), LOGICAL("ldm"), PHYSICAL("pdm");

    /** Extension of a file with the abstraction layer. */
    private String extension;

    /**
     * Registers a file extension corresponding to a layer, without the dot.
     * @param extension the extension to register, without the dot.
     */
    Layer(String extension) {
        this.extension = extension;
    }

    /**
     * Gets the layer of a data model file with given path.
     * @param filename path of a data model. May be absolute or relative, must contain the extension.
     * @return type of layer corresponding to the given path to data model.
     * If the given data model is not supported, {@code null}.
     */
    public static Layer getLayer(String filename) {
        Layer result = null;
        String extension = FilenameUtils.getExtension(filename);
        for (Layer type : Layer.values()) {
            if (type.getExtension().equals(extension)) {
                result = type;
            }
        }
        return result;
    }

    /**
     * Get the extension that data model files with the abstraction layer have, not including the dot.
     * @return the extension that data model files with the abstraction layer have, not including the dot.
     */
    public String getExtension() {
        return extension;
    }
}
