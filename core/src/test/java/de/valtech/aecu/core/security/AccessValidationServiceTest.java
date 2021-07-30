package de.valtech.aecu.core.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.spi.security.user.UserConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests AccessValidationService
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class AccessValidationServiceTest {


    private static final String VALID_GROUP = "validGroup";

    private AccessValidationService service;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private Principal principal;

    @Mock
    private Authorizable authorizable;

    @Mock
    private Group validGroup;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private UserManager userManager;

    @Mock
    private AccessValidationServiceConfiguration config;

    @Before
    public void setup() throws RepositoryException {
        service = new AccessValidationService();
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("user");
        when(userManager.getAuthorizable(principal)).thenReturn(authorizable);
        List<Group> groups = new ArrayList<>();
        groups.add(validGroup);
        when(validGroup.getID()).thenReturn(VALID_GROUP);
        when(authorizable.memberOf()).thenReturn(groups.iterator());
        when(request.getResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(UserManager.class)).thenReturn(userManager);
    }

    @Test
    public void canReadHistory_Admin() {
        when(principal.getName()).thenReturn(UserConstants.DEFAULT_ADMIN_ID);

        service.activate(config);

        assertTrue(service.canReadHistory(request));
    }

    @Test
    public void canReadHistory_Administrators() throws RepositoryException {
        when(principal.getName()).thenReturn("user");
        when(validGroup.getID()).thenReturn(AccessValidationService.ADMINISTRATORS);

        service.activate(config);

        assertTrue(service.canReadHistory(request));
    }

    @Test
    public void canReadHistory_NotAllowedNoGroupConfigured() {
        when(principal.getName()).thenReturn("user");

        service.activate(config);

        assertFalse(service.canReadHistory(request));
    }

    @Test
    public void canReadHistory_NotAllowedWrongGroup() {
        when(principal.getName()).thenReturn("user");
        when(config.readers()).thenReturn(new String[] {"group"});

        service.activate(config);

        assertFalse(service.canReadHistory(request));
    }

    @Test
    public void canReadHistory_AllowedValidGroup() {
        when(principal.getName()).thenReturn("user");
        when(config.readers()).thenReturn(new String[] {VALID_GROUP});

        service.activate(config);

        assertTrue(service.canReadHistory(request));
    }

    @Test
    public void canExecute_Admin() {
        when(principal.getName()).thenReturn(UserConstants.DEFAULT_ADMIN_ID);

        service.activate(config);

        assertTrue(service.canExecute(request));
    }

    @Test
    public void canExecute_Administrators() throws RepositoryException {
        when(principal.getName()).thenReturn("user");
        when(validGroup.getID()).thenReturn(AccessValidationService.ADMINISTRATORS);

        service.activate(config);

        assertTrue(service.canExecute(request));
    }

    @Test
    public void canExecute_NotAllowedNoGroupConfigured() {
        when(principal.getName()).thenReturn("user");

        service.activate(config);

        assertFalse(service.canExecute(request));
    }

    @Test
    public void canExecute_NotAllowedWrongGroup() {
        when(principal.getName()).thenReturn("user");
        when(config.executers()).thenReturn(new String[] {"group"});

        service.activate(config);

        assertFalse(service.canExecute(request));
    }

    @Test
    public void canExecute_AllowedValidGroup() {
        when(principal.getName()).thenReturn("user");
        when(config.executers()).thenReturn(new String[] {VALID_GROUP});

        service.activate(config);

        assertTrue(service.canExecute(request));
    }

}
