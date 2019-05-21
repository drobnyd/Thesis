package eu.profinit.manta.connector.powerdesigner;

import eu.profinit.manta.connector.powerdesigner.model.Layer;

import java.io.File;
import java.io.FileFilter;

/**
 * File filter accepting only data models of PowerDesigner.
 *
 * @author ddrobny
 */
public class PowerDesignerFileFilter implements FileFilter {
    /**
     * Tests whether or not the specified abstract pathname should be
     * included in a pathname list.
     * @param pathname  The abstract pathname to be tested.
     * @return {@code true} if and only if pathname has an extension of a supported PowerDesigner data model.
     */
    @Override
    public boolean accept(File pathname) {
        return Layer.getLayer(pathname.getPath()) != null;
    }
}
