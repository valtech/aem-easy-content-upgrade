package de.valtech.aecu.api.groovy.console.bindings;

import java.util.HashMap;
import java.util.Map;

import groovy.lang.GString;

/**
 * Converts GString to String.
 * 
 * @author Roland Gruber
 */
public class GStringConverter {

    private GStringConverter() {
        // no instantiation
    }

    /**
     * Converts the input in case it is a GString.
     * 
     * @param input input
     * @return converted value
     */
    public static Object convert(Object input) {
        if (input instanceof GString) {
            return ((GString) input).toString();
        }
        return input;
    }

    /**
     * Converts the input in case it is a GString.
     * 
     * @param input input
     * @return converted value
     */
    public static Map<String, Object> convert(Map<String, Object> input) {
        if (input == null) {
            return input;
        }
        Map<String, Object> output = new HashMap<>(input);
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            if (entry.getValue() instanceof GString) {
                output.put(entry.getKey(), convert(entry.getValue()));
            }
        }
        return output;
    }

}
