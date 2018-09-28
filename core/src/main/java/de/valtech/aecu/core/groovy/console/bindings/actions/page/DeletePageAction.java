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
package de.valtech.aecu.core.groovy.console.bindings.actions.page;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Deletes the page of a given resource.
 * 
 * @author Roland Gruber
 */
public class DeletePageAction implements Action {

    private BindingContext context;

    /**
     * Constructor
     * 
     * @param context binding context
     */
    public DeletePageAction(BindingContext context) {
        this.context = context;
    }

    @Override
    public String doAction(Resource resource) throws PersistenceException {
        Page page = context.getPageManager().getContainingPage(resource);
        if (page == null) {
            return "Unable to find a page for resource " + resource.getPath();
        }
        String successMessage = "Deleted page " + page.getPath();
        if (context.isDryRun()) {
            return successMessage;
        }
        try {
            context.getPageManager().delete(page, false);
        } catch (WCMException e) {
            throw new PersistenceException("Unable to delete " + page.getPath());
        }
        return successMessage;
    }

}
