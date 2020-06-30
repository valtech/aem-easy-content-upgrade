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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.valtech.aecu.core.security.AccessValidationService;

/**
 * Tests AccessValidatorModel
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class AccessValidatorModelTest {

    @Mock
    private AccessValidationService accessValidatorService;

    @Mock
    private SlingHttpServletRequest request;

    @InjectMocks
    private AccessValidatorModel model;

    @Before
    public void setup() {
        when(accessValidatorService.canExecute(request)).thenReturn(true);
        when(accessValidatorService.canReadHistory(request)).thenReturn(false);
    }

    @Test
    public void isAbleToReadHistory() {
        assertFalse(model.isAbleToReadHistory());
    }

    @Test
    public void isAbleToExecute() {
        assertTrue(model.isAbleToExecute());
    }

}
