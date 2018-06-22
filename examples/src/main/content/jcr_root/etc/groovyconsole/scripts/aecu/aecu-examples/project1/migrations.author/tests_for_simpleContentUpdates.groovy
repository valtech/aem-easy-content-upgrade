println "simpleContentUpdate is $aecu"

def conditionMapHero = [:]
conditionMapHero['sling:resourceType'] = "weretail/components/content/heroimage"
conditionMapHero['fileReference'] = "/content/dam/we-retail/en/activities/running/fitness-woman.jpg"

def conditionMapPage = [:]
conditionMapPage['jcr:primaryType'] = "cq:PageContent"


aecu
// test filter methods
        .forResources((String[])["/content/we-retail/ca/en/jcr:content","/invalid/path", "/content/we-retail/ca/en/experience/jcr:content"])
        .forChildResourcesOf("/content/we-retail/ca/en/women")
        .forDescendantResourcesOf("/content/we-retail/ca/en/men")
        .forChildResourcesOfWithProperties("/content/we-retail/ca/en/products", conditionMapPage)
        .forDescendantResourcesOfWithProperties("/content/we-retail/ca/en/equipment", conditionMapHero)
        .forDescendantResourcesOfWithProperties("/content/we-retail/ca/en/women", conditionMapHero)

// test editor methods
        .doSetProperty("newProperty", "added by aecu")
        .doRenameProperty("newProperty", "delete_me_later")
//    .removeProperty("delete_me_later")
        .apply()