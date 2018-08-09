/**
 * Updating the value of a property for all components found under a certain path
 */
def conditionMap_resourceType = [:]
conditionMap_resourceType['sling:resourceType'] = "weretail/components/content/heroimage"

println aecu.contentUpgradeBuilder()
// traversers
        .forDescendantResourcesOf("/content/we-retail/ca/en")
// filters
        .filterByProperties(conditionMap_resourceType)
// actions
        .printPath()
        .doSetProperty("useFullWidth", false)
        .dryRun()
        //.run()