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
package de.valtech.aecu.core.healthcheck;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.hc.api.Result;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.valtech.aecu.core.history.HistoryUtil;
import de.valtech.aecu.core.serviceuser.ServiceResourceResolverService;

/**
 * Tests SelfCheckHealthCheck
 * 
 * @author Roland Gruber
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SelfCheckHealthCheckTest {

    @InjectMocks
    private SelfCheckHealthCheck check = new SelfCheckHealthCheck();

    @Mock
    private ServiceResourceResolverService resolverService;

    @Spy
    private HistoryUtil historyUtil;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Resource resource;

    @Before
    public void setup() throws LoginException {
        when(resolverService.getServiceResourceResolver()).thenReturn(resolver);
        when(resolverService.getContentMigratorResourceResolver()).thenReturn(resolver);
        when(resolver.getResource("/var/aecu-installhook")).thenReturn(resource);
        when(resolver.getResource("/var/aecu")).thenReturn(resource);
    }

    @Test
    public void checkServiceResolver_ok() {
        Result result = check.execute();

        assertEquals(Result.Status.OK, result.getStatus());
    }

    @Test
    public void checkServiceResolver_failService() throws LoginException {
        when(resolverService.getServiceResourceResolver()).thenReturn(null);

        Result result = check.execute();

        assertEquals(Result.Status.CRITICAL, result.getStatus());
    }

    @Test
    public void checkServiceResolver_failServiceException() throws LoginException {
        when(resolverService.getServiceResourceResolver()).thenThrow(LoginException.class);

        Result result = check.execute();

        assertEquals(Result.Status.CRITICAL, result.getStatus());
    }

    @Test
    public void checkServiceResolver_failMigrationService() throws LoginException {
        when(resolverService.getContentMigratorResourceResolver()).thenReturn(null);

        Result result = check.execute();

        assertEquals(Result.Status.CRITICAL, result.getStatus());
    }

    @Test
    public void checkServiceResolver_failMigrationServiceException() throws LoginException {
        when(resolverService.getContentMigratorResourceResolver()).thenThrow(LoginException.class);

        Result result = check.execute();

        assertEquals(Result.Status.CRITICAL, result.getStatus());
    }

    @Test
    public void checkServiceResolver_failHistory() throws LoginException {
        when(resolver.getResource("/var/aecu")).thenReturn(null);

        Result result = check.execute();

        assertEquals(Result.Status.CRITICAL, result.getStatus());
    }

    @Test
    public void checkServiceResolver_failHookHistory() throws LoginException {
        when(resolver.getResource("/var/aecu-installhook")).thenReturn(null);

        Result result = check.execute();

        assertEquals(Result.Status.CRITICAL, result.getStatus());
    }

}
