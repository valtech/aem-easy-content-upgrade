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

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import de.valtech.aecu.api.groovy.console.bindings.accessrights.ValidationResult;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.AccessValidatorContext;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.AccessValidatorContext.TestUser;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.resource.ModifyAccessValidator;

/**
 * Checks if modify access to pages is available.
 * 
 * @author Roland Gruber
 */
public class ModifyPageAccessValidator extends ModifyAccessValidator {

    protected static final String PROPERTY_NAME = "aecuValidationTestProperty";

    /**
     * Constructor.
     * 
     * @param group              group
     * @param resource           page to check
     * @param context            context
     * @param checkAccessGranted checks if the access is granted or denied
     */
    public ModifyPageAccessValidator(Group group, Resource resource, AccessValidatorContext context, boolean checkAccessGranted) {
        super(group, resource, context, checkAccessGranted);
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
        return canModifyPageWithUser();
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
     * Checks if page can be modified with user rights.
     * 
     * @return validation result
     */
    private ValidationResult canModifyPageWithUser() {
        TestUser testUser = getContext().getTestUserForGroup(group);
        if (testUser == null) {
            return new ValidationResult(true, false, "Unable to create test user");
        }
        PageManager userPageManager = testUser.getResolver().adaptTo(PageManager.class);
        Page page = userPageManager.getPage(getResource().getPath());
        if (page == null) {
            return new ValidationResult(getCheckAccessGranted(), false, "Cannot read page");
        }
        try {
            Resource contentResource = page.getContentResource();
            if (contentResource == null) {
                return new ValidationResult(getCheckAccessGranted(), false, "No jcr:content found");
            }
            ValueMap vm = contentResource.adaptTo(ModifiableValueMap.class);
            if (vm == null) {
                return new ValidationResult(getCheckAccessGranted(), false, "No value map returned");
            }
            vm.put(PROPERTY_NAME, "testvalue");
            Node node = contentResource.adaptTo(Node.class);
            if (node == null) {
                return new ValidationResult(getCheckAccessGranted(), false, "No node returned");
            }
            node.setProperty(PROPERTY_NAME, "testvalue");
        } catch (RepositoryException e) {
            return new ValidationResult(getCheckAccessGranted(), false, e.getMessage());
        } finally {
            testUser.getResolver().revert();
            testUser.getResolver().refresh();
        }
        return new ValidationResult(!getCheckAccessGranted(), false, "Cannot replicate page");
    }

    @Override
    public String getLabel() {
        return getCheckAccessGranted() ? "Modify Page" : "Cannot Modify Page";
    }

}
