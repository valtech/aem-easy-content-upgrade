/*
 *  Copyright 2018 Valtech GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package de.valtech.aecu.core.serviceuser;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides the service resource resolver.
 *
 * @author Roland Gruber
 */
@Component(service = ServiceResourceResolverService.class)
public class ServiceResourceResolverService {

    private static final String SUBSERVICE_AECU = "aecu";
    private static final String SUBSERVICE_AECU_CONTENT_MIGRATION = "aecu-content-migrator";

    @Reference
    ResourceResolverFactory resolverFactory;

    /**
     * Returns a resource resolver of the AECU service user.
     *
     * @return service resource resolver
     * @throws LoginException error opening resource resolver
     */
    public ResourceResolver getServiceResourceResolver() throws LoginException {
        final Map<String, Object> authenticationInfo = new HashMap<>();
        authenticationInfo.put(ResourceResolverFactory.SUBSERVICE, SUBSERVICE_AECU);
        return resolverFactory.getServiceResourceResolver(authenticationInfo);
    }

    /**
     * Returns a resource resolver of the AECU content migrator user.
     *
     * @return service resource resolver
     * @throws LoginException error opening resource resolver
     */ // TODO: add /apps write rights!!!
    public ResourceResolver getContentMigratorResourceResolver() throws LoginException {
        final Map<String, Object> authenticationInfo = new HashMap<>();
        authenticationInfo.put(ResourceResolverFactory.SUBSERVICE, SUBSERVICE_AECU_CONTENT_MIGRATION);
        return resolverFactory.getServiceResourceResolver(authenticationInfo);
    }

}
