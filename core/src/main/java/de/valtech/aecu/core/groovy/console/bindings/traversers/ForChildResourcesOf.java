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
package de.valtech.aecu.core.groovy.console.bindings.traversers;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.filters.FilterBy;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * @author Roxana Muresan
 */
public class ForChildResourcesOf implements TraversData {

    private String path;

    public ForChildResourcesOf(@Nonnull String path) {
        this.path = path;
    }


    @Override
    public void traverse(@Nonnull ResourceResolver resourceResolver, FilterBy filter, @Nonnull Action action, @Nonnull StringBuffer stringBuffer, boolean dryRun) throws PersistenceException {
        Resource parentResource = resourceResolver.getResource(path);
        if (parentResource != null) {
            Iterator<Resource> resourceIterator = resourceResolver.listChildren(parentResource);
            while (resourceIterator.hasNext()) {
                Resource resource = resourceIterator.next();
                if (filter == null || filter.filter(resource)) {
                    stringBuffer.append(action.doAction(resource) + "\n");
                }
            }
            if (!dryRun) {
                resourceResolver.commit();
            }
        }
    }
}
