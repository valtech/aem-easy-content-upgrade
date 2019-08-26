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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.valtech.aecu.api.groovy.console.bindings.ValidateAccessRights;
import de.valtech.aecu.api.groovy.console.bindings.accessrights.AccessRightValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.AccessRightValidatorComparator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.AccessValidatorContext;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.ValidateAccessRightsTable;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.page.CreatePageAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.page.DeletePageAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.page.ReadPageAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.resource.CreateAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.resource.DeleteAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.resource.ModifyAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.resource.ReadAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.resource.ReadAclAccessValidator;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.resource.WriteAclAccessValidator;

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
    private Set<String> warnings = new LinkedHashSet<>();

    private ResourceResolver resolver;
    private AccessValidatorContext context;

    /**
     * Constructor
     * 
     * @param resolver resource resolver
     * @throws RepositoryException error setting up context
     */
    public ValidateAccessRightsImpl(ResourceResolverFactory resourceResolverFactory, ResourceResolver resolver)
            throws RepositoryException {
        this.resolver = resolver;
        context = new AccessValidatorContext(resourceResolverFactory, resolver);
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
    public ValidateAccessRights forGroups(String... groups) {
        authorizablesToCheck.clear();
        for (String authorizable : groups) {
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
        List<Group> groups = resolveGroups();
        for (Group group : groups) {
            for (Resource resource : resources) {
                validators.add(creator.createValidator(group, resource, checkAccessGranted));
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
    public ValidateAccessRights cannotRead() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ReadAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
    }

    @Override
    public ValidateAccessRights canModify() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ModifyAccessValidator(authorizable, resource, context,
                checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights cannotModify() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ModifyAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
    }

    @Override
    public ValidateAccessRights canCreate() {
        addValidators((authorizable, resource, checkAccessGranted) -> new CreateAccessValidator(authorizable, resource, context,
                checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights cannotCreate() {
        addValidators((authorizable, resource, checkAccessGranted) -> new CreateAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
    }

    @Override
    public ValidateAccessRights canDelete() {
        addValidators((authorizable, resource, checkAccessGranted) -> new DeleteAccessValidator(authorizable, resource, context,
                checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights cannotDelete() {
        addValidators((authorizable, resource, checkAccessGranted) -> new DeleteAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
    }

    @Override
    public ValidateAccessRights canReadAcl() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ReadAclAccessValidator(authorizable, resource, context,
                checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights cannotReadAcl() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ReadAclAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
    }

    @Override
    public ValidateAccessRights canWriteAcl() {
        addValidators((authorizable, resource, checkAccessGranted) -> new WriteAclAccessValidator(authorizable, resource, context,
                checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights cannotWriteAcl() {
        addValidators((authorizable, resource, checkAccessGranted) -> new WriteAclAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
    }

    @Override
    public ValidateAccessRights canReadPage() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ReadPageAccessValidator(authorizable, resource, context,
                checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights cannotReadPage() {
        addValidators((authorizable, resource, checkAccessGranted) -> new ReadPageAccessValidator(authorizable, resource, context,
                checkAccessGranted), false);
        return this;
    }

    @Override
    public ValidateAccessRights canCreatePage(String templatePath) {
        addValidators((authorizable, resource, checkAccessGranted) -> new CreatePageAccessValidator(authorizable, resource,
                context, checkAccessGranted, templatePath), true);
        return this;
    }

    @Override
    public ValidateAccessRights cannotCreatePage(String templatePath) {
        addValidators((authorizable, resource, checkAccessGranted) -> new CreatePageAccessValidator(authorizable, resource,
                context, checkAccessGranted, templatePath), false);
        return this;
    }

    @Override
    public ValidateAccessRights canDeletePage() {
        addValidators((authorizable, resource, checkAccessGranted) -> new DeletePageAccessValidator(authorizable, resource,
                context, checkAccessGranted), true);
        return this;
    }

    @Override
    public ValidateAccessRights cannotDeletePage() {
        addValidators((authorizable, resource, checkAccessGranted) -> new DeletePageAccessValidator(authorizable, resource,
                context, checkAccessGranted), false);
        return this;
    }

    @Override
    public String validate(boolean simulate) {
        try {
            validators.sort(new AccessRightValidatorComparator());
            ValidateAccessRightsTable table = new ValidateAccessRightsTable();
            for (AccessRightValidator validator : validators) {
                table.add(validator, simulate);
            }
            StringBuilder output = new StringBuilder();
            output.append(String.join("\n", warnings));
            output.append("\n");
            output.append(table.getText());
            return output.toString();
        } finally {
            context.cleanup();
        }
    }

    @Override
    public String validate() {
        return validate(false);
    }

    @Override
    public String simulate() {
        return validate(true);
    }

    /**
     * Resolves authorizables to be checked.
     * 
     * @return authorizables
     */
    private List<Group> resolveGroups() {
        List<Group> authorizables = new ArrayList<>();
        UserManager userManager = resolver.adaptTo(UserManager.class);
        for (String groupName : authorizablesToCheck) {
            Authorizable authorizable;
            try {
                authorizable = userManager.getAuthorizable(groupName);
                if ((authorizable != null) && authorizable.isGroup()) {
                    authorizables.add((Group) authorizable);
                } else {
                    warnings.add("Unable to resolve " + groupName);
                }
            } catch (RepositoryException e) {
                String message = "Unable to resolve " + groupName;
                LOG.warn(message);
                warnings.add(message);
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
                String message = "Unable to resolve " + path;
                LOG.warn(message);
                warnings.add(message);
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
         * @param group              group
         * @param resource           resource
         * @param checkAccessGranted check for granted access
         * @return validator
         */
        AccessRightValidator createValidator(Group group, Resource resource, boolean checkAccessGranted);

    }

}
