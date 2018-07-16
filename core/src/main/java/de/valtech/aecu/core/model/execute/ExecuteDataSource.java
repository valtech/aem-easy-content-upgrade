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
@Model(adaptables = SlingHttpServletRequest.class)
public class ExecuteDataSource {

    private static final String ITEM_TYPE = "valtech/aecu/tools/execute/dataitem";
    private static final String ALLOWED_PATH = "/etc/groovyconsole/scripts";

    @SlingObject
    SlingHttpServletRequest request;

    @Inject
    private AecuService aecuService;

    @PostConstruct
    public void setup() throws AecuException {

        String path = request.getParameter("searchPath");
        List<Resource> entries = new ArrayList<>();

        if (path != null && StringUtils.isNotEmpty(path) && path.startsWith(ALLOWED_PATH)) {
            List<String> allowedScripts = aecuService.getFiles(path);
            ResourceResolver resourceResolver = request.getResourceResolver();
            for (String scriptPath : allowedScripts) {
                entries.add(new ValueMapResource(resourceResolver, scriptPath, ITEM_TYPE, null));
            }
        }

        DataSource ds = new SimpleDataSource(entries.iterator());
        request.setAttribute(DataSource.class.getName(), ds);
    }

}
