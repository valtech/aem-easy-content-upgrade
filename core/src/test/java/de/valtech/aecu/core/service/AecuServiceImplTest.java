/*
 * Copyright 2018 Valtech GmbH
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
package de.valtech.aecu.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests AecuServiceImpl
 *
 * @author Roland Gruber
 */
@RunWith(value = MockitoJUnitRunner.class)
public class AecuServiceImplTest {

    @InjectMocks
    @Spy
    private AecuServiceImpl service;

    @Mock
    private SlingSettingsService settingsService;

    @Mock
    private ResourceResolver resolver;

    @Before
    public void setup() {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        runModes.add("test");
        runModes.add("test2");
        runModes.add("test3");
        when(settingsService.getRunModes()).thenReturn(runModes);
    }

    @Test
    public void matchesRunmodes_noMode() {
        assertTrue(service.matchesRunmodes("name"));
    }

    @Test
    public void matchesRunmodes_nonMatching() {
        assertFalse(service.matchesRunmodes("name.publish"));
    }

    @Test
    public void matchesRunmodes_matching() {
        assertTrue(service.matchesRunmodes("name.author"));
        assertTrue(service.matchesRunmodes("name.test"));
    }

    @Test
    public void matchesRunmodes_matchingMulti() {
        assertTrue(service.matchesRunmodes("name.author.test"));
        assertTrue(service.matchesRunmodes("name.author.test;testNO"));
        assertTrue(service.matchesRunmodes("name.authorNO.test;test2.author"));
    }

    @Test
    public void matchesRunmodes_nonMatchingMulti() {
        assertFalse(service.matchesRunmodes("name.author.testNO"));
        assertFalse(service.matchesRunmodes("name.author.testNO;testNO"));
        assertFalse(service.matchesRunmodes("name.author.testNO;test2NO.author"));
    }

    @Test
    public void isValidScriptName_ok() {
        assertTrue(service.isValidScriptName("test.groovy"));
    }

    @Test
    public void isValidScriptName_invalidExtension() {
        assertFalse(service.isValidScriptName("test.txt"));
    }

    @Test
    public void isValidScriptName_fallback() {
        assertFalse(service.isValidScriptName("test.fallback.groovy"));
    }

    @Test
    public void getFallbackScript_Exists() {
        when(resolver.getResource("/path/to/script.fallback.groovy")).thenReturn(mock(Resource.class));

        assertEquals("/path/to/script.fallback.groovy", service.getFallbackScript(resolver, "/path/to/script.always.groovy"));
        assertEquals("/path/to/script.fallback.groovy", service.getFallbackScript(resolver, "/path/to/script.groovy"));
    }

    @Test
    public void getFallbackScript_NotExists() {
        assertNull(service.getFallbackScript(resolver, "/path/to/script.always.groovy"));
        assertNull(service.getFallbackScript(resolver, "/path/to/script.groovy"));
    }

    @Test
    public void getFallbackScript_Fallback() {
        verify(resolver, never()).getResource("/path/to/script.fallback.groovy");

        assertNull(service.getFallbackScript(resolver, "/path/to/script.fallback.groovy"));
    }

}
