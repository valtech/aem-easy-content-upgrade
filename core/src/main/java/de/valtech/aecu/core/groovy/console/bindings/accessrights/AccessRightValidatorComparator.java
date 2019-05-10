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
package de.valtech.aecu.core.groovy.console.bindings.accessrights;

import java.util.Comparator;

import de.valtech.aecu.api.groovy.console.bindings.accessrights.AccessRightValidator;

/**
 * Comparator to sort access right validators.
 * 
 * @author Roland Gruber
 */
public class AccessRightValidatorComparator implements Comparator<AccessRightValidator> {

    @Override
    public int compare(AccessRightValidator o1, AccessRightValidator o2) {
        if (!o1.getAuthorizableId().equals(o2.getAuthorizableId())) {
            return o1.getAuthorizableId().compareTo(o2.getAuthorizableId());
        }
        if (!o1.getResource().getPath().equals(o2.getResource().getPath())) {
            return o1.getResource().getPath().compareTo(o2.getResource().getPath());
        }
        return o1.getLabel().compareTo(o2.getLabel());
    }

}
