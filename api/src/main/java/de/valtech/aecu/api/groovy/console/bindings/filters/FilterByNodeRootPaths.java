package de.valtech.aecu.api.groovy.console.bindings.filters;

import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Filters resources by node root paths. Only resource matching or starting with the root paths are accepted
 *
 * @author Dries Vanbilloen
 */
public class FilterByNodeRootPaths implements FilterBy {

    private List<String> rootPaths;

    /**
     * Constructor
     *
     * @param rootPaths list of root paths
     */
    public FilterByNodeRootPaths(List<String> rootPaths) {
        this.rootPaths = rootPaths;
    }

    @Override
    public boolean filter(@Nonnull Resource resource, @Nonnull StringBuilder output) {
        final String currentPath = resource.getPath();
        return rootPaths.stream()
                .anyMatch(rootPath -> rootPath.equals(currentPath) || (currentPath + "/").startsWith(rootPath));
    }

}
