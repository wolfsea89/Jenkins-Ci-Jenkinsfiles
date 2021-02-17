import groovy.json.JsonSlurper
 
 
// filename = 
// println(filename)
def a = System.getProperty("user.dir");
println(a)

 
def jsonSlurper = new JsonSlurper()
data = jsonSlurper.parse(new File("jobs.json"))
println(data)
println ("WSK")