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
package de.valtech.aecu.api.groovy.console.bindings.accessrights;

/**
 * Result for an access right validation.
 * 
 * @author Roland Gruber
 */
public class ValidationResult {

    private boolean hasErrors;
    private boolean hasWarnings;
    private String message;

    /**
     * Constructor.
     * 
     * @param hasErrors   errors occured
     * @param hasWarnings warnings occured
     * @param message     message text in case of errors/warnings
     */
    public ValidationResult(boolean hasErrors, boolean hasWarnings, String message) {
        this.hasErrors = hasErrors;
        this.hasWarnings = hasWarnings;
        this.message = message;
    }

    /**
     * Returns if errors occured.
     * 
     * @return errors occured
     */
    public boolean hasErrors() {
        return hasErrors;
    }

    /**
     * Returns if warnings occured.
     * 
     * @return warnings occured
     */
    public boolean hasWarnings() {
        return hasWarnings;
    }

    /**
     * Returns if validation was successful.
     * 
     * @return successful
     */
    public boolean isSuccessful() {
        return !hasErrors && !hasWarnings;
    }

    /**
     * Returns the error/warning message if any.
     * 
     * @return message
     */
    public String getMessage() {
        return message;
    }

}
