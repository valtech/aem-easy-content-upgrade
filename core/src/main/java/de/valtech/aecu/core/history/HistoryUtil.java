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
package de.valtech.aecu.core.history;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.oak.spi.security.authorization.accesscontrol.AccessControlConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ResourceUtil.BatchResourceRemover;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.ExecutionState;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.api.service.HistoryEntry.RESULT;
import de.valtech.aecu.api.service.HistoryEntry.STATE;
import de.valtech.aecu.core.service.HistoryEntryImpl;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

/**
 * Reads and writes history entries.
 *
 * @author Roland Gruber
 */
@Component(service = HistoryUtil.class)
public class HistoryUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HistoryUtil.class);

    /**
     * Base node for history entries
     */
    public static final String HISTORY_BASE = "/var/aecu";

    public static final String NODE_FALLBACK = "fallback";

    public static final String ATTR_PATH = "path";
    protected static final String ATTR_RUN_OUTPUT = "runOutput";
    protected static final String ATTR_RUN_STATE = "runState";
    protected static final String ATTR_RUN_RESULT = "runResult";
    protected static final String ATTR_RUN_TIME = "runTime";
    protected static final String ATTR_RESULT = "result";
    protected static final String ATTR_STATE = "state";
    protected static final String ATTR_START = "start";
    protected static final String ATTR_END = "end";
    private static final String NAME_INDEX = "oak:index";
    // This size is limited by the LuceneDocumentMaker to be able to read the property and create the new index
    // The limit is 102400 but just to be in the safe size, is set to a bit lower number
    private static final int MAXIMUN_PROPERTY_SIZE = 100000;

    private Random random = new Random();

    /**
     * Starts a new history entry.
     *
     * @param resolver resource resolver
     * @return history entry
     * @throws AecuException error setting up entry
     */
    public HistoryEntry createHistoryEntry(ResourceResolver resolver) throws AecuException {
        HistoryEntryImpl history = new HistoryEntryImpl();
        Calendar start = new GregorianCalendar();
        String basePath = HISTORY_BASE + "/" + start.get(Calendar.YEAR) + "/" + (start.get(Calendar.MONTH) + 1) + "/"
                + start.get(Calendar.DAY_OF_MONTH);
        String nodeName = generateHistoryNodeName();
        String nodePath = basePath + "/" + nodeName;
        createPath(basePath, resolver, JcrResourceConstants.NT_SLING_ORDERED_FOLDER);
        createPath(nodePath, resolver, JcrConstants.NT_UNSTRUCTURED);
        Resource resource = resolver.getResource(nodePath);
        ModifiableValueMap values = resource.adaptTo(ModifiableValueMap.class);
        values.put(ATTR_START, start);
        values.put(ATTR_STATE, STATE.RUNNING.name());
        values.put(ATTR_RESULT, RESULT.UNKNOWN.name());
        history.setStart(start.getTime());
        history.setRepositoryPath(nodePath);
        history.setState(STATE.RUNNING);
        return history;
    }

    /**
     * Stores an execution run in existing history.
     *
     * @param history  history entry
     * @param result   script execution result
     * @param resolver resource resolver
     * @throws AecuException error inserting history entry
     */
    public void storeExecutionInHistory(HistoryEntry history, ExecutionResult result, ResourceResolver resolver)
            throws AecuException {
        String path = history.getRepositoryPath() + "/" + history.getSingleResults().size();
        saveExecutionResultInHistory(result, path, resolver);
    }

    /**
     * Finishes the history entry.
     *
     * @param history  open history entry
     * @param resolver resource resolver
     */
    public void finishHistoryEntry(HistoryEntry history, ResourceResolver resolver) {
        Resource resource = resolver.getResource(history.getRepositoryPath());
        ModifiableValueMap values = resource.adaptTo(ModifiableValueMap.class);
        Calendar end = new GregorianCalendar();
        values.put(ATTR_END, end);
        values.put(ATTR_STATE, STATE.FINISHED.name());
        values.put(ATTR_RESULT, history.getResult().name());
        ((HistoryEntryImpl) history).setEnd(end.getTime());
        ((HistoryEntryImpl) history).setState(STATE.FINISHED);
    }

    /**
     * Returns the last history entries. The search starts at the newest entry.
     *
     * @param startIndex start reading at this index (first is 0)
     * @param count      number of entries to read
     * @param resolver   resource resolver
     * @return history entries (newest first)
     */
    public List<HistoryEntry> getHistory(int startIndex, int count, ResourceResolver resolver) {
        List<HistoryEntry> entries = new ArrayList<>();
        if (count == 0) {
            return entries;
        }
        Resource base = resolver.getResource(HISTORY_BASE);
        Resource current = getLatestHistoryEntry(base);
        if (current == null) {
            return entries;
        }
        // skip up to start index
        for (int i = 0; i < startIndex; i++) {
            current = getPreviousHistoryEntry(current);
        }
        for (int i = 0; i < count; i++) {
            if (current == null) {
                break;
            }
            entries.add(readHistoryEntry(current));
            current = getPreviousHistoryEntry(current);
        }
        return entries;
    }

    /**
     * Returns the run before the given one.
     *
     * @param current current run
     * @return previous run
     */
    private Resource getPreviousHistoryEntry(Resource current) {
        // check if the parent has a sibling before the current node
        Resource previous = getPreviousSibling(current);
        if (previous != null) {
            return previous;
        }
        // go down till we find an earlier sibling
        Resource base = descendToPreviousSiblingInHistory(current.getParent());
        // go back up the folders
        return ascendToLastRun(base);
    }

    /**
     * Gos up the folders to last run.
     *
     * @param resource current node
     * @return last run
     */
    private Resource ascendToLastRun(Resource resource) {
        if (resource == null) {
            return null;
        }
        Resource last = getLastChild(resource);
        if (last == null) {
            // stop if there is no child at all
            return null;
        }
        ValueMap values = last.adaptTo(ValueMap.class);
        if (JcrResourceConstants.NT_SLING_ORDERED_FOLDER.equals(values.get(JcrConstants.JCR_PRIMARYTYPE, String.class))) {
            return ascendToLastRun(last);
        }
        return last;
    }

    /**
     * Descends in history till a previous sibling is found. Descending stops at history base level
     *
     * @param current current resource
     * @return previous sibling
     */
    private Resource descendToPreviousSiblingInHistory(Resource current) {
        if ((current == null) || HISTORY_BASE.equals(current.getPath())) {
            return null;
        }
        Resource previous = getPreviousSibling(current);
        if (previous != null) {
            return previous;
        }
        previous = descendToPreviousSiblingInHistory(current.getParent());
        return previous;
    }

    /**
     * Returns the previous sibling of the given node.
     *
     * @param resource current node
     * @return last sibling or null
     */
    private Resource getPreviousSibling(Resource resource) {
        Iterator<Resource> siblings = resource.getParent().listChildren();
        Resource previous = null;
        while (siblings.hasNext()) {
            Resource sibling = siblings.next();
            if (sibling.getName().equals(resource.getName())) {
                break;
            }
            if (!sibling.getName().equals(AccessControlConstants.REP_POLICY) && !sibling.getName().equals(NAME_INDEX)) {
                previous = sibling;
            }
        }
        return previous;
    }

    /**
     * Returns the latest history entry.
     *
     * @param base base resource
     * @return latest run resource
     */
    private Resource getLatestHistoryEntry(Resource base) {
        if (base == null) {
            return null;
        }
        return ascendToLastRun(base);
    }

    /**
     * Returns the last child of the given resource.
     *
     * @param resource resource
     * @return last child
     */
    private Resource getLastChild(Resource resource) {
        if (resource == null) {
            return null;
        }
        Resource last = null;
        Iterator<Resource> lastIterator = resource.listChildren();
        while (lastIterator.hasNext()) {
            Resource candidate = lastIterator.next();
            if (!AccessControlConstants.REP_POLICY.equals(candidate.getName()) && !NAME_INDEX.equals(candidate.getName())) {
                last = candidate;
            }
        }
        return last;
    }

    /**
     * Returns the base resource for a (fallback) script resource.
     * 
     * @param child child of base resource
     * @return base run resource
     */
    public Resource getHistoryEntryResource(Resource child) {
        Resource resource = child;
        ValueMap values = resource.adaptTo(ValueMap.class);
        // if we are in a script subnode then go down to base node of run
        if (values.containsKey(ATTR_RUN_STATE)) {
            resource = resource.getParent();
            values = resource.adaptTo(ValueMap.class);
            if (values.containsKey(ATTR_RUN_STATE)) {
                resource = resource.getParent();
            }
        }
        return resource;
    }

    /**
     * Reads a history entry from JCR.
     *
     * @param resource history resource
     * @return history entry
     */
    public HistoryEntry readHistoryEntry(Resource resource) {
        HistoryEntryImpl entry = new HistoryEntryImpl();
        entry.setRepositoryPath(resource.getPath());
        ValueMap values = resource.adaptTo(ValueMap.class);
        if (values.containsKey(ATTR_STATE)) {
            entry.setState(STATE.valueOf(values.get(ATTR_STATE, String.class)));
        }
        if (values.containsKey(ATTR_START)) {
            entry.setStart(values.get(ATTR_START, Calendar.class).getTime());
        }
        if (values.containsKey(ATTR_END)) {
            entry.setEnd(values.get(ATTR_END, Calendar.class).getTime());
        }
        Iterable<Resource> children = resource.getChildren();
        for (Resource child : children) {
            entry.addSingleResult(readHistorySingleResult(child));
        }
        return entry;
    }

    /**
     * Reads a single script run from history.
     *
     * @param resource resource
     * @return result
     */
    public ExecutionResult readHistorySingleResult(Resource resource) {
        ExecutionResult fallback = null;
        Resource fallbackResource = resource.getChild(NODE_FALLBACK);
        if (fallbackResource != null) {
            fallback = readHistorySingleResult(fallbackResource);
        }
        ValueMap values = resource.adaptTo(ValueMap.class);
        String output = values.get(ATTR_RUN_OUTPUT, "");
        String time = values.get(ATTR_RUN_TIME, "");
        ExecutionState state = ExecutionState.valueOf(values.get(ATTR_RUN_STATE, ExecutionState.FAILED.name()));
        String runResult = values.get(ATTR_RUN_RESULT, "");
        String path = values.get(ATTR_PATH, "");
        return new ExecutionResult(state, time, runResult, output, fallback, path);
    }

    private void saveExecutionResultInHistory(ExecutionResult result, String path, ResourceResolver resolver)
            throws AecuException {
        createPath(path, resolver, "nt:unstructured");
        Resource entry = resolver.getResource(path);
        ModifiableValueMap values = entry.adaptTo(ModifiableValueMap.class);
        values.put(ATTR_RUN_STATE, result.getState().name());
        values.put(ATTR_PATH, result.getPath());
        if (StringUtils.isNotBlank(result.getOutput())) {
            if (result.getOutput().getBytes(StandardCharsets.UTF_8).length < MAXIMUN_PROPERTY_SIZE) {
                values.put(ATTR_RUN_OUTPUT, result.getOutput());
            } else {
                try {
                    ValueFactory factory = resolver.adaptTo(Session.class).getValueFactory();
                    InputStream is = new ByteArrayInputStream(result.getOutput().getBytes());
                    Binary binary = factory.createBinary(is);
                    Value value = factory.createValue(binary);
                    values.put(ATTR_RUN_OUTPUT, value);
                } catch (RepositoryException e) {
                    LOG.error("Not able to save the output of the script as binary on the History node [{}]", entry.getPath());
                }
            }
        }
        if (StringUtils.isNotBlank(result.getResult())) {
            values.put(ATTR_RUN_RESULT, result.getResult());
        }
        if (StringUtils.isNotBlank(result.getTime())) {
            values.put(ATTR_RUN_TIME, result.getTime());
        }
        if (result.getFallbackResult() != null) {
            String fallbackPath = path + "/" + NODE_FALLBACK;
            saveExecutionResultInHistory(result.getFallbackResult(), fallbackPath, resolver);
        }
    }

    /**
     * Creates the folder at the given path if not yet existing.
     *
     * @param path        path
     * @param resolver    resource resolver
     * @param primaryType primary type
     * @throws AecuException error creating folder
     */
    protected void createPath(String path, ResourceResolver resolver, String primaryType) throws AecuException {
        Resource folder = resolver.getResource(path);
        if (folder == null) {
            String parent = path.substring(0, path.lastIndexOf('/'));
            String name = path.substring(path.lastIndexOf('/') + 1);
            if (resolver.getResource(parent) == null) {
                createPath(parent, resolver, primaryType);
            }
            Map<String, Object> properties = new HashMap<>();
            properties.put(JcrConstants.JCR_PRIMARYTYPE, primaryType);
            try {
                resolver.create(resolver.getResource(parent), name, properties);
            } catch (PersistenceException e) {
                throw new AecuException("Unable to create " + path, e);
            }
        }
    }

    /**
     * Generates the node name for a history entry.
     *
     * @return name
     */
    private String generateHistoryNodeName() {
        return System.currentTimeMillis() + "" + random.nextInt(100000);
    }

    /**
     * Purges the history by keeping only entries within the set number of days.
     *
     * @param resolver   resource resolver
     * @param daysToKeep number of days to keep
     * @throws PersistenceException error deleting node
     */
    public void purgeHistory(ResourceResolver resolver, int daysToKeep) throws PersistenceException {
        Resource base = resolver.getResource(HISTORY_BASE);
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -daysToKeep);
        LOG.debug("Starting purge with limit {}", calendar.getTime());
        deleteRecursive(base.listChildren(), calendar, new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH});
    }

    /**
     * Deletes the year resources that are too old.
     *
     * @param resources resources
     * @param calendar  time limit
     * @param fields    calendar fields
     * @throws PersistenceException error deleting node
     */
    private void deleteRecursive(Iterator<Resource> resources, Calendar calendar, int[] fields) throws PersistenceException {
        int currentField = fields[0];
        while (resources.hasNext()) {
            Resource resource = resources.next();
            String name = resource.getName();
            // skip extra nodes such as ACLs
            if (!StringUtils.isNumeric(name)) {
                LOG.debug("Skipping purge of other node: {}", resource.getPath());
                continue;
            }
            int nodeValue = Integer.parseInt(name);
            int limit = calendar.get(currentField);
            if (currentField == Calendar.MONTH) {
                // months start with 0 but are stored beginning with 1 in CRX
                limit++;
            }
            if (nodeValue > limit) {
                LOG.debug("Skipping purge of too young node: {}", resource.getPath());
            } else if (nodeValue == limit) {
                LOG.debug("Skipping purge of too young node: {}", resource.getPath());
                // check next level
                if (fields.length == 1) {
                    return;
                }
                int[] fieldsNew = new int[fields.length - 1];
                System.arraycopy(fields, 1, fieldsNew, 0, fieldsNew.length);
                deleteRecursive(resource.listChildren(), calendar, fieldsNew);
            } else {
                LOG.debug("Purging node: {}", resource.getPath());
                BatchResourceRemover remover = ResourceUtil.getBatchResourceRemover(1000);
                remover.delete(resource);
            }
        }
    }

    /**
     * Self test of history. Checks if the history node exists.
     * 
     * @param resolver resource resolver
     * @throws AecuException check failed
     */
    public void selfCheck(ResourceResolver resolver) throws AecuException {
        Resource base = resolver.getResource(HISTORY_BASE);
        if (base == null) {
            throw new AecuException(HISTORY_BASE + " does not exist or is not accessible.");
        }
    }

}
