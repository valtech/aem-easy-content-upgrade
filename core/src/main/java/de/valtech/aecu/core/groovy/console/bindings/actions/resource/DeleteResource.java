/*
 * Copyright 2018 Valtech GmbH
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;

/**
 * This action is used to delete the child nodes of a given node.
 * 
 * @author sravan
 * @author Roxana Muresan
 */
public class DeleteResource implements Action {

    private ResourceResolver resourceResolver;
    private String[] children;

    public DeleteResource(@Nonnull ResourceResolver resourceResolver, String... children) {
        this.resourceResolver = resourceResolver;
        this.children = children;
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException {
        List<String> deletedResources = new ArrayList<>();
        List<String> nonExistingResources = new ArrayList<>();
        for (String child : children) {
            Resource childResource = resourceResolver.getResource(resource, child);
            if (null != childResource) {
                resourceResolver.delete(childResource);
                deletedResources.add(child);
            } else {
                nonExistingResources.add(child);
            }
        }
        if (resourceResolver.hasChanges()) {
            resourceResolver.commit();
        }
        return "Deleted child resource(s) - " + Arrays.toString(deletedResources.toArray())
                + " and non-existing child resource(s) - " + Arrays.toString(nonExistingResources.toArray()) + " under parent "
                + resource.getPath();
    }

}
