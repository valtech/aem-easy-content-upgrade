/*
 * Copyright 2019 Valtech GmbH
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

import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

/**
 * Filters resources by a given property using regular expression matching. In case the property
 * name is null all properties will be checked if there is any match. This only works for single
 * value properties.
 * 
 * @author Roland Gruber
 */
public class FilterByPropertyRegex implements FilterBy {

    private String name;
    private Pattern pattern;

    /**
     * Constructor
     * 
     * @param name  property name or null for all properties
     * @param regex regular expression
     */
    public FilterByPropertyRegex(String name, @Nonnull String regex) {
        this.name = name;
        pattern = Pattern.compile(regex);
    }

    @Override
    public boolean filter(@Nonnull Resource resource, StringBuffer stringBuffer) {
        ValueMap properties = resource.getValueMap();
        if (StringUtils.isNotBlank(name)) {
            String value = properties.get(name, String.class);
            return (value != null) && pattern.matcher(value).matches();
        }
        for (String key : properties.keySet()) {
            String value = properties.get(key, String.class);
            if ((value != null) && pattern.matcher(value).matches()) {
                return true;
            }
        }
        return false;
    }

}

