/*
 * Copyright 2020 - 2022 Valtech GmbH
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.day.cq.replication.Replicator;
import be.orbinson.aem.groovy.console.api.BindingExtensionProvider;
import be.orbinson.aem.groovy.console.api.BindingVariable;
import be.orbinson.aem.groovy.console.api.context.ScriptContext;

import de.valtech.aecu.api.groovy.console.bindings.AecuBinding;
import de.valtech.aecu.core.serviceuser.ServiceResourceResolverService;

/**
 * Tests AecuBindingExtensionProvider
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AecuBindingExtensionProviderTest {

    @Mock
    private BindingExtensionProvider defaultBindingExtensionProvider;

    @Mock
    private ServiceResourceResolverService resourceResolverService;

    @Mock
    private ResourceResolverFactory resourceResolverFactory;

    @Mock
    private Replicator replicator;

    @InjectMocks
    private AecuBindingExtensionProvider provider;

    @Mock
    private ScriptContext scriptContext;

    @Test
    public void getBindingVariables() {
        Map<String, BindingVariable> variables = provider.getBindingVariables(scriptContext);

        assertEquals(1, variables.size());
        BindingVariable variable = variables.get("aecu");
        assertNotNull(variable);
        AecuBinding binding = (AecuBinding) variable.getValue();
        assertNotNull(binding);
    }
}
