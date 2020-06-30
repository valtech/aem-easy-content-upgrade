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
package de.valtech.aecu.core.model.accessvalidation;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import de.valtech.aecu.core.security.AccessValidationService;

/**
 * Model for access validation.
 *
 * @author Roland Gruber
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class AccessValidatorModel {

    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private AccessValidationService accessValidationServce;

    /**
     * Returns if the current user may read the history.
     * 
     * @return can read
     */
    public boolean isAbleToReadHistory() {
        return accessValidationServce.canReadHistory(request);
    }

    /**
     * Returns if the current user may execute scripts.
     * 
     * @return can execute
     */
    public boolean isAbleToExecute() {
        return accessValidationServce.canExecute(request);
    }

}
