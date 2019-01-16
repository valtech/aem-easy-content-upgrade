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
package de.valtech.aecu.core.groovy.console.bindings.actions.multivalue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;

/**
 * @author Roxana Muresan
 */
public class RemoveMultiValues implements Action {

    private String name;
    private String[] values;

    public RemoveMultiValues(@Nonnull String name, @Nonnull String[] values) {
        this.name = name;
        this.values = Arrays.stream(values).filter(Objects::nonNull).collect(Collectors.toList()).toArray(new String[] {});
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException {
        ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
        if (properties != null) {
            String[] currentValues = properties.get(name, String[].class);
            List<String> valuesList = new ArrayList<>();
            if (currentValues != null && currentValues.length > 0) {
                Collections.addAll(valuesList, currentValues);
            }
            valuesList.removeAll(Arrays.asList(values));
            properties.put(name, valuesList.toArray(new String[] {}));

            return "Removing values " + Arrays.toString(values) + " from multi-value property " + name + ": "
                    + Arrays.toString(currentValues) + " results in " + valuesList + " for resource " + resource.getPath();
        }
        return "WARNING: could not get ModifiableValueMap for resource " + resource.getPath();
    }

}
