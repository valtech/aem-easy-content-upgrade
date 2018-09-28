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
package de.valtech.aecu.core.groovy.console.bindings.traversers;

import de.valtech.aecu.api.groovy.console.bindings.filters.FilterBy;
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * @author Roxana Muresan
 * @author Roland Gruber
 */
public interface TraversData {

    /**
     * Traverses the resources and performs the filters and actions.
     * 
     * @param context      binding context
     * @param filter       filter
     * @param actions      list of actions
     * @param stringBuffer output buffer
     * @param dryRun       dry run
     * @throws PersistenceException error traversing nodes
     */
    void traverse(@Nonnull BindingContext context, FilterBy filter, @Nonnull List<Action> actions,
            @Nonnull StringBuffer stringBuffer, boolean dryRun) throws PersistenceException;

    /**
     * Checks if the resource is still valid. E.g. this returns false if it was already deleted.
     * 
     * @param resource resource
     * @return valid
     */
    default boolean isResourceValid(Resource resource) {
        try {
            ValueMap values = resource.getValueMap();
            return values.get(JcrConstants.JCR_PRIMARYTYPE, String.class) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
