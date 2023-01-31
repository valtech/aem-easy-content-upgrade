/*
 * Copyright 2018 - 2020 Valtech GmbH
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
package de.valtech.aecu.core.groovy.console.bindings.provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;
import be.orbinson.aem.groovy.console.api.BindingExtensionProvider;
import be.orbinson.aem.groovy.console.api.BindingVariable;
import be.orbinson.aem.groovy.console.api.context.ScriptContext;

import de.valtech.aecu.api.groovy.console.bindings.AecuBinding;
import de.valtech.aecu.core.groovy.console.bindings.impl.AecuBindingImpl;
import de.valtech.aecu.core.serviceuser.ServiceResourceResolverService;

/**
 * Provides additional AECU Bindings for the Groovy Console
 *
 * @author Roxana Muresan
 */
@Component(immediate = true, service = {BindingExtensionProvider.class, AecuBindingExtensionProvider.class})
public class AecuBindingExtensionProvider implements BindingExtensionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AecuBindingExtensionProvider.class);

    @Reference
    private BindingExtensionProvider defaultBindingExtensionProvider;
    @Reference
    private ServiceResourceResolverService resourceResolverService;
    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    @Reference
    private Replicator replicator;


    @Override
    public Map<String, BindingVariable> getBindingVariables(ScriptContext context) {
        Map<String, BindingVariable> variables = new HashMap<>();
        try {
            AecuBinding aecuBinding = new AecuBindingImpl(resourceResolverService.getContentMigratorResourceResolver(),
                    resourceResolverService.getAdminResourceResolver(), resourceResolverFactory, replicator, context);
            BindingVariable aecuVar =
                    new BindingVariable(aecuBinding, AecuBinding.class, "https://github.com/valtech/aem-easy-content-upgrade");
            variables.put(AecuBinding.BINDING_NAME, aecuVar);
        } catch (LoginException e) {
            LOG.error(
                    "Failed to get resource resolver for aecu-content-migrator or aecu-admin, make sure you all the configurations needed for this system user are deployed.");
        }
        return variables;
    }

}
