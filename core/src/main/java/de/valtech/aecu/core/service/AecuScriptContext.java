package de.valtech.aecu.core.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.icfolson.aem.groovy.console.api.context.ScriptContext;
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants;

/**
 * Script context to run Groovy Console scripts.
 * 
 * @author Roland Gruber
 */
public class AecuScriptContext implements ScriptContext {

    private static final Logger LOG = LoggerFactory.getLogger(AecuScriptContext.class);

    private String script;
    private ResourceResolver resolver;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    private SlingHttpServletRequest request;

    /**
     * Constructor
     * 
     * @param script   script content
     * @param resolver resolver
     * @param request  request
     */
    public AecuScriptContext(String script, ResourceResolver resolver, SlingHttpServletRequest request) {
        this.script = script;
        this.resolver = resolver;
        this.request = request;
    }

    @Override
    public String getData() {
        return request.getParameter(GroovyConsoleConstants.DATA);
    }

    @Override
    public ByteArrayOutputStream getOutputStream() {
        return out;
    }

    @Override
    public PrintStream getPrintStream() {
        try {
            return new PrintStream(out, true, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOG.error("Unable to create print stream", e);
        }
        return null;
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return resolver;
    }

    @Override
    public String getScript() {
        return script;
    }

    @Override
    public String getUserId() {
        return request.getResourceResolver().getUserID();
    }

}
