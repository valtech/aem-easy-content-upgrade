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
package de.valtech.aecu.core.groovy.console.bindings.actions.properties;

<<<<<<< HEAD:core/src/main/java/de/valtech/aecu/core/groovy/console/bindings/actions/properties/SetStringProperty.java
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
=======
import javax.annotation.Nonnull;

>>>>>>> 8a3edfd7f429d77f0ea258e43b5fdfb4b1b49a2e:core/src/main/java/de/valtech/aecu/core/groovy/console/bindings/actions/SetProperty.java
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;

/**
 * @author Roxana Muresan
 */
public class SetStringProperty implements Action {

    protected String name;
    protected Object value;

    protected SetStringProperty() {}

    public SetStringProperty(@Nonnull String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String doAction(@Nonnull Resource resource) {
        ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
        properties.put(name, value);
        return "Setting " + value.getClass().getSimpleName() + " property " + name + "=" + value + " for resource " + resource.getPath();
    }
}
