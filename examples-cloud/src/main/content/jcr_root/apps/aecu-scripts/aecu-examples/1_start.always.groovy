println "AECU migration started"

aecu.contentUpgradeBuilder()
  .forChildResourcesOf("/content/")
  .printPath()
  .run()
