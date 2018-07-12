def conditionMap_Hero1 = [:]
conditionMap_Hero1['sling:resourceType'] = "weretail/components/content/heroimage"
def conditionMap_Hero2 = [:]
conditionMap_Hero2['fileReference'] = "/content/dam/we-retail/en/activities/running/fitness-woman.jpg"

def conditionMap_Page = [:]
conditionMap_Page['jcr:primaryType'] = "cq:PageContent"


println aecu.contentUpgradeBuilder()
// traversers
        .forResources((String[]) ["/content/we-retail/ca/en/jcr:content", "/content/we-retail/ca/en/experience/jcr:content"]) //,"/invalid/path"
        .forChildResourcesOf("/content/we-retail/ca/en/men")
        .forDescendantResourcesOf("/content/we-retail/ca/en/women")
// filters
        .filterByProperties(conditionMap_Page)
// actions
        .doSetStringProperty("newStringProperty", "aecu test with conditionMap_Page")
        .doSetBooleanProperty("newBooleanProperty", true)
        .doSetIntegerProperty("newStringProperty", 123)
        .doRenameProperty("newStringProperty", "delete_me_later")
//    .removeProperty("delete_me_later")
        .run()




def complexFilter = new ORFilter(
        [new FilterByProperties(conditionMap_Page),
         new ANDFilter([
                 new FilterByProperties(conditionMap_Hero1),
                 new FilterByProperties(conditionMap_Hero2)
         ])
        ])

println aecu.contentUpgradeBuilder()
// traversers
        .forResources((String[]) ["/content/we-retail/ca/en/jcr:content", "/content/we-retail/ca/en/experience/jcr:content"]) //,"/invalid/path"
        .forChildResourcesOf("/content/we-retail/ca/en/men")
        .forDescendantResourcesOf("/content/we-retail/ca/en/women")
// filters
        .filterWith(complexFilter)
// actions
        .doSetProperty("newStringProperty", "aecu test with conditionMap_Hero")
        .doSetBooleanProperty("newBooleanProperty", false)
        .doSetIntegerProperty("newStringProperty", 789)
        .doRenameProperty("newStringProperty", "delete_me_later")
//    .removeProperty("delete_me_later")
        .run()