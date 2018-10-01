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
package de.valtech.aecu.core.serviceuser;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests ServiceResourceResolverService
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceResourceResolverServiceTest {

    @Mock
    private ResourceResolverFactory resolverFactory;

    @Mock
    private ResourceResolver resolver;

    @InjectMocks
    private ServiceResourceResolverService service = new ServiceResourceResolverService();

    @Test
    public void getServiceResourceResolver() throws LoginException {
        when(resolverFactory.getServiceResourceResolver(Mockito.any())).thenReturn(resolver);

        ResourceResolver serviceResolver = service.getServiceResourceResolver();

        assertEquals(resolver, serviceResolver);
    }

    @Test
    public void getContentMigratorResourceResolver() throws LoginException {
        when(resolverFactory.getServiceResourceResolver(Mockito.any())).thenReturn(resolver);

        ResourceResolver serviceResolver = service.getContentMigratorResourceResolver();

        assertEquals(resolver, serviceResolver);
    }

}
