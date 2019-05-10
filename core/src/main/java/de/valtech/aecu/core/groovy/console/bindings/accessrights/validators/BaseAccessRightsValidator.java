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
package de.valtech.aecu.core.groovy.console.bindings.accessrights.validators;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.valtech.aecu.api.groovy.console.bindings.accessrights.AccessRightValidator;

/**
 * Base class for access right validators.
 * 
 * @author Roland Gruber
 */
public abstract class BaseAccessRightsValidator implements AccessRightValidator {

    private static final Logger LOG = LoggerFactory.getLogger(BaseAccessRightsValidator.class);

    protected Authorizable authorizable;
    private Resource resource;

    /**
     * Constructor.
     * 
     * @param authorizable user or group
     * @param resource     resource to check
     */
    protected BaseAccessRightsValidator(Authorizable authorizable, Resource resource) {
        this.authorizable = authorizable;
        this.resource = resource;
    }

    @Override
    public String getAuthorizableId() {
        try {
            return authorizable.getID();
        } catch (RepositoryException e) {
            LOG.error("Authorizable cannot be resolved", e);
            return StringUtils.EMPTY;
        }
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public String toString() {
        return getAuthorizableId() + " - " + resource.getPath() + " - " + getLabel();
    }

}
