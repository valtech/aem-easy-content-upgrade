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

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.resource.Resource;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;

import de.valtech.aecu.api.groovy.console.bindings.accessrights.ValidationResult;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.AccessValidatorContext;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.AccessValidatorContext.TestUser;
import de.valtech.aecu.core.groovy.console.bindings.accessrights.validators.resource.CreateAccessValidator;

/**
 * Checks if create access to pages is available.
 * 
 * @author Roland Gruber
 */
public class CreatePageAccessValidator extends CreateAccessValidator {

    private String templatePath;

    /**
     * Constructor.
     * 
     * @param group              group
     * @param resource           page to check
     * @param checkAccessGranted checks if the access is granted or denied
     * @param templatePath       template path
     */
    public CreatePageAccessValidator(Group group, Resource resource, AccessValidatorContext context, boolean checkAccessGranted,
            String templatePath) {
        super(group, resource, context, checkAccessGranted);
        this.templatePath = templatePath;
    }

    @Override
    public ValidationResult validate(boolean simulate) {
        ValidationResult resourceResult = super.validate(simulate);
        // stop if no access and check for granted permission
        // otherwise, check also template later
        if (!resourceResult.isSuccessful() && getCheckAccessGranted()) {
            return resourceResult;
        }
        if (!pageExists()) {
            return new ValidationResult(false, true, "Page not found");
        }
        return canCreatePageWithUser();
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
     * @return validation result
     */
    private ValidationResult canCreatePageWithUser() {
        TestUser testUser = getContext().getTestUserForGroup(group);
        if (testUser == null) {
            return new ValidationResult(true, false, "Unable to create test user");
        }
        PageManager userPageManager = testUser.getResolver().adaptTo(PageManager.class);
        Page page = userPageManager.getPage(getResource().getPath());
        try {
            Page subpage = userPageManager.create(page.getPath(), "aecu-testpage", templatePath, "AECU Test", false);
            Template template = subpage.getTemplate();
            if (template == null) {
                return new ValidationResult(getCheckAccessGranted(), false, "Cannot read template");
            }
            if (!template.isAllowed(page.getPath())) {
                return new ValidationResult(getCheckAccessGranted(), false, "Template not allowed at this location");
            }
        } catch (WCMException e) {
            return new ValidationResult(getCheckAccessGranted(), false, e.getMessage());
        } finally {
            testUser.getResolver().revert();
            testUser.getResolver().refresh();
        }
        return new ValidationResult(!getCheckAccessGranted(), false, "Wrong permissions");
    }

    @Override
    public String getLabel() {
        return getCheckAccessGranted() ? "Create Page" : "Cannot Create Page";
    }

}
