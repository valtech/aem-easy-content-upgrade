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

package de.valtech.aecu.core.groovy.console.bindings.actions.print;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.granite.rest.utils.ModifiableMappedValueMapDecorator;

/**
 * Tests PrintJson
 *
 * @author Roxana Muresan
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PrintJsonTest {

    @Mock
    private Resource resource;
    @Mock
    private ValueMap properties;


    @BeforeEach
    public void init() {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("sling:resourceType", "weretail/components/content/heroimage");
        propertiesMap.put("multivalue", new String[] {"v1", "v2", "v3"});
        propertiesMap.put("number", 123);
        properties = new ModifiableMappedValueMapDecorator(propertiesMap);

        when(resource.getValueMap()).thenReturn(properties);
    }

    @Test
    public void test_doAction() {
        PrintJson printJson = new PrintJson();
        String result = printJson.doAction(resource);

        assertTrue(result.contains("\"sling:resourceType\": \"weretail/components/content/heroimage\""));
        assertTrue(result.contains("\"number\": 123"));
        assertTrue(result.contains("\"multivalue\": ["));
        assertTrue(result.contains("\"v1\","));
        assertTrue(result.contains("\"v2\","));
        assertTrue(result.contains("\"v3\""));
    }

}
