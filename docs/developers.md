# AEM Server Setup

By default AEM is expected to listen on localhost on port 5702. This setting can be overridden by adding parameters:
* -Daem.port=5702
* -Daem.host=localhost
* -Daem.publish.port=5703
* -Daem.publish.host=localhost

You need AEM 6.3 with service pack 2 or AEM 6.4.

# Build and Deploy

To build and deploy run this in the base (aem-easy-content-upgrade) or ui.apps/examples folder:

```bash
mvn clean install -PautoInstallPackage
```

In case you want to deploy core only you can use this command in core folder:

```bash
mvn clean install -PautoInstallBundle
```

To build and deploy on publish instance run this in the base (aem-easy-content-upgrade) or ui.apps/examples folder:

```bash
mvn clean install -PautoInstallPackagePublish
```


# Code Formatting

Please use our standard code formatters for [Eclipse](formatter/eclipse-aecu.xml)
and [IntelliJ](formatter/intellij-aecu.xml).
