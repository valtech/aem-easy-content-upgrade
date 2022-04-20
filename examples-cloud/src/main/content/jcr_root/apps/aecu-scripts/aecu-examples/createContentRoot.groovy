def mapContent = [
  "sling:resourceType": "core/wcm/components/page/v2/page"
]

aecu.contentUpgradeBuilder()
  .forResources((String[]) ["/content/"])
  .doDeleteResource((String[]) ["aecu-example"])
  .doCreateResource("aecu-example", "cq:Page")
  .doCreateResource("jcr:content", "cq:PageContent", mapContent, "aecu-example")
  .run()
