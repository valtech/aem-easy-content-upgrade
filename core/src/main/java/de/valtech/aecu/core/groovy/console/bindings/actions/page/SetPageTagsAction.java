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
package de.valtech.aecu.core.groovy.console.bindings.actions.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Sets tags to the page of a given resource. This will overwrite existing tags.
 * 
 * @author Roland Gruber
 */
public class SetPageTagsAction implements Action {

    private BindingContext context;
    private String[] tags;

    /**
     * Constructor
     * 
     * @param context binding context
     * @param tags    tag names
     */
    public SetPageTagsAction(BindingContext context, String... tags) {
        this.context = context;
        this.tags = tags.clone();
    }

    @Override
    public String doAction(Resource resource) throws PersistenceException {
        Page page = context.getPageManager().getContainingPage(resource);
        if (page == null) {
            return "Unable to find a page for resource " + resource.getPath();
        }
        List<Tag> tagsToSet = getTagsToSet();
        String successMessage = "Set page tags on " + page.getPath() + ": " + getTagListAsString(tagsToSet);
        if (context.isDryRun()) {
            return successMessage;
        }
        TagManager tagManager = context.getTagManager();
        tagManager.setTags(page.getContentResource(), tagsToSet.toArray(new Tag[tagsToSet.size()]));
        return successMessage;
    }

    /**
     * Returns the tags that need to be added.
     * 
     * @return tags to add
     * @throws PersistenceException invalid tag found
     */
    private List<Tag> getTagsToSet() throws PersistenceException {
        TagManager tagManager = context.getTagManager();
        List<Tag> toSet = new ArrayList<>();
        for (String newTagName : tags) {
            Tag tag = tagManager.resolve(newTagName);
            if (tag == null) {
                throw new PersistenceException("Tag " + newTagName + " does not exist.\n");
            }
            toSet.add(tag);
        }
        return toSet;
    }

    private String getTagListAsString(List<Tag> tags) {
        List<String> tagIds = new ArrayList<>();
        for (Tag tag : tags) {
            tagIds.add(tag.getTagID());
        }
        return String.join(", ", tagIds);
    }

}
