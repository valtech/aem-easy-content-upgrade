/**
 * Moving pages
 */
println aecu.contentUpgradeBuilder()
println aecu.contentUpgradeBuilder()
// traversers
        .forDescendantResourcesOf("/content/we-retail")
// filters
        .filterByNodeName("equipment")
// actions
        .printPath()
        .doMoveResourceToRelativePath("../experience")
        .dryRun()
        //.run()