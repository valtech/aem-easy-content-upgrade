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
     * Checks the permissions for specific groups.
     * 
     * @param groups group names
     * @return access right validation builder
     */
    ValidateAccessRights forGroups(String... groups);

    /**
     * Checks if read access is granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights canRead();

    /**
     * Checks if read access is NOT granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights cannotRead();

    /**
     * Checks if modify access is granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights canModify();

    /**
     * Checks if modify access is NOT granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights cannotModify();

    /**
     * Checks if create access is granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights canCreate();

    /**
     * Checks if create access is NOT granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights cannotCreate();

    /**
     * Checks if delete access is granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights canDelete();

    /**
     * Checks if delete access is NOT granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights cannotDelete();

    /**
     * Checks if read ACL access is granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights canReadAcl();

    /**
     * Checks if read ACL access is NOT granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights cannotReadAcl();

    /**
     * Checks if write ACL access is granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights canWriteAcl();

    /**
     * Checks if write ACL access is NOT granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights cannotWriteAcl();

    /**
     * Checks if read access to pages is granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights canReadPage();

    /**
     * Checks if read access to pages is NOT granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights cannotReadPage();

    /**
     * Checks if create access to pages is granted.
     * 
     * @param templatePath template path
     * @return access right validation builder
     */
    ValidateAccessRights canCreatePage(String templatePath);

    /**
     * Checks if create access to pages is NOT granted.
     * 
     * @param templatePath template path
     * @return access right validation builder
     */
    ValidateAccessRights cannotCreatePage(String templatePath);

    /**
     * Checks if delete access to pages is granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights canDeletePage();

    /**
     * Checks if delete access to pages is NOT granted.
     * 
     * @return access right validation builder
     */
    ValidateAccessRights cannotDeletePage();

    /**
     * Checks if the specified rights are correctly set.
     * 
     * @return access right validation builder
     */
    String validate();

}
