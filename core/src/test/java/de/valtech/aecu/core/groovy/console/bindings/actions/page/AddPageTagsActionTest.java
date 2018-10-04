package de.valtech.aecu.core.groovy.console.bindings.actions.page;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Tests AddPageTagsAction
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class AddPageTagsActionTest {

    private static final String TAG1 = "tag1";
    private static final String TAG2 = "tag2";

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

    private AddPageTagsAction action;

    @Before
    public void setup() {
        when(context.getTagManager()).thenReturn(tagManager);
        when(context.getPageManager()).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(page);
        action = new AddPageTagsAction(context, TAG1, TAG2);
        when(page.getTags()).thenReturn(new Tag[] {tag1});
        when(tagManager.resolve(TAG1)).thenReturn(tag1);
        when(tagManager.resolve(TAG2)).thenReturn(tag2);
        when(tag1.getPath()).thenReturn(TAG1);
        when(tag2.getPath()).thenReturn(TAG2);
        when(page.getContentResource()).thenReturn(resource);
    }

    @Test(expected = PersistenceException.class)
    public void doAction_invalidTag() throws PersistenceException {
        action = new AddPageTagsAction(context, "invalid");

        action.doAction(resource);
    }

    @Test
    public void doAction() throws PersistenceException {
        action.doAction(resource);

        verify(tagManager, times(1)).setTags(resource, new Tag[] {tag2, tag1});
    }

}
