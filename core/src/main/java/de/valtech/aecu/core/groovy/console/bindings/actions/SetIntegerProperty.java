package de.valtech.aecu.core.groovy.console.bindings.actions;

import javax.annotation.Nonnull;

public class SetIntegerProperty extends SetStringProperty {

    public SetIntegerProperty(@Nonnull String name, Integer value) {
        this.name = name;
        this.value = value;
    }
}
