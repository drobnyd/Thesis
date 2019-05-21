package eu.profinit.manta.dataflow.generator.powerdesigner;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.dataflow.generator.modelutils.GraphScenario;

import java.util.List;

/**
 * Graph scenario for a logical set of data models from PowerDesigner.
 */
public class PowerDesignerScenario extends GraphScenario<List<DataModel>> {
    /** Code of the PowerDesigner scenario.*/
    private static final String MANTA_FLOW_POWERDESIGNER_CODE = "mf_pd";
    /** Description of the PowerDesigner scenario. */
    private static final String MANTA_FLOW_POWERDESIGNER_DESCRIPTION = "Manta Flow PowerDesigner";

    @Override
    protected boolean isInputCorrect(List<DataModel> input) {
        return input != null;
    }

    @Override
    protected void checkInput(List<DataModel> input) {
        if (!checkTechnology(MANTA_FLOW_POWERDESIGNER_CODE)) {
            throw new IllegalStateException(
                    "Insufficient license for the " + MANTA_FLOW_POWERDESIGNER_DESCRIPTION + " technology.");
        }
    }
}
