/*
 * Copyright 2021 - 2022 Valtech GmbH
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
package de.valtech.aecu.core.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Tests AccessValidationService
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    @BeforeEach
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
