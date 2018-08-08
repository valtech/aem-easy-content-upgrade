/* Demo #1:
   We want to replace responsivegrid pars with a separate resourceType while keeping the responsive grid as it is
   We assume the /apps/<prj> part will be deployed separately with the release of the project
*/

/* Step 1: replace references in /content/<prj> */
def conditionMap_resourceType = [:]
conditionMap_resourceType['sling:resourceType'] = "wcm/foundation/components/responsivegrid"

println aecu.contentUpgradeBuilder()
// traversers
        .forDescendantResourcesOf("/content/we-retail")
// filters
        .filterByProperties(conditionMap_resourceType)
        .filterByNodeNameRegex("par.*")
// actions
        .printPath()
        .doSetProperty("sling:resourceType", "wcm/foundation/components/custom_par")
        .dryRun()

/* Step 2: add new component references in /etc/designs/<prj> without overwriting the responsivegrid references */
println aecu.contentUpgradeBuilder()
// traversers
        .forDescendantResourcesOf("/etc/designs/we-retail")
        .forDescendantResourcesOf("/etc/designs/we-retail-client-app")
        .forDescendantResourcesOf("/etc/designs/we-retail-screens")
        .forDescendantResourcesOf("/etc/designs/we-unlimited-app")
// filters
        .filterByMultiValuePropContains("components", (String[])["/libs/wcm/foundation/components/responsivegrid"])
// actions
        .printPath()
        .doAddValuesToMultiValueProperty("components", (String[])["/libs/wcm/foundation/components/responsivegrid_par"])
        .dryRun()