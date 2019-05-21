package eu.profinit.manta.connector.erstudio;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;

/**
 * File filter accepting supported files storing ER/Studio's data models.
 *
 * @author ddrobny
 */
public class Dm1FileFilter implements FileFilter {
    /**
     * Tests whether or not the specified abstract pathname should be
     * included in a pathname list.
     *
     * @param  pathname  The abstract pathname to be tested
     * @return  <code>true</code> if and only if <code>pathname</code>
     *          should be included
     */
    @Override
    public boolean accept(File pathname) {
        return FilenameUtils.getExtension(pathname.getName()).equalsIgnoreCase("dm1");
    }
}
