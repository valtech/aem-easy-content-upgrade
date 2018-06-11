# AEM Easy Content Upgrade (AECU)

AECU simplifies content migrations by executing migration scripts during package installation. It is built on top of [Groovy Console](https://github.com/OlsonDigital/aem-groovy-console).


Features:

* GUI to run scripts and see history of runs
* Run mode support
* Fallback scripts in case of errors
* Extension of Groovy Console bindings
* Service API
* Health Checks

# Requirements

AECU requires Java 8 and AEM 6.3 or above.

#Installation

TODO

# Execution of Migration Scripts

TODO

# History of Past Runs

TODO

# Extension to Groovy Console

TODO

# JMX Interface

TODO

# Health Checks

Health checks show you the status of AECU itself and the last migration run.
You can access them on the [status page](http://localhost:4502/libs/granite/operations/content/healthreports/healthreportlist.html/system/sling/monitoring/mbeans/org/apache/sling/healthcheck/HealthCheck/aecuHealthCheckmBean).
For the status of older runs use AECU's history page.

<img src="docs/images/healthCheck.png">

# License

The AC Tool is licensed under the [GNU GENERAL PUBLIC LICENSE - v 3](LICENSE).
