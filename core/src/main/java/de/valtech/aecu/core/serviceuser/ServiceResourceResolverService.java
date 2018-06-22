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
@Component(service=ServiceResourceResolverService.class)
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
     */
    public ResourceResolver getContentMigratorResourceResolver() throws LoginException {
        final Map<String, Object> authenticationInfo = new HashMap<>();
        authenticationInfo.put(ResourceResolverFactory.SUBSERVICE, SUBSERVICE_AECU_CONTENT_MIGRATION);
        return resolverFactory.getServiceResourceResolver(authenticationInfo);
    }

}
