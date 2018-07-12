def conditionMap_Hero1 = [:]
conditionMap_Hero1['sling:resourceType'] = "weretail/components/content/heroimage"
def conditionMap_Hero2 = [:]
conditionMap_Hero2['fileReference'] = "/content/dam/we-retail/en/activities/running/fitness-woman.jpg"

def conditionMap_Page = [:]
conditionMap_Page['jcr:primaryType'] = "cq:PageContent"


println aecu.contentUpgradeBuilder()
// traversers
        .forResources((String[])["/content/we-retail/ca/en/jcr:content", "/content/we-retail/ca/en/experience/jcr:content"]) //,"/invalid/path"
        .forChildResourcesOf("/content/we-retail/ca/en/men")
        .forDescendantResourcesOf("/content/we-retail/ca/en/women")
// filters
        .filterByProperties(conditionMap_Page)
// actions
        .doSetStringProperty("newStringProperty1", "aecu test with conditionMap_Page")
        .doSetBooleanProperty("newBooleanProperty1", true)
        .doSetIntegerProperty("newIntegerProperty1", 123)
        .doRenameProperty("newStringProperty1", "delete_me_later")
//    .removeProperty("delete_me_later")
        .dryRun()




def complexFilter =  new ORFilter(
        [ new FilterByProperties(conditionMap_Page),
          new ANDFilter( [
                  new FilterByProperties(conditionMap_Hero1),
                  new FilterByProperties(conditionMap_Hero2)
          ] )
        ])

println aecu.contentUpgradeBuilder()
// traversers
        .forResources((String[])["/content/we-retail/ca/en/jcr:content", "/content/we-retail/ca/en/experience/jcr:content"]) //,"/invalid/path"
        .forChildResourcesOf("/content/we-retail/ca/en/men")
        .forDescendantResourcesOf("/content/we-retail/ca/en/women")
// filters
        .filterWith(complexFilter)
// actions
        .doSetStringProperty("newStringProperty2", "aecu test with conditionMap_Hero")
        .doSetBooleanProperty("newBooleanProperty2", false)
        .doSetIntegerProperty("newIntegerProperty2", 789)
        .doRenameProperty("newStringProperty2", "delete_me_later2")
//    .removeProperty("delete_me_later")
        .dryRun()