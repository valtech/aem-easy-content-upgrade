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

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import de.valtech.aecu.api.groovy.console.bindings.GStringConverter;

/**
 * Filters resources by a given property. The filter only matches if the attribute exists and has
 * the exact given value.
 * 
 * @author Roland Gruber
 */
public class FilterByProperty implements FilterBy {

    private String name;
    private Object value;

    /**
     * Constructor
     * 
     * @param name  attribute name
     * @param value attribute value
     */
    public FilterByProperty(@Nonnull String name, Object value) {
        this.name = name;
        this.value = GStringConverter.convert(value);
    }

    @Override
    public boolean filter(@Nonnull Resource resource, StringBuilder output) {
        ValueMap properties = resource.getValueMap();
        Object attrValue = properties.get(name);
        return (value == null) && (attrValue == null) || ((value != null) && value.equals(attrValue));
    }
}

