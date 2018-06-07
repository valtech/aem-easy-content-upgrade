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
package de.valtech.aecu.core.groovy.console.bindings;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.query.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Groovy Console Bindings: Simple Content Update
 * @author Roxana Muresan
 */
public class SimpleContentUpdate {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleContentUpdate.class);

    private ResourceResolver resourceResolver;// TODO check if a system user resource resolver is needed here!

    private List<Resource> resourceList = new ArrayList<>();// TODO with stream?


    public SimpleContentUpdate(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;

    }

    /** content filter methods **/
    public SimpleContentUpdate forResources(String[] paths) {
        if (paths != null && paths.length > 0) {
            List<Resource> resources = new ArrayList<>();
            for (String path : paths) {
                if (path != null) {
                    CollectionUtils.addIgnoreNull(resources, resourceResolver.getResource(path));
                }
            }
            CollectionUtils.addAll(this.resourceList, resources.iterator());
        }
        return this;
    }

    public SimpleContentUpdate forChildResourcesOf(String path) {
        if (path != null) {
            String queryString = "SELECT * FROM [nt:base] AS s WHERE ISCHILDNODE(s,'" + path + "')";
            LOG.debug("Running query: " + queryString);
            CollectionUtils.addAll(this.resourceList, resourceResolver.findResources(queryString, Query.JCR_SQL2));
        }
        return this;
    }

    public SimpleContentUpdate forDescendantResourcesOf(String path) {
        if (path != null) {
            String queryString = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE(s,'" + path + "')";
            LOG.debug("Running query: " + queryString);
            CollectionUtils.addAll(this.resourceList, resourceResolver.findResources(queryString, Query.JCR_SQL2));
        }
        return this;
    }

    public SimpleContentUpdate forChildResourcesOfWithProperties(String path, Map<String, Object> conditionProperties) {
        if (path != null && conditionProperties != null) {
            String queryString = "SELECT * FROM [nt:base] AS s WHERE ISCHILDNODE(s,'" + path + "')" + getQueryStringForProperties(conditionProperties);
            LOG.debug("Running query: " + queryString);
            CollectionUtils.addAll(this.resourceList, resourceResolver.findResources(queryString, Query.JCR_SQL2));
        }
        return this;
    }

    public SimpleContentUpdate forDescendantResourcesOfWithProperties(String path, Map<String, Object> conditionProperties) {
        if (path != null && conditionProperties != null) {
            String queryString = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE(s,'" + path + "')" + getQueryStringForProperties(conditionProperties);
            LOG.debug("Running query: " + queryString);
            CollectionUtils.addAll(this.resourceList, resourceResolver.findResources(queryString, Query.JCR_SQL2));
        }
        return this;
    }

    public SimpleContentUpdate printFoundResources() {
        resourceList.forEach(s -> LOG.info("Found " + s.getPath()));
        return this;
    }

    private String getQueryStringForProperties(Map<String, Object> properties) {
        String queryString = "";
        for (String key: properties.keySet()) {
            queryString += " AND s.[" + key + "] = '" + properties.get(key).toString() + "'";
        }
        return queryString;
    }

    /** content edit methods **/
    public void remove() {
        try {
            for (Resource current : resourceList) {
                LOG.debug("Removing resource " + current.getPath());
                resourceResolver.delete(current);
            }
            resourceResolver.commit();
        } catch (PersistenceException e) {
            LOG.error("Failed to commit changes.", e);
        }
    }

    public SimpleContentUpdate setProperty(String name, Object value) {
        if (name != null) {
            try {
                for (Resource current : resourceList) {
                    LOG.debug("Setting property " + name + "=" + value + " for resource " + current.getPath());
                    ModifiableValueMap properties = current.adaptTo(ModifiableValueMap.class);
                    properties.put(name, value);
                }
                resourceResolver.commit();
            } catch (PersistenceException e) {
                LOG.error("Failed to commit changes.", e);
            }
        }
        return this;
    }

    public SimpleContentUpdate removeProperty(String name) {
        if (name != null) {
            try {
                for (Resource current : resourceList) {
                    LOG.debug("Removing property " + name + " for resource " + current.getPath());
                    ModifiableValueMap properties = current.adaptTo(ModifiableValueMap.class);
                    properties.remove(name);
                }
                resourceResolver.commit();
            } catch (PersistenceException e) {
                LOG.error("Failed to commit changes.", e);
            }
        }
        return this;
    }

    public SimpleContentUpdate renameProperty(String oldName, String newName) {
        if (oldName != null && newName != null) {
            try {
                for (Resource current : resourceList) {
                    LOG.debug("Renaming property " + oldName + " to " + newName + " for resource " + current.getPath());
                    ModifiableValueMap properties = current.adaptTo(ModifiableValueMap.class);
                    Object value = properties.remove(oldName);
                    properties.put(newName, value);
                }
                resourceResolver.commit();
            } catch (PersistenceException e) {
                LOG.error("Failed to commit changes.", e);
            }
        }
        return this;
    }

}
