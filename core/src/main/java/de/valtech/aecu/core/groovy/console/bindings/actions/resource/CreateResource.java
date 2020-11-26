/*
 * Copyright 2020 Valtech GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import de.valtech.aecu.api.groovy.console.bindings.GStringConverter;
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;

/**
 * Creates a new node
 *
 * @author Roland Gruber
 */
public class CreateResource implements Action {

    private String name;
    private Map<String, Object> properties;
    private String relativePath;
    private ResourceResolver resourceResolver;

    public CreateResource(@Nonnull String name, @Nonnull Map<String, Object> properties, String relativePath,
            @Nonnull ResourceResolver resourceResolver) {
        this.name = name;
        this.properties = GStringConverter.convert(properties);
        this.relativePath = relativePath;
        this.resourceResolver = resourceResolver;
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException {
        Resource destinationResource = resource;
        if (StringUtils.isNotBlank(relativePath)) {
            destinationResource = resourceResolver.getResource(resource, relativePath);
        }
        if (destinationResource != null) {
            Resource newResource = resourceResolver.create(destinationResource, name, properties);
            return "Created " + newResource.getPath();
        }
        return "WARNING: could not read destination resource at " + relativePath;
    }

}
