import groovy.json.JsonSlurper

String jobsDefinition = 'jobs.json'

def jobs = new JsonSlurper().parseText(readFileFromWorkspace(jobsDefinition))

for (job in jobs){
  pipelineJob(job.name) {
    parameters{
      stringParam("branch", job.defaultBranch, 'Branch name')
    }
  }
}


 
// def jsonSlurper = new JsonSlurper()
// data = jsonSlurper.parse(new File("jobs.json"))
// println(data)
// println ("WSK")