/*
 * Copyright 2018 - 2022 Valtech GmbH
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

package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Tests MoveResourceToPathRegex
 *
 * @author Roxana Muresan
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MoveResourceToPathRegexTest {

    @Mock
    private ResourceResolver resourceResolver;
    @Mock
    private Resource resource;
    @Mock
    private ValueMap valueMap;

    @BeforeEach
    public void setup() {
        when(resource.getPath()).thenReturn("/content/project/something");
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenReturn(JcrConstants.NT_FOLDER);
    }

    @Test
    public void testDoAction_noMatch() {
        MoveResourceToPathRegex underTest = createObjectUnderTest("/content/bla/(\\w+)/(\\w+)", "/content/abc/$1");
        mockWithValues("/content/somewhere/else", "/doesnt/matter", true);

        try {
            String returnString = underTest.doAction(resource);
            assertTrue(returnString.startsWith("INFO"));

            verify(resourceResolver, never()).getResource(anyString());
            verify(resourceResolver, never()).move(anyString(), anyString());

        } catch (PersistenceException pe) {
            fail();
        }
    }

    @Test
    public void testDoAction_match_invalidDestination() {
        MoveResourceToPathRegex underTest = createObjectUnderTest("/content/(\\w+)/mmm/(\\w+)/(\\w+)", "/content/ooo/$1/$2/i");
        mockWithValues("/content/en/mmm/sub/resource", "/content/ooo/en/sub/i", false);

        try {
            String returnString = underTest.doAction(resource);
            assertTrue(returnString.startsWith("WARN"));

            verify(resourceResolver, times(1)).getResource(eq("/content/ooo/en/sub/i"));
            verify(resourceResolver, never()).move(anyString(), anyString());

        } catch (PersistenceException pe) {
            fail();
        }
    }

    @Test
    public void testDoAction_match_validDestination() {
        MoveResourceToPathRegex underTest = createObjectUnderTest("/content/(\\w+)/mmm/(\\w+)/(\\w+)", "/content/ooo/$1/$2/i");
        String resourcePath = "/content/en/mmm/sub/resource";
        String targetPath = "/content/ooo/en/sub/i";
        mockWithValues(resourcePath, targetPath, true);

        try {
            String returnString = underTest.doAction(resource);
            assertTrue(returnString.startsWith("Moved"));

            verify(resourceResolver, times(1)).getResource(eq(targetPath));
            verify(resourceResolver, times(1)).move(resourcePath, targetPath);

        } catch (PersistenceException pe) {
            fail();
        }
    }

    private MoveResourceToPathRegex createObjectUnderTest(String matchPattern, String replaceExpr) {
        BindingContext context = new BindingContext(resourceResolver);
        return new MoveResourceToPathRegex(matchPattern, replaceExpr, context);
    }

    private void mockWithValues(String resourcePath, String targetPath, boolean destinationExists) {
        when(resource.getPath()).thenReturn(resourcePath);
        Resource destinationResource = destinationExists ? mock(Resource.class) : null;
        when(resourceResolver.getResource(eq(targetPath))).thenReturn(destinationResource);
    }
}
