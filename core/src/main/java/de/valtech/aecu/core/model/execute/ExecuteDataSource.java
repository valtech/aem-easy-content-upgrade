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
package de.valtech.aecu.core.model.execute;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.adobe.granite.ui.components.ds.ValueMapResource;
import de.valtech.aecu.service.AecuException;
import de.valtech.aecu.service.AecuService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;


/**
 * Datasource model for execute page.
 *
 * @author Bryan Chavez
 */
@Model(adaptables=SlingHttpServletRequest.class)
public class ExecuteDataSource {

    private static final String ITEM_TYPE = "valtech/aecu/tools/execute/dataitem";
    private static final String ALLOWED_PATH = "/etc/groovyconsole/scripts";

    @SlingObject
    SlingHttpServletRequest request;

    @Inject
    private AecuService aecuService;

    @PostConstruct
    public void setup() throws AecuException{

        String path = request.getParameter("searchPath");
        List<Resource> entries = new ArrayList<>();

        if(path!=null && StringUtils.isNotEmpty(path) && path.startsWith(ALLOWED_PATH)){
            List<String> allowedScripts = aecuService.getFiles(path);
            ResourceResolver resourceResolver = request.getResourceResolver();
            for(String scriptPath : allowedScripts){
                entries.add(new ValueMapResource(resourceResolver, scriptPath, ITEM_TYPE, null));
            }
        }

        DataSource ds = new SimpleDataSource(entries.iterator());
        request.setAttribute(DataSource.class.getName(), ds);
    }

}
