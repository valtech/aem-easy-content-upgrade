package de.valtech.aecu.core.groovy.console.bindings.impl;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.valtech.aecu.api.groovy.console.bindings.AecuBinding;

@RunWith(MockitoJUnitRunner.class)
public class SimpleContentUpdateTest {

    @Mock
    private ResourceResolver resourceResolverMock;

    private AecuBinding simpleContentUpdate;


    @Before
    public void setUp() throws Exception {
        simpleContentUpdate = new AecuBindingImpl(resourceResolverMock);
    }

    @Test
    public void toDo() {
        // TODO!!
    }
}
