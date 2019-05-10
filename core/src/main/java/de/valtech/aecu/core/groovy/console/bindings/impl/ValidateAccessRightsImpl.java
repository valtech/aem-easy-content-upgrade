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
package de.valtech.aecu.core.groovy.console.bindings.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.valtech.aecu.api.groovy.console.bindings.ValidateAccessRights;
import de.valtech.aecu.api.groovy.console.bindings.accessrights.AccessRightValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.AccessRightValidatorComparator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.ValidateAccessRightsTable;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.ReadAccessValidator;

/**
 * Validates access rights for users or groups.
 * 
 * @author Roland Gruber
 */
public class ValidateAccessRightsImpl implements ValidateAccessRights {

    private static final Logger LOG = LoggerFactory.getLogger(ValidateAccessRightsImpl.class);

    private Set<String> pathsToCheck = new HashSet<>();

    private Set<String> authorizablesToCheck = new HashSet<>();

    private List<AccessRightValidator> validators = new ArrayList<>();

    private ResourceResolver resolver;

    /**
     * Constructor
     * 
     * @param resolver resource resolver
     */
    public ValidateAccessRightsImpl(ResourceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public ValidateAccessRights forPaths(String... paths) {
        pathsToCheck.clear();
        for (String path : paths) {
            this.pathsToCheck.add(path);
        }
        return this;
    }

    @Override
    public ValidateAccessRights forAuthorizables(String... authorizables) {
        authorizablesToCheck.clear();
        for (String authorizable : authorizables) {
            authorizablesToCheck.add(authorizable);
        }
        return this;
    }

    @Override
    public ValidateAccessRights canRead() {
        List<Resource> resources = resolveResources();
        List<Authorizable> authorizables = resolveAuthorizables();
        for (Authorizable authorizable : authorizables) {
            for (Resource resource : resources) {
                validators.add(new ReadAccessValidator(authorizable, resource));
            }
        }
        return this;
    }

    @Override
    public ValidateAccessRights canWrite() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String validate() {
        validators.sort(new AccessRightValidatorComparator());
        ValidateAccessRightsTable table = new ValidateAccessRightsTable();
        for (AccessRightValidator validator : validators) {
            table.add(validator);
        }
        return table.getText();
    }

    private List<Authorizable> resolveAuthorizables() {
        List<Authorizable> authorizables = new ArrayList<>();
        UserManager userManager = resolver.adaptTo(UserManager.class);
        for (String authorizableName : authorizablesToCheck) {
            Authorizable authorizable;
            try {
                authorizable = userManager.getAuthorizable(authorizableName);
                if (authorizable != null) {
                    authorizables.add(authorizable);
                }
            } catch (RepositoryException e) {
                LOG.warn("Unable to resolve {}", authorizableName);
            }
        }
        return authorizables;
    }

    /**
     * Resolves all given paths to resources.
     * 
     * @return resource list
     */
    private List<Resource> resolveResources() {
        List<Resource> resources = new ArrayList<>();
        for (String path : pathsToCheck) {
            Resource resource = resolver.getResource(path);
            if (resource == null) {
                LOG.warn("Unable to resolve {}", path);
            } else {
                resources.add(resource);
            }
        }
        return resources;
    }

}
