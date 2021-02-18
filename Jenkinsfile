@Library('Sharedlibraries') import devops.ci.*

pipeline {
  ////// SET PARAMETERS BY SEED JOB
  parameters {
  //   string(name: 'branch', defaultValue: 'feature/create_baseimage', description: 'Branch name')
  //   string(name: 'repositoryUrl', defaultValue: 'git@github.com:wolfsea89/Jenkins-BaseImage.git', description: 'Repository URL (git/https)')
    string(name: 'manualVersion', defaultValue: '', description: 'Set manual version (X.Y.Z). Worked with branch release, hotfix, master without version')
  }
  // environment {
  //   JENKINSFILE_SCRIPTS_DIR = '.jenkins'
  //   GIT_CREDS_ID = 'github'
  //   APP_CONFIGURATION_JSON_PATH = 'configuration/jenkins.json'
  //   BASEIMAGE_SERVICES_ADMIN_CREDS_ID = 'baseImage_services_AminPassword'
  //   DOCKER_REPOSITORY_CREDS_ID = 'docker_hub'
  //   DOCKER_REPOSITORY_URL = 'https://index.docker.io/v1/'
  //   DOCKER_REPOSITORY_SNAPSHOT_NAME = 'wolfsea89/${projectName}_snapshot'
  //   DOCKER_REPOSITORY_RELEASE_NAME = 'wolfsea89/${projectName}'
  // }
  agent none
  options {
    skipDefaultCheckout true
  }
  stages{
    stage('Continuous Integration') {
      agent {
        label 'slave_ci_build'
      }
      stages {
        stage('Preparing to work') {
          steps {
            script {

              def facts = new GatheringFacts(params, env)
              facts.setJenkinsScriptDirectory(".jenkins")

              deleteDir()

              def git = new Git(this)
              println(scm.getProperties())
              git.checkoutApplicationRepository(
                branchName:facts.branchName,
                repositoryUrl:facts.repositoryUrl,
                gitCredentialId:facts.gitCredentialId
              )
                
              git.checkoutJenkinsSripts(facts.branchName, facts.repositoryUrl)
              
              gitcheckout.application
              gitcheckout.jenkinsSripts(JENKINSFILE_SCRIPTS_DIR)
              
              facts['applicationConfiguration'] = gatheringFacts.applicationConfiguration(env.WORKSPACE + '/' + APP_CONFIGURATION_JSON_PATH)
              currentBuild.displayName = "#${env.BUILD_NUMBER} - ${facts.branchName} - ${facts.version.semanticVersionWithBuildNumber}"
              env.facts = facts

            }
          }
        }
        stage('Prebuild Scripts') {
          parallel {
            stage('Docker'){
              when{
                expression {
                  facts.applicationConfiguration.DOCKER_PROJECTS ? true : false
                }
              }
              steps{
                script{
                  prebuildScriptsDocker.setVersion(facts)
                  prebuildScriptsDocker.setCredentials(facts, env.BASEIMAGE_SERVICES_ADMIN_CREDS_ID)
                  prebuildScriptsDocker.setJenkinsJobInfo(facts)
                }
              }
            }
          }
        }
        stage('Build'){
          parallel {
            stage('Docker'){
              when{
                expression {
                  facts.applicationConfiguration.DOCKER_PROJECTS ? true : false
                }
              }
              steps{
                script{
                  dockerCi.buildProjects(
                    facts.applicationConfiguration.DOCKER_PROJECTS,
                    facts.version.semanticVersionWithBuildNumber
                  )
                }
              }
            }
          }
        }
        stage('Publish'){
          parallel {
            stage('Docker publish - Release'){
              when{
                expression {
                  facts.artifactType == "release" ? true : false
                }
              }
              steps{
                script{
                  dockerCi.publishBaseImage(
                    facts.applicationConfiguration.DOCKER_PROJECTS,
                    facts.version.semanticVersionWithBuildNumber,
                    env.DOCKER_REPOSITORY_URL,
                    env.DOCKER_REPOSITORY_RELEASE_NAME,
                    env.DOCKER_REPOSITORY_CREDS_ID
                  )
                  dockerCi.cleanAfterBuild(
                    facts.applicationConfiguration.DOCKER_PROJECTS,
                    facts.version.semanticVersionWithBuildNumber,
                    env.DOCKER_REPOSITORY_RELEASE_NAME,
                  )
                }
              }
            }
            stage('Docker publish - Snapshot'){
              when{
                expression {
                  facts.artifactType == "snapshot" ? true : false
                }
              }
              steps{
                script{
                  dockerCi.publishBaseImage(
                    facts.applicationConfiguration.DOCKER_PROJECTS,
                    facts.version.semanticVersionWithBuildNumber,
                    env.DOCKER_REPOSITORY_URL,
                    env.DOCKER_REPOSITORY_SNAPSHOT_NAME,
                    env.DOCKER_REPOSITORY_CREDS_ID
                  )
                  dockerCi.cleanAfterBuild(
                    facts.applicationConfiguration.DOCKER_PROJECTS,
                    facts.version.semanticVersionWithBuildNumber,
                    env.DOCKER_REPOSITORY_SNAPSHOT_NAME,
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
