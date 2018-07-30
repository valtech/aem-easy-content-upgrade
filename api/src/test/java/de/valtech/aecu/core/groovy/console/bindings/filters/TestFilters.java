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

package de.valtech.aecu.core.groovy.console.bindings.filters;

import com.adobe.granite.rest.utils.ModifiableMappedValueMapDecorator;

import de.valtech.aecu.api.groovy.console.bindings.filters.ANDFilter;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterBy;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByMultiValuePropContains;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByNodeName;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByNodeNameRegex;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByProperties;
import de.valtech.aecu.api.groovy.console.bindings.filters.NOTFilter;
import de.valtech.aecu.api.groovy.console.bindings.filters.ORFilter;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Roxana Muresan
 */
public class TestFilters {

    private static Map<String, Object> properties = null;

    @BeforeClass
    public static void init() {
        properties = new LinkedHashMap<>();
        properties.put("has_string_prop", "strrrrrring");
        properties.put("multivalue", new String[] {"v1", "v2", "v3"});
        properties.put("has_boolean_prop", true);
        properties.put("has_int_prop", 123);
        properties.put("string_prop_as_int", "456");
    }

    @Test
    public void filter_whenEmpty_thenTrue() {
        Resource resource = getMockResourceWithNameAndProperties("any_resource", properties);

        Map<String, Object> filter_properties_1 = new HashMap<>();
        FilterBy noMatch_1 = new FilterByProperties(filter_properties_1);
        assertTrue(noMatch_1.filter(resource));
    }

    @Test
    public void filter_whenValueMismatch_thenFalse() {
        Resource resource = getMockResourceWithNameAndProperties("any_resource", properties);

        testWithOneProp(resource,"has_string_prop", "no match!", false);

        testWithOneProp(resource,"has_string_prop", null, false);

        testWithOneProp(resource,"not_has_prop2", "not null", false);

        testWithOneProp(resource,"has_int_prop", "123", false);

        testWithOneProp(resource,"string_prop_as_int", 456, false);

        testWithOneProp(resource,"has_boolean_prop", "true", false);

        testWithOneProp(resource,"has_boolean_prop", false, false);

        testWithOneProp(resource,"has_int_prop", 124, false);
    }

    @Test
    public void filter_whenValueMatches_thenTrue() {
        Resource resource = getMockResourceWithNameAndProperties("any_resource", properties);

        testWithOneProp(resource,"has_string_prop", "strrrrrring", true);

        testWithOneProp(resource,"has_boolean_prop", true, true);

        testWithOneProp(resource,"has_int_prop", 123, true);

        testWithOneProp(resource,"unexisting", null, true);
    }

    @Test
    public void filter_whenMultipleValuesMismatch_thenFalse() {
        Resource resource = getMockResourceWithNameAndProperties("any_resource", properties);
        Map<String, Object> filter_properties_1 = new HashMap<>();
        filter_properties_1.putAll(properties);
        assertTrue(new FilterByProperties(filter_properties_1).filter(resource));

        filter_properties_1.put("mismatching", "any");
        assertFalse(new FilterByProperties(filter_properties_1).filter(resource));

        filter_properties_1.remove("mismatching");
        filter_properties_1.put("has_int_prop", "change value");
        assertFalse(new FilterByProperties(filter_properties_1).filter(resource));
    }

    @Test
    public void filter_whenMultipleValuesMatch_thenTrue() {
        Resource resource = getMockResourceWithNameAndProperties("any_resource", properties);
        Map<String, Object> filter_properties_1 = new HashMap<>();
        filter_properties_1.putAll(properties);
        assertTrue(new FilterByProperties(filter_properties_1).filter(resource));

        filter_properties_1.remove("has_int_prop");
        assertTrue(new FilterByProperties(filter_properties_1).filter(resource));

        filter_properties_1.remove("has_string_prop");
        assertTrue(new FilterByProperties(filter_properties_1).filter(resource));

        filter_properties_1.put("any", null);
        assertTrue(new FilterByProperties(filter_properties_1).filter(resource));

        filter_properties_1.put("multivalue", new String[] {"v1", "v2"});
        assertFalse(new FilterByProperties(filter_properties_1).filter(resource));

        Map<String, Object> filter_properties_2 = new LinkedHashMap<>();
        filter_properties_2.put("multivalue", new String[] {"v1", "v2", "v3"});
        filter_properties_2.put("invalid_after", "make_sure_it_checks_til_the_last_element");
        assertFalse(new FilterByProperties(filter_properties_2).filter(resource));
    }

    @Test
    public void filter_whenMultiValueFieldMatch_thenTrue() {
        Map<String, Object> properties_multiValue = new HashMap<>();
        properties_multiValue.put("testMultiValue", new String[]{"val_1", "val_2", "val_3"});
        properties_multiValue.put("testMultiValueInt", new Integer[]{1, 2, 3});
        Resource resource = getMockResourceWithNameAndProperties("any_resource", properties_multiValue);

        Map<String, Object> properties_multiValue_same = new HashMap<>();
        properties_multiValue_same.put("testMultiValue", new String[]{"val_1", "val_2", "val_3"});
        properties_multiValue_same.put("testMultiValueInt", new Integer[]{1, 2, 3});
        assertTrue(new FilterByProperties(properties_multiValue_same).filter(resource));

        Map<String, Object> properties_multiValue_same_int = new HashMap<>();
        properties_multiValue_same_int.put("testMultiValueInt", new Integer[]{1, 2, 3});
        assertTrue(new FilterByProperties(properties_multiValue_same).filter(resource));

        Map<String, Object> properties_multiValue_2 = new HashMap<>();
        properties_multiValue_2.put("testMultiValue", new String[]{"val_1", "val_2"});
        assertFalse(new FilterByProperties(properties_multiValue_2).filter(resource));
    }

    @Test
    public void testFilterByNodeName() {
        Resource resource = getMockResourceWithNameAndProperties("any_resource", null);
        FilterBy nameFilter = new FilterByNodeName("noMatch");
        assertFalse(nameFilter.filter(resource));
    }

    @Test
    public void testFilterByNodeNameRegex() {
        Resource resource = getMockResourceWithNameAndProperties("any_resource_1234", null);

        assertFalse(new FilterByNodeNameRegex("any_resource").filter(resource));
        assertTrue(new FilterByNodeNameRegex("\\w+").filter(resource));
        assertTrue(new FilterByNodeNameRegex("any_resource\\w*").filter(resource));
        assertFalse(new FilterByNodeNameRegex("any_resource\\d*").filter(resource));
        assertTrue(new FilterByNodeNameRegex("any_resource_\\d*").filter(resource));
    }

    @Test
    public void testNOTFilter() {
        Resource resource = getMockResourceWithNameAndProperties("any_name", null);

        FilterBy filter_1 = new FilterByNodeName("not_just_any_name");
        assertFalse(filter_1.filter(resource));
        assertTrue(new NOTFilter(filter_1).filter(resource));

        FilterBy filter_2 = new FilterByNodeName("any_name");
        assertTrue(filter_2.filter(resource));
        assertFalse(new NOTFilter(filter_2).filter(resource));
    }

    @Test
    public void testORFilter() {
        Resource resource = getMockResourceWithNameAndProperties("any_name", properties);

        FilterBy filter_1 = new FilterByNodeNameRegex("[^_]+");
        assertFalse(filter_1.filter(resource));

        Map<String, Object> filter_properties = new HashMap<>();
        filter_properties.put("no_match", false);
        FilterBy filter_2 = new FilterByProperties(filter_properties);
        assertFalse(filter_2.filter(resource));

        FilterBy filter_3 = new FilterByNodeName("any_name");
        assertTrue(filter_3.filter(resource));

        FilterBy orFilter_mismatch = new ORFilter(Arrays.asList(new FilterBy[]{filter_1, filter_2}));
        assertFalse(orFilter_mismatch.filter(resource));

        FilterBy orFilter_match = new ORFilter(Arrays.asList(new FilterBy[]{filter_1, filter_2, filter_3}));
        assertTrue(orFilter_match.filter(resource));

        FilterBy orFilter_mismatch_2 = new ORFilter(Arrays.asList(new FilterBy[]{filter_1, filter_2, new NOTFilter(filter_3)}));
        assertFalse(orFilter_mismatch_2.filter(resource));
    }

    @Test
    public void testANDFilter() {
        Resource resource = getMockResourceWithNameAndProperties("any_name", properties);

        FilterBy filter_1 = new FilterByNodeNameRegex("\\w+");
        assertTrue(filter_1.filter(resource));

        Map<String, Object> filter_properties = new HashMap<>();
        filter_properties.put("no_match", false);
        FilterBy filter_2 = new FilterByProperties(filter_properties);
        assertFalse(filter_2.filter(resource));

        FilterBy filter_3 = new FilterByNodeName("any_name");
        assertTrue(filter_3.filter(resource));

        FilterBy andFilter_match = new ANDFilter(Arrays.asList(new FilterBy[]{filter_1, filter_3}));
        assertTrue(andFilter_match.filter(resource));

        FilterBy andFilter_mismatch = new ANDFilter(Arrays.asList(new FilterBy[]{filter_1, filter_3, filter_2, filter_1, filter_3}));
        assertFalse(andFilter_mismatch.filter(resource));

        FilterBy andFilter_match_2 = new ANDFilter(Arrays.asList(new FilterBy[]{filter_1, filter_3, new NOTFilter(filter_2), filter_1, filter_3}));
        assertTrue(andFilter_match_2.filter(resource));
    }

    @Test
    public void testFilterByMultiValuePropContains() {
        Resource resource = getMockResourceWithNameAndProperties("any_name", properties);
        FilterBy filter = new FilterByMultiValuePropContains("any", new String[]{"any"});

        assertFalse(filter.filter(resource));

        FilterBy filter_1 = new FilterByMultiValuePropContains("has_string_prop", new String[]{"strrrrrring"});
        assertFalse(filter_1.filter(resource));

        Map<String, Object> propertiesWithMultiValue = new HashMap<>();
        propertiesWithMultiValue.put("multiValues", new String[]{"val_1", "val_2", "val_3"});
        propertiesWithMultiValue.put("multiValuesInt", new Integer[]{1, 2, 3});
        Resource resource_2 = getMockResourceWithNameAndProperties("any_name", propertiesWithMultiValue);

        assertFalse(filter.filter(resource_2));

        FilterBy filter_2 = new FilterByMultiValuePropContains("multiValues", new String[]{});
        assertTrue(filter_2.filter(resource_2));

        FilterBy filter_3 = new FilterByMultiValuePropContains("multiValues", new String[]{"no_match"});
        assertFalse(filter_3.filter(resource_2));
        FilterBy filter_3_int = new FilterByMultiValuePropContains("multiValuesInt", new Integer[]{56});
        assertFalse(filter_3_int.filter(resource_2));

        FilterBy filter_4 = new FilterByMultiValuePropContains("multiValues", new String[]{"val_2"});
        assertTrue(filter_4.filter(resource_2));

        FilterBy filter_5 = new FilterByMultiValuePropContains("multiValuesInt", new Integer[]{1, 2, 3});
        assertTrue(filter_5.filter(resource_2));

        FilterBy filter_6 = new FilterByMultiValuePropContains("multiValues", new String[]{"val_1", "no_match", "val_3"});
        assertFalse(filter_6.filter(resource_2));
    }

    private void testWithOneProp(Resource resource, String propName, Object propValue, boolean matches) {
        Map<String, Object> filter_properties = new HashMap<>();
        filter_properties.put(propName, propValue);
        FilterBy filter = new FilterByProperties(filter_properties);
        assertEquals(matches, filter.filter(resource));
    }

    private Resource getMockResourceWithNameAndProperties(String name, Map<String, Object> proeprties) {
        Resource resourceMock = Mockito.mock(Resource.class);
        when(resourceMock.getName()).thenReturn(name);
        when(resourceMock.adaptTo(ModifiableValueMap.class)).thenReturn(new ModifiableMappedValueMapDecorator(proeprties));
        when(resourceMock.adaptTo(ValueMap.class)).thenReturn(new ValueMapDecorator(proeprties));
        return resourceMock;
    }
}
