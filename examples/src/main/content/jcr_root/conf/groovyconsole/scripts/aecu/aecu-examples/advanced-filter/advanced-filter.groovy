def complexFilter =  new ORFilter(
    [ new FilterByProperty("sling:resourceType", "weretail/components/content/heroimage"),
      new ANDFilter( [
              new FilterByProperty("sling:resourceType", "weretail/components/content/image"),
              new FilterByNodeName("image")
      ] )
    ])

aecu.contentUpgradeBuilder()
.forDescendantResourcesOf("/content/we-retail/us/en")
.filterWith(complexFilter)
.printPath()
.run()
