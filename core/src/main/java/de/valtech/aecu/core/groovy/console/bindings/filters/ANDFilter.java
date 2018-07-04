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
package de.valtech.aecu.core.groovy.console.bindings.filters;

import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Roxana Muresan
 */
public class ANDFilter implements FilterBy {

    private List<FilterBy> filters;


    public ANDFilter(@Nonnull List<FilterBy> filters) {
        this.filters = filters;
    }

    @Override
    public boolean filter(@Nonnull Resource resource) {
        boolean foundFalse = filters
                .parallelStream()
                .filter(f -> f.filter(resource) == false)
                .findAny().isPresent();
        return !foundFalse;

    }
}
