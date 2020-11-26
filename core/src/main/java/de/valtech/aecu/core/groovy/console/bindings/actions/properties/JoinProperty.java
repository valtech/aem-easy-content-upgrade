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
package de.valtech.aecu.core.groovy.console.bindings.actions.properties;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import de.valtech.aecu.api.groovy.console.bindings.GStringConverter;
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;

/**
 * Joins a multi-value property to single values.
 * 
 * @author Yves De Bruyne
 */
public class JoinProperty implements Action {

    protected String name;
    protected String separator;
    protected Object emptyValue;

    /**
     * Constructor
     * 
     * @param name property name
     */
    public JoinProperty(@Nonnull String name) {
        this(name, null, ",");
    }

    /**
     * Constructor
     * 
     * @param name       property name
     * @param emptyValue value to set for empty arrays
     */
    public JoinProperty(@Nonnull String name, Object emptyValue) {
        this(name, emptyValue, ",");
    }

    /**
     * Constructor
     * 
     * @param name       property name
     * @param emptyValue value to set for empty arrays
     * @param separator  separator text for joining
     */
    public JoinProperty(@Nonnull String name, Object emptyValue, @Nonnull String separator) {
        this.name = name;
        this.separator = separator;
        this.emptyValue = GStringConverter.convert(emptyValue);
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException {
        ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
        if (properties == null) {
            return "WARNING: could not get ModifiableValueMap for resource " + resource.getPath();
        }
        if (!properties.containsKey(name)) {
            return StringUtils.EMPTY;
        }

        Object value = properties.get(name);

        if (!value.getClass().isArray()) {
            return StringUtils.EMPTY;
        }

        Object[] values = (Object[]) value;

        if (values.length > 0) {
            Node node = resource.adaptTo(Node.class);
            try {
                node.getProperty(name).remove();
                node.setProperty(name, StringUtils.join(values, separator));
            } catch (RepositoryException e) {
                throw new PersistenceException(e.getMessage(), e);
            }
            return "Joined " + value.getClass().getSimpleName() + " property " + name + " for resource " + resource.getPath();
        }

        if (this.emptyValue == null) {
            properties.remove(name);
            return "Removed empty " + value.getClass().getSimpleName() + " property " + name + " for resource "
                    + resource.getPath();
        }

        // replace empty array with fallback
        properties.remove(name);
        properties.put(name, this.emptyValue);
        return "Replaced empty " + value.getClass().getSimpleName() + " property " + name + " with " + this.emptyValue
                + " for resource " + resource.getPath();
    }

}
