println "simpleContentUpdate is $simpleContentUpdate"

def conditionMap = [:]
//conditionMap['sling:resourceType'] = "weretail/components/content/heroimage"
//conditionMap['jcr:primaryType'] = "cq:Page"
//conditionMap['fileReference'] = "/content/dam/we-retail/en/experiences/arctic-surfing-in-lofoten/surfer-wave-01.jpg"

simpleContentUpdate
// test filter methods
//    .forResources((String[])["/content/we-retail/ca/en/jcr:content","/invalid/path", "/content/we-retail/ca/en/experience/jcr:content"])
//    .forChildResourcesOf("/content/we-retail/ca/en/experience")
//    .forDescendantResourcesOf("/content/we-retail/ca/en/experience")
        .forChildResourcesOfWithProperties("/content/we-retail/ca/en/experience", conditionMap)
//    .forDescendantResourcesOfWithProperties("/content/we-retail/ca/en", conditionMap)

// print filter results in log file
        .printFoundResources()

// test editor methods
//    .setProperty("newProperty", "added by aecu")
//    .renameProperty("newProperty", "delete_me_later")
//    .removeProperty("delete_me_later")
//    .remove()