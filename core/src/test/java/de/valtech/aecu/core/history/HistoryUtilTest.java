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
package de.valtech.aecu.core.history;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.valtech.aecu.service.AecuException;

/**
 * Tests HistoryUtil
 * 
 * @author Roland Gruber
 */
@RunWith(value=MockitoJUnitRunner.class)
public class HistoryUtilTest {
    
    @Spy
    private HistoryUtil historyUtil;

    @Mock
    private ResourceResolver resolver;
    
    @Test
    public void createPath_Existing() throws AecuException {
        String path = "/var/aecu/2018/5";
        when(resolver.getResource(path)).thenReturn(mock(Resource.class));
        
        historyUtil.createPath(path, resolver, JcrResourceConstants.NT_SLING_FOLDER);
        
        verify(historyUtil, times(1)).createPath(anyString(), eq(resolver), eq(JcrResourceConstants.NT_SLING_FOLDER));
    }

    @Test
    public void createPath_NotExisting() throws AecuException, PersistenceException {
        String path = "/var/aecu/2018/5";
        when(resolver.getResource("/var/aecu/2018")).thenReturn(mock(Resource.class));
        
        historyUtil.createPath(path, resolver, JcrResourceConstants.NT_SLING_FOLDER);
        
        verify(historyUtil, times(1)).createPath(anyString(), eq(resolver), eq(JcrResourceConstants.NT_SLING_FOLDER));
        verify(resolver, times(1)).create(any(Resource.class), eq("5"), any());
    }

}
