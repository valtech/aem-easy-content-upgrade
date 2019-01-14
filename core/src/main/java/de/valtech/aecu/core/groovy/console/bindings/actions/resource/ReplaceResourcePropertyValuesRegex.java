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
package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Replaces strings via regex in resource properties.
 * 
 * @author Roland Gruber
 */
public class ReplaceResourcePropertyValuesRegex extends ReplaceResourcePropertyValues {

    private String searchRegex;
    private String replacement;
    private Pattern searchPattern;

    /**
     * Constructor
     * 
     * @param searchRegex   search regex
     * @param replacement   replacement string with optional regex group wildcards
     * @param propertyNames property names
     */
    public ReplaceResourcePropertyValuesRegex(String searchRegex, String replacement, List<String> propertyNames) {
        super(searchRegex, replacement, propertyNames);
        this.searchRegex = searchRegex;
        this.replacement = replacement;
        this.searchPattern = Pattern.compile(searchRegex);
    }

    @Override
    protected boolean valueMatches(String value) {
        return searchPattern.matcher(value).find();
    }

    @Override
    protected String getNewValue(String propertyValue) {
        return propertyValue.replaceAll(searchRegex, replacement);
    }

}
