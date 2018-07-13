# Build and Deploy

To build and deploy run this in the base folder:

```bash
mvn clean install -PautoInstallPackage
```

You can deploy the core project using this command in core folder:

```bash
mvn clean install -PautoInstallBundle
```


# Code Formatting

Please use our standard code formatters for [Eclipse](formatter/eclipse-aecu.xml)
and [IntelliJ](formatter/intellij-aecu.xml).
