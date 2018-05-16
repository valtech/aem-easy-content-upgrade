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
