/*
 * Copyright 2020 - 2022 Valtech GmbH
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

package de.valtech.aecu.core.groovy.console.bindings.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import de.valtech.aecu.api.groovy.console.bindings.AecuBinding;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AceAutocompleteServletTest {

    @Spy
    private AceAutocompleteServlet underTest = new AceAutocompleteServlet();

    @Test
    public void test_getPublicMethodsOfClass_ofAecuBinding()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Method> result = underTest.getPublicMethodsOfClass(AecuBinding.class);

        assertNotNull(result);
        assertEquals(2, result.size());
        for (Method method : result) {
            assertTrue("contentUpgradeBuilder".equals(method.getName()) || "validateAccessRights".equals(method.getName()));
        }
    }

    @Test
    public void test_doGet_response() throws IOException, ServletException {
        SlingHttpServletResponse responseMock = mock(SlingHttpServletResponse.class);
        PrintWriter writerMock = mock(PrintWriter.class);
        when(responseMock.getWriter()).thenReturn(writerMock);

        underTest.doGet(mock(SlingHttpServletRequest.class), responseMock);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(writerMock, times(1)).write(captor.capture());
        String actualJsonContent = captor.getValue();

        assertNotNull(actualJsonContent);
        JsonArray actualJson = new Gson().fromJson(actualJsonContent, JsonArray.class);
        assertNotNull(actualJson);
        assertTrue(actualJson.size() > 5);
        assertTrue(actualJson.contains(new JsonPrimitive("aecu")));
        assertTrue(actualJson.contains(new JsonPrimitive("contentUpgradeBuilder()")));
        assertTrue(actualJson.contains(new JsonPrimitive("run()")));
    }
}
