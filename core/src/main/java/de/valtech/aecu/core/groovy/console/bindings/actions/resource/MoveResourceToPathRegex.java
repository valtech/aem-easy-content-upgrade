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

package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;

public class MoveResourceToPathRegex implements Action {

    private ResourceResolver resourceResolver;
    private String matchPattern;
    private String targetPathExpr;

    /**
     * Constructor
     * @param resourceResolver
     * @param matchPattern
     * @param targetPathExpr
     */
    public MoveResourceToPathRegex(@Nonnull String matchPattern, @Nonnull String targetPathExpr, @Nonnull ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        this.matchPattern = matchPattern;
        this.targetPathExpr = targetPathExpr;
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException {
        String resourcePath = resource.getPath();
        if (resourcePath.matches(matchPattern)) {
            String targetPath = resourcePath.replaceAll(matchPattern, targetPathExpr);
            Resource destinationResource = resourceResolver.getResource(targetPath);

            if (destinationResource != null) {
                resourceResolver.move(resourcePath, targetPath);

                return "Moved " + resourcePath + " to path " + targetPath;
            }
            return "WARNING: could not read move destination resource " + targetPath;
        }
        return "INFO: resource " + resourcePath + " does not match path regex " + matchPattern + ", skipping";
    }
}
