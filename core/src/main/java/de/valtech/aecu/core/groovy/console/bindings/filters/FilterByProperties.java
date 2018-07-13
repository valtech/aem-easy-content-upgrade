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
package de.valtech.aecu.core.groovy.console.bindings.filters;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;

/**
 * @author Roxana Muresan
 */
public class FilterByProperties implements FilterBy {

    private Map<String, String> conditionProperties = new HashMap<>();

    public FilterByProperties(@Nonnull Map<String, String> conditionProperties) {
        this.conditionProperties.putAll(conditionProperties);
    }

    @Override
    public boolean filter(@Nonnull Resource resource) {
        ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
        for (String key : conditionProperties.keySet()) {
            String conditionValue = conditionProperties.get(key);
            String propertiesValue = properties.get(key, String.class);

            if ((conditionValue == null && propertiesValue != null) || (conditionValue != null && !conditionValue.equals(propertiesValue))) {
                return false;
            }
        }
        return true;
    }
}

