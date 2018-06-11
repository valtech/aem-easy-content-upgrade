def checkResourceResolver = (resourceResolver != null);
def checkHelloWorld = (helloWorld != null);
def sayHello = (checkHelloWorld) ? helloWorld.sayHello() : "NooooOOOOoooooo!!!!!"

println "resourceProvider is defined $checkResourceResolver"
println "helloWorld is defined $checkHelloWorld"
println "helloWorld says $sayHello"