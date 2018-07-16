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
package de.valtech.aecu.core.installhook;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import javax.annotation.Nonnull;

/**
 * Service Provider for InstallHook.
 */
public class OsgiServiceProvider {

    private final BundleContext bundleContext;

    /**
     * Constructor.
     * 
     * @param clazz the class that was loaded over a bundle classloader.
     */
    public OsgiServiceProvider(@Nonnull Class clazz) {
        Bundle currentBundle = FrameworkUtil.getBundle(clazz);
        if (currentBundle == null) {
            throw new IllegalStateException("The class " + clazz + " was not loaded through a bundle classloader");
        }
        bundleContext = currentBundle.getBundleContext();
        if (bundleContext == null) {
            throw new IllegalStateException("Could not get bundle context for bundle " + currentBundle);
        }
    }

    /**
     * Retrieves a {@link ServiceReference} for the given class.
     * 
     * @see org.osgi.framework.BundleContext#getServiceReference(Class)
     * @param clazz the class to retrieve a {@link ServiceReference} for.
     * @param       <T> the type of the service.
     * @return a {@link ServiceReference} for the requested service.
     */
    @Nonnull
    <T> ServiceReference<T> getServiceReference(@Nonnull Class<T> clazz) {
        ServiceReference<T> serviceReference = bundleContext.getServiceReference(clazz);
        if (serviceReference == null) {
            throw new IllegalStateException("Could not retrieve service reference for class " + clazz);
        }
        return serviceReference;
    }

    /**
     * Retrieves the service object the {@link ServiceReference} is pointing to.
     * 
     * @see org.osgi.framework.BundleContext#getService(ServiceReference)
     * @param serviceReference the {@link ServiceReference}.
     * @param                  <T> the service type.
     * @return the service instance.
     */
    @Nonnull
    <T> T getService(@Nonnull ServiceReference<T> serviceReference) {
        T service = bundleContext.getService(serviceReference);
        if (service == null) {
            throw new IllegalStateException("Could not get the service for reference " + serviceReference
                    + ", verify that the bundle was installed correctly!");
        }
        return service;
    }

    /**
     * @see org.osgi.framework.BundleContext#ungetService(ServiceReference)
     */
    boolean ungetService(@Nonnull ServiceReference<?> serviceReference) {
        return bundleContext.ungetService(serviceReference);
    }

}
