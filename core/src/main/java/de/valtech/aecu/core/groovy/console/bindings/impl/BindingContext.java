/*
 * Copyright 2018 Valtech GmbH
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
package de.valtech.aecu.core.groovy.console.bindings.impl;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.PageManager;

/**
 * Context for binding.
 * 
 * @author Roland Gruber
 */
public class BindingContext {

    private ResourceResolver resolver;
    private PageManager pageManager;
    private TagManager tagManager;
    private Replicator replicator;
    private boolean dryRun = true;
    private RequestResponseFactory requestResponseFactory;
    private SlingRequestProcessor slingRequestProcessor;

    /**
     * Constructor
     * 
     * @param resolver resource resolver
     */
    public BindingContext(ResourceResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Returns the resource resolver.
     * 
     * @return resolver
     */
    public ResourceResolver getResolver() {
        return resolver;
    }

    /**
     * Returns the page manager.
     * 
     * @return page manager
     */
    public PageManager getPageManager() {
        if (pageManager != null) {
            return pageManager;
        }
        pageManager = resolver.adaptTo(PageManager.class);
        return pageManager;
    }

    /**
     * Returns the tag manager.
     * 
     * @return tag manager
     */
    public TagManager getTagManager() {
        if (tagManager != null) {
            return tagManager;
        }
        tagManager = resolver.adaptTo(TagManager.class);
        return tagManager;
    }

    /**
     * Returns the page replicator.
     * 
     * @return replicator
     */
    public Replicator getReplicator() {
        if (replicator != null) {
            return replicator;
        }
        Bundle bundle = FrameworkUtil.getBundle(BindingContext.class);
        ServiceReference<Replicator> replicatorReference = bundle.getBundleContext().getServiceReference(Replicator.class);
        if (replicatorReference != null) {
            replicator = bundle.getBundleContext().getService(replicatorReference);
        }
        return replicator;
    }

    /**
     * Returns the request response factory.
     * 
     * @return request response factory
     */
    public RequestResponseFactory getRequestResponseFactory() {
        if (requestResponseFactory != null) {
            return requestResponseFactory;
        }
        Bundle bundle = FrameworkUtil.getBundle(BindingContext.class);
        ServiceReference<RequestResponseFactory> requestResponseFactoryReference =
                bundle.getBundleContext().getServiceReference(RequestResponseFactory.class);
        if (requestResponseFactoryReference != null) {
            requestResponseFactory = bundle.getBundleContext().getService(requestResponseFactoryReference);
        }
        return requestResponseFactory;
    }

    /**
     * Returns the Sling request processor.
     * 
     * @return Sling request processor
     */
    public SlingRequestProcessor getSlingRequestProcessor() {
        if (slingRequestProcessor != null) {
            return slingRequestProcessor;
        }
        Bundle bundle = FrameworkUtil.getBundle(BindingContext.class);
        ServiceReference<SlingRequestProcessor> slingRequestProcessorReference =
                bundle.getBundleContext().getServiceReference(SlingRequestProcessor.class);
        if (slingRequestProcessorReference != null) {
            slingRequestProcessor = bundle.getBundleContext().getService(slingRequestProcessorReference);
        }
        return slingRequestProcessor;
    }

    /**
     * Returns if this is a dry run.
     * 
     * @return dry run
     */
    public boolean isDryRun() {
        return dryRun;
    }

    /**
     * Sets the dry run mode.
     * 
     * @param dryRun dry run
     */
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

}
