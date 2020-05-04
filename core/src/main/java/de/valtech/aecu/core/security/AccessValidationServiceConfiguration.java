package de.valtech.aecu.core.security;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configuration for access validation service.
 *
 * @author Roland Gruber
 */
@ObjectClassDefinition(name = "AECU Access Validation Service Configuration")
@ProviderType
public interface AccessValidationServiceConfiguration {

    @AttributeDefinition(type = AttributeType.STRING, cardinality = 1000, name = "Read access",
            description = "The configured group and user names have read access to AECU. Admin user always has access.")
    String[] readers();

    @AttributeDefinition(type = AttributeType.STRING, cardinality = 1000, name = "Execute access",
            description = "The configured group and user names have execute access to AECU. Admin user always has access.")
    String[] executers();

}
