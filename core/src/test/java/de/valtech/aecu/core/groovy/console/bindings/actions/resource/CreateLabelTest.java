package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import de.valtech.aecu.core.groovy.console.bindings.actions.properties.SetProperty;
import groovy.lang.GString;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.codehaus.groovy.runtime.GStringImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreateLabelTest {

    private static final String VAL1 = "val1";

    private static final String ATTR = "attr";


    @Mock
    private Resource resource;


    @Mock
    private ModifiableValueMap valueMap;


//    @BeforeEach
//    public void setup() {
//        when(resource.adaptTo(ModifiableValueMap.class)).thenReturn(valueMap);
//        when(subNode.adaptTo(ModifiableValueMap.class)).thenReturn(valueMapSubnode);
//        when(resource.getChild(SUBNODE_PATH)).thenReturn(subNode);
//    }
//
//    @Test
//    public void doAction() throws PersistenceException {
//        CreateLabel action = new CreateLabel();
//
//        action.doAction(resource);
//
//        verify(valueMap, times(1)).put(ATTR, VAL1);
//    }

}