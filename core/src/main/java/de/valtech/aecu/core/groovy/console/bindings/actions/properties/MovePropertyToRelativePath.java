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
package de.valtech.aecu.core.groovy.console.bindings.actions.properties;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;

/**
 * @author Roxana Muresan
 */
public class MovePropertyToRelativePath implements Action {

    private String relativeResourcePath;
    private String name;
    private String newName;
    private ResourceResolver resourceResolver;

    public MovePropertyToRelativePath(@Nonnull String name, String newName, @Nonnull ResourceResolver resourceResolver, @Nonnull String relativeResourcePath) {
        this.name = name;
        this.newName = newName;
        this.resourceResolver = resourceResolver;
        this.relativeResourcePath = relativeResourcePath;
    }

    @Override
    public String doAction(@Nonnull Resource resource) {
        ModifiableValueMap sourceProperties = resource.adaptTo(ModifiableValueMap.class);

        Resource targetResource = resourceResolver.getResource(resource, relativeResourcePath);
        ModifiableValueMap targetProperties = targetResource.adaptTo(ModifiableValueMap.class);

        Object propValue = sourceProperties.get(name);
        targetProperties.put((newName != null) ? newName : name, propValue);
        sourceProperties.remove(name);

        return "Moving property " + name + " from " + resource.getPath() + " to resource " + targetResource.getPath();
    }

}
