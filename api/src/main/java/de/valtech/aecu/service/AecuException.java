/*
 *  Copyright 2018 Valtech GmbH
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package de.valtech.aecu.service;

/**
 * Thrown when the AECU service faces an error.
 * 
 * @author Roland Gruber
 */
public class AecuException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param message error message
     * @param e original exception
     */
    public AecuException(String message, Throwable e) {
        super(message, e);
    }
    
    /**
     * Constructor
     * 
     * @param message error message
     */
    public AecuException(String message) {
        super(message);
    }
    
}
