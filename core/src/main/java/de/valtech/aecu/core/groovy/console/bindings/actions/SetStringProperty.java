/*
 *  Copyright 2018 Valtech GmbH
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package de.valtech.aecu.core.groovy.console.bindings.actions;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

/**
 * @author Roxana Muresan
 */
public class SetStringProperty implements Action {

    protected String name;
    protected Object value;

    protected SetStringProperty() {}

    public SetStringProperty(@Nonnull String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String doAction(@Nonnull Resource resource) {
        ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
        properties.put(name, value);
        return "Setting property " + name + "=" + value + " for resource " + resource.getPath();
    }
}
