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
package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import java.util.List;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.value.StringValue;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;

/**
 * Replaces strings in resource properties.
 * 
 * @author Roland Gruber
 */
public class ReplaceResourcePropertyValues implements Action {

    private String oldValue;
    private String newValue;
    private List<String> propertyNames;

    /**
     * Constructor
     * 
     * @param oldValue      old value
     * @param newValue      new value
     * @param propertyNames property names to check
     */
    public ReplaceResourcePropertyValues(@Nonnull String oldValue, @Nonnull String newValue,
            @Nonnull List<String> propertyNames) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.propertyNames = propertyNames;
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException {
        Node node = resource.adaptTo(Node.class);
        if (node == null) {
            return StringUtils.EMPTY;
        }
        boolean updated = false;
        try {
            PropertyIterator propertyIterator = node.getProperties();
            while (propertyIterator.hasNext()) {
                Property property = propertyIterator.nextProperty();
                if (doChangeProperty(property)) {
                    if (property.isMultiple()) {
                        boolean propUpdated = updateMulti(property);
                        updated = updated || propUpdated;
                    } else {
                        boolean propUpdated = updateSingle(property);
                        updated = updated || propUpdated;
                    }
                }
            }
        } catch (RepositoryException e) {
            throw new PersistenceException("Replace failed for " + resource.getPath(), e);
        }
        if (updated) {
            return "Updated values from " + oldValue + " to " + newValue + " in " + resource.getPath();
        }
        return StringUtils.EMPTY;
    }

    /**
     * Updates a single value property.
     * 
     * @param property property
     * @return property was updated
     * @throws RepositoryException error setting property
     */
    private boolean updateSingle(Property property) throws RepositoryException {
        if (!valueMatches(property.getString())) {
            return false;
        }
        String newPropertyValue = getNewValue(property.getString());
        if (property.getString().equals(newPropertyValue)) {
            return false;
        }
        property.setValue(newPropertyValue);
        return true;
    }

    /**
     * Updates a multi value property.
     * 
     * @param property property
     * @return property was updated
     * @throws RepositoryException error setting property
     */
    private boolean updateMulti(Property property) throws RepositoryException {
        Value[] values = property.getValues();
        boolean updated = false;
        for (int i = 0; i < values.length; i++) {
            Value value = values[i];
            if (valueMatches(value.getString())) {
                String newPropertyValue = getNewValue(value.getString());
                if (value.getString().equals(newPropertyValue)) {
                    continue;
                }
                values[i] = new StringValue(newPropertyValue);
                updated = true;
            }
        }
        if (updated) {
            property.setValue(values);
        }
        return updated;
    }

    /**
     * Checks if the value matches the searched value.
     * 
     * @param value content property value
     * @return matches condition
     */
    protected boolean valueMatches(String value) {
        return value.contains(oldValue);
    }

    /**
     * Returns the new property value.
     * 
     * @param propertyValue old property value
     * @return new value
     */
    protected String getNewValue(String propertyValue) {
        return propertyValue.replace(oldValue, newValue);
    }

    /**
     * Checks if this property name should be updated.
     * 
     * @param property property
     * @return update
     * @throws RepositoryException error reading property
     */
    private boolean doChangeProperty(Property property) throws RepositoryException {
        if (property.getType() != PropertyType.STRING) {
            return false;
        }
        String propertyName = property.getName();
        return (propertyNames.isEmpty() || propertyNames.contains(propertyName));
    }

}
