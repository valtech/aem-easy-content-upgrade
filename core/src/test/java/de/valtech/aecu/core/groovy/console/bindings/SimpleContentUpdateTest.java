package de.valtech.aecu.core.groovy.console.bindings;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SimpleContentUpdateTest {

    @Mock
    private ResourceResolver resourceResolverMock;

    private SimpleContentUpdate simpleContentUpdate;


    @Before
    public void setUp() throws Exception {
        simpleContentUpdate = new SimpleContentUpdate(resourceResolverMock);
    }

    @Test
    public void toDo() {
        // TODO!!
    }
}
