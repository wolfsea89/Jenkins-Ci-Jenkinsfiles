import groovy.json.JsonSlurper
 
 
// filename = 
// println(filename)
println(env.WORKSPACE)
 
def jsonSlurper = new JsonSlurper()
data = jsonSlurper.parse(new File("jobs.json"))
println(data)
println ("WSK")