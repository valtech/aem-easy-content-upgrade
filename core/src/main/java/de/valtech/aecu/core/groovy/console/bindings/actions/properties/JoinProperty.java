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
package de.valtech.aecu.core.groovy.console.bindings.actions.properties;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

/**
 * @author Yves De Bruyne
 */
public class JoinProperty implements Action {

    protected String name;
    protected String separator;
    protected Object emptyValue;

    public JoinProperty(@Nonnull String name) {
        this.name = name;
        this.separator = ",";
        this.emptyValue = null;
    }

    public JoinProperty(@Nonnull String name, Object emptyValue) {
        this.name = name;
        this.separator = ",";
        this.emptyValue = emptyValue;
    }

    public JoinProperty(@Nonnull String name, @Nonnull String separator, Object emptyValue) {
        this.name = name;
        this.separator = separator;
        this.emptyValue = emptyValue;
    }

    @Override
    public String doAction(@Nonnull Resource resource) {
        ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
        if (properties != null) {

            if (!properties.containsKey(name)) {
                return StringUtils.EMPTY;
            }

            Object value = properties.get(name);

            if (!value.getClass().isArray()) {
                return StringUtils.EMPTY;
            }

            Object[] values = (Object[]) value;

            if (values.length >= 1) {
                properties.remove(name);
                properties.put(name, StringUtils.join(values, separator));
                return "Flattening " + value.getClass().getSimpleName() + " property " + name + " for resource " + resource.getPath();
            }

            if (this.emptyValue == null) {
                properties.remove(name);
                return "Flattening " + value.getClass().getSimpleName() + " removing property " + name + " for resource " + resource.getPath();
            }

            //replace empty array with fallback
            properties.remove(name);
            properties.put(name, this.emptyValue);
            return "Flattening " + value.getClass().getSimpleName() + " property " + name + "=" + this.emptyValue + " for resource " + resource.getPath();
        }
        return "WARNING: could not get ModifiableValueMap for resource " + resource.getPath();
    }
}
