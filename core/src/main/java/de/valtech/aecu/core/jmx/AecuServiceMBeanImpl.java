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
package de.valtech.aecu.core.jmx;

import java.util.List;

import javax.management.NotCompliantMBeanException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;
import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.core.serviceuser.ServiceResourceResolverService;

@Component(service = {AecuServiceMBean.class}, immediate = true,
        property = {"jmx.objectname=de.valtech:type=AECU", "pattern=/.*"})
public class AecuServiceMBeanImpl extends AnnotatedStandardMBean implements AecuServiceMBean {

    @Reference
    private AecuService aecuService;

    @Reference
    private ServiceResourceResolverService serviceResourceResolver;

    /**
     * Constructor
     *
     * @throws NotCompliantMBeanException error setting up mbean
     */
    public AecuServiceMBeanImpl() throws NotCompliantMBeanException {
        super(AecuServiceMBean.class);
    }

    @Override
    public String getVersion() {
        return aecuService.getVersion();
    }

    @Override
    public List<String> getFiles(String path) throws AecuException {
        return aecuService.getFiles(path);
    }

    @Override
    public String execute(String path) throws AecuException {
        HistoryEntry history = aecuService.createHistoryEntry();
        List<String> files = aecuService.getFiles(path);
        StringBuilder result = new StringBuilder("Found " + files.size() + " files to execute\n\n");
        for (String file : files) {
            result.append(file + "\n");
            ExecutionResult singleResult = aecuService.execute(file);
            aecuService.storeExecutionInHistory(history, singleResult);
            result.append(singleResult.toString());
            result.append("\n\n");
        }
        aecuService.finishHistoryEntry(history);
        return result.toString();
    }

    @Override
    public String executeWithHistory(String path) throws AecuException {
        HistoryEntry history = aecuService.executeWithInstallHookHistory(path);
        return history.toString();
    }

    @Override
    public String getHistory(int start, int count) throws AecuException {
        List<HistoryEntry> entries = aecuService.getHistory(start, count);
        StringBuilder output = new StringBuilder();
        for (HistoryEntry entry : entries) {
            output.append(entry.toString() + "\n\n");
        }
        return output.toString();
    }

    @Override
    public String executeAllScripts(String data) throws AecuException {
        HistoryEntry historyEntry = aecuService.executeAllScripts(data);
        return historyEntry.toString();
    }

}
