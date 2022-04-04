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
package de.valtech.aecu.core.groovy.console.bindings.actions.page;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Tests RemovePageTagsAction
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RemovePageTagsActionTest {

    private static final String TAG1 = "tag1";
    private static final String TAG2 = "tag2";
    private static final String TAG3 = "tag3";

    @Mock
    private TagManager tagManager;

    @Mock
    private PageManager pageManager;

    @Mock
    private BindingContext context;

    @Mock
    private Resource resource;

    @Mock
    private Page page;

    @Mock
    private Tag tag1;

    @Mock
    private Tag tag2;

    @Mock
    private Tag tag3;

    private RemovePageTagsAction action;

    @BeforeEach
    public void setup() {
        when(context.getTagManager()).thenReturn(tagManager);
        when(context.getPageManager()).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(page);
        action = new RemovePageTagsAction(context, TAG1, TAG2);
        when(page.getTags()).thenReturn(new Tag[] {tag2, tag3});
        when(tagManager.resolve(TAG1)).thenReturn(tag1);
        when(tagManager.resolve(TAG2)).thenReturn(tag2);
        when(tag1.getPath()).thenReturn(TAG1);
        when(tag2.getPath()).thenReturn(TAG2);
        when(tag3.getPath()).thenReturn(TAG3);
        when(page.getContentResource()).thenReturn(resource);
    }

    @Test
    public void doAction_invalidTag() throws PersistenceException {
        action = new RemovePageTagsAction(context, "invalid");

        assertThrows(PersistenceException.class, () -> action.doAction(resource));
    }

    @Test
    public void doAction() throws PersistenceException {
        action.doAction(resource);

        verify(tagManager, times(1)).setTags(resource, new Tag[] {tag3});
    }

}
