package de.valtech.aecu.core.groovy.console.bindings.actions;

import javax.annotation.Nonnull;

public class SetBooleanProperty extends SetStringProperty {

    public SetBooleanProperty(@Nonnull String name, Boolean value) {
        this.name = name;
        this.value = value;
    }
}
