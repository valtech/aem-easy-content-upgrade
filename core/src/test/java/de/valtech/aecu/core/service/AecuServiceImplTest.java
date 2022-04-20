/*
 * Copyright 2018 - 2022 Valtech GmbH
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
package de.valtech.aecu.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.icfolson.aem.groovy.console.GroovyConsoleService;
import com.icfolson.aem.groovy.console.api.context.ScriptContext;
import com.icfolson.aem.groovy.console.response.RunScriptResponse;
import com.icfolson.aem.groovy.console.response.impl.DefaultRunScriptResponse;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;
import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.ExecutionState;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.api.service.HistoryEntry.STATE;
import de.valtech.aecu.core.history.HistoryUtil;
import de.valtech.aecu.core.installhook.HookExecutionHistory;
import de.valtech.aecu.core.serviceuser.ServiceResourceResolverService;

/**
 * Tests AecuServiceImpl
 *
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AecuServiceImplTest {

    private static final String DIR = AecuService.AECU_CONF_PATH_PREFIX + "/dir";

    private static final String FILE1 = "file1.groovy";

    @InjectMocks
    @Spy
    private AecuServiceImpl service;

    @Mock
    private SlingSettingsService settingsService;

    @Mock
    private ServiceResourceResolverService resolverService;

    @Mock
    private GroovyConsoleService groovyConsoleService;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private HistoryUtil historyUtil;

    @Mock
    private ScriptContext scriptContext = mock(ScriptContext.class);

    @Mock
    private Session session;


    @BeforeEach
    public void setup() throws LoginException {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        runModes.add("test");
        runModes.add("test2");
        runModes.add("test3");
        when(settingsService.getRunModes()).thenReturn(runModes);
        when(resolverService.getServiceResourceResolver()).thenReturn(resolver);
        when(resolverService.getContentMigratorResourceResolver()).thenReturn(resolver);
    }

    @Test
    public void matchesRunmodes_noMode() {
        assertTrue(service.matchesRunmodes("name"));
    }

    @Test
    public void matchesRunmodes_nonMatching() {
        assertFalse(service.matchesRunmodes("name.publish"));
    }

    @Test
    public void matchesRunmodes_matching() {
        assertTrue(service.matchesRunmodes("name.author"));
        assertTrue(service.matchesRunmodes("name.test"));
    }

    @Test
    public void matchesRunmodes_matchingMulti() {
        assertTrue(service.matchesRunmodes("name.author.test"));
        assertTrue(service.matchesRunmodes("name.author.test;testNO"));
        assertTrue(service.matchesRunmodes("name.authorNO.test;test2.author"));
    }

    @Test
    public void matchesRunmodes_nonMatchingMulti() {
        assertFalse(service.matchesRunmodes("name.author.testNO"));
        assertFalse(service.matchesRunmodes("name.author.testNO;testNO"));
        assertFalse(service.matchesRunmodes("name.author.testNO;test2NO.author"));
    }

    @Test
    public void isValidScriptName_ok() {
        assertTrue(service.isValidScriptName("test.groovy"));
    }

    @Test
    public void isValidScriptName_invalidExtension() {
        assertFalse(service.isValidScriptName("test.txt"));
    }

    @Test
    public void isValidScriptName_fallback() {
        assertFalse(service.isValidScriptName("test.fallback.groovy"));
        assertFalse(service.isValidScriptName("fallback.groovy"));
    }

    @Test
    public void isValidScriptName_prechecks() {
        assertFalse(service.isValidScriptName("test.prechecks.groovy"));
        assertFalse(service.isValidScriptName("prechecks.groovy"));
    }

    @Test
    public void getFallbackScript_Exists() {
        when(resolver.getResource("/path/to/script.fallback.groovy")).thenReturn(mock(Resource.class));

        assertEquals("/path/to/script.fallback.groovy", service.getFallbackScript(resolver, "/path/to/script.always.groovy"));
        assertEquals("/path/to/script.fallback.groovy", service.getFallbackScript(resolver, "/path/to/script.groovy"));
    }

    @Test
    public void getFallbackScript_NotExists() {
        assertNull(service.getFallbackScript(resolver, "/path/to/script.always.groovy"));
        assertNull(service.getFallbackScript(resolver, "/path/to/script.groovy"));
    }

    @Test
    public void getFallbackScript_Fallback() {
        verify(resolver, never()).getResource("/path/to/script.fallback.groovy");

        assertNull(service.getFallbackScript(resolver, "/path/to/script.fallback.groovy"));
    }

    @Test
    public void getFallbackScript_DirectoryLevel() {
        when(resolver.getResource("/path/to/script1.fallback.groovy")).thenReturn(mock(Resource.class));
        when(resolver.getResource("/path/to/fallback.groovy")).thenReturn(mock(Resource.class));

        assertEquals("/path/to/script1.fallback.groovy", service.getFallbackScript(resolver, "/path/to/script1.always.groovy"));
        assertEquals("/path/to/script1.fallback.groovy", service.getFallbackScript(resolver, "/path/to/script1.groovy"));
        assertEquals("/path/to/fallback.groovy", service.getFallbackScript(resolver, "/path/to/script2.always.groovy"));
        assertEquals("/path/to/fallback.groovy", service.getFallbackScript(resolver, "/path/to/script2.groovy"));
    }

    @Test
    public void getFallbackScript_DirectoryLevelFallback() {
        verify(resolver, never()).getResource("/path/to/fallback.groovy");

        assertNull(service.getFallbackScript(resolver, "/path/to/fallback.groovy"));
    }

    @Test
    public void getPrechecksScript_Exists() {
        when(resolver.getResource("/path/to/script.prechecks.groovy")).thenReturn(mock(Resource.class));

        assertEquals("/path/to/script.prechecks.groovy", service.getPrechecksScript(resolver, "/path/to/script.always.groovy"));
        assertEquals("/path/to/script.prechecks.groovy", service.getPrechecksScript(resolver, "/path/to/script.groovy"));
    }

    @Test
    public void getPrechecksScript_NotExists() {
        assertNull(service.getPrechecksScript(resolver, "/path/to/script.always.groovy"));
        assertNull(service.getPrechecksScript(resolver, "/path/to/script.groovy"));
    }

    @Test
    public void getPrechecksScript_Prechecks() {
        verify(resolver, never()).getResource("/path/to/script.prechecks.groovy");

        assertNull(service.getPrechecksScript(resolver, "/path/to/script.prechecks.groovy"));
    }

    @Test
    public void getPrechecksScript_DirectoryLevel() {
        when(resolver.getResource("/path/to/script1.prechecks.groovy")).thenReturn(mock(Resource.class));
        when(resolver.getResource("/path/to/prechecks.groovy")).thenReturn(mock(Resource.class));

        assertEquals("/path/to/script1.prechecks.groovy", service.getPrechecksScript(resolver, "/path/to/script1.always.groovy"));
        assertEquals("/path/to/script1.prechecks.groovy", service.getPrechecksScript(resolver, "/path/to/script1.groovy"));
        assertEquals("/path/to/prechecks.groovy", service.getPrechecksScript(resolver, "/path/to/script2.always.groovy"));
        assertEquals("/path/to/prechecks.groovy", service.getPrechecksScript(resolver, "/path/to/script2.groovy"));
    }

    @Test
    public void getPrechecksScript_DirectoryLevelPrechecks() {
        verify(resolver, never()).getResource("/path/to/prechecks.groovy");

        assertNull(service.getPrechecksScript(resolver, "/path/to/prechecks.groovy"));
    }

    @Test
    public void getFiles_invalidPath() throws AecuException {
        assertThrows(AecuException.class, () -> service.getFiles(FILE1));
    }

    @Test
    public void getFiles_validSinglePath() throws AecuException {
        Resource fileResource = mock(Resource.class);
        when(resolver.getResource(FILE1)).thenReturn(fileResource);
        when(fileResource.getName()).thenReturn(FILE1);
        ValueMap fileValues = mock(ValueMap.class);
        when(fileValues.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenReturn(JcrConstants.NT_FILE);
        when(fileResource.getValueMap()).thenReturn(fileValues);

        assertEquals(Arrays.asList(FILE1), service.getFiles(FILE1));
    }

    @Test
    public void getFiles_validDirectoryPath() throws AecuException {
        Resource dirResource = mock(Resource.class);
        when(resolver.getResource(DIR)).thenReturn(dirResource);
        when(dirResource.getName()).thenReturn("dir.author");
        ValueMap dirValues = mock(ValueMap.class);
        when(dirValues.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenReturn(JcrResourceConstants.NT_SLING_FOLDER);
        when(dirResource.getValueMap()).thenReturn(dirValues);

        Resource fileResource = mock(Resource.class);
        when(resolver.getResource(DIR + "/" + FILE1)).thenReturn(fileResource);
        when(fileResource.getName()).thenReturn(FILE1);
        ValueMap fileValues = mock(ValueMap.class);
        when(fileValues.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenReturn(JcrConstants.NT_FILE);
        when(fileResource.getValueMap()).thenReturn(fileValues);
        when(fileResource.getPath()).thenReturn(DIR + "/" + FILE1);

        when(dirResource.getChildren()).thenReturn(Arrays.asList(fileResource));
        when(dirResource.getChild(FILE1)).thenReturn(fileResource);

        assertEquals(Arrays.asList(DIR + "/" + FILE1), service.getFiles(DIR));
    }

    @Test
    public void getFiles_wrongRunmode() throws AecuException {
        Resource dirResource = mock(Resource.class);
        when(resolver.getResource(DIR)).thenReturn(dirResource);
        when(dirResource.getName()).thenReturn("dir.invalid");
        ValueMap dirValues = mock(ValueMap.class);
        when(dirValues.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenReturn(JcrResourceConstants.NT_SLING_FOLDER);
        when(dirResource.getValueMap()).thenReturn(dirValues);

        assertEquals(Arrays.asList(), service.getFiles(DIR));
    }

    @Test
    public void getHistory() throws AecuException {
        when(historyUtil.getHistory(0, 1, resolver)).thenReturn(Arrays.asList(mock(HistoryEntry.class)));

        List<HistoryEntry> entries = service.getHistory(0, 1);

        assertEquals(1, entries.size());
    }

    @Test
    public void storeExecutionInHistory_invalid() throws AecuException {
        assertThrows(AecuException.class, () -> service.storeExecutionInHistory(null, null));
    }

    @Test
    public void storeExecutionInHistory() throws AecuException {
        HistoryEntryImpl history = new HistoryEntryImpl();
        history.setState(STATE.RUNNING);

        service.storeExecutionInHistory(history, null);

        verify(historyUtil, times(1)).storeExecutionInHistory(history, null, resolver);
    }

    @Test
    public void finishHistoryEntry() throws AecuException {
        HistoryEntryImpl history = new HistoryEntryImpl();
        history.setState(STATE.RUNNING);

        service.finishHistoryEntry(history);

        verify(historyUtil, times(1)).finishHistoryEntry(history, resolver);
    }

    @Test
    public void createHistoryEntry() throws AecuException {
        service.createHistoryEntry();

        verify(historyUtil, times(1)).createHistoryEntry(resolver);
    }

    @Test
    public void execute_invalidResource() throws AecuException {
        assertThrows(AecuException.class, () -> service.execute("invalid"));
    }

    @Test
    public void execute_invalidFileName() throws AecuException {
        Resource resource = mock(Resource.class);
        when(resolver.getResource(DIR)).thenReturn(resource);
        when(resource.getName()).thenReturn("invalid");

        assertThrows(AecuException.class, () -> service.execute(DIR));
    }

    @Test
    public void execute() throws AecuException, RepositoryException {
        Resource resource = mock(Resource.class);
        when(resolver.getResource(DIR + "/" + FILE1)).thenReturn(resource);
        when(resolver.getResource(DIR + "/" + FILE1 + "/" + JcrConstants.JCR_CONTENT)).thenReturn(resource);
        when(resource.getName()).thenReturn(FILE1);
        when(scriptContext.getScript()).thenReturn(DIR + "/" + FILE1);
        ByteArrayInputStream stream = new ByteArrayInputStream("test".getBytes());
        when(resource.adaptTo(InputStream.class)).thenReturn(stream);

        RunScriptResponse response = DefaultRunScriptResponse.fromResult(scriptContext, null, null, null);
        when(groovyConsoleService.runScript(Mockito.any())).thenReturn(response);

        ExecutionResult result = service.execute(DIR + "/" + FILE1);

        assertEquals(ExecutionState.SUCCESS, result.getState());
    }

    @Test
    public void executeWithInstallHookHistory() throws AecuException, LoginException {
        ExecutionResult result = mock(ExecutionResult.class);
        when(result.getState()).thenReturn(ExecutionState.SUCCESS);
        doReturn(result).when(service).execute(DIR + "/" + FILE1);
        when(resolverService.getAdminResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        doReturn(Arrays.asList(DIR + "/" + FILE1)).when(service).getFiles(DIR);
        doReturn(mock(HookExecutionHistory.class)).when(service).createHookExecutionHistory(session, DIR + "/" + FILE1);
        HistoryEntryImpl history = new HistoryEntryImpl();
        history.setState(STATE.RUNNING);
        when(historyUtil.createHistoryEntry(resolver)).thenReturn(history);

        service.executeWithInstallHookHistory(DIR);

        verify(resolverService, times(1)).getAdminResourceResolver();
        verify(service, times(1)).getFiles(DIR);
        verify(service, times(1)).createHistoryEntry();
        verify(service, times(1)).execute(DIR + "/" + FILE1);
        verify(service, times(1)).finishHistoryEntry(Mockito.any());
    }

}
