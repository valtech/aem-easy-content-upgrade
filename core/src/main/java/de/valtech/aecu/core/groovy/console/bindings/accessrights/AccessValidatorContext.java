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
package de.valtech.aecu.core.groovy.console.bindings.accessrights;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.PrincipalIterator;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.security.util.CqActions;

/**
 * Contains common objects for access validation.
 * 
 * @author Roland Gruber
 */
public class AccessValidatorContext {

    private static final Logger LOG = LoggerFactory.getLogger(AccessValidatorContext.class);

    private ResourceResolver resolver;
    private Session session;
    private CqActions cqActions;
    private Map<String, Set<Principal>> principalCache = new HashMap<>();

    public AccessValidatorContext(ResourceResolver resolver) throws RepositoryException {
        this.resolver = resolver;
        this.session = resolver.adaptTo(Session.class);
        this.cqActions = new CqActions(session);
    }

    /**
     * Returns the admin resource resolver.
     * 
     * @return resolver
     */
    public ResourceResolver getResolver() {
        return resolver;
    }

    /**
     * Returns the admin session.
     * 
     * @return session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Returns the CqActions.
     * 
     * @return CqActions
     */
    public CqActions getCqActions() {
        return cqActions;
    }

    public Set<Principal> getPrincipals(Authorizable authorizable) {
        Set<Principal> principals = new HashSet<>();
        try {
            String authorizableId = authorizable.getID();
            if (principalCache.containsKey(authorizableId)) {
                return principalCache.get(authorizableId);
            }
            Principal principal = authorizable.getPrincipal();
            principals.add(principal);
            PrincipalIterator it = ((JackrabbitSession) getSession()).getPrincipalManager().getGroupMembership(principal);
            while (it.hasNext()) {
                principals.add(it.nextPrincipal());
            }
            principalCache.put(authorizableId, principals);
        } catch (RepositoryException e) {
            LOG.error("Error resolving principals", e);
        }
        return principals;
    }

}
