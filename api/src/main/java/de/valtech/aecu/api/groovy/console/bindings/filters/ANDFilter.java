/*
 * Copyright 2018 - 2019 Valtech GmbH
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

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.Resource;

/**
 * Combines multiple filters with AND.
 * 
 * @author Roxana Muresan
 */
public class ANDFilter implements FilterBy {

    private List<FilterBy> filters;

    /**
     * Constructor
     * 
     * @param filters list of filters that should be chained with AND
     */
    public ANDFilter(@Nonnull List<FilterBy> filters) {
        this.filters = filters;
    }

    @Override
    public boolean filter(@Nonnull Resource resource, StringBuilder output) {
        for (FilterBy filter : filters) {
            if (!filter.filter(resource, output)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds a new filter to the AND condition.
     * 
     * @param filter filter
     */
    public void addFilter(@Nonnull FilterBy filter) {
        filters.add(filter);
    }

}
