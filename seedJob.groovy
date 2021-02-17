import groovy.json.JsonSlurper

String jobsDefinition = 'jobs.json'
String jobs = readFileFromWorkspace(jobsDefinition)
def test = new JsonSlurper().parseText(jobs)

job('example-1') {
    steps {
      println(test)
    }
}


 
// def jsonSlurper = new JsonSlurper()
// data = jsonSlurper.parse(new File("jobs.json"))
// println(data)
// println ("WSK")