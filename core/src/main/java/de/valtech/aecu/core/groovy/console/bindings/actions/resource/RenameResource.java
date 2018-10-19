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

import javax.annotation.Nonnull;
import javax.jcr.ItemExistsException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.VersionException;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;

/**
 * Renames a resource.
 * 
 * @author Roland Gruber
 */
public class RenameResource implements Action {

    private ResourceResolver resourceResolver;
    private String newName;

    /**
     * Constructor
     * 
     * @param resourceResolver resource resolver
     * @param newName          new name
     */
    public RenameResource(@Nonnull ResourceResolver resourceResolver, @Nonnull String newName) {
        this.resourceResolver = resourceResolver;
        this.newName = newName;
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException {
        Session session = resourceResolver.adaptTo(Session.class);
        String path = resource.getPath();
        String newPath = resource.getParent().getPath() + "/" + newName;
        try {
            session.move(path, newPath);
        } catch (ItemExistsException e) {
            throw new PersistenceException("Target path already exists", e);
        } catch (PathNotFoundException e) {
            throw new PersistenceException("Target path does not exist", e);
        } catch (VersionException e) {
            throw new PersistenceException("Version error", e);
        } catch (RepositoryException e) {
            throw new PersistenceException("Rename failed", e);
        }
        return "Renamed resource " + path + " to " + newName;
    }
}
