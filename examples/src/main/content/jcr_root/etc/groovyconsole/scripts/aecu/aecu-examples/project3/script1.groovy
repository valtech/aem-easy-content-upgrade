println aecu
        .contentUpgradeBuilder()
        // collectors
        .forDescendantResourcesOf("/content/we-retail/ca/en/experience")
        // filters
        .filterByProperty("jcr:primaryType", "cq:Page")
        // actions
        .printPath()
        .doMoveResourceToPathRegex("/content/we-retail/([\\w-]+)/([\\w-]+)/experience/([\\w-]+)", "/content/we-retail/it/it")
        .run()