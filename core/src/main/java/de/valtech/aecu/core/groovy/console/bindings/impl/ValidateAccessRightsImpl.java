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
import de.valtech.aecu.core.groovy.console.bindings.accessrights.AccessValidatorContext;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.ValidateAccessRightsTable;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.CreateAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.DeleteAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.ModifyAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.ReadAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.ReadAclAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.WriteAclAccessValidator;

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
    private AccessValidatorContext context;

    /**
     * Constructor
     * 
     * @param resolver resource resolver
     * @throws RepositoryException error setting up context
     */
    public ValidateAccessRightsImpl(ResourceResolver resolver) throws RepositoryException {
        this.resolver = resolver;
        context = new AccessValidatorContext(resolver);
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

    /**
     * Adds validators based on set authorizables and resources.
     * 
     * @param creator            creator function for validator
     * @param checkAccessGranted check for granted access
     */
    private void addValidators(ValidatorCreator creator, boolean checkAccessGranted) {
        List<Resource> resources = resolveResources();
        List<Authorizable> authorizables = resolveAuthorizables();
        for (Authorizable authorizable : authorizables) {
            for (Resource resource : resources) {
                validators.add(creator.createValidator(authorizable, resource, checkAccessGranted));
            }
        }
    }

    @Override
    public ValidateAccessRights canRead() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ReadAccessValidator(authorizable, resource, context,
                checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights canModify() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ModifyAccessValidator(authorizable, resource, context,
                checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights canCreate() {
        addValidators((authorizable, resource, checkAccessGranted) -> new CreateAccessValidator(authorizable, resource, context,
                checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights canDelete() {
        addValidators((authorizable, resource, checkAccessGranted) -> new DeleteAccessValidator(authorizable, resource, context,
                checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights canReadAcl() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ReadAclAccessValidator(authorizable, resource, context,
                checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights canWriteAcl() {
        addValidators((authorizable, resource, checkAccessGranted) -> new WriteAclAccessValidator(authorizable, resource, context,
                checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights cannotRead() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ReadAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
    }

    @Override
    public ValidateAccessRights cannotModify() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ModifyAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
    }

    @Override
    public ValidateAccessRights cannotCreate() {
        addValidators((authorizable, resource, checkAccessGranted) -> new CreateAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
    }

    @Override
    public ValidateAccessRights cannotDelete() {
        addValidators((authorizable, resource, checkAccessGranted) -> new DeleteAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
    }

    @Override
    public ValidateAccessRights cannotReadAcl() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ReadAclAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
    }

    @Override
    public ValidateAccessRights cannotWriteAcl() {
        addValidators((authorizable, resource, checkAccessGranted) -> new WriteAclAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
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

    /**
     * Resolves authorizables to be checked.
     * 
     * @return authorizables
     */
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

    /**
     * Functional interface to create validator objects.
     * 
     * @author Roland Gruber
     */
    @FunctionalInterface
    private static interface ValidatorCreator {

        /**
         * Creates a new validator object.
         * 
         * @param authorizable       user/group
         * @param resource           resource
         * @param checkAccessGranted check for granted access
         * @return validator
         */
        AccessRightValidator createValidator(Authorizable authorizable, Resource resource, boolean checkAccessGranted);

    }

}
