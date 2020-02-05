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
package de.valtech.aecu.core.groovy.console.bindings.actions.util;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

import com.day.cq.wcm.api.NameConstants;

/**
 * Util functions for pages.
 * 
 * @author Roland Gruber
 */
public class PageUtil {

    private static final String CONTENT = "/content";

    /**
     * Returns if the given resource is a page. This will return false for any subnodes of a page
     * incl. jcr:content.
     * 
     * @param resource resource
     * @return is page
     */
    public boolean isPageResource(Resource resource) {
        if (resource == null) {
            return false;
        }
        String path = resource.getPath();
        if (!path.startsWith(CONTENT) || path.contains(JcrConstants.JCR_CONTENT)) {
            return false;
        }
        String primaryType = resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, String.class);
        return NameConstants.NT_PAGE.equals(primaryType);
    }

}
