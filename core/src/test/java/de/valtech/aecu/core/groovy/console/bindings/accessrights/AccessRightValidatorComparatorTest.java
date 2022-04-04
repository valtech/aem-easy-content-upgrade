/*
 * Copyright 2019 - 2022 Valtech GmbH
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.valtech.aecu.api.groovy.console.bindings.accessrights.AccessRightValidator;

/**
 * Tests AccessRightValidatorComparator
 * 
 * @author Roland Gruber
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AccessRightValidatorComparatorTest {

    private static final String PATH2 = "path2";

    private static final String PATH1 = "path1";

    private static final String LABEL2 = "label2";

    private static final String LABEL1 = "label1";

    private static final String GROUP2 = "group2";

    private static final String GROUP1 = "group1";

    @Mock
    private AccessRightValidator validator1;

    @Mock
    private AccessRightValidator validator2;

    @Mock
    private Resource resource1;

    @Mock
    private Resource resource2;

    private AccessRightValidatorComparator comp = new AccessRightValidatorComparator();

    @BeforeEach
    public void setup() {
        when(validator1.getResource()).thenReturn(resource1);
        when(validator2.getResource()).thenReturn(resource2);
        when(resource1.getPath()).thenReturn(PATH1);
        when(resource2.getPath()).thenReturn(PATH2);
        when(validator1.getGroupId()).thenReturn(GROUP1);
        when(validator2.getGroupId()).thenReturn(GROUP2);
        when(validator1.getLabel()).thenReturn(LABEL1);
        when(validator2.getLabel()).thenReturn(LABEL2);
    }

    @Test
    public void compare_differentGroupIds() {
        assertTrue(comp.compare(validator1, validator2) < 0);
        assertTrue(comp.compare(validator2, validator1) > 0);
    }

    @Test
    public void compare_sameGroupIdButdifferentPaths() {
        when(validator2.getGroupId()).thenReturn(GROUP1);

        assertTrue(comp.compare(validator1, validator2) < 0);
        assertTrue(comp.compare(validator2, validator1) > 0);
    }

    @Test
    public void compare_sameGroupIdAndPathButdifferentLabel() {
        when(validator2.getGroupId()).thenReturn(GROUP1);
        when(resource2.getPath()).thenReturn(PATH1);

        assertTrue(comp.compare(validator1, validator2) < 0);
        assertTrue(comp.compare(validator2, validator1) > 0);
    }

    @Test
    public void compare_allSame() {
        when(validator2.getGroupId()).thenReturn(GROUP1);
        when(resource2.getPath()).thenReturn(PATH1);
        when(validator2.getLabel()).thenReturn(LABEL1);

        assertTrue(comp.compare(validator1, validator2) == 0);
    }

}
