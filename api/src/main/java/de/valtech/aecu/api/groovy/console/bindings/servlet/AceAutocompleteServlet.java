/*
 * Copyright 2020 Valtech GmbH
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

package de.valtech.aecu.api.groovy.console.bindings.servlet;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.valtech.aecu.api.groovy.console.bindings.AecuBinding;

import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

@Component(immediate = true,
        service = { Servlet.class },
        property = {
                "sling.servlet.paths=/bin/public/valtech/aecu/ace_autocomplete",
                "sling.servlet.extensions=json",
                "sling.servlet.methods=GET" })
public class AceAutocompleteServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AceAutocompleteServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        try (PrintWriter responseWriter = response.getWriter()) {
            JsonObject extensions = new JsonObject();

            List<Method> methods = getPublicMethodsOfClass(AecuBinding.class);
            for (Method method : methods) {
                Class extensionClass = method.getReturnType();
                List<Method> publicMethods = getPublicMethodsOfClass(extensionClass);

                JsonArray methodsJson = new JsonArray(publicMethods.size());
                publicMethods.stream().forEach(n -> methodsJson.add(n.getName()));
                extensions.add(method.getName(), methodsJson);
            }

            JsonObject aecuRoot = new JsonObject();
            aecuRoot.add(AecuBinding.BINDING_NAME, extensions);

            String responseString = new Gson().toJson(aecuRoot);

            responseWriter.write(responseString);
            response.setStatus(HttpStatus.SC_OK);
        }

    }

    private List<Method> getPublicMethodsOfClass(Class clazz) {
        return Arrays.stream(clazz.getDeclaredMethods()).filter(m -> (m.getModifiers() & Modifier.PUBLIC) != 0).collect(Collectors.toList());
    }
}
