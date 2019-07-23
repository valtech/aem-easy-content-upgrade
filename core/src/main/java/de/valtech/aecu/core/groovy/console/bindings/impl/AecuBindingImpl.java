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
package de.valtech.aecu.core.groovy.console.bindings.impl;

import org.apache.sling.api.resource.ResourceResolver;

import com.icfolson.aem.groovy.console.api.ScriptContext;

import de.valtech.aecu.api.groovy.console.bindings.AecuBinding;
import de.valtech.aecu.api.groovy.console.bindings.ContentUpgrade;

/**
 * Groovy Console Bindings for AEM Simple Content Update. This provides the "aecu" binding variable.
 *
 * @author Roxana Muresan
 */
public class AecuBindingImpl implements AecuBinding {

    private ResourceResolver resourceResolver;
    private ScriptContext scriptContext;

    /**
     * Constructor
     * 
     * @param resourceResolver resolver
     * @param scriptContext    Groovy context
     */
    public AecuBindingImpl(ResourceResolver resourceResolver, ScriptContext scriptContext) {
        this.resourceResolver = resourceResolver;
        this.scriptContext = scriptContext;
    }

    @Override
    public ContentUpgrade contentUpgradeBuilder() {
        return new ContentUpgradeImpl(resourceResolver, scriptContext);
    }

}
