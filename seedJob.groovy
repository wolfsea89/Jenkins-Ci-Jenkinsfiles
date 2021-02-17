import groovy.json.JsonSlurper
 
 
filename = new File("jobs.json")
println(filename)
 
def jsonSlurper = new JsonSlurper()
data = jsonSlurper.parse(filename)
println(data)
println ("WSK")