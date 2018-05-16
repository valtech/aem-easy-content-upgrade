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
package de.valtech.aecu.core.history;

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
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import de.valtech.aecu.core.service.HistoryEntryImpl;
import de.valtech.aecu.service.AecuException;
import de.valtech.aecu.service.ExecutionResult;
import de.valtech.aecu.service.HistoryEntry;
import de.valtech.aecu.service.HistoryEntry.RESULT;
import de.valtech.aecu.service.HistoryEntry.STATE;

/**
 * Reads and writes history entries.
 * 
 * @author Roland Gruber
 */
public class HistoryUtil {

    private static final String HISTORY_BASE = "/var/aecu";

    private static final String NODE_FALLBACK = "fallback";

    private static final String ATTR_RUN_OUTPUT = "runOutput";

    private static final String ATTR_RUN_SUCCESS = "runSuccess";

    private static final String ATTR_RUN_RESULT = "runResult";

    private static final String ATTR_RUN_TIME = "runTime";

    private static final String ATTR_RESULT = "result";

    private static final String ATTR_STATE = "state";

    private static final String ATTR_START = "start";

    private static final String ATTR_END = "end";

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
        String basePath = HISTORY_BASE + "/" + start.get(Calendar.YEAR) + "/" + (start.get(Calendar.MONTH) + 1) + "/" + start.get(Calendar.DAY_OF_MONTH);
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
     * @param history history entry
     * @param result script execution result
     * @param resolver resource resolver
     * @throws AecuException error inserting history entry
     */
    public void storeExecutionInHistory(HistoryEntry history, ExecutionResult result, ResourceResolver resolver) throws AecuException {
        String path = history.getRepositoryPath() + "/" + history.getSingleResults().size();
        saveExecutionResultInHistory(result, path, resolver);
    }

    /**
     * Finishes the history entry.
     * 
     * @param history open history entry
     * @param resolver resource resolver
     * @return history entry
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
     * @param count number of entries to read
     * @param resolver resource resolver
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
        ValueMap values = last.adaptTo(ValueMap.class);
        if (JcrResourceConstants.NT_SLING_ORDERED_FOLDER.equals(values.get(JcrConstants.JCR_PRIMARYTYPE, String.class))) {
            return ascendToLastRun(last);
        }
        return last;
    }

    /**
     * Descends in history till a previous sibling is found.
     * Descending stops at history base level
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
            if (!sibling.getName().equals(AccessControlConstants.REP_POLICY)) {
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
            last = lastIterator.next();
        }
        return last;
    }
    
    /**
     * Reads a history entry from JCR.
     * 
     * @param resource history resource
     * @return history entry
     */
    private HistoryEntry readHistoryEntry(Resource resource) {
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
            entry.getSingleResults().add(readHistorySingleResult(child));
        }
        return entry;
    }

    /**
     * Reads a single script run from history.
     * 
     * @param resource resource
     * @return result
     */
    private ExecutionResult readHistorySingleResult(Resource resource) {
        ExecutionResult fallback = null;
        Resource fallbackResource = resource.getChild(NODE_FALLBACK);
        if (fallbackResource != null) {
            fallback = readHistorySingleResult(fallbackResource);
        }
        ValueMap values = resource.adaptTo(ValueMap.class);
        String output = values.get(ATTR_RUN_OUTPUT, "");
        String time = values.get(ATTR_RUN_TIME, "");
        Boolean success = values.get(ATTR_RUN_SUCCESS, Boolean.FALSE);
        String runResult = values.get(ATTR_RUN_RESULT, "");
        ExecutionResult result = new ExecutionResult(success, time, runResult, output, fallback);
        return result;
    }

    private void saveExecutionResultInHistory(ExecutionResult result, String path, ResourceResolver resolver) throws AecuException {
        createPath(path, resolver, "nt:unstructured");
        Resource entry = resolver.getResource(path);
        ModifiableValueMap values = entry.adaptTo(ModifiableValueMap.class);
        values.put(ATTR_RUN_SUCCESS, result.isSuccess());
        if (StringUtils.isNotBlank(result.getOutput())) {
            values.put(ATTR_RUN_OUTPUT, result.getOutput());
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
     * @param path path
     * @param resolver resource resolver
     * @param primaryType primary type
     * @throws AecuException error creating folder
     */
    protected void createPath(String path, ResourceResolver resolver, String primaryType) throws AecuException {
        Resource folder = resolver.getResource(path);
        if (folder == null) {
            String parent = path.substring(0, path.lastIndexOf("/"));
            String name = path.substring(path.lastIndexOf("/") + 1);
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
        Random random = new Random();
        return System.currentTimeMillis() + "" + random.nextInt(100000);
    }

}
