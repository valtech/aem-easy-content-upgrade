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

import com.drew.lang.annotations.NotNull;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;

import java.util.HashMap;
import java.util.Map;

public class FilterByProperties implements FilterBy {

    private Map<String, String> conditionProperties = new HashMap<>();

    public FilterByProperties(@NotNull Map<String, String> conditionProperties) {
        this.conditionProperties.putAll(conditionProperties);
    }

    @Override
    public boolean filter(Resource resource) {
        if (resource != null) {
            ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
            for (String key : conditionProperties.keySet()) {
                String conditionValue = conditionProperties.get(key);
                String propertiesValue = properties.get(key, String.class);

                if ((conditionValue == null && propertiesValue != null) || (conditionValue != null && !conditionValue.equals(propertiesValue))) {
                    return false;
                }
            }
        }
        return true;
    }
}

