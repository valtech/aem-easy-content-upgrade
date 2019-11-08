package de.valtech.aecu.core.groovy.console.bindings.accessrights;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.valtech.aecu.api.groovy.console.bindings.accessrights.AccessRightValidator;

/**
 * Tests AccessRightValidatorComparator
 * 
 * @author Roland Gruber
 *
 */
@RunWith(MockitoJUnitRunner.class)
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

    @Before
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
