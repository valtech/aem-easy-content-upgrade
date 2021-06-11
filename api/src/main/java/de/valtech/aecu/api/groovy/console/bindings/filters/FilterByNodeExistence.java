/*
 * Copyright 2021 Valtech GmbH
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
package de.valtech.aecu.api.groovy.console.bindings.filters;


import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;


/**
 * Filters resources by absolute or relative node path. Only resources that (not) exist in the
 * repository are accepted.
 *
 * @author Vugar Aghayev
 */
public class FilterByNodeExistence implements FilterBy {

    private String path;
    private boolean nodeExists;

    /**
     * Constructor
     *
     * @param path       absolute or relative resource path
     * @param nodeExists node exists option
     */
    public FilterByNodeExistence(@Nonnull String path, boolean nodeExists) {
        this.path = path;
        this.nodeExists = nodeExists;
    }

    @Override
    public boolean filter(@Nonnull Resource resource, StringBuilder output) {
        if (StringUtils.isBlank(path)) {
            return true;
        }

        if (isAbsolutePath(path)) {
            return (nodeExists && null != resource.getResourceResolver().getResource(path))
                    || (!nodeExists && null == resource.getResourceResolver().getResource(path));
        } else {
            return (nodeExists && null != resource.getChild(path)) || (!nodeExists && null == resource.getChild(path));
        }
    }

    /**
     * checks whether given path is absolute or not.
     *
     * @param path jcr path
     * @return true if the given path is the absolute path. Otherwise false.
     */
    private boolean isAbsolutePath(String path) {
        return path.startsWith("/");
    }

}
