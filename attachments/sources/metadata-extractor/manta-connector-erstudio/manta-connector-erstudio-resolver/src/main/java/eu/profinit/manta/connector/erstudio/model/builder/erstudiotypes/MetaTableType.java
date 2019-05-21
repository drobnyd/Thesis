package eu.profinit.manta.connector.erstudio.model.builder.erstudiotypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Types of metatables in ER/Studio.
 *
 * @author ddrobny
 */
public enum MetaTableType {EXTERNAL_MAPPED_OBJECT(71), SIMPLE_OBJECT(3), COMPOSITE_OBJECT(1), UNKNOWN_TYPE(-1);
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaTableType.class);
    private static final Map<Integer, MetaTableType> map = new HashMap<>();

    static {
        for (MetaTableType type : MetaTableType.values()) {
            map.put(type.number, type);
        }
    }

    private int number;

    MetaTableType(final int num) {
        number = num;
    }

    public static MetaTableType valueOf(int i) {
        if (!map.containsKey(i)) {
            LOGGER.warn("Object of unknown type " + i + " was found");
            return UNKNOWN_TYPE;
        }
        return map.get(i);
    }}
