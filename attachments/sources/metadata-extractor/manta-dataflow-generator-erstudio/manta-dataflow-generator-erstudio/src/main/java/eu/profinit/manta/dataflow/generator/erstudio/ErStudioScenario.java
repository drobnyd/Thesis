package eu.profinit.manta.dataflow.generator.erstudio;

import eu.profinit.manta.connector.erstudio.model.ErStudioFileModel;
import eu.profinit.manta.dataflow.generator.modelutils.GraphScenario;

/**
 * Scenario of crating data flow from an ER/Studio file.
 *
 * @author ddrobny
 */
public class ErStudioScenario extends GraphScenario<ErStudioFileModel> {
    private static final String MANTA_FLOW_ERSTUDIO_CODE = "mf_ers";
    private static final String MANTA_FLOW_ERSTUDIO_DESCRIPTION = "Manta Flow ErStudio";

    @Override
    protected boolean isInputCorrect(ErStudioFileModel input) {
        return input != null;
    }

    @Override
    protected void checkInput(ErStudioFileModel input) {
        if (!checkTechnology(MANTA_FLOW_ERSTUDIO_CODE)) {
            throw new IllegalStateException(
                    "Insufficient license for the " + MANTA_FLOW_ERSTUDIO_DESCRIPTION + " technology.");
        }
    }
}
