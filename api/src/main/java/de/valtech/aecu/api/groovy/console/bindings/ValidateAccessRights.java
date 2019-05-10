/*
 * Copyright 2019 Valtech GmbH
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
package de.valtech.aecu.api.groovy.console.bindings;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Validates access rights for users or groups.
 * 
 * @author Roland Gruber
 */
@ProviderType
public interface ValidateAccessRights {

    /**
     * Checks the permissions on specific paths.
     * 
     * @param paths repository paths (e.g. /content/project)
     * @return access right validation builder
     */
    ValidateAccessRights forPaths(String... paths);

    /**
     * Checks the permissions for specific users or groups.
     * 
     * @param authorizables user or group names
     * @return access right validation builder
     */
    ValidateAccessRights forAuthorizables(String... authorizables);

    /**
     * Checks if read access is granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights canRead();

    /**
     * Checks if write access is granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights canWrite();

    /**
     * Checks if the specified rights are correctly set.
     * 
     * @return access right validation builder
     */
    String validate();

}
