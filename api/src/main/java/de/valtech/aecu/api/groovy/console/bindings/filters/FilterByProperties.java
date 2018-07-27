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
package de.valtech.aecu.api.groovy.console.bindings.filters;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Filters resources by properties. You can define multiple properties that all need an exact match.
 * This works for multi-value properties too with exact match.
 * 
 * @author Roxana Muresan
 */
public class FilterByProperties implements FilterBy {

    private Map<String, Object> conditionProperties = new HashMap<>();

    /**
     * Constructor
     * 
     * @param conditionProperties list of properties to match (property name, property value)
     */
    public FilterByProperties(@Nonnull Map<String, Object> conditionProperties) {
        this.conditionProperties.putAll(conditionProperties);
    }

    @Override
    public boolean filter(@Nonnull Resource resource) {
        ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
        for (String key : conditionProperties.keySet()) {
            Object conditionValue = conditionProperties.get(key);
            Object propertiesValue = properties.get(key);

            if (conditionValue != null && propertiesValue != null && conditionValue instanceof Object[] && propertiesValue instanceof Object[]) {
                if (!Arrays.equals((Object[]) conditionValue, (Object[]) propertiesValue)) {
                    return false;
                }
            }

            if ((conditionValue == null && propertiesValue != null)
                    || (conditionValue != null && !conditionValue.equals(propertiesValue))) {
                return false;
            }
        }
        return true;
    }
}

