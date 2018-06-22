def conditionMap_Hero1 = [:]
conditionMap_Hero1['sling:resourceType'] = "weretail/components/content/heroimage"
def conditionMap_Hero2 = [:]
conditionMap_Hero2['fileReference'] = "/content/dam/we-retail/en/activities/running/fitness-woman.jpg"

def conditionMap_Page = [:]
conditionMap_Page['jcr:primaryType'] = "cq:PageContent"


def return1 = aecu.getNewMigration()
// traversers
        .forResources((String[])["/content/we-retail/ca/en/jcr:content", "/content/we-retail/ca/en/experience/jcr:content"]) //,"/invalid/path"
        .forChildResourcesOf("/content/we-retail/ca/en/men")
        .forDescendantResourcesOf("/content/we-retail/ca/en/women")
// filters
        .filterByProperties(conditionMap_Page)
// actions
        .doSetProperty("newProperty", "aecu test with conditionMap_Page")
        .doRenameProperty("newProperty", "delete_me_later")
//    .removeProperty("delete_me_later")
        .apply()
println "$return1"


def complexFilter = new ORFilter(
        [ new FilterByProperties(conditionMap_Page),
          new ANDFilter( [
                  new FilterByProperties(conditionMap_Hero1),
                  new FilterByProperties(conditionMap_Hero2)
          ] )
        ])

def return2 = aecu.getNewMigration()
// traversers
        .forResources((String[])["/content/we-retail/ca/en/jcr:content", "/content/we-retail/ca/en/experience/jcr:content"]) //,"/invalid/path"
        .forChildResourcesOf("/content/we-retail/ca/en/men")
        .forDescendantResourcesOf("/content/we-retail/ca/en/women")
// filters
        .filterWith(complexFilter)
// actions
        .doSetProperty("newProperty", "aecu test with conditionMap_Hero")
        .doRenameProperty("newProperty", "delete_me_later")
//    .removeProperty("delete_me_later")
        .apply()
println "$return2"

return return1 + return2