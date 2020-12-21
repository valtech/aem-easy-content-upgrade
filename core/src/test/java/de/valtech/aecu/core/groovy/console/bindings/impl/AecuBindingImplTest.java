package de.valtech.aecu.core.groovy.console.bindings.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.day.cq.replication.Replicator;
import com.icfolson.aem.groovy.console.api.context.ScriptContext;

/**
 * Tests AecuBindingImpl
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
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

    @Before
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
