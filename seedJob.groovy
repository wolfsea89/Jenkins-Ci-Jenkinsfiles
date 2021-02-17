import groovy.json.JsonSlurper

String jobsDefinition = 'jobs.json'

def jobs = new JsonSlurper().parseText(readFileFromWorkspace(jobsDefinition))

for (job in jobs){
  pipelineJob(job.name) {
    parameters{
      stringParam("branch", job.defaultBranch, 'Branch name')
      stringParam("repositoryUrl", job.repositoryUrl, 'Repository URL (git/https)')
      stringParam("manualVersion", "", 'Set manual version (X.Y.Z). Worked with branch release, hotfix, master without version')
    }
    environmentVariables {
      env('JENKINSFILE_SCRIPTS_DIR', '.jenkins')
      env('GIT_CREDS_ID', 'github')
      env('APP_CONFIGURATION_JSON_PATH', 'configuration/jenkins.json')
      env('BASEIMAGE_SERVICES_ADMIN_CREDS_ID', 'baseImage_services_AminPassword')
      env('DOCKER_REPOSITORY_CREDS_ID', 'docker_hub')
      env('DOCKER_REPOSITORY_URL', 'https://index.docker.io/v1/')
      env('DOCKER_REPOSITORY_SNAPSHOT_NAME', 'wolfsea89/${projectName}_snapshot')
      env('DOCKER_REPOSITORY_RELEASE_NAME', 'wolfsea89/${projectName}')
    }
    definition {
      cpsScm{
        scm {
          git {
            remote {
                url('git@github.com:wolfsea89/Jenkins-Ci-Jenkinsfiles.git')
                credentials('github')
            }
            branch('master')
            extensions {
              wipeOutWorkspace()
              submoduleOptions {
                disable(false)
                parentCredentials(true)
                recursive(false)
                tracking(true)
              }
            }
          }
          scriptPath('Jenkinsfile')
        }
      }
    }
  }
}
 
// def jsonSlurper = new JsonSlurper()
// data = jsonSlurper.parse(new File("jobs.json"))
// println(data)
// println ("WSK")