package de.valtech.aecu.core.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.orbinson.aem.groovy.console.api.context.ScriptContext;

/**
 * Script context to run Groovy Console scripts.
 *
 * @author Roland Gruber
 */
public class AecuScriptContext implements ScriptContext {

    private static final Logger LOG = LoggerFactory.getLogger(AecuScriptContext.class);

    private String script;
    private ResourceResolver resolver;
    private String data;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    /**
     * Constructor
     *
     * @param data json data for scripts
     * @param script   script content
     * @param resolver resolver
     */
    public AecuScriptContext(String script, ResourceResolver resolver, String data) {
        this.script = script;
        this.resolver = resolver;
        this.data = data;
    }

    public AecuScriptContext(String script, ResourceResolver resolver) {
        this(script, resolver, null);
    }


    @Override
    public String getData() {
        return this.data;
    }

    @Override
    public ByteArrayOutputStream getOutputStream() {
        return out;
    }

    @Override
    public PrintStream getPrintStream() {
        try {
            return new PrintStream(out, true, StandardCharsets.UTF_8.name());
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
        return resolver.getUserID();
    }

}
