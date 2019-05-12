
aecu.contentUpgradeBuilder()
    // collectors
    .forChildResourcesOf("/content/we-retail/ca/en")
    // filters
    .filterByNodeName("equipment")
    // actions
    .printPath()
    .doMoveResourceToRelativePath("../experience")
    .run()