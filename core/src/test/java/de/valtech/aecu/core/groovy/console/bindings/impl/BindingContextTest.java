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
package de.valtech.aecu.core.groovy.console.bindings.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.day.cq.replication.Replicator;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.PageManager;

/**
 * Test for BindingContext
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BindingContextTest {

    @Mock
    private TagManager tagManager;

    @Mock
    private PageManager pageManager;

    @Mock
    private Replicator replicator;

    @Mock
    private ResourceResolver resolver;

    private BindingContext context;

    @BeforeEach
    public void setup() {
        when(resolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(resolver.adaptTo(TagManager.class)).thenReturn(tagManager);
        context = new BindingContext(resolver);
    }

    @Test
    public void getResolver() {
        assertEquals(resolver, context.getResolver());
    }

    @Test
    public void getPageManager() {
        assertEquals(pageManager, context.getPageManager());
    }

    @Test
    public void getTagManager() {
        assertEquals(tagManager, context.getTagManager());
    }

    @Test
    public void isDryRun() {
        assertTrue(context.isDryRun());
    }

    @Test
    public void setDryRun() {
        context.setDryRun(false);

        assertFalse(context.isDryRun());
    }

}
