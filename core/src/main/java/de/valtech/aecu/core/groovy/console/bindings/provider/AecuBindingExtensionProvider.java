/*
 *  Copyright 2018 Valtech GmbH
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package de.valtech.aecu.core.groovy.console.bindings.provider;

import com.icfolson.aem.groovy.console.api.BindingExtensionProvider;
import de.valtech.aecu.core.groovy.console.bindings.SimpleContentUpdate;
import de.valtech.aecu.core.groovy.console.bindings.hello.HelloWorld;
import groovy.lang.Binding;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides additional AECU Bindings for the Groovy Console
 * @author Roxana Muresan
 */
@Component(immediate = true)
public class AecuBindingExtensionProvider implements BindingExtensionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AecuBindingExtensionProvider.class);

    @Reference
    private BindingExtensionProvider defaultBindingExtensionProvider;


    @Override
    public Binding getBinding(SlingHttpServletRequest request) {
        Binding binding = defaultBindingExtensionProvider.getBinding(request);
        binding.setVariable("helloWorld", new HelloWorld());
        binding.setVariable("simpleContentUpdate", new SimpleContentUpdate(request.getResourceResolver()));

        return binding;
    }

}
