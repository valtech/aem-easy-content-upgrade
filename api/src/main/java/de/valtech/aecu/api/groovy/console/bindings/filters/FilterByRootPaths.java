package de.valtech.aecu.api.groovy.console.bindings.filters;

import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.List;

public class FilterByRootPaths implements FilterBy {

    private List<String> rootPaths;

    public FilterByRootPaths(List<String> rootPaths) {
        this.rootPaths = rootPaths;
    }

    @Override
    public boolean filter(@Nonnull Resource resource, @Nonnull StringBuilder output) {
        final String currentPath = resource.getPath();
        return rootPaths.stream()
                .anyMatch(currentPath::contains);
    }

}
