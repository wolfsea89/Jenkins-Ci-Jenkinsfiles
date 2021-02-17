import groovy.json.JsonSlurper

String jobsDefinition = 'jobs.json'

job('example-1') {
    steps {
      String jobs = readFileFromWorkspace(jobsDefinition)
      def test = new JsonSlurper().parseText(jobs)
      println(test)
    }
}


 
// def jsonSlurper = new JsonSlurper()
// data = jsonSlurper.parse(new File("jobs.json"))
// println(data)
// println ("WSK")