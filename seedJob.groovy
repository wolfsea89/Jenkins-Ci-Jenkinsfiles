import groovy.json.JsonSlurper
 
 
// filename = 
// println(filename)
def a = getClass().protectionDomain.codeSource.location.path
println(a)

 
def jsonSlurper = new JsonSlurper()
data = jsonSlurper.parse(new File("jobs.json"))
println(data)
println ("WSK")