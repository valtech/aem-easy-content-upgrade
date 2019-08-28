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
package de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.page;

import javax.jcr.Session;

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import de.valtech.aecu.api.groovy.console.bindings.accessrights.ValidationResult;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.AccessValidatorContext;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.AccessValidatorContext.TestUser;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.resource.ReplicateAccessValidator;

/**
 * Checks if replicate access to pages is available.
 * 
 * @author Roland Gruber
 */
public class ReplicatePageAccessValidator extends ReplicateAccessValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ReplicatePageAccessValidator.class);

    private ReplicationActionType type;

    /**
     * Constructor.
     * 
     * @param group              group
     * @param resource           page to check
     * @param checkAccessGranted checks if the access is granted or denied
     * @param type               action type
     */
    public ReplicatePageAccessValidator(Group group, Resource resource, AccessValidatorContext context,
            boolean checkAccessGranted, ReplicationActionType type) {
        super(group, resource, context, checkAccessGranted);
        this.type = type;
    }

    @Override
    public ValidationResult validate(boolean simulate) {
        ValidationResult resourceResult = super.validate(simulate);
        if (!resourceResult.isSuccessful()) {
            return resourceResult;
        }
        if (!pageExists()) {
            return new ValidationResult(false, true, "Page not found");
        }
        boolean deleteAccess = canReplicatePageWithUser(simulate);
        boolean failed = getCheckAccessGranted() ? !deleteAccess : deleteAccess;
        return new ValidationResult(failed, false, null);
    }

    /**
     * Checks if the given page exists.
     * 
     * @return page exists
     */
    private boolean pageExists() {
        Page page = getContext().getAdminPageManager().getPage(getResource().getPath());
        return page != null;
    }

    /**
     * Checks if page can be deleted with user rights.
     * 
     * @return can read page
     */
    private boolean canReplicatePageWithUser(boolean simulate) {
        TestUser testUser = getContext().getTestUserForGroup(group);
        if (testUser == null) {
            return false;
        }
        PageManager userPageManager = testUser.getResolver().adaptTo(PageManager.class);
        Page page = userPageManager.getPage(getResource().getPath());
        if (page == null) {
            return false;
        }
        if (!simulate) {
            return true;
        }
        try {
            Session session = testUser.getResolver().adaptTo(Session.class);
            getContext().getReplicator().replicate(session, type, page.getPath());
        } catch (ReplicationException e) {
            LOG.error("Unable to perform replictaion", e);
            return false;
        } finally {
            testUser.getResolver().revert();
            testUser.getResolver().refresh();
        }
        return true;
    }

    @Override
    public String getLabel() {
        return getCheckAccessGranted() ? "Replicate Page" : "Cannot Replicate Page";
    }

}
