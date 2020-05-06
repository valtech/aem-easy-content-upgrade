/*
 * Copyright 2018 - 2019 Valtech GmbH
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
 * Groovy Console Bindings for AEM Simple Content Update. This provides the "aecu" binding variable.
 * 
 * @author Roxana Muresan
 */
@ProviderType
public interface AecuBinding {

    static final String BINDING_NAME="aecu";

    /**
     * Returns a content upgrade builder. This is the starting point for the migrations.
     * 
     * @return builder
     */
    ContentUpgrade contentUpgradeBuilder();

    /**
     * Returns an access right validator. This is the starting point for all access right checks.
     * 
     * @return access right validator
     */
    ValidateAccessRights validateAccessRights();

}
