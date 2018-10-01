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
package de.valtech.aecu.core.maintenance;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.consumer.JobExecutionContext;
import org.apache.sling.event.jobs.consumer.JobExecutionContext.ResultBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.valtech.aecu.core.serviceuser.ServiceResourceResolverService;

/**
 * Tests PurgeHistoryTask
 *
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class PurgeHistoryTaskTest {

    @Mock
    private PurgeHistoryConfiguration config;

    @Mock
    private ServiceResourceResolverService resolverService;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Resource baseResource;

    @Mock
    private ResultBuilder builder;

    @Mock
    private JobExecutionContext context;

    @InjectMocks
    private PurgeHistoryTask task = new PurgeHistoryTask();

    @Before
    public void setup() throws LoginException {
        task.activate(config);
        when(resolverService.getServiceResourceResolver()).thenReturn(resolver);
        when(resolver.getResource("/var/aecu")).thenReturn(baseResource);
        when(baseResource.listChildren()).thenReturn(Collections.emptyIterator());
        when(context.result()).thenReturn(builder);
        when(builder.message(Mockito.anyString())).thenReturn(builder);
    }

    @Test
    public void process() throws PersistenceException {
        task.process(null, context);

        verify(resolver, times(1)).commit();
    }

    @Test
    public void process_loginException() throws PersistenceException, LoginException {
        when(resolverService.getServiceResourceResolver()).thenThrow(LoginException.class);

        task.process(null, context);

        verify(resolver, never()).commit();
    }

}
