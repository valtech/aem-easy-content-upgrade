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
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.PrincipalIterator;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;
import com.day.cq.security.util.CqActions;
import com.day.cq.wcm.api.PageManager;

/**
 * Contains common objects for access validation.
 * 
 * @author Roland Gruber
 */
public class AccessValidatorContext {

    private static final Logger LOG = LoggerFactory.getLogger(AccessValidatorContext.class);

    private ResourceResolverFactory resourceResolverFactory;
    private ResourceResolver adminResolver;
    private Session adminSession;
    private CqActions cqActions;
    private PageManager adminPageManager;
    private UserManager adminUserManager;
    private Replicator replicator;
    private Map<String, Set<Principal>> principalCache = new HashMap<>();
    /** maps groups to test user data */
    private Map<Group, TestUser> testUsers = new HashMap<>();

    /**
     * Constructor
     * 
     * @param resourceResolverFactory resource resolver factory
     * @param adminResolver           admin resource resolver
     * @param replicator              replicator
     * @throws RepositoryException error setting up CqActions
     */
    public AccessValidatorContext(ResourceResolverFactory resourceResolverFactory, ResourceResolver adminResolver,
            Replicator replicator) throws RepositoryException {
        this.resourceResolverFactory = resourceResolverFactory;
        this.adminResolver = adminResolver;
        this.adminSession = adminResolver.adaptTo(Session.class);
        this.cqActions = new CqActions(adminSession);
        this.adminPageManager = adminResolver.adaptTo(PageManager.class);
        this.adminUserManager = adminResolver.adaptTo(UserManager.class);
        this.replicator = replicator;
    }

    /**
     * Returns the admin resource resolver.
     * 
     * @return resolver
     */
    public ResourceResolver getAdminResolver() {
        return adminResolver;
    }

    /**
     * Returns the admin session.
     * 
     * @return session
     */
    public Session getAdminSession() {
        return adminSession;
    }

    /**
     * Returns the CqActions.
     * 
     * @return CqActions
     */
    public CqActions getCqActions() {
        return cqActions;
    }

    /**
     * Returns the admin page manager.
     * 
     * @return page manager
     */
    public PageManager getAdminPageManager() {
        return adminPageManager;
    }

    /**
     * Returns the replicator.
     * 
     * @return replicator
     */
    public Replicator getReplicator() {
        return replicator;
    }

    /**
     * Returns the principals for the given authorizable.
     * 
     * @param authorizable authorizable
     * @return principals
     */
    public Set<Principal> getPrincipals(Authorizable authorizable) {
        Set<Principal> principals = new HashSet<>();
        try {
            String authorizableId = authorizable.getID();
            if (principalCache.containsKey(authorizableId)) {
                return principalCache.get(authorizableId);
            }
            Principal principal = authorizable.getPrincipal();
            principals.add(principal);
            PrincipalIterator it = ((JackrabbitSession) getAdminSession()).getPrincipalManager().getGroupMembership(principal);
            while (it.hasNext()) {
                principals.add(it.nextPrincipal());
            }
            principalCache.put(authorizableId, principals);
        } catch (RepositoryException e) {
            LOG.error("Error resolving principals", e);
        }
        return principals;
    }

    /**
     * Returns the test user for the given group.
     * 
     * @param group group
     * @return test user
     */
    public TestUser getTestUserForGroup(Group group) {
        if (testUsers.containsKey(group)) {
            return testUsers.get(group);
        }
        try {
            String userId = generateUserId();
            String userPassword = generateUserPassword();
            User user = createTestUser(group, userId, userPassword);
            ResourceResolver userResourceResolver = login(userId, userPassword);
            TestUser testUser = new TestUser(user, group, userResourceResolver);
            testUsers.put(group, testUser);
            return testUser;
        } catch (PersistenceException | RepositoryException | LoginException e) {
            LOG.error("Unable to create temporary test user", e);
        }
        return null;
    }

    /**
     * Generates a temporary test user name.
     * 
     * @return user id
     */
    private String generateUserId() {
        SecureRandom random = new SecureRandom();
        return "aecu-testuser-" + System.currentTimeMillis() + "_" + random.nextLong();
    }

    /**
     * Generates a password for the test user.
     * 
     * @return password
     */
    private String generateUserPassword() {
        SecureRandom random = new SecureRandom();
        byte[] passwordBytes = new byte[10];
        random.nextBytes(passwordBytes);
        return System.currentTimeMillis() + "_" + Base64.getEncoder().encodeToString(passwordBytes);
    }

    /**
     * Performs a login with the test user credentials.
     * 
     * @param userId       user name
     * @param userPassword user password
     * @return resource resolver
     * @throws LoginException error logging in
     */
    private ResourceResolver login(String userId, String userPassword) throws LoginException {
        final Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.USER, userId);
        authInfo.put(ResourceResolverFactory.PASSWORD, userPassword.toCharArray());
        return resourceResolverFactory.getResourceResolver(authInfo);
    }

    /**
     * Creates a test user that belongs to the given group.
     * 
     * @param group    group
     * @param userId   user name
     * @param password user password
     * @return user
     * @throws RepositoryException  error creating user
     * @throws PersistenceException error creating user
     */
    private User createTestUser(Group group, String userId, String password) throws RepositoryException, PersistenceException {
        User user = adminUserManager.createUser(userId, password);
        adminResolver.commit();
        group.addMember(user);
        adminResolver.commit();
        return user;
    }

    /**
     * Cleans up any resources created by the context.
     */
    public void cleanup() {
        adminResolver.revert();
        for (TestUser testUser : testUsers.values()) {
            if (testUser.resolver != null) {
                testUser.resolver.revert();
                testUser.resolver.close();
            }
            try {
                adminResolver.refresh();
                testUser.group.removeMember(testUser.user);
                testUser.user.remove();
                adminResolver.commit();
            } catch (RepositoryException | PersistenceException e) {
                LOG.error("Unable to delete temporary user", e);
                adminResolver.revert();
            }
        }
    }

    /**
     * Encapsulates the data to simulate access for a specific group.
     * 
     * @author Roland Gruber
     */
    public static class TestUser {

        /**
         * The group that the temporary user belongs to.
         */
        private Group group;

        /**
         * Temporary user used for group access testing.
         */
        private User user;

        /**
         * Resource resolver used by the temporary user.
         */
        private ResourceResolver resolver;

        /**
         * Constructor
         * 
         * @param user     user
         * @param group    group
         * @param resolver user resolver
         */
        public TestUser(User user, Group group, ResourceResolver resolver) {
            this.user = user;
            this.group = group;
            this.resolver = resolver;
        }

        /**
         * Returns the group.
         * 
         * @return group
         */
        public Group getGroup() {
            return group;
        }

        /**
         * Returns the test user.
         * 
         * @return test user
         */
        public User getUser() {
            return user;
        }

        /**
         * Returns the user resource resolver.
         * 
         * @return resolver
         */
        public ResourceResolver getResolver() {
            return resolver;
        }

    }

}
