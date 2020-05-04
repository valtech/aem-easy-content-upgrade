/*
 * Copyright 2020 Valtech GmbH
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.spi.security.user.UserConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Service to check if an action is allowed.
 * 
 * @author Roland Gruber
 */
@Component(service = AccessValidationService.class)
@Designate(ocd = AccessValidationServiceConfiguration.class)
public class AccessValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(AccessValidationService.class);

    protected String[] readers;

    protected String[] executers;

    @Activate
    public void activate(AccessValidationServiceConfiguration config) {
        readers = config.readers();
        executers = config.executers();
    }

    /**
     * Checks if the current user is allowed to read the AECU history.
     * 
     * @param request request
     * @return read allowed
     */
    public boolean canReadHistory(SlingHttpServletRequest request) {
        return isAdminOrInAllowedList(request, readers);
    }

    /**
     * Checks if the user is allowed to execute scripts.
     * 
     * @param request request
     * @return execute allowed
     */
    public boolean canExecute(SlingHttpServletRequest request) {
        return isAdminOrInAllowedList(request, executers);
    }

    /**
     * Returns if the user is either admin or part of the provided groups.
     * 
     * @param request request
     * @param groups  groups that are allowed
     * @return is allowed
     */
    private boolean isAdminOrInAllowedList(SlingHttpServletRequest request, String[] groups) {
        String userName = getUserName(request);
        if (isAdmin(userName)) {
            return true;
        }
        if (groups == null) {
            return false;
        }
        List<String> userGroups = getUserGroupNames(request);
        for (String group : groups) {
            if (userGroups.contains(group)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts the user name from the request.
     * 
     * @param request request
     * @return user name
     */
    private String getUserName(SlingHttpServletRequest request) {
        return request.getUserPrincipal().getName();
    }

    /**
     * Returns the AEM groups that belong to the user.
     * 
     * @param user user
     * @return group names
     */
    private List<String> getUserGroupNames(SlingHttpServletRequest request) {
        List<String> groupList = new ArrayList<>();
        UserManager userManager = request.getResourceResolver().adaptTo(UserManager.class);
        try {
            Authorizable authorizable = userManager.getAuthorizable(request.getUserPrincipal());
            Iterator<Group> groupIt = authorizable.memberOf();
            while (groupIt.hasNext()) {
                groupList.add(groupIt.next().getID());
            }
        } catch (RepositoryException e) {
            LOG.error("Unable to get groups", e);
        }
        return groupList;
    }

    /**
     * Returns if the user is admin.
     * 
     * @param userName user name
     * @return is admin
     */
    private boolean isAdmin(String userName) {
        return UserConstants.DEFAULT_ADMIN_ID.equals(userName);
    }

}
