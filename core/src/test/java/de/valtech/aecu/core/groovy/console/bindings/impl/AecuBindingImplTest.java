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
package de.valtech.aecu.core.groovy.console.bindings.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.day.cq.replication.Replicator;
import be.orbinson.aem.groovy.console.api.context.ScriptContext;

/**
 * Tests AecuBindingImpl
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AecuBindingImplTest {

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private ResourceResolver adminResourceResolver;

    @Mock
    private Session adminSession;

    @Mock
    private AccessControlManager aclManager;

    @Mock
    private Privilege privilege;

    @Mock
    private ResourceResolverFactory resourceResolverFactory;

    @Mock
    private Replicator replicator;

    @Mock
    private ScriptContext scriptContext;

    private AecuBindingImpl binding;

    @BeforeEach
    public void setup() throws UnsupportedRepositoryOperationException, RepositoryException {
        when(adminResourceResolver.adaptTo(Session.class)).thenReturn(adminSession);
        when(adminSession.getAccessControlManager()).thenReturn(aclManager);
        when(aclManager.privilegeFromName(Mockito.anyString())).thenReturn(privilege);
        binding =
                new AecuBindingImpl(resourceResolver, adminResourceResolver, resourceResolverFactory, replicator, scriptContext);
    }

    @Test
    public void contentUpgradeBuilder() {
        assertNotNull(binding.contentUpgradeBuilder());
    }

    @Test
    public void validateAccessRights() {
        assertNotNull(binding.validateAccessRights());
    }

}
