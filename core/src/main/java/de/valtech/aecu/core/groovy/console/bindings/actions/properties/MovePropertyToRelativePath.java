/*
 *  Copyright 2018 Valtech GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
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
        String key = (newName != null) ? newName : name;
        targetProperties.put(key, propValue);
        sourceProperties.remove(name);

        return "Moving property " + name + " from " + resource.getPath() + " to resource " + targetResource.getPath() + " as " + key;
    }

}
