package cz.manta.drobny.DM1ReverseEngineer;

import cz.manta.drobny.DM1ReverseEngineer.table.TableDefinition;

import java.util.Collection;

/**
 * Class for creating dependencies between column with some behaviour changed for testing purposes.
 *
 * @author ddrobny
 */
public class TestDependencyCreator extends DependencyCreator {

    public TestDependencyCreator(Collection<TableDefinition> tables) {
        super(tables);
    }

    /**
     * Not complete behaviour of the method. It is not filtering anything since in the test we don't take the blacklist into consideration
     * @param columnName to be examined if is blacklisted or not
     * @return always false
     */
    @Override
    protected boolean isOnBlackList(String columnName) {
        return false;
    }
}
