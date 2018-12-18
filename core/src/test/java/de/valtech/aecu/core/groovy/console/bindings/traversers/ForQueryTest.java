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
package de.valtech.aecu.core.groovy.console.bindings.traversers;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Tests ForQuery
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class ForQueryTest {

    private static final String QUERY = "query";

    private static final String TYPE = "type";

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Resource resource;

    @Mock
    private Action action;

    private BindingContext context;
    private StringBuffer output;
    List<Action> actions;


    @Before
    public void setup() {
        actions = Arrays.asList(action);
        context = new BindingContext(resolver);
        output = new StringBuffer();
        when(resource.getResourceResolver()).thenReturn(resolver);
        when(resolver.findResources(QUERY, TYPE)).thenReturn(Arrays.asList(resource).iterator());
    }

    @Test
    public void traverse() throws PersistenceException, AecuException {
        ForQuery traverser = new ForQuery(QUERY, TYPE);

        traverser.traverse(context, null, actions, output, false);

        verify(action, times(1)).doAction(resource);
        verify(resolver, never()).commit();
    }

    @Test
    public void traverse_many() throws PersistenceException, AecuException {
        List<Resource> results = new ArrayList<>();
        for (int i = 0; i < 1500; i++) {
            results.add(resource);
        }
        when(resolver.findResources(QUERY, TYPE)).thenReturn(results.iterator());
        ForQuery traverser = new ForQuery(QUERY, TYPE);

        traverser.traverse(context, null, actions, output, false);

        verify(action, times(1500)).doAction(resource);
        verify(resolver, times(1)).commit();
    }

    @Test
    public void traverse_dry() throws PersistenceException, AecuException {
        ForQuery traverser = new ForQuery(QUERY, TYPE);

        traverser.traverse(context, null, actions, output, true);

        verify(action, times(1)).doAction(resource);
        verify(resolver, never()).commit();
    }

}
