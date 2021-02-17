import groovy.json.JsonSlurper
 
 
filename = new File("jobs.json")
 
def jsonSlurper = new JsonSlurper()
data = jsonSlurper.parse(new File(filename))
println(data)
println ("WSK")