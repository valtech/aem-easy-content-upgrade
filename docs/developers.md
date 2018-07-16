# AEM Server Setup

By default AEM is expected to listen on localhost on port 5702. This setting can be overridden by adding parameters:
* -Daem.port=4502
* -Daem.host=localhost

You need AEM 6.3 with service pack 2.

# Build and Deploy

To build and deploy run this in the base (aem-easy-content-upgrade) or ui.apps/examples folder:

```bash
mvn clean install -PautoInstallPackage
```

In case you want to deploy core only you can use this command in core folder:

```bash
mvn clean install -PautoInstallBundle
```


# Code Formatting

Please use our standard code formatters for [Eclipse](formatter/eclipse-aecu.xml)
and [IntelliJ](formatter/intellij-aecu.xml).
