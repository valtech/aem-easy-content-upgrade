# AEM Easy Content Upgrade (AECU)

AECU simplifies content migrations by executing migration scripts during package installation. It is built on top of [Groovy Console](https://github.com/OlsonDigital/aem-groovy-console).


Features:

* GUI to run scripts and see history of runs
* Run mode support
* Fallback scripts in case of errors
* Extension of Groovy Console bindings
* Service API
* Health Checks

Table of contents
1. [Requirements](#requirements)
2. [Installation](#installation)
3. [Execution of Migration Scripts](#execution)
    1. [Install Hook](#installHook)
    2. [Manual Execution](#manualExecution)
4. [History of Past Runs](#history)
5. [Extension to Groovy Console](#groovy)
6. [JMX Interface](#jmx)
7. [Health Checks](#healthchecks)
8. [License](#license)


<a name="requirements"></a>

# Requirements

AECU requires Java 8 and AEM 6.3 or above. Groovy Console can be installed manually if [bundle install](#bundleInstall) is not used.

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

<a name="groovy"></a>

# Extension to Groovy Console

AECU adds its own binding to Groovy Console. You can reach it using "aecu" in your script. This provides methods to perform common tasks like property modification or node deletion.

It follows a collect, filter, execute process.

## Collect Options
In the collect phase you define which nodes should be checked for a migration.

* forResources(String[] paths): use the given paths without any subnodes
* forChildResourcesOf(String path): use all direct childs of the given path (but no grandchilds)
* forDescendantResourcesOf(String path): use the whole subtree under this path

You can call these methods multiple times and combine them. They will be merged together.

Example:

```java
println aecu.contentUpgradeBuilder()
        .forResources((String[])["/content/we-retail/ca/en"])
        .forChildResourcesOf("/content/we-retail/us/en")
        .forDescendantResourcesOf("/content/we-retail/us/en/experience")
        .doSetProperty("name", "value")
        .run()
```

## Filter options
These methods can be used to filter the nodes that were collected above. Multiple filters can be applied for one run.

### Filter by Properties
Use this to filter by a list of property values (e.g. sling:resourceType).

```java
filterByProperties(Map<String, String> properties)
```

Example:

```java
def conditionMap = [:]
conditionMap["sling:resourceType"] = "weretail/components/structure/page"

println aecu.contentUpgradeBuilder()
        .forChildResourcesOf("/content/we-retail/ca/en")
        .filterByProperties(conditionMap)
        .doSetProperty("name", "value")
        .run()
```

<a name="jmx"></a>

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

<a name="jmx"></a>

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
        .forDescendantResourcesOf("/content/we-retail/ca/en")
        .filterWith(complexFilter)
        .doSetProperty("name", "value")
        .run()        
```

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

TODO

# JMX Interface

<img src="docs/images/jmx.png">

AECU provides JMX methods for executing scripts and reading the history. You can also check the version here.

## Execute

This will execute the given script or folder. If a folder is specified then all files (incl. any subfolders) are executed. AECU will respect runmodes during execution.

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

<a name="license"></a>

# License

The AC Tool is licensed under the [MIT LICENSE](LICENSE).

# Developers

See our [developer zone](docs/developers.md)