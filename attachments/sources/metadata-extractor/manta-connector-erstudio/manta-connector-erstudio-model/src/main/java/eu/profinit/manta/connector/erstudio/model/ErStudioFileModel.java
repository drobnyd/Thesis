package eu.profinit.manta.connector.erstudio.model;

import java.util.Collection;

/**
 * Interface allowing read-only access to a completely loaded structure from one .DM1 file.
 * May have references to objects of different {@link ErStudioSolution}s saved in different .DM1 files.
 *
 * The internal's {@link LogicalDataModel} must not be {@code null}.
 *
 * @author ddrobny
 */
public interface ErStudioFileModel extends ErStudioFile {
    /**
     * Gets the name of the file.
     * @return the name of the file.
     */
    String getFileName();

    /**
     * Gets the internal solution saved in the file.
     * @return the internal solution.
     */
    ErStudioSolution getInternalErStudioSolution();

    /**
     * Gets every {@link ErStudioSolution} that is referenced from the {@link ErStudioFile}.
     * @return Every {@link ErStudioSolution} that is referenced from the {@link ErStudioFile}.
     * If there isn't any reference, empty collection is returned.
     */
    Collection<ErStudioSolution> getAllExternalErStudioSolutions();

    /**
     * Gets an {@link ErStudioSolution} that is  saved in a file named {@code fileName} and being referenced
     * by the {@link ErStudioFileModel}.
     * @param fileName Unique name of the file that is being referenced as {@link ErStudioSolution}.
     * @return {@link ErStudioSolution} that is  saved in a file named {@code fileName} and being referenced
     * by the {@link ErStudioFileModel}.
     */
    ErStudioSolution getExternalErStudioSolution(String fileName);
}
