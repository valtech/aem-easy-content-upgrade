# AEM Easy Content Upgrade (AECU)

AECU simplifies content migrations by executing migration scripts during package installation. It is built on top of [Groovy Console](https://github.com/OlsonDigital/aem-groovy-console).


Features:

* GUI to run scripts and see history of runs
* Run mode support
* Fallback scripts in case of errors
* Extension of Groovy Console bindings
* Service API
* Health Checks

The tool was presented at [adaptTo() conference in Berlin](https://adapt.to/2018/en/schedule/aem-easy-content-upgrade.html). You can get the slides there and also see the video here:

[![AECU @ adaptTo() 2018](https://img.youtube.com/vi/ZPEJ_cbzBoE/0.jpg)](https://www.youtube.com/watch?v=ZPEJ_cbzBoE "AECU @ adaptTo() 2018")

Table of contents
1. [Requirements](#requirements)
2. [Installation](#installation)
3. [File and Folder Structure](#structure)
4. [Execution of Migration Scripts](#execution)
    1. [Install Hook](#installHook)
    2. [Manual Execution](#manualExecution)
5. [History of Past Runs](#history)
6. [Extension to Groovy Console](#groovy)
    1. [Collect Options](#binding_collect)
    2. [Filter Options](#binding_filter)
    3. [Execute Options](#binding_execute)
    4. [Run Options](#binding_run)
7. [JMX Interface](#jmx)
8. [Health Checks](#healthchecks)
9. [API Documentation](#api)
10. [License](#license)
11. [Changelog](#changelog)
12. [Developers](#developers)


<a name="requirements"></a>

# Requirements

AECU requires Java 8 and AEM 6.3 or above. Groovy Console can be installed manually if [bundle install](#bundleInstall) is not used.

| AEM Version   | Groovy Console | AECU |
| ------------- | -------------- | ---- |
| 6.3           | 12.x           | 1.x  |
| 6.4           | 12.x           | 1.x  |

**Please note that Groovy Console 13 is not yet supported!**

<a name="installation"></a>

# Installation

You can download the package from [Maven Central](http://repo1.maven.org/maven2/de/valtech/aecu/aecu.ui.apps/) or our [releases section](https://github.com/valtech/aem-easy-content-upgrade/releases). The aecu.ui.apps package will install the AECU software. It requires that you installed [Groovy Console](https://github.com/OlsonDigital/aem-groovy-console) before.

```xml
        <dependency>
            <groupId>de.valtech.aecu</groupId>
            <artifactId>aecu.ui.apps</artifactId>
            <version>LATEST</version>
            <type>zip</type>
        </dependency>
```


<a name="bundleInstall"></a>

## Bundle Installation

To simplify installation we provide a bundle package that already includes the Groovy Console. This makes sure there are no compatibility issues.
The package is also available on [Maven Central](http://repo1.maven.org/maven2/de/valtech/aecu/aecu.bundle/) or our [releases section](https://github.com/valtech/aem-easy-content-upgrade/releases).

```xml
        <dependency>
            <groupId>de.valtech.aecu</groupId>
            <artifactId>aecu.bundle</artifactId>
            <version>LATEST</version>
            <type>zip</type>
        </dependency>
```


<a name="structure"></a>

# File and Folder Structure

All migration scripts need to be located in /etc/groovyconsole/scripts/aecu. There you can create
an unlimited number of folders and files. E.g. organize your files by project or deployment.
The content of the scripts is plain Groovy code that can be run via [Groovy Console](https://github.com/OlsonDigital/aem-groovy-console).

<img src="docs/images/files.png">

There are just a few naming conventions:

* Run modes: folders can contain run modes to limit the execution to a specific target environment. E.g. some scripts are for author only or for your local dev environment.
* Always selector: if a script name ends with ".always.groovy" then it will be executed by
[install hook](#installHook) on each package installation. There will be no more check if this script
was already executed before.
* Fallback selector: if a script name ends with ".fallback.groovy" then it will be executed only if
the corresponding script failed with an exception. E.g. if there is "script.groovy" and "script.fallback.groovy" then the fallback script only gets executed if "script.groovy" fails.
* Reserved file names
    * fallback.groovy: optional directory level fallback script. This will be executed if a script fails and no script specific fallback script is provided.

<a name="execution"></a>

# Execution of Migration Scripts

<a name="installHook"></a>

## Install Hook

This is the preferred method to execute your scripts. It allows to run them without any user interaction. Just package them with a content package and do a regular deployment.

You can add the install hook by adding de.valtech.aecu.core.installhook.AecuInstallHook as a hook to your package properties. The AECU package and Groovy Console need to be installed beforehand.

```xml
<plugin>
    <groupId>com.day.jcr.vault</groupId>
    <artifactId>content-package-maven-plugin</artifactId>
    <extensions>true</extensions>
    <configuration>
        <filterSource>src/main/content/META-INF/vault/filter.xml</filterSource>
        <verbose>true</verbose>
        <failOnError>true</failOnError>
        <group>Valtech</group>
        <properties>
            <installhook.aecu.class>de.valtech.aecu.core.installhook.AecuInstallHook</installhook.aecu.class>
        </properties>
    </configuration>
</plugin>
```

<a name="manualExecution"></a>

## Manual Execution

Manual script execution is useful in case you want to manually rerun a script (e.g. because it failed before). You can find the execute feature in AECU's tools menu.

<img src="docs/images/tools.png">

Execution is done in two simple steps:

1. Select the base path and run the search. This will show a list of runnable scripts.
2. Run all scripts in batch or just single ones. If you run all you can change the order before (drag and drop with marker at the right).

Once execution is done you will see if the script(s) succeeded. Click on the history link to see the details.

<img src="docs/images/run.png">

<a name="history"></a>

# History of Past Runs

You can find the history in AECU's tools menu.

<img src="docs/images/tools.png">

The history shows all runs that were executed via package install hook, manual run and JMX. It will not display scripts that were executed directly via Groovy Console.

<img src="docs/images/historyOverview.png">

You can click on any run to see the full details. This will show the status for each script. You can also see the output of all scripts.

<img src="docs/images/historyDetails.png">

## Search History

AECU maintains a full-text search index for the history entries. You can search for script names and their output.

Simply click on the magnifying glass in header to open the search bar:

<img src="docs/images/fulltext1.png">

Now you can enter a search term and will see the runs that contain this text. Click on the link to see the full history entry.

<img src="docs/images/fulltext2.png">

<a name="groovy"></a>

# Extension to Groovy Console

AECU adds its own binding to Groovy Console. You can reach it using "aecu" in your script. This provides methods to perform common tasks like property modification or node deletion.

It follows a collect, filter, execute process.

<a name="binding_collect"></a>

## Collect Options
In the collect phase you define which nodes should be checked for a migration.

* forResources(String[] paths): use the given paths without any subnodes
* forChildResourcesOf(String path): use all direct childs of the given path (but no grandchilds)
* forDescendantResourcesOf(String path): use the whole subtree under this path excluding the parent root node
* forResourcesInSubtree(String path): use the whole subtree under this path including the parent root node
* forResourcesBySql2Query(String query): executes the query and applies actions on found resources

You can call these methods multiple times and combine them. They will be merged together.

Example:

```java
println aecu.contentUpgradeBuilder()
        .forResources((String[])["/content/we-retail/ca/en"])
        .forChildResourcesOf("/content/we-retail/us/en")
        .forDescendantResourcesOf("/content/we-retail/us/en/experience")
        .forResourcesInSubtree("/content/we-retail/us/en/experience")
        .forResourcesBySql2Query("SELECT * FROM [cq:Page] AS s WHERE ISDESCENDANTNODE(s,'/content/we-retail/us/en/experience')")
        .doSetProperty("name", "value")
        .run()
```

<a name="binding_filter"></a>

## Filter Options
These methods can be used to filter the nodes that were collected above. Multiple filters can be applied for one run.

### Filter by Properties

Filters the resources by property values.

* filterByHasProperty: matches all nodes that have the given property. The value of the property is not relevant.
* filterByProperty: matches all nodes that have the given attribute value. Filter does not match if attribute is not present. By using a value of "null" you can search if an attribute is not present.
* filterByProperties: use this to filter by a list of property values (e.g. sling:resourceType). All properties in the map are required to to match. Filter does not match if attribute does not exist.
* filterByMultiValuePropContains: checks if all condition values are contained in the defined attribute. Filter does not match if attribute does not exist.

```java
filterByHasProperty(String name)
filterByProperty(String name, Object value)
filterByProperties(Map<String, String> properties)
filterByMultiValuePropContains(String name,  Object[] conditionValues)
```

Example:

```java
def conditionMap = [:]
conditionMap["sling:resourceType"] = "weretail/components/structure/page"

println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByHasProperty("myProperty")
        .filterByProperty("sling:resourceType", "wcm/foundation/components/responsivegrid")
        .filterByProperties(conditionMap)
        .filterByMultiValuePropContains("myAttribute", ["value"] as String[])
        .doSetProperty("name", "value")
        .run()
```

### Filter by Node Name

You can also filter nodes by their name.

* filterByNodeName(String name): process only nodes which have this exact name
* filterByNodeNameRegex(String regex): process nodes that have a name that matches the given regular expression

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByNodeName("jcr:content")
        .filterByNodeNameRegex("jcr.*")
        .doSetProperty("name", "value")
        .run()
```

### Filter by Node Path

Nodes can also be filtered by their path using a regular expression.

* filterByPathRegex(String regex): process nodes whose path matches the given regular expression

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByPathRegex(".*/jcr:content/.*")
        .doSetProperty("name", "value")
        .run()
```


### Combine Multiple Filters
You can combine filters with AND and OR to build more complex filters.

```java
def conditionMap_type = [:]
conditionMap_type['sling:resourceType'] = "weretail/components/content/heroimage"
def conditionMap_file = [:]
conditionMap_file['fileReference'] = "/content/dam/we-retail/en/activities/running/fitness-woman.jpg"
def conditionMap_page = [:]
conditionMap_page['jcr:primaryType'] = "cq:PageContent"

def complexFilter =  new ORFilter(
        [ new FilterByProperties(conditionMap_page),
          new ANDFilter( [
                  new FilterByProperties(conditionMap_type),
                  new FilterByProperties(conditionMap_file)
          ] )
        ])

println aecu.contentUpgradeBuilder()
        .forDescendantResourcesOf("/content/we-retail/ca/en", false)
        .filterWith(complexFilter)
        .doSetProperty("name", "value")
        .run()        
```

<a name="binding_execute"></a>

## Execute Options

### Update Single-value Properies

* doSetProperty(String name, Object value): sets the given property to the value. Any existing value is overwritten.
* doDeleteProperty(String name): removes the property with the given name if existing.
* doRenameProperty(String oldName, String newName): renames the given property if existing. If the new property name already exists it will be overwritten.

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByNodeName("jcr:content")
        .doSetProperty("name", "value")
        .doDeleteProperty("nameToDelete")
        .doRenameProperty("oldName", "newName")
        .run()
```

### Update Multi-value Properties

* doAddValuesToMultiValueProperty(String name, String[] values): adds the list of values to a property. The property is created if it does not yet exist.
* doRemoveValuesOfMultiValueProperty(String name, String[] values): removes the list of values from a given property. 
* doReplaceValuesOfMultiValueProperty(String name, String[] oldValues, String[] newValues): removes the old values and adds the new values in a given property. 

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByNodeName("jcr:content")
        .doAddValuesToMultiValueProperty("name", (String[])["value1", "value2"])
        .doRemoveValuesOfMultiValueProperty("name", (String[])["value1", "value2"])
        .doReplaceValuesOfMultiValueProperty("name", (String[])["old1", "old2"], (String[])["new1", "new2"])
        .run()
```

### Copy and Move Properties

This will copy or move a property to a subnode. You can also change the property name.

* doCopyPropertyToRelativePath(String name, String newName, String relativeResourcePath): copy the property to the given path under the new name.
* doMovePropertyToRelativePath(String name, String newName, String relativeResourcePath): move the property to the given path under the new name.

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByNodeName("jcr:content")
        .doCopyPropertyToRelativePath("name", "newName", "subnode")
        .doMovePropertyToRelativePath("name", "newName", "subnode")
        .run()
```

### Replace Property Content
You can replace the content of String properties. This also supports multi-value properties.

* doReplaceValueInAllProperties(String oldValue, String newValue): replaces the substring "oldValue" with "newValue". Applies to all String properties
* doReplaceValueInProperties(String oldValue, String newValue, String[] propertyNames): replaces the substring "oldValue" with "newValue". Applies to all specified String properties
* doReplaceValueInAllPropertiesRegex(String searchRegex, String replacement): checks if the property value(s) match the search pattern and replaces it with "replacement". Applies to all String properties. You can use group references such as $1 (hint: "$" needs to be escaped with "\" in Groovy).
* doReplaceValueInPropertiesRegex(String searchRegex, String replacement, String[] propertyNames): checks if the property value(s) match the search pattern and replaces it with "replacement".  Applies to specified String properties. You can use group references such as $1 (hint: "$" needs to be escaped with "\" in Groovy).

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByNodeName("jcr:content")
        .doReplaceValueInAllProperties("old", "new")
        .doReplaceValueInProperties("old", "new", (String[]) ["propertyName1", "propertyName2"])
        .doReplaceValueInAllPropertiesRegex("/content/([^/]+)/(.*)", "/content/newSub/\$2")
        .doReplaceValueInPropertiesRegex("/content/([^/]+)/(.*)", "/content/newSub/\$2", (String[]) ["propertyName1", "propertyName2"])
        .run()
```

### Copy and Move Nodes

The matching nodes can be copied/moved to a new location. You can use ".." if you want to step back in path.

* doRename(String newName): renames the resource to the given name
* doCopyResourceToRelativePath(String relativePath): copies the node to the given target path
* doMoveResourceToRelativePath(String relativePath): moves the node to the given target path
* doMoveResourceToPathRegex(String matchPattern, String replacementExpr): moves a resource if its path matches the pattern to the target path obtained by applying the replacement expression. You can use group references such as $1 (hint: "$" needs to be escaped with "\" in Groovy).

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByNodeName("jcr:content")
        .doRename("newNodeName")
        .doCopyResourceToRelativePath("subNode")
        .doCopyResourceToRelativePath("../subNode")
        .doMoveResourceToRelativePath("subNode")
        .doMoveResourceToPathRegex("/content/we-retail/(\\w+)/(\\w+)/(\\w+)", "/content/somewhere/\$1/and/\$2")
        .run()
```

### Delete Nodes

You can delete all nodes that match your collection and filter.

* doDeleteResource(): deletes the matching nodes

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByNodeName("jcr:content")
        .doDeleteResource()
        .run()
```

#### Node (De)activation

Please note that this is for non-page resources such as commerce products. For page level (de)activation there are [separate methods](#binding_page_replication).

* doActivateResource(): activates the current resource
* doDeactivateResource(): deactivates the current resource

### Page Actions

AECU can run actions on the page that contains a filtered resource. This is e.g. helpful if you filter by page resource type.

Please note that there is no check for duplicate actions. If you run a page action for two resources in the same page then the action will be executed twice.

<a name="binding_page_replication"></a>

#### Page (De)activation

* doActivateContainingPage(): activates the page that contains the current resource
* doDeactivateContainingPage(): deactivates the page that contains the current resource
* doTreeActivateContainingPage(): activates the page that contains the current resource AND all subpages
* doTreeActivateContainingPage(boolean skipDeactivated): activates the page that contains the current resource AND all subpages. If "skipDeactivated" is set to true then deactivated pages will be ignored and not activated.

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByProperty("sling:resourceType", "weretail/components/structure/page")
        .doActivateContainingPage()
        .doDeactivateContainingPage()
        .doTreeActivateContainingPage()
        .doTreeActivateContainingPage(true)
        .run()
```

#### Page Deletion

* doDeleteContainingPage(): deletes the page (incl. subpages) that contains the current resource

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByProperty("sling:resourceType", "weretail/components/structure/page")
        .doDeleteContainingPage()
        .run()
```

#### Page Tagging

Tags can be specified by Id (e.g. "properties:style/color") or path (e.g. "/etc/tags/properties/orientation/landscape").

* doAddTagsToContainingPage(): adds the given tags to the page
* doSetTagsForContainingPage(): sets the page's tags. This will delete any tags that were assigned but are not part of the new tag list. An empty list of tags will delete all tags.
* doRemoveTagsFromContainingPage(): removes the given tags from the page

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByProperty("sling:resourceType", "weretail/components/structure/page")
        .doAddTagsToContainingPage("properties:style/color", "/etc/tags/properties/orientation/landscape")
        .doSetTagsForContainingPage("properties:style/color", "/etc/tags/properties/orientation/landscape")
        .doRemoveTagsFromContainingPage("properties:style/color", "/etc/tags/properties/orientation/landscape")
        .run()
```

#### Validate Page Rendering

AECU can do some basic tests if pages render correctly. You can use this to verify a migration run.

* doCheckPageRendering(): checks if page renders with status code 200
* doCheckPageRendering(int code): checks if page renders with given status code 
* doCheckPageRendering(String textPresent): verifies that the given text is included in page output + page renders with code 200
* doCheckPageRendering(String textPresent, String textNotPresent): verifies that the given text is (not) included in page output + page renders with code 200. The parameters textPresent/textNotPresent can be set to null if you do not need the check.

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByProperty("sling:resourceType", "weretail/components/structure/page")
        .doCheckPageRendering()
        .doCheckPageRendering(200)
        .doCheckPageRendering("some test string")
        .doCheckPageRendering("some test string", "exception")
        .run()
```

### Print Nodes and Properties

Sometimes, you only want to print some information about the matched nodes.

* printPath(): prints the path of the matched node
* printProperty(String property): prints the value of the specified property of the matched node
* printJson(): prints a json representation of all the matched node's properties

```java
println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByNodeName("jcr:content")
        .printPath()
        .printProperty("sling:resourceType")
        .printJson()
        .run()
```

### Custom Actions

You can also hook in custom code to perform actions on resources. For this "doCustomResourceBasedAction()" can take a Lambda expression.

* doCustomResourceBasedAction(): run your custom code

```java
def myAction = {
    resource -> 
    hasChildren = resource.hasChildren()
    String output = resource.path + " has children: "
    output += hasChildren ? "yes" : "no"
    return output
}

println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .doCustomResourceBasedAction(myAction)
        .run()
```



<a name="binding_run"></a>

## Run Options

At the end you can run all actions or perform a dry-run first. The dry-run will just provide output about modifications but not save any changes. The normal run saves the session, no additional "session.save()" is required.

* run(): performs all actions and saves the session
* dryRun(): only prints actions but does not perform repository changes
* run(boolean dryRun): the "dryRun" parameter defines if it should be a run or dry-run


# JMX Interface

<img src="docs/images/jmx.png">

AECU provides JMX methods for executing scripts and reading the history. You can also check the version here.

## Execute

This will execute the given script or folder. If a folder is specified then all files (incl. any subfolders) are executed. AECU will respect run modes during execution.

Parameters:
 * Path: file or folder to execute

## GetHistory

Prints the history of the specified last runs. The entries are sorted by date and start with the last run.

Parameters:
 * Start index: starts with 0 (= latest history entry)
 * Count: number of entries to print

## GetFiles

This will print all files that are executable for a given path. You can use this to check which scripts of a given folder would be executed.

Parameters:
* Path: file or folder to check


<a name="healthchecks"></a>

# Health Checks

Health checks show you the status of AECU itself and the last migration run.
You can access them on the [status page](http://localhost:4502/libs/granite/operations/content/healthreports/healthreportlist.html/system/sling/monitoring/mbeans/org/apache/sling/healthcheck/HealthCheck/aecuHealthCheckmBean).
For the status of older runs use AECU's history page.

<img src="docs/images/healthCheck.png">

<a name="api"></a>

# API Documentation

You can access our AECU service (AecuService class) in case you have special requirements. See the [API documentation](https://valtech.github.io/aem-easy-content-upgrade/).

<a name="license"></a>

# License

The AECU tool is licensed under the [MIT LICENSE](LICENSE).

<a name="changelog"></a>

# Changelog

Please see our [history file](HISTORY).

<a name="developers"></a>

# Developers

See our [developer zone](docs/developers.md).
