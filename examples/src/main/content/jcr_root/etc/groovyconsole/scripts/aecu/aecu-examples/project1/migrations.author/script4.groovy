
println aecu
        .contentUpgradeBuilder()
        // traversers
        .forDescendantResourcesOf("/content/we-retail")
        // filters
        .filterByNodeName("equipment")
        // actions
        .printPath()
        .doMoveResourceToRelativePath("../experience")
        .run()