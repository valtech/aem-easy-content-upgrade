def propertiesMap = [:]
propertiesMap['test_with_props'] = "Test with properties"
def propertiesMap2 = [:]
propertiesMap2['test_with_props_and_relative'] = "Test with properties and relative path"
propertiesMap2['another_property'] = "Another property value here"

aecu.contentUpgradeBuilder()
        .forResources((String[])["/content/we-retail/ca/en/experience/jcr:content"])
        .doCreateResource("parent", "nt:unstructured")
        .doCreateResource("parent_props", "nt:unstructured", propertiesMap)
        .doCreateResource("relative_path", "nt:unstructured", "parent")
        .doCreateResource("relative_path_props", "nt:unstructured", propertiesMap2, "parent/relative_path")
        .dryRun()