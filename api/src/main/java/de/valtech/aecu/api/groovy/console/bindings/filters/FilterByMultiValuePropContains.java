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

import java.util.Arrays;

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

/**
 * Filters resources by multi-value properties. It checks if the given values are contained in the
 * resource's multi-value field, no exact match.
 *
 * @author Roxana Muresan
 */
public class FilterByMultiValuePropContains implements FilterBy {

    private String name;
    private Object[] values;

    /**
     * Constructor
     *
     * @param name   name of the multi-value property
     * @param values values to be searched for
     */
    public FilterByMultiValuePropContains(@Nonnull String name, @Nonnull Object[] values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public boolean filter(@Nonnull Resource resource, StringBuilder output) {
        ValueMap properties = resource.adaptTo(ValueMap.class);
        if (properties != null) {
            Object value = properties.get(name);
            if (value != null && value.getClass().isArray()) {
                Object[] multiValues = (Object[]) value;
                return Arrays.asList(multiValues).containsAll(Arrays.asList(values));
            }
        }
        return false;
    }
}
