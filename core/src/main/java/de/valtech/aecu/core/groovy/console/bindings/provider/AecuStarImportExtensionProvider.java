package de.valtech.aecu.core.groovy.console.bindings.provider;

import com.icfolson.aem.groovy.console.api.StarImportExtensionProvider;
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants;
import org.osgi.service.component.annotations.Component;

import java.util.Set;

@Component(immediate = true)
public class AecuStarImportExtensionProvider implements StarImportExtensionProvider {

    @Override
    public Set<String> getStarImports() {
        Set<String> imports = GroovyConsoleConstants.DEFAULT_STAR_IMPORTS;
        imports.add("de.valtech.aecu.core.groovy.console.bindings.filters");
        return imports;
    }
}
