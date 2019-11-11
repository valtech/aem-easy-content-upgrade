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

package de.valtech.aecu.core.groovy.console.bindings.actions.print;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;

/**
 * Action for printing the name and value of a given property.
 *
 * @author sravan
 * @author Roxana Muresan
 */
public class PrintProperty implements Action {

    private static final Logger LOG = LoggerFactory.getLogger(PrintProperty.class);

    private String propertyName;

    public PrintProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String doAction(@Nonnull Resource resource) {
        Node node = resource.adaptTo(Node.class);
        String output = propertyName + " = ";
        try {
            if (node.hasProperty(propertyName)) {
                Property prop = node.getProperty(propertyName);

                // This check is necessary to ensure a multi-valued property
                if (prop.isMultiple()) {
                    Value[] values = prop.getValues();
                    String[] valuesAsStrings = new String[values.length];
                    for (int i = 0; i < values.length; i++) {
                        valuesAsStrings[i] = values[i].getString();
                    }
                    output = output + Arrays.toString(valuesAsStrings);
                } else {
                    output = output + prop.getValue().getString();
                }

                return output;
            }
            return propertyName + " not defined";
        } catch (RepositoryException e) {
            LOG.debug("Cannot read value of [{}]. Reason [{}]", propertyName, e.getMessage());
        }
        return "";
    }
}
