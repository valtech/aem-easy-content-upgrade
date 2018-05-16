/*
 *  Copyright 2018 Valtech GmbH
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>
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
@RunWith(value=MockitoJUnitRunner.class)
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
