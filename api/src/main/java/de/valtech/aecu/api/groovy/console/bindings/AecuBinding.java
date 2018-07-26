package de.valtech.aecu.api.groovy.console.bindings;

/**
 * Groovy Console Bindings for AEM Simple Content Update. This provides the "aecu" binding variable.
 * 
 * @author Roxana Muresan
 */
public interface AecuBinding {

    /**
     * Returns a content upgrade builder. This is the starting point for the migrations.
     * 
     * @return builder
     */
    ContentUpgrade contentUpgradeBuilder();

}
