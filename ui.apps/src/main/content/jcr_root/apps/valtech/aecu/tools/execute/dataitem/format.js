use(function () {

    var path = this.path;
    var shortPath = path.replace("/var/groovyconsole/scripts/aecu/", "");
    shortPath = shortPath.replace("/conf/groovyconsole/scripts/aecu/", "");
    shortPath = shortPath.replace("/apps/aecu-scripts/", "");
    return {
    	shortPath: shortPath
    };
});
